package jye.budget.mapper;

import jye.budget.entity.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssetMapper {

    List<Asset> findByUserId(@Param("userId") Long userId);
}
