package jye.budget.entity;

import jye.budget.type.CategoryType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {
    @EqualsAndHashCode.Include
    private Long categoryId;
    private Long userId;
    private CategoryType categoryType;
    private String categoryName;
    private String categoryColor;
    private boolean isUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
