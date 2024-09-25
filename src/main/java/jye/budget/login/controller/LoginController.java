package jye.budget.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.login.SessionConst;
import jye.budget.login.dto.LoginDto;
import jye.budget.login.service.LoginService;
import jye.budget.user.entity.User;
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

    private final LoginService loginService;

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("login") LoginDto loginDto, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        User loginUser = loginService.login(loginDto.getEmail());
        log.info("login? {}", loginUser);

        if(loginUser == null) {
            bindingResult.rejectValue("email", "error","이메일이 존재하지 않습니다.");
            return "login";
        }
        if(!loginUser.getPassword().equals(loginDto.getPassword())) {
            bindingResult.rejectValue("password","error", "비밀번호가 일치하지 않습니다.");
            return "login";
        }
        if(!loginUser.isVerified()) {
            bindingResult.reject("loginFail", "인증되지 않은 이메일입니다.");
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
