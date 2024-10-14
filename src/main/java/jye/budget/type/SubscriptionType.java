package jye.budget.type;

import lombok.Getter;

@Getter
public enum SubscriptionType {
    ANNUAL("연간"),
    MONTHLY("월간");

    private final String description;

    SubscriptionType(String description) {
        this.description = description;
    }
}
