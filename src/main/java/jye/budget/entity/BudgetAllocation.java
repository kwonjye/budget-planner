package jye.budget.entity;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetAllocation {
    private Long budgetAllocationId;
    private Long budgetId;
    private Asset asset;
    @Min(0)
    private int amount;
}
