package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.cash.*;
import com.hh.consertreservation.domain.concert.Concert;
import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.cash.ReservationType;
import com.hh.consertreservation.domain.user.User;
import com.hh.consertreservation.support.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {

    @InjectMocks
    private CashService cashService;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    /**
     * 잔액조회의 기본기능 테스트
     */
    @Test
    void 잔액조회() throws Exception {
        UserBalance mockBalance = UserBalance.builder()
                .userId(userId)
                .balance(10000L)
                .updatedAt(LocalDateTime.now())
                .build();

        given(userBalanceRepository.findByUserIdWithLock(userId))
                .willReturn(Optional.of(mockBalance));

        Optional<UserBalance> balance = cashService.getUserBalance(userId);

        Assertions.assertEquals(userId, balance.get().getUserId());
        Assertions.assertEquals(10000L, balance.get().getBalance());
    }

    /**
     * 잔액조회했을때 데이터자체가 없을 경우 = 없는 사용자 취급
     */
    @Test
    void 잔액조회_결과없음() {

        given(userBalanceRepository.findByUserIdWithLock(userId))
                .willReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> cashService.getUserBalance(userId));

        Assertions.assertEquals("유저를 찾을 수 없습니다", exception.getMessage());
    }

    @Test
    void 잔액충전() throws Exception {
        long amount = 10000L;
        UserBalance balance = UserBalance.builder()
                .userId(userId)
                .balance(10000L)
                .updatedAt(LocalDateTime.now()).build();

        given(userBalanceRepository.findByUserIdWithLock(userId))
                .willReturn(Optional.of(balance));

        given(userBalanceRepository.save(balance))
                .willReturn(Optional.of(balance));

        Optional<UserBalance> result = cashService.charge(userId, amount);

        Assertions.assertEquals(userId, result.get().getUserId());
        Assertions.assertEquals(20000L, result.get().getBalance());
    }

    @Test
    void 잔액충전_0이하() {
        long amount = -10000L;

        UserBalance balance = UserBalance.builder()
                .userId(userId)
                .balance(10000L)
                .updatedAt(LocalDateTime.now()).build();

        given(userBalanceRepository.findByUserIdWithLock(userId))
                .willReturn(Optional.of(balance));

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> cashService.charge(userId, amount));

        Assertions.assertEquals("충전할 금액은 0보다 커야 합니다", exception.getMessage());
    }

    @Test
    void 결제() {
        UserBalance balance = UserBalance.builder()
                .userId(userId)
                .balance(100000L)
                .updatedAt(LocalDateTime.now()).build();
        List<ConcertSchedule> schedules = Concert.getMockListData();
        Seat seat = Seat.builder().build();
        seat.setMockData();
        User user = User.builder()
                .id(userId)
                .userName("짱구")
                .phone("010-1234-5678")
                .email("Jjang9@example.com")
                .balance(balance)
                .build();
        ConcertSchedule schedule = schedules.get(0);

        ReservationInfo saveReservation = ReservationInfo.builder()
                .reservationId(1L)
                .concertSchedule(schedule)
                .seat(seat)
                .user(user)
                .createdAt(LocalDateTime.now())
                .status(ReservationType.RESERVED)
                .build();

        given(userBalanceRepository.save(balance))
                .willReturn(Optional.of(balance));
        given(reservationRepository.save(any(ReservationInfo.class)))
                .willReturn(Optional.of(saveReservation));

        Optional<ReservationInfo> result = cashService.payment(user, seat, schedule);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.get().getStatus(), ReservationType.RESERVED);
        Assertions.assertEquals(result.get().getUser().getId(), user.getId());
    }

    @Test
    void 결제_잔액부족() {
        UserBalance balance = UserBalance.builder()
                .userId(userId)
                .balance(100L)
                .updatedAt(LocalDateTime.now()).build();
        List<ConcertSchedule> schedules = Concert.getMockListData();
        Seat seat = Seat.builder().build();
        seat.setMockData();
        User user = User.builder()
                .id(userId)
                .userName("짱구")
                .phone("010-1234-5678")
                .email("Jjang9@example.com")
                .balance(balance)
                .build();
        ConcertSchedule schedule = schedules.get(0);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> cashService.payment(user, seat, schedule));

        Assertions.assertEquals("잔액 부족", exception.getMessage());
    }

    @Test
    void 결제_음수_사용() {
        UserBalance balance = UserBalance.builder()
                .userId(userId)
                .balance(100L)
                .updatedAt(LocalDateTime.now()).build();
        List<ConcertSchedule> schedules = Concert.getMockListData();
        Seat seat = Seat.builder().build();
        seat.setMockData();
        User user = User.builder()
                .id(userId)
                .userName("짱구")
                .phone("010-1234-5678")
                .email("Jjang9@example.com")
                .balance(balance)
                .build();
        ConcertSchedule schedule = schedules.get(0);
        schedule = schedule.toBuilder().price(-1000L).build();

        ConcertSchedule finalSchedule = schedule;
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> cashService.payment(user, seat, finalSchedule));

        Assertions.assertEquals("사용할 금액은 0보다 커야 합니다", exception.getMessage());
    }
}