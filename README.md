# 콘서트 예약 서비스

## 소개
- 콘서트 예약을 위한 API
- 클린아키텍처 적용 및 TDD 개발
- Redis 를 이용한 대기열 구현
- 대용량 처리를 위해 기능 별로 락 적용 및 조회기능 캐싱 처리
- Kafka 를 이용해 '결제' 기능 책임 분리
- Kafka 발행 실패 방지를 위해 Transactional Outbox-Pattern 적용

## 
## 기술 스택
### Environment
IntelliJ, Git

### Development
Java, Spring Boot, Redis, Kafka, Docker
##

## 기능
- 대기열 토큰 발급
- 잔액 조회
- 잔액 충전
- 콘서트 조회
- 콘서트 날짜 조회
- 콘서트 예약 가능 좌석 조회
- 콘서트 예약
- 결제
