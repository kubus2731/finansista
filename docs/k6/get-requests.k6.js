// =============================================================================
// docs/k6/get-requests.k6.js
//
// Test wydajnosciowy endpointu GET /api/v1/requests (REST API Finansisty).
// Symuluje stopniowy wzrost obciazenia do 1000 wirtualnych uzytkownikow,
// 5-minutowe utrzymanie szczytu i kontrolowany ramp-down.
//
// Uruchomienie:
//   1) wystartuj backend lokalnie (mvn spring-boot:run, port 8080)
//   2) zaloguj sie raz po REST i wklej JWT do zmiennej srodowiskowej K6_JWT:
//        $env:K6_JWT = "eyJhbGciOi..."     # PowerShell
//        export K6_JWT="eyJhbGciOi..."     # bash
//   3) k6 run docs/k6/get-requests.k6.js
//
// Przekazywane parametry (opcjonalne):
//   --env BASE_URL=http://localhost:8080
//   --env PAGE_SIZE=50
// =============================================================================

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const PAGE_SIZE = __ENV.PAGE_SIZE || '50';
const JWT = __ENV.K6_JWT || '';

const errorRate = new Rate('errors');
const p95Latency = new Trend('p95_latency_ms');

export const options = {
  // 1000 VU szczyt — wymog z deklaracji "testow wydajnosciowych endpointow API"
  stages: [
    { duration: '30s', target: 100 },   // warm-up
    { duration: '1m',  target: 500 },   // ramp-up
    { duration: '2m',  target: 1000 },  // utrzymanie pelnego obciazenia
    { duration: '30s', target: 0 },     // ramp-down
  ],
  thresholds: {
    // SLO: 95% zapytan ponizej 800 ms, mniej niz 1% bledow
    http_req_duration: ['p(95)<800', 'p(99)<1500'],
    errors: ['rate<0.01'],
  },
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(95)', 'p(99)'],
};

export default function () {
  const url = `${BASE_URL}/api/v1/requests?page=0&size=${PAGE_SIZE}`;
  const params = {
    headers: {
      'Accept': 'application/json',
      'Authorization': JWT ? `Bearer ${JWT}` : '',
    },
    tags: { endpoint: 'GET /api/v1/requests' },
  };

  const res = http.get(url, params);

  const ok = check(res, {
    'status is 200':           (r) => r.status === 200,
    'body is JSON array':      (r) => Array.isArray(r.json()),
    'X-Total-Count present':   (r) => r.headers['X-Total-Count'] !== undefined,
    'latency < 1500 ms':       (r) => r.timings.duration < 1500,
  });

  errorRate.add(!ok);
  p95Latency.add(res.timings.duration);

  sleep(1);
}

export function handleSummary(data) {
  // Prosty raport tekstowy + JSON do dalszej analizy waskich gardel.
  return {
    'stdout': textSummary(data),
    'docs/k6/get-requests.summary.json': JSON.stringify(data, null, 2),
  };
}

function textSummary(data) {
  const m = data.metrics;
  const get = (k, sub) => (m[k] && m[k].values && m[k].values[sub] != null
    ? m[k].values[sub].toFixed(2)
    : 'n/a');
  return [
    '',
    '=== GET /api/v1/requests — wyniki testu wydajnosciowego ===',
    `  total requests : ${get('http_reqs', 'count')}`,
    `  failed         : ${get('http_req_failed', 'rate')} (rate)`,
    `  latency avg    : ${get('http_req_duration', 'avg')} ms`,
    `  latency p95    : ${get('http_req_duration', 'p(95)')} ms`,
    `  latency p99    : ${get('http_req_duration', 'p(99)')} ms`,
    `  errors (check) : ${get('errors', 'rate')} (rate)`,
    '',
  ].join('\n');
}
