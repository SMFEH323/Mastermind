
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Game options for Mastermind.
 */
public class GameOptions {

	// named variations specify a particular combination of number of colors and
	// number of holes (length of code)
	// named difficulties specify a particular number of guesses and the time
	// limit
	// per guess
	private String variation_, difficulty_;

	private int numcolors_, // number of peg colors
	    codelength_, // number of holes (length of the code)
	    numguesses_; // number of guesses the codebreaker gets
	private boolean hastimelimit_; // whether there is a time limit on guessing
	                               // turns
	private long limit_; // time limit (ms)

	private int numrounds_; // number of rounds per game (one round consists of
	                        // one turn as codebreaker and one as codemaker)

	/**
	 * Create game options with default values.
	 */
	public GameOptions () {
		this("standard","average",false,3);
	}

	/**
	 * Create game options with the specified values.
	 * 
	 * @param variation
	 *          variation name (standard, challenge, or mini)
	 * @param difficulty
	 *          difficulty name (average, harder, difficult, fiendish)
	 * @param hastimelimit
	 *          whether or not there is a time limit on making guesses
	 * @param numrounds
	 *          number of rounds per game
	 */
	public GameOptions ( String variation, String difficulty,
	                     boolean hastimelimit, int numrounds ) {

		variation_ = variation;
		difficulty_ = difficulty;

		if ( variation.equals("standard") ) {
			numcolors_ = 6;
			codelength_ = 4;
		} else if ( variation.equals("challenge") ) {
			numcolors_ = 8;
			codelength_ = 5;
		} else if ( variation.equals("mini") ) {
			numcolors_ = 5;
			codelength_ = 3;
		} else {
			throw new IllegalArgumentException("invalid variation " + variation);
		}

		if ( difficulty.equals("average") ) {
			numguesses_ = 12;
		} else if ( difficulty.equals("harder") ) {
			numguesses_ = 10;
		} else if ( difficulty.equals("difficult") ) {
			numguesses_ = 8;
		} else if ( difficulty.equals("fiendish") ) {
			numguesses_ = 6;
		} else {
			throw new IllegalArgumentException("invalid difficulty " + difficulty);
		}

		hastimelimit_ = hastimelimit;
		limit_ = 30000; // change if desired
		numrounds_ = numrounds;
	}

	/**
	 * Get the supported variations names.
	 * 
	 * @return supported variations names
	 */
	public static ObservableList<String> getVariations () {
		return FXCollections.observableArrayList("standard","challenge","mini");
	}

	/**
	 * Get the supported difficulty names.
	 * 
	 * @return supported difficulty names
	 */
	public static ObservableList<String> getDifficulties () {
		return FXCollections.observableArrayList("average","harder","difficult",
		                                         "fiendish");
	}

	/**
	 * Get the number of code peg colors.
	 * 
	 * @return number of code peg colors
	 */
	public int getNumColors () {
		return numcolors_;
	}

	/**
	 * Get the number of holes (length of the code ).
	 * 
	 * @return the number of holes (length of code)
	 */
	public int getCodeLength () {
		return codelength_;
	}

	/**
	 * Get the number of guesses the codebreaker is allowed.
	 * 
	 * @return number of guesses
	 */
	public int getNumGuesses () {
		return numguesses_;
	}

	/**
	 * Is there a time limit on guesses?
	 * 
	 * @return whether there is a time limit on guesses
	 */
	public boolean hasTimeLimit () {
		return hastimelimit_;
	}

	/**
	 * Get the variation name.
	 * 
	 * @return the variation name
	 */
	public String getVariation () {
		return variation_;
	}

	/**
	 * Get the difficulty name.
	 * 
	 * @return the difficulty name
	 */
	public String getDifficulty () {
		return difficulty_;
	}

	/**
	 * Get the number of rounds.
	 * 
	 * @return the number of rounds
	 */
	public int getNumRounds () {
		return numrounds_;
	}

	/**
	 * Get the time limit (ms).
	 * 
	 * @return the time limit.
	 */
	public long getTimeLimit () {
		if ( hasTimeLimit() ) {
			return limit_;
		}
		return 0;
	}
}
