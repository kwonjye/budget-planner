package jye.budget.controller;

import jakarta.servlet.http.HttpSession;
import jye.budget.entity.Category;
import jye.budget.entity.User;
import jye.budget.login.SessionConst;
import jye.budget.service.CategoryService;
import jye.budget.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String view(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("카테고리 조회 : {}", user);

        List<Category> fixExpenseList = categoryService.findByUserIdAndType(user.getUserId(), CategoryType.FIXED_EXPENSE);
        List<Category> etcBudgetList = categoryService.findByUserIdAndType(user.getUserId(), CategoryType.ETC_BUDGET);
        List<Category> expenseList = categoryService.findByUserIdAndType(user.getUserId(), CategoryType.EXPENSE);

        log.info("{} 카테고리 : {}", CategoryType.FIXED_EXPENSE.getDescription(), fixExpenseList);
        log.info("{} 카테고리 : {}", CategoryType.ETC_BUDGET.getDescription(), etcBudgetList);
        log.info("{} 카테고리 : {}", CategoryType.EXPENSE.getDescription(), expenseList);

        model.addAttribute("fixExpenseList", fixExpenseList);
        model.addAttribute("etcBudgetList", etcBudgetList);
        model.addAttribute("expenseList", expenseList);

        return "/category/view";
    }
}
