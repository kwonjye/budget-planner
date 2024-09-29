package jye.budget.mapper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jye.budget.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByEmail(@NotBlank @Param("email") String email);

    void save(User user);

    void updateVerified(@NotBlank @Param("email") String email);

    void updatePassword(@NotBlank @Param("email") String email,
                        @Param("password") String password);

    void delete(@NotNull @Param("userId") Long userId);
}
