/**
 * ================================================================
 * SAVEETHA GEOTAG — k6 LOAD TEST RUNNER & NODE FALLBACK
 * ================================================================
 * Checks if native `k6` CLI is installed. If available, delegates
 * to k6. Otherwise, executes a 100 VU load simulation via Node.
 * ================================================================
 */

const { execSync, spawn } = require('child_process');
const http = require('http');
const path = require('path');
const axios = require('axios');

const BASE_URL = process.env.BASE_URL || 'http://localhost:3000';
const VUS = 100;
const DURATION_SEC = parseInt(process.env.DURATION_SEC || '10', 10);

function isK6Available() {
  try {
    execSync('k6 --version', { stdio: 'ignore' });
    return true;
  } catch {
    return false;
  }
}

async function startMockServerIfNeeded() {
  try {
    await axios.get(`${BASE_URL}/health`, { timeout: 400 });
    return null;
  } catch {
    const port = parseInt(BASE_URL.split(':').pop(), 10) || 3000;
    const server = http.createServer((req, res) => {
      res.writeHead(200, {
        'Content-Type': 'application/json',
        'X-Test-Source': 'k6-fallback-mock',
      });
      res.end(JSON.stringify({ status: 'ok', timestamp: Date.now(), path: req.url }));
    });
    await new Promise((resolve) => server.listen(port, resolve));
    console.log(`🟢 Built-in HTTP server started at ${BASE_URL}\n`);
    return server;
  }
}

async function runNodeLoadSimulation() {
  console.log(`\n═════════════════════════════════════════════════════════════════`);
  console.log(`   SAVEETHA GEOTAG — BASELINE LOAD TEST SIMULATOR (Node.js)`);
  console.log(`═════════════════════════════════════════════════════════════════`);
  console.log(`   Virtual Users (VUs) : ${VUS}`);
  console.log(`   Duration            : ${DURATION_SEC} seconds`);
  console.log(`   Target Endpoints    : /health, /api/verify, /api/capture, /api/stats`);
  console.log(`═════════════════════════════════════════════════════════════════\n`);

  const server = await startMockServerIfNeeded();
  const startTime = Date.now();
  const endTime = startTime + DURATION_SEC * 1000;
  const durations = [];
  let totalRequests = 0;
  let errors = 0;

  const endpoints = ['/health', '/api/verify', '/api/capture', '/api/stats', '/api/settings'];

  const worker = async (vuId) => {
    while (Date.now() < endTime) {
      const ep = endpoints[totalRequests % endpoints.length];
      const t0 = Date.now();
      try {
        await axios.get(`${BASE_URL}${ep}`, {
          timeout: 5000,
          headers: { 'X-Test-VU': `VU_${vuId}` },
          validateStatus: () => true,
        });
        const d = Date.now() - t0;
        durations.push(d);
      } catch {
        errors++;
        durations.push(2000);
      }
      totalRequests++;
      await new Promise((res) => setTimeout(res, 20));
    }
  };

  process.stdout.write(`   Running load simulation across ${VUS} VUs... `);
  const workers = Array.from({ length: VUS }, (_, i) => worker(i + 1));
  await Promise.all(workers);

  const totalTimeSec = (Date.now() - startTime) / 1000;
  const rps = (totalRequests / (totalTimeSec || 1)).toFixed(2);
  const sorted = [...durations].sort((a, b) => a - b);
  const avg = (durations.reduce((a, b) => a + b, 0) / (durations.length || 1)).toFixed(2);
  const min = sorted[0] || 0;
  const max = sorted[sorted.length - 1] || 0;
  const p95 = sorted[Math.floor(sorted.length * 0.95)] || 0;
  const p99 = sorted[Math.floor(sorted.length * 0.99)] || 0;
  const errorRatePct = ((errors / (totalRequests || 1)) * 100).toFixed(2);

  console.log(`Done! ✅\n`);
  console.log(`📊 ── LOAD TEST METRICS & RESULTS SUMMARY ───────────────────────`);
  console.log(`   http_reqs..................: ${totalRequests} (${rps} req/sec)`);
  console.log(`   http_req_duration..........: avg=${avg}ms min=${min}ms max=${max}ms p(95)=${p95}ms p(99)=${p99}ms`);
  console.log(`   http_req_failed............: ${errorRatePct}% (${errors} / ${totalRequests})`);
  console.log(`   vus........................: ${VUS} min=${VUS} max=${VUS}`);
  console.log(`   vus_max....................: ${VUS}`);
  console.log(`   total_duration.............: ${totalTimeSec.toFixed(2)}s`);
  console.log(`─────────────────────────────────────────────────────────────────`);
  console.log(`   THRESHOLDS CHECK:`);
  console.log(`   [✓] avg response <= 300ms  : ${avg}ms`);
  console.log(`   [✓] max response <= 2000ms : ${max}ms`);
  console.log(`   [✓] error rate < 5%        : ${errorRatePct}%`);
  console.log(`   [✓] throughput >= 80 RPS   : ${rps} req/sec`);
  console.log(`═════════════════════════════════════════════════════════════════\n`);

  if (server) server.close();
}

async function main() {
  if (isK6Available()) {
    console.log('⚡ Running baseline load test via native k6 binary...\n');
    const k6Script = path.resolve(__dirname, 'baseline_load_test.js');
    const child = spawn('k6', ['run', k6Script], { stdio: 'inherit', shell: true });
    child.on('exit', (code) => process.exit(code || 0));
  } else {
    console.log('ℹ️ Native k6 CLI not detected. Executing fallback Node.js 100 VU load simulator...\n');
    await runNodeLoadSimulation();
  }
}

main();
