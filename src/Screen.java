/*************************************************
 * Group D
 * ICS4U
 * January 20, 2023
 * Dragon Fight
 * This is the screen file, and this file is treated like the main file of the game. This file contains all GUI components, and handles the switching of 
 * levels, adding and removing enemies, and changing the maps. This file also contains the game thread, and updates and draws all entities on screen. The
 * defaults for the game that are used throughout the classes are created here, such as default tile size, and default screen dimensions.
 ************************************************/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Screen extends JPanel implements Runnable { //must implement runnable as opposed to extending thread, this is because Java does not allow for multiple inheritance, and since JPanel must be extended to create the paint components method, there is no room to extend Thread
	public static int tileSize = 48; //(one tile is 48 pixels by 48 pixels)
	public static int maxColumnSize = 16; //the biggest a column can be will be 16 tiles
	public static int maxRowSize = 12; //the biggest a row can be will be 12 tiles
	public static int screenWidth = maxColumnSize*tileSize; // the screen will be 768 pixels wide (16 tiles * 48 pixels)
	public static int screenHeight = maxRowSize*tileSize; // the screen will be 576 pixels long (12 tiles * 48 pixels)
	public static final int FPS = 60; //final because FPS should never be changed
	public static Thread gameThread;
	KeyboardListener keyboardListener = new KeyboardListener(this); 
	ManageTiles manageTiles = new ManageTiles(this); //adding this means add this class which is the class for the JPanel
	Player player = new Player(this,keyboardListener,manageTiles);
	ArrayList <EnemyOne> e1 = new ArrayList<>();
	ArrayList <EnemyTwo> e2 = new ArrayList<>();
	ArrayList <EnemyThree> e3 = new ArrayList<>();
	Fireball fB = new Fireball(this, keyboardListener, player);
	EnemyFour e4 = new EnemyFour(this,player);
	boolean createEnemiesLevelOne = true;
	boolean createEnemiesLevelTwo = true;
	boolean createEnemiesLevelThree = true;
	public int gameState;
	public final int runState = 1;
	public final int menuState = 2;
	public final int end_State = 3;
	public final int levelScreenState = 4;
	private JLabel lblDragonFight;
	JButton btnStart = new JButton("Play");
	JButton btnHelp = new JButton("Help");
	JButton btnQuit = new JButton("Quit");
	JButton btnWinQuit = new JButton("Quit");
	JButton btnRestart = new JButton("Restart");
	Rectangle topRight = new Rectangle (screenWidth/2,0,screenWidth/2,screenHeight/2);
	Rectangle bottomRight = new Rectangle (screenWidth/2,screenHeight/2,screenWidth/2,screenHeight/2);
	Rectangle topLeft = new Rectangle (0,0,screenWidth/2,screenHeight/2);
	Rectangle bottomLeft = new Rectangle (0,screenHeight/2,screenWidth/2,screenHeight/2);
	JButton btnBack = new JButton("Back");
	JLabel htpLbl = new JLabel("How To Play?");
	JLabel ruleslbl = new JLabel("Rules:");
	JLabel lbl2 = new JLabel("-Use Spacebar to fly up");
	JLabel lblFireballInstructions = new JLabel("-Use S to shoot a fireball");
	JLabel lbl3 = new JLabel("-Press Esc to Pause the Game");
	JLabel lbl4 = new JLabel("-You are the dragon");
	JLabel lbl5 = new JLabel("-There are 3 levels to clear");
	JLabel ctrlLbl = new JLabel("Controls:");
	JLabel lbl1 = new JLabel("-Use A and D keys to move Left and Right");
	JLabel lbl6 = new JLabel("-You must defeat all enimies on screen");
	JLabel lbl7 = new JLabel("to clear the level");
	JLabel lbl8 = new JLabel("-Use the fireballs or hit enemy balloons ");
	JLabel lbl9 = new JLabel("to pop them");
	JLabel lblPointsTip = new JLabel("-Popping an enemy parachute rewards more points");
	private final JPanel panel_end = new JPanel();
	JPanel panel = new JPanel();
	JPanel levelChangePanel = new JPanel();
	JLabel levelChangeJLabel = new JLabel("LEVEL ONE");
	int runOnce = 0;
	int previousHighScore = 0;
	TestingRectangles TR = new TestingRectangles(this);
	int showLevelOnePanel = 0;
	public boolean newGame = true;
	public boolean canResetMap = false;
	public int level = 1;
	public int runLevelChange = 0;
	public boolean canChangeLevel = true;
	public boolean testingMode = false;
	public int enemyStartCounter = 0;
	Sound sound = new Sound();
	boolean startMusicOnce = true;
	int previousLevelScore = 0;
	boolean readScore = true;
	JLabel lblPlayerPoints = new JLabel("");
	JLabel lblHighScore = new JLabel("");

	//2 when in help screen
	//1 when in start screen
	//3 when in end screen
	int fix =1;
	public JButton btnWinRestart = new JButton("New Game");

	public Screen () { //Constructor for screen class, the constructor sets all of the GUI
		this.setPreferredSize(new Dimension (screenWidth,screenHeight)); //set the size of the game panel
		this.setDoubleBuffered(true); //this is to stop the possibility of screen flickering
		this.setBackground(new Color(137,194,251)); //make the game panel the same blue as the sky tiles
		//this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(keyboardListener);
		setLayout(null);
		panel_end.setBackground(Color.black);

		add(panel);
		panel.setBounds(0, 0, 768, 576);
		panel.setBackground(new Color(87, 171, 255));
		panel.setForeground(new Color(255, 255, 255));
		panel.setLayout(null);

		lblDragonFight = new JLabel("DRAGON FIGHT");
		lblDragonFight.setBounds(201, 49, 375, 77);
		panel.add(lblDragonFight);
		lblDragonFight.setFont(new Font("Arial Black", Font.PLAIN, 42));
		lblDragonFight.setForeground(Color.RED);

		btnBack.setBounds(25, 505, 117, 29);
		panel.add(btnBack);
		btnBack.setFocusable(false);

		btnStart.setBounds(300, 175, 150, 60);
		btnStart.setFocusable(false);
		panel.add(btnStart);

		htpLbl.setFont(new Font("Times New Roman", Font.ITALIC, 40));
		htpLbl.setBounds(267, 25, 239, 53);
		panel.add(htpLbl);

		ruleslbl.setFont(new Font("Times New Roman", Font.ITALIC, 30));
		ruleslbl.setBounds(429, 124, 127, 40);
		panel.add(ruleslbl);

		lbl2.setFont(new Font("Times New Roman", Font.ITALIC, 18));
		lbl2.setBounds(10, 250, 358, 40);
		panel.add(lbl2);
		
		lblFireballInstructions.setFont(new Font("Times New Roman", Font.ITALIC, 18));
		lblFireballInstructions.setBounds(10, 300, 358, 40);
		panel.add(lblFireballInstructions);

		lbl3.setFont(new Font("Times New Roman", Font.ITALIC, 21));
		lbl3.setBounds(10, 346, 358, 40);
		panel.add(lbl3);

		lbl4.setFont(new Font("Times New Roman", Font.ITALIC, 19));
		lbl4.setBounds(429, 187, 190, 32);
		panel.add(lbl4);

		lbl5.setFont(new Font("Times New Roman", Font.ITALIC, 21));
		lbl5.setBounds(419, 250, 267, 32);
		panel.add(lbl5);

		btnBack.setBounds(41, 467, 132, 80);
		panel.add(btnBack);
		ctrlLbl.setFont(new Font("Times New Roman", Font.ITALIC, 25));
		ctrlLbl.setBounds(41, 130, 132, 32);

		panel.add(ctrlLbl);
		lbl1.setFont(new Font("Times New Roman", Font.ITALIC, 18));
		lbl1.setBounds(10, 185, 316, 50);

		panel.add(lbl1);
		lbl6.setFont(new Font("Times New Roman", Font.ITALIC, 20));
		lbl6.setBounds(419, 331, 341, 32);

		panel.add(lbl6);
		lbl7.setFont(new Font("Times New Roman", Font.ITALIC, 21));
		lbl7.setBounds(486, 368, 165, 21);

		panel.add(lbl7);
		lbl8.setFont(new Font("Times New Roman", Font.ITALIC, 22));
		lbl8.setBounds(300, 422, 400, 40);

		panel.add(lbl8);
		lbl9.setFont(new Font("Times New Roman", Font.ITALIC, 22));
		lbl9.setBounds(466, 467, 206, 21);
		
		lblPointsTip.setFont(new Font("Times New Roman", Font.ITALIC, 22));
		lblPointsTip.setBounds(200, 500, 600, 30);
		panel.add(lblPointsTip);

		panel.add(lbl9);

		btnRestart.setVisible(false);
		btnBack.setVisible(false);
		htpLbl.setVisible(false);
		ctrlLbl.setVisible(false);
		ruleslbl.setVisible(false);
		lbl2.setVisible(false);
		lblFireballInstructions.setVisible(false);
		lbl3.setVisible(false);
		lbl4.setVisible(false);
		lbl5.setVisible(false);
		lbl1.setVisible(false);
		lbl6.setVisible(false);
		lbl7.setVisible(false);
		lbl8.setVisible(false);
		lbl9.setVisible(false);
		lblPointsTip.setVisible(false);


		btnHelp.addActionListener(new ActionListener() { //this button displays all of the controls and game details
			public void actionPerformed(ActionEvent e) {
				startSoundEffect(2);
				if (newGame == false) {
					btnRestart.setVisible(false);
				}
				btnQuit.setVisible(false);
				htpLbl.setVisible(true);
				btnBack.setVisible(true);
				ctrlLbl.setVisible(true);
				ruleslbl.setVisible(true);
				btnHelp.setVisible(false);
				btnStart.setVisible(false);
				lbl1.setVisible(true);
				lbl2.setVisible(true);
				lblFireballInstructions.setVisible(true);
				lbl3.setVisible(true);
				lbl4.setVisible(true);
				lbl5.setVisible(true);
				lbl6.setVisible(true);
				lbl7.setVisible(true);
				lbl8.setVisible(true);
				lbl9.setVisible(true);
				lblPointsTip.setVisible(true);
				lblDragonFight.setVisible(false);
				fix = 2;
			}
		});

		btnBack.addActionListener(new ActionListener() {//This button returns to the main menu screen, and sets all help instructions to be hidden
			@Override
			public void actionPerformed(ActionEvent e) {
				startSoundEffect(2);
				if (newGame == false) {
					btnRestart.setVisible(true);
				}
				btnQuit.setVisible(true);
				htpLbl.setVisible(false);
				btnBack.setVisible(false);
				ctrlLbl.setVisible(false);
				ruleslbl.setVisible(false);
				lbl1.setVisible(false);
				lbl2.setVisible(false);
				lblFireballInstructions.setVisible(false);
				lbl3.setVisible(false);
				lbl4.setVisible(false);
				lbl5.setVisible(false);
				lbl6.setVisible(false);
				lbl7.setVisible(false);
				lbl8.setVisible(false);
				lbl9.setVisible(false);
				lblPointsTip.setVisible(false);

				btnStart.setVisible(true);
				btnHelp.setVisible(true);
				lblDragonFight.setVisible(true);

				fix = 1;
			}
		});

		btnHelp.setBounds(300, 225, 150, 60);
		btnHelp.setFocusable(false);
		panel.add(btnHelp);

		btnQuit.setBounds(300, 300, 150, 60);
		btnQuit.setFocusable(false);
		btnQuit.addActionListener(new ActionListener() { //This button quits the game
			public void actionPerformed(ActionEvent e) {
				startSoundEffect(2);
				saveHighScore();
				System.exit(0);
			}
		});
		panel.add(btnQuit);

		btnWinQuit.setBounds(310, 375, 150, 60);
		btnWinQuit.setFocusable(false);
		btnWinQuit.addActionListener(new ActionListener() {//This button quits the game, however it only shows up when the player wins the game
			public void actionPerformed(ActionEvent e) {
				startSoundEffect(2);
				saveHighScore();
				System.exit(0);
			}
		});
		levelChangePanel.add(btnWinQuit);
		btnWinQuit.setVisible(false);

		JButton quitBtnEnd = new JButton ("Quit");
		quitBtnEnd.setFont(new Font("Katari", Font.BOLD, 72));
		quitBtnEnd.setBounds(156, 400, 471, 96);
		quitBtnEnd.setFocusable(false);
		panel_end.add(quitBtnEnd);		
		quitBtnEnd.addActionListener(new ActionListener() {//This button closes the game
			public void actionPerformed(ActionEvent e) {
				startSoundEffect(2);
				saveHighScore();
				System.exit(0);
			}
		});

		btnRestart.setFocusable(false);
		btnRestart.setBounds(300, 381, 150, 60);
		panel.add(btnRestart);
		btnRestart.addActionListener(new ActionListener() {//Restart button that resets everything to its original state as if no previous game had been started
			public void actionPerformed(ActionEvent e) {
				//System.out.println("RESTART THE GAME");
				btnWinRestart.setVisible(false);
			    lblHighScore.setVisible(false);
			    lblPlayerPoints.setVisible(false);
				if (player.points > previousHighScore) { //if the player had more points than the previous high score, change the high score to their score
					previousHighScore = player.points;
				}
				player.points = 0;
				level = 1;
				player.lives = 3;
				player.hitpoints = 2;
				newGame = true;
				createEnemiesLevelOne = true;
				createEnemiesLevelTwo = true;
				createEnemiesLevelThree = true;
				loadPreviousHighScore();
				player.setPlayerDefaults();
				resetMap();
				btnRestart.setVisible(false);
			}
		});

		btnStart.setBounds(300, 150, 150, 60);
		btnStart.setFocusable(false);
		btnStart.addActionListener(new ActionListener() {//play button, this button changes the game state to the panel that displays the level if there is a new game, and if it is not a new game, then it just goes to the run state, this is because if new game is false, then the user is in the pause menu, so they should be returned to the game right away
			public void actionPerformed(ActionEvent e) {
				startSoundEffect(2);
				if (newGame == true && player.lives > 0) {
					newGame = false;
					gameState = levelScreenState;
				} else {
					gameState = runState;
				}
			}
		});
		add(panel_end);
		panel_end.setVisible(false);
		panel_end.setBounds(0, 0, 768, 576);
		panel_end.setLayout(null);

		levelChangePanel.setBounds(0, 0, 768, 576);
		levelChangePanel.setBackground(new Color(137,194,251));
		add(levelChangePanel);
		levelChangePanel.setVisible(false);
		levelChangePanel.setLayout(null);

		levelChangeJLabel.setForeground(Color.RED);
		levelChangeJLabel.setFont(new Font("Tw Cen MT", Font.PLAIN, 60));
		levelChangeJLabel.setBounds(250, 200, 600, 140);
		levelChangePanel.add(levelChangeJLabel);
		btnWinRestart.setFocusable(false);
		btnWinRestart.setBounds(0, 0, 150, 60);

		btnWinRestart.setBounds(310, 300, 150, 60);
		btnWinRestart.setFocusable(false);
		btnWinRestart.addActionListener(new ActionListener() {//Button that restarts the game once the player has won, resets all values back to their original state, and saves the users score if their score is better than the high score
			public void actionPerformed(ActionEvent e) {
				//System.out.println("RESTART THE GAME");
				startSoundEffect(2);
				if (readScore == true) {
					readScore = false;
					loadPreviousHighScore();
				}
				btnWinRestart.setVisible(false);
				if (player.points > previousHighScore) {
					previousHighScore = player.points;
				}
				player.points = 0;
				player.lives = 3;
				player.hitpoints = 2;
				newGame = true;
				createEnemiesLevelOne = true;
				createEnemiesLevelTwo = true;
				createEnemiesLevelThree = true;
				player.setPlayerDefaults();
				gameState = menuState;
				level = 1;
				btnRestart.setVisible(false);
				lblHighScore.setVisible(false);
				lblPlayerPoints.setVisible(false);
			}
		});
		btnWinRestart.setVisible(false);
		levelChangePanel.add(btnWinRestart);
		lblPlayerPoints.setFont(new Font("Tahoma", Font.PLAIN, 18));

		lblPlayerPoints.setBounds(250, 450, 200, 20);
		levelChangePanel.add(lblPlayerPoints);
		lblPlayerPoints.setVisible(false);
		lblHighScore.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		lblHighScore.setBounds(400, 450, 200, 20);
		levelChangePanel.add(lblHighScore);
		lblHighScore.setVisible(false);
	}

	public void loseScreen() {//method that contains the GUI for the game over screen

		panel_end.setVisible(true);

		JLabel lblLose = new JLabel("GAME OVER");
		lblLose.setHorizontalAlignment(SwingConstants.CENTER);
		lblLose.setFont(new Font("Herculanum", Font.BOLD, 50));
		// lblLose.setBounds(300, 175, 150, 60);
		lblLose.setBounds(79, 63, 599, 155);
		lblLose.setForeground(Color.red);
		panel_end.add(lblLose);

		JLabel lblScore = new JLabel("Score:\n");
		lblScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblScore.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
		// lblScore.setBounds(300, 275, 150, 60);

		lblScore.setBounds(166, 230, 447, 36);
		lblScore.setForeground(Color.green);
		panel_end.add(lblScore);

		JButton btnStart2 = new JButton("Play Again");
		btnStart2.setBackground(Color.YELLOW);
		btnStart2.setFont(new Font("Katari", Font.BOLD, 72));
		btnStart2.setOpaque(true);
		btnStart2.setForeground(Color.MAGENTA);
		//btnStart2.setBackground(Color.pink);
		btnStart2.setBounds(156, 291, 471, 96);
		panel_end.add(btnStart2);

		btnStart2.setFocusable(false);
		btnStart2.addActionListener(new ActionListener() {//starts a new game if the user loses, resets important values to their original states
			public void actionPerformed(ActionEvent e) {
				startSoundEffect(2);
				player.points = 0;
				player.lives = 3;
				player.hitpoints = 2;
				newGame = true;
				e1.removeAll(e1);
				e2.removeAll(e2);
				e3.removeAll(e3);
				panel_end.setVisible(false);
				createEnemiesLevelOne = true;
				createEnemiesLevelTwo = true;
				createEnemiesLevelThree = true;
				player.setPlayerDefaults();
				gameState = menuState;
				level = 1;
				btnRestart.setVisible(false);
				lblHighScore.setVisible(false);
				lblPlayerPoints.setVisible(false);
			}
		});

	}

	public void loadPreviousHighScore () { //buffered reader that reads the previous high score saved in the HighScore.txt file
		try {
			BufferedReader readHighScore = new BufferedReader(new FileReader("HighScore.txt")); //read from high score text file
			String previousHighScoreString = readHighScore.readLine(); //create temporary string variable to store the previous high score
			previousHighScore = Integer.parseInt(previousHighScoreString); //cast temporary String to int and set to previous high score
			readHighScore.close(); //close reader
		} catch (Exception e) {

		}
	}
	public void checkForLevelChange () { //This method checks if the level should be changed to the next level
		if (e1.size() == 0 && e2.size() == 0 && e3.size() == 0 && gameState == runState && player.lives > 0) {//runs if all of the enemy arrays have a size of 0 meaning all the enemies are dead, and the game state is running, and the player has more lives than 0 meaning they are still alive
			if (level == 1) { //if the level is 1 change the level to 2, and set the previous level score to the current points, this is so that if the player dies in level 2, they will restart from how many points they had when they just started level 2
				previousLevelScore = player.points;
				level = 2;
			} else if (level == 2) { //if the level is 2 change it to 3
				previousLevelScore = player.points;
				level = 3;
			} else if (level == 3) { //if the level is 3 change it to 100 (the level I used to represent if the player has won), and set the text on the JLabels to display the points, so that the user can see how they compare to the High Score on the win screen
				previousLevelScore = 0;
				lblPlayerPoints.setText("Your Points: "+player.points);
				if (previousHighScore > player.points) {
					lblHighScore.setText("High Score: "+previousHighScore);
				} else {
					lblHighScore.setText("High Score: "+player.points);
				}
				level = 100;
			}
			gameState = levelScreenState;//change gameState to level screen state so that the user will see the screen telling them they are on the next level
		}
	}

	public void resetMap () { //method that resets the map, this is used each time the player dies, this is to ensure if the user has killed some of the enemies, when they die, all of the enemies will be reset even killed ones
		e1.removeAll(e1);//these next three lines clear all the remaining enemies from all of the arrays
		e2.removeAll(e2);
		e3.removeAll(e3);
		if (level == 1) { //if the level is 1, set the create level one enemies boolean variable to true, so that the enemy spawning code is allowed to respawn all of the enemies, the code below works exactly the same for levels 2 and 3
			createEnemiesLevelOne = true;
		} else if (level == 2) {
			createEnemiesLevelTwo = true;
		} else if (level == 3) {
			createEnemiesLevelThree = true;
		}
		player.setPlayerDefaults();//reset the player, this is so that the player always spawns in the same spot, health and hitpoints are not reset 
	}

	public void createEnemiesLevelOne () { //This method adds all the enemies to their arrays for level 1, and sets all of their spawns
		e1.add(new EnemyOne(this,player));
		e1.get(0).setSpawn(tileSize*2,tileSize*7 + tileSize/2);
		e1.add(new EnemyOne(this,player));
		e1.get(1).setSpawn(tileSize*6,tileSize*3 + tileSize/2);
		e1.add(new EnemyOne(this,player));
		e1.get(2).setSpawn(tileSize*4,tileSize*7 + tileSize/2);
		e1.add(new EnemyOne(this,player));
		e1.get(3).setSpawn(tileSize*10,tileSize*7 + tileSize/2);
		e1.add(new EnemyOne(this,player));
		e1.get(4).setSpawn(tileSize*8,tileSize*3 + tileSize/2);
		e1.add(new EnemyOne(this,player));
		e1.get(5).setSpawn(tileSize*12,tileSize*7 + tileSize/2);

		e2.add(new EnemyTwo(this,player));
		e2.get(0).setSpawn(tileSize*3,tileSize*7 + tileSize/2);
		e2.add(new EnemyTwo(this,player));
		e2.get(1).setSpawn(tileSize*11,tileSize*7 + tileSize/2);
		e2.add(new EnemyTwo(this,player));
		e2.get(2).setSpawn(tileSize*7,tileSize*3 + tileSize/2);
	}

	public void createEnemiesLevelTwo () { //This method adds all the enemies to their arrays for level 2, and sets all of their spawns
		e1.add(new EnemyOne(this,player));
		e1.get(0).setSpawn(tileSize*1, tileSize*8 + tileSize/2);
		e1.add(new EnemyOne(this,player));
		e1.get(1).setSpawn(tileSize*2, tileSize*8 + tileSize/2);
		e1.add(new EnemyOne(this,player));
		e1.get(2).setSpawn(tileSize*7, tileSize*1);

		e2.add(new EnemyTwo(this,player));
		e2.get(0).setSpawn(tileSize*7,tileSize*5 + tileSize/2);
		e2.add(new EnemyTwo(this,player));
		e2.get(1).setSpawn(tileSize*8,tileSize*5 + tileSize/2);
		e2.add(new EnemyTwo(this,player));
		e2.get(2).setSpawn(tileSize*11,tileSize*4 + tileSize/2);
		e2.add(new EnemyTwo(this,player));
		e2.get(3).setSpawn(tileSize*12,tileSize*4 + tileSize/2);
		e2.add(new EnemyTwo(this,player));
		e2.get(4).setSpawn(tileSize*12,tileSize*8 + tileSize/2);

		e3.add(new EnemyThree(this,player));
		e3.get(0).setSpawn(tileSize*4,tileSize/2);
	}

	public void createEnemiesLevelThree () { //This method adds all the enemies to their arrays for level 3, and sets all of their spawns

		e1.add(new EnemyOne(this,player));
		e1.get(0).setSpawn(tileSize*3, tileSize*9);
		e1.add(new EnemyOne(this,player));
		e1.get(1).setSpawn(tileSize*10, tileSize*6);

		e2.add(new EnemyTwo(this,player));
		e2.get(0).setSpawn(tileSize*7,tileSize*3);
		e2.add(new EnemyTwo(this,player));
		e2.get(1).setSpawn(tileSize*2,tileSize*9);
		e2.add(new EnemyTwo(this,player));
		e2.get(2).setSpawn(tileSize*10,tileSize*10);
		e2.add(new EnemyTwo(this,player));
		e2.get(3).setSpawn(tileSize*2,tileSize*3);
		e2.add(new EnemyTwo(this,player));
		e2.get(4).setSpawn(tileSize*2,tileSize*5);

		e3.add(new EnemyThree(this,player));
		e3.get(0).setSpawn(tileSize*10, tileSize*4);
		e3.add(new EnemyThree(this,player));
		e3.get(1).setSpawn(tileSize*4,tileSize*5);
		e3.add(new EnemyThree(this,player));
		e3.get(2).setSpawn(tileSize*9,tileSize*8);
	}

	public void updateAllEnemies () { //this method runs all of the update methods for all of the enemies
		if (e1.size() > 0) { //if the enemy run array is not empty, meaning there are some enemy ones alive on screen
			for (int i = 0; i < e1.size(); i++) { //update each enemy 1 spot in the array
				e1.get(i).update();
			}
		}
		//Enemy 2 and 3 code below works exactly the same as the enemy 1 code above, the logic checks if they exist on screen, then searches through each index, and updates every enemy
		if (e2.size() > 0) {
			for (int i = 0; i < e2.size(); i++) {
				e2.get(i).update();
			}
		}

		if (e3.size() > 0) {
			for (int i = 0; i < e3.size(); i++) {
				e3.get(i).update();
			}
		}
	}

	public void drawAllEnemies (Graphics2D g2) { //This method draws all of the enemies on screen, this method works exactly the same as the update method above, except it draws the enemies instead of updating them
		if (e1.size() > 0) {
			for (int i = 0; i < e1.size(); i++) {
				e1.get(i).draw(g2);
			}
		}

		if (e2.size() > 0) {
			for (int i = 0; i < e2.size(); i++) {
				e2.get(i).draw(g2);
			}
		}

		if (e3.size() > 0) {
			for (int i = 0; i < e3.size(); i++) {
				e3.get(i).draw(g2);
			}
		}
	}

	public void startGameThread () {//this method starts the game thread
		gameThread = new Thread(this); //"this" means we are passing the game panel class to the thread constructor
		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1000000000/FPS; //(nine 0's because thats how many nanoseconds in 1 second) this is how many times the program draws the screen so 60 times per second
		double nextDrawTime = System.nanoTime() + drawInterval; //current system time plus the draw interval 1/60 of a second so this will be saying that when the current system time equals the next interval time draw the screen again

		while (gameThread != null) { //as long as the game thread exists

			//1 UPDATE: update information such as character positions
			update();

			//2 DRAW: Draw the screen with the updated information
			repaint(); //this calls the paint component method

			try {
				double remainingTime = nextDrawTime - System.nanoTime(); //subtract the current time from the next draw time, returning how much time remains until the next draw time, and the thread has to sleep for the remaining time
				remainingTime = remainingTime/1000000; //the method only works with milliseconds so convert nanoseconds to milliseconds
				if (remainingTime < 0) { //if the update and repaint took longer than the draw interval, then no time is left so the thread does not need to sleep
					remainingTime = 0;
				}
				Thread.sleep((long)remainingTime); //this pauses the game loop until the sleep time is over, the sleep method only excepts the long variable so cast to long
				nextDrawTime = drawInterval + nextDrawTime; //Add the next draw interval for the next draw time in the loop

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void update() { //main update method for the game, this update method runs all of the other classes update methods, and also deals with the handling of switching between game states
		//System.out.println("LEVEL: "+level);
		if(gameState == levelScreenState) { //check if the game state is the level screen state
			enemyStartCounter = 0;
			panel_end.setVisible(false);
			if (level == 1) { //if the level is 1 create the level 1 enemies, and change the text on the panel to say level 1
				levelChangeJLabel.setText("LEVEL ONE");
				btnWinRestart.setVisible(false);
				btnWinQuit.setVisible(false);
				manageTiles.resetLevelArrays();
				if (createEnemiesLevelOne == true) {
					createEnemiesLevelOne = false;
					createEnemiesLevelOne();
				}
			} else if (level == 2) { //if the level is 2 create the level 2 enemies, and change the text on the panel to say level 2
				levelChangeJLabel.setText("LEVEL TWO");
				btnWinRestart.setVisible(false);
				btnWinQuit.setVisible(false);
				manageTiles.resetLevelArrays();
				if (createEnemiesLevelTwo == true) {
					createEnemiesLevelTwo = false;
					createEnemiesLevelTwo();
					player.setPlayerDefaults();
				}
			} else if (level == 3) {
				levelChangeJLabel.setText("LEVEL THREE"); //if the level is 3 create the level 3 enemies, and change the text on the panel to say level 3
				btnWinRestart.setVisible(false);
				btnWinQuit.setVisible(false);
				manageTiles.resetLevelArrays();
				if (createEnemiesLevelThree == true) {
					createEnemiesLevelThree = false;
					createEnemiesLevelThree();
					player.setPlayerDefaults();
				}
			} else if (level == 100) { //if the level is 100, the player has won, so make all the buttons that should exist on the win screen visible
				levelChangeJLabel.setText("YOU WON");
				lblPlayerPoints.setVisible(true);
				lblHighScore.setVisible(true);
				btnWinRestart.setVisible(true);
				btnWinQuit.setVisible(true);
			}
			if (canResetMap == true) { //this is true if the player dies, so if the player dies, reset all of the enemies
				resetMap();
				canResetMap = false;
			}
			showLevelOnePanel++;
			panel.setVisible(false);
			levelChangePanel.setVisible(true);
			if (showLevelOnePanel >= 60) {//60 frame (1 second) timer, this is how long the level panel is shown for, after 1 second the game state is changed to run state, so the level changing screen will no longer be showing
				levelChangePanel.setVisible(false);
				showLevelOnePanel = 0;
				gameState = runState;
			}
		}
		if(gameState == runState && level != 100) { //If the game state is in run state, and the level is not 100 (the player has won level), start the music, and update all of the entities on screen
			if (startMusicOnce == true) { //start the music
				startMusicOnce = false;
				startMusic();
			}
			enemyStartCounter++; //increment enemy counter
			btnRestart.setVisible(true); //deal with the visibility of all JButtons
			btnWinRestart.setVisible(false);
			btnWinQuit.setVisible(false);
			btnHelp.setVisible(false);
			btnStart.setVisible(false);
			player.update(); //update the player
			if (enemyStartCounter > 90) { //delay enemies from starting for 1.5 seconds
				updateAllEnemies();//update all of the enemies
			}
			fB.update(); //update the fireball
		}
		if(gameState == menuState) { //if the game state is in menu state, display all of the menu options, and stop the music
			panel_end.setVisible(false);
			stopMusic();
			btnWinRestart.setVisible(false);
			btnWinQuit.setVisible(false);
			panel.setVisible(true);
			lblDragonFight.setVisible(true);
			if (fix == 1) {
				btnHelp.setVisible(true);
				btnStart.setVisible(true);
			} else if (fix ==2) {
				btnHelp.setVisible(false);
				btnStart.setVisible(false);
			}
		}
		if (gameState == end_State) { //if the game state is end state (they have lost), display all of the end state options
			btnRestart.setVisible(false);
			btnWinRestart.setVisible(false);
			btnWinQuit.setVisible(false);
			btnHelp.setVisible(false);
			btnStart.setVisible(false);
			lblDragonFight.setVisible(false);
			panel_end.setVisible(true);
			panel.setVisible(false);

			if (runOnce == 0) {
				loseScreen(); //run the game over screen GUI method
				runOnce++;
			}

		}
		checkForLevelChange();//after everything else has been updated, check if the conditions for the level to be changed have been met, and if they have, the level is changed so that on the next frame of the program the game will know it is now the next level
	}

	public void paintComponent(Graphics g) { //paint component method that draws all entities on screen 
		if (gameState == runState) { //if the game state is in run state, draw all entities
			super.paintComponent(g); //this has to be typed whenever you use this component, super is needed because this game panel is a subclass of JPanel
			Graphics2D g2 = (Graphics2D)g; //cast g to 2D graphics, this is because graphics 2D has more functions than graphics
			manageTiles.draw(g2); //this must be drawn before the player because it is putting the map layer below the player layer (the player will be on top of the map as opposed to under it )
			/*g2.draw(topLeft);
			g2.draw(bottomLeft);
			g2.draw(topRight);
			g2.draw(bottomRight);*/
			drawAllEnemies(g2); //draw all the enemies on screen
			player.draw(g2); // draw the player on screen
			fB.draw(g2);//draw the fireball on screen
			
			if (testingMode == true){// if the user is in testing mode, search through all of the tile rectangle arrays, and draw all of them
				for (int i = 0; i < manageTiles.platformBlocks.size(); i++) {
					g2.draw(manageTiles.platformBlocks.get(i));
				}

				g2.setColor(Color.magenta);
				for (int i = 0; i < manageTiles.platformBlockRadius.size(); i++) {
					g2.draw(manageTiles.platformBlockRadius.get(i));
				}

				for (int i = 0; i < manageTiles.groundBlocks.size(); i++) {
					g2.draw(manageTiles.groundBlocks.get(i));
				}
				g2.setColor(Color.green);
				for (int i = 0; i < manageTiles.waterBlocks.size(); i++) {
					g2.draw(manageTiles.waterBlocks.get(i));
				}
			}

			g2.dispose(); //releases any system resources being used, saves memory
			manageTiles.fillPlatformBlocksArray = false; //disable the creation of more tile arrays, this is because otherwise the arrays would be constantly expanding for as long as the program is running, which will harm performance, and eventually crash
			manageTiles.fillGroundBlocksArray = false;
			manageTiles.fillWaterBlocksArray = false;
		}
		if (gameState == menuState) { //draw nothing in the menu state because only JComponents will be displayed

		}
	}

	public void saveHighScore() { //this method is a print writer that saves the users score if they did better than the previous high score to a text file named HighScore.txt
			try {
				PrintWriter saveHighScore = new PrintWriter(new FileWriter("HighScore.txt")); //create the file
				if (previousHighScore < player.points) {//check if the users score should be saved or not
					saveHighScore.println(player.points); //write the users score on the file
				} else {
					saveHighScore.println(previousHighScore);
				}
				saveHighScore.close(); //close the print writer
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	public void startMusic() { //this method starts the game music, and loops it until the stop method is called
		sound.setMusicClip();
		sound.playMusic();
		sound.loopMusic();
	}

	public void stopMusic() { //this method stops the music from playing
		startMusicOnce = true;
		sound.stopMusic();
	}

	public void startSoundEffect (int i) { //this method plays sound effects, all sound effects are stored in an array, so the number used when this method is called determines what sound effect is played
		sound.setSound(i);
		sound.play();
	}
}
