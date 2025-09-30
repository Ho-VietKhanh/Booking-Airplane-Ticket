package swp.project.swp391.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promotionID;

    @NotBlank(message = "Title is required")
    @Size(max = 100)
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    @NotNull(message = "End time is required")
    @Column(nullable = false)
    private LocalDateTime endTime;

    @NotNull(message = "Value is required")
    @PositiveOrZero(message = "Value must be zero or positive")
    @Column(nullable = false)
    private Double value;

    @Size(max = 255)
    @Column(length = 255)
    private String image;

    @Size(max = 50)
    @Column(length = 50)
    private String promotionType;

    private Double maxDiscountAmount; // optional, nullable


    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String condition; // Có thể lưu JSON hoặc mô tả điều kiện áp dụng

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String detail; // Mô tả chi tiết khuyến mãi

    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, DELETED

    // ✅ New fields
    @NotNull(message = "isExclusive is required")
    @Column(nullable = false)
    private Boolean isExclusive = false; // true: chỉ áp dụng 1 promotion duy nhất

    @PositiveOrZero(message = "Tổng lượt sử dụng phải là số không hoặc dương")
    private Integer maxTotalUsage; // null = không giới hạn

    @PositiveOrZero(message = "Số lần sử dụng mỗi khách phải là số không hoặc dương")
    private Integer maxUsagePerCustomer; // null = không giới hạn

    @Column(nullable = true)
    private Integer usedCount = 0;

    @ManyToOne
    @JoinColumn(name = "group_id") // FK column in Promotions table
    private PromotionGroup group;

}
