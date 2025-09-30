package swp.project.swp391.serviceImp.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import swp.project.swp391.service.auth.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom("your-email@example.com"); // Thay bằng email cấu hình trong application.properties
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true => cho phép HTML

            mailSender.send(mimeMessage);
            log.info("Email HTML đã được gửi thành công đến {}", to);

        } catch (MailSendException | MessagingException e) {
            log.error("Gửi email thất bại đến {}: {}", to, e.getMessage());
            throw new RuntimeException("Không thể gửi email. Vui lòng kiểm tra lại địa chỉ email.", e);
        }
    }
}
