package jye.budget.entity;

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
public class Expenses {
    private Long expenseId;
    private Long userId;
    private Category category;
    private String expenseName;
    private String expenseDetail;
    private int amount;
    private String memo;
    private String relatedUrl;
    private LocalDate expenseDate;
    private boolean isNecessary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
