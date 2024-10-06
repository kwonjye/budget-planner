package jye.budget.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jye.budget.entity.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetForm {

    @NotBlank
    private String assetName;

    @Min(value = 0, message = "초기 금액은 0 이상이어야 합니다.")
    private int initialAmount;

    private boolean isAllocated;

    public AssetForm(Asset asset) {
        this.assetName = asset.getAssetName();
        this.initialAmount = asset.getInitialAmount();
        this.isAllocated = asset.isAllocated();
    }
}
