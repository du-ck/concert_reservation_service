package com.hh.consertreservation.infra.entity;

import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.dto.types.SeatType;
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
@Table(name = "seat")
public class SeatEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false, insertable = false, updatable = false)
    private Long scheduleId;

    @Column(name = "seat_number", nullable = false)
    private Long seatNumber;

    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatType status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity scheduleEntity;

    public static Seat toDomain(SeatEntity entity) {
        return Seat.builder()
                .id(entity.id)
                .scheduleId(entity.getScheduleId())
                .seatNumber(entity.getSeatNumber())
                .status(entity.getStatus())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static List<Seat> toDomainList(List<SeatEntity> entities) {
        return entities.stream().map(m -> toDomain(m)).toList();
    }
}
