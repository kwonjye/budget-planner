package jye.budget.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.form.VerifyEmailForm;
import jye.budget.login.SessionConst;
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

    @GetMapping("/send")
    public String send(@ModelAttribute("verifyEmailForm") VerifyEmailForm verifyEmailForm,
                       HttpSession session) {

        String email = (String) session.getAttribute(SessionConst.EMAIL);
        verifyEmailForm.setEmail(email);

        emailService.verifyEmail(email);
        return "email/verify";
    }

    @PostMapping("/verify")
    public String verify(@Valid @ModelAttribute("verifyEmailForm") VerifyEmailForm verifyEmailForm, BindingResult bindingResult,
                         HttpSession session) {

        String email = (String) session.getAttribute(SessionConst.EMAIL);
        verifyEmailForm.setEmail(email);

        log.info("이메일 인증 : {}", verifyEmailForm);

        if (bindingResult.hasErrors()) {
            return "email/verify";
        }

        boolean isVerified = emailService.verifyCode(email, verifyEmailForm.getCode());
        if(!isVerified) {
            bindingResult.rejectValue("code", "email.code.mismatch");
            return "email/verify";
        }
        return "email/verify-success";
    }
}
