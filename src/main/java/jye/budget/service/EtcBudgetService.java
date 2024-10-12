package jye.budget.service;

import jakarta.validation.Valid;
import jye.budget.entity.Asset;
import jye.budget.entity.AssetChange;
import jye.budget.entity.Category;
import jye.budget.entity.EtcBudget;
import jye.budget.form.AssetChangeForm;
import jye.budget.form.EtcBudgetForm;
import jye.budget.mapper.EtcBudgetMapper;
import jye.budget.req.EtcBudgetReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtcBudgetService {

    private final EtcBudgetMapper etcBudgetMapper;
    private final AssetService assetService;

    @Transactional(readOnly = true)
    public List<EtcBudget> findByReqAndUserId(EtcBudgetReq req, Long userId) {
        return etcBudgetMapper.findByReqAndUserId(req, userId);
    }

    @Transactional(readOnly = true)
    public List<Category> findCategoryBySearchDateAndUserId(String searchDate, Long userId) {
        return etcBudgetMapper.findCategoryBySearchDateAndUserId(searchDate, userId);
    }

    @Transactional
    public void save(Long userId, @Valid EtcBudgetForm etcBudgetForm, Category category) {
        AssetChange assetChange = new AssetChange();
        if(etcBudgetForm.getAssetId() != null) {
            Asset asset = assetService.check(etcBudgetForm.getAssetId(), userId);
            if(asset != null) {
                assetChange = assetService.change(new AssetChangeForm(etcBudgetForm), asset);
            }
        }
        EtcBudget etcBudget = EtcBudget.builder()
                .userId(userId)
                .category(category)
                .assetChange(assetChange)
                .calcType(etcBudgetForm.getCalcType())
                .amount(etcBudgetForm.getAmount())
                .etcBudgetDetail(etcBudgetForm.getEtcBudgetDetail())
                .etcBudgetDate(etcBudgetForm.getEtcBudgetDate())
                .build();
        log.info("save etcBudget : {}", etcBudget);
        etcBudgetMapper.save(etcBudget);
    }

    @Transactional(readOnly = true)
    public EtcBudget check(Long etcBudgetId, Long userId) {
        EtcBudget etcBudget = etcBudgetMapper.findById(etcBudgetId);
        if(etcBudget == null) {
            log.error("기타 예산 정보 없음 : {}", etcBudgetId);
            return null;
        }
        if(!etcBudget.getUserId().equals(userId)) {
            log.error("회원 ID 불일치 : etcBudget - {}, user - {}", etcBudget, userId);
            return null;
        }
        return etcBudget;
    }

    @Transactional
    public void delete(EtcBudget etcBudget) {
        if(etcBudget.getAssetChange() != null) {
            assetService.deleteChange(etcBudget.getAssetChange().getChangeId(), etcBudget.getAssetChange().getAsset());
        } else {
            log.info("delete etcBudget : {}", etcBudget.getEtcBudgetId());
            etcBudgetMapper.delete(etcBudget.getEtcBudgetId());
        }
    }

    @Transactional
    public void update(Long etcBudgetId, @Valid EtcBudgetForm etcBudgetForm, AssetChange assetChange, Asset asset, Category category) {
        EtcBudget etcBudget = EtcBudget.builder()
                .etcBudgetId(etcBudgetId)
                .category(category)
                .calcType(etcBudgetForm.getCalcType())
                .amount(etcBudgetForm.getAmount())
                .etcBudgetDetail(etcBudgetForm.getEtcBudgetDetail())
                .etcBudgetDate(etcBudgetForm.getEtcBudgetDate())
                .build();

        if(assetChange != null) {
            assetService.updateChange(assetChange.getChangeId(), new AssetChangeForm(etcBudgetForm), asset);
        } else {
            if(asset != null) {
                assetChange = assetService.change(new AssetChangeForm(etcBudgetForm), asset);
                etcBudget.setAssetChange(assetChange);
            }
        }

        log.info("update etcBudget : {}", etcBudgetForm);
        etcBudgetMapper.update(etcBudget);
    }
}
