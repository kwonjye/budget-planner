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
public class FixedExpenses {
    private Long fixedExpenseId;
    private Long budgetId;
    private Category category;
    @Min(0)
    private int amount;
}
