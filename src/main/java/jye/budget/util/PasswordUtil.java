package jye.budget.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 비밀번호 암호화
    public static String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // 비밀번호 검증
    public static boolean verifyPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }
}
