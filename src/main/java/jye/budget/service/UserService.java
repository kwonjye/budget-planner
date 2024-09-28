package jye.budget.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jye.budget.form.JoinUserForm;
import jye.budget.mapper.UserMapper;
import jye.budget.entity.User;
import jye.budget.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public User findByEmail(@NotBlank String email) {
        return userMapper.findByEmail(email);
    }

    @Transactional
    public void save(@Valid JoinUserForm joinUserForm) {
        log.info("save user: {}", joinUserForm);

        User user = User.builder()
                .email(joinUserForm.getEmail())
                .password(PasswordUtil.hashPassword(joinUserForm.getPassword()))
                .build();
        userMapper.save(user);
    }

    @Transactional
    public void delete(Long userId) {
        log.info("delete user: {}", userId);
        userMapper.delete(userId);
    }
}
