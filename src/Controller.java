import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The main controller for the Mastermind client - carries out the user actions.
 */
public class Controller {

	// the user interface
	private MastermindGUI gui_;

	// configuration
	private ServerSettings settings_; // server configuration
	private GameOptions options_; // game options

	// current state
	private int curguess_; // which guess the codebreaker is on
	private int curround_; // current round

	// server connection
	private Socket connection_;

	// streams
	private BufferedReader reader_;
	private PrintWriter writer_;

	// reader thread
	private Communications cm_;
	private Thread thread_;

	/**
	 * Create a controller to work with the specified user interface.
	 * 
	 * @param gui
	 *          mastermind user interface
	 */
	public Controller ( MastermindGUI gui ) {
		gui_ = gui;

		// game not started yet
		curguess_ = -1;
		curround_ = -1;

		// set default options and configuration
		settings_ = new ServerSettings("localhost",9050);
		options_ = new GameOptions("standard","average",false,3);
	}

	/**
	 * Start playing with the current server settings and game options.
	 */
	public void startPlay () {
		curround_ = 1;

		try {

			// connect to server (Sayf) 1
			connection_ = new Socket(settings_.getHost(),settings_.getPort());
			System.out.println("connected to server");
			reader_ = new BufferedReader(new InputStreamReader(connection_
			    .getInputStream()));
			writer_ = new PrintWriter(connection_.getOutputStream());
			System.out.println("initialized streams");
			cm_ = new Communications(gui_,connection_,reader_,options_,curguess_,
			                         curround_);
			thread_ = new Thread(cm_);
			thread_.start();
			// send game options (number of colors, code length, number
			// of guesses, number of rounds) to server (Sayf) 2
			writer_.println(options_.getTimeLimit());
			System.out.println("time limit: " + options_.getTimeLimit());
			writer_.println(options_.getNumColors());
			System.out.println("sent num colors");
			writer_.println(options_.getCodeLength());
			System.out.println("sent code length");
			writer_.println(options_.getNumGuesses());
			System.out.println("sent num guesses");
			writer_.println(options_.getNumRounds());
			System.out.println("sent num rounds");
			writer_.flush();

			// read from server which turn to do next (codebreaker or codemaker)
			// (Sayf) 3
			// String turn = reader_.readLine();
			// System.out.println("turn read from server");

			gui_.enableStartGame(false);
			gui_.enableConfig(false);
			gui_.enableEndGame(true);

			// if ( /* it is a codebreaker turn (Sayf) 4 */ cm_.getResponse()
			// .equals("codebreaker") ) {
			// startCodeBreakerTurn();
			// System.out.println("started codebreaker turn");
			// } else {
			// startCodeMakerTurn();
			// System.out.println("started codemaker turn");
			// }
		} catch ( IOException e ) {
			System.out.println("socket error: " + e.getMessage());
		}

	}

	/**
	 * Start the player-as-codebreaker turn - prepares for the player to make
	 * their first guess.
	 */
	public void startCodeBreakerTurn () {
		// get scores from server and update score display (Sayf) 5
		// try {
		// int playerScore = Integer.parseInt(reader_.readLine());
		// System.out.println("read player score");
		// int cpuScore = Integer.parseInt(reader_.readLine());
		// System.out.println("read cpu score");
		// gui_.updateScores(playerScore,cpuScore);
		// System.out.println("updated score display");
		// } catch ( IOException e ) {
		// System.out.println("socket error: " + e.getMessage());
		// }
		// show codebreaker display
		// gui_.setupCodeBreakerDisplay(options_);
		//
		// curguess_ = 0;
		//
		// gui_.activateGuess(curguess_);

	}

	/**
	 * End the player-as-codebreaker turn - what happens after the code has been
	 * guessed or the guesses have all been used.
	 */
	public void endCodeBreakerTurn () {

		// get scores from server and update score display (Sayf) 5
		// try {
		// int playerScore = Integer.parseInt(reader_.readLine());
		// System.out.println("read player score");
		// int cpuScore = Integer.parseInt(reader_.readLine());
		// System.out.println("read cpu score");
		// gui_.updateScores(playerScore,cpuScore);
		// System.out.println("updated score display");
		// } catch ( IOException e ) {
		// System.out.println("socket error: " + e.getMessage());
		// }
	}

	/**
	 * Start the player-as-codemaker turn - prepares for the player to create the
	 * code for the server to guess.
	 */
	public void startCodeMakerTurn () {
		gui_.setupCodeMakerDisplay(options_);
	}

	/**
	 * Send the player's guess to the server and handle the results. Also sets up
	 * for the next guess/ends the turn if the guesses have been used.
	 * 
	 * @param guess
	 *          the player's guess
	 */
	public void makeGuess ( Peg[] guess ) {
		// gui_.deactivateGuess(curguess_);

		// try {
		// send guess to server (Sayf) 6
		for ( int i = 0 ; i < guess.length ; i++ ) {
			writer_.println(guess[i].getColor().toString());
		}
		writer_.flush();
		System.out.println("sent guesses to server");

		// get feedback from server and display it (Sayf) 7
		// int which = Integer.parseInt(reader_.readLine());
		// int numcorrect = Integer.parseInt(reader_.readLine());
		// int numwrongpos = Integer.parseInt(reader_.readLine());
		// System.out.println("recieved feedback from server");
		// gui_.displayFeedback(which,numcorrect,numwrongpos);

		// find out from server whether the user gets another guess or if the
		// turn is over (Sayf) 8
		// String response = reader_.readLine();
		// System.out
		// .println("recived from server if the player has more guesses or code
		// guesses");

		// advance to the next guess in the player-as-codebreaker turn, ending the
		// codebreaker turn if all the guesses have been used
		// if ( /* guesses are not used up (Sayf) 9 */
		// cm_.getResponse().equals("yes") ) {
		// curguess_++;
		// gui_.activateGuess(curguess_);
		//
		// } else { // end codebreaker turn
		//
		// // get scores from server and update score display (Sayf) 10 (Ben)
		// // 1
		// endCodeBreakerTurn();
		//
		// // read from server which turn to do next (codebreaker or
		// // codemaker) (Ben) 2
		//// String turn = reader_.readLine();
		//// System.out.println("turn read from server");
		// if ( /* it is a codebreaker turn (Sayf) 11 */ cm_.getResponse()
		// .equals("codebreaker") ) {
		// startCodeBreakerTurn();
		// } else {
		// startCodeMakerTurn();
		// }
		// }
		// } catch ( IOException e ) {
		// System.out.println("socket error: " + e.getMessage());
		// }
	}

	/**
	 * Send the player's code to the server and handle the rest of the
	 * player-as-codemaker turn.
	 * 
	 * @param code
	 *          the code for the server to guess
	 */
	public void playCodeMaker ( Peg[] code ) {

		// send code to server (Ben) 3
		for ( int i = 0 ; i < code.length ; i++ ) {
			writer_.println(code[i].getColor().toString());
		}
		System.out.println("sent code to server");
		writer_.flush();

		// get and display guess and feedback for each of the server's guesses
		// until the server's turn is complete (Ben) 4
		// try {
		// for ( int k = 0 ; true ; k++ ) {
		// String[] cpuGuess = new String[options_.getCodeLength()];
		// System.out.println(k);
		// int guessNum = Integer.parseInt(reader_.readLine());
		// for ( int i = 0 ; i < options_.getCodeLength() ; i++ ) {
		// cpuGuess[i] = reader_.readLine();
		// }
		// System.out.println("recieved guess from server");
		// gui_.displayGuess(guessNum,cpuGuess);
		// System.out.println("displayed guess");
		//
		// int numcorrect = Integer.parseInt(reader_.readLine());
		// int numwrongpos = Integer.parseInt(reader_.readLine());
		// System.out.println("recieved feedback from server");
		// gui_.displayFeedback(guessNum,numcorrect,numwrongpos);
		// System.out.println("displayed feedback");
		//
		// if ( guessNum + 1 == options_.getNumGuesses()
		// || numcorrect == options_.getCodeLength() ) {
		// System.out.println("server turn is complete");
		// break;
		// }
		// }

		// get scores from server and update score display (Ben) 5
		// int playerScore = Integer.parseInt(reader_.readLine());
		// System.out.println("read player score");
		// int cpuScore = Integer.parseInt(reader_.readLine());
		// System.out.println("read cpu score");
		// gui_.updateScores(playerScore,cpuScore);
		// System.out.println("updated score display");
		//
		// find out from server whether there's another round or if the
		// game is over (Ben) 6
		// String response = reader_.readLine();
		// System.out.println("response read from server");

		// upon completion of a player-as-codemaker turn, advance to the next
		// round
		// or end the game if all of the rounds have been completed
		// if ( /* there's another round (Ben) 7 */ cm_.getResponse()
		// .equals("yes") ) {
		// curround_++;
		//
		// // read from server which turn to do next (codebreaker or
		// // codemaker) (Sayf) 18
		// // String turn = reader_.readLine();
		// // System.out.println("turn read from server");
		// if ( /* it is a codebreaker turn (Ben) 8 */ cm_.getResponse()
		// .equals("codebreaker") ) {
		// startCodeBreakerTurn();
		// } else {
		// startCodeMakerTurn();
		// }
		//
		// } else { // end game
		//
		// // get the game outcome (winner message) from the server and
		// // display it (Ben) 9
		// // String outcome = reader_.readLine();
		// // System.out.println("recieved outcome");
		// // gui_.displayGameOutcome(outcome);
		// // System.out.println("displayed outcome");
		//
		// // close the connection (Ben)10
		// connection_.close();
		//
		// gui_.enableStartGame(true);
		// gui_.enableConfig(true);
		// gui_.enableEndGame(false);
		//
		// gui_.clearGameDisplay();
		// }
		//
		// } catch ( IOException e ) {
		// System.out.println("socket error: " + e.getMessage());
		// }
	}

	/**
	 * End a game before all of the rounds have been completed.
	 */
	public void abortPlay () {
		// close connection (Ben) 11
		try {
			thread_.interrupt();
			connection_.close();
		} catch ( IOException e ) {
			System.out.println("socket error: " + e.getMessage());
		}

		gui_.enableStartGame(true);
		gui_.enableConfig(true);
		gui_.enableEndGame(false);

		gui_.clearGameDisplay();
	}

	/**
	 * Set the server settings.
	 * 
	 * @param settings
	 *          server settings to set
	 */
	public void setServerSettings ( ServerSettings settings ) {
		settings_ = settings;
	}

	/**
	 * Set the game options.
	 * 
	 * @param options
	 *          game options to set
	 */
	public void setGameOptions ( GameOptions options ) {
		options_ = options;
	}

	/**
	 * Get the current game options.
	 * 
	 * @return current game options
	 */
	public GameOptions getOptions () {
		return options_;
	}

	/**
	 * Get the current server settings.
	 * 
	 * @return current server settings
	 */
	public ServerSettings getServerSettings () {
		return settings_;
	}

	/**
	 * Get the current round.
	 * 
	 * @return current round
	 */
	public int getRound () {
		curround_ = cm_.getCurrentRound();
		return curround_;
	}

}
