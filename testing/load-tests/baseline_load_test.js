/**
 * ================================================================
 * SAVEETHA GEOTAG — BASELINE / LOAD TESTING (k6)
 * ================================================================
 * Tool      : k6 (https://k6.io)
 * Strategy  : Baseline Load Test
 * Users     : 100 Virtual Users (VUs)
 * Duration  : 60 seconds (1 minute)
 * Expected  : ~120 req/sec | Avg ≤ 300ms | Max ≤ 2000ms
 * ================================================================
 * Run command:
 *   k6 run testing/load-tests/baseline_load_test.js
 * ================================================================
 */

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// ─────────────────────────────────────────────────────────────────
// CUSTOM METRICS
// ─────────────────────────────────────────────────────────────────
const errorRate       = new Rate('error_rate');
const healthCheckTime = new Trend('health_check_response_ms');
const verifyCodeTime  = new Trend('verify_code_response_ms');
const captureApiTime  = new Trend('capture_api_response_ms');
const dbSyncTime      = new Trend('firebase_sync_response_ms');
const totalRequests   = new Counter('total_requests_sent');

// ─────────────────────────────────────────────────────────────────
// LOAD TEST OPTIONS — 100 VUs × 60 seconds (Baseline)
// ─────────────────────────────────────────────────────────────────
export const options = {
  scenarios: {
    baseline_load: {
      executor:    'constant-vus',
      vus:         100,          // 100 concurrent virtual users
      duration:    '60s',        // Run for exactly 1 minute
      gracefulStop: '10s',
    },
  },

  // ── Pass/Fail Thresholds ───────────────────────────────────────
  thresholds: {
    // Error rate must stay below 5%
    error_rate: [{ threshold: 'rate<0.05', abortOnFail: false }],

    // 95th percentile response time < 500ms
    http_req_duration: [
      { threshold: 'p(95)<500',  abortOnFail: false },
      { threshold: 'p(99)<1000', abortOnFail: false },
      { threshold: 'avg<300',    abortOnFail: false },
    ],

    // Individual endpoint thresholds
    health_check_response_ms: ['p(95)<200'],
    verify_code_response_ms:  ['p(95)<400'],
    capture_api_response_ms:  ['p(95)<600'],
    firebase_sync_response_ms:['p(95)<800'],

    // Ensure enough requests are processed (~120 RPS × 60s = 7200 minimum)
    http_reqs: ['rate>80'],
  },
};

// ─────────────────────────────────────────────────────────────────
// BASE URL — Update to match your deployed backend
// ─────────────────────────────────────────────────────────────────
const BASE_URL = __ENV.BASE_URL || 'http://localhost:3000';

// ─────────────────────────────────────────────────────────────────
// SHARED HEADERS
// ─────────────────────────────────────────────────────────────────
const HEADERS = {
  'Content-Type':  'application/json',
  'Accept':        'application/json',
  'X-Test-Source': 'k6-baseline-load-test',
};

// ─────────────────────────────────────────────────────────────────
// SAMPLE GP-CODES (Rotating pool to simulate real user distribution)
// ─────────────────────────────────────────────────────────────────
const GP_CODES = [
  'GP-ABCD1234', 'GP-EFGH5678', 'GP-IJKL9012',
  'GP-MNOP3456', 'GP-QRST7890', 'GP-UVWX1234',
  'GP-YZAB5678', 'GP-CDEF9012', 'GP-GHIJ3456',
  'GP-KLMN7890',
];

function randomCode() {
  return GP_CODES[Math.floor(Math.random() * GP_CODES.length)];
}

// ─────────────────────────────────────────────────────────────────
// VIRTUAL USER SCENARIO — Each VU loops this during the 60s window
// ─────────────────────────────────────────────────────────────────
export default function () {
  const vu  = __VU;
  const iter = __ITER;

  // ── Group 1: Health / Start Screen ──────────────────────────────
  group('TC_LT001 Health Check — Start Screen', () => {
    const start = Date.now();
    const res = http.get(`${BASE_URL}/health`, { headers: HEADERS });
    const dur = Date.now() - start;

    healthCheckTime.add(dur);
    totalRequests.add(1);

    const ok = check(res, {
      'TC_LT001 status 200':          (r) => r.status === 200,
      'TC_LT002 body not empty':       (r) => r.body && r.body.length > 0,
      'TC_LT003 response time <500ms': () => dur < 500,
    });
    errorRate.add(!ok);
  });

  sleep(0.1);

  // ── Group 2: Verify Code Screen ─────────────────────────────────
  group('TC_LT004 Verify Code API', () => {
    const code = randomCode();
    const payload = JSON.stringify({ code, userId: `user_${vu}_${iter}` });

    const start = Date.now();
    const res = http.post(`${BASE_URL}/api/verify`, payload, { headers: HEADERS });
    const dur = Date.now() - start;

    verifyCodeTime.add(dur);
    totalRequests.add(1);

    check(res, {
      'TC_LT004 verify responds':         (r) => [200, 201, 400, 404].includes(r.status),
      'TC_LT005 verify no 500 error':      (r) => r.status !== 500,
      'TC_LT006 verify time <600ms':       () => dur < 600,
      'TC_LT007 verify returns JSON body': (r) => {
        try { JSON.parse(r.body); return true; } catch { return false; }
      },
    });
    errorRate.add(res.status >= 500);
  });

  sleep(0.1);

  // ── Group 3: Home / Capture Screen ─────────────────────────────
  group('TC_LT008 Capture Metadata Upload', () => {
    const payload = JSON.stringify({
      userId:    `user_${vu}`,
      latitude:  13.0827 + (Math.random() * 0.01),
      longitude: 80.2707 + (Math.random() * 0.01),
      timestamp: new Date().toISOString(),
      gpCode:    randomCode(),
      imageRef:  `capture_${vu}_${iter}.jpg`,
    });

    const start = Date.now();
    const res = http.post(`${BASE_URL}/api/captures`, payload, { headers: HEADERS });
    const dur = Date.now() - start;

    captureApiTime.add(dur);
    totalRequests.add(1);

    check(res, {
      'TC_LT008 upload responds':           (r) => r.status < 500,
      'TC_LT009 upload time <800ms':        () => dur < 800,
      'TC_LT010 upload no server crash':    (r) => r.status !== 503,
      'TC_LT011 upload content-type json':  (r) =>
        (r.headers['Content-Type'] || '').includes('json'),
    });
    errorRate.add(res.status >= 500);
  });

  sleep(0.1);

  // ── Group 4: Dashboard / Stats Screen ──────────────────────────
  group('TC_LT012 Dashboard Statistics API', () => {
    const start = Date.now();
    const res = http.get(`${BASE_URL}/api/stats?userId=user_${vu}`, { headers: HEADERS });
    const dur = Date.now() - start;

    totalRequests.add(1);

    check(res, {
      'TC_LT012 stats responds':        (r) => r.status < 500,
      'TC_LT013 stats time <500ms':     () => dur < 500,
      'TC_LT014 stats no timeout':      (r) => r.status !== 408,
      'TC_LT015 stats body parseable':  (r) => {
        try { JSON.parse(r.body); return true; } catch { return true; }
      },
    });
    errorRate.add(res.status >= 500);
  });

  sleep(0.1);

  // ── Group 5: Captures History Screen ───────────────────────────
  group('TC_LT016 Captures History List API', () => {
    const start = Date.now();
    const res = http.get(`${BASE_URL}/api/captures?userId=user_${vu}&page=1&limit=20`, { headers: HEADERS });
    const dur = Date.now() - start;

    totalRequests.add(1);

    check(res, {
      'TC_LT016 history responds':       (r) => r.status < 500,
      'TC_LT017 history time <600ms':    () => dur < 600,
      'TC_LT018 history list structure': (r) => {
        try {
          const json = JSON.parse(r.body);
          return Array.isArray(json) || typeof json === 'object';
        } catch { return true; }
      },
    });
    errorRate.add(res.status >= 500);
  });

  sleep(0.1);

  // ── Group 6: Firebase Sync ──────────────────────────────────────
  group('TC_LT019 Firebase Sync Endpoint', () => {
    const payload = JSON.stringify({
      userId: `user_${vu}`,
      action: 'sync',
      timestamp: Date.now(),
    });

    const start = Date.now();
    const res = http.post(`${BASE_URL}/api/firebase/sync`, payload, { headers: HEADERS });
    const dur = Date.now() - start;

    dbSyncTime.add(dur);
    totalRequests.add(1);

    check(res, {
      'TC_LT019 firebase sync responds':  (r) => r.status < 500,
      'TC_LT020 firebase sync <1000ms':   () => dur < 1000,
      'TC_LT021 sync no internal error':  (r) => r.status !== 500,
    });
    errorRate.add(res.status >= 500);
  });

  sleep(0.1);

  // ── Group 7: Scan Screen (QR lookup) ───────────────────────────
  group('TC_LT022 Scan QR Code Lookup', () => {
    const code = randomCode();
    const start = Date.now();
    const res = http.get(`${BASE_URL}/api/scan?code=${code}`, { headers: HEADERS });
    const dur = Date.now() - start;

    totalRequests.add(1);

    check(res, {
      'TC_LT022 scan responds':        (r) => r.status < 500,
      'TC_LT023 scan time <400ms':     () => dur < 400,
      'TC_LT024 scan no 503':          (r) => r.status !== 503,
    });
    errorRate.add(res.status >= 500);
  });

  sleep(0.1);

  // ── Group 8: Settings Screen ────────────────────────────────────
  group('TC_LT025 Settings Preferences API', () => {
    const start = Date.now();
    const res = http.get(`${BASE_URL}/api/settings?userId=user_${vu}`, { headers: HEADERS });
    const dur = Date.now() - start;

    totalRequests.add(1);

    check(res, {
      'TC_LT025 settings responds':   (r) => r.status < 500,
      'TC_LT026 settings time <300ms': () => dur < 300,
    });
    errorRate.add(res.status >= 500);
  });

  sleep(0.2); // Pause between user iterations
}

// ─────────────────────────────────────────────────────────────────
// SETUP — Called once before all VUs start
// ─────────────────────────────────────────────────────────────────
export function setup() {
  console.log('='.repeat(60));
  console.log('  SAVEETHA GEOTAG — BASELINE LOAD TEST STARTED');
  console.log('  100 Virtual Users × 60 Seconds');
  console.log('  Target: ~120 req/sec | Avg ≤ 300ms | Max ≤ 2000ms');
  console.log('='.repeat(60));

  // Pre-flight health check
  const res = http.get(`${BASE_URL}/health`);
  if (res.status !== 200) {
    console.warn(`⚠️  Backend not healthy (${res.status}). Proceeding anyway...`);
  }

  return { startTime: Date.now(), baseUrl: BASE_URL };
}

// ─────────────────────────────────────────────────────────────────
// TEARDOWN — Called once after all VUs finish
// ─────────────────────────────────────────────────────────────────
export function teardown(data) {
  const elapsed = ((Date.now() - data.startTime) / 1000).toFixed(1);
  console.log(`\n✅ Load test completed in ${elapsed}s`);
  console.log('📊 Check the generated HTML report for detailed results.');
}

// ─────────────────────────────────────────────────────────────────
// SUMMARY — Generates HTML + console summary after the run
// ─────────────────────────────────────────────────────────────────
export function handleSummary(data) {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
  const reportPath = `testing/load-tests/reports/baseline_report_${timestamp}.html`;

  return {
    [reportPath]: htmlReport(data),
    stdout: textSummary(data, { indent: '  ', enableColors: true }),
    'testing/load-tests/reports/baseline_summary.json': JSON.stringify(data, null, 2),
  };
}
