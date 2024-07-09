package com.hh.consertreservation.infra.entity;

import com.hh.consertreservation.domain.dto.UserBalance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_balance")
public class UserBalanceEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Long balance;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static UserBalance toDomain(UserBalanceEntity entity) {
        return UserBalance.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .balance(entity.getBalance())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static UserBalanceEntity toEntity(UserBalance domain) {
        return UserBalanceEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .balance(domain.getBalance())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
