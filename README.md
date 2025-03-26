# Momentum Game AI

This project is an implementation of the **Momentum** board game, where I developed my own AI to compete against my professor's AI at UNIVERSIDADE PORTUCALENSE. The server was provided by my professor, and the project was built using **Java** and **JavaFX**.

## 📌 Project Overview
Momentum is a dynamic abstract strategy board game inspired by **Newton’s cradle** mechanics. The game involves placing marbles on a 7×7 board, with momentum transferring through adjacent lines of marbles. The goal is to place all eight marbles on the board while strategically pushing the opponent's marbles off.

- **AI vs AI Battles**: My AI competes against my professor’s AI, making strategic moves based on board evaluation.
- **Turn-Based Game Mechanics**: Players take turns dropping marbles, following game rules such as the **Pie Rule** and **30-Round Rule**.
- **Momentum Simulation**: The physics-based movement of marbles follows the momentum transfer mechanics.

## 🛠️ Technologies Used
- **Java**: Core programming language for AI logic and game mechanics.
- **JavaFX**: Used for the graphical user interface (GUI) to visualize the game.
- **Sockets & Networking**: The game interacts with the professor's server for AI battles.
- **Algorithmic Decision Making**: Implemented AI strategies to optimize move selection.

## 🚀 Features
- Custom AI that makes intelligent moves based on game state evaluation.
- Interactive JavaFX UI for visualization.
- Integration with a remote game server.
- Automated turn management and rule enforcement.

## 🎯 How to Start and Run the Game

### **Before Starting the Server**
1. **All response clients must be connected to the same network.**
2. The client that **joins** the server (**not the one creating it**) must update the `SERVER_IP` variable in **`MomentumResponse.java`** to match the **server's local IP address**.

### **Starting the Server**
To start the **Momentum Game Server**, use the following command:
```sh
java --module-path "C:\Users\<user>\Documents\UPT\AI\Momentum\javafx-sdk-23.0.1\lib" --add-modules=javafx.controls -jar MomentumServer.jar
