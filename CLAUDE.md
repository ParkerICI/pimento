# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Pimento is PICI's (Parker Institute for Cancer Immunotherapy) institutional memory system — a RAG (Retrieval Augmented Generation) application. Users ask natural language questions about PICI research and grants; the backend queries an OpenAI vector store of institutional documents and returns AI-generated answers with source citations.

Full-stack Clojure/ClojureScript. Backend uses Ring/Compojure via the `com.hyperphor/way` framework. Frontend uses re-frame (React 18) with shadow-cljs.

## Build & Development Commands

```bash
# Development - start REPL and call:
#   (pimento.core/dev-mode 3000)    ; bypasses OAuth, opens browser

# Frontend dev (live reload):
shadow-cljs watch app

# Production build (uberjar includes cljs release):
lein do clean, uberjar

# Deploy to Heroku:
bin/deploy.sh
```

The uberjar build runs `shadow-cljs release app` automatically via prep-tasks in `project.clj`.

No test suite exists yet.

## Environment Variables

- `OPENAI_API_KEY` — required for all OpenAI calls
- `OAUTH_CLIENT_ID` / `OAUTH_CLIENT_SECRET` — Google OAuth2 (not needed in dev-mode)
- `PORT` — server port
- AWS credentials (standard env vars) — for DynamoDB logging

## Architecture

### Backend (src/clj/pimento/)

- **core.clj** — Server entry point. `dev-mode` bypasses OAuth; `-main` is the production entry.
- **handler.clj** — HTTP routing. The `exec` multimethod dispatches API calls by `:id`. The `:pquery` method is the main RAG query path; `:explain` handles term explanations via Way's data feed mechanism. All queries are logged to DynamoDB in a background future.
- **openai.clj** — OpenAI API client. Uses the Responses API (`/v1/responses`) with `gpt-4.1` for RAG queries and `gpt-5-mini` for chat completions. The vector store ID is hardcoded as `pici-vector-store`. `rag-response` does file_search against this store. `explain-term` generates explanations at configurable complexity levels.
- **ingest.clj** — Utilities for converting grant documents (TSV/CSV) to markdown for upload to OpenAI.
- **logging/dynamo.clj** — DynamoDB client for the `pimento_log` table (query/response audit log).

### Frontend (src/cljs/pimento/)

- **core.cljs** — App initialization, header, tab layout via Way UI components.
- **main.cljs** — Main tab layout: query box (left) → response (center) → explanation (right). Wires components together using the pin system.
- **qbox.cljs** — Query input textarea with example query dropdown. Dispatches to `/api/exec?type=qbox&id=pquery`.
- **markdown.cljs** — Renders markdown responses using markdown-it (with KaTeX, footnotes). Supports text selection for term lookup and copy-to-clipboard as markdown.
- **explain.cljs** — Fetches and displays term explanations at different audience levels.
- **eli.cljs** — Pre-built "Explain Like I'm..." grid with hardcoded terms and audience types.
- **pins.cljs** — Re-frame-based pin/wire system for inter-component data flow (output pin of one component feeds input pin of another).

### Shared (src/cljc/)

Shared code between backend and frontend.

### Key Framework: com.hyperphor/way

Way provides the web application skeleton: server startup, OAuth middleware, HTML page generation, data feeds (used for the `:explain` endpoint), config management (`resources/config.edn`), and frontend UI components (modals, flash, tabs, loading spinner).

## Data Flow

1. User submits query in qbox → AJAX to `/api/exec?id=pquery`
2. Handler appends "Focus on PICI sponsored projects and network members." to query
3. `openai/rag-response` calls OpenAI Responses API with file_search tool against the PICI vector store
4. Response logged to DynamoDB, text returned to frontend
5. Frontend renders markdown; user can select terms for ELI explanations

## Configuration

- **project.clj** — Leiningen config, Clojure dependencies, uberjar build
- **shadow-cljs.edn** — ClojureScript build config, browser target, re-frame-10x devtools
- **package.json** — npm dependencies (React 18, MUI, markdown-it, KaTeX, react-draggable)
- **resources/config.edn** — Way framework config (OAuth, app title, CSS, Heroku SSL)

## Deployment

Heroku with JDK 24. `bin/deploy.sh` builds uberjar and deploys via `heroku deploy:jar`. The deployed app is at the URL in config.edn's OAuth callback.
