# Mastermind

## Overview

**Mastermind** is a Java implementation of the classic code-breaking board game where players attempt to guess a hidden sequence in the fewest possible attempts.

This project extends the traditional game by supporting multiplayer gameplay through a **client-server architecture**, allowing players to compete over a network using a graphical user interface.

The goal of this project was to practice networking concepts, GUI development, and multiplayer game logic using Java.

---

## Features

- Multiplayer gameplay using client-server communication
- Graphical user interface for player interaction
- Secret code generation and validation
- Guess tracking and feedback system
- Network-based multiplayer support
- Java socket communication

---

## How the Game Works

Players connect to a server and attempt to guess a hidden code.

After each guess, the game provides feedback indicating:

- Correct values in the correct position
- Correct values in the wrong position

The objective is to crack the code in the fewest attempts possible.

---

## Tech Stack

- Java
- Java Swing / GUI Development
- Socket Programming
- Client-Server Architecture
- Object-Oriented Programming

---

## Installation

### Requirements

- Java 11 or higher
- Network access for multiplayer mode

### Clone the Repository

```bash
git clone [repository-url]
```

### Navigate to the Project Directory

```bash
cd Mastermind
```

### Compile the Project

```bash
javac *.java
```

---

## Usage

### Start the Server

```bash
java MastermindServer
```

### Start the Client

```bash
java MastermindGUI
```

Players can then connect to the server through the GUI and begin playing.

---

## Current Limitations

- Basic GUI design
- Limited matchmaking functionality
- No persistent player profiles
- Minimal game customization options

---

## Future Improvements

- Add online matchmaking
- Add difficulty settings
- Improve UI and animations
- Add chat and lobby features
- Add score tracking and leaderboards

---

## What This Project Demonstrates

- Client-server networking concepts
- Multiplayer game development
- GUI programming in Java
- Socket communication
- Object-oriented software design

---
