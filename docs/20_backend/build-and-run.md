# Back-end — Build & Run Guide

> **対象**
> *Smart Clinic* の Spring Boot + DB 群（MySQL／MongoDB）を
> Docker でサクッと立ち上げたい開発者・運用担当者向け

---

## 1. 前提

| ツール               | 推奨バージョン | 補足                        |
| ----------------- | ------- | ------------------------- |
| Git               | ≥ 2.x   | リポジトリ取得                   |
| Docker Engine     | ≥ 24.x  | Linux / macOS / Windows 可 |
| Docker Compose v2 | 同上      | `docker compose` が使えること   |
| (任意) make         | ≥ 4.x   | `make up` 等の補助ターゲット       |

> **⚠️ Apple Silicon (M1/M2)** ではクロスビルドにやや時間が掛かります。

---

## 2. ソース取得

```bash
git clone https://github.com/portFolio20241001/ClinicManagementSystem.git
cd ClinicManagementSystem
```

---

## 3. ビルド & 起動

### 3-1. バックエンドイメージをビルド

```bash
docker build -t smart-clinic-backend .
```

→ `smart-clinic-backend:latest` が出来上がります。

### 3-2. コンテナを立ち上げる

```bash
docker compose up -d     # -d = detached
```

| サービス             | イメージ                 | ポート   | 用途                   |
| ---------------- | -------------------- | ----- | -------------------- |
| **clinic-api**   | smart-clinic-backend | 8080  | Spring Boot REST API |
| **clinic-mysql** | mysql:8              | 3306  | RDB                  |
| **clinic-mongo** | mongo:6              | 27017 | ドキュメント DB            |

> ポート・パスワードは `.env` でまとめて変更できます。

---

## 4. よく使う Docker コマンド

| 操作         | コマンド例                                               |
| ---------- | --------------------------------------------------- |
| コンテナ一覧     | `docker ps -a`                                      |
| API 再起動    | `docker restart clinic-api`                         |
| ログ最後 100 行 | `docker logs --tail 100 clinic-api`                 |
| ログを追尾      | `docker logs -f clinic-api`                         |
| MySQL シェル  | `docker exec -it clinic-mysql mysql -u root -proot` |
| Mongo シェル  | `docker exec -it clinic-mongo mongosh`              |

---

## 5. クリーン再構築したいとき

```bash
# ① 全停止 & ネットワーク削除
docker compose down            # ボリュームも消すなら `down -v`

# ② 再ビルド ＆ 再起動
docker compose up -d --build
```

---

## 6. Web アクセス URL（デフォルト）

| 役割              | URL                                                                            |
| --------------- | ------------------------------------------------------------------------------ |
| フロント SPA        | [http://localhost:8080/](http://localhost:8080/)                               |
| Swagger UI      | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| Actuator Health | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) |

---

## 7. 主要環境変数

| 変数                       | 既定値        | 説明               |
| ------------------------ | ---------- | ---------------- |
| `MYSQL_ROOT_PASSWORD`    | `root`     | MySQL root パスワード |
| `MYSQL_DATABASE`         | `clinic`   | 初期 DB 名          |
| `SPRING_PROFILES_ACTIVE` | `dev`      | Spring プロファイル    |
| `JWT_SECRET`             | `changeme` | JWT 署名キー         |

`.env` を編集後は `docker compose up -d --build` で反映してください。

---

## 8. トラブルシューティング

| 症状                                    | 対処                                                   |
| ------------------------------------- | ---------------------------------------------------- |
| **ポート競合**<br>`Address already in use` | `.env` でポートを変更                                       |
| **DB 先に落ちて API だけ再起動ループ**             | `docker compose up -d` でまとめて再起動                      |
| **依存ライブラリ更新後に起動失敗**                   | `mvn clean package` → `docker compose up -d --build` |

---

## 9. Tips

* **make** が入っていれば `make up` / `make down` / `make logs` が使えます。
* IDE デバッグ時は `docker compose stop clinic-api` → `./mvnw spring-boot:run`。

---

© 2025 Smart Clinic Dev Team
