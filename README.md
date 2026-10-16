# ITScan Telegram Bot

Telegram bot with RAG (Retrieval-Augmented Generation) that answers IT-related questions using articles parsed from Habr RSS feeds.

📖 In Russian: [перевод на русский](README.ru.md)

## 📋 Overview

ITScan is a learning project built with **Java 21 + Spring Boot 4 + Spring AI**. The bot:

- parses **RSS feeds** from Habr (career, research, startups, statistics hubs),
- splits articles into **chunks** and stores in **pgvector**,
- answers user questions using **RAG** (`retrieval → prompt → LLM`),
- runs on **Telegram** via long polling,
- supports **Telegram Stars** payments.

The project is built to reinforce **RAG** concepts and practice **Spring AI**, **pgvector**, **Docker**, and **Telegram Bot API**.

## 🏗️ Architecture

```
              ┌──────────────────────┐
              │   Habr RSS Feeds     │
              └──────────┬───────────┘
                         │ HTTP
                         ▼
              ┌──────────────────────┐
              │  RssParserService    │  ← parse XML → Document
              │  (scheduled, 12h)    │
              └──────────┬───────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │   VectorService      │  ← chunk + embed
              │   TokenTextSplitter  │
              └──────────┬───────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │   PgVectorStore      │  ← vector_store table
              │   (Postgres + HNSW)  │
              └──────────────────────┘

              ┌──────────────────────┐
              │   Telegram User      │
              └──────────┬───────────┘
                         │ long polling
                         ▼
              ┌──────────────────────┐
              │   MainBot            │  ← update consumer
              │   CommandHandler     │
              └──────────┬───────────┘
                         │
         ┌───────────────┼────────────────┐
         │               │                │
         ▼               ▼                ▼
   ┌──────────┐   ┌──────────┐    ┌──────────────┐
   │ /start   │   │ /ask     │    │ /buy, /profile│
   │ /profile │   │ → RAG    │    │               │
   └──────────┘   └────┬─────┘    └──────────────┘
                       │
                       ▼
              ┌──────────────────────┐
              │   RagService         │  ← ChatClient + Advisor
              │   (QuestionAnswer)   │
              └──────────┬───────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │   LM Studio (LLM)    │  ← OpenAI-compatible API
              │   + Embedding model  │
              └──────────────────────┘
```

**Two PostgreSQL instances:**

- `postgres-user` - users, states, request counters (JPA).
- `pgvector-itscan` - vector store for RAG (Spring AI).

Both live in the Docker `dbnet` network. Only the bot's HTTP port is exposed locally.

## 🚀 Technology Stack

**Backend**

- Java 21 - core language
- Spring Boot 4 - application framework
- Spring AI - ChatClient, VectorStore, TokenTextSplitter, QuestionAnswerAdvisor
- Spring Data JPA - user storage
- Spring Scheduling - RSS parsing on a fixed rate
- Spring Async - non-blocking message handling via a custom thread pool
- ROME - RSS/Atom feed parsing
- Jsoup - HTML sanitization

**AI**

- LM Studio - local LLM server with OpenAI-compatible REST API
- Chat model: `qwen/qwen3-8b`
- Embedding model: `text-embedding-nomic-embed-text-v1.5` (768 dims)
- pgvector - Postgres extension for vector storage

**Telegram**

- `telegrambots` (long polling) - bot API
- Telegram Stars (`XTR`) - payments for extra requests

**Databases**

- PostgreSQL - user data
- PostgreSQL + pgvector - RAG vector store

**DevOps**

- Docker - containerization
- Docker Compose - orchestration of two DBs + bot

**Testing**

- JUnit 5
- Mockito

## 🧩 Modules

| Module                | Purpose                                                       |
|-----------------------|---------------------------------------------------------------|
| **RssParserService**  | Parses RSS feeds into `Document`s with `url` metadata         |
| **RssSchedule**       | Triggers parsing every 12h (configurable)                     |
| **VectorService**     | Chunks documents via `TokenTextSplitter` and saves to store   |
| **RagService**        | Builds prompt, calls LLM through `ChatClient` with advisor    |
| **UserService**       | Manages users, request counters, and states                   |
| **CommandHandler**    | Routes Telegram updates to the appropriate `Command`          |
| **MainBot**           | Long-polling consumer, entry point for all updates            |

## 🛠️ Quick Start

### Prerequisites

- Docker
- Docker Compose
- LM Studio (running locally with a chat + embedding model loaded)

### Run

```bash
# 1. Start LM Studio, load models, start server at http://localhost:1234/v1
# 2. Export secrets
export TOKEN_BOT=<your-telegram-bot-token>
export API_KEY_AI=<your-lm-studio-key-or-dummy>

# 3. Build and run
docker compose up --build
```

On first start:

- the bot registers with Telegram via long polling,
- the scheduler parses RSS feeds after a 25s delay,
- vectors are stored in `pgvector-itscan`.

## 📚 Bot Commands

| Command    | Description                                 | Access              |
|------------|---------------------------------------------|---------------------|
| `/start`   | Greeting and instructions                   | Public              |
| `/ask`     | Ask a question (requires ≥1 request left)   | Authenticated(auto  |
| `/profile` | Show request balance and status             | Authenticated(auto) |
| `/buy`     | Buy +1 request via Telegram Stars           | Authenticated(auto) |

Users are auto-registered on their first message with a base request(ai ask) count for ai (configurable, default 3).

## 🔄 RAG Pipeline

```
RSS → Parser → Document(url) → TokenTextSplitter → chunks
     → EmbeddingModel → pgvector

Question → EmbeddingModel → similaritySearch (top-K, threshold)
         → QuestionAnswerAdvisor → ChatClient → LLM → answer
```

**Implementation notes:**

- `TokenTextSplitter` is configured via `RagProperties` (chunk size, min chunk size, keep separator).
- `PgVectorStore` uses **cosine distance** for fast approximate search.
- `QuestionAnswerAdvisor` injects retrieved chunks into the prompt.
- The scheduler runs every 12h (configurable via `parser.fixed-rate`).
- On successful answer, the user's request counter is decremented.

## ✨ Features

- 💬 **Telegram chat** - long polling, no webhooks needed
- 🤖 **RAG over Habr** - answers grounded in parsed articles
- 🧠 **Context persistence** - retrieved chunks are added to the prompt
- 💳 **Telegram Stars payments** - buy extra requests with `XTR`
- 🔒 **Per-user rate limiting** - request counter in `User`
- ⚙️ **Configurable** - chunk size, top-K, threshold, scheduler rate
- 🐳 **Docker-first** - one command to run everything

## 🔒 Security

- **Two isolated databases** - user data and vectors are separated.
- **Docker network isolation** - only the bot's HTTP port is exposed.
- **Secrets via env vars** - `TOKEN_BOT`, `API_KEY_AI`.
- **No user passwords** - authentication is delegated to Telegram.
- **Internal endpoints not exposed** - the bot talks to DBs directly, no public REST API for RAG.

## ⚙️ Configuration

Key properties in `application.yml`:

| Property                       | Default | Description                          |
|--------------------------------|---------|--------------------------------------|
| `telegram.bot.base-count-request` | 3    | Free requests per new user           |
| `parser.init-delay`            | 25000   | Delay before first RSS parse (ms)    |
| `parser.fixed-rate`            | 43200000| Parse interval (12h)                 |
| `rag.top-k`                    | 5       | Number of retrieved chunks           |
| `rag.similarity-threshold`     | 0.7     | Minimum cosine similarity            |
| `rag.chunk-size`               | 500     | Tokens per chunk                     |
| `rag.embed-dimensions`         | 768     | Embedding dimensions (must match model) |

Secrets are provided via environment variables:

- `TOKEN_BOT` - Telegram bot token
- `API_KEY_AI` - LM Studio API key (any value for local)
- `POSTGRES_DB_*` - user DB credentials
- `PGVECTOR_DB_*` - vector DB credentials

## 📝 Note

An educational project for reinforcing **RAG** concepts and practicing **Spring AI**, **pgvector**, and **Telegram Bot API**.
