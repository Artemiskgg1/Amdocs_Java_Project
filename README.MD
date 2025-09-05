# 🏋️‍♂️ Fitness Tracker (Console + Oracle DB)

A **console-based fitness tracker** built in **Core Java** with **Oracle Database** for persistence.  
This project demonstrates **CRUD operations** (Create, Read, Update, Delete) using JDBC and Oracle.

---

## ✨ Features

- 👤 **User Management**

  - Create user profiles with personal details
  - Update email, delete users, list all users

- 💪 **Exercise Library**

  - Add new exercises
  - List available exercises

- 📅 **Workout Sessions**

  - Create workout sessions for a user
  - Add sets (reps/weight) to a session
  - List workout sessions by user

- 🥗 **Nutrition Logs**
  - Add daily nutrition entries (food, calories, macros)
  - List logs for a user by date

---

## ⚡ Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/Artemiskgg1/FitnessTrackerApp.git
cd FitnessTrackerApp
```

### 2. Set up Oracle Database

- Add Oracle JDBC Driver.
- Place [ojdbc11.jar](https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html) inside the lib/ folder.

- Run the provided SQL scripts in `sql/` to create necessary tables and procedures.

### 3. Configure Database Connection

- Update `DBConnection.java` with your Oracle DB credentials.

### 4. Compile and Run

```bash
javac -cp ".;lib\ojdbc11.jar" -d out $(Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })

java -cp "out;lib\ojdbc11.jar" com.fittracker.app.Main

```

<p align="center">Made with ❤️ by <a href="https://github.com/Artemiskgg1">Artemis</a></p>
