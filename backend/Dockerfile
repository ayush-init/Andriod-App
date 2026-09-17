# ==========================================
# ROXSTAR Backend - Multi-stage Production Dockerfile
# ==========================================

# Stage 1: Build & Dependencies
FROM node:20-alpine AS builder

WORKDIR /usr/src/app

# Install build tools if native dependencies are needed
RUN apk add --no-cache python3 make g++

# Copy package manifests
COPY package*.json ./

# Install all dependencies (including devDependencies for build/testing)
RUN npm ci

# Copy source code
COPY . .

# Prune devDependencies to reduce image size
RUN npm prune --omit=dev

# ==========================================
# Stage 2: Minimal Production Runtime
# ==========================================
FROM node:20-alpine AS runner

WORKDIR /usr/src/app

# Install curl for container HEALTHCHECK
RUN apk add --no-cache curl

# Set environment to production
ENV NODE_ENV=production
ENV PORT=5000

# Create uploads directory and set permissions for non-root 'node' user
RUN mkdir -p /usr/src/app/uploads && chown -R node:node /usr/src/app

# Copy production node_modules from builder
COPY --from=builder --chown=node:node /usr/src/app/node_modules ./node_modules

# Copy application source code
COPY --chown=node:node . .

# Run under least-privilege non-root user
USER node

# Expose HTTP & WebSocket port
EXPOSE 5000

# Healthcheck to monitor service responsiveness
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:5000/health || exit 1

# Start production server
CMD ["node", "src/index.js"]
