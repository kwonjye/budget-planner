package jye.budget.type;

import lombok.Getter;

@Getter
public enum CalcType {
    ADD("+"),
    SUB("-");

    private final String symbol;

    CalcType(String symbol) {
        this.symbol = symbol;
    }
}
