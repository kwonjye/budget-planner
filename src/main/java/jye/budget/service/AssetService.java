package jye.budget.service;

import jye.budget.entity.Asset;
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
}
