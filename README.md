# 🎵 Concert Reservation Service

> **대규모 트래픽을 고려한 콘서트 예약 시스템**  
> Redis 기반 대기열, Kafka 이벤트 처리, Outbox 패턴을 적용한 고가용성 예약 플랫폼

<br>

## 📌 목차
- [프로젝트 개요](#-프로젝트-개요)
- [기술 스택](#-기술-스택)
- [핵심 기술적 도전과 해결 방법](#-핵심-기술적-도전과-해결-방법)
- [시스템 아키텍처](#-시스템-아키텍처)
- [API 명세](#-api-명세)
- [ERD](#-erd)
- [프로젝트 문서](#-프로젝트-문서)

<br>

## 🎯 프로젝트 개요

콘서트 예약 시 발생하는 **동시 접속 폭증** 문제를 해결하기 위해 설계된 백엔드 서비스입니다.  
단순한 CRUD를 넘어, 실제 운영 환경에서 발생하는 **동시성 이슈**, **데이터 정합성**, **성능 최적화** 문제를 직접 분석하고 해결하는 데 집중했습니다.

| 항목 | 내용 |
|------|------|
| 개발 인원 | 1인 |
| 주요 목표 | 대기열 시스템, 동시성 제어, 이벤트 기반 결제 처리 |

<br>

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.3, Spring Data JPA, Spring AOP |
| Database | H2 (개발), Redis (캐시 / 대기열) |
| Message Queue | Apache Kafka |
| 분산 락 | Redisson |
| 문서화 | Swagger (SpringDoc OpenAPI) |
| 빌드 | Gradle |
| 테스트 | JUnit5, Mockito |

<br>

## 💡 핵심 기술적 도전과 해결 방법

### 1. Redis 기반 대기열 시스템

**문제**: 콘서트 오픈 시 수천 명이 동시에 접근할 경우 서버 과부하 발생

**해결**:
- Redis의 **Sorted Set**을 활용하여 대기열(Waiting Queue)과 활성 토큰(Active Token)을 분리 관리
- 대기 인원이 최대 허용치 미만이면 즉시 활성 토큰 발급, 초과 시 대기열 편입
- **스케줄러**가 주기적으로 대기열에서 활성 토큰으로 승격시켜 트래픽을 제어

```
[토큰 발급 흐름]
요청 → activeToken 수 확인 → 여유 있음: 즉시 activeToken 발급
                           → 가득 참:  waitingToken으로 대기열 편입
                                         ↓
                               Scheduler가 주기적으로 active로 승격
```

---

### 2. Kafka + Outbox 패턴으로 결제 이벤트의 신뢰성 확보

**문제**: 결제 완료 후 외부 시스템(포인트, 알림 등)에 이벤트를 전송할 때, 메시지 유실 시 데이터 불일치 발생

**해결**:
- **Transactional Outbox Pattern** 적용: 결제 트랜잭션과 동일한 DB 트랜잭션 내에서 `PaymentOutBox`에 메시지를 저장
- `@TransactionalEventListener(phase = AFTER_COMMIT)` 로 트랜잭션 커밋 이후에만 Kafka 이벤트 발행
- 스케줄러가 `INIT` 상태의 OutBox 메시지를 재발행하여 **메시지 유실 방지**
- Kafka Consumer에서 OutBox 상태를 `RECEIVED → SUCCESS`로 업데이트하여 **멱등성** 보장

```
[결제 이벤트 흐름]
결제 요청 → DB 저장 + OutBox(INIT) 저장 (같은 트랜잭션)
         → AFTER_COMMIT → Kafka 발행
         → Consumer 수신 → OutBox(RECEIVED → SUCCESS) 업데이트
         → 스케줄러: INIT 상태 OutBox 재발행 (유실 복구)
```

---

### 3. 낙관적 락(Optimistic Lock) + 재시도 전략으로 동시성 제어

**문제**: 잔액 충전 시 동시 요청으로 인한 데이터 정합성 문제 (Lost Update)

**해결**:
- JPA `@Version`을 통한 **낙관적 락** 적용으로 충돌 감지
- `@Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))` 로 충돌 시 자동 재시도
- 비관적 락 대비 **DB 커넥션 점유 시간을 최소화**하여 처리량 개선

---

### 4. Redis 캐싱으로 조회 성능 최적화

**문제**: 콘서트 목록, 좌석 정보 등 반복 조회가 많은 데이터에 대한 DB 부하

**해결**:
- `Spring Cache` + Redis 연동으로 **TTL 10분** 캐시 적용
- `GenericJackson2JsonRedisSerializer` 로 타입 안전한 직렬화
- 예약/취소 이벤트 발생 시 캐시 무효화(Eviction)로 정합성 유지

---

### 5. 인덱스 전략 및 성능 테스트

- 자주 조회되는 컬럼(콘서트 날짜, 좌석 상태 등)에 **인덱스** 설계 및 실행 계획 분석
- **k6**를 이용한 부하 테스트로 병목 구간 확인 및 개선 (`document/step19_k6테스트.pdf` 참고)
- 장애 상황 시나리오별 **장애 보고서** 작성 (`document/step20_장애보고서.pdf` 참고)

---

### 6. 클린 아키텍처 기반 레이어 설계

```
interfaces   →  사용자 요청 처리 (Controller, Consumer, EventListener, Scheduler)
application  →  유스케이스 조합 (Facade)
domain       →  핵심 비즈니스 로직 (Service, Entity, Repository interface)
infra        →  외부 연동 구현 (JPA, Redis, Kafka)
support      →  공통 설정, 예외처리, 필터, 인터셉터
```

- 도메인 레이어가 인프라에 의존하지 않도록 **Repository 인터페이스**를 domain에 두고, 구현체는 infra에 위치
- 레이어 간 의존성 방향을 단방향으로 유지하여 테스트 용이성 및 유지보수성 향상

<br>

## 🏗 시스템 아키텍처

```
┌──────────────────────────────────────────────────────┐
│                      Client                          │
└──────────────────┬───────────────────────────────────┘
                   │ HTTP
┌──────────────────▼───────────────────────────────────┐
│           Spring Boot Application                    │
│  ┌──────────────────────────────────────────────┐    │
│  │  interfaces: Controller / EventListener      │    │
│  ├──────────────────────────────────────────────┤    │
│  │  application: Facade (유스케이스 조합)          │    │
│  ├──────────────────────────────────────────────┤    │
│  │  domain: Service / Entity (비즈니스 로직)      │    │
│  ├──────────────────────────────────────────────┤    │
│  │  infra: JPA / Redis / Kafka 구현체            │    │
│  └──────────────────────────────────────────────┘    │
└──────┬──────────────────────┬────────────────────────┘
       │                      │
┌──────▼──────┐      ┌────────▼────────┐
│  H2 / RDB   │      │  Redis          │
│  (데이터 저장) │      │  (대기열 / 캐시) │
└─────────────┘      └─────────────────┘
                               │ Kafka
                      ┌────────▼────────┐
                      │  Kafka Broker   │
                      │  (결제 이벤트)   │
                      └─────────────────┘
```

<br>

## 📡 API 명세

> Swagger UI: `http://localhost:8080/swagger-ui/index.html`

| 기능 | Method | URL | 인증 |
|------|--------|-----|------|
| 대기열 토큰 발급 | POST | `/token/issued` | - |
| 예약 가능 날짜 조회 | GET | `/concert/dates` | Queue-Token |
| 예약 가능 좌석 조회 | GET | `/concert/seats` | Queue-Token |
| 좌석 예약 요청 | POST | `/concert/reservation` | Queue-Token |
| 잔액 충전 | PATCH | `/cash/charge` | - |
| 잔액 조회 | GET | `/cash/balance` | - |
| 결제 | POST | `/cash/payment` | Queue-Token |

<details>
<summary><b>대기열 토큰 발급</b></summary>

**POST** `/token/issued`

```json
// Request
{ "userId": 1 }

// Response
{
  "data": {
    "userId": 1,
    "queueToken": "waitingToken1111111112222222"
  },
  "success": true
}
```
</details>

<details>
<summary><b>좌석 예약 요청</b></summary>

**POST** `/concert/reservation`

```json
// Request
{
  "concertId": 100,
  "userId": 1,
  "concertDateTime": "2024-07-04 22:30",
  "seatNo": 22
}

// Response
{
  "data": {
    "seat": {
      "id": 1,
      "concertId": 100,
      "seatNumber": 22,
      "status": "RESERVED",
      "updatedAt": "2024-07-04T18:56:05.0797596"
    },
    "result": true
  },
  "success": true
}
```
</details>

<details>
<summary><b>결제</b></summary>

**POST** `/cash/payment`

```json
// Request
{
  "userId": 1,
  "concertId": 100,
  "concertDateTime": "2024-07-04 22:30",
  "seatNo": 22
}

// Response
{
  "data": {
    "user": { "userId": 1, "userName": "짱구", "balance": { "balance": 10000 } },
    "reservation": {
      "reservationId": 1,
      "status": "RESERVED"
    }
  },
  "success": true
}
```
</details>

<br>

## 📊 ERD

> `document/erd_diagram.png` 참고

<br>

## 📂 프로젝트 문서

| 문서 | 설명 |
|------|------|
| `document/milestone.PNG` | 프로젝트 마일스톤 |
| `document/erd_diagram.png` | ERD 다이어그램 |
| `document/시퀀스다이어그램/` | 주요 기능별 시퀀스 다이어그램 |
| `document/lock종류별_테스트.pdf` | 비관적 락 / 낙관적 락 / 분산 락 성능 비교 분석 |
| `document/인덱스테스트.pdf` | DB 인덱스 설계 및 실행 계획 분석 |
| `document/캐싱_보고서.pdf` | Redis 캐싱 적용 전후 성능 비교 |
| `document/step19_k6테스트.pdf` | k6 부하 테스트 결과 및 분석 |
| `document/step20_장애보고서.pdf` | 장애 시나리오별 원인 분석 및 대응 방안 |
| `document/서비스분리_설계.pdf` | 마이크로서비스 분리 설계 문서 |

<br>

## ⚙️ 로컬 실행 방법

```bash
# 1. Redis 실행 (Docker)
docker run -d -p 6379:6379 redis

# 2. Kafka 실행 (Docker)
docker run -d -p 9092:9092 apache/kafka

# 3. 애플리케이션 실행
./gradlew bootRun
```

> H2 콘솔: `http://localhost:8080/h2-console`  
> Swagger UI: `http://localhost:8080/swagger-ui/index.html`
