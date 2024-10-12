package jye.budget.controller;

import com.google.gson.Gson;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.*;
import jye.budget.form.ExpensesForm;
import jye.budget.login.SessionConst;
import jye.budget.mapper.BudgetMapper;
import jye.budget.mapper.CategoryMapper;
import jye.budget.mapper.EtcBudgetMapper;
import jye.budget.req.EtcBudgetReq;
import jye.budget.req.ExpensesReq;
import jye.budget.service.ExpensesService;
import jye.budget.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpensesController {

    private final ExpensesService expensesService;
    private final BudgetMapper budgetMapper;
    private final CategoryMapper categoryMapper;
    private final EtcBudgetMapper etcBudgetMapper;

    @GetMapping
    public String view(@ModelAttribute("req") ExpensesReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }

        log.info("지출 조회 : {}", req);

        List<Expenses> expensesReq = expensesService.findByReqAndUserId(req, user.getUserId());
        Map<LocalDate, List<Expenses>> groupedByExpenseDate = expensesReq.stream()
                .sorted(Comparator.comparing(Expenses::getCreatedAt).reversed())
                .collect(Collectors.groupingBy(Expenses::getExpenseDate));
        log.info("검색 조건 날짜별 지출 : {}", groupedByExpenseDate);
        model.addAttribute("groupedByExpenseDate", groupedByExpenseDate);

        int totalExpensesReq = expensesReq.stream().mapToInt(Expenses::getAmount).sum();
        int countExpensesReq = expensesReq.size();
        int maxExpensesReq = expensesReq.stream().mapToInt(Expenses::getAmount).max().orElse(0);

        log.info("검색 조건 합계 : {}", totalExpensesReq);
        log.info("검색 조건 개수 : {}", countExpensesReq);
        log.info("검색 조건 최대값 : {}", maxExpensesReq);

        model.addAttribute("totalExpensesReq", totalExpensesReq);
        model.addAttribute("countExpensesReq", countExpensesReq);
        model.addAttribute("maxExpensesReq", maxExpensesReq);

        Budget budget = budgetMapper.findByYearMonth(req.getSearchDate(), user.getUserId());
        int livingExpenseBudget = budget == null ? 0 : budget.getLivingExpenseBudget();
        log.info("생활비 : {}", livingExpenseBudget);
        model.addAttribute("livingExpenseBudget", livingExpenseBudget);

        List<EtcBudget> etcBudgets = etcBudgetMapper.findByReqAndUserId(EtcBudgetReq.builder().searchDate(req.getSearchDate()).build(), user.getUserId());
        Map<Category, Integer> etcBudgetAmountMap = etcBudgets.stream()
                .sorted(Comparator.comparing(etcBudget -> etcBudget.getCategory().getCreatedAt()))
                .collect(Collectors.groupingBy(
                        EtcBudget::getCategory,
                        LinkedHashMap::new,
                        Collectors.reducing(0,
                                etcBudget -> etcBudget.getCalcType().apply(0, etcBudget.getAmount()),
                                Integer::sum
                        )
                ));
        log.info("기타 예산 : {}", etcBudgetAmountMap);
        model.addAttribute("etcBudgetAmountMap", etcBudgetAmountMap);

        List<Expenses> expenses = expensesService.findByReqAndUserId(ExpensesReq.builder().searchDate(req.getSearchDate()).build(), user.getUserId());
        Map<Category, Integer> expensesAmountMap = expenses.stream()
                .collect(Collectors.groupingBy(Expenses::getCategory, Collectors.summingInt(Expenses::getAmount)));

        List<Category> categories = categoryMapper.findByUserIdAndType(user.getUserId(), CategoryType.EXPENSE);
        categories.forEach(category -> expensesAmountMap.putIfAbsent(category, 0));
        Map<Category, Integer> sortedExpensesAmountMap = expensesAmountMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        log.info("카테고리별 지출 합계 : {}", sortedExpensesAmountMap);
        model.addAttribute("expensesAmountMap", sortedExpensesAmountMap);

        int totalBudget = livingExpenseBudget + etcBudgetAmountMap.values().stream().mapToInt(Integer::intValue).sum();
        int totalExpenses = expenses.stream().mapToInt(Expenses::getAmount).sum();
        int totalNecessary = expenses.stream().filter(Expenses::isNecessary).mapToInt(Expenses::getAmount).sum();
        int totalUnNecessary = expenses.stream().filter(expense -> !expense.isNecessary()).mapToInt(Expenses::getAmount).sum();
        int balance = totalBudget - totalExpenses;

        log.info("예산 : {}", totalBudget);
        log.info("합계 : {}", totalExpenses);
        log.info("필요 : {}", totalNecessary);
        log.info("불필요 : {}", totalUnNecessary);
        log.info("잔액 : {}", balance);

        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("totalNecessary", totalNecessary);
        model.addAttribute("totalUnNecessary", totalUnNecessary);
        model.addAttribute("balance", balance);

        return "/expenses/view";
    }

    @GetMapping("/chart")
    public String chart(@ModelAttribute("req") ExpensesReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }

        log.info("지출 차트 : {}", req);

        List<Expenses> expenses = expensesService.findByReqAndUserId(req, user.getUserId());
        Map<Category, Integer> groupedByCategoryTotals = expenses.stream()
                .sorted(Comparator.comparing(expense -> expense.getCategory().getCreatedAt()))
                .collect(Collectors.groupingBy(Expenses::getCategory, Collectors.summingInt(Expenses::getAmount)));

        List<String> labels = groupedByCategoryTotals.keySet().stream().map(Category::getCategoryName).toList();
        List<Integer> data = new ArrayList<>(groupedByCategoryTotals.values());
        List<String> backgroundColors = groupedByCategoryTotals.keySet().stream().map(Category::getCategoryColor).toList();

        Gson gson = new Gson();
        model.addAttribute("labels", gson.toJson(labels));
        model.addAttribute("data", gson.toJson(data));
        model.addAttribute("backgroundColors", gson.toJson(backgroundColors));

        return "/expenses/chart";
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("expensesForm") ExpensesForm expensesForm, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);
        List<Category> categories = categoryMapper.findByUserIdAndType(user.getUserId(), CategoryType.EXPENSE);
        model.addAttribute("categories", categories);
        return "/expenses/form";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("expensesForm") ExpensesForm expensesForm, BindingResult bindingResult,
                      HttpSession session, Model model) {

        log.info("지출 입력 : {}", expensesForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        List<Category> categories = categoryMapper.findByUserIdAndType(user.getUserId(), CategoryType.EXPENSE);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categories);
            return "/expenses/form";
        }

        Optional<Category> categoryOptional = getCategory(expensesForm.getCategoryId(), bindingResult, model, categories);
        if (categoryOptional.isEmpty()) return "/expenses/form";
        expensesService.save(user.getUserId(), expensesForm, categoryOptional.get());

        return "redirect:/expenses";
    }

    @GetMapping("/{expenseId}")
    public String editForm(@PathVariable("expenseId") Long expenseId,
                           HttpSession session, Model model) {

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Expenses expenses = expensesService.check(expenseId, user.getUserId());
        if(expenses == null) {
            return "/error";
        }
        List<Category> categories = categoryMapper.findByUserIdAndType(user.getUserId(), CategoryType.EXPENSE);
        model.addAttribute("categories", categories);
        model.addAttribute("expensesForm", new ExpensesForm(expenses));
        return "/expenses/form";
    }

    @PostMapping("/{expenseId}")
    public String edit(@PathVariable("expenseId") Long expenseId,
                       @Valid @ModelAttribute("expensesForm") ExpensesForm expensesForm, BindingResult bindingResult,
                       HttpSession session, Model model) {

        log.info("지출 수정 : {}", expensesForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        List<Category> categories = categoryMapper.findByUserIdAndType(user.getUserId(), CategoryType.EXPENSE);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categories);
            return "/expenses/form";
        }

        Expenses expenses = expensesService.check(expenseId, user.getUserId());
        if(expenses == null) {
            return "/error";
        }

        Optional<Category> categoryOptional = getCategory(expensesForm.getCategoryId(), bindingResult, model, categories);
        if (categoryOptional.isEmpty()) return "/expenses/form";
        expensesService.update(expenseId, expensesForm, categoryOptional.get());

        return "redirect:/expenses";
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> delete(@PathVariable Long expenseId, HttpSession session) {

        log.info("지출 삭제 : {}", expenseId);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Expenses expenses = expensesService.check(expenseId, user.getUserId());
        if(expenses == null) {
            return ResponseEntity.notFound().build();
        }
        expensesService.delete(expenseId);
        return ResponseEntity.noContent().build();
    }

    private static Optional<Category> getCategory(Long categoryId, BindingResult bindingResult, Model model, List<Category> categories) {
        Optional<Category> categoryOptional = categories.stream().filter(category -> Objects.equals(category.getCategoryId(), categoryId)).findFirst();
        if(categoryOptional.isEmpty()) {
            log.error("카테고리 정보 없음 : {}", categoryId);
            bindingResult.rejectValue("categoryId", "category.notFound");
            model.addAttribute("categories", categories);
        }
        return categoryOptional;
    }
}
