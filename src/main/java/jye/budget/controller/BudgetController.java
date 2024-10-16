package jye.budget.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.Budget;
import jye.budget.entity.User;
import jye.budget.login.SessionConst;
import jye.budget.req.BudgetReq;
import jye.budget.dto.BudgetDto;
import jye.budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }

        log.info("예산 조회 : {}", req);

        BudgetDto data = budgetService.findByYearMonthAndUserId(req.getSearchDate(), user.getUserId(), false);
        model.addAttribute("data", data);

        return "budget/view";
    }

    @GetMapping("/edit")
    public String editForm(@ModelAttribute("req") BudgetReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        BudgetDto budgetDto = budgetService.findByYearMonthAndUserId(req.getSearchDate(), user.getUserId(), true);
        model.addAttribute("budgetDto", budgetDto);

        return "budget/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("budgetDto") BudgetDto reqDto, BindingResult bindingResult,
                       HttpSession session) {

        log.info("예산 수정 : {}", reqDto);

        if(StringUtils.isBlank(reqDto.getBudget().getYearMonth())) {
            return "error";
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        BudgetDto budgetDto = budgetService.findByYearMonthAndUserId(reqDto.getBudget().getYearMonth(), user.getUserId(), true);
        if (bindingResult.hasErrors()) {
            return "budget/edit";
        }

        if(!Objects.equals(budgetDto.getBudget().getBudgetId(), reqDto.getBudget().getBudgetId())) {
            log.error("예산 ID 불일치 : budget - {}, req.budgetId - {}", budgetDto.getBudget(), reqDto.getBudget().getBudgetId());
            return "error";
        }

        if(reqDto.getBudget().getBudgetId() != null) {
            log.info("기존 예산 정보 있음 : {}", budgetDto.getBudget());
            if(!budgetDto.getBudgetAllocations().isEmpty() && reqDto.getBudgetAllocations() != null) {
                boolean hasNonMatchingBudgetAllocationIds = reqDto.getBudgetAllocations().stream()
                        .anyMatch(reqBudgetAllocation ->
                                budgetDto.getBudgetAllocations().stream()
                                        .noneMatch(budgetAllocation ->
                                                Objects.equals(budgetAllocation.getBudgetAllocationId(), reqBudgetAllocation.getBudgetAllocationId()) &&
                                                        Objects.equals(budgetAllocation.getAsset().getAssetId(), reqBudgetAllocation.getAsset().getAssetId())));

                if (hasNonMatchingBudgetAllocationIds) {
                    log.error("예산 배분 - 예산 배분 or 자산 ID 불일치 : budgetDto - {}, reqDto - {}", budgetDto.getBudgetAllocations(), reqDto.getBudgetAllocations());
                    return "error";
                }
            }

            if(!budgetDto.getFixedExpenses().isEmpty() && reqDto.getFixedExpenses() != null) {
                boolean hasNonMatchingFixedExpenseIds = reqDto.getFixedExpenses().stream()
                        .anyMatch(reqFixedExpense ->
                                budgetDto.getFixedExpenses().stream()
                                        .noneMatch(fixedExpense ->
                                                Objects.equals(fixedExpense.getFixedExpenseId(), reqFixedExpense.getFixedExpenseId()) &&
                                                        Objects.equals(fixedExpense.getCategory().getCategoryId(), reqFixedExpense.getCategory().getCategoryId())));

                if (hasNonMatchingFixedExpenseIds) {
                    log.error("고정 지출 - 고정 지출 or 카테고리 ID 불일치 : budgetDto - {}, reqDto - {}", budgetDto.getFixedExpenses(), reqDto.getFixedExpenses());
                    return "error";
                }
            }

            budgetService.update(reqDto, user.getUserId());
        } else {
            log.info("기존 예산 정보 없음");

            if(!budgetDto.getBudgetAllocations().isEmpty() && reqDto.getBudgetAllocations() != null) {
                boolean hasNonMatchingAssetIds = reqDto.getBudgetAllocations().stream()
                        .anyMatch(reqBudgetAllocation ->
                                budgetDto.getBudgetAllocations().stream()
                                        .noneMatch(budgetAllocation ->
                                                Objects.equals(budgetAllocation.getAsset().getAssetId(), reqBudgetAllocation.getAsset().getAssetId())));

                if (hasNonMatchingAssetIds) {
                    log.error("예산 배분 - 자산 ID 불일치 : budgetDto - {}, reqDto - {}", budgetDto.getBudgetAllocations(), reqDto.getBudgetAllocations());
                    return "error";
                }
            }

            if(!budgetDto.getFixedExpenses().isEmpty() && reqDto.getFixedExpenses() != null) {
                boolean hasNonMatchingCategoryIds = reqDto.getFixedExpenses().stream()
                        .anyMatch(reqFixedExpense ->
                                budgetDto.getFixedExpenses().stream()
                                        .noneMatch(fixedExpense ->
                                                Objects.equals(fixedExpense.getCategory().getCategoryId(), reqFixedExpense.getCategory().getCategoryId())));

                if (hasNonMatchingCategoryIds) {
                    log.error("고정 지출 - 카테고리 ID 불일치 : budgetDto - {}, reqDto - {}", budgetDto.getFixedExpenses(), reqDto.getFixedExpenses());
                    return "error";
                }
            }

            reqDto.getBudget().setUserId(user.getUserId());
            budgetService.save(reqDto, user.getUserId());
        }
        return "redirect:/budget?searchDate=" + reqDto.getBudget().getYearMonth();
    }

    @GetMapping("/recent")
    public ResponseEntity<BudgetDto> getRecent(HttpSession session) {

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("최근 예산 정보 조회 : {}", user.getUserId());

        BudgetDto recentBudget = budgetService.getRecentBudget(user.getUserId());

        if (recentBudget != null) {
            return ResponseEntity.ok(recentBudget);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> delete(@PathVariable Long budgetId, HttpSession session) {

        log.info("예산 삭제 : {}", budgetId);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Budget budget = budgetService.check(budgetId, user.getUserId());
        if(budget == null) {
            return ResponseEntity.notFound().build();
        }

        budgetService.delete(budgetId);
        return ResponseEntity.noContent().build();
    }
}
