package jye.budget.entity;

import jye.budget.type.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {
    private Long subscriptionId;
    private Long userId;
    private String subscriptionName;
    private SubscriptionType subscriptionType;
    private LocalDate subscriptionDate;
    private int subscriptionCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
