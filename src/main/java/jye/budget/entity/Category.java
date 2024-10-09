package jye.budget.entity;

import jye.budget.type.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private Long categoryId;
    private Long userId;
    private CategoryType categoryType;
    private String categoryName;
    private String categoryColor;
    private boolean isUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
