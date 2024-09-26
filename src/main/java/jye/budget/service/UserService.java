package jye.budget.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jye.budget.mapper.UserMapper;
import jye.budget.entity.User;
import jye.budget.form.UserForm;
import jye.budget.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public User findByEmail(@NotBlank String email) {
        return userMapper.findByEmail(email);
    }

    @Transactional
    public void save(@Valid UserForm userForm) {
        log.info("save user: {}", userForm);

        User user = User.builder()
                .email(userForm.getEmail())
                .password(PasswordUtil.hashPassword(userForm.getPassword()))
                .build();
        userMapper.save(user);

        log.info("email verify : {}", userForm.getEmail());

        // 인증번호 생성
        String verificationCode = generateVerificationCode();

        // Redis에 인증번호 저장 (유효시간 설정 가능)
        redisTemplate.opsForValue().set(userForm.getEmail(), verificationCode, 5, TimeUnit.MINUTES);

        // 인증번호가 담긴 이메일 발송
        emailService.sendVerificationCode(userForm.getEmail(), verificationCode);
    }

    /**
     * 인증번호 생성
     * @return 인증번호
     */
    private String generateVerificationCode() {
        // 간단한 랜덤 숫자 생성 로직 (6자리 숫자)
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
