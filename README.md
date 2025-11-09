# Refluent
Refluent is a **minimal, distraction-free flashcard app** for Android.

![readme](https://github.com/user-attachments/assets/cf4276b7-b348-4baa-b719-ff7ea7ccc67c)


- No clutter, no complicated decks setup  
- Just you, your cards, and a solid spaced-repetition engine  
- Available on Google Play  

> Refluent is built to stay out of your way and help you actually *remember* things, not manage yet another complex tool.

---

## Download

You can download Refluent from Google Play:

https://play.google.com/store/apps/details?id=app.refluent

---

## Building from source

### Prerequisites

- Android Studio (latest stable)
- A **Gemini API key**

### 1. Clone the repository

```bash
git clone https://github.com/dalmif/refluent.git
cd refluent
```
### 2. Add your Gemini API key

The app expects a Gemini API key in a local secrets.properties file that is not checked into version control.

Create this file, secrets/secrets.properties

with this content:
```
GEMINI_API_KEY={YOUR_API_KEY}
```

Replace `{YOUR_API_KEY}` with your actual key from Google AI Studio.


### 3. Build & run

Open the project in Android Studio, let it sync, then:

Choose a device/emulator

Click Run

## License

This project is licensed under the Apache License 2.0.

Copyright © 2025 Mohammad Fallah
