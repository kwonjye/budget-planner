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

    List<BudgetAllocation> findBudgetAllocationByBudgetIdForUpdate(@Param("budgetId") Long budgetId,
                                                                   @Param("userId") Long userId);

    List<FixedExpenses> findFixedExpensesByBudgetIdForUpdate(@Param("budgetId") Long budgetId,
                                                             @Param("userId") Long userId);

    void save(@Valid Budget budget);

    void saveBudgetAllocation(BudgetAllocation budgetAllocation);

    void saveFixedExpenses(FixedExpenses fixedExpenses);

    void update(@Valid Budget budget);

    void updateBudgetAllocation(BudgetAllocation budgetAllocation);

    void updateFixedExpenses(FixedExpenses fixedExpenses);

    Budget findRecent(@Param("userId") Long userId);

    Long findChangeIdByBudgetAllocationId(@Param("budgetAllocationId") Long budgetAllocationId);

    Budget findById(@Param("budgetId") Long budgetId);

    void delete(@Param("budgetId") Long budgetId);
}
