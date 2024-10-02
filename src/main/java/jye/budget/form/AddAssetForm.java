package jye.budget.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAssetForm {

    @NotBlank
    private String assetName;

    @Min(value = 0, message = "초기 금액은 0 이상이어야 합니다.")
    private int initialAmount;

    private boolean isAllocated;
}
