package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}
