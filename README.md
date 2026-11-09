# Pimento

Pimento is the institutional memory system of the [Parker Institute for Cancer Immunotherapy](https://www.parkerici.org) (PICI). It is a retrieval-augmented generation (RAG) web app. You ask a question in natural language, Pimento searches a corpus of internal documents (grants, project reviews, meeting summaries, site reports), and it returns an LLM-written answer with citations.

Pimento also has an "Explain Like I'm…" panel. Select any term in an answer to get an explanation pitched at a chosen audience, such as a five-year-old or a scientist from another field.

> **Note:** The code is open source, but the document corpus is not. To run Pimento against your own documents, you need your own OpenAI vector store (see [Using your own corpus](#using-your-own-corpus)).

## Architecture

Pimento is written entirely in Clojure and ClojureScript.

- **Backend** (`src/clj/pimento/`): a Ring/Compojure server built on [`com.hyperphor/way`](https://github.com/hyperphor/way).
  - `handler.clj` dispatches API calls.
  - `openai.clj` uses the OpenAI Responses API with the `file_search` tool against a vector store. LLM access goes through [`com.hyperphor/ellum`](https://github.com/hyperphor/ellum).
  - Queries are sent in background mode and polled, which avoids HTTP timeouts on long answers.
  - Every query and response is logged to a DynamoDB table (`logging/dynamo.clj`).
- **Frontend** (`src/cljs/pimento/`): re-frame and React 18, built with shadow-cljs. It has a query box, a markdown renderer for answers (markdown-it with KaTeX and footnotes), and the term-explanation panel.
- **Ingest** (`src/clj/pimento/ingest.clj`): REPL helpers for converting grant exports (TSV/CSV) to markdown and uploading them to OpenAI. The running app does not use them.

### Request flow

1. The user submits a query, and the browser calls `/api/exec?id=pquery`.
2. The backend starts a background OpenAI response that uses `file_search` over the vector store.
3. The frontend polls `/api/exec?id=poll` until the response completes.
4. The answer is rendered as markdown and logged to DynamoDB.

## Prerequisites

- JDK 21+
- [Leiningen](https://leiningen.org/)
- Node.js and npm (for shadow-cljs and the frontend dependencies)
- An OpenAI API key
- AWS credentials with access to a DynamoDB table named `pimento_log` in `us-east-1` (used for query logging)
- For production: a Google OAuth 2.0 client (web application type)

## Configuration

Secrets are read from environment variables and are never stored in the repository.

| Variable | Purpose |
|---|---|
| `OPENAI_API_KEY` | Required for all LLM calls |
| `OAUTH_CLIENT_ID` | Google OAuth client ID (not needed in dev mode) |
| `OAUTH_CLIENT_SECRET` | Google OAuth client secret (not needed in dev mode) |
| `OAUTH_CALLBACK` | OAuth callback URL, e.g. `https://your-host/oauth2/google/callback` |
| `PORT` | HTTP port (production) |
| `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` | DynamoDB logging (standard AWS credential chain) |

Non-secret app settings (title, CSS, OAuth wiring) are in `resources/config.edn`.

## Development

```bash
npm install
npx shadow-cljs watch app      # frontend, with live reload
```

Then, in a Clojure REPL (`lein repl`):

```clojure
(require 'pimento.core)
(pimento.core/dev-mode 3000)   ; starts the server without OAuth and opens a browser
```

## Building and deploying

```bash
lein do clean, uberjar         # also runs `shadow-cljs release app`
java -jar target/uberjar/pimento-standalone.jar
```

The reference deployment runs on Heroku. `bin/deploy.sh` builds the uberjar and pushes it with `heroku deploy:jar`. Before the first deploy, set the environment variables above with `heroku config:set`.

## Using your own corpus

1. Upload your documents to OpenAI and create a vector store. The helpers in `openai.clj` (`api-upload`, `make-vector-store`, `store-add-file`) are meant to be called from the REPL.
2. Set `pici-vector-store` in `src/clj/pimento/openai.clj` to your vector store ID.
3. Adjust the query suffix in `handler.clj` (`query-extra`), the example queries in `main.cljs`, and the about text in `core.cljs` to fit your organization.

## License

Copyright © 2025-2026 Parker Institute for Cancer Immunotherapy.

Licensed under the [Apache License, Version 2.0](LICENSE).
