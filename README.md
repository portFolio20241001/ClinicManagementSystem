# Smart Clinic – Web-based Clinic Management System

オンライン予約・処方までをワンストップで行える  **小規模クリニック向け OSS** です。

![screenshot](docs/figures/dashboard.png)

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
