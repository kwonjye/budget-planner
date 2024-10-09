package jye.budget.entity;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {
    private Long budgetId;
    private Long userId;
    private String yearMonth;
    @Min(0)
    private int totalBudget;
    @Min(0)
    private int assetAllocation;
    @Min(0)
    private int fixedExpenses;
    @Min(0)
    private int livingExpenseBudget;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
