# 💬 ChatFusion – Java Chat App with Gemini AI Integration

A lightweight, responsive web-based chat application powered by [Gemini 1.5 Flash](https://ai.google.dev/gemini-api/docs) from Google, built using **Java Servlets**, **HTML/CSS/JS**, and Gemini’s Generative Language API.

---

## 🚀 Features

- 🔧 Backend: Java Servlet (`HttpServlet`)
- 🤖 AI Integration: Gemini 1.5 Flash (via REST API)
- 🎨 Frontend: Clean, responsive HTML/CSS/JavaScript UI
- 💬 Real-time AI responses
- ⚠️ Error handling for API rate limits and safety filters

---

## 📸 Demo

![UI Screenshot Placeholder](https://via.placeholder.com/800x400.png?text=ChatFusion+UI+Preview)

---

## 🛠️ Tech Stack

- Java EE (Servlet API)
- Gemini API (Google Generative Language)
- HTML, CSS, JavaScript (Vanilla)
- JSON Parsing via `org.json`

---

## 🔑 Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/chatfusion.git
cd chatfusion


### 2.Add your Gemini API key
In ChatServlet.java, replace the placeholder:

java
private static final String API_KEY = "YOUR_GEMINI_API_KEY";

➡️ Tip: For production, load the API key from environment variables or a secure config file.

### 3. Compile and Deploy
Deploy the app on a servlet container like Apache Tomcat or Jetty.

Folder structure:

/WEB-INF
   └── web.xml
/ChatServlet.java
/index.html
### 4. Run
Access the app in your browser:

bash
http://localhost:8080/chatfusion/
📄 API Model
This app uses:

bash
Model: gemini-1.5-flash-latest
Endpoint: https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent
📋 Example Request Payload
json
{
  "contents": [
    {
      "role": "user",
      "parts": [
        { "text": "Hello, what can you do?" }
      ]
    }
  ]
}
❗ Safety & Error Handling
Returns descriptive messages for:

🔁 429 Too Many Requests

🔐 Blocked prompts (e.g., sensitive content)

⚠️ API or network errors

📌 To-Do / Improvements
🔐 Externalize API key (env var or .properties)

💾 Add chat history with session tracking

📱 Improve mobile responsiveness

🧪 Add unit tests and input sanitization



