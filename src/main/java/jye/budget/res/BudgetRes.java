package jye.budget.res;

import jye.budget.entity.Budget;
import jye.budget.entity.BudgetAllocation;
import jye.budget.entity.FixedExpenses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRes {
    private Budget budget;
    private List<BudgetAllocation> budgetAllocations;
    private List<FixedExpenses> fixedExpenses;
}
