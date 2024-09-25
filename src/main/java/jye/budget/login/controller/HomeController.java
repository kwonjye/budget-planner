package jye.budget.login.controller;

import jye.budget.login.argumentresolver.Login;
import jye.budget.login.dto.LoginDto;
import jye.budget.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home(@ModelAttribute("login") LoginDto loginDto,
                       @Login User user, Model model) {

        // 세션에 회원 데이터 있으면 메인 페이지로 이동
        if (user != null) {
            model.addAttribute("user", user);
            return "main";
        }

        return "login";
    }
}
