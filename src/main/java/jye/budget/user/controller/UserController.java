package jye.budget.user.controller;

import jakarta.validation.Valid;
import jye.budget.user.entity.User;
import jye.budget.user.form.UserForm;
import jye.budget.user.service.UserService;
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
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute("userForm") UserForm userForm) {
        return "user/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/join";
        }

        log.info("join user : {}", userForm);

        User user = userService.findByEmail(userForm.getEmail());
        if(user != null) {
            bindingResult.rejectValue("email", "email.exists");
            return "user/join";
        }

        if(!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password.mismatch");
            return "user/join";
        }

        userService.save(userForm);
        return "redirect:/";
    }
}
