package swp.project.swp391.service.auth;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
