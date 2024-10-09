package jye.budget.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jye.budget.entity.Budget;
import jye.budget.entity.Expenses;
import jye.budget.entity.User;
import jye.budget.login.SessionConst;
import jye.budget.mapper.BudgetMapper;
import jye.budget.req.ExpensesReq;
import jye.budget.service.ExpensesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpensesController {

    private final ExpensesService expensesService;
    private final BudgetMapper budgetMapper;

    @GetMapping
    public String view(@ModelAttribute("req") ExpensesReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }

        log.info("지출 조회 : {}", req);

        List<Expenses> expenses = expensesService.findByReqAndUserId(req, user.getUserId());
        Map<LocalDate, List<Expenses>> groupedByExpenseDate = expenses.stream()
                .collect(Collectors.groupingBy(Expenses::getExpenseDate));
        model.addAttribute("groupedByExpenseDate", groupedByExpenseDate);

        Budget budget = budgetMapper.findByYearMonth(req.getSearchDate(), user.getUserId());
        model.addAttribute("livingExpenseBudget", budget == null ? 0 : budget.getLivingExpenseBudget());

        return "/expenses/view";
    }
}
