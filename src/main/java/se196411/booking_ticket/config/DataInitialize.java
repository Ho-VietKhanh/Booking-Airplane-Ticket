package se196411.booking_ticket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import se196411.booking_ticket.model.*;
import se196411.booking_ticket.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitialize implements CommandLineRunner {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {

        RoleEntity roleEntity = this.roleService.findAllRoles().stream().findFirst().orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setRoleId("role-user");
            role.setRoleName("USER");
            role.setDescription("Default role for users");
            return this.roleService.createRole(role);
        });

        UserEntity user = this.userService.findAll().stream().findFirst().orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setUserId("user-test-1");
            newUser.setFullName("Test User");
            newUser.setEmail("user@gmail.com");
            newUser.setPassword("password");
            newUser.setPhone("0123456789");
            newUser.setRole(roleEntity);
            newUser.setCreateAt(LocalDateTime.now());
            return this.userService.createUser(newUser);
        });

        if (this.paymentMethodService.getAllPaymentMethods().isEmpty()) {
            PaymentMethodEntity momo = new PaymentMethodEntity();
            momo.setPaymentMethodId("pm-momo");
            momo.setPaymentMethodName("MOMO");
            momo.setDescription("Momo e-wallet");
            this.paymentMethodService.createPaymentMethod(momo);

            PaymentMethodEntity vnpay = new PaymentMethodEntity();
            vnpay.setPaymentMethodId("pm-vnpay");
            vnpay.setPaymentMethodName("VNPAY");
            vnpay.setDescription("VNPAY / QR");
            this.paymentMethodService.createPaymentMethod(vnpay);

            PaymentMethodEntity card = new PaymentMethodEntity();
            card.setPaymentMethodId("pm-card");
            card.setPaymentMethodName("Credit Card");
            card.setDescription("Visa / Mastercard");
            this.paymentMethodService.createPaymentMethod(card);
        }

        // Create a sample payment + booking if not present
        Optional<PaymentEntity> existing = this.paymentService.getPaymentById("pay-0001");
        if (existing.isEmpty()) {
            PaymentEntity p = new PaymentEntity();
            p.setPaymentId("pay-0001");
            p.setCreatedAt(LocalDateTime.now());
            p.setPaidAt(null);
            p.setStatus("PENDING");
            p.setAmount(new BigDecimal("8008000.00"));
            this.paymentMethodService.getPaymentMethodById("pm-momo").ifPresent(p::setPaymentMethod);
            this.paymentService.createPayment(p);
        }

        // Booking: ensure not duplicated. NOTE: BookingEntity has a non-null user reference in your model.
        // This loader will create a booking that references the payment id but will set user to null.
        // If your DB enforces user_id NOT NULL, change 'userId' below to an existing user id and set booking.setUser(...)
        Optional<BookingEntity> bOpt = this.bookingService.findById("bk-0001");
        if (bOpt.isEmpty()) {
            BookingEntity b = new BookingEntity();
            b.setBookingId("bk-0001");
            b.setBookingTime(LocalDateTime.now());
            b.setTotalAmount(new BigDecimal("8008000.00"));
            b.setStatus("PENDING_PAYMENT");
            // set payment relation
            PaymentEntity paymentLink = this.paymentService.getPaymentById("pay-0001").orElse(null);
            b.setPayment(paymentLink);
            b.setUser(user);
            this.bookingService.create(b);
        }
    }
}
