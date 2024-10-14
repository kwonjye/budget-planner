package jye.budget.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jye.budget.entity.Expenses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpensesForm {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String expenseName;

    @NotBlank
    private String expenseDetail;

    @Min(0)
    private int amount;

    private String memo;

    private String relatedUrl;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Builder.Default
    private LocalDate expenseDate = LocalDate.now();

    @Builder.Default
    private boolean isNecessary = true;

    public ExpensesForm(Expenses expenses) {
        this.categoryId = expenses.getCategory().getCategoryId();
        this.expenseName = expenses.getExpenseName();
        this.expenseDetail = expenses.getExpenseDetail();
        this.amount = expenses.getAmount();
        this.memo = expenses.getMemo();
        this.relatedUrl = expenses.getRelatedUrl();
        this.expenseDate = expenses.getExpenseDate();
        this.isNecessary = expenses.isNecessary();
    }
}
