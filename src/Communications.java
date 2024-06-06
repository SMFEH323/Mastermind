import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Platform;

/**
 * @author Sayf Elhawary
 */
public class Communications implements Runnable {

	// the user interface
	private MastermindGUI gui_;

	// server connection
	private Socket connection_;

	// reader
	private BufferedReader reader_;

	private GameOptions options_; // game options

	private int curguess_; // which guess the codebreaker is on
	private int curround_; // current round

	private String turn_; // the next turn for player.

	private boolean inTime_; // the next turn for player.

	public Communications ( MastermindGUI gui, Socket connection,
	                        BufferedReader reader, GameOptions options,
	                        int curguess, int curround ) {
		gui_ = gui;
		connection_ = connection;
		reader_ = reader;
		options_ = options;
		curguess_ = curguess;
		curround_ = curround;
	}

	public void run () {
		startPlay();
	}

	private void startPlay () {
		// read from server which turn to do next (codebreaker or codemaker)
		try {
			turn_ = reader_.readLine();
			System.out.println("turn read from server");

			if ( /* it is a codebreaker turn (Sayf) 4 */ turn_
			    .equals("codebreaker") ) {
				startCodeBreakerTurn();
				System.out.println("started codebreaker turn");
				makeGuess();
			} else {
				startCodeMakerTurn();
				System.out.println("started codemaker turn");
				playCodeMaker();
			}
		} catch ( IOException e ) {
			System.out.println("socket error: " + e.getMessage());
		}
	}

	public void startCodeBreakerTurn () {
		// get scores from server and update score display (Sayf) 5
		try {
			int playerScore = Integer.parseInt(reader_.readLine());
			System.out.println("read player score");
			int cpuScore = Integer.parseInt(reader_.readLine());
			System.out.println("read cpu score");
			Platform.runLater( () -> gui_.updateScores(playerScore,cpuScore));
			System.out.println("updated score display");
		} catch ( IOException e ) {
			System.out.println("socket error: " + e.getMessage());
		}
		Platform.runLater( () -> gui_.setupCodeBreakerDisplay(options_));

		curguess_ = 0;

		Platform.runLater( () -> gui_.activateGuess(curguess_));
		System.out.println(curguess_ + "opened");
	}

	public void endCodeBreakerTurn () {

		// get scores from server and update score display (Sayf) 5
		try {
			int playerScore = Integer.parseInt(reader_.readLine());
			System.out.println("read player score");
			int cpuScore = Integer.parseInt(reader_.readLine());
			System.out.println("read cpu score");
			Platform.runLater( () -> gui_.updateScores(playerScore,cpuScore));
			System.out.println("updated score display");
		} catch ( IOException e ) {
			System.out.println("socket error: " + e.getMessage());
		}
		//startCodeMakerTurn();
	}

	public void startCodeMakerTurn () {
		Platform.runLater( () -> gui_.setupCodeMakerDisplay(options_));
		playCodeMaker();
	}

	public void makeGuess () {

		for ( ; true ; ) {
			try {
				// get feedback from server and display it (Sayf) 7
				String doneIntime = reader_.readLine();
				Platform.runLater( () -> gui_.deactivateGuess(curguess_));
				System.out.println(curguess_ + "closed");
				if ( doneIntime.equals("just in time") ) {
					inTime_ = true;
					int which = Integer.parseInt(reader_.readLine());
					int numcorrect = Integer.parseInt(reader_.readLine());
					int numwrongpos = Integer.parseInt(reader_.readLine());
					System.out.println("recieved feedback from server");
					Platform.runLater( () -> gui_.displayFeedback(which,numcorrect,
					                                              numwrongpos));
				} else {
					inTime_ = false;
				}
				// find out from server whether the user gets another guess or if the
				// turn is over (Sayf) 8
				turn_ = reader_.readLine();
				System.out
				    .println("recived from server if the player has more guesses or code guesses");

				// advance to the next guess in the player-as-codebreaker turn, ending
				// the
				// codebreaker turn if all the guesses have been used
				if ( /* guesses are not used up (Sayf) 9 */ turn_.equals("yes") ) {
					Platform.runLater( () -> gui_.deactivateGuess(curguess_ - 1));
					curguess_++;
					Platform.runLater( () -> gui_.activateGuess(curguess_));
					System.out.println(curguess_ + "opened");
				} else { // end codebreaker turn

					// get scores from server and update score display (Sayf) 10 (Ben)
					// 1
					endCodeBreakerTurn();

					// read from server which turn to do next (codebreaker or
					// codemaker) (Ben) 2
					turn_ = reader_.readLine();
					System.out.println("turn read from server");
					if ( /* it is a codebreaker turn (Sayf) 11 */ turn_
					    .equals("codebreaker") ) {
						startCodeBreakerTurn();
					} else {
						startCodeMakerTurn();
					}
				}

			} catch ( IOException e ) {
				System.out.println("socket error: " + e.getMessage());
			}
		}

	}

	public void playCodeMaker () {

		// get and display guess and feedback for each of the server's guesses
		// until the server's turn is complete (Ben) 4
		try {
			for ( int k = 0 ; true ; k++ ) {
				String[] cpuGuess = new String[options_.getCodeLength()];
				System.out.println(k);
				int guessNum = Integer.parseInt(reader_.readLine());
				for ( int i = 0 ; i < options_.getCodeLength() ; i++ ) {
					cpuGuess[i] = reader_.readLine();
				}
				System.out.println("recieved guess from server");
				Platform.runLater( () -> gui_.displayGuess(guessNum,cpuGuess));
				System.out.println("displayed guess");

				int numcorrect = Integer.parseInt(reader_.readLine());
				int numwrongpos = Integer.parseInt(reader_.readLine());
				System.out.println("recieved feedback from server");
				Platform.runLater( () -> gui_.displayFeedback(guessNum,numcorrect,
				                                              numwrongpos));
				System.out.println("displayed feedback");

				if ( guessNum + 1 == options_.getNumGuesses()
				    || numcorrect == options_.getCodeLength() ) {
					System.out.println("server turn is complete");
					break;
				}
			}

			// get scores from server and update score display (Ben) 5
			int playerScore = Integer.parseInt(reader_.readLine());
			System.out.println("read player score");
			int cpuScore = Integer.parseInt(reader_.readLine());
			System.out.println("read cpu score");
			Platform.runLater( () -> gui_.updateScores(playerScore,cpuScore));
			System.out.println("updated score display");

			// find out from server whether there's another round or if the
			// game is over (Ben) 6
			turn_ = reader_.readLine();
			System.out.println("response read from server");

			// upon completion of a player-as-codemaker turn, advance to the next
			// round
			// or end the game if all of the rounds have been completed
			if ( /* there's another round (Ben) 7 */ turn_.equals("yes") ) {
				curround_++;

				// read from server which turn to do next (codebreaker or
				// codemaker) (Sayf) 18
				turn_ = reader_.readLine();
				System.out.println("turn read from server");
				if ( /* it is a codebreaker turn (Ben) 8 */ turn_
				    .equals("codebreaker") ) {
					startCodeBreakerTurn();
				} else {
					startCodeMakerTurn();
				}

			} else { // end game

				// get the game outcome (winner message) from the server and
				// display it (Ben) 9
				String outcome = reader_.readLine();
				System.out.println("recieved outcome");
				Platform.runLater( () -> gui_.displayGameOutcome(outcome));

				System.out.println("displayed outcome");

				// close the connection (Ben)10
				connection_.close();

				Platform.runLater( () -> gui_.enableStartGame(true));
				Platform.runLater( () -> gui_.enableConfig(true));
				Platform.runLater( () -> gui_.enableEndGame(false));

				Platform.runLater( () -> gui_.clearGameDisplay());
			}

		} catch ( IOException e ) {
			System.out.println("socket error: " + e.getMessage());
		}
	}

	public int getCurrentGuess () {
		return curguess_;
	}

	public int getCurrentRound () {
		return curround_;
	}

	public String getResponse () {
		return turn_;
	}

	public boolean isInTime () {
		return inTime_;
	}

}
