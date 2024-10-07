package jye.budget.service;

import jye.budget.entity.Budget;
import jye.budget.entity.BudgetAllocation;
import jye.budget.entity.FixedExpenses;
import jye.budget.mapper.BudgetMapper;
import jye.budget.req.BudgetReq;
import jye.budget.res.BudgetRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetMapper budgetMapper;

    @Transactional(readOnly = true)
    public BudgetRes findByYearMonthAndUserId(BudgetReq req, Long userId) {
        Budget budget = budgetMapper.findByYearMonth(req, userId);
        log.info("{} budget : {}", req.getSearchDate(), budget);

        if(budget != null) {
            List<BudgetAllocation> budgetAllocations = budgetMapper.findBudgetAllocationByBudgetId(budget.getBudgetId());
            List<FixedExpenses> fixedExpenses = budgetMapper.findFixedExpensesByBudgetId(budget.getBudgetId());

            log.info("budgetAllocations : {}", budgetAllocations);
            log.info("fixedExpenses : {}", fixedExpenses);

            return BudgetRes.builder()
                    .budget(budget)
                    .budgetAllocations(budgetAllocations)
                    .fixedExpenses(fixedExpenses)
                    .build();
        }
        return null;
    }
}
