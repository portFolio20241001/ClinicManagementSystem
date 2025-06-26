### System Context

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
