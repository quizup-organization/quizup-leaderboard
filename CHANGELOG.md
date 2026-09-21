## [1.5.1](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.5.0...v1.5.1) (2026-09-21)

### Bug Fixes

* **saga:** await RecordXpCommand so dispatch failures are retried (no XP loss) ([1b7aa0d](https://github.com/quizup-organization/quizup-leaderboard/commit/1b7aa0df94010466dbbf60ed4446cc8b1b0ae575))

## [1.5.0](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.4.5...v1.5.0) (2026-09-21)

### Features

* **leaderboard:** idempotent XP projection by (topic, user, game) ([5d03f28](https://github.com/quizup-organization/quizup-leaderboard/commit/5d03f280f195f7c1a5a653731523f9f197c03d33))

### Bug Fixes

* **observability:** keep readable console logs in local ([56f5372](https://github.com/quizup-organization/quizup-leaderboard/commit/56f537256dfb8125622c37ca1d8a41bbfcef6248))

## [1.4.5](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.4.4...v1.4.5) (2026-09-20)

### Bug Fixes

* **deps:** bump quizup-sdk to 2.1.1 ([204d8f0](https://github.com/quizup-organization/quizup-leaderboard/commit/204d8f00d8ccc5f385cd8dc8b43b8fcb50ecfd54))

## [1.4.4](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.4.3...v1.4.4) (2026-09-20)

### Bug Fixes

* **config:** align prod service URLs with quizup-* names ([f266cc9](https://github.com/quizup-organization/quizup-leaderboard/commit/f266cc955a4b73b4b3244c34fcca3f36ce5484d4))

## [1.4.3](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.4.2...v1.4.3) (2026-09-20)

### Bug Fixes

* **deps:** bump quizup-sdk to 2.1.0 ([a83dbf7](https://github.com/quizup-organization/quizup-leaderboard/commit/a83dbf723e748b29839a66f59a98a99ceaa96fd9))

## [1.4.2](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.4.1...v1.4.2) (2026-09-20)

### Bug Fixes

* **observability:** remove WebSocket and business KPI metrics (consume quizup-sdk 1.4.3) ([348c569](https://github.com/quizup-organization/quizup-leaderboard/commit/348c5697dbe7806dd88e04c32d2a966d4c485bc9))

## [1.4.1](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.4.0...v1.4.1) (2026-09-20)

### Bug Fixes

* **observability:** consume quizup-sdk 1.4.2 (Axon activity metrics fix + Swagger server URL) ([4f48f7f](https://github.com/quizup-organization/quizup-leaderboard/commit/4f48f7feaaab73725957eb354a44b3252d3a7103))

## [1.4.0](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.3.0...v1.4.0) (2026-09-20)

### Features

* **observability:** consume quizup-sdk 1.4.0 (Axon activity metrics) ([df390cf](https://github.com/quizup-organization/quizup-leaderboard/commit/df390cf3a4aab6e8f334fa77917cb3ec61d0089f))

## [1.3.0](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.2.0...v1.3.0) (2026-09-20)

### Features

* **observability:** leaderboard KPIs (XP recorded) + consume quizup-sdk 1.3.0 ([361d466](https://github.com/quizup-organization/quizup-leaderboard/commit/361d466fc4801889f5ba8ed8c7a2e262a6280521))

## [1.2.0](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.1.0...v1.2.0) (2026-09-20)

### Features

* **observability:** consume quizup-sdk 1.2.0 (structured logs + tracing) ([8835658](https://github.com/quizup-organization/quizup-leaderboard/commit/88356584ca32a99276cfb4db8ed47a6e674d9656))

## [1.1.0](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.0.1...v1.1.0) (2026-09-20)

### Features

* **observability:** consume quizup-sdk 1.1.0 (Prometheus metrics) ([4180c81](https://github.com/quizup-organization/quizup-leaderboard/commit/4180c81362039b143d92bd22c0ad0fd6516c7ce7))

## [1.0.1](https://github.com/quizup-organization/quizup-leaderboard/compare/v1.0.0...v1.0.1) (2026-09-19)

### Bug Fixes

* add missing application-prod.yml (datasource, kafka, jwt) ([11810f0](https://github.com/quizup-organization/quizup-leaderboard/commit/11810f0faf1cea081addb28dec61fa59e3813054))

## 1.0.0 (2026-09-19)

### Features

* **ci): add CI/release workflows; fix(maven:** pin inter-service domains to 0.0.1; align parent to quizup-parent 1.0.0 ([d311478](https://github.com/quizup-organization/quizup-leaderboard/commit/d311478ed45aa987fa988c9f6e3581b2b56004dd))
* first commit ([73ce04a](https://github.com/quizup-organization/quizup-leaderboard/commit/73ce04a5e594c28c7ffb90771145ca3aef11fedb))
