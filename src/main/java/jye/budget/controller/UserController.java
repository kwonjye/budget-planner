package jye.budget.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.form.VerifyEmailForm;
import jye.budget.entity.User;
import jye.budget.form.JoinUserForm;
import jye.budget.login.SessionConst;
import jye.budget.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute("joinUserForm") JoinUserForm joinUserForm) {
        return "user/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("joinUserForm") JoinUserForm joinUserForm, BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/join";
        }

        log.info("join user : {}", joinUserForm);

        User user = userService.findByEmail(joinUserForm.getEmail());
        if(user != null) {
            bindingResult.rejectValue("email", "email.exists");
            return "user/join";
        }

        if(!joinUserForm.getPassword().equals(joinUserForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password.mismatch");
            return "user/join";
        }

        userService.save(joinUserForm);

        redirectAttributes.addFlashAttribute("verifyEmailForm", VerifyEmailForm.builder().email(joinUserForm.getEmail()).build());
        return "redirect:/email/verify";
    }

    @GetMapping("/info")
    public String info(Model model, HttpSession session) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);
        model.addAttribute("user", user);
        return "user/info";
    }
}
