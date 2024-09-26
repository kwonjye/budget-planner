package jye.budget.mapper;

import jakarta.validation.constraints.NotBlank;
import jye.budget.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByEmail(@NotBlank String email);

    void save(User user);

    void updateVerified(@Param("email") String email);
}
