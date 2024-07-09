# 콘서트 예약 서비스
### 📜ERD, 마일스톤, 시퀀스 다이어그램은 document 폴더에 있습니다
<br>
<br>

**1. 대기열 토큰 발급 API**
----
- 콘서트 예약을 하기위한 대기열의 토큰을 발급<br>
- 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함<br>
- 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능

## Request
* **URL**


| 메서드  | 요청 URL               |
|:----:|:--------------|
| POST | /token/issued |

* **Request Header**

| 파라미터          | 타입     | 필수여부 | 설명               |
|:--------------|:----|:----:|:-----------------|
| Content-Type  | String |  O   | application/json |

* **Request Element**

| 파라미터   | 타입   | 필수여부 | 설명      |
|:-------|:-----|:----:|:--------|
| userId | Long |  O   | 유저 고유번호 |


## Response
  ```json
    {
        "data": {
          "userId": 1,
          "queueToken": "waitingToken1111111112222222"
        },
        "success": true
      }
  ```


**2. 예약 가능 날짜 조회 API**
----
- 해당 콘서트의 예약가능한 날짜목록 조회

## Request
* **URL**


| 메서드  | 요청 URL         |
|:----:|:---------------|
| GET  | /concert/dates |

* **Request Header**

| 파라미터          | 타입     | 필수여부 | 설명               |
|:--------------|:----|:----:|:-----------------|
| Content-Type  | String |  O   | application/json |
| Queue-Token | String |  O   | 대기열 토큰           |

* **Request Element**

| 파라미터      | 타입   | 필수여부 | 설명                   |
|:----------|:-----|:----:|:---------------------|
| concertId | Long |  O   | 콘서트 고유번호 |


## Response
  ```json
    {
      "data": {
        "concerts": [
          {
            "concertId": 100,
            "title": "짱구는 못말려 극장판 9기 어른제국의 역습",
            "concertDate": "2024-07-04 22:30",
            "description": "개꿀잼",
            "price": 15000,
            "seats": 50
          },
          {
            "concertId": 100,
            "title": "짱구는 못말려 극장판 9기 어른제국의 역습",
            "concertDate": "2024-07-05 19:30",
            "description": "개꿀잼",
            "price": 15000,
            "seats": 50
          },
          {
            "concertId": 100,
            "title": "짱구는 못말려 극장판 9기 어른제국의 역습",
            "concertDate": "2024-07-06 20:30",
            "description": "개꿀잼",
            "price": 15000,
            "seats": 50
          }
        ]
      },
      "success": true
    }
  ```

**3. 예약 가능 좌석 조회 API**
----
- 예약가능한 날짜정보를 입력받아 예약가능한 좌석정보 조회<br>
- 좌석정보는 1~50까지의 번호로 관리

## Request
* **URL**


| 메서드 | 요청 URL         |
|:---:|:---------------|
| GET | /concert/seats |

* **Request Header**

| 파라미터          | 타입     | 필수여부 | 설명               |
|:--------------|:----|:----:|:-----------------|
| Content-Type  | String |  O   | application/json |
| Queue-Token | String |  O   | 대기열 토큰           |

* **Request Element**

| 파라미터      | 타입     | 필수여부 | 설명                               |
|:----------|:-------|:----:|:---------------------------------|
| concertId | Long   |  O   | 콘서트 고유번호             |
| concertDateTime | String |  O   | 콘서트 날짜 (yyyy-MM-dd HH:mm)    |


## Response
  ```json
    {
      "data": {
        "seats": [
          {
            "id": 1,
            "concertId": 100,
            "seatNumber": 22,
            "status": "EMPTY",
            "updatedAt": "2024-07-04T18:53:17.9570689"
          },
          {
            "id": 2,
            "concertId": 100,
            "seatNumber": 23,
            "status": "EMPTY",
            "updatedAt": "2024-07-04T18:53:17.9570689"
          },
          {
            "id": 3,
            "concertId": 100,
            "seatNumber": 24,
            "status": "EMPTY",
            "updatedAt": "2024-07-04T18:53:17.9570689"
          },
          {
            "id": 4,
            "concertId": 100,
            "seatNumber": 25,
            "status": "EMPTY",
            "updatedAt": "2024-07-04T18:53:17.9570689"
          }
        ]
      },
      "success": true
    }
  ```

**4. 좌석 예약 요청 API**
----
- 날짜와 좌석 정보를 입력받아 좌석을 예약 처리<br>
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 약 5분간 임시 배정<br>
- 만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되며 <br>
다른 사용자는 예약할 수 없음
## Request
* **URL**


| 메서드  | 요청 URL               |
|:----:|:---------------------|
| POST | /concert/reservation |

* **Request Header**

| 파라미터          | 타입     | 필수여부 | 설명               |
|:--------------|:----|:----:|:-----------------|
| Content-Type  | String |  O   | application/json |
| Queue-Token | String |  O   | 대기열 토큰           |

* **Request Element**

| 파라미터            | 타입     | 필수여부 | 설명                        |
|:----------------|:-------|:----:|:--------------------------|
| concertId       | Long   |  O   | 콘서트 고유번호                  |
| userId          | Long   |  O   | 유저 고유번호                   |
| concertDateTime | String |  O   | 콘서트 날짜 (yyyy-MM-dd HH:mm) |
| seatNo          | Long   |  O   | 좌석번호                      |



## Response
  ```json
    {
      "data": {
        "seat": {
          "id": 1,
          "concertId": 100,
          "seatNumber": 1,
          "status": "RESERVED",
          "updatedAt": "2024-07-04T18:56:05.0797596"
        },
        "result": true
      },
      "success": true
    }
  ```

**5. 잔액 충전 API**
----
- 사용자 식별자 및 충전할 금액을 받아 사용자의 잔액을 충전

## Request
* **URL**

|  메서드  | 요청 URL       |
|:-----:|:-------------|
| PATCH | /cash/charge |

* **Request Header**

| 파라미터          | 타입     | 필수여부 | 설명               |
|:--------------|:----|:----:|:-----------------|
| Content-Type  | String |  O   | application/json |

* **Request Element**

| 파라미터   | 타입     | 필수여부 | 설명      |
|:-------|:-------|:----:|:--------|
| userId | Long   |  O   | 유저 고유번호 |
| amount | Long   |  O   | 충전할 금액  |


## Response
  ```json
{
    "data": {
      "balance": {
        "userId": 1,
        "balance": 10000,
        "updatedAt": "2024-07-04T18:48:42.8808534"
      }
    },
    "success": true
}
  ```

**6. 잔액 조회 API**
----
- 사용자 식별자를 통해 사용자의 잔액을 조회

## Request
* **URL**

| 메서드 | 요청 URL        |
|:---:|:--------------|
| GET | /cash/balance |

* **Request Header**

| 파라미터          | 타입     | 필수여부 | 설명               |
|:--------------|:----|:----:|:-----------------|
| Content-Type  | String |  O   | application/json |

* **Request Element**

| 파라미터   | 타입     | 필수여부 | 설명      |
|:-------|:-------|:----:|:--------|
| userId | Long   |  O   | 유저 고유번호 |


## Response
  ```json
    {
      "data": {
        "balance": {
          "userId": 1,
          "balance": 10000,
          "updatedAt": "2024-07-04T18:48:42.8808534"
        }
      },
      "success": true
    }
  ```


**7. 결제 API**
----
- 결제 처리하고 결제 내역을 생성<br>
- 결제 완료 시 좌석을 배정하고 토큰을 만료시킨다

## Request
* **URL**

| 메서드  | 요청 URL        |
|:----:|:--------------|
| POST | /cash/payment |

* **Request Header**

| 파라미터          | 타입     | 필수여부 | 설명               |
|:--------------|:----|:----:|:-----------------|
| Content-Type  | String |  O   | application/json |
| Queue-Token | String |  O   | 대기열 토큰           |

* **Request Element**

| 파라미터      | 타입     | 필수여부 | 설명   |
|:----------|:-------|:----:|:--------|
| userId    | Long   |  O   | 유저 고유번호 |
| concertId | Long   |  O   | 유저 고유번호 |
| concertDateTime | String |  O   | 콘서트 날짜 (yyyy-MM-dd HH:mm) |
| seatNo          | Long   |  O   | 좌석번호                      |


## Response
  ```json
{
    "data": {
        "user": {
          "userId": 1,
          "userName": "짱구",
          "phone": "010-1234-5678",
          "email": "Jjang9@example.com",
          "balance": {
            "userId": 1,
            "balance": 10000,
            "updatedAt": "2024-07-04T18:54:43.6158489"
          }
        },
        "reservation": {
          "reservationId": 1,
          "concert": {
            "concertId": 100,
            "title": "짱구는 못말려 극장판 9기 어른제국의 역습",
            "concertDateTime": "2024-07-04T18:54:43.6158489",
            "description": "개꿀잼",
            "price": 15000,
            "seats": 50
          },
          "seat": {
            "id": 1,
            "concertId": 100,
            "seatNumber": 1,
            "status": "RESERVED",
            "updatedAt": "2024-07-04T18:54:43.6163492"
          },
          "createdAt": "2024-07-04T18:54:43.6163492",
          "status": "RESERVED"
        }
    },
    "success": true
}
  ```
