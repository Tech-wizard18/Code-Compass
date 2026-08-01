# CodeCompass 🧭

CodeCompass is an AI-powered learning companion that lets you explore any public GitHub repository through natural language chat. Paste a repo URL, and ask questions like *"how does login work?"* — get answers grounded in the actual code, with exact file and line citations.

Think of it as ChatGPT/Claude, but scoped to a specific codebase you choose.

**🔴 Live demo:** [code-compass-5r07.onrender.com](https://code-compass-5r07.onrender.com)
*(hosted on a free tier — the first request after a period of inactivity can take 30–60 seconds to wake up)*

## Why CodeCompass?

- **Grounded, not hallucinated** — every answer is backed by a RAG (Retrieval-Augmented Generation) pipeline that retrieves real code chunks before generating a response.
- **Persistent conversations** — like Claude/ChatGPT, your chat history is saved per repo and per user, so you can pick up where you left off, rename, or delete conversations at any time.
- **Cited, verifiable answers** — every response links back to the exact file and line numbers it drew from, so you can check the source yourself instead of taking the answer on faith.

## Tech Stack

**Backend**
- Java 17, Spring Boot 3, Maven
- JGit for repo cloning
- JavaParser for AST-based code chunking
- Google Gemini API (`gemini-embedding-001` for embeddings, `gemini-2.5-flash` for generation)
- PostgreSQL 16 + pgvector for vector similarity search
- Spring Security + JWT for stateless authentication

**Frontend**
- React + Vite
- Tailwind CSS
- React Router
- react-hot-toast

**Deployment**
- [Neon](https://neon.tech) — serverless Postgres 16 with pgvector, free tier
- [Render](https://render.com) — Docker-based web service, free tier
- Frontend build is bundled directly into the Spring Boot jar as static resources, so the whole app (API + UI) is served from a single origin — no separate frontend host, no CORS to manage in production

## How It Works

1. **Index a repo** — paste a public GitHub URL. The backend clones it, filters out sensitive files, chunks the code at class/method level using AST parsing, embeds each chunk, and stores it in Postgres with pgvector.
2. **Ask a question** — your question is embedded the same way, and pgvector's cosine similarity search finds the most relevant code chunks.
3. **Get a grounded answer** — the retrieved chunks + your question are sent to an LLM, which generates an answer citing the exact file and line numbers it used.

## Features

- 🔍 Index any public GitHub repository, with automatic re-indexing on repeat submissions
- 💬 Natural language Q&A grounded in real code, with expandable file/line citations
- 🔐 JWT authentication — register/login with username or email, with automatic session expiry handling
- 🗂️ Persistent, multi-turn conversations per repo — browse, resume, rename, or delete past conversations from a sidebar
- 📱 Fully responsive — works on desktop and mobile, with a collapsible slide-over sidebar on smaller screens
- 🎨 Clean, modern chat interface inspired by Claude's UI

## Project Structure

```
Code-Compass/
├── backend/     # Spring Boot API — indexing, RAG pipeline, auth
└── frontend/    # React UI — chat interface, auth pages
```

## Running Locally

### Prerequisites
- Java 17, Maven
- Node.js + npm
- PostgreSQL 16 with the `pgvector` extension
- A free [Google Gemini API key](https://aistudio.google.com/) (no credit card required)

### Backend

`application.properties` reads its config from environment variables rather than hardcoded values, so it works the same way locally and in production. Set these before running (e.g. in your IDE's Run Configuration, or a local `.env`/export):

```
DB_URL=jdbc:postgresql://localhost:5432/codecompass
DB_USERNAME=postgres
DB_PASSWORD=<your local postgres password>
JWT_SECRET=<any random string, 32+ characters>
GEMINI_API_KEY=<your Gemini API key>
```

Then:
```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

> **Note:** in the deployed app, the frontend calls the API using relative paths (`/api/...`) since it's served from the same origin as the backend. For local development with the frontend and backend running on separate ports (`5173` and `8080`), point API calls at `http://localhost:8080` instead — either via a `VITE_API_URL` env var wired back into `src/api.js`, or a Vite dev-server proxy.

## Status

Deployed and working end-to-end: authentication, repo indexing, the full RAG pipeline, cited answers, persistent/renameable conversations, and a responsive UI are all live in production. Next up: a dedicated "re-index" button, nicer inline error states, and rate limiting.

## Privacy

Public repositories only. Sensitive files (`.env`, credentials, `.pem`, `.key`, etc.) are automatically filtered and never processed. Code chunks are sent to Google's Gemini API to generate embeddings and answers — see [Google's API terms](https://ai.google.dev/gemini-api/terms) for how that data is handled.

## Author

Built by [Sanjog](https://github.com/Tech-wizard18)
