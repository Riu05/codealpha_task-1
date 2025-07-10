#  Java AI ChatBot using Gemini API

A sleek, responsive AI chatbot built with **Java**, **Maven**, and **Swing GUI**, powered by Google's **Gemini API**.  
Inspired by Instagram's DM layout, this chatbot lets you interact in real-time with an AI using a beautifully designed desktop interface.

---

##  Features

- Gemini API Integration (Google AI)
- Interactive GUI using Java Swing
- Secure API key via `config.properties`
- Clean Maven project structure
- Real-time chat-like conversation flow
- Aesthetic UI inspired by classic messaging apps

---

## Tech Stack

| Layer        | Tech Used             |
|--------------|------------------------|
| Language     | Java (JDK 17+)         |
| Build Tool   | Maven                  |
| UI Framework | Java Swing             |
| AI Service   | Gemini 1.5 Flash API   |
| Parsing Tool | Gson (for JSON parsing) |
| Config File  | `config.properties` (excluded from repo)

---

##  Project Structure

AI-ChatBot/
├── src/
│ └── main/
│ ├── java/
│ │ └── com/
│ │ └── riya/
│ │ └── chatbot/
│ │ ├── ChatBot.java
│ │ ├── ChatBotGUI.java
│ │ └── GeminiClient.java
│ └── resources/
│ └── config.properties (excluded for safety)
├── pom.xml
└── .gitignore

---

## How to Run Locally

1. Clone or download the repository
2. Add your Gemini API key to `config.properties` in `src/main/resources`:
   ```properties
   GEMINI_API_KEY=your_real_key_here
Open the project in IntelliJ IDEA or any Java IDE

Run ChatBotGUI.java to launch the application

Start chatting with your personal AI! 

⚠️ Security Note
The config.properties file containing your Gemini API key is intentionally excluded from this repository using .gitignore.

