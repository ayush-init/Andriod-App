import http from 'http';
import dotenv from 'dotenv';
import app from './app.js';
import db from './db/index.js';
import logger from './utils/logger.js';
import { initSocketIO } from './sockets/index.js';

dotenv.config();

const PORT = process.env.PORT || 5000;
const server = http.createServer(app);

// Initialize Socket.IO engine
const io = initSocketIO(server);

// Startup Recovery Routine (Server restart crash-safety)
async function onServerStartup() {
  try {
    logger.info('Performing database health check and startup recovery...');
    const health = await db.checkHealth();
    logger.info(`Database connected! Server version: ${health.version.split(' on ')[0]}`);

    // Transition any interrupted spins to ABORTED
    const recoveryRes = await db.query(`
      UPDATE spins 
      SET status = 'ABORTED', completed_at = NOW() 
      WHERE status IN ('WAITING', 'RUNNING')
      RETURNING id, room_id;
    `);

    if (recoveryRes.rowCount > 0) {
      logger.warn(`Startup Recovery: Cleanly aborted ${recoveryRes.rowCount} interrupted active spins from previous server run.`);
      for (const spin of recoveryRes.rows) {
        await db.query(
          `INSERT INTO spin_events (spin_id, event_type, sequence_no, payload_json)
           VALUES ($1, 'SPIN_ABORTED', (SELECT COALESCE(MAX(sequence_no), 0) + 1 FROM spin_events WHERE spin_id = $1), '{"reason": "SERVER_RESTART_ABORT"}'::jsonb)`,
          [spin.id]
        );
      }
    // Clean up any stale online room memberships from previous server run
    const membersCleanRes = await db.query(`
      UPDATE room_members 
      SET is_online = false, left_at = NOW() 
      WHERE is_online = true
      RETURNING id;
    `);
    if (membersCleanRes.rowCount > 0) {
      logger.info(`Startup Recovery: Reset ${membersCleanRes.rowCount} stale online room memberships to offline.`);
    }
  } catch (err) {
    logger.error('Startup Recovery error:', err);
  }
}

// Start HTTP server
server.listen(PORT, async () => {
  logger.info(`🚀 ROXSTAR Backend server running on http://localhost:${PORT}`);
  logger.info(`📍 Health check: http://localhost:${PORT}/health`);
  logger.info(`📁 Uploads hosted at: http://localhost:${PORT}/uploads`);
  await onServerStartup();
});

// Graceful shutdown handling
const shutdown = async (signal) => {
  logger.info(`${signal} received. Initiating graceful shutdown...`);
  server.close(() => {
    logger.info('HTTP server closed.');
    process.exit(0);
  });
};

process.on('SIGINT', () => shutdown('SIGINT'));
process.on('SIGTERM', () => shutdown('SIGTERM'));

export { server };
