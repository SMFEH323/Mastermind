
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Mastermind main program and GUI.
 */
public class MastermindGUI extends Application {

	public static void main ( String[] args ) {
		launch(args);
	}

	// diameter of circle for code pegs
	private static final double CODE_PEG_SIZE = 40;

	// colors for code pegs
	public static final Color[] CODE_PEG_COLORS =
	    { Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.WHITE,
	      Color.BLACK, Color.MAGENTA, Color.CYAN };

	// colors for the key (feedback) pegs
	public static final Color KEY_PEG_CORRECT = Color.BLACK,
	    KEY_PEG_WRONGPOS = Color.WHITE;

	private Controller controller_; // controller for client-side game logic

	private Stage stage_; // main application window
	private BorderPane gamedisplay_; // container for the game play elements

	// game UI elements
	private Label playerscore_, computerscore_; // score displays
	private MenuItem startitem_, enditem_, optionsitem_, settingsitem_; // menu
	                                                                    // items
	private Image checkmark_; // image for submit guess button
	private Font font_; // label font

	// codebreaker UI elements
	private Peg[][] guesses_; // the displayed guesses (code pegs)
	private Peg[][] feedback_; // the displayed feedback (key pegs)
	private Button[] submitbtns_; // the submit guess buttons

	// codemaker UI elements
	private Peg[] code_; // the player's code

	@Override
	public void start ( Stage stage ) throws Exception {
		stage_ = stage;
		controller_ = new Controller(this);
		checkmark_ = new Image("file:images/checkmark.png");
		font_ = Font.font("Arial",FontWeight.NORMAL,FontPosture.REGULAR,18);

		stage.setTitle("Mastermind");

		BorderPane root = new BorderPane();
		root.setStyle("-fx-background-color: darkslategray");

		Scene scene = new Scene(root);
//		scene.setFill(Color.TRANSPARENT);

		stage.setScene(scene);
		stage.setResizable(false);

		gamedisplay_ = new BorderPane();
		root.setCenter(gamedisplay_);

		MenuBar menubar = new MenuBar();

		Menu gamemenu = new Menu("Game");
		Menu configmenu = new Menu("Configure");

		// starts a new game with the same variation, difficulty, etc
		startitem_ = new MenuItem("Start New Game");
		startitem_.setDisable(true);
		// ends the current game
		enditem_ = new MenuItem("End Game");
		// quits the program
		MenuItem quititem = new MenuItem("Quit");

		startitem_.setOnAction(e -> {
			controller_.startPlay();
		});

		enditem_.setOnAction(e -> {
			controller_.abortPlay();
		});

		// set the game variation, difficulty, etc
		optionsitem_ = new MenuItem("Game Options");
		optionsitem_.setDisable(true);

		optionsitem_.setOnAction(e -> {
			GameOptions newoptions =
			    configureGameOptions(controller_.getOptions(),true);
			if ( newoptions != null ) {
				controller_.setGameOptions(newoptions);
//				controller_.startPlay();
			}
		});

		// set the server
		settingsitem_ = new MenuItem("Server Settings");
		settingsitem_.setDisable(true);

		settingsitem_.setOnAction(e -> {
			ServerSettings newsettings =
			    configureServerSettings(controller_.getServerSettings(),true);
			if ( newsettings != null ) {
				controller_.setServerSettings(newsettings);
//				controller_.startPlay();
			}
		});

		quititem.setOnAction(e -> Platform.exit());

		gamemenu.getItems().addAll(startitem_,enditem_,new SeparatorMenuItem(),
		                           quititem);
		configmenu.getItems().addAll(optionsitem_,settingsitem_);

		menubar.getMenus().addAll(gamemenu,configmenu);
		root.setTop(menubar);

		{
			HBox scorepane = new HBox();
			scorepane.setSpacing(10);

			Label playerlabel = new Label("player score: ");
			playerlabel.setFont(font_);
			playerlabel.setTextFill(Color.WHITE);

			playerscore_ = new Label("0");
			playerscore_.setFont(font_);
			playerscore_.setTextFill(Color.WHITE);
			playerscore_.setMaxWidth(Double.POSITIVE_INFINITY);
			HBox.setHgrow(playerscore_,Priority.ALWAYS);

			Label computerlabel = new Label("computer score: ");
			computerlabel.setFont(font_);
			computerlabel.setTextFill(Color.WHITE);

			computerscore_ = new Label("0");
			computerscore_.setFont(font_);
			computerscore_.setTextFill(Color.WHITE);
			computerscore_.setMaxWidth(Double.POSITIVE_INFINITY);
			HBox.setHgrow(computerscore_,Priority.ALWAYS);

			scorepane.getChildren().addAll(playerlabel,playerscore_,computerlabel,
			                               computerscore_);

			gamedisplay_.setTop(scorepane);
			BorderPane.setMargin(scorepane,new Insets(10,20,10,10));
		}

//		GameOptions options = configureGameOptions(controller_.getOptions(),false);
//		int numrounds = getNumRounds(controller_.getNumRounds(),false);
		ServerSettings settings =
		    configureServerSettings(controller_.getServerSettings(),false);

		controller_.setServerSettings(settings);
		controller_.startPlay();

		stage.show();
	}

	/**
	 * Make the specified guess-entry control active so the player can drag pegs
	 * to specify their guess.
	 * 
	 * @param which
	 *          which guess (0-based)
	 */
	public void activateGuess ( int which ) {
		for ( int ctr = 0 ; ctr < guesses_[which].length ; ctr++ ) {
			Peg peg = guesses_[which][ctr];
			peg.getCanvas().setOnDragDropped(e -> {
				Dragboard db = e.getDragboard();
				boolean success = false;
				if ( db.hasString() ) {
					Color color = Color.valueOf(db.getString());
					peg.setColor(color);
					success = true;
				}
				e.setDropCompleted(success);
				e.consume();
			});
			peg.getCanvas().setOnDragOver(e -> {
				if ( e.getDragboard().hasString() ) {
					e.acceptTransferModes(TransferMode.ANY);
				}
				e.consume();
			});
			peg.getCanvas().setOnDragEntered(e -> {
				if ( e.getDragboard().hasString() ) {
					peg.highlight(Color.valueOf(e.getDragboard().getString()));
				}
			});
			peg.getCanvas().setOnDragExited(e -> {
				if ( e.getDragboard().hasString() ) {
					peg.unhighlight();
				}
			});
		}

		if ( submitbtns_ != null ) {
			submitbtns_[which].setDisable(false);
		}
	}

	/**
	 * Clear the game display area - removes codebreaker and codemaker controls.
	 */
	public void clearGameDisplay () {
		gamedisplay_.setLeft(null);
		gamedisplay_.setCenter(null);
		gamedisplay_.setRight(null);
		gamedisplay_.setBottom(null);
	}

	/**
	 * Make the specified guess-entry control inactive.
	 * 
	 * @param which
	 *          which guess (0-based)
	 */
	public void deactivateGuess ( int which ) {
		for ( int ctr = 0 ; ctr < guesses_[which].length ; ctr++ ) {
			Peg peg = guesses_[which][ctr];
			peg.getCanvas().setOnDragDropped(e -> {});
			peg.getCanvas().setOnDragOver(e -> {});
			peg.getCanvas().setOnDragEntered(e -> {});
			peg.getCanvas().setOnDragExited(e -> {});
		}
		if ( submitbtns_ != null ) {
			submitbtns_[which].setDisable(true);
		}
	}

	/**
	 * Display an error message.
	 * 
	 * @param msg
	 *          error message to display
	 */
	public void displayError ( String msg ) {
		Alert alert = new Alert(Alert.AlertType.ERROR,msg);
		alert.showAndWait();
	}

	/**
	 * Display a guess, which consists of the string values for the code peg
	 * colors.
	 * 
	 * @param which
	 *          which guess this guess is (0-based)
	 * @param guess
	 *          the guess
	 */
	public void displayGuess ( int which, String[] guess ) {
		for ( int ctr = 0 ; ctr < guess.length ; ctr++ ) {
			guesses_[which][ctr].setColor(Color.valueOf(guess[ctr]));
		}
	}

	/**
	 * Display feedback (number of correct pegs, number of correct colors but
	 * wrong positions) for a guess.
	 * 
	 * @param which
	 *          which guess this is for (0-based)
	 * @param numcorrect
	 *          the number of correct pegs (color and position) in the guess
	 * @param numwrongpos
	 *          the number of correct color but wrong position pegs in the guess
	 */
	public void displayFeedback ( int which, int numcorrect, int numwrongpos ) {
		int i = 0;
		for ( int ctr = 0 ; ctr < numcorrect ; ctr++, i++ ) {
			feedback_[which][i].setColor(KEY_PEG_CORRECT);
		}
		for ( int ctr = 0 ; ctr < numwrongpos ; ctr++, i++ ) {
			feedback_[which][i].setColor(KEY_PEG_WRONGPOS);
		}
		for ( ; i < feedback_[which].length ; i++ ) {
			feedback_[which][i].setColor(null);
		}
	}

	/**
	 * Display the final game outcome.
	 * 
	 * @param msg
	 *          message to display
	 */
	public void displayGameOutcome ( String msg ) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION,msg);
		alert.setGraphic(null);
		alert.setHeaderText(null);
		alert.setTitle("Game Over!");
		alert.showAndWait();
	}

	/**
	 * Set the enabled/disabled status of the "end game" menu item.
	 * 
	 * @param enable
	 *          enabled status to set
	 */
	public void enableEndGame ( boolean enable ) {
		enditem_.setDisable(!enable);
	}

	/**
	 * Set the enabled/disabled status of the configuration-related menu items.
	 * 
	 * @param enable
	 *          enabled status to set
	 */
	public void enableConfig ( boolean enable ) {
		optionsitem_.setDisable(!enable);
		settingsitem_.setDisable(!enable);
	}

	/**
	 * Set the enabled/disabled status of the "start game" menu item.
	 * 
	 * @param enable
	 *          enabled status to set
	 */
	public void enableStartGame ( boolean enable ) {
		startitem_.setDisable(!enable);
	}

	/**
	 * Configure the game display for the player-as-codebreaker.
	 * 
	 * @param options
	 *          game options
	 */
	public void setupCodeBreakerDisplay ( GameOptions options ) {
		Pane pegpane = makePegPane(options.getNumColors(),true);
		Pane gamepane =
		    makeDisplayPane(options.getNumGuesses(),options.getCodeLength(),true);
		Label title =
		    new Label("playing as codebreaker\nround " + controller_.getRound());
		title.setFont(font_);
		title.setTextFill(Color.WHITE);
		title.setMaxWidth(Double.POSITIVE_INFINITY);
		title.setAlignment(Pos.CENTER);
		title.setTextAlignment(TextAlignment.CENTER);

		gamedisplay_.setLeft(pegpane);
		gamedisplay_.setCenter(gamepane);
		gamedisplay_.setRight(null);
		gamedisplay_.setBottom(title);

		BorderPane.setMargin(pegpane,new Insets(15,30,15,20));
		BorderPane.setMargin(gamepane,new Insets(10,15,10,5));
		BorderPane.setMargin(title,new Insets(5,0,10,0));

		stage_.sizeToScene();
	}

	/**
	 * Configure the display for the player-as-codemaker.
	 * 
	 * @param options
	 *          game options
	 */
	public void setupCodeMakerDisplay ( GameOptions options ) {
		Pane makerpane =
		    makeCodeMakerPane(options.getNumColors(),options.getCodeLength());
		Pane gamepane =
		    makeDisplayPane(options.getNumGuesses(),options.getCodeLength(),false);
		Label title =
		    new Label("playing as codemaker\nround " + controller_.getRound());
		title.setFont(font_);
		title.setTextFill(Color.WHITE);
		title.setMaxWidth(Double.POSITIVE_INFINITY);
		title.setAlignment(Pos.CENTER);
		title.setTextAlignment(TextAlignment.CENTER);

		gamedisplay_.setLeft(null);
		gamedisplay_.setCenter(makerpane);
		gamedisplay_.setRight(gamepane);
		gamedisplay_.setBottom(title);

		BorderPane.setMargin(makerpane,new Insets(15,30,15,20));
		BorderPane.setMargin(gamepane,new Insets(10,15,10,5));
		BorderPane.setMargin(title,new Insets(5,0,10,0));

		stage_.sizeToScene();
	}

	/**
	 * Update the score display.
	 * 
	 * @param player
	 *          player score
	 * @param computer
	 *          computer score
	 */
	public void updateScores ( int player, int computer ) {
		playerscore_.setText("" + player);
		computerscore_.setText("" + computer);
	}

	/**
	 * Display dialog box for player to set game options, returning the choices.
	 * 
	 * @param defaults
	 *          default values for the options
	 * @param allowcancel
	 *          if true, include a CANCEL button
	 * @return the selected options, or null if the operation is cancelled
	 */
	private GameOptions configureGameOptions ( GameOptions defaults,
	                                           boolean allowcancel ) {
		Dialog<GameOptions> dialog = new Dialog<GameOptions>();
		dialog.setTitle("Configure Mastermind");

		DialogPane dialogpane = dialog.getDialogPane();
		dialogpane.getButtonTypes().add(new ButtonType("OK",ButtonData.OK_DONE));
		if ( allowcancel ) {
			dialogpane.getButtonTypes()
			    .add(new ButtonType("Cancel",ButtonData.CANCEL_CLOSE));
		}

		GridPane pane = new GridPane();
		pane.setHgap(10);
		pane.setVgap(20);
		dialogpane.setContent(pane);

		Label variationslabel = new Label("variation");
		ComboBox<String> variations =
		    new ComboBox<String>(GameOptions.getVariations());
		variations.setValue(defaults.getVariation());
		pane.add(variationslabel,0,0);
		pane.add(variations,1,0);

		Label difficultylabel = new Label("difficulty");
		ComboBox<String> difficulty =
		    new ComboBox<String>(GameOptions.getDifficulties());
		difficulty.setValue(defaults.getDifficulty());
		pane.add(difficultylabel,0,1);
		pane.add(difficulty,1,1);

		Label timelimitlabel = new Label("set guess time limit?");
		CheckBox hastimelimit = new CheckBox();
		hastimelimit.setSelected(defaults.hasTimeLimit());
		pane.add(timelimitlabel,0,3);
		pane.add(hastimelimit,1,3);
		GridPane.setValignment(timelimitlabel,VPos.TOP);

		Label numroundslabel = new Label("number of rounds");
		Spinner<Integer> numrounds = new Spinner<Integer>();
		numrounds
		    .setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
		                                                                        20,
		                                                                        defaults
		                                                                            .getNumRounds()));
		pane.add(numroundslabel,0,4);
		pane.add(numrounds,1,4);

		dialog.setResultConverter(btn -> {
			if ( btn.getButtonData() == ButtonData.OK_DONE ) {
				return new GameOptions(variations.getValue(),difficulty.getValue(),
				                       hastimelimit.isSelected(),numrounds.getValue());
			} else {
				return null;
			}
		});

		Optional<GameOptions> response = dialog.showAndWait();
		if ( response.isPresent() ) {
			return response.get();
		} else {
			return null;
		}
	}

	/**
	 * Display dialog box for player to set server settings, returning the
	 * settings.
	 * 
	 * @param defaults
	 *          default values for the settings
	 * @param allowcancel
	 *          if true, include a CANCEL button
	 * @return the selected options, or null if the operation is cancelled
	 */
	private ServerSettings configureServerSettings ( ServerSettings defaults,
	                                                 boolean allowcancel ) {
		Dialog<ServerSettings> dialog = new Dialog<ServerSettings>();
		dialog.setTitle("Configure Server");

		DialogPane dialogpane = dialog.getDialogPane();
		dialogpane.getButtonTypes().add(new ButtonType("OK",ButtonData.OK_DONE));
		if ( allowcancel ) {
			dialogpane.getButtonTypes()
			    .add(new ButtonType("Cancel",ButtonData.CANCEL_CLOSE));
		}

		GridPane pane = new GridPane();
		pane.setHgap(10);
		pane.setVgap(20);
		dialogpane.setContent(pane);

		Label hostlabel = new Label("server host");
		TextField host = new TextField(defaults.getHost());
		pane.add(hostlabel,0,0);
		pane.add(host,1,0);

		Label portlabel = new Label("server port");
		TextField port = new TextField("" + defaults.getPort());
		port.setPrefColumnCount(6);
		pane.add(portlabel,0,1);
		pane.add(port,1,1);

		dialog.setResultConverter(btn -> {
			if ( btn.getButtonData() == ButtonData.OK_DONE ) {
				return new ServerSettings(host.getText(),
				                          Integer.parseInt(port.getText()));
			} else {
				return null;
			}
		});

		Optional<ServerSettings> response = dialog.showAndWait();
		if ( response.isPresent() ) {
			return response.get();
		} else {
			return null;
		}
	}

	/**
	 * Display a dialog box to get the number of rounds to play, returning the
	 * result.
	 * 
	 * @param rounds
	 *          default value for the number of rounds
	 * @param allowcancel
	 *          if true, include a CANCEL button
	 * @return the number of rounds, or -1 if the operation was cancelled
	 */
	private int configureNumRounds ( int rounds, boolean allowcancel ) {
		Dialog<Integer> dialog = new Dialog<Integer>();
		dialog.setTitle("Start Game");

		DialogPane dialogpane = dialog.getDialogPane();
		dialogpane.getButtonTypes().add(new ButtonType("OK",ButtonData.OK_DONE));
		if ( allowcancel ) {
			dialogpane.getButtonTypes()
			    .add(new ButtonType("Cancel",ButtonData.CANCEL_CLOSE));
		}

		HBox box = new HBox();
		dialogpane.setContent(box);
		box.setSpacing(10);

		Label numroundslabel = new Label("number of rounds");
		Spinner<Integer> numrounds = new Spinner<Integer>();
		numrounds
		    .setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
		                                                                        20,
		                                                                        rounds));
		box.setAlignment(Pos.CENTER_LEFT);
		box.getChildren().addAll(numroundslabel,numrounds);

		dialog.setResultConverter(btn -> {
			if ( btn.getButtonData() == ButtonData.OK_DONE ) {
				return numrounds.getValue();
			} else {
				return -1;
			}
		});

		Optional<Integer> response = dialog.showAndWait();
		if ( response.isPresent() ) {
			return response.get();
		} else {
			return -1;
		}
	}

	/**
	 * Set up the display for the codemaker - it allows the player to specify a
	 * code. Returns the pane containing the UI elements.
	 * 
	 * @param numcolors
	 *          number peg colors allowed
	 * @param codelength
	 *          number of holes (length of the code)
	 * @return the pane containing the UI elements
	 */
	private Pane makeCodeMakerPane ( int numcolors, int codelength ) {
		code_ = new Peg[codelength];

		VBox pane = new VBox();
		pane.setSpacing(20);

		HBox codepane = new HBox();
		for ( int ctr = 0 ; ctr < codelength ; ctr++ ) {
			code_[ctr] = new Peg(CODE_PEG_SIZE * 1.5);
			codepane.getChildren().add(code_[ctr].getCanvas());
			HBox.setMargin(code_[ctr].getCanvas(),new Insets(3,3,3,3));

			Peg peg = code_[ctr];
			peg.getCanvas().setOnDragDropped(e -> {
				Dragboard db = e.getDragboard();
				boolean success = false;
				if ( db.hasString() ) {
					Color color = Color.valueOf(db.getString());
					peg.setColor(color);
					success = true;
				}
				e.setDropCompleted(success);
				e.consume();
			});
			peg.getCanvas().setOnDragOver(e -> {
				if ( e.getDragboard().hasString() ) {
					e.acceptTransferModes(TransferMode.ANY);
				}
				e.consume();
			});
			peg.getCanvas().setOnDragEntered(e -> {
				if ( e.getDragboard().hasString() ) {
					peg.highlight(Color.valueOf(e.getDragboard().getString()));
				}
			});
			peg.getCanvas().setOnDragExited(e -> {
				if ( e.getDragboard().hasString() ) {
					peg.unhighlight();
				}
			});
		}
		codepane
		    .setStyle("-fx-border-color: black; -fx-border-radius: 20 20 20 20");
		codepane.setMaxWidth(CODE_PEG_SIZE * codelength);
//		codepane.setAlignment(Pos.CENTER);

		Button setcodebtn = new Button("set code");
		setcodebtn.setOnAction(e -> controller_.playCodeMaker(code_));

		pane.getChildren().addAll(makePegPane(numcolors,false),codepane,setcodebtn);
		pane.setAlignment(Pos.CENTER);

		return pane;
	}

	/**
	 * Set up the display for the guesses and feedback. For the codebreaker, this
	 * also allows entry of the player's guesses. Returns the pane containing the
	 * UI elements.
	 * 
	 * @param numguesses
	 *          number of guesses allowed
	 * @param codelength
	 *          number of holes (length of the code)
	 * @param breakermode
	 *          if true, this is for the codebreaker - it includes controls to
	 *          submit the guesses
	 * @return the pane containing the UI elements
	 */
	private Pane makeDisplayPane ( int numguesses, int codelength,
	                               boolean breakermode ) {
		VBox gamepane = new VBox();
		gamepane.setSpacing(5);

		guesses_ = new Peg[numguesses][codelength];
		feedback_ = new Peg[numguesses][codelength];
		submitbtns_ = (breakermode ? new Button[numguesses] : null);
		for ( int ctr = 0 ; ctr < guesses_.length ; ctr++ ) {
			HBox guessresponse = new HBox();
			guessresponse.setSpacing(20);
			guessresponse.setAlignment(Pos.CENTER);

			{
				HBox guess = new HBox();
				for ( int ctr2 = 0 ; ctr2 < guesses_[ctr].length ; ctr2++ ) {
					guesses_[ctr][ctr2] = new Peg(CODE_PEG_SIZE);
					guess.getChildren().add(guesses_[ctr][ctr2].getCanvas());
					HBox.setMargin(guesses_[ctr][ctr2].getCanvas(),new Insets(3,3,3,3));
				}
				guess
				    .setStyle("-fx-border-color: black; -fx-border-radius: 20 20 20 20");
				guessresponse.getChildren().add(guess);
			}
			if ( breakermode ) {
				ImageView icon = new ImageView(checkmark_);
				icon.setFitWidth(40);
				icon.setPreserveRatio(true);
				submitbtns_[ctr] = new Button("",icon);
				submitbtns_[ctr].setStyle("-fx-background-color: transparent");
				{
					int which = ctr;
					submitbtns_[ctr].setOnAction(e -> {
						controller_.makeGuess(guesses_[which]);
					});
				}
				guessresponse.getChildren().add(submitbtns_[ctr]);
			}
			{
				GridPane response = new GridPane();
				response.setAlignment(Pos.CENTER);
				response.setPrefSize(CODE_PEG_SIZE,CODE_PEG_SIZE);

				int rowlen = (int) Math.ceil(Math.sqrt(codelength));
				for ( int ctr2 = 0 ; ctr2 < feedback_[ctr].length ; ctr2++ ) {
					feedback_[ctr][ctr2] = new Peg(CODE_PEG_SIZE / 3);
					response.add(feedback_[ctr][ctr2].getCanvas(),ctr2 % rowlen,
					             ctr2 / rowlen,1,1);
					GridPane.setMargin(feedback_[ctr][ctr2].getCanvas(),
					                   new Insets(2,2,2,2));
				}
				response
				    .setStyle("-fx-border-color: black; -fx-border-radius: 20 20 20 20");
				guessresponse.getChildren().add(response);
			}

			deactivateGuess(ctr);

			gamepane.getChildren().add(guessresponse);
		}

		return gamepane;
	}

	/**
	 * Create a pane displaying the available code peg colors.
	 * 
	 * @param numcolors
	 *          number of code peg colors
	 * @param vertical
	 *          if true, arrange the colors vertically; if false, arrange them
	 *          horizontally
	 * @return the pane containing the UI elements
	 */
	private Pane makePegPane ( int numcolors, boolean vertical ) {
		Pane pane;

		if ( vertical ) {
			VBox pegpane = new VBox();
			pegpane.setAlignment(Pos.CENTER);
			pegpane.setSpacing(10);
			pane = pegpane;
		} else {
			HBox pegpane = new HBox();
			pegpane.setAlignment(Pos.CENTER);
			pegpane.setSpacing(10);
			pane = pegpane;
		}

		Peg[] pegs = new Peg[numcolors];
		for ( int ctr = 0 ; ctr < numcolors ; ctr++ ) {
			pegs[ctr] = new Peg(CODE_PEG_COLORS[ctr],CODE_PEG_SIZE);
			pane.getChildren().add(pegs[ctr].getCanvas());
			{
				Peg peg = pegs[ctr];
				pegs[ctr].getCanvas().setOnDragDetected(e -> {
					Dragboard db = peg.getCanvas().startDragAndDrop(TransferMode.ANY);
					ClipboardContent content = new ClipboardContent();
					content.putString(peg.getColor().toString());
					db.setContent(content);
					e.consume();
				});
			}
		}

		return pane;
	}

}
