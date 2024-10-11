package jye.budget.entity;

import jye.budget.type.CalcType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtcBudget {
    private Long etcBudgetId;
    private Long userId;
    private Category category;
    private Asset asset;
    private CalcType calcType;
    private int amount;
    private String etcBudgetDetail;
    private LocalDate etcBudgetDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
