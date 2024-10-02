package jye.budget.mapper;

import jakarta.validation.constraints.NotBlank;
import jye.budget.entity.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssetMapper {

    List<Asset> findByUserId(@Param("userId") Long userId);

    void save(Asset asset);

    boolean existsByAssetName(@Param("userId") Long userId,
                              @Param("assetName") @NotBlank String assetName,
                              @Param("assetId") Long assetId);

    Asset findById(@Param("assetId") Long assetId);

    void update(Asset asset);
}
