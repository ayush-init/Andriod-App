import pg from 'pg';
import dns from 'dns/promises';
import dotenv from 'dotenv';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
dotenv.config({ path: path.resolve(__dirname, '../../.env') });
dotenv.config(); // fallback to cwd

const { Pool } = pg;

let pool = null;

export async function getPool() {
  if (pool && !pool.ended) return pool;

  const rawUrl = process.env.DATABASE_URL;
  if (!rawUrl) {
    throw new Error('DATABASE_URL environment variable is not defined.');
  }

  // Parse connection URL
  const parsedUrl = new URL(rawUrl);
  const hostname = parsedUrl.hostname;
  const port = parseInt(parsedUrl.port || '5432', 10);
  const user = decodeURIComponent(parsedUrl.username);
  const password = decodeURIComponent(parsedUrl.password);
  const database = parsedUrl.pathname.replace(/^\//, '');

  let hostToConnect = hostname;
  // If not localhost, resolve IPv4 to prevent Windows Node v24 dual-stack IPv6 timeout
  if (hostname !== 'localhost' && hostname !== '127.0.0.1') {
    try {
      const ipv4List = await dns.resolve4(hostname);
      if (ipv4List && ipv4List.length > 0) {
        hostToConnect = ipv4List[0];
      }
    } catch {
      // Fallback to original hostname if DNS resolve4 fails
      hostToConnect = hostname;
    }
  }

  const isSsl = rawUrl.includes('sslmode=require') || !['localhost', '127.0.0.1'].includes(hostname);

  pool = new Pool({
    host: hostToConnect,
    port,
    user,
    password,
    database,
    ssl: isSsl
      ? {
          rejectUnauthorized: false,
          servername: hostname, // Required for Neon / AWS SNI routing
        }
      : false,
    max: 20,
    idleTimeoutMillis: 30000,
    connectionTimeoutMillis: 15000,
  });

  pool.on('error', (err) => {
    console.error('Unexpected PostgreSQL client error on idle pool:', err);
  });

  return pool;
}

export const query = async (text, params) => {
  const p = await getPool();
  return p.query(text, params);
};

export const getClient = async () => {
  const p = await getPool();
  return p.connect();
};

export const checkHealth = async () => {
  const start = Date.now();
  const res = await query('SELECT NOW() as current_time, version() as version');
  const duration = Date.now() - start;
  return {
    status: 'connected',
    latencyMs: duration,
    timestamp: res.rows[0].current_time,
    version: res.rows[0].version,
  };
};

export default {
  getPool,
  query,
  getClient,
  checkHealth,
};
