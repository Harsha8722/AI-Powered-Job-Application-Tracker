# 🚀 AI Powered Job Application Tracker

A full-stack application to track job applications, upload resumes, and use AI/NLP to analyze and match resumes with job descriptions.

## 🏗️ Architecture

```
React Frontend (port 3000)
        ↓ REST API
Spring Boot Backend (port 8080)
        ↓ HTTP
Python AI Service (port 5000)
        ↓ JDBC
MySQL Database (port 3306)
```

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React, Recharts, Axios, React Router |
| Backend | Java 17, Spring Boot 3, Spring Security, JWT |
| Database | MySQL 8 |
| AI Service | Python, Flask, spaCy, scikit-learn |
| DevOps | Docker, Docker Compose |

## 📦 Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- Python 3.11+
- MySQL 8.0
- (Optional) Docker & Docker Compose

## 🚀 Quick Start (Manual)

### 1. Database Setup

```sql
mysql -u root -p"-Gowda@789" < database/schema.sql
```

### 2. Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

Backend will start at: http://localhost:8080  
Swagger UI: http://localhost:8080/swagger-ui.html

### 3. AI Service (Python)

```bash
cd ai-service
pip install -r requirements.txt
python -m spacy download en_core_web_sm
python app.py
```

AI Service will start at: http://localhost:5000  
Health check: http://localhost:5000/health

### 4. Frontend (React)

```bash
cd frontend
npm install
npm start
```

Frontend will start at: http://localhost:3000

## 🐳 Docker Quick Start

```bash
docker-compose up --build
```

All services will start automatically.

## 🔗 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login & get JWT |

### Job Applications (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/jobs` | Create application |
| GET | `/api/jobs` | Get all applications |
| PUT | `/api/jobs/{id}` | Update application |
| DELETE | `/api/jobs/{id}` | Delete application |
| GET | `/api/jobs/dashboard` | Get dashboard stats |

### Resume (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/resume/upload` | Upload PDF/DOCX resume |
| GET | `/api/resume` | Get all resumes |
| POST | `/api/resume/analyze` | Analyze resume with AI |
| POST | `/api/resume/match` | Match resume with job description |

## 📊 Features

- 🔐 **JWT Authentication** — Secure register/login with BCrypt passwords
- 💼 **Job Tracker** — Add, update, delete, and filter applications
- 📊 **Dashboard** — Charts showing application statistics
- 📄 **Resume Upload** — Drag & drop PDF/DOCX upload
- 🤖 **AI Skill Extraction** — spaCy NLP extracts skills from resume
- 🎯 **Job Matching** — TF-IDF cosine similarity match score

## 📁 Project Structure

```
Job simulator/
├── backend/                    # Spring Boot
│   ├── src/main/java/com/jobtracker/
│   │   ├── controller/         # REST Controllers
│   │   ├── service/            # Business Logic
│   │   ├── repository/         # JPA Repositories
│   │   ├── model/              # JPA Entities
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── security/           # JWT Utils & Filter
│   │   ├── config/             # Security & OpenAPI Config
│   │   └── exception/          # Global Exception Handler
│   └── pom.xml
├── ai-service/                 # Python Flask
│   ├── app.py                  # Main Flask app
│   └── requirements.txt
├── frontend/                   # React
│   └── src/
│       ├── pages/              # Login, Register, Dashboard, Jobs, Resume, AI
│       ├── components/         # Layout (Sidebar)
│       └── api/                # Axios instance
├── database/
│   └── schema.sql              # MySQL schema
└── docker-compose.yml
```

## 🔑 Default Credentials (for testing)
Register at http://localhost:3000/register with any email/password.

## 🛠️ Configuration
- Backend config: `backend/src/main/resources/application.properties`
- MySQL: `root / -Gowda@789`
- JWT expiration: 24 hours
