package jye.budget.mapper;

import jye.budget.entity.Budget;
import jye.budget.entity.BudgetAllocation;
import jye.budget.entity.FixedExpenses;
import jye.budget.req.BudgetReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetMapper {
    Budget findByYearMonth(@Param("req") BudgetReq req,
                           @Param("userId") Long userId);

    List<BudgetAllocation> findBudgetAllocationByBudgetId(@Param("budgetId") Long budgetId);

    List<FixedExpenses> findFixedExpensesByBudgetId(@Param("budgetId") Long budgetId);
}
