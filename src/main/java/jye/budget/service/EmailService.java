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

    // 인증번호를 생성하여 이메일 발송
    public void sendVerificationCode(String to, String verificationCode) {
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);

        // 템플릿을 기반으로 이메일 콘텐츠 생성
        String process = templateEngine.process("email/verification", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setFrom(from); // 구글은 from 설정하지 않아도 properties username 이 기본값으로 설정되지만, 네이버는 직접 설정해줘야 함
            helper.setSubject("이메일 인증");
            helper.setText(process, true); // HTML 본문 설정
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public boolean verifyCode(String email, String inputCode) {
        // Redis에서 인증번호 가져오기
        String storedCode = redisTemplate.opsForValue().get(email);

        // 인증번호가 일치하는지 확인
        if (storedCode != null && storedCode.equals(inputCode)) {
            // 인증번호가 일치할 경우 사용자 인증 처리
            userMapper.updateVerified(email); // verified 컬럼 업데이트 메서드 호출
            return true; // 인증 성공
        }

        return false; // 인증 실패
    }
}
