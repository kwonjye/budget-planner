package jye.budget.controller;

import jye.budget.entity.User;
import jye.budget.form.FindPasswordForm;
import jye.budget.service.PasswordService;
import jye.budget.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/password")
public class PasswordController {

    private final UserService userService;
    private final PasswordService passwordService;

    @GetMapping("/find")
    public String findForm(@ModelAttribute("findPasswordForm") FindPasswordForm findPasswordForm) {
        return "password/find";
    }

    @PostMapping("/find")
    public String find(@ModelAttribute("findPasswordForm") FindPasswordForm findPasswordForm, BindingResult bindingResult) {

        log.info("find password : {}", findPasswordForm);

        User loginUser = userService.findByEmail(findPasswordForm.getEmail());
        if(loginUser == null) {
            bindingResult.rejectValue("email", "email.notFound");
            return "password/find";
        }
        if(!loginUser.isVerified()) {
            bindingResult.rejectValue("email", "email.notVerified");
            return "password/verify-email";
        }

        passwordService.issueTemp(findPasswordForm.getEmail());
        return "password/temp-issue";
    }
}
