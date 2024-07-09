package com.hh.consertreservation.infra.entity;

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
@Table(name = "balance_history")
public class BalanceHistoryEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_balance_id", nullable = false)
    private Long userBalanceId;

    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    @Column(nullable = false)
    private Long used;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
