# Backend Component Diagram

Smart Clinic の Spring Boot API（バックエンド）をコンポーネント視点で整理した図です。  
3 レイヤ構造（Controller → Service → Repository）＋ Security フィルタ、  
そして 2 種類のデータストア（MySQL / MongoDB）という構成になっています。

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
    （jwtAuthFilter, UserDetailsService,　jwtService）" --> service

    service --> repoJpa
    service --> repoMongo

    repoJpa -->|JDBC| mysql
    repoMongo -->|Driver| mongo