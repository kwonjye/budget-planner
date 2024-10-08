package jye.budget.mapper;

import jakarta.validation.Valid;
import jye.budget.entity.Budget;
import jye.budget.entity.BudgetAllocation;
import jye.budget.entity.FixedExpenses;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetMapper {
    Budget findByYearMonth(@Param("yearMonth") String yearMonth,
                           @Param("userId") Long userId);

    List<BudgetAllocation> findBudgetAllocationByBudgetId(@Param("budgetId") Long budgetId);

    List<FixedExpenses> findFixedExpensesByBudgetId(@Param("budgetId") Long budgetId);

    void save(@Valid Budget budget);

    void saveBudgetAllocation(@Param("budgetId") Long budgetId,
                              @Param("budgetAllocations") @Valid List<BudgetAllocation> budgetAllocations);

    void saveFixedExpenses(@Param("budgetId") Long budgetId,
                           @Param("fixedExpenses") @Valid List<FixedExpenses> fixedExpenses);

    void update(@Valid Budget budget);

    void updateBudgetAllocation(BudgetAllocation budgetAllocation);

    void updateFixedExpenses(FixedExpenses fixedExpenses);
}
