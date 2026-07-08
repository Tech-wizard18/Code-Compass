# CodeCompass 🧭

CodeCompass is an AI-powered learning companion that lets you explore any public GitHub repository through natural language chat. Paste a repo URL, and ask questions like *"how does login work?"* — get answers grounded in the actual code, with exact file and line citations.

Think of it as ChatGPT/Claude, but scoped to a specific codebase you choose.

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

## How It Works

1. **Index a repo** — paste a public GitHub URL. The backend clones it, filters out sensitive files, chunks the code at class/method level using AST parsing, embeds each chunk, and stores it in Postgres with pgvector.
2. **Ask a question** — your question is embedded the same way, and pgvector's cosine similarity search finds the most relevant code chunks.
3. **Get a grounded answer** — the retrieved chunks + your question are sent to an LLM, which generates an answer citing the exact file and line numbers it used.

## Features

- 🔍 Index any public GitHub repository, with automatic re-indexing on repeat submissions
- 💬 Natural language Q&A grounded in real code, with expandable file/line citations
- 🔐 JWT authentication — register/login with username or email, with automatic session expiry handling
- 🗂️ Persistent, multi-turn conversations per repo — browse, resume, or delete past conversations from a sidebar
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
```bash
cd backend
# configure src/main/resources/application.properties with your DB credentials
# and add: gemini.api.key=<your key>
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

The frontend expects the backend running at `http://localhost:8080` (configurable via `frontend/.env`).

## Status

Actively in development. Core RAG pipeline, authentication, repo indexing, citations, conversation history, and a fully responsive UI are complete and working end-to-end. Upcoming: conversation renaming, nicer error states, and deployment.

## Privacy

Public repositories only. Sensitive files (`.env`, credentials, `.pem`, `.key`, etc.) are automatically filtered and never processed. Code chunks are sent to Google's Gemini API to generate embeddings and answers — see [Google's API terms](https://ai.google.dev/gemini-api/terms) for how that data is handled.

## Author

Built by [Sanjog](https://github.com/Tech-wizard18)
