package jye.budget.service;

import jye.budget.entity.Subscription;
import jye.budget.mapper.SubscriptionMapper;
import jye.budget.type.SubscriptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;

    @Transactional(readOnly = true)
    public List<Subscription> findByTypeAndUserId(Long userId, SubscriptionType subscriptionType) {
        return subscriptionMapper.findByTypeAndUserId(userId, subscriptionType);
    }
}
