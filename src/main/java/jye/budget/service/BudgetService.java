package jye.budget.service;

import jye.budget.entity.*;
import jye.budget.mapper.AssetMapper;
import jye.budget.mapper.BudgetMapper;
import jye.budget.mapper.CategoryMapper;
import jye.budget.req.BudgetReq;
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
    public BudgetDto findByYearMonthAndUserId(BudgetReq req, Long userId) {
        Budget budget = budgetMapper.findByYearMonth(req, userId);
        log.info("{} budget : {}", req.getSearchDate(), budget);

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
                    .budget(Budget.builder().yearMonth(req.getSearchDate()).build())
                    .budgetAllocations(budgetAllocations)
                    .fixedExpenses(fixedExpenses)
                    .build();
        }
    }
}
