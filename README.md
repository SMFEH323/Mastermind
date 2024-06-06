# Mastermind

## Project Description
Mastermind is a Java-based implementation of the classic board game where players must guess the secret code in the fewest attempts. This software allows players to compete against each other over a network, facilitated by a server-client architecture. It features a graphical user interface for enhanced user experience.

## Installation

**Requirements:**
- Java 11 or higher
- Network access if playing in multiplayer mode

**Setup:**
To set up the Mastermind game, follow these steps:
1. Clone the repository:
   ```bash
   git clone [repository-url]
   ```
2. Navigate to the cloned directory and compile the Java files:
   ```bash
   cd Mastermind
   javac *.java
   ```

## Usage
To start the Mastermind game:
1. Launch the server:
   ```bash
   java MastermindServer
   ```
2. On each player's machine, launch the client:
   ```bash
   java MastermindGUI
   ```

Players can then connect to the server using the GUI and start playing by entering guesses to try and crack the code.

## Contributing
Contributions are welcome! If you'd like to contribute, please fork the repository and use a feature branch. Pull requests are warmly welcome.

## Contact
For any inquiries, please reach out via email at elhawaryseif@gmail.com.
