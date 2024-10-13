package jye.budget.form;

import jakarta.validation.constraints.NotBlank;
import jye.budget.entity.Category;
import jye.budget.type.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryForm {

    private CategoryType categoryType;

    @NotBlank
    private String categoryName;

    private String categoryColor;

    public CategoryForm(Category category) {
        this.categoryType = category.getCategoryType();
        this.categoryName = category.getCategoryName();
        this.categoryColor = category.getCategoryColor();
    }
}
