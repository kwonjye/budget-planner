package jye.budget.controller;

import jakarta.validation.Valid;
import jye.budget.form.VerifyEmailForm;
import jye.budget.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public String send(@ModelAttribute("verifyEmailForm") VerifyEmailForm verifyEmailForm) {
        emailService.verifyEmail(verifyEmailForm.getEmail());
        return "email/verify";
    }

    @PostMapping("/verify")
    public String verify(@Valid @ModelAttribute("verifyEmailForm") VerifyEmailForm verifyEmailForm, BindingResult bindingResult) {

        log.info("email verify {}", verifyEmailForm);

        boolean isVerified = emailService.verifyCode(verifyEmailForm.getEmail(), verifyEmailForm.getCode());
        if(!isVerified) {
            bindingResult.rejectValue("code", "email.code.mismatch");
            return "email/verify";
        }
        return "email/verify-success";
    }
}
