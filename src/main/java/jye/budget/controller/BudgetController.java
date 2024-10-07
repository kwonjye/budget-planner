package jye.budget.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.User;
import jye.budget.login.SessionConst;
import jye.budget.req.BudgetReq;
import jye.budget.dto.BudgetDto;
import jye.budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String editForm(@ModelAttribute("req") BudgetReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        BudgetDto budgetDto = budgetService.findByYearMonthAndUserId(req, user.getUserId());
        model.addAttribute("budgetDto", budgetDto);

        return "budget/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("budgetDto") BudgetDto budget, BindingResult bindingResult,
                       HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "budget/edit";
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("edit budget : {}", user);

        return "redirect:/budget";
    }
}
