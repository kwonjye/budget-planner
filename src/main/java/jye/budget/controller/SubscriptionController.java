package jye.budget.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.Subscription;
import jye.budget.entity.User;
import jye.budget.form.SubscriptionForm;
import jye.budget.login.SessionConst;
import jye.budget.service.SubscriptionService;
import jye.budget.type.SubscriptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

        int annualTotalCost = annualSubscriptions.stream()
                .mapToInt(Subscription::getSubscriptionCost)
                .sum();
        int monthlyTotalCost = monthlySubscriptions.stream()
                .mapToInt(Subscription::getSubscriptionCost)
                .sum();

        log.info("{} 구독 : {}", SubscriptionType.ANNUAL, annualSubscriptions);
        log.info("{} 구독 합계 : {}", SubscriptionType.ANNUAL, annualTotalCost);
        log.info("{} 구독 : {}", SubscriptionType.MONTHLY, monthlySubscriptions);
        log.info("{} 구독 합계 : {}", SubscriptionType.MONTHLY, monthlyTotalCost);

        model.addAttribute("annualSubscriptions", annualSubscriptions);
        model.addAttribute("monthlySubscriptions", monthlySubscriptions);
        model.addAttribute("annualTotalCost", annualTotalCost);
        model.addAttribute("monthlyTotalCost", monthlyTotalCost);

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

    @GetMapping("{subscriptionId}")
    public String editForm(@PathVariable Long subscriptionId, HttpSession session, Model model) {

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Subscription subscription = subscriptionService.check(subscriptionId, user.getUserId());
        if(subscription == null) {
            return "error";
        }

        model.addAttribute("subscriptionForm", new SubscriptionForm(subscription));
        model.addAttribute("subscriptionTypeValues", SubscriptionType.values());
        return "subscription/form";
    }

    @PostMapping("{subscriptionId}")
    public String edit(@PathVariable Long subscriptionId, @Valid @ModelAttribute("subscriptionForm") SubscriptionForm subscriptionForm, BindingResult bindingResult,
                       HttpSession session, Model model) {

        log.info("구독 수정 : {}", subscriptionForm);

        if (bindingResult.hasErrors()) {
            model.addAttribute("subscriptionTypeValues", SubscriptionType.values());
            return "subscription/form";
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Subscription subscription = subscriptionService.check(subscriptionId, user.getUserId());
        if (subscription == null) {
            return "error";
        }

        subscriptionService.update(subscriptionId, subscriptionForm);
        return "redirect:/subscription";
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("subscriptionForm") SubscriptionForm subscriptionForm, Model model) {
        model.addAttribute("subscriptionTypeValues", SubscriptionType.values());
        return "subscription/form";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("subscriptionForm") SubscriptionForm subscriptionForm, BindingResult bindingResult,
                      HttpSession session, Model model) {

        log.info("구독 추가 : {}", subscriptionForm);

        if (bindingResult.hasErrors()) {
            model.addAttribute("subscriptionTypeValues", SubscriptionType.values());
            return "subscription/form";
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);
        subscriptionService.save(user.getUserId(), subscriptionForm);
        return "redirect:/subscription";
    }
}
