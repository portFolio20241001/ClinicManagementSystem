# Smart Clinic – Web-based Clinic Management System

オンライン予約・処方までをワンストップで行える  **小規模クリニック向け OSS** です。

---

## ✨ Features
- 患者（PATIENT） / 医師（DOCTOR） / 管理者（ADMIN）の 3 ロール UI
- ロール別のダッシュボード画面
- JWT 認証 & RBAC
- MySQL + MongoDB の Polyglot Persistence
- コンテナ 1 コマンド (`docker compose up -d`) で起動

## 🚀 Quick Start

```bash
# 1. Clone
git clone https://github.com/your-org/SmartClinic.git
cd SmartClinic

# 2. Build & Run (API + MySQL + MongoDB)
docker compose up -d --build

# 3. Access
#  - Frontend : http://localhost:8080/
#  - Swagger  : http://localhost:8080/swagger-ui/index.html

# 4. 初期データ(デモ用)・・・起動時に自動でInsertされるように設定しております。
#  - Adminユーザ（ユーザ名：adminUser1、PW：addpass1）
#  - Doctorユーザ（ユーザ名：doctorUser1、PW：docpass1）

```
---
## 🐬 システム概要図

```mermaid
%% C4 – Context 図
C4Context
title Smart Clinic – System Context

Person(patient,  "Patient",  "オンラインで診療予約・決済を行う一般利用者")
Person(doctor,   "Doctor",   "診療スケジュールと処方箋を管理する医師")
Person(admin,    "Admin",    "医師情報やクリニックを管理する管理者")

System_Boundary(sc, "Smart Clinic") {
  Container(spa,    "SPA Front-End",   "HTML / JavaScript", "ロール別 UI (Admin / Doctor / Patient)")
  Container(api,    "Spring Boot API", "REST + JWT",        "ビジネスロジックと認証を提供")
  ContainerDb(mysql,"MySQL",           "RDB",               "ユーザー・予約・支払いなど")
  ContainerDb(mongo,"MongoDB",         "Document DB",       "処方箋・操作ログ")
}

%% --- Relationships (全部 Rel で書く) -----------------
Rel(patient, spa, "オンライン予約 / 支払い")
Rel(doctor,  spa, "診療スケジュール管理")
Rel(admin,   spa, "マスタ管理")

Rel(spa, api,   "HTTPS + JWT", "JSON/REST")
Rel(api, mysql, "JPA / JDBC")
Rel(api, mongo, "Spring Data Mongo")

```
---
## 🔥 バックエンド構成図
```mermaid
flowchart TD
    %%──────────────────────────────────────
    %%  Sub-graph : API
    %%──────────────────────────────────────
    subgraph SPRING_BOOT_API["Spring Boot API"]
        direction TB
        controller["REST Controller<br/><span style='font-size:11px'>Spring MVC</span>"]
        security["Security Filter<br/><span style='font-size:11px'>JWT / RBAC</span>"]
        service["Service Layer<br/><span style='font-size:11px'>Business Logic</span>"]
        repoJpa["JPA Repository<br/><span style='font-size:11px'>Spring Data JPA</span>"]
        repoMongo["Mongo Repository<br/><span style='font-size:11px'>Spring Data Mongo</span>"]
    end

    %%──────────────────────────────────────
    %%  Datastores
    %%──────────────────────────────────────
    mysql["MySQL<br/><span style='font-size:11px'>users / admins / patients / doctors / clinic_locations / doctor_available_times / appointments / payments</span>"]
    mongo["MongoDB<br/><span style='font-size:11px'>prescriptions / audit logs</span>"]

    %%──────────────────────────────────────
    %%  Relations（内部）
    %%──────────────────────────────────────
    controller -->|calls| service
    security -- filters --> controller
    security -- "・token認証しROLE取得
                ・POST /api/auth/loginでJWT発行
    （jwtAuthFilter, UserDetailsService,　jwtService）
        ⇒　SecurityContextに認証情報をセット" --> service

    service --> repoJpa
    service --> repoMongo

    repoJpa -->|JDBC| mysql
    repoMongo -->|Driver| mongo


