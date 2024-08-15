package com.hh.consertreservation.infra.cash;

import com.hh.consertreservation.domain.cash.*;
import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PAYMENT_OUTBOX")
public class PaymentOutBoxEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 3000)
    private String message;

    @Column(length = 9, nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentOutBoxType status;

    @Column(name = "process_status", length = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentOutBoxProcessType processStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static PaymentOutBoxEntity toEntity(PaymentOutBox domain) {
        return PaymentOutBoxEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .message(domain.getMessage())
                .status(domain.getStatus())
                .processStatus(domain.getProcessStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public static PaymentOutBox toDomain(PaymentOutBoxEntity entity) {
        return PaymentOutBox.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .processStatus(entity.getProcessStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static List<PaymentOutBox> toDomainList(List<PaymentOutBoxEntity> entities) {
        return entities.stream().map(m -> toDomain(m)).toList();
    }
}
