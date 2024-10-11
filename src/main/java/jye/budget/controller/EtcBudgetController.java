package jye.budget.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jye.budget.entity.Category;
import jye.budget.entity.EtcBudget;
import jye.budget.entity.User;
import jye.budget.login.SessionConst;
import jye.budget.req.EtcBudgetReq;
import jye.budget.service.EtcBudgetService;
import jye.budget.type.CalcType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/etc-budget")
@RequiredArgsConstructor
public class EtcBudgetController {

    private final EtcBudgetService etcBudgetService;

    @GetMapping
    public String view(@ModelAttribute("req") EtcBudgetReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("기타 예산 내역 : {}", user);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        List<EtcBudget> etcBudgets = etcBudgetService.findByReqAndUserId(req, user.getUserId());
        Map<LocalDate, List<EtcBudget>> groupedByEtcBudgetDate = etcBudgets.stream()
                .sorted(Comparator.comparing(EtcBudget::getCreatedAt).reversed())
                .collect(Collectors.groupingBy(EtcBudget::getEtcBudgetDate));
        model.addAttribute("groupedByEtcBudgetDate", groupedByEtcBudgetDate);

        List<Category> categories = etcBudgetService.findCategoryBySearchDateAndUserId(req.getSearchDate(), user.getUserId());
        model.addAttribute("categories", categories);

        model.addAttribute("calcTypeValues", CalcType.values());
        return "/etc-budget/view";
    }
}
