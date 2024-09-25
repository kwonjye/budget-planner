package jye.budget.user.mapper;

import jakarta.validation.constraints.NotBlank;
import jye.budget.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByEmail(@NotBlank String email);
}
