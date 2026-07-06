# CodeCompass 🧭

CodeCompass is an AI-powered learning companion that lets you explore any public GitHub repository through natural language chat. Paste a repo URL, and ask questions like *"how does login work?"* — get answers grounded in the actual code, with exact file and line citations.

Think of it as ChatGPT/Claude, but scoped to a specific codebase you choose.

## Why CodeCompass?

- **Fully local, private by design** — embeddings and LLM inference both run locally via Ollama. Your code never leaves your machine, and no external API calls are made.
- **Grounded, not hallucinated** — every answer is backed by a RAG (Retrieval-Augmented Generation) pipeline that retrieves real code chunks before generating a response.
- **Persistent conversations** — like Claude/ChatGPT, your chat history is saved per repo and per user, so you can pick up where you left off.

## Tech Stack

**Backend**
- Java 17, Spring Boot 3, Maven
- JGit for repo cloning
- JavaParser for AST-based code chunking
- Ollama (`nomic-embed-text` for embeddings, `llama3` for generation)
- PostgreSQL 16 + pgvector for vector similarity search
- Spring Security + JWT for stateless authentication

**Frontend**
- React + Vite
- Tailwind CSS
- React Router
- react-hot-toast

## How It Works

1. **Index a repo** — paste a public GitHub URL. The backend clones it, filters out sensitive files, chunks the code at class/method level using AST parsing, embeds each chunk locally, and stores it in Postgres with pgvector.
2. **Ask a question** — your question is embedded the same way, and pgvector's cosine similarity search finds the most relevant code chunks.
3. **Get a grounded answer** — the retrieved chunks + your question are sent to a local LLM, which generates an answer citing the exact file and line numbers it used.

## Features

- 🔍 Index any public GitHub repository
- 💬 Natural language Q&A grounded in real code, with citations
- 🔐 JWT authentication — register/login with username or email
- 🗂️ Persistent, multi-turn conversations per repo
- 🎨 Clean, modern chat interface

## Project Structure
Code-Compass/
├── backend/     # Spring Boot API — indexing, RAG pipeline, auth
└── frontend/    # React UI — chat interface, auth pages

## Running Locally

### Prerequisites
- Java 17, Maven
- Node.js + npm
- PostgreSQL 16 with the `pgvector` extension
- [Ollama](https://ollama.com) installed, with `nomic-embed-text` and `llama3` models pulled

### Backend
```bash
cd backend
# configure src/main/resources/application.properties with your DB credentials
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

Actively in development. Core RAG pipeline, authentication, repo indexing, and chat are complete and working end-to-end. Upcoming: conversation history sidebar, citation display in the UI, and deployment.

## Privacy

Public repositories only. Sensitive files (`.env`, credentials, `.pem`, `.key`, etc.) are automatically filtered and never processed. No code or embeddings are sent to any external service — everything runs on your machine via Ollama.



## Author

Built by [Sanjog](https://github.com/Tech-wizard18)