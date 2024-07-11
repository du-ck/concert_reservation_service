package com.hh.consertreservation.infra.entity;

import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_date", nullable = false)
    private String concertDate;

    @Column(length = 100, nullable = false)
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long seats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private ConcertEntity concert;

    public static ConcertSchedule toDomain(ScheduleEntity entity) {
        return ConcertSchedule.builder()
                .scheduleId(entity.getId())
                .concertId(entity.getConcert().getId())
                .title(entity.getConcert().getTitle())
                .concertDate(entity.getConcertDate())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .seats(entity.getSeats())
                .build();
    }

    public static List<ConcertSchedule> toDomainList(List<ScheduleEntity> entities) {
        return entities.stream().map(m -> toDomain(m)).toList();
    }
}
