# Board Game Android Client

This repository contains the client-side Android application for a digital Monopoly-like game. It is built with Kotlin and Jetpack Compose, providing the user interface and handling communication with the game server.

This application is the **game client** and is responsible for rendering the game board, handling user input, and communicating game actions to the server via WebSockets.

## ⚠️ Important: Client-Server Architecture

This application is **only** the client component of the Monopoly game. It **will not function** on its own.

It is designed to work exclusively with the corresponding server-side application, which is managed in a separate repository. Both components are required for the game to function.

  * **Server Repository:** [https://github.com/hoffmann2109/Monopoly-Server.git](https://www.google.com/url?sa=E&source=gmail&q=https://github.com/hoffmann2109/Monopoly-Server.git)

-----

## Technology Stack

  * **Language:** Kotlin
  * **UI Toolkit:** Jetpack Compose
  * **Networking:** WebSockets (using `org.java-websocket`)
  * **Authentication:** Firebase Authentication (including Google Sign-In)
  * **Database:** Cloud Firestore (for user profiles, statistics, etc.)
  * **Build Tool:** Gradle (with Kotlin DSL)

-----

## Building and Running

### Prerequisites

  * **Android Studio:** The latest stable version (e.g., Hedgehog or newer) is recommended.
  * **Java 17:** Required by the project's Gradle configuration.
  * **Running Server:** You must have an instance of the [Monopoly Server](https://www.google.com/url?sa=E&source=gmail&q=https://github.com/hoffmann2109/Monopoly-Server.git) running and accessible from your device or emulator.

### 1\. Set Up the Server

Before running the client, ensure the server application is running and you know its IP address and port.

### 2\. Run the client

1.  Let Android Studio sync and build the project (this may take a few minutes).
2.  Select an Android emulator or connect a physical Android device.
3.  Click the "Run" button (▶) in Android Studio to build and install the app on your selected device.
4.  Start the app and connect to the game.
