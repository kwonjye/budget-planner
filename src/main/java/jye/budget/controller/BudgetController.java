package jye.budget.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.User;
import jye.budget.login.SessionConst;
import jye.budget.req.BudgetReq;
import jye.budget.dto.BudgetDto;
import jye.budget.service.BudgetService;
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
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {


    private final BudgetService budgetService;

    @GetMapping
    public String view(@ModelAttribute("req") BudgetReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("budget view : {}", user);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        BudgetDto data = budgetService.findByYearMonthAndUserId(req.getSearchDate(), user.getUserId());
        model.addAttribute("data", data);

        return "budget/view";
    }

    @GetMapping("/edit")
    public String editForm(@ModelAttribute("req") BudgetReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        BudgetDto budgetDto = budgetService.findByYearMonthAndUserId(req.getSearchDate(), user.getUserId());
        model.addAttribute("budgetDto", budgetDto);

        return "budget/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("budgetDto") BudgetDto reqDto, BindingResult bindingResult,
                       HttpSession session) {

        log.info("edit budget : {}", reqDto);

        if(StringUtils.isBlank(reqDto.getBudget().getYearMonth())) {
            return "/error";
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        BudgetDto budgetDto = budgetService.findByYearMonthAndUserId(reqDto.getBudget().getYearMonth(), user.getUserId());
        if (bindingResult.hasErrors()) {
            return "budget/edit";
        }

        if(!Objects.equals(budgetDto.getBudget().getBudgetId(), reqDto.getBudget().getBudgetId())) {
            return "/error";
        }

        if(reqDto.getBudget().getBudgetId() != null) {
            boolean hasNonMatchingBudgetAllocationIds = reqDto.getBudgetAllocations().stream()
                    .anyMatch(reqBudgetAllocation ->
                            reqBudgetAllocation.getBudgetAllocationId() == null || reqBudgetAllocation.getAsset().getAssetId() == null ||
                            budgetDto.getBudgetAllocations().stream()
                                    .noneMatch(budgetAllocation ->
                                            budgetAllocation.getBudgetAllocationId().equals(reqBudgetAllocation.getBudgetAllocationId())
                                                    && budgetAllocation.getAsset().getAssetId().equals(reqBudgetAllocation.getAsset().getAssetId())));

            if (hasNonMatchingBudgetAllocationIds) {
                return "/error";
            }

            boolean hasNonMatchingFixedExpenseIds = reqDto.getFixedExpenses().stream()
                    .anyMatch(reqFixedExpense ->
                            reqFixedExpense.getFixedExpenseId() == null || reqFixedExpense.getCategory().getCategoryId() == null ||
                            budgetDto.getFixedExpenses().stream()
                                    .noneMatch(fixedExpense ->
                                            fixedExpense.getFixedExpenseId().equals(reqFixedExpense.getFixedExpenseId())
                                                    && fixedExpense.getCategory().getCategoryId().equals(reqFixedExpense.getCategory().getCategoryId())));

            if (hasNonMatchingFixedExpenseIds) {
                return "/error";
            }

            budgetService.update(reqDto);
        } else {
            boolean hasNonMatchingAssetIds = reqDto.getBudgetAllocations().stream()
                    .anyMatch(reqBudgetAllocation ->
                            reqBudgetAllocation.getAsset().getAssetId() == null ||
                            budgetDto.getBudgetAllocations().stream()
                                    .noneMatch(budgetAllocation ->
                                            budgetAllocation.getAsset().getAssetId().equals(reqBudgetAllocation.getAsset().getAssetId())));

            if (hasNonMatchingAssetIds) {
                return "/error";
            }

            boolean hasNonMatchingCategoryIds = reqDto.getFixedExpenses().stream()
                    .anyMatch(reqFixedExpense ->
                            reqFixedExpense.getCategory().getCategoryId() == null ||
                            budgetDto.getFixedExpenses().stream()
                                    .noneMatch(fixedExpense ->
                                            fixedExpense.getCategory().getCategoryId().equals(reqFixedExpense.getCategory().getCategoryId())));

            if (hasNonMatchingCategoryIds) {
                return "/error";
            }

            reqDto.getBudget().setUserId(user.getUserId());
            budgetService.save(reqDto);
        }
        return "redirect:/budget?searchDate=" + reqDto.getBudget().getYearMonth();
    }
}
