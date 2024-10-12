package jye.budget.req;

import jye.budget.type.CalcType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtcBudgetReq {
    private String searchDate; // yyyy-MM
    private Long categoryId;
    private CalcType calcType;
    private String searchText;
}
