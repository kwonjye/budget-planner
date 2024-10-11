package jye.budget.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.Asset;
import jye.budget.entity.Category;
import jye.budget.entity.EtcBudget;
import jye.budget.entity.User;
import jye.budget.form.EtcBudgetForm;
import jye.budget.login.SessionConst;
import jye.budget.req.EtcBudgetReq;
import jye.budget.service.AssetService;
import jye.budget.service.CategoryService;
import jye.budget.service.EtcBudgetService;
import jye.budget.type.CalcType;
import jye.budget.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    private final AssetService assetService;
    private final CategoryService categoryService;

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

        if(req.getCategoryId() != null) {
            Category category = categoryService.check(req.getCategoryId(), user.getUserId());
            if(category == null) {
                return "/error";
            }
            model.addAttribute("category", category);
        }

        model.addAttribute("calcTypeValues", CalcType.values());
        return "/etc-budget/view";
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("etcBudgetForm") EtcBudgetForm etcBudgetForm, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        List<Category> categories = categoryService.findByUserIdAndType(user.getUserId(), CategoryType.ETC_BUDGET);
        model.addAttribute("categories", categories);

        List<Asset> assets = assetService.findByUserId(user.getUserId());
        model.addAttribute("assets", assets);

        model.addAttribute("calcTypeValues", CalcType.values());
        return "/etc-budget/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("etcBudgetForm") EtcBudgetForm etcBudgetForm, BindingResult bindingResult,
                      HttpSession session, Model model) {

        log.info("기타 예산 추가 : {}", etcBudgetForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        List<Category> categories = categoryService.findByUserIdAndType(user.getUserId(), CategoryType.ETC_BUDGET);
        List<Asset> assets = assetService.findByUserId(user.getUserId());
        if (bindingResult.hasErrors()) {
            return handleAddFormError(model, categories, assets);
        }

        Category category = categoryService.check(etcBudgetForm.getCategoryId(), user.getUserId());
        if(category == null) {
            bindingResult.rejectValue("category", "category.notFound");
            return handleAddFormError(model, categories, assets);
        }
        if(etcBudgetForm.getAssetId() != null) {
            boolean isExists = assets.stream()
                    .anyMatch(asset -> asset.getAssetId().equals(etcBudgetForm.getAssetId()));
            if(!isExists) {
                log.error("선택할 수 없는 자산 선택 : {}", etcBudgetForm.getAssetId());
                bindingResult.rejectValue("assetId", "asset.notFound");
                return handleAddFormError(model, categories, assets);
            }
        }
        etcBudgetService.save(user.getUserId(), etcBudgetForm, category);

        return "redirect:/etc-budget?categoryId=" + etcBudgetForm.getCategoryId();
    }

    private String handleAddFormError(Model model, List<Category> categories, List<Asset> assets) {
        model.addAttribute("categories", categories);
        model.addAttribute("assets", assets);
        model.addAttribute("calcTypeValues", CalcType.values());
        return "/etc-budget/add";
    }

    @DeleteMapping("/{etcBudgetId}")
    public ResponseEntity<Void> delete(@PathVariable Long etcBudgetId, HttpSession session) {

        log.info("기타 예산 삭제 : {}", etcBudgetId);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        EtcBudget etcBudget = etcBudgetService.check(etcBudgetId, user.getUserId());
        if(etcBudget == null) {
            return ResponseEntity.notFound().build();
        }

        etcBudgetService.delete(etcBudget);
        return ResponseEntity.noContent().build();
    }
}
