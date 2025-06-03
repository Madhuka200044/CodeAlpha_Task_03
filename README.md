Intelligent Chatbot`


# 🤖 Intelligent Chatbot v2.0

A simple intelligent console-based chatbot written in Java. This chatbot uses regular expressions, basic sentiment analysis, and a learning model to respond to user inputs and improve its replies over time.

---

## 🚀 Features

- Pattern-based recognition for common queries like time, date, jokes, greetings, and farewells.
- Sentiment-aware responses (positive, negative, neutral).
- Learning model that adapts to user input and prioritizes commonly used responses.
- Persistent learning through model serialization to a file (`chatbot_model.ser`).
- Graceful shutdown with auto-save using a shutdown hook.

---

## 🛠️ Technologies Used

- Java Standard Edition (Java SE)
- Java Regex (`java.util.regex`)
- Java I/O & Serialization
- Collections Framework
- Basic NLP techniques (keyword matching for sentiment)

---

## 📦 Installation

1. **Clone the repository:**

```bash
git clone https://github.com/your-username/intelligent-chatbot.git
cd intelligent-chatbot
````

2. **Compile the program:**

```bash
javac IntelligentChatbot.java
```

3. **Run the chatbot:**

```bash
java IntelligentChatbot
```

---

## 💡 Usage

* Type your message in the console.
* Try inputs like:

  * `What's the weather like?`
  * `Tell me a joke`
  * `Hi`
  * `How are you?`
  * `What time is it?`
* Type `quit` to exit and save the learning model.

---

## 📁 Learning Model

The chatbot stores learned user-response associations in a serialized file named `chatbot_model.ser`. This allows the chatbot to prioritize responses based on frequency.

---

## 🤖 Example Interaction

```
Intelligent Chatbot v2.0: Hello! How can I assist you today? (Type 'quit' to exit)
You: hi
Chatbot: Hi there! That's wonderful to hear!

You: tell me a joke
Chatbot: Why don't scientists trust atoms? Because they make up everything!

You: quit
Saving learning model before exiting...
Chatbot: Goodbye! Have a great day!
```

---

## 🧠 Future Improvements

* Add more advanced NLP techniques.
* GUI interface with JavaFX or Swing.
* External knowledge integration (e.g., weather API).
* Context-aware conversation flow.

---

## 📄 License

This project is open-source under the [MIT License](LICENSE).

