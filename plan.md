# ROXSTAR: Voice Draft, Real-Time Room & Spin Wheel System
## Complete Phase-by-Phase Master Plan & Execution Tracker

> **Project Goal**: Build an Android voice-draft feature using Oboe native audio, a Node.js real-time room service with WebSockets, and a multiplayer spin wheel with robust concurrency handling and cloud deployment (200/200 points).
> **Repository**: [https://github.com/ayush-init/Andriod-App.git](https://github.com/ayush-init/Andriod-App.git)
> **Execution Strategy**: Phased execution. Each phase is built cleanly, verified manually by the user, checked off in this plan, and pushed to GitHub.

---

## Score Allocation Breakdown (200 Points)

| Section | Domain | Points | Status |
| :--- | :--- | :--- | :--- |
| **Section A** | Android Audio Studio using Oboe (Recording, Drafts, Echo DSP) | 40 pts | ⏳ Pending |
| **Section B** | Room Management & Real-Time WebSocket Communication | 40 pts | ⏳ Pending |
| **Section C** | Spin Wheel Concurrency Logic, State Machine & Edge Cases | 50 pts | ⏳ Pending |
| **Section D** | Backend Architecture, PostgreSQL Database & REST APIs | 30 pts | ⏳ Pending |
| **Section E** | Cloud Deployment, Docker & GitHub Actions CI/CD (AWS / GCP) | 20 pts | ⏳ Pending |
| **Section F** | Technical Documentation, Architecture Diagrams & OpenAPI Specs | 20 pts | ⏳ Pending |
| **Total** | **Target: Level 5 Exceptional** | **200 pts** | ⏳ In Progress |

---

## Architectural Decisions & Principles

1. **Android App vs Browser Test Client**:
   - The **Android App (`/android-app`)** is the primary production application containing the complete user experience: local recording with Oboe NDK C++, Echo DSP, draft management, room participation, draft sharing/playback, and the interactive Spin Wheel UI.
   - The **Browser Test Client (`/backend/public/test-client.html`)** is a lightweight testing harness designed to simulate Player 2 and Player 3 during multi-client spin wheel verification and demonstration, eliminating the need to run 3 heavy Android emulators.
2. **Audio File Architecture & Hosting**:
   - Audio is captured locally via Oboe and saved as a `.wav` file.
   - Android uploads the draft via `POST /api/drafts/upload` (multipart/form-data).
   - Backend saves it into `/backend/uploads` (served statically at `/uploads/<id>.wav`) and records the canonical hosted URL in the PostgreSQL `drafts` table.
   - When a draft is shared (`POST /api/rooms/:id/drafts`), the backend broadcasts `draft_shared` with the streaming URL, enabling any participant to stream and listen.
3. **Cloud Hosting**:
   - Strictly targeted to **Google Cloud Platform (Cloud Run)** or **AWS (EC2 / App Runner)** to fully comply with Section E and the submission checklist.
4. **Authoritative Concurrency & State Management**:
   - Application-layer mutex guards against duplicate spin triggers in Node.js event loop.
   - Database-layer partial unique index `idx_one_active_spin_per_room ON spins (room_id) WHERE status IN ('WAITING', 'RUNNING')` guarantees zero race conditions.
5. **Server Restart & Crash Recovery**:
   - On server startup, an atomic startup routine transitions any interrupted spins in PostgreSQL from `RUNNING` to `ABORTED` with reason `SERVER_RESTART_ABORT`, maintaining state integrity and preventing zombie games.

---

## Master Architecture & Directory Structure

```text
/android-app/             # Native Android project (Kotlin + Jetpack Compose)
  ├── app/src/main/
  │   ├── cpp/            # Native C++ Oboe Audio Pipeline & Echo DSP Filter
  │   ├── java/.../       # Compose UI, Audio Recorder ViewModel, Socket Client
/native-audio/            # Shared native audio C++ sources & CMake definitions
/backend/                 # Node.js + Express REST & Socket.IO server
  ├── src/
  │   ├── controllers/    # Room, Spin, Draft, User controllers
  │   ├── services/       # Authoritative Spin State Machine & Room Engine
  │   ├── sockets/        # WebSocket event handlers (presence, spin, draft)
  │   ├── db/             # PostgreSQL connection pool and queries
  ├── uploads/            # Hosted audio files volume
  ├── public/             # Browser test client for multi-user simulation
/database/                # PostgreSQL schema migrations, indexes, seed data
/infrastructure/          # Dockerfile, docker-compose.yml, cloud deployment assets
/.github/workflows/       # GitHub Actions CI/CD automation pipeline
/docs/                    # System diagrams (Architecture, Audio, State Machine, OpenAPI)
/tests/                   # Unit and integration test suite
plan.md                   # Live phase-by-phase execution tracker (this document)
README.md                 # Setup, manual verification guide & submission document
```

---

## Detailed Phase-by-Phase Roadmap

### 📦 Phase 0: Repository Setup & Foundational Scaffolding
- [x] Initialize local Git repository on branch `main`
- [x] Link remote `origin` to `https://github.com/ayush-init/Andriod-App.git`
- [x] Scaffold folder structure (`/android-app`, `/native-audio`, `/backend`, `/database`, `/infrastructure`, `/docs`, `/tests`)
- [x] Create root `.gitignore` tailored for Node.js, Android/NDK, environment files, and audio assets
- [x] Create initial `README.md`
- [x] Commit and push initial scaffolding to GitHub
- 🔍 **Manual Verification Checklist**:
  1. Open [https://github.com/ayush-init/Andriod-App.git](https://github.com/ayush-init/Andriod-App.git) on GitHub and verify clean folder structure and initial commit.

---

### 🗄️ Phase 1: Database Engineering & PostgreSQL Migrations (Section D2 - 15 pts)
- [x] Design PostgreSQL schema with strict constraints, foreign keys, and indexes:
  - `users`: ID (UUID), username, display_name, avatar_url, created_at
  - `rooms`: ID (UUID), title, owner_id, status (ACTIVE/CLOSED), max_participants, created_at, closed_at
  - `room_members`: ID (UUID), room_id, user_id, role (HOST/PARTICIPANT), is_online, joined_at, left_at (Unique constraint on `(room_id, user_id)`)
  - `drafts`: ID (UUID), user_id, title, duration_ms, file_url (hosted path), effect_applied, created_at
  - `spins`: ID (UUID), room_id, initiated_by, status (WAITING/RUNNING/COMPLETED/ABORTED), winner_id, prize_points, created_at, completed_at
  - `spin_participants`: ID (UUID), spin_id, user_id, seat_order, is_eliminated, elimination_round, eliminated_at
  - `spin_events`: ID (UUID), spin_id, event_type, payload_json, sequence_no, created_at
- [x] Add partial unique index: `idx_one_active_spin_per_room ON spins (room_id) WHERE status IN ('WAITING', 'RUNNING')`
- [x] Create migration scripts in `/database/migrations/`
- [x] Implement database client pool supporting remote PostgreSQL (Neon/Supabase/Render/AWS RDS/Local)
- [x] Create a DB migration & health runner script (`npm run db:migrate`)
- [x] Commit and push Phase 1 to GitHub
- 🔍 **Manual Verification Checklist**:
  1. Run migration with your PostgreSQL database URL (`npm run db:migrate`).
  2. Verify all 7 tables, indexes, and relations are created in PostgreSQL (`npm run db:test`).

---

### ⚡ Phase 2: Backend REST APIs & Audio Upload Hosting (Section B1 & D1, D3 - 35 pts)
- [ ] Setup Node.js backend with Express, CORS, and structured logging
- [ ] Implement Health/Readiness endpoint (`GET /health`) with live DB ping
- [ ] Implement User identity endpoint (`POST /api/users`)
- [ ] Implement Audio Draft Upload & Hosting:
  - `POST /api/drafts/upload` (multipart/form-data upload using `multer`)
  - Static file serving at `/uploads`
  - Persist draft record in PostgreSQL `drafts` table with full file URL
- [ ] Implement Room Management REST APIs:
  - `POST /api/rooms` - Create Room (creates room, adds host as first member)
  - `GET /api/rooms` - List Active Rooms
  - `GET /api/rooms/:id` - Get Room State & Participant List
  - `POST /api/rooms/:id/join` - Join Room (validates room capacity & status)
  - `POST /api/rooms/:id/leave` - Leave Room
  - `POST /api/rooms/:id/drafts` - Share Draft with Room
  - `GET /api/rooms/:id/drafts` - List Shared Drafts in Room
- [ ] Implement input validation (zod), error handling middleware, and idempotency guards
- [ ] Write integration test suite (`tests/integration/room_api.test.js`)
- [ ] Commit and push Phase 2 to GitHub
- 🔍 **Manual Verification Checklist**:
  1. Open browser/cURL to `GET /health` -> returns `{ status: "ok", db: "connected" }`.
  2. Test `POST /api/drafts/upload` with a sample audio file -> returns hosted URL.
  3. Call `POST /api/rooms` and `POST /api/rooms/:id/join` -> verify room state.

---

### 🌐 Phase 3: Real-Time WebSocket Engine & Room Presence (Section B2 - 20 pts)
- [ ] Integrate Socket.IO server into Node.js backend
- [ ] Implement presence tracking & connection lifecycle:
  - `user_joined`: Broadcast updated participant list when a user joins
  - `user_left`: Broadcast departure and clean presence on leave/disconnect
  - `draft_shared`: Real-time notification with audio streaming URL when a draft is shared
  - `room_state`: Deliver full snapshot upon initial connect or reconnection
- [ ] Build interactive Browser-based Socket Test Client (`/backend/public/test-client.html`):
  - Connect as User A, B, or C
  - View live participant list
  - Listen to shared audio drafts directly in browser
  - Ready for multi-client spin wheel simulation
- [ ] Commit and push Phase 3 to GitHub
- 🔍 **Manual Verification Checklist**:
  1. Open `/test-client.html` in two separate browser tabs (Client A and Client B).
  2. When Client B joins Room 1, Client A instantly sees `user_joined` event and updated list.
  3. When Client B closes tab, Client A instantly sees `user_left` event.
  4. Share a draft in Tab A -> Tab B immediately receives `draft_shared` and can play it.

---

### 🎡 Phase 4: Spin Wheel State Machine & 9 Edge Cases (Section C - 50 pts)
- [ ] Implement authoritative Server-Side Spin State Machine:
  - Transitions: `WAITING` ➔ `RUNNING` ➔ `COMPLETED` / `ABORTED`
  - Rule: 3 to 20 eligible participants required to start
  - Rule: Only room owner/admin can start the spin
  - Rule: Exactly one active spin allowed per room
  - Rule: High-precision 5-second elimination timer
  - Rule: Last remaining participant is declared winner and awarded virtual points
- [ ] Implement WebSocket Spin Events:
  - `spin_started`: Initial players, sequence ID, round info
  - `user_eliminated`: Emitted every 5 seconds with eliminated user & remaining pool
  - `winner_announced`: Emitted when 1 player remains, with final winner details
- [ ] Implement All 9 Required Edge Cases (Section C4 - 15 pts):
  1. Duplicate start requests blocked (idempotency guard)
  2. Simultaneous room joins handled without state race conditions
  3. User departure during spin (auto-eliminated gracefully without halting the game)
  4. User reconnect during spin (receives current round state and active timer)
  5. Admin disconnects mid-spin (spin continues autonomously to completion)
  6. Insufficient players (< 3) rejected with descriptive error
  7. Last remaining players disconnect -> Spin transitions to `ABORTED`
  8. Duplicate event sequence IDs prevent replay
  9. Server restart recovery: startup script marks uncompleted spins as `ABORTED`
- [ ] Write unit tests for state machine (`tests/unit/spin_state_machine.test.js`)
- [ ] Commit and push Phase 4 to GitHub
- 🔍 **Manual Verification Checklist**:
  1. Connect 3 browser test clients to a room.
  2. Admin clicks "Start Spin" -> all 3 clients receive `spin_started`.
  3. Every 5 seconds, watch one user eliminated in real-time until 1 winner is crowned (`winner_announced`).
  4. Try starting with only 2 clients -> verify error returned.
  5. Close 1 client during spin -> verify game continues smoothly.

---

### 🎙️ Phase 5: Android Audio Studio using Oboe & Native C++ DSP (Section A - 40 pts)
- [ ] Scaffold native Android project with C++ NDK support in `/android-app`
- [ ] Integrate Google Oboe library for low-latency audio capture & playback
- [ ] Native C++ Audio Pipeline (`/android-app/app/src/main/cpp/` and `/native-audio/`):
  - Microphone input stream lifecycle (Start, Stop, Cancel)
  - Real-time Echo DSP Effect: Circular delay buffer with adjustable feedback and decay
  - Native PCM buffer processing and WAV encoder with standard 44.1kHz / 16-bit PCM RIFF header
- [ ] Android UI & Lifecycle Layer:
  - Runtime `RECORD_AUDIO` permission handling with graceful failure states
  - Recording Studio Screen: Start, Stop, Cancel buttons, recording timer, Effect toggle (Clean vs Echo)
  - Drafts Management Screen (Section A2):
    - Save recording as local Draft with title and timestamp
    - List drafts with name, creation time, and formatted duration
    - Play and Delete draft functionality
- [ ] Commit and push Phase 5 to GitHub
- 🔍 **Manual Verification Checklist**:
  1. Launch Android app, grant microphone permission.
  2. Record a 5-second voice sample with Echo effect enabled.
  3. Tap Stop -> Verify draft appears in the Drafts list.
  4. Tap Play -> Hear voice recording with clear echo effect.
  5. Tap Delete -> Verify draft is removed.

---

### 📱 Phase 6: Android Room & Multiplayer Spin Wheel UI (Section B & C Frontend)
- [ ] Integrate Socket.IO Android client library
- [ ] Connect Android Draft Studio to Backend:
  - Upload local draft to backend (`POST /api/drafts/upload`)
- [ ] Build Room UI:
  - Create Room & Browse/Join Room screen
  - Live Room view: participant avatars, host badge, real-time presence indicators
  - "Share Draft" button: select a saved draft from Phase 5 and broadcast to room
  - Audio Player for shared drafts inside room
- [ ] Build Spin Wheel Multiplayer View:
  - Visual Wheel / Participant Circle showing 3-20 active players
  - "Start Spin" button (enabled only for Room Host when eligible players >= 3)
  - Real-time 5-second countdown animation for eliminations
  - Visual elimination animation (fade/cross out eliminated players)
  - Winner Celebration Dialog showing winner avatar and virtual points
  - Reconnection banner with automatic state sync
- [ ] Commit and push Phase 6 to GitHub
- 🔍 **Manual Verification Checklist**:
  1. Open Android App as User A and Browser Client as User B & C.
  2. User A creates Room; User B & C join. Android app shows all 3 users in real-time.
  3. User A shares a recorded voice draft -> B and C receive `draft_shared` and can play it.
  4. User A starts the Spin Wheel -> Android UI animates 5-second eliminations until winner is crowned!

---

### 🐳 Phase 7: Docker Packaging & Cloud Deployment on AWS / GCP (Section E - 20 pts)
- [ ] Multi-stage production `Dockerfile` for Node.js backend
- [ ] `docker-compose.yml` for unified local testing (Backend + PostgreSQL)
- [ ] GitHub Actions CI/CD pipeline (`.github/workflows/ci.yml`):
  - Dependency installation
  - Automated tests execution
  - Docker build verification
- [ ] Cloud deployment configuration for **Google Cloud Run** or **AWS (EC2 / App Runner)**
- [ ] Environment configuration guide and secrets handling documentation
- [ ] Health check and rollback procedures documented
- [ ] Commit and push Phase 7 to GitHub
- 🔍 **Manual Verification Checklist**:
  1. Run `docker compose up --build` -> verify system boots cleanly locally.
  2. Deploy backend to Cloud provider -> verify public hosted endpoint `https://.../health` responds with `200 OK`.
  3. Connect Android app or browser to the cloud endpoint.

---

### 📑 Phase 8: System Documentation, Diagrams & Demo Preparation (Section F - 20 pts)
- [ ] `README.md` with complete setup, installation, environment variables, and run guide
- [ ] System Architecture Diagram (Mermaid) in `/docs/architecture/system_architecture.md`
- [ ] Audio Flow Diagram (Microphone -> Oboe -> Echo DSP -> WAV -> Draft) in `/docs/architecture/audio_flow.md`
- [ ] Room & WebSocket Event Flow Diagram in `/docs/architecture/websocket_events.md`
- [ ] Spin State Machine & Sequence Diagram in `/docs/architecture/spin_state_machine.md`
- [ ] Edge Cases, Assumptions & Trade-offs document in `/docs/architecture/edge_cases_and_tradeoffs.md`
- [ ] OpenAPI 3.0 / Swagger documentation in `/docs/api/openapi.yaml`
- [ ] Demonstration Checklist (Section 9) & Candidate Submission Checklist (Section 10) completed
- [ ] Commit and push final documentation and deliverables to GitHub
- 🔍 **Manual Verification Checklist**:
  1. Review repository to verify all deliverables, code, and documentation match the 200-point rubric.

---

## Current Status & Next Immediate Step

- **Current Active Phase**: **Phase 2: Backend REST APIs & Audio Upload Hosting**
- **Status**: READY TO EXECUTE
- **Completed**: 
  - Phase 0: Repository scaffolding, git configuration, remote push to GitHub
  - Phase 1: Database Engineering & PostgreSQL Migrations (7 tables, partial unique index, migration scripts, connection pool tested with Neon DB)
