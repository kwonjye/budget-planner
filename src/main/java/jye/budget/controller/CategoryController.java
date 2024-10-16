package jye.budget.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.Category;
import jye.budget.entity.User;
import jye.budget.form.CategoryForm;
import jye.budget.login.SessionConst;
import jye.budget.service.CategoryService;
import jye.budget.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

        return "category/view";
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId, HttpSession session) {

        log.info("카테고리 삭제 : {}", categoryId);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Category category = categoryService.check(categoryId, user.getUserId());
        if(category == null) {
            return ResponseEntity.notFound().build();
        }

        categoryService.delete(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}")
    public String editForm(@PathVariable Long categoryId, HttpSession session, Model model) {

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Category category = categoryService.check(categoryId, user.getUserId());
        if(category == null) {
            return "error";
        }

        model.addAttribute("categoryForm", new CategoryForm(category));
        return "category/edit";
    }

    @PostMapping("/{categoryId}")
    public String edit(@PathVariable Long categoryId, @Valid @ModelAttribute CategoryForm categoryForm, BindingResult bindingResult,
                       HttpSession session) {

        log.info("카테고리 수정 : {}", categoryForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Category category = categoryService.check(categoryId, user.getUserId());
        if(category == null) {
            return "error";
        }

        if(bindingResult.hasErrors()) {
            categoryForm.setCategoryType(category.getCategoryType());
            return "category/edit";
        }

        categoryService.update(categoryId, categoryForm);
        return "redirect:/category";
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("categoryForm") CategoryForm categoryForm, Model model) {
        model.addAttribute("categoryTypeValues", CategoryType.values());
        return "category/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("categoryForm") CategoryForm categoryForm, BindingResult bindingResult,
                      HttpSession session, Model model) {

        log.info("카테고리 추가 : {}", categoryForm);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryTypeValues", CategoryType.values());
            return "category/add";
        }
        if(categoryForm.getCategoryType() == null) {
            log.error("카테고리 유형 없음");
            bindingResult.rejectValue("categoryType", "category.type.notFound");
            model.addAttribute("categoryTypeValues", CategoryType.values());
            return "category/add";
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        categoryService.save(user.getUserId(), categoryForm);

        return "redirect:/category";
    }
}
