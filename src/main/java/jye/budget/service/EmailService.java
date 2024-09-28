package jye.budget.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jye.budget.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserMapper userMapper;

    /**
     * 이메일 인증
     * @param email 이메일
     */
    public void verifyEmail(String email) {
        log.info("email verify : {}", email);

        // 인증 코드 생성
        String verificationCode = generateVerificationCode();

        // Redis에 인증 코드 저장 (유효시간 설정 가능)
        redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES);

        // 인증 코드가 담긴 이메일 발송
        sendVerificationCode(email, verificationCode);
    }

    /**
     * 인증 코드 생성
     * @return 인증 코드
     */
    private String generateVerificationCode() {
        // 간단한 랜덤 숫자 생성 로직 (6자리 숫자)
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    /**
     * 인증 메일 발송
     * @param to 받는 사람
     * @param verificationCode 인증 코드
     */
    public void sendVerificationCode(String to, String verificationCode) {
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);

        // 템플릿을 기반으로 이메일 콘텐츠 생성
        String process = templateEngine.process("email/template/verification-code", context);
        sendMail(to, "[나만의 가계부] 이메일 인증", process);
    }

    /**
     * 인증 코드 검증
     * @param email 이메일
     * @param inputCode 입력 인증 코드
     * @return 검증 결과
     */
    @Transactional
    public boolean verifyCode(String email, String inputCode) {
        // Redis에서 인증 코드 가져오기
        String storedCode = redisTemplate.opsForValue().get(email);

        // 인증 코드가 일치하는지 확인
        if (storedCode != null && storedCode.equals(inputCode)) {
            // 인증 코드가 일치할 경우 사용자 인증 처리
            userMapper.updateVerified(email);
            return true;
        }
        return false;
    }

    /**
     * 임시 비밀번호 발급
     * @param to 받는 사람
     * @param tempPassword 임시 비밀번호
     */
    public void sendTempPassword(String to, String tempPassword) {
        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);

        // 템플릿을 기반으로 이메일 콘텐츠 생성
        String process = templateEngine.process("email/template/temp-password", context);
        sendMail(to, "[나만의 가계부] 임시 비밀번호 발급", process);
    }

    /**
     * 이메일 발송
     * @param to 받는 사람
     * @param subject 제목
     * @param process 본문
     */
    private void sendMail(String to, String subject, String process) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setFrom(from); // 구글은 from 설정하지 않아도 properties username 이 기본값으로 설정되지만, 네이버는 직접 설정해줘야 함
            helper.setSubject(subject);
            helper.setText(process, true); // HTML 본문 설정
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
