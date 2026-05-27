# 🚀 InterviewCoach - AI Powered Interview Preparation Platform

<div align="center">

![React](https://img.shields.io/badge/Frontend-React-blue?style=for-the-badge&logo=react)
![Spring Boot](https://img.shields.io/badge/Backend-SpringBoot-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue?style=for-the-badge&logo=postgresql)
![OpenAI](https://img.shields.io/badge/AI-OpenAI-black?style=for-the-badge&logo=openai)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

### 🎯 Prepare Smarter. Practice Better. Get Hired Faster.

An AI-powered full-stack interview preparation platform that helps candidates practice technical and behavioral interviews, analyze resumes, track performance, and receive personalized learning recommendations.

</div>

---

# 📖 Overview

InterviewCoach is an intelligent interview preparation platform designed to simulate real interview experiences across multiple domains including:

- Data Structures & Algorithms
- Java Programming
- React & Frontend Development
- SQL & Databases
- HR & Behavioral Interviews
- Voice-Based Mock Interviews

The platform provides AI-generated questions, performance analytics, resume analysis, ATS evaluation, adaptive learning roadmaps, and personalized feedback to help candidates improve interview readiness.

---

# 🧠 Key Features

## 🎤 AI Mock Interviews

Practice interviews in multiple domains:

- DSA
- Java
- React
- SQL
- HR & Behavioral

Features:

- Multiple difficulty levels
- Dynamic question generation
- Real-time scoring
- Detailed explanations
- Session tracking

---

## 🎙 Voice Interview Practice

Simulates real interview conversations.

Features:

- Speech-to-text integration
- Voice answer evaluation
- AI-powered feedback
- Communication analysis
- Confidence assessment

---

## 📄 Resume Intelligence

Upload resumes and receive intelligent analysis.

Features:

- Resume parsing
- Skill extraction
- Resume quality score
- ATS compatibility score
- Missing skills identification
- Improvement recommendations

---

## 📊 Performance Analytics Dashboard

Track interview performance over time.

Metrics include:

- Average score
- Topic-wise performance
- Confidence level
- Practice hours
- Question accuracy
- Interview completion rate

Visualized using:

- Line Charts
- Radar Charts
- Progress Indicators
- Analytics Cards

---

## 🛣 Personalized Learning Roadmap

Automatically generates improvement plans.

Features:

- Weak area detection
- Skill gap analysis
- Topic recommendations
- Practice priorities
- Progress milestones

---

## 🏢 Company Preparation

Prepare for company-specific interviews.

Includes:

- Company-focused questions
- Interview patterns
- Topic recommendations
- Difficulty customization

---

## 📚 Interview History

Track all previous interview sessions.

Includes:

- Session score
- Topic
- Difficulty
- Duration
- Completion status
- Answer history

---

# 🧭 System Mind Map

```text
InterviewCoach
│
├── Authentication
│   ├── Register
│   ├── Login
│   └── JWT Security
│
├── Dashboard
│   ├── Analytics
│   ├── Progress Tracking
│   ├── Roadmap
│   └── Recommendations
│
├── Mock Interviews
│   ├── DSA
│   ├── Java
│   ├── React
│   ├── SQL
│   └── HR
│
├── Voice Interview
│   ├── Speech Input
│   ├── AI Evaluation
│   └── Communication Feedback
│
├── Resume Analysis
│   ├── Resume Upload
│   ├── ATS Score
│   ├── Skill Extraction
│   ├── Missing Skills
│   └── Suggestions
│
├── Analytics
│   ├── Performance Trend
│   ├── Topic Breakdown
│   ├── Confidence Score
│   └── Practice Hours
│
├── History
│   ├── Completed Sessions
│   ├── Scores
│   └── Review Answers
│
└── AI Engine
    ├── Question Generation
    ├── Feedback Generation
    ├── Resume Evaluation
    └── Learning Recommendations
```

---

# 🏗 Architecture

```text
┌──────────────────────────┐
│      React Frontend      │
│   Dashboard / Resume     │
│   Interviews / History   │
└────────────┬─────────────┘
             │ REST APIs
             ▼
┌──────────────────────────┐
│   Spring Boot Backend    │
│ Controllers & Services   │
│ Business Logic Layer     │
└────────────┬─────────────┘
             │ JPA
             ▼
┌──────────────────────────┐
│      PostgreSQL DB       │
│ Users / Interviews       │
│ Scores / Analytics       │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────┐
│       OpenAI APIs        │
│ Question Generation      │
│ Feedback & Evaluation    │
└──────────────────────────┘
```

---

# 🛠 Technology Stack

## Frontend

- React.js
- JavaScript
- React Router
- Axios
- Tailwind CSS
- Recharts
- Vite

---

## Backend

- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT Authentication
- Maven

---

## Database

- PostgreSQL

---

## AI Services

- OpenAI API
- Resume Parsing
- Feedback Generation
- Question Generation

---

# 📂 Project Structure

```text
interview-coach
│
├── backend
│   ├── controller
│   ├── service
│   ├── repository
│   ├── model
│   ├── dto
│   ├── config
│   └── security
│
├── frontend
│   ├── components
│   ├── pages
│   ├── hooks
│   ├── services
│   ├── routes
│   └── assets
│
├── database
│
└── README.md
```

---

# ⚙ Installation

## Clone Repository

```bash
git clone https://github.com/Sakthi222006/interview-coach.git

cd interview-coach
```

---

## Backend Setup

```bash
cd backend

mvn clean install

mvn spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

---

## Frontend Setup

```bash
cd frontend

npm install

npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

---

## PostgreSQL Configuration

Update:

```properties
application.properties
```

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/interviewcoach

spring.datasource.username=postgres

spring.datasource.password=your_password
```

---

# 🔐 Environment Variables

```env
OPENAI_API_KEY=your_api_key

JWT_SECRET=your_secret_key

DB_URL=jdbc:postgresql://localhost:5432/interviewcoach

DB_USERNAME=postgres

DB_PASSWORD=password
```

---

# 📸 Screenshots

## Dashboard

- Performance Analytics
- Progress Tracking
- Learning Roadmap

## Interview Module

- AI Generated Questions
- Timer
- Scoring System

## Resume Analysis

- ATS Score
- Missing Skills
- Resume Feedback

## History

- Previous Sessions
- Score Tracking
- Review Answers

---

# 🎯 Future Enhancements

- AI Interview Video Analysis
- Multi-language Interviews
- Coding Playground
- Company-specific Interview Packs
- Interview Certificates
- Leaderboard System
- Daily Streak Tracking
- Export Feedback as PDF
- Interview Scheduling
- Real-time Collaboration

---

# 👨‍💻 Author

### Sakthivel R S

Emerging Full Stack Developer passionate about:

- Java Development
- Spring Boot
- React
- Artificial Intelligence
- Data Structures & Algorithms

LinkedIn:
https://www.linkedin.com/in/sakthivelrs

GitHub:
https://github.com/Sakthi222006

---

# ⭐ Support

If you found this project useful:

⭐ Star the repository

🍴 Fork the project

💡 Share feedback and suggestions

---

## Made with ❤️ by Sakthivel R S
