package jye.budget.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jye.budget.type.CalcType;
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
public class EtcBudgetForm {
    @NotNull
    private Long categoryId;

    private Long assetId;

    @NotNull
    private CalcType calcType;

    @Min(0)
    private int amount;

    private String etcBudgetDetail;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate etcBudgetDate = LocalDate.now();
}
