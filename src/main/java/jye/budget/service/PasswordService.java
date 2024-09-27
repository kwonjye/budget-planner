package jye.budget.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jye.budget.mapper.UserMapper;
import jye.budget.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final EmailService emailService;
    private final UserMapper userMapper;

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    private static final int TEMP_PASSWORD_LENGTH = 12; // 임시 비밀번호의 길이

    /**
     * 임시 비밀번호 발급
     * @param email 이메일
     */
    @Transactional
    public void issueTemp(@NotBlank @Email String email) {
        log.info("issue temp password : {}", email);

        String tempPassword = generateTempPassword();
        log.info("temp password : {}", tempPassword);

        // 임시 비밀번호로 회원 비밀번호 변경
        userMapper.updatePassword(email, PasswordUtil.hashPassword(tempPassword));

        // 임시 비밀번호 이메일 발송
        emailService.sendTempPassword(email, tempPassword);
    }

    /**
     * 임시 비밀번호 생성
     * @return 임시 비밀번호
     */
    public String generateTempPassword() {
        StringBuilder tempPassword = new StringBuilder(TEMP_PASSWORD_LENGTH);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            tempPassword.append(CHARACTERS.charAt(index));
        }

        return tempPassword.toString();
    }
}
