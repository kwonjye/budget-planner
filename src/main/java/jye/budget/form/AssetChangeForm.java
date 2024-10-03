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
public class AssetChangeForm {
    @NotNull
    private Long assetId;

    @NotNull
    private CalcType calcType;

    @Min(value = 0, message = "변동 금액은 0 이상이어야 합니다.")
    private int amount;

    private String changeDetail;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate changeDate = LocalDate.now();
}
