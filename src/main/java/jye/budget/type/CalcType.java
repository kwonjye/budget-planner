package jye.budget.type;

import lombok.Getter;

@Getter
public enum CalcType {
    ADD("+", "+ 더하기") {
        @Override
        public int apply(int current, int amount) {
            return current + amount;
        }
    },
    SUB("-", "- 빼기") {
        @Override
        public int apply(int current, int amount) {
            return current - amount;
        }
    };

    private final String symbol;
    private final String text;

    CalcType(String symbol, String text) {
        this.symbol = symbol;
        this.text = text;
    }

    public abstract int apply(int current, int amount);
}
