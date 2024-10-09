package jye.budget.type;

import lombok.Getter;

@Getter
public enum CategoryType {
    FIXED_EXPENSE("고정 지출"),
    ETC_BUDGET("기타 예산"),
    EXPENSE("지출");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }
}
