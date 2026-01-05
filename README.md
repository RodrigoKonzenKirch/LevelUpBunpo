# Level Up Bunpo

Level Up Bunpo is an Android application designed to help users learn and master Japanese grammar through an interactive and engaging quiz format. The app provides instant feedback, tracks user progress, and offers detailed explanations to reinforce learning.

## App Purpose

The primary goal of this app is to provide a simple yet effective tool for Japanese language learners to practice grammar points (文法, bunpō). By repeatedly quizzing the user, the app helps solidify their understanding and build a strong foundation, with a built-in mastery system to track their journey from novice to expert for each grammar concept.

## Key Features

* Interactive Grammar Quiz: A multiple-choice quiz that presents users with a Japanese sentence and asks them to select the correct grammatical particle.
* Immediate Feedback: Users receive instant visual feedback on whether their answer was correct or incorrect. The correct answer is always revealed to reinforce learning.
* Mastery System: Each question has a mastery level that increases with every correct answer, allowing users to see their progress over time.
* Dynamic Hints: Users can toggle an English translation for each question to get a hint if they are stuck.
* In-depth Grammar Tips: After answering a question, a "Grammar Tip" card appears, providing a detailed explanation of the specific grammar point that was just tested.
* Achievements Tracking: A dedicated screen shows the user's overall mastery progress across all questions, as well as a detailed breakdown of their mastery for each individual grammar point.
* Offline First: All grammar and question data is pre-populated into a local Room database, making the app fully functional offline.

## Tech Stack & Architecture

This project is built with a modern Android tech stack, focusing on best practices, scalability, and maintainability.

### UI

* Jetpack Compose: The entire UI is built with Compose, using a declarative and state-driven approach. 
* Material 3: Implements the latest Material Design guidelines for a clean and modern look.
* Compose Navigation 3: Handles navigation between the Welcome, Quiz, and Achievements screens.

### Architecture

* MVVM (Model-View-ViewModel): A clean separation of concerns between the UI, business logic, and data layers.
* Repository Pattern: Abstracts data sources, providing a clean API for the domain layer.
* Use Case Layer: Encapsulates specific business logic, making the ViewModels lean and the overall architecture more modular.

### Asynchronous Programming

* Kotlin Coroutines & Flow: Used extensively throughout the app for managing background threads and providing reactive data streams from the database to the UI.
* StateFlow: The primary mechanism for exposing UI state from the ViewModel to the Composables.

### Database

* Room: For local, persistent storage of all grammar points and questions.
* Database Pre-population: The database is automatically seeded from local JSON files on first launch using a RoomDatabase.Callback.

### Dependency Injection

* Hilt: Manages dependency injection throughout the app, simplifying the creation and management of objects.

### Testing

* JUnit 4: The core framework for testing.
* MockK: For creating mocks and verifying interactions in unit tests.
* Turbine: For testing Kotlin Flow emissions in a predictable way.
* Robolectric: For running local, fast UI tests without needing an emulator.
* Hilt Android Testing: For instrumented tests that require dependency injection in a real Android environment.

### Design Choices & Explanations

Why Pre-populate from JSON instead of a Packaged Database?

For this project, the grammar and question data is pre-populated into the Room database from local JSON files (grammar.json, questions.json) located in the res/raw directory. This approach was chosen over the alternative of packaging a pre-filled SQLite database file in the assets (.createFromAsset()).

The primary motivation for this choice is long-term maintainability and ease of future updates.
1. Human-Readable and Editable: JSON is a lightweight, human-readable format. This makes it incredibly easy for anyone (including non-developers) to view, edit, or add new questions and grammar points without needing specialized SQLite tools.
2. Decoupling Data from Schema: Using JSON decouples the raw content from the database schema. If we need to refactor a table or column name in Room, we often only need to update the parsing logic (e.g., add a @SerializedName annotation). The source JSON file can remain unchanged, which is much more flexible than being tied to a rigid SQL schema.
3. Clear Version Control: Changes to the data are very clear and easy to review in pull requests. A git diff on a JSON file is far more readable than a diff on a binary .db file or a large, auto-generated SQL script.

