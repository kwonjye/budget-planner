package jye.budget.controller;

import jakarta.validation.Valid;
import jye.budget.form.EmailForm;
import jye.budget.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/verify")
    public String verifyForm(@ModelAttribute("emailForm") EmailForm emailForm) {
        return "user/verify";
    }

    @PostMapping("/verify")
    public String verify(@Valid @ModelAttribute("emailForm") EmailForm emailForm, BindingResult bindingResult) {
        boolean isVerified = emailService.verifyCode(emailForm.getEmail(), emailForm.getCode());
        if(!isVerified) {
            bindingResult.reject("code", "email.code.mismatch");
            return "user/verify";
        }
        return "redirect:/";
    }
}
