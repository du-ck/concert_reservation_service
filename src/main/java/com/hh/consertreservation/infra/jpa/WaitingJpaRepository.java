package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.domain.dto.Token;
import com.hh.consertreservation.domain.dto.types.WaitingType;
import com.hh.consertreservation.infra.entity.WaitingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaitingJpaRepository extends JpaRepository<WaitingEntity, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE WaitingEntity w SET w.status = 'EXPIRE' " +
            "where w.expiresAt < :localDateTime " +
            "and w.status = 'ONGOING'")
    int expireToken(@Param("localDateTime")LocalDateTime localDateTime);
    Optional<WaitingEntity> findFirstByStatusOrderById(WaitingType status);
    int countByStatus(WaitingType status);

    @Query(value = "SELECT * FROM waiting where status = 'WAITING' ORDER BY id ASC LIMIT :limit", nativeQuery = true)
    List<WaitingEntity> findWaitingWithLimit(@Param("limit") int limit);

    Optional<WaitingEntity> findByUserIdAndTokenAndStatus(long userId, String token, WaitingType status);
}
