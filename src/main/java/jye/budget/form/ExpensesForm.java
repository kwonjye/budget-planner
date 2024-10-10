package jye.budget.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
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
    private LocalDate expenseDate = LocalDate.now();

    private boolean isNecessary = true;
}
