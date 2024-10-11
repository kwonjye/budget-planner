package jye.budget.service;

import jakarta.validation.Valid;
import jye.budget.entity.Asset;
import jye.budget.entity.AssetChange;
import jye.budget.entity.Category;
import jye.budget.entity.EtcBudget;
import jye.budget.form.AssetChangeForm;
import jye.budget.form.EtcBudgetForm;
import jye.budget.mapper.CategoryMapper;
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
    private final CategoryMapper categoryMapper;
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
    public void save(Long userId, @Valid EtcBudgetForm etcBudgetForm) {
        AssetChange assetChange = new AssetChange();
        if(etcBudgetForm.getAssetId() != null) {
            Asset asset = assetService.checkAsset(etcBudgetForm.getAssetId(), userId);
            if(asset != null) {
                assetChange = assetService.change(new AssetChangeForm(etcBudgetForm), asset);
            }
        }
        Category category = categoryMapper.findById(etcBudgetForm.getCategoryId());
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
}
