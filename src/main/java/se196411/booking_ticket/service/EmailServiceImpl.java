package se196411.booking_ticket.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.BookingEntity;
import se196411.booking_ticket.model.entity.TicketEntity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.name}")
    private String fromName;

    @Override
    public void sendBookingConfirmationEmail(BookingEntity booking, String recipientEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(recipientEmail);
            helper.setSubject("Xác nhận đặt vé thành công - Mã booking: " + booking.getBookingId());

            String emailContent = buildEmailContent(booking);
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to: " + recipientEmail);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error while sending email");
            e.printStackTrace();
        }
    }

    private String buildEmailContent(BookingEntity booking) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>");
        content.append("<html lang='vi'>");
        content.append("<head>");
        content.append("<meta charset='UTF-8'>");
        content.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        content.append("<style>");
        content.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }");
        content.append(".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 0 20px rgba(0,0,0,0.1); }");
        content.append(".header { background: linear-gradient(135deg, #00693E 0%, #008C4F 100%); color: white; padding: 30px 20px; text-align: center; }");
        content.append(".header h1 { margin: 0; font-size: 28px; font-weight: 600; }");
        content.append(".header p { margin: 10px 0 0; font-size: 14px; opacity: 0.9; }");
        content.append(".content { padding: 30px 20px; }");
        content.append(".booking-info { background-color: #E8F5F1; border-left: 4px solid #00693E; padding: 20px; margin-bottom: 25px; border-radius: 4px; }");
        content.append(".booking-info h2 { color: #00693E; margin: 0 0 15px; font-size: 20px; }");
        content.append(".info-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #ddd; }");
        content.append(".info-row:last-child { border-bottom: none; }");
        content.append(".info-label { font-weight: 600; color: #555; }");
        content.append(".info-value { color: #333; text-align: right; }");
        content.append(".ticket-section { margin-top: 30px; }");
        content.append(".ticket-section h3 { color: #00693E; margin-bottom: 15px; font-size: 18px; }");
        content.append(".ticket-card { background-color: #f9f9f9; border: 1px solid #e0e0e0; border-radius: 8px; padding: 15px; margin-bottom: 15px; }");
        content.append(".ticket-header { font-weight: 600; color: #00693E; margin-bottom: 10px; font-size: 16px; }");
        content.append(".ticket-info { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; font-size: 14px; }");
        content.append(".total-section { background-color: #00693E; color: white; padding: 20px; margin-top: 25px; border-radius: 8px; text-align: center; }");
        content.append(".total-section h3 { margin: 0 0 10px; font-size: 18px; }");
        content.append(".total-amount { font-size: 32px; font-weight: bold; margin: 0; }");
        content.append(".footer { background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #666; }");
        content.append(".footer p { margin: 5px 0; }");
        content.append(".note { background-color: #FFF9E6; border-left: 4px solid #FFC107; padding: 15px; margin-top: 20px; border-radius: 4px; }");
        content.append(".note p { margin: 0; font-size: 14px; color: #666; }");
        content.append("</style>");
        content.append("</head>");
        content.append("<body>");
        content.append("<div class='container'>");

        // Header
        content.append("<div class='header'>");
        content.append("<h1>✈️ Bamboo Airways</h1>");
        content.append("<p>Đặt vé thành công!</p>");
        content.append("</div>");

        // Content
        content.append("<div class='content'>");

        // Booking Information
        content.append("<div class='booking-info'>");
        content.append("<h2>📋 Thông Tin Đặt Chỗ</h2>");
        content.append("<div class='info-row'>");
        content.append("<span class='info-label'>Mã Booking:</span>");
        content.append("<span class='info-value'><strong>").append(booking.getBookingId()).append("</strong></span>");
        content.append("</div>");
        content.append("<div class='info-row'>");
        content.append("<span class='info-label'>Thời gian đặt:</span>");
        content.append("<span class='info-value'>").append(dateFormat.format(java.sql.Timestamp.valueOf(booking.getBookingTime()))).append("</span>");
        content.append("</div>");
        content.append("<div class='info-row'>");
        content.append("<span class='info-label'>Trạng thái:</span>");
        content.append("<span class='info-value' style='color: #00693E; font-weight: bold;'>").append(booking.getStatus()).append("</span>");
        content.append("</div>");
        content.append("</div>");

        // Tickets Information
        if (booking.getTickets() != null && !booking.getTickets().isEmpty()) {
            content.append("<div class='ticket-section'>");
            content.append("<h3>🎫 Thông Tin Vé</h3>");

            int ticketNumber = 1;
            for (TicketEntity ticket : booking.getTickets()) {
                content.append("<div class='ticket-card'>");
                content.append("<div class='ticket-header'>Vé #").append(ticketNumber++).append(" - Mã vé: ").append(ticket.getTicketId()).append("</div>");
                content.append("<div class='ticket-info'>");
                content.append("<div><strong>Hành khách:</strong> ").append(ticket.getTitle()).append(" ").append(ticket.getFirstName()).append(" ").append(ticket.getLastName()).append("</div>");
                content.append("<div><strong>Giới tính:</strong> ").append(ticket.getGender() ? "Nam" : "Nữ").append("</div>");
                content.append("<div><strong>CCCD:</strong> ").append(ticket.getCccd()).append("</div>");
                content.append("<div><strong>SĐT:</strong> ").append(ticket.getSdt()).append("</div>");
                content.append("<div><strong>Quốc tịch:</strong> ").append(ticket.getNationality()).append("</div>");

                if (ticket.getFlight() != null) {
                    content.append("<div><strong>Chuyến bay:</strong> ").append(ticket.getFlight().getFlightId()).append("</div>");
                    if (ticket.getFlight().getFlightRoute() != null) {
                        if (ticket.getFlight().getFlightRoute().getStartedAirport() != null && ticket.getFlight().getFlightRoute().getEndedAirport() != null) {
                            content.append("<div><strong>Tuyến bay:</strong> ").append(ticket.getFlight().getFlightRoute().getStartedAirport().getCode())
                                    .append(" → ").append(ticket.getFlight().getFlightRoute().getEndedAirport().getCode()).append("</div>");
                        }
                    }
                    if (ticket.getFlight().getStartedTime() != null) {
                        content.append("<div><strong>Khởi hành:</strong> ").append(dateFormat.format(java.sql.Timestamp.valueOf(ticket.getFlight().getStartedTime()))).append("</div>");
                    }
                    if (ticket.getFlight().getEndedTime() != null) {
                        content.append("<div><strong>Đến:</strong> ").append(dateFormat.format(java.sql.Timestamp.valueOf(ticket.getFlight().getEndedTime()))).append("</div>");
                    }
                }

                if (ticket.getSeat() != null) {
                    content.append("<div><strong>Ghế:</strong> ").append(ticket.getSeat().getSeatNumber()).append("</div>");
                    content.append("<div><strong>Hạng:</strong> ").append(ticket.getSeat().getSeatClass()).append("</div>");
                }

                if (ticket.getMeal() != null) {
                    content.append("<div><strong>Suất ăn:</strong> ").append(ticket.getMeal().getName()).append("</div>");
                }

                if (ticket.getLuggage() != null) {
                    content.append("<div><strong>Hành lý:</strong> ").append(ticket.getLuggage().getLuggageAllowance()).append(" kg</div>");
                }

                content.append("<div><strong>Giá vé:</strong> <span style='color: #00693E; font-weight: bold;'>").append(currencyFormat.format(ticket.getPrice())).append("</span></div>");
                content.append("</div>");
                content.append("</div>");
            }

            content.append("</div>");
        }

        // Total Amount
        content.append("<div class='total-section'>");
        content.append("<h3>Tổng Thanh Toán</h3>");
        content.append("<p class='total-amount'>").append(currencyFormat.format(booking.getTotalAmount())).append("</p>");
        content.append("</div>");

        // Note
        content.append("<div class='note'>");
        content.append("<p><strong>📌 Lưu ý:</strong></p>");
        content.append("<p>• Vui lòng mang theo giấy tờ tùy thân khi làm thủ tục check-in</p>");
        content.append("<p>• Đến sân bay trước giờ bay ít nhất 2 giờ</p>");
        content.append("<p>• Giữ lại email này để xuất trình khi cần thiết</p>");
        content.append("</div>");

        content.append("</div>");

        // Footer
        content.append("<div class='footer'>");
        content.append("<p><strong>Bamboo Airways - Bay cùng niềm tin</strong></p>");
        content.append("<p>Hotline: 1900 1166 | Email: support@bambooairways.com</p>");
        content.append("<p>© 2025 Bamboo Airways. All rights reserved.</p>");
        content.append("</div>");

        content.append("</div>");
        content.append("</body>");
        content.append("</html>");

        return content.toString();
    }
}

