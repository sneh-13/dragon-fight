/*************************************************
 * Group D
 * ICS4U
 * January 20, 2023
 * Dragon Fight
 * This file creates the frame for the game, and starts the game thread. It also loads the 
 * high score at the start of the game.
 ************************************************/
import javax.swing.JFrame;

public class DragonFightMain { //implements runnable is for the game thread, extend JPanel to use the paint component method

	public static JFrame frame = new JFrame();

	public static void main(String[] args) {

		frame.setTitle("Dragon Fight");
		frame.setResizable(false);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //disable the x so that the users are forced to use the quit button, therefore ensuring that their score is saved if they won

		Screen gameScreen = new Screen(); //Instantiate the screen
		frame.add(gameScreen);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		gameScreen.startGameThread(); //start the game thread
		gameScreen.loadPreviousHighScore(); //load the high score
	}

}

// for jar file E:/>java.exe -jar nameoffile.jar
