import http from 'k6/http';
import {check, sleep} from 'k6';
import {randomIntBetween, randomItem} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';



export let options = {
    scenarios: {
        B_concert: {
            executor: 'per-vu-iterations',
            vus: 10,                   // 10명의 가상 사용자를 유지
            iterations: 1,
            exec: 'getBConcertSeat',    // B 콘서트 접근을 시뮬레이션하는 함수 실행
        },
        A_concert: {
            executor: 'per-vu-iterations',
            vus: 400,                        // 400명의 가상 사용자를 설정
            iterations: 1,
            exec: 'getAConcertSeat',         // A 콘서트 접근을 시뮬레이션하는 함수 실행
        },
    }
};



function issueToken() {
    const body = JSON.stringify({
        userId: 1
    });

    let response = http.post(
        `http://localhost:8080/api/token/issued`,
        body,
        {
            headers: {
                'Content-Type': 'application/json'
            }
        }
    )
    // 요청이 성공했는지 확인
    check(response, {
        'is status 200': (r) => r.status === 200
    });

    return response.json('data.queueToken');
}

export function getAConcertSeat() {

    const token = issueToken();
    const concertDateTime = encodeURIComponent("2024-08-15 10:00");

    let response = http.get(
        `http://localhost:8080/api/concert/seats?concertId=1&concertDateTime=${concertDateTime}&userId=1`,
        JSON.stringify({}),
        {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'Queue-Token': token
            }
        }
    )
    // 요청이 성공했는지 확인
    let chesResp = check(response, {
        'is status 200': (r) => r.status === 200
    });
    if (!chesResp) {
        console.log("A 실패")
    }
    sleep(1);   //결과 본 후 최소 1초는 가만히 있는다고 가정
}

export function getBConcertSeat() {

    const token = issueToken();

    const concertDateTime = encodeURIComponent("2024-08-20 15:00");

    let response = http.get(
        `http://localhost:8080/api/concert/seats?concertId=2&concertDateTime=${concertDateTime}&userId=1`,
        {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'Queue-Token': token
            }
        }
    )
    // 요청이 성공했는지 확인
    let chesResp = check(response, {
        'is status 200': (r) => r.status === 200
    });
    if (!chesResp) {
        console.log("B 실패")
    }
    sleep(1);   //결과 본 후 최소 1초는 가만히 있는다고 가정
}