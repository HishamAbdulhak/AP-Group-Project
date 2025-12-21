# 🎓 UniHelp AI - Intelligent University Knowledge Base System

> **Course:** ITS66704 Advanced Programming (September 2025)
> **Assignment:** Group Project Task 2 (Part B - Development)
> **Group:** 40

**UniHelp AI** is a robust, enterprise-grade desktop application designed to bridge the gap between university administration and student queries. Built using **JavaFX** for a responsive UI and **LangChain4j** for advanced AI orchestration, it utilizes **Retrieval-Augmented Generation (RAG)** to provide accurate, cited answers from a localized repository of official university documents.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Key Features](#key-features)
3. [System Architecture](#system-architecture)
4. [Technical Stack](#technical-stack)
5. [Project Structure](#project-structure)
6. [Prerequisites](#prerequisites)
7. [Installation & Setup](#installation--setup)
8. [Configuration Guide](#configuration-guide)
9. [Usage Manual](#usage-manual)
10. [Troubleshooting](#troubleshooting)
11. [Contributors](#contributors-group-40)

---

##  Project Overview
The primary goal of UniHelp AI is to automate the repetitive task of answering student inquiries regarding admissions, campus facilities, and examination schedules. Unlike generic chatbots, UniHelp AI is **grounded**—it only answers based on the files you upload, effectively eliminating "hallucinations" and ensuring students receive official, verified information.

---

##  Key Features

###  Student Module
* **AI-Powered Chat Interface**: A stylish chat interface where students can ask natural language questions (e.g., *"Can I change my major?"*).
* **Smart Citations**: Every AI response includes clickable **"Reference"** links. Clicking a link immediately opens the exact source PDF/Doc within the app's Document Viewer.
* **Context-Aware Memory**: The chat session remembers the last 10 turns of conversation, allowing for follow-up questions.
* **Category Browser**: A "Quick Access" dashboard allows students to browse documents filtered by categories like *Admissions*, *Exams*, and *Campus Life*.
* **Secure Authentication**: Student accounts are password-protected with profile management capabilities.

###  Admin Module
* **Drag-and-Drop Ingestion**: Administrators can simply drag PDF, DOCX, or TXT files onto the dashboard to instantly index them into the AI's vector memory.
* **Real-Time Analytics**: The dashboard displays live statistics on the total number of documents indexed and the time since the last knowledge base update.
* **Content Management**: Admins can view a tabular list of all uploaded files and delete obsolete documents with a single click.
* **System Configuration**: A dedicated Settings page allows runtime modification of the AI model (e.g., switching between `gpt-4o-mini` and `gpt-3.5`), temperature (creativity), and API keys without restarting the app.

---

##  System Architecture

The application adheres to the **Model-View-Controller (MVC)** architectural pattern to ensure separation of concerns and maintainability.

### 1. The AI Pipeline (RAG)
The core intelligence is powered by a custom **Retrieval-Augmented Generation** engine located in `RAGService.java`:
1.  **Ingestion**: When a file is uploaded, `IngestionService` uses **Apache Tika** to extract text.
2.  **Chunking**: The text is split into 500-token segments with a 50-token overlap to preserve context.
3.  **Embedding**: These segments are converted into vector embeddings using OpenAI's `text-embedding-3-small`.
4.  **Vector Storage**: Embeddings are stored in a static, in-memory `EmbeddingStore` shared across the application.
5.  **Retrieval**: When a query is received, the system performs a cosine similarity search to find the top 3 most relevant segments.
6.  **Synthesis**: The retrieved segments + the user's question are sent to the LLM (`gpt-4o-mini`) to generate a final answer.

### 2. Design Patterns Implemented
* **Singleton Pattern**: Implemented in `UserSession.java` to maintain global state (current user, permissions) across different FXML views.
* **Factory Pattern**: Implicitly used via the `AiServices` builder in LangChain4j to construct the AI Assistant interface.
* **Observer Pattern**: Used extensively in Controllers (e.g., `BrowserController`) to listen for text changes in search bars and dynamically filter lists.
* **Service Layer Pattern**: Business logic is decoupled from Controllers into dedicated services (`AuthenticationService`, `IngestionService`, `ConfigService`).

---

##  Technical Stack

| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 21 (LTS) | Core application logic |
| **Frontend** | JavaFX | 21.0.1 | Desktop GUI framework |
| **AI Framework** | LangChain4j | 0.35.0 | AI Orchestration & RAG |
| **LLM Provider** | OpenAI API | GPT-4o-mini | Natural Language Generation |
| **Vector Model** | OpenAI | text-embedding-3-small | Semantic Search Embeddings |
| **Document Parsing** | Apache Tika | 2.9.1 | Extraction from PDF/DOCX |
| **Build Tool** | Maven | 3.8+ | Dependency Management |
| **Logging** | SLF4J / Custom | Custom | Application event tracking |

## 📂 Project Structure

A high-level overview of the important directories and files:
```text
unihelp-ai/
├── src/main/java/                 # Source code (Controllers, Services, Models)
├── src/main/resources/            # FXML Views, CSS, and Configuration Files
├── project_documents/             # Knowledge Base (PDFs/Docs stored here)
├── logs/                          # Application Logs (app.log)
├── students.txt                   # User Database (Simulated)
├── admins.txt                     # Admin Database (Simulated)
└── pom.xml                        # Maven Dependencies
```
---
##  Prerequisites

Before running the application, ensure your environment meets the following requirements:
1.  **Java Development Kit (JDK) 21** or higher installed.
2.  **Maven** installed (or use the included `mvnw` wrapper).
3.  **Active Internet Connection** (Required for OpenAI API communication).
4.  **OpenAI API Key**: You must have a valid key (starts with `sk-proj-...`).

---

##  Installation & Setup

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/your-group/unihelp-ai.git](https://github.com/your-group/unihelp-ai.git)
    cd unihelp-ai
    ```

2.  **Configure Environment Variables (Crucial)**
    For security, do not hardcode your API key. Set it in your system environment:
    * **Windows (PowerShell):** `$env:OPENAI_API_KEY="sk-proj-..."`
    * **Mac/Linux:** `export OPENAI_API_KEY="sk-proj-..."`

3.  **Build the Project**
    This command downloads all dependencies (LangChain4j, JavaFX, etc.).
    ```bash
    mvn clean install
    ```

4.  **Run the Application**
    ```bash
    mvn javafx:run
    ```

---

##  Configuration Guide

The application uses a hybrid configuration system (`ConfigService.java`):

1.  **Defaults**: If no config is found, it defaults to `gpt-4o-mini` and temperature `0.2`.
2.  **Environment Variables**: `OPENAI_API_KEY` is read from the OS for security.
3.  **`app_config.properties`**: Located in `src/main/resources`. You can modify this file to change defaults permanently:
    ```properties
    openai.chatModel=gpt-4o-mini
    openai.temperature=0.3
    openai.embeddingModel=text-embedding-3-small
    ```
4.  **Runtime Settings**: Admins can change these values via the **Settings Page** inside the app. Changes are saved to a local `app_config.properties` file in the working directory.

---

##  Usage Manual

###  Student Workflow
1.  **Login**: Use the default credentials (ID: `123456`, Password: `12345678`) or create your own account in the Register tab.
2.  **Dashboard**:
    * Use the **"Quick Access"** buttons to jump to specific document categories.
    * Type a question in the main search bar and click **"Ask AI"**.
3.  **Chat**:
    * The question from the dashboard is automatically carried over.
    * Wait for the **Blue Bubble** (User) and **Grey Bubble** (AI).
    * **Click the Citation**: If the AI says "Reference: handbook.pdf", click it to open the file.

###  Admin Workflow
1.  **Login**: Use the admin credentials (User: `admin1`, Password: `password1`).
2.  **Ingest Documents**:
    * Locate the **"Drop Zone"** (Blue dashed box).
    * Drag a PDF from your computer into the box.
    * Wait for the "Ingestion Successful" log in the console.
3.  **Manage Files**:
    * Use the table to view upload dates and file names.
    * Click **"Delete"** to remove a file from both the disk and the AI's memory.
4.  **Settings**:
    * Navigate to Settings to update the API Key or change the AI's "Creativity" (Temperature).

---

## Troubleshooting

| Issue | Possible Cause | Solution |
| :--- | :--- | :--- |
| **App crashes on launch** | Missing JavaFX SDK or JDK version mismatch. | Ensure you are using JDK 21+. Run `mvn clean javafx:run`. |
| **"401 Unauthorized" Error** | Invalid or missing API Key. | Check your environment variable `OPENAI_API_KEY`. |
| **"Insufficient Quota" Error** | OpenAI account has $0.00 balance. | Add $5 credit to your OpenAI billing account. |
| **AI answers "I don't know"** | No documents ingested. | Log in as Admin and upload relevant PDFs to the Dashboard. |
| **Files not showing in Browser** | `project_documents` folder missing. | Create a folder named `project_documents` in the project root. |

---

##  Contributors (Group 40)

* **Hisham Abdulhak** (0379361)
* **Moustapha Hezbeur** (0380611)
* **Muhammad Abdul Rehman Khan** (0381460)

---
