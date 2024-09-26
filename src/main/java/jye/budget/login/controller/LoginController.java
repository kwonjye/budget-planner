package jye.budget.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.login.SessionConst;
import jye.budget.login.form.LoginForm;
import jye.budget.user.entity.User;
import jye.budget.user.service.UserService;
import jye.budget.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        User loginUser = userService.findByEmail(loginForm.getEmail());
        log.info("login? {}", loginUser);

        if(loginUser == null) {
            bindingResult.rejectValue("email", "email.notFound");
            return "login";
        }
        if(!PasswordUtil.verifyPassword(loginForm.getPassword(), loginUser.getPassword())) {
            bindingResult.rejectValue("password","password.mismatch");
            return "login";
        }
        if(!loginUser.isVerified()) {
            bindingResult.reject("email.notVerified");
            return "login";
        }

        // 로그인 성공
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, loginUser);

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
