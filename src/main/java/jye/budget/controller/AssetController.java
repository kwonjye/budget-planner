package jye.budget.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jye.budget.entity.Asset;
import jye.budget.entity.AssetChange;
import jye.budget.entity.User;
import jye.budget.form.AssetChangeForm;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        log.info("자산 조회 : {}", user);

        List<Asset> assets = assetService.findByUserId(user.getUserId());
        int totalAmount = assets.stream().mapToInt(Asset::getCurrentAmount).sum();

        model.addAttribute("assets", assets);
        model.addAttribute("totalAmount", totalAmount);
        return "asset/view";
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("assetForm") AssetForm assetForm) {
        return "asset/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("assetForm") AssetForm assetForm, BindingResult bindingResult,
                      HttpSession session) {

        log.info("자산 추가 : {}", assetForm);

        if (bindingResult.hasErrors()) {
            return "asset/add";
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        boolean existsByAssetName = assetService.existsByAssetName(user.getUserId(), assetForm.getAssetName(), null);
        if(existsByAssetName) {
            bindingResult.rejectValue("assetName", "asset.exists");
            return "asset/add";
        }

        assetService.save(user.getUserId(), assetForm);

        return "redirect:/asset";
    }

    @GetMapping("/{assetId}")
    public String editForm(@PathVariable Long assetId,
                           HttpSession session, Model model) {

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Asset asset = assetService.check(assetId, user.getUserId());
        if(asset == null) {
            return "error";
        }

        model.addAttribute("assetForm", new AssetForm(asset));
        return "asset/edit";
    }

    @PostMapping("/{assetId}")
    public String edit(@PathVariable Long assetId, @Valid @ModelAttribute("assetForm") AssetForm assetForm, BindingResult bindingResult,
                       HttpSession session) {

        log.info("자산 수정 : {}", assetForm);

        if (bindingResult.hasErrors()) {
            return "asset/edit";
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Asset asset = assetService.check(assetId, user.getUserId());
        if(asset == null) {
            return "error";
        }

        boolean existsByAssetName = assetService.existsByAssetName(user.getUserId(), assetForm.getAssetName(), assetId);
        if(existsByAssetName) {
            bindingResult.rejectValue("assetName", "asset.exists");
            return "asset/edit";
        }

        assetService.update(assetId, assetForm);
        return "redirect:/asset";
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> delete(@PathVariable Long assetId, HttpSession session) {

        log.info("자산 삭제 : {}", assetId);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        Asset asset = assetService.check(assetId, user.getUserId());
        if(asset == null) {
            return ResponseEntity.notFound().build();
        }

        assetService.delete(assetId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 자산 변동 내역
     */
    @GetMapping("/change")
    public String changeView(@ModelAttribute("req") AssetChangeReq req, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        log.info("자산 변동 내역 : {}", user);

        if(StringUtils.isBlank(req.getSearchDate())) {
            req.setSearchDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        List<AssetChange> assetChanges = assetService.findChangeByReqAndUserId(req, user.getUserId());
        Map<LocalDate, List<AssetChange>> groupedByChangeDate = assetChanges.stream()
                .sorted(Comparator.comparing(AssetChange::getCreatedAt).reversed())
                .collect(Collectors.groupingBy(AssetChange::getChangeDate));
        model.addAttribute("groupedByChangeDate", groupedByChangeDate);

        List<Asset> assets = assetService.findByUserId(user.getUserId());
        model.addAttribute("assets", assets);

        model.addAttribute("calcTypeValues", CalcType.values());
        return "asset/change/view";
    }

    @GetMapping("/change/add")
    public String changeAddForm(@ModelAttribute("assetChangeForm") AssetChangeForm assetChangeForm, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        List<Asset> assets = assetService.findByUserId(user.getUserId());
        model.addAttribute("assets", assets);

        model.addAttribute("calcTypeValues", CalcType.values());
        return "asset/change/add";
    }

    @PostMapping("/change/add")
    public String addChange(@Valid @ModelAttribute("assetChangeForm") AssetChangeForm assetChangeForm, BindingResult bindingResult,
                            Model model, HttpSession session) {

        log.info("자산 변동 추가 : {}", assetChangeForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        if (bindingResult.hasErrors()) {
            List<Asset> assets = assetService.findByUserId(user.getUserId());
            model.addAttribute("assets", assets);

            model.addAttribute("calcTypeValues", CalcType.values());
            return "asset/change/add";
        }

        Asset asset = assetService.check(assetChangeForm.getAssetId(), user.getUserId());
        if(asset == null) {
            return "error";
        }
        assetService.change(assetChangeForm, asset);

        return "redirect:/asset/change";
    }

    @GetMapping("/change/{changeId}")
    public String changeEditForm(@PathVariable Long changeId, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        AssetChange assetChange = assetService.findChangeById(changeId);
        if(assetChange == null) {
            return "error";
        }

        Asset asset = assetService.check(assetChange.getAsset().getAssetId(), user.getUserId());
        if(asset == null) {
            return "error";
        }

        model.addAttribute("asset", asset);
        model.addAttribute("calcTypeValues", CalcType.values());
        model.addAttribute("assetChangeForm", new AssetChangeForm(assetChange));
        return "asset/change/edit";
    }

    @PostMapping("/change/{changeId}")
    public String editChange(@PathVariable Long changeId,
                             @Valid @ModelAttribute("assetChangeForm") AssetChangeForm assetChangeForm, BindingResult bindingResult,
                             Model model, HttpSession session) {

        log.info("자산 변동 수정 : {}", assetChangeForm);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        AssetChange assetChange = assetService.findChangeById(changeId);
        if(assetChange == null) {
            return "error";
        }
        if(!Objects.equals(assetChange.getAsset().getAssetId(), assetChangeForm.getAssetId())) {
            log.error("자산 ID 불일치 : asset - {}, req.assetId - {}", assetChange.getAsset(), assetChangeForm.getAssetId());
            return "error";
        }

        Asset asset = assetService.check(assetChangeForm.getAssetId(), user.getUserId());
        if(asset == null) {
            return "error";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("asset", asset);
            model.addAttribute("calcTypeValues", CalcType.values());
            return "asset/change/edit";
        }

        assetService.updateChange(changeId, assetChangeForm, asset);
        return "redirect:/asset/change";
    }

    @DeleteMapping("/change/{changeId}")
    public ResponseEntity<Void> deleteChange(@PathVariable Long changeId, HttpSession session) {

        log.info("자산 변동 삭제 : {}", changeId);

        User user = (User) session.getAttribute(SessionConst.LOGIN_USER);

        AssetChange assetChange = assetService.findChangeById(changeId);
        if(assetChange == null) {
            return ResponseEntity.notFound().build();
        }

        Asset asset = assetService.check(assetChange.getAsset().getAssetId(), user.getUserId());
        if(asset == null) {
            return ResponseEntity.badRequest().build();
        }

        assetService.deleteChange(changeId, asset);
        return ResponseEntity.noContent().build();
    }
}
