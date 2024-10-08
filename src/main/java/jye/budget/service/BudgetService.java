package jye.budget.service;

import jakarta.validation.Valid;
import jye.budget.entity.*;
import jye.budget.mapper.AssetMapper;
import jye.budget.mapper.BudgetMapper;
import jye.budget.mapper.CategoryMapper;
import jye.budget.dto.BudgetDto;
import jye.budget.type.CategoryType;
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
    private final AssetMapper assetMapper;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public BudgetDto findByYearMonthAndUserId(String yearMonth, Long userId) {
        Budget budget = budgetMapper.findByYearMonth(yearMonth, userId);
        log.info("{} budget : {}", yearMonth, budget);

        if(budget != null) {
            List<BudgetAllocation> budgetAllocations = budgetMapper.findBudgetAllocationByBudgetId(budget.getBudgetId());
            List<FixedExpenses> fixedExpenses = budgetMapper.findFixedExpensesByBudgetId(budget.getBudgetId());

            log.info("budgetAllocations : {}", budgetAllocations);
            log.info("fixedExpenses : {}", fixedExpenses);

            return BudgetDto.builder()
                    .budget(budget)
                    .budgetAllocations(budgetAllocations)
                    .fixedExpenses(fixedExpenses)
                    .build();
        } else {
            List<Asset> assets = assetMapper.findByUserIdAndAllocated(userId);
            List<Category> FixedExpansesCategories = categoryMapper.findByUserIdAndType(userId, CategoryType.FIXED_EXPENSE);

            log.info("assets : {}", assets);
            log.info("FixedExpansesCategories : {}", FixedExpansesCategories);

            List<BudgetAllocation> budgetAllocations = assets.stream().map(asset -> BudgetAllocation.builder().asset(asset).build()).toList();
            List<FixedExpenses> fixedExpenses = FixedExpansesCategories.stream().map(fixedExpensesCategory -> FixedExpenses.builder().category(fixedExpensesCategory).build()).toList();

            return BudgetDto.builder()
                    .budget(Budget.builder().yearMonth(yearMonth).build())
                    .budgetAllocations(budgetAllocations)
                    .fixedExpenses(fixedExpenses)
                    .build();
        }
    }

    @Transactional
    public void save(@Valid BudgetDto req) {
        log.info("save budget : {}", req.getBudget());
        budgetMapper.save(req.getBudget());

        log.info("save budgetAllocation : {}", req.getBudgetAllocations());
        budgetMapper.saveBudgetAllocation(req.getBudget().getBudgetId(), req.getBudgetAllocations());

        log.info("save fixedExpenses : {}", req.getFixedExpenses());
        budgetMapper.saveFixedExpenses(req.getBudget().getBudgetId(), req.getFixedExpenses());
    }

    @Transactional
    public void update(@Valid BudgetDto req) {
        log.info("update budget : {}", req.getBudget());
        budgetMapper.update(req.getBudget());

        log.info("update budgetAllocation : {}", req.getBudgetAllocations());
        req.getBudgetAllocations().forEach(budgetMapper::updateBudgetAllocation);

        log.info("update fixedExpenses : {}", req.getFixedExpenses());
        req.getFixedExpenses().forEach(budgetMapper::updateFixedExpenses);
    }
}
