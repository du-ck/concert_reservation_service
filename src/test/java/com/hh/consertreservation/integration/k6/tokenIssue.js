import http from 'k6/http';
import {check, sleep} from 'k6';
import {randomIntBetween, randomItem} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export let options = {
    /*vus: 400,  // 가상 사용자 설정
    iterations: 400,  // 총 3000번의 요청 실행 (각 사용자당 1회씩)
    duration: '10s',  // 10초 동안 실행*/
    scenarios: {
        normal_traffic: {
            executor: 'constant-vus',  // 평상시 트래픽을 유지하는 시나리오
            vus: 50,                   // 50명의 가상 사용자를 유지
            duration: '1m',            // 1분 동안 유지
        },
        spike_traffic: {
            executor: 'ramping-vus',   // 몰렸을 때
            startVUs: 0,
            stages: [
                { duration: '10s', target: 400 },  // 10초 동안 400명으로 증가
                { duration: '30s', target: 400 },  // 30초 동안 400명 유지
                { duration: '10s', target: 0 },     // 10초 동안 0명으로 감소
            ],
            startTime: '10s',           // 10초 후에 시작하여 평상시 트래픽과 겹치게 실행
        },
    },
};

export default function () {
    issue()
    sleep(1);
}

function issue() {
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
}