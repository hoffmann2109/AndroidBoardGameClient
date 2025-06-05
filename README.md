Websocket Demo using Krossbow for STOMP protocol in Android App with Kotlin for 621.250 (25S) Software Engineering II. 

# Monopoly-Client

Dieses Projekt ist eine digitale Umsetzung des klassischen Brettspiels **Monopoly** für bis zu vier Spieler. 
Ziel ist es, eine spielbare Android-App zu entwickeln.

---

## 📜 Spielregeln

Die Spielregeln orientieren sich an den offiziellen Monopoly-Regeln und sind hier einsehbar:  
🔗 https://en.wikibooks.org/wiki/Monopoly/Official_Rules

---

## 📱 Zielplattform

- **Geräteklasse**: Tablet
- **Android-Version**: API 24 oder höher
- **Auflösung**: 2560 × 1600 px 
- **Orientierung**: Querformat (Landscape)

---

## ⚙️ Technologien

- **Programmiersprache**: Kotlin (Client) / Java (Server)
- **UI-Framework**: Jetpack Compose / Android Views
- **Backend-Framework**: Spring Boot (Java)
- **Multiplayer-Kommunikation**: STOMP über WebSockets (Spring WebSocket)

---

## Firebase Ruleset

- Die Firebase Regeln wurden so angepasst, dass alles außer der Namen nach der initialen Erstellung nur mehr vom Server aus bearbeitet werden kann:

### Dump des Rulesets
'''
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // 1) User-Daten
    match /users/{uid} {
      // Erlaubt Lesen nur durch den authentifizierten Benutzer
      allow read: if request.auth != null && request.auth.uid == uid;

      // Erlaubt Erstellen nur durch den authentifizierten Benutzer
      allow create: if request.auth != null && request.auth.uid == uid;

      // Erlaubt Update **nur**, wenn nur das Feld "name" geändert wird
      allow update: if request.auth != null
                    && request.auth.uid == uid
                && request.resource.data.diff(resource.data).affectedKeys().hasOnly(['name']);

      // Löschen nicht erlaubt
      allow delete: if false;

      // Subcollection: gameHistory
      match /gameHistory/{gameId} {
        allow get, list: if request.auth != null && request.auth.uid == uid;
        allow write: if false; // nur Server darf schreiben
      }
    }

    // 2) Leaderboards: jede Collection einzeln
    match /leaderboard_gamesPlayed/{rank} {
      allow get, list: if request.auth != null;
      allow create, update, delete: if false;
    }
    match /leaderboard_highestMoney/{rank} {
      allow get, list: if request.auth != null;
      allow create, update, delete: if false;
    }
    match /leaderboard_level/{rank} {
      allow get, list: if request.auth != null;
      allow create, update, delete: if false;
    }
    match /leaderboard_averageMoney/{rank} {
      allow get, list: if request.auth != null;
      allow create, update, delete: if false;
    }
    match /leaderboard_wins/{rank} {
      allow get, list: if request.auth != null;
      allow create, update, delete: if false;
    }
  }
}
'''