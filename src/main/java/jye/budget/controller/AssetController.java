package jye.budget.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.Asset;
import jye.budget.entity.User;
import jye.budget.form.AssetForm;
import jye.budget.login.SessionConst;
import jye.budget.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/add")
    public String addForm(@ModelAttribute("assetForm") AssetForm assetForm) {
        return "/asset/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("assetForm") AssetForm assetForm, BindingResult bindingResult,
                      HttpSession session) {

        log.info("add asset : {}", assetForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        boolean existsByAssetName = assetService.existsByAssetName(user.getUserId(), assetForm.getAssetName(), null);
        if(existsByAssetName) {
            log.error("existsByAssetName : {}", assetForm.getAssetName());
            bindingResult.rejectValue("assetName", "asset.exists");
            return "/asset/add";
        }

        assetService.save(user.getUserId(), assetForm);

        return "redirect:/asset";
    }

    @GetMapping("/edit/{assetId}")
    public String editForm(@PathVariable Long assetId,
                           HttpSession session, Model model) {

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Asset asset = assetService.findById(assetId);
        if(asset == null) {
            return "redirect:/asset";
        }
        if(!asset.getUserId().equals(user.getUserId())) {
            return "redirect:/asset";
        }
        model.addAttribute("asset", asset);
        return "/asset/edit";
    }

    @PostMapping("/edit/{assetId}")
    public String edit(@PathVariable Long assetId, @ModelAttribute("assetForm") AssetForm assetForm, BindingResult bindingResult,
                       HttpSession session) {

        log.info("edit asset : {}", assetForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Asset asset = assetService.findById(assetId);
        if(asset == null) {
            return "redirect:/asset";
        }
        if(!asset.getUserId().equals(user.getUserId())) {
            return "redirect:/asset";
        }

        boolean existsByAssetName = assetService.existsByAssetName(user.getUserId(), assetForm.getAssetName(), assetId);
        if(existsByAssetName) {
            log.error("existsByAssetName : {}", assetForm.getAssetName());
            bindingResult.rejectValue("assetName", "asset.exists");
            return "/asset/edit";
        }

        assetService.update(assetId, assetForm);
        return "redirect:/asset";
    }
}
