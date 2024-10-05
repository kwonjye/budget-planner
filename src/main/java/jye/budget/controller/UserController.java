package jye.budget.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.form.DeleteUserForm;
import jye.budget.entity.User;
import jye.budget.form.JoinUserForm;
import jye.budget.login.SessionConst;
import jye.budget.service.UserService;
import jye.budget.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
                       HttpServletRequest request) {

        log.info("join user : {}", joinUserForm);

        if (bindingResult.hasErrors()) {
            return "user/join";
        }

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

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.EMAIL, joinUserForm.getEmail());
        return "redirect:/email/send";
    }

    @GetMapping("/info")
    public String info(Model model, HttpSession session) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);
        model.addAttribute("user", user);
        return "user/info";
    }

    @GetMapping("/delete")
    public String deleteForm(@ModelAttribute("deleteUserForm") DeleteUserForm deleteUserForm) {
        return "user/delete";
    }

    @PostMapping("/delete")
    public String delete(@ModelAttribute("deleteUserForm") DeleteUserForm deleteUserForm, BindingResult bindingResult,
                         HttpSession session) {

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);
        if(PasswordUtil.isPasswordMismatch(deleteUserForm.getPassword(), user.getPassword())) {
            bindingResult.rejectValue("password","password.mismatch");
            return "user/delete";
        }

        userService.delete(user.getUserId());
        session.invalidate();
        return "user/delete-success";
    }
}
