# ROXSTAR: Voice Draft, Real-Time Room & Spin Wheel System

> **Unified Technical Assessment for Audio, Backend, and DevOps (200 Points)**

---

## 🎯 Overview

ROXSTAR is a real-time, audio-enabled multiplayer platform combining:
1. **Android Audio Studio**: High-performance local voice recording using **Google Oboe C++ NDK** with real-time **Echo DSP** effect processing and WAV encoding.
2. **Real-Time Room Management**: Authoritative Node.js room service with WebSocket/Socket.IO presence, draft sharing, and audio streaming.
3. **Multiplayer Spin Wheel**: Concurrency-safe, server-authoritative elimination engine with a 5-second countdown timer, winner selection, and comprehensive edge-case handling.
4. **Cloud & DevOps**: PostgreSQL relational database, Docker containerization, GitHub Actions CI/CD automation, and cloud deployment on Google Cloud Platform / AWS.

---

## 🏗️ Repository Structure

Following the assessment specification:
```text
├── android-app/             # Android Studio Kotlin + Jetpack Compose application
│   └── app/src/main/cpp/    # Native C++ Oboe Audio Studio & Echo DSP filter
├── native-audio/            # Shared native audio C++ sources & CMake definitions
├── backend/                 # Node.js + Express REST API and Socket.IO server
│   ├── src/                 # Controllers, services, sockets, database clients
│   ├── uploads/             # Hosted audio draft volume
│   └── public/              # Browser test client for multi-user simulation
├── database/                # PostgreSQL schema DDL, migrations, and indexes
├── infrastructure/          # Dockerfile, docker-compose.yml, cloud deployment
├── .github/workflows/       # GitHub Actions CI/CD pipeline
├── docs/                    # Architecture diagrams (Mermaid), audio flow, OpenAPI
├── tests/                   # Automated unit & integration test suite
├── plan.md                  # Live phase-by-phase execution tracker
└── README.md                # Project documentation & run guide
```

---

## 🚀 Execution Roadmap

- [x] **Phase 0: Repository Setup & Foundational Scaffolding**
- [ ] **Phase 1: Database Engineering & PostgreSQL Migrations**
- [ ] **Phase 2: Backend REST APIs & Audio Upload Hosting**
- [ ] **Phase 3: Real-Time WebSocket Engine & Room Presence**
- [ ] **Phase 4: Spin Wheel State Machine & 9 Edge Cases**
- [ ] **Phase 5: Android Audio Studio using Oboe & Native C++ DSP**
- [ ] **Phase 6: Android Room & Multiplayer Spin Wheel UI**
- [ ] **Phase 7: Docker Packaging & Cloud Deployment (GCP / AWS)**
- [ ] **Phase 8: System Documentation, Diagrams & Demo Preparation**

---

## ⚙️ Tech Stack

- **Audio / Mobile**: Android, Kotlin, Jetpack Compose, C++ NDK, Google Oboe (AAudio / OpenSL ES).
- **Backend**: Node.js, Express, Socket.IO, Multer, Zod.
- **Database**: PostgreSQL (UUIDv4 keys, partial unique indexes, foreign key constraints).
- **DevOps**: Docker, Docker Compose, GitHub Actions, Google Cloud Run / AWS.
