### Container Diagram

```mermaid
%%=============================================================
%% C4 – Containers（Smart Clinic）
%%=============================================================
C4Container
title Smart Clinic – Containers

Person(patient, "Patient", "オンライン予約・決済の利用者")
Person(doctor,  "Doctor",  "診療スケジュールと処方箋を管理")
Person(admin,   "Admin",   "医師情報やクリニックを管理")

System_Boundary(sc, "Smart Clinic") {

  Container(spa,   "SPA",            "HTML + JS",     "ロール別 UI")
  Container(api,   "Spring Boot",    "REST + JWT",    "Controller / Service")
  ContainerDb(mysql,"MySQL",         "RDB",           "ユーザー・予約")
  ContainerDb(mongo,"MongoDB",       "Document DB",   "処方箋・ログ")
}

Rel(patient, spa, "HTTPS")
Rel(doctor,  spa, "HTTPS")
Rel(admin,   spa, "HTTPS")

Rel(spa, api,   "JSON + JWT",        "HTTPS")
Rel(api, mysql, "JPA",               "JDBC")
Rel(api, mongo, "Spring Data Mongo", "Driver")
