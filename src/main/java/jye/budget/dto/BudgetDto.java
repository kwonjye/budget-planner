package jye.budget.dto;

import jakarta.validation.Valid;
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
public class BudgetDto {
    @Valid
    private Budget budget;
    @Valid
    private List<BudgetAllocation> budgetAllocations;
    @Valid
    private List<FixedExpenses> fixedExpenses;
}
