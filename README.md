# SES Job Crawler

日本IT業界のフリーランス・SES案件情報を自動収集・管理するシステム

## 概要

SES Job Crawlerは、複数のフリーランス案件サイトから案件情報を自動収集し、一元管理するためのシステムです。

### 主な機能

- 🔍 **自動案件収集**: SESBoard、Tech Direct などから案件を自動収集
- 📊 **データ管理**: 案件の検索、フィルター、お気に入り機能
- 📬 **通知機能**: 新着案件や条件マッチ案件の通知
- 📈 **統計分析**: スキル別案件数、単価分布などの分析

## 技術スタック

### バックエンド
- Java 17 + Spring Boot 3.2
- MySQL 8.0 / H2 (開発用)
- Spring Data JPA
- Spring Scheduler

### 爬虫モジュール
- Python 3.11
- httpx / BeautifulSoup4
- Playwright (ログイン対応)

### インフラ
- Docker + Docker Compose
- Redis (キャッシュ)

## クイックスタート

### 前提条件

- Docker & Docker Compose
- Java 17+ (ローカル開発用)
- Python 3.11+ (ローカル開発用)

### Docker Compose で起動

```bash
# プロジェクトディレクトリに移動
cd ses-job-crawler

# 全サービスを起動
docker-compose up -d

# ログ確認
docker-compose logs -f
```

起動後:
- バックエンド API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

### ローカル開発

#### バックエンド (Spring Boot)

```bash
cd backend

# H2データベースで起動（開発モード）
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# または Maven で直接実行
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 爬虫モジュール (Python)

```bash
cd crawler

# 仮想環境を作成
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# 依存関係をインストール
pip install -r requirements.txt

# Playwrightブラウザをインストール
playwright install chromium

# 環境変数を設定
cp .env.example .env
# .env を編集

# テスト実行
python main.py test

# 本番実行
python main.py
```

## API エンドポイント

### 案件 API

| Method | Endpoint | 説明 |
|--------|----------|------|
| GET | `/api/jobs` | 案件一覧取得 |
| GET | `/api/jobs/{id}` | 案件詳細取得 |
| POST | `/api/jobs` | 案件登録 |
| POST | `/api/jobs/batch` | 案件一括登録 |
| POST | `/api/jobs/search` | 案件検索 |
| PATCH | `/api/jobs/{id}/status` | ステータス更新 |
| POST | `/api/jobs/{id}/favorite` | お気に入り切替 |
| GET | `/api/jobs/favorites` | お気に入り一覧 |
| GET | `/api/jobs/stats` | 統計情報 |

### 検索パラメータ例

```json
{
  "keyword": "Java",
  "skills": ["Spring", "AWS"],
  "minPrice": 60,
  "maxPrice": 100,
  "location": "東京",
  "remoteType": "FULL",
  "page": 0,
  "size": 20,
  "sortBy": "maxPrice",
  "sortOrder": "desc"
}
```

## プロジェクト構成

```
ses-job-crawler/
├── backend/                  # Spring Boot バックエンド
│   ├── src/main/java/com/sesjob/
│   │   ├── controller/       # REST API
│   │   ├── service/          # ビジネスロジック
│   │   ├── repository/       # データアクセス
│   │   ├── entity/           # JPA エンティティ
│   │   ├── dto/              # データ転送オブジェクト
│   │   └── config/           # 設定
│   └── src/main/resources/
│       └── application.yml
│
├── crawler/                  # Python 爬虫モジュール
│   ├── crawlers/             # 各サイト専用クローラー
│   ├── models/               # データモデル
│   ├── api_client.py         # APIクライアント
│   ├── config.py             # 設定
│   └── main.py               # エントリーポイント
│
├── frontend/                 # React フロントエンド (Phase 3)
│
├── docker-compose.yml
└── README.md
```

## 対応サイト

| サイト | 状態 | ログイン | 備考 |
|--------|------|----------|------|
| SESBoard | ✅ 実装済 | 不要 | 基本的な案件情報を取得 |
| Tech Direct | ✅ 実装済 | 任意 | Playwright使用 |
| フリーランスボード | 🚧 予定 | - | - |
| フリーランスHub | 🚧 予定 | - | - |

## 開発ロードマップ

- [x] Phase 1: 基盤構築（バックエンド、DB、Docker）
- [x] Phase 2: 爬虫モジュール（SESBoard、Tech Direct）
- [ ] Phase 3: フロントエンド（React）
- [ ] Phase 4: 定時実行・通知
- [ ] Phase 5: 拡張機能

## ライセンス

個人利用のみ

---

**注意**: このツールは個人の案件検索を効率化するために作成されたものです。各サイトの利用規約を確認の上、適切にご利用ください。
