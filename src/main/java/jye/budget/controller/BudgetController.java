package jye.budget.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jye.budget.entity.User;
import jye.budget.login.SessionConst;
import jye.budget.req.BudgetReq;
import jye.budget.dto.BudgetDto;
import jye.budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {


    private final BudgetService budgetService;

    @GetMapping
    public String view(@ModelAttribute("req") BudgetReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("budget view : {}", user);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        BudgetDto data = budgetService.findByYearMonthAndUserId(req, user.getUserId());
        model.addAttribute("data", data);

        return "budget/view";
    }

    @GetMapping("/edit")
    public String editForm(@ModelAttribute("req") BudgetReq req,
                           @ModelAttribute("budget") BudgetDto budget, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        BudgetDto data = budgetService.findByYearMonthAndUserId(req, user.getUserId());
        model.addAttribute("data", data);

        return "budget/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("budget") BudgetDto budget, HttpSession session) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("edit budget : {}", user);

        return "redirect:/budget";
    }
}
