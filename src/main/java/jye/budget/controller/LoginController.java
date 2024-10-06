package jye.budget.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.login.SessionConst;
import jye.budget.form.LoginForm;
import jye.budget.entity.User;
import jye.budget.service.UserService;
import jye.budget.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login";
    }

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
        if(PasswordUtil.isPasswordMismatch(loginForm.getPassword(), loginUser.getPassword())) {
            bindingResult.rejectValue("password","password.mismatch");
            return "login";
        }
        if(!loginUser.isVerified()) {
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.EMAIL, loginForm.getEmail());
            return "email/error/login";
        }

        // 로그인 성공
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, loginUser);

        return "redirect:" + redirectURL;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
