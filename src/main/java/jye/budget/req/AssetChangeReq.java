package jye.budget.req;

import jye.budget.type.CalcType;
import lombok.Data;

@Data
public class AssetChangeReq {
    private String searchDate; // yyyy-MM
    private Long assetId;
    private CalcType calcType;
    private String searchText;
}
