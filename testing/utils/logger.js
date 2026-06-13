/**
 * Winston Logger Utility
 * Provides structured logging for the entire test suite
 */
const winston = require('winston');
const path = require('path');
const fs = require('fs');

// Ensure logs directory exists
const logsDir = path.resolve(__dirname, '../reports/logs');
if (!fs.existsSync(logsDir)) {
  fs.mkdirSync(logsDir, { recursive: true });
}

const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
    winston.format.errors({ stack: true }),
    winston.format.json()
  ),
  defaultMeta: { service: 'saveetha-geotag-tests' },
  transports: [
    // Console output
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.printf(({ timestamp, level, message, ...meta }) => {
          const metaStr = Object.keys(meta).length
            ? ` | ${JSON.stringify(meta)}`
            : '';
          return `[${timestamp}] ${level}: ${message}${metaStr}`;
        })
      ),
    }),
    // File output
    new winston.transports.File({
      filename: path.join(logsDir, 'test-run.log'),
      maxsize: 5242880, // 5MB
      maxFiles: 5,
    }),
    // Error-only file
    new winston.transports.File({
      filename: path.join(logsDir, 'errors.log'),
      level: 'error',
    }),
  ],
});

module.exports = logger;
