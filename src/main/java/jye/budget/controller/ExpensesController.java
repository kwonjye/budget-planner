package jye.budget.controller;

import com.google.gson.Gson;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.Budget;
import jye.budget.entity.Category;
import jye.budget.entity.Expenses;
import jye.budget.entity.User;
import jye.budget.form.ExpensesForm;
import jye.budget.login.SessionConst;
import jye.budget.mapper.BudgetMapper;
import jye.budget.mapper.CategoryMapper;
import jye.budget.req.ExpensesReq;
import jye.budget.service.ExpensesService;
import jye.budget.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping
    public String view(@ModelAttribute("req") ExpensesReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }

        log.info("지출 조회 : {}", req);

        List<Expenses> expenses = expensesService.findByReqAndUserId(req, user.getUserId());
        Map<LocalDate, List<Expenses>> groupedByExpenseDate = expenses.stream()
                .sorted(Comparator.comparing(Expenses::getCreatedAt).reversed())
                .collect(Collectors.groupingBy(Expenses::getExpenseDate));
        model.addAttribute("groupedByExpenseDate", groupedByExpenseDate);

        Budget budget = budgetMapper.findByYearMonth(req.getSearchDate(), user.getUserId());
        model.addAttribute("livingExpenseBudget", budget == null ? 0 : budget.getLivingExpenseBudget());

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
        return "/expenses/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("expensesForm") ExpensesForm expensesForm, BindingResult bindingResult,
                      HttpSession session, Model model) {

        log.info("지출 입력 : {}", expensesForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        List<Category> categories = categoryMapper.findByUserIdAndType(user.getUserId(), CategoryType.EXPENSE);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categories);
            return "/expenses/add";
        }

        Optional<Category> categoryOptional = categories.stream().filter(category -> Objects.equals(category.getCategoryId(), expensesForm.getCategoryId())).findFirst();
        if(categoryOptional.isEmpty()) {
            bindingResult.rejectValue("categoryId", "category.notFound");
            model.addAttribute("categories", categories);
            return "/asset/add";
        }
        expensesService.save(user.getUserId(), expensesForm, categoryOptional.get());

        return "redirect:/expenses";
    }
}
