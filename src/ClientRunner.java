import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sayf Elhawary
 */
public class ClientRunner implements Runnable {

	private Socket connection_;

	public ClientRunner ( Socket connection ) {
		connection_ = connection;
	}

	public void run () {

		try {
			// get streams for the connection
			BufferedReader reader =
			    new BufferedReader(new InputStreamReader(connection_
			        .getInputStream()));
			PrintWriter writer = new PrintWriter(connection_.getOutputStream());
			long timeLimit = Long.parseLong(reader.readLine());
			System.out.println("recieved time limit details");
			int numColors = Integer.parseInt(reader.readLine());
			System.out.println("recieved num colors");
			int codeLength = Integer.parseInt(reader.readLine());
			String[] cpuCodePegs = new String[codeLength];
			String[] playerCodePegs = new String[codeLength];
			String[] cpuGuess = new String[codeLength];
			System.out.println("recieved code length");
			int numGuesses = Integer.parseInt(reader.readLine());
			System.out.println("recieved num guesses");
			int numRounds = Integer.parseInt(reader.readLine());
			System.out.println("recieved num rounds");
			int playerScore = 0;
			int cpuScore = 0;

			for ( int roundNum = 0 ; roundNum < numRounds ; roundNum++ ) {
				boolean playerWonCodebreaker = false;
				for ( int i = 0 ; i < codeLength ; i++ ) {
					cpuCodePegs[i] =
					    MastermindGUI.CODE_PEG_COLORS[(int) (Math.random() * numColors)]
					        .toString();
				}
				System.out.println("created code for codebreaker");
				writer.println("codebreaker");
				System.out.println("told client to start codebreaker turn");
				writer.flush();
				writer.println(playerScore);
				System.out.println("sent player score");
				writer.println(cpuScore);
				System.out.println("sent cpu score");
				writer.flush();
				for ( int guessNum = 0 ; guessNum < numGuesses ; guessNum++ ) {
					String[] playerGuess = new String[codeLength];
					boolean completed = true;
					if ( timeLimit > 0 ) {
						TimerTask task = new TimerTask() {
							public void run () {
								System.out.println("Timer task started at:" + new Date());
								for ( int i = 0 ; i < codeLength ; i++ ) {
									try {
										playerGuess[i] = reader.readLine();
									} catch ( IOException e ) {
										e.printStackTrace();
									}
								}
								System.out.println("Timer task finished at:" + new Date());
								cancel();
							}
						};
						Timer timer = new Timer();
						timer.schedule(task,new Date());
						System.out.println("TimerTask started");
						try {
							Thread.sleep(timeLimit);
						} catch ( InterruptedException e ) {
							e.printStackTrace();
						}
						timer.cancel();
						System.out.println("TimerTask cancelled");
						for ( int i = 0 ; i < codeLength ; i++ ) {
							if ( playerGuess[i] == null ) {
								completed = false;
							}
						}
					} else {
						for ( int i = 0 ; i < codeLength ; i++ ) {
							playerGuess[i] = reader.readLine();
						}
					}
					if ( completed ) {
						System.out.println("recieved player guess");
						writer.println("just in time");
						System.out.println("sent to client on time confirmation");
						writer.flush();
						int numcorrect = 0;
						int numwrongpos = 0;
						for ( int i = 0 ; i < codeLength ; i++ ) {
							List<Integer> locations = new ArrayList<Integer>();
							int colorCorrect = 0;
							boolean contains = false;
							for ( int j = 0 ; j < codeLength ; j++ ) {
								if ( playerGuess[i].equals(cpuCodePegs[j]) ) {
									locations.add(j);
									contains = true;
								}
							}

							for ( int j = 0 ; j < locations.size() ; j++ ) {
								if ( i == j ) {
									colorCorrect++;
									numcorrect++;
									break;
								}
							}

							if ( contains && colorCorrect == 0 ) {
								numwrongpos++;
							}
						}
						writer.println(guessNum);
						writer.println(numcorrect);
						if ( numcorrect == codeLength ) {
							playerWonCodebreaker = true;
						}
						writer.println(numwrongpos);
						System.out
						    .println("checked player guess and sent feedback to client");
						writer.flush();

						if ( guessNum + 1 == numGuesses || numcorrect == codeLength ) {
							writer.println("no");
							System.out
							    .println("sent to client if the player has more guesses or code guesses");
							writer.flush();
							break;
						} else {
							writer.println("yes");
							System.out
							    .println("sent to client if the player has more guesses or code guesses");
							writer.flush();
						}
					} else {
						System.out.println("time ran out for guess");
						writer.println("out of time");
						System.out.println("sent to client on time confirmation");
						writer.flush();
					}

				}

				if ( playerWonCodebreaker ) {
					playerScore = playerScore + 50;
				} else {
					cpuScore = cpuScore + 50;
				}

				writer.println(playerScore);
				System.out.println("sent player score");
				writer.println(cpuScore);
				System.out.println("sent cpu score");
				writer.flush();

				boolean playerWonCodemaker = true;
				writer.println("codemaker");
				System.out.println("told client to start codemaker turn");
				writer.flush();

				for ( int i = 0 ; i < codeLength ; i++ ) {
					playerCodePegs[i] = reader.readLine();
				}
				System.out.println("recieved code from client");

				for ( int guessNum = 0 ; guessNum < numGuesses ; guessNum++ ) {
					writer.println(guessNum);
					for ( int i = 0 ; i < codeLength ; i++ ) {
						cpuGuess[i] =
						    MastermindGUI.CODE_PEG_COLORS[(int) (Math.random() * numColors)]
						        .toString();
						writer.println(cpuGuess[i].toString());
					}
					System.out.println("made guess and sent guess to client");
					writer.flush();

					int numcorrect = 0;
					int numwrongpos = 0;
					for ( int i = 0 ; i < codeLength ; i++ ) {
						List<Integer> locations = new ArrayList<Integer>();
						int colorCorrect = 0;
						boolean contains = false;
						for ( int j = 0 ; j < codeLength ; j++ ) {
							if ( cpuGuess[i].equals(playerCodePegs[j]) ) {
								locations.add(j);
								contains = true;
							}
						}

						for ( int j = 0 ; j < locations.size() ; j++ ) {
							if ( i == j ) {
								colorCorrect++;
								numcorrect++;
								break;
							}
						}

						if ( contains && colorCorrect == 0 ) {
							numwrongpos++;
						}
					}
					writer.println(numcorrect);
					if ( numcorrect == codeLength ) {
						playerWonCodemaker = false;
					}
					writer.println(numwrongpos);
					System.out
					    .println("checked server guess and sent feedback to client");
					writer.flush();

					if ( guessNum + 1 == numGuesses || numcorrect == codeLength ) {
						System.out
						    .println("sent to client if the player has more guesses or code guesses");
						break;
					} else {
						System.out
						    .println("sent to client if the player has more guesses or code guesses");
					}
				}

				if ( playerWonCodemaker ) {
					playerScore = playerScore + 50;
				} else {
					cpuScore = cpuScore + 50;
				}
				writer.println(playerScore);
				System.out.println("sent player score");
				writer.println(cpuScore);
				System.out.println("sent cpu score");
				writer.flush();

				if ( roundNum + 1 == numRounds ) {
					writer.println("no");
					System.out
					    .println("sent to client if the player has more rounds or game is over");
					writer.flush();
					break;
				} else {
					writer.println("yes");
					System.out
					    .println("sent to client if the player has more rounds or game is over");
					writer.flush();
				}
			}

			if ( playerScore > cpuScore ) {
				writer.println("You Won!!!");
			} else if ( playerScore < cpuScore ) {
				writer.println("You Lost :(");
			} else {
				writer.println("Draw");
			}
			writer.flush();

		} catch ( IOException e ) {
			System.out.println("communication error - connection terminated");
		}

	}

}
