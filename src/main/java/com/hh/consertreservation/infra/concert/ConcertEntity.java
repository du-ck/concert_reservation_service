package com.hh.consertreservation.infra.concert;

import com.hh.consertreservation.domain.concert.ConcertTitle;
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
@Table(name = "concert")
public class ConcertEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    public static ConcertTitle toDomain(ConcertEntity entity) {
        return ConcertTitle.builder()
                .concertId(entity.getId())
                .title(entity.getTitle())
                .build();
    }

    public static List<ConcertTitle> toDomainList(List<ConcertEntity> entities) {
        return entities.stream().map(m -> toDomain(m)).toList();
    }
}
