package jye.budget.user.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jye.budget.user.entity.User;
import jye.budget.user.form.UserForm;
import jye.budget.user.mapper.UserMapper;
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
    public void save(@Valid UserForm userForm) {
        log.info("save user: {}", userForm);

        User user = User.builder()
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .build();
        userMapper.save(user);
    }
}
