package jye.budget.mapper;

import jakarta.validation.constraints.NotBlank;
import jye.budget.entity.Asset;
import jye.budget.entity.AssetChange;
import jye.budget.req.AssetChangeReq;
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

    void delete(@Param("assetId") Long assetId);

    List<AssetChange> findChangeByReqAndUserId(@Param("req") AssetChangeReq req,
                                               @Param("userId") Long userId);

    void change(AssetChange assetChange);

    void updateCurrentAmount(@Param("assetId") Long assetId,
                             @Param("currentAmount") int currentAmount);

    AssetChange findChangeById(@Param("changeId") Long changeId);

    void updateChange(AssetChange assetChange);

    List<AssetChange> findChangeByAssetId(@Param("assetId") Long assetId);

    void deleteChange(@Param("changeId") Long changeId);

    List<Asset> findByUserIdAndAllocated(@Param("userId") Long userId);
}
