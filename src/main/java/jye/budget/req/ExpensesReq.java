package jye.budget.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpensesReq {
    private String searchDate; // yyyy-MM
    private String searchText;
    private Long categoryId;
}
