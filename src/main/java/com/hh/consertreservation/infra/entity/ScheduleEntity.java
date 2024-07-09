package com.hh.consertreservation.infra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(name = "concert_date", nullable = false)
    private String concertDate;

    @Column(length = 100, nullable = false)
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long seats;
}
