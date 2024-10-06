package jye.budget.form;

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

    private int initialAmount;

    private boolean isAllocated;

    public AssetForm(Asset asset) {
        this.assetName = asset.getAssetName();
        this.initialAmount = asset.getInitialAmount();
        this.isAllocated = asset.isAllocated();
    }
}
