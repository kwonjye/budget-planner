package jye.budget.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jye.budget.entity.Subscription;
import jye.budget.type.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionForm {
    @NotBlank
    private String subscriptionName;

    @NotNull
    private SubscriptionType subscriptionType;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate subscriptionDate = LocalDate.now();

    @Min(0)
    private int subscriptionCost;

    public SubscriptionForm(Subscription subscription) {
        this.subscriptionName = subscription.getSubscriptionName();
        this.subscriptionType = subscription.getSubscriptionType();
        this.subscriptionDate = subscription.getSubscriptionDate();
        this.subscriptionCost = subscription.getSubscriptionCost();
    }
}
