package jye.budget.service;

import jakarta.validation.Valid;
import jye.budget.entity.Subscription;
import jye.budget.form.SubscriptionForm;
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

    @Transactional(readOnly = true)
    public Subscription check(Long subscriptionId, Long userId) {
        Subscription subscription = findById(subscriptionId);
        if(subscription == null) {
            log.error("구독 정보 없음 : {}", subscriptionId);
            return null;
        }
        if(!subscription.getUserId().equals(userId)) {
            log.error("회원 ID 불일치 : subscription - {}, userId - {}", subscription, userId);
            return null;
        }
        return subscription;
    }

    @Transactional(readOnly = true)
    public Subscription findById(Long subscriptionId) {
        return subscriptionMapper.findById(subscriptionId);
    }

    @Transactional
    public void delete(Long subscriptionId) {
        log.info("delete subscription : {}", subscriptionId);
        subscriptionMapper.delete(subscriptionId);
    }

    @Transactional
    public void update(Long subscriptionId, @Valid SubscriptionForm subscriptionForm) {
        Subscription subscription = Subscription.builder()
                .subscriptionId(subscriptionId)
                .subscriptionName(subscriptionForm.getSubscriptionName())
                .subscriptionType(subscriptionForm.getSubscriptionType())
                .subscriptionDate(subscriptionForm.getSubscriptionDate())
                .subscriptionCost(subscriptionForm.getSubscriptionCost())
                .build();
        log.info("update subscription : {}", subscription);
        subscriptionMapper.update(subscription);
    }

    @Transactional
    public void save(Long userId, @Valid SubscriptionForm subscriptionForm) {
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .subscriptionName(subscriptionForm.getSubscriptionName())
                .subscriptionType(subscriptionForm.getSubscriptionType())
                .subscriptionDate(subscriptionForm.getSubscriptionDate())
                .subscriptionCost(subscriptionForm.getSubscriptionCost())
                .build();
        log.info("save subscription : {}", subscription);
        subscriptionMapper.save(subscription);
    }
}
