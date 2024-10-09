package jye.budget.req;

import lombok.Data;

@Data
public class ExpensesReq {
    private String searchDate; // yyyy-MM
    private String searchText;
}
