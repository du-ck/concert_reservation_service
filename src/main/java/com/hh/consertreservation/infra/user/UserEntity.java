package com.hh.consertreservation.infra.user;

import com.hh.consertreservation.domain.user.User;
import com.hh.consertreservation.infra.cash.UserBalanceEntity;
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
@Table(name = "\"user\"")
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(length = 13, nullable = false, unique = true)
    private String phone;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id")
    private UserBalanceEntity userBalanceEntity;

    public static User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .userName(entity.getName())
                .userName(entity.getName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .balance(UserBalanceEntity.toDomain(entity.getUserBalanceEntity()))
                .build();
    }
}
