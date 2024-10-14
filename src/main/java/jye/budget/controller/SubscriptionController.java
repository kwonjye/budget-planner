package jye.budget.controller;

import jakarta.servlet.http.HttpSession;
import jye.budget.entity.Subscription;
import jye.budget.entity.User;
import jye.budget.login.SessionConst;
import jye.budget.service.SubscriptionService;
import jye.budget.type.SubscriptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public String view(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("구독 조회 : {}", user);

        List<Subscription> annualSubscriptions = subscriptionService.findByTypeAndUserId(user.getUserId(), SubscriptionType.ANNUAL);
        List<Subscription> monthlySubscriptions = subscriptionService.findByTypeAndUserId(user.getUserId(), SubscriptionType.MONTHLY);

        log.info("{} 구독 : {}", SubscriptionType.ANNUAL, annualSubscriptions);
        log.info("{} 구독 : {}", SubscriptionType.MONTHLY, monthlySubscriptions);

        model.addAttribute("annualSubscriptions", annualSubscriptions);
        model.addAttribute("monthlySubscriptions", monthlySubscriptions);

        return "subscription/view";
    }

    @DeleteMapping("{subscriptionId}")
    public ResponseEntity<Void> delete(@PathVariable Long subscriptionId, HttpSession session) {

        log.info("구독 삭제 : {}", subscriptionId);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Subscription subscription = subscriptionService.check(subscriptionId, user.getUserId());
        if(subscription == null) {
            return ResponseEntity.notFound().build();
        }

        subscriptionService.delete(subscriptionId);
        return ResponseEntity.noContent().build();
    }
}
