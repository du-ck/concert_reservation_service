package com.hh.consertreservation.infra.entity;

import com.hh.consertreservation.domain.dto.types.SeatType;
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
@Table(name = "seat")
public class SeatEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(name = "seat_number", nullable = false)
    private Long seatNumber;

    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatType status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
