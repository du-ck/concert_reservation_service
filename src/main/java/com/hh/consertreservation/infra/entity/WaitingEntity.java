package com.hh.consertreservation.infra.entity;

import com.hh.consertreservation.domain.dto.types.WaitingType;
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
@Table(name = "waiting")
public class WaitingEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //todo token 형식 정해야함
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
}
