package jye.budget.mapper;

import jakarta.validation.constraints.NotNull;
import jye.budget.entity.Category;
import jye.budget.type.CategoryType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findByUserIdAndType(@Param("userId") Long userId,
                                       @Param("categoryType") CategoryType categoryType);

    Category findById(@NotNull @Param("categoryId") Long categoryId);
}
