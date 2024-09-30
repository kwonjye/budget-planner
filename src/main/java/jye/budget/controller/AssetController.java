package jye.budget.controller;

import jakarta.servlet.http.HttpSession;
import jye.budget.entity.Asset;
import jye.budget.entity.User;
import jye.budget.login.SessionConst;
import jye.budget.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/asset")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    public String view(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("asset view : {}", user);

        List<Asset> assets = assetService.findByUserId(user.getUserId());
        int totalAmount = assets.stream().mapToInt(Asset::getCurrentAmount).sum();

        model.addAttribute("assets", assets);
        model.addAttribute("totalAmount", totalAmount);
        return "/asset/view";
    }
}
