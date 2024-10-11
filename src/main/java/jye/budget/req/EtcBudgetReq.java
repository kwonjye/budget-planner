package jye.budget.req;

import jye.budget.type.CalcType;
import lombok.Data;

@Data
public class EtcBudgetReq {
    private String searchDate; // yyyy-MM
    private Long categoryId;
    private CalcType calcType;
    private String searchText;
}
