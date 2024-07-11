package com.hh.consertreservation.infra.entity;

import com.hh.consertreservation.domain.dto.ConcertSchedule;
import com.hh.consertreservation.domain.dto.ReservationInfo;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.dto.User;
import com.hh.consertreservation.domain.dto.types.ReservationType;
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
@Table(name = "reservation")
public class ReservationEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(name = "concert_title", length = 100, nullable = false)
    private String concertTitle;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Column(name = "seat_number", nullable = false)
    private Long seatNumber;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", length = 10, nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationType status;

    public static ReservationEntity toEntity(ReservationInfo domain) {
        return ReservationEntity.builder()
                .concertId(domain.getConcertSchedule().getConcertId())
                .concertTitle(domain.getConcertSchedule().getTitle())
                .seatId(domain.getSeat().getId())
                .seatNumber(domain.getSeat().getSeatNumber())
                .userId(domain.getUser().getId())
                .name(domain.getUser().getUserName())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getCreatedAt())
                .status(domain.getStatus())
                .build();
    }

    public static ReservationInfo toDomain(ReservationEntity entity) {
        return ReservationInfo.builder()
                .reservationId(entity.getId())
                .user(User.builder()
                        .id(entity.getUserId())
                        .userName(entity.getName())
                        .build())
                .concertSchedule(ConcertSchedule.builder()
                        .concertId(entity.getConcertId())
                        .title(entity.getConcertTitle())
                        .build())
                .seat(Seat.builder()
                        .id(entity.getSeatId())
                        .seatNumber(entity.getSeatNumber())
                        .build())
                .createdAt(entity.getCreatedAt())
                .status(entity.getStatus())
                .build();
    }
}
