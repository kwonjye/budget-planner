package jye.budget.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jye.budget.entity.Asset;
import jye.budget.form.AddAssetForm;
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
    public void save(Long userId, @Valid AddAssetForm addAssetForm) {
        Asset asset = Asset.builder()
                .userId(userId)
                .assetName(addAssetForm.getAssetName())
                .initialAmount(addAssetForm.getInitialAmount())
                .currentAmount(addAssetForm.getInitialAmount())
                .isAllocated(addAssetForm.isAllocated())
                .build();
        log.info("save asset : {}", asset);
        assetMapper.save(asset);
    }

    @Transactional(readOnly = true)
    public boolean existsByAssetName(Long userId, @NotBlank String assetName) {
        return assetMapper.existsByAssetName(userId, assetName);
    }
}
