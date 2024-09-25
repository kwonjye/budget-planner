package jye.budget.login.service;

import jakarta.validation.constraints.NotBlank;
import jye.budget.user.entity.User;
import jye.budget.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserMapper userMapper;

    public User login(@NotBlank String email) {
        return userMapper.findByEmail(email);
    }
}
