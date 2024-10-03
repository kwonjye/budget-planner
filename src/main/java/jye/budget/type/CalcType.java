package jye.budget.type;

import lombok.Getter;

@Getter
public enum CalcType {
    ADD("+", "+ 더하기"),
    SUB("-", "- 빼기");

    private final String symbol;
    private final String text;

    CalcType(String symbol, String text) {
        this.symbol = symbol;
        this.text = text;
    }
}
