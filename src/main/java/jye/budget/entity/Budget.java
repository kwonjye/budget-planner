package jye.budget.entity;

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
    private int totalBudget;
    private int assetAllocation;
    private int fixedExpenses;
    private int livingExpenseBudget;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
