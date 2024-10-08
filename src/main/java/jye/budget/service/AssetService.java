package jye.budget.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jye.budget.entity.Asset;
import jye.budget.entity.AssetChange;
import jye.budget.form.AssetChangeForm;
import jye.budget.form.AssetForm;
import jye.budget.mapper.AssetMapper;
import jye.budget.req.AssetChangeReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetMapper assetMapper;

    @Transactional(readOnly = true)
    public List<Asset> findByUserId(Long userId) {
        return assetMapper.findByUserId(userId);
    }

    @Transactional
    public void save(Long userId, @Valid AssetForm assetForm) {
        Asset asset = Asset.builder()
                .userId(userId)
                .assetName(assetForm.getAssetName())
                .initialAmount(assetForm.getInitialAmount())
                .currentAmount(assetForm.getInitialAmount())
                .isAllocated(assetForm.isAllocated())
                .build();
        log.info("save asset : {}", asset);
        assetMapper.save(asset);
    }

    @Transactional(readOnly = true)
    public boolean existsByAssetName(Long userId, @NotBlank String assetName, Long assetId) {
        boolean existsByAssetName = assetMapper.existsByAssetName(userId, assetName, assetId);
        if (existsByAssetName) log.error("이미 존재하는 자산명 : {}", assetName);
        return existsByAssetName;
    }

    @Transactional(readOnly = true)
    public Asset findById(Long assetId) {
        return assetMapper.findById(assetId);
    }

    @Transactional
    public void update(Long assetId, AssetForm assetForm) {
        Asset asset = Asset.builder()
                .assetId(assetId)
                .assetName(assetForm.getAssetName())
                .initialAmount(assetForm.getInitialAmount())
                .currentAmount(assetForm.getInitialAmount())
                .isAllocated(assetForm.isAllocated())
                .build();
        log.info("update asset : {}", asset);
        assetMapper.update(asset);
    }

    @Transactional
    public void delete(Long assetId) {
        assetMapper.delete(assetId);
    }

    @Transactional(readOnly = true)
    public List<AssetChange> findChangeByReqAndUserId(AssetChangeReq req, Long userId) {
        return assetMapper.findChangeByReqAndUserId(req, userId);
    }

    @Transactional
    public void change(@Valid AssetChangeForm assetChangeForm,
                       Asset asset) {

        AssetChange assetChange = AssetChange.builder()
                .asset(asset)
                .calcType(assetChangeForm.getCalcType())
                .amount(assetChangeForm.getAmount())
                .changeDetail(assetChangeForm.getChangeDetail())
                .changeDate(assetChangeForm.getChangeDate())
                .build();
        log.info("save assetChange : {}", assetChange);
        assetMapper.change(assetChange);

        int currentAmount = assetChangeForm.getCalcType().apply(asset.getCurrentAmount(), assetChange.getAmount());
        log.info("update asset {} currentAmount : {}", asset, currentAmount);
        assetMapper.updateCurrentAmount(asset.getAssetId(), currentAmount);
    }

    @Transactional(readOnly = true)
    public AssetChange findChangeById(Long changeId) {
        AssetChange assetChange = assetMapper.findChangeById(changeId);
        if(assetChange == null) log.error("자산 변동 정보 없음 : {}", changeId);
        return assetChange;
    }

    @Transactional
    public void updateChange(Long changeId, @Valid AssetChangeForm assetChangeForm,
                             Asset asset) {

        AssetChange assetChange = AssetChange.builder()
                .changeId(changeId)
                .asset(asset)
                .calcType(assetChangeForm.getCalcType())
                .amount(assetChangeForm.getAmount())
                .changeDetail(assetChangeForm.getChangeDetail())
                .changeDate(assetChangeForm.getChangeDate())
                .build();
        log.info("update assetChange : {}", assetChange);
        assetMapper.updateChange(assetChange);

        updateCurrentAmount(asset);
    }

    /**
     * 현재 금액 업데이트
     * @param asset 자산
     */
    private void updateCurrentAmount(Asset asset) {
        log.info("update currentAmount : {}", asset);

        AtomicInteger currentAmount = new AtomicInteger(asset.getInitialAmount());
        log.info("initialAmount : {}", currentAmount.get());

        List<AssetChange> assetChanges = assetMapper.findChangeByAssetId(asset.getAssetId());
        assetChanges.forEach(change -> currentAmount.set(change.getCalcType().apply(currentAmount.get(), change.getAmount())));

        log.info("currentAmount : {}", currentAmount.get());
        assetMapper.updateCurrentAmount(asset.getAssetId(), currentAmount.get());
    }

    @Transactional
    public void deleteChange(Long changeId, Asset asset) {
        assetMapper.deleteChange(changeId);
        updateCurrentAmount(asset);
    }
}
