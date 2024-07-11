package com.hh.consertreservation.infra.entity;

import com.hh.consertreservation.domain.dto.Token;
import com.hh.consertreservation.domain.dto.types.WaitingType;
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
@Table(name = "waiting")
public class WaitingEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String token;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private WaitingType status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public static Token toDomain(WaitingEntity entity) {
        return Token.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .queueToken(entity.getToken())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }

    public static List<Token> toDomainList(List<WaitingEntity> entityList) {
        return entityList.stream().map(m -> WaitingEntity.toDomain(m)).toList();
    }

    public static List<WaitingEntity> toEntityList(List<Token> domainList) {
        return domainList.stream().map(m -> WaitingEntity.toEntity(m)).toList();
    }

    public static WaitingEntity toEntity(Token domain) {
        return WaitingEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .token(domain.getQueueToken())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .expiresAt(domain.getExpiresAt())
                .build();
    }
}
