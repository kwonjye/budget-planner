package jye.budget.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.Asset;
import jye.budget.entity.AssetChange;
import jye.budget.entity.User;
import jye.budget.form.AssetForm;
import jye.budget.login.SessionConst;
import jye.budget.req.AssetChangeReq;
import jye.budget.service.AssetService;
import jye.budget.type.CalcType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        Asset asset = checkAsset(assetId, user.getUserId());
        if(asset == null) {
            return "/error";
        }

        AssetForm assetForm = AssetForm.builder()
                .assetName(asset.getAssetName())
                .initialAmount(asset.getInitialAmount())
                .isAllocated(asset.isAllocated())
                .build();

        model.addAttribute("assetForm", assetForm);
        return "/asset/edit";
    }

    @PostMapping("/edit/{assetId}")
    public String edit(@PathVariable Long assetId, @Valid @ModelAttribute("assetForm") AssetForm assetForm, BindingResult bindingResult,
                       HttpSession session) {

        log.info("edit asset : {}", assetForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Asset asset = checkAsset(assetId, user.getUserId());
        if(asset == null) {
            return "/error";
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

    private Asset checkAsset(Long assetId, Long userId) {
        Asset asset = assetService.findById(assetId);
        if(asset == null) {
            log.error("asset not found: {}", assetId);
            return null;
        }
        if(!asset.getUserId().equals(userId)) {
            log.error("asset userId mismatch: assetId - {}, assetUserId - {}, userId - {}",
                    asset.getAssetId(), asset.getUserId(), userId);
            return null;
        }
        return asset;
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> delete(@PathVariable Long assetId, HttpSession session) {

        log.info("delete asset : {}", assetId);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Asset asset = checkAsset(assetId, user.getUserId());
        if(asset == null) {
            return ResponseEntity.badRequest().build();
        }

        assetService.delete(assetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/change")
    public String changeView(@ModelAttribute("req") AssetChangeReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("asset change view : {}", user);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        List<AssetChange> assetChanges = assetService.findChange(req, user.getUserId());
        Map<LocalDate, List<AssetChange>> groupedByChangeDate = assetChanges.stream()
                .collect(Collectors.groupingBy(AssetChange::getChangeDate));
        model.addAttribute("groupedByChangeDate", groupedByChangeDate);

        List<Asset> assets = assetService.findByUserId(user.getUserId());
        model.addAttribute("assets", assets);

        model.addAttribute("calcTypeValues", CalcType.values());
        return "/asset/change/view";
    }
}
