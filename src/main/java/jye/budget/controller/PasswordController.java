package jye.budget.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.User;
import jye.budget.form.ChangePasswordForm;
import jye.budget.form.FindPasswordForm;
import jye.budget.login.SessionConst;
import jye.budget.service.PasswordService;
import jye.budget.service.UserService;
import jye.budget.util.PasswordUtil;
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
    public String find(@Valid @ModelAttribute("findPasswordForm") FindPasswordForm findPasswordForm, BindingResult bindingResult,
                       HttpServletRequest request) {
        log.info("find password : {}", findPasswordForm);

        if (bindingResult.hasErrors()) {
            return "password/find";
        }

        User loginUser = userService.findByEmail(findPasswordForm.getEmail());
        if(loginUser == null) {
            bindingResult.rejectValue("email", "email.notFound");
            return "password/find";
        }
        if(!loginUser.isVerified()) {
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.EMAIL, findPasswordForm.getEmail());
            return "email/error/password";
        }

        passwordService.issueTemp(findPasswordForm.getEmail());
        return "password/temp-issue";
    }

    @GetMapping("/change")
    public String changeForm(@ModelAttribute("changePasswordForm") ChangePasswordForm changePasswordForm) {
        return "password/change";
    }

    @PostMapping("/change")
    public String change(@Valid @ModelAttribute("changePasswordForm") ChangePasswordForm changePasswordForm, BindingResult bindingResult,
                         HttpSession session) {

        log.info("change password : {}", changePasswordForm);

        if (bindingResult.hasErrors()) {
            return "password/change";
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);
        if(PasswordUtil.isPasswordMismatch(changePasswordForm.getPassword(), user.getPassword())) {
            bindingResult.rejectValue("password","password.mismatch");
            return "password/change";
        }
        if(!changePasswordForm.getChangePassword().equals(changePasswordForm.getChangePasswordConfirm())) {
            bindingResult.rejectValue("changePasswordConfirm", "password.change.mismatch");
            return "password/change";
        }

        passwordService.change(user.getEmail(), changePasswordForm.getChangePassword());
        session.invalidate();
        return "password/change-success";
    }
}
