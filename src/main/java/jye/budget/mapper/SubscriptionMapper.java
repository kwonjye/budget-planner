package jye.budget.mapper;

import jye.budget.entity.Subscription;
import jye.budget.type.SubscriptionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubscriptionMapper {
    List<Subscription> findByTypeAndUserId(@Param("userId") Long userId,
                                           @Param("subscriptionType") SubscriptionType subscriptionType);

    Subscription findById(@Param("subscriptionId") Long subscriptionId);

    void delete(@Param("subscriptionId") Long subscriptionId);
}
