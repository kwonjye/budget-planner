package jye.budget.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jye.budget.entity.Asset;
import jye.budget.form.AssetForm;
import jye.budget.mapper.AssetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return assetMapper.existsByAssetName(userId, assetName, assetId);
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
}
