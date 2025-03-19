import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player extends Character {

	BufferedImage fullHeart, halfHeart, numOne, numTwo, numThree;

	Screen gameScreen;
	KeyboardListener kL;
	TeleportCollisions TC;
	int frameCheck;
	int flightSpriteCounter = 0;
	int flightSpriteNumber = 1;
	boolean runSpaceCode = true;
	int gravity = y + (speed/2);
	TileCollisionChecker CheckCol = new TileCollisionChecker();
	boolean invincibility = false;
	int invincibilityCounter = 0;
	int points = 0;
	String displayPoints;
	ManageTiles mT = new ManageTiles(gameScreen);
	int resetMapTimer = 0;
	boolean changeToLevelOneState = true;

	//movement permission
	boolean moveLeft = true;
	boolean moveRight = true;
	boolean moveUp = true;

	public Player() {
		gameScreen = null;
		kL = null;
	}

	public Player(Screen gameScreen, KeyboardListener kL, ManageTiles mT) {
		this.gameScreen = gameScreen;
		this.kL = kL;
		this.mT = mT;
		setPlayerDefaults();
	}

	public void setPlayerDefaults() {
		getPlayerSprites();
		//x = 5;
		//y = 400;
		y = gameScreen.tileSize*10;
		x = 0;
		speed = 4;
		collisionBox = new Rectangle(x,y,gameScreen.tileSize,gameScreen.tileSize);
		characterDirection = "falling";
	}

	/*method to create a string containing current points, this has to be in a method so it can be called in the update method, 
	because player points will change throughout the game*/
	public void displayPlayerPoints() { 
		String pointsString = Integer.toString(points);
		displayPoints = "Points: "+ pointsString;
	}

	public void getPlayerSprites () {
		try {
			up1 = ImageIO.read(getClass().getResourceAsStream("/player/dragon_flight_right_1.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/player/dragon_flight_right_2.png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/player/dragon_left_1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/player/dragon_left_2.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/player/dragon_right_1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/player/dragon_right_2.png"));
			fullHeart = ImageIO.read(getClass().getResourceAsStream("/screenObjects/heart_full.png"));
			halfHeart = ImageIO.read(getClass().getResourceAsStream("/screenObjects/heart_half.png"));
			numOne = ImageIO.read(getClass().getResourceAsStream("/screenObjects/number_one.png"));
			numTwo = ImageIO.read(getClass().getResourceAsStream("/screenObjects/number_two.png"));
			numThree = ImageIO.read(getClass().getResourceAsStream("/screenObjects/number_three.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playerIsHit() {
		if (invincibility == false && lives == 1 && hitpoints == 1) {
			lives = 0;
		} else if (invincibility == false) {
			hitpoints--;
			invincibility = true;
		} 
		//System.out.println("Hitpoints: "+hitpoints);
		//System.out.println("Lives: "+lives);
	}

	public void checkIfPlayerIsDead() {
		if (lives == 0) {
			gameScreen.gameState = gameScreen.end_State;
			gameScreen.newGame = true;
			invincibility = false;
			lives = 3;
			hitpoints = 2;
		}
	}

	public void resetGameMap () {
		if ((hitpoints == 0 || playerTouchesWater() == true) && lives > 0) {
			if (changeToLevelOneState == true) {
				gameScreen.gameState = gameScreen.levelScreenState;
				changeToLevelOneState = false;
			}
			resetMapTimer++;
			if (resetMapTimer == 1) {
				lives--;
				hitpoints = 2;
				points = gameScreen.previousLevelScore;
				gameScreen.canResetMap = true;
				resetMapTimer = 0;
				changeToLevelOneState = true;
				if (lives == 0) {
					gameScreen.gameState = gameScreen.end_State;
					gameScreen.newGame = true;
					invincibility = false;
					lives = 3;
					hitpoints = 2;
				}
			}
		}
	}

	public boolean playerTouchesWater() {
		boolean touchingWater = false;
		if (gameScreen.manageTiles.waterBlocks.size() > 0) {
			for (int i = 0; i < gameScreen.manageTiles.waterBlocks.size(); i++) {
				if (collisionBox.intersects(gameScreen.manageTiles.waterBlocks.get(i))) {
					gameScreen.startSoundEffect(9);
					touchingWater = true;
					break;
				}
			}
		}
		return(touchingWater);
	}

	public void playerInvincibility() {
		if (invincibility == true) {
			invincibilityCounter++;
			//System.out.println("I Am invincible for "+invincibilityCounter);

			if (invincibilityCounter == 180) { //runs for 3 seconds (60 frames*3 seconds = 180)
				invincibilityCounter = 0;
				invincibility = false;
			}
		}
	}

	public void update() { //this method is called 60 times per second
		checkIfPlayerIsDead();
		resetGameMap();
		CheckCol.Tile_Chara(this); // check for any collisions
		y = gravity; //player's gravitational pull depends on the Tile_Chara downward collision

		if (kL.upPressed == true || kL.leftPressed == true || kL.rightPressed == true) { //this if statement is in place so that if no keys are pressed the sprite counter will not increment, and the sprite will not change unless a button is pressed
			/*if (kL.upPressed == true) { //if up is pressed
				characterDirection = "up"; //set the direction of the character
				if(moveUp==true) {
					y = y - speed; 
				} else {
					moveUp = false;
				}
			}*/
			if (kL.leftPressed == true) {
				characterDirection = "left";
				if (moveLeft == true) {
					x = x -speed; //player speed is amount of pixels, so 4 means 4 pixels
				} else {
					moveLeft = false;
				}
			}
			else if (kL.rightPressed == true) {
				characterDirection = "right";
				if (moveRight == true) {
					x = x + speed;
				} else {
					moveRight = false;
				}
			} 

			spriteCounter++; //this sprite counter increments every frame, so the next if statement will run every 10 frames changing the sprite to the other sprite
			if (spriteCounter > 10) { //this means when the counter hits 10 it changes the sprite, meaning the sprite changes every 10 frames since this method runs 60 times (60 frames)
				if (spriteNumber == 1) { //if sprite one is the current sprite change it to sprite 2
					spriteNumber = 2;
				}
				else if(spriteNumber == 2) { //if sprite two is the current sprite change it to sprite 1
					spriteNumber = 1;
				}
				spriteCounter = 0; //reset sprite counter so that this if statement will run again in 10 frames
			}
		}

		if (kL.spacePressed == true) {
			gameScreen.startSoundEffect(10);
			CheckCol.Tile_Chara(this); // check for any collisions
			characterDirection = "flying";
			if (moveUp == true) {
				y = y - gameScreen.tileSize;
			} else {
				moveUp = false;
			}
			kL.spacePressed = false;
			if (runSpaceCode == true) {
				runSpaceCode = false;
			}
			frameCheck++; //has to be seperate counter from sprite counter because each loop of ten sprite counter is reset to 0, but the jump should not be that quick
			flightSpriteCounter++;
			if (flightSpriteCounter > 1) {
				if (flightSpriteNumber == 1) {
					flightSpriteNumber = 2;
				}
				else if (flightSpriteNumber == 2) {
					flightSpriteNumber = 1;
				}
			}
			if (frameCheck == 60) {
				frameCheck = 0;
				runSpaceCode = true;
			}
		}
		playerInvincibility();
		collisionBox = new Rectangle(x,y,gameScreen.tileSize,gameScreen.tileSize);
		TC = new TeleportCollisions(gameScreen,this);
		displayPlayerPoints();
	}

	public void draw(Graphics2D g2) {
		BufferedImage invincibilityTimer = null;
		if (invincibilityCounter <= 60 && invincibilityCounter > 0) {
			invincibilityTimer = numThree;
		} else if (invincibilityCounter <= 120 && invincibilityCounter > 0) {
			invincibilityTimer = numTwo;
		} else if (invincibilityCounter <= 180 && invincibilityCounter > 0) {
			invincibilityTimer = numOne;
		}
		g2.drawImage(invincibilityTimer,x+((gameScreen.tileSize/2)-(gameScreen.tileSize/4)),y-gameScreen.tileSize/2,gameScreen.tileSize/2,gameScreen.tileSize/2,null);

		if(lives == 3) {
			g2.drawImage(fullHeart,0,0,gameScreen.tileSize,gameScreen.tileSize,null);
			g2.drawImage(fullHeart,gameScreen.tileSize/2+(gameScreen.tileSize/4),0,gameScreen.tileSize,gameScreen.tileSize,null);
			if (hitpoints == 2) {
				g2.drawImage(fullHeart,gameScreen.tileSize + (gameScreen.tileSize/2),0,gameScreen.tileSize,gameScreen.tileSize,null);
			}else if (hitpoints == 1) {
				g2.drawImage(halfHeart,gameScreen.tileSize + (gameScreen.tileSize/2),0,gameScreen.tileSize,gameScreen.tileSize,null);
			}
		}else if (lives == 2) {
			g2.drawImage(fullHeart,0,0,gameScreen.tileSize,gameScreen.tileSize,null);
			if (hitpoints == 2) {
				g2.drawImage(fullHeart,gameScreen.tileSize/2+(gameScreen.tileSize/4),0,gameScreen.tileSize,gameScreen.tileSize,null);
			}else if (hitpoints == 1) {
				g2.drawImage(halfHeart,gameScreen.tileSize/2 + (gameScreen.tileSize/4),0,gameScreen.tileSize,gameScreen.tileSize,null);
			}

		} else if (lives == 1) {
			if (hitpoints == 2) {
				g2.drawImage(fullHeart,0,0,gameScreen.tileSize,gameScreen.tileSize,null);
			} else if (hitpoints ==1) {
				g2.drawImage(halfHeart,0,0,gameScreen.tileSize,gameScreen.tileSize,null);
			}
		}
		BufferedImage image = null;
		//g2.setColor(Color.white);
		//g2.fillRect(x,y,gamePanel.tileSize,gamePanel.tileSize); //draw a rectangle on the screen, (x coordinate, y coordinate, width, height)
		switch (characterDirection) { //switch statement to check the direction that is clicked, and choose the image based on the direction
		case "up":
			if (spriteNumber == 1) {
				image = up1;
			}
			if (spriteNumber == 2) {
				image = up2;
			}
			break;
		case "falling":
			image = up2;
			break;
		case "flying":
			if (flightSpriteNumber == 1) {
				image = up1;
			} 
			if (flightSpriteNumber == 2) {
				image = up2;
			}
			break;
		case "left":
			if (spriteNumber == 1) {
				image = left1;
			}
			if (spriteNumber == 2) {
				image = left2;
			}
			break;
		case "right":
			if (spriteNumber == 1) {
				image = right1;
			}
			if (spriteNumber == 2) {
				image = right2;
			}
			break;
		}		
		g2.setColor(Color.black);
		g2.setFont(new Font("Serif",Font.BOLD,gameScreen.tileSize/2));
		if (displayPoints != null) {
			g2.drawString(displayPoints, gameScreen.tileSize*3,gameScreen.tileSize - gameScreen.tileSize/3);
		}
		if (gameScreen.previousHighScore > points) {
			g2.drawString("High Score: "+Integer.toString(gameScreen.previousHighScore),gameScreen.tileSize*6,gameScreen.tileSize - gameScreen.tileSize/3);
		} else {
			g2.drawString("High Score: "+Integer.toString(points),gameScreen.tileSize*6,gameScreen.tileSize - gameScreen.tileSize/3);
		}
		g2.drawImage(image,x,y,gameScreen.tileSize,gameScreen.tileSize,null);
		g2.setColor(Color.red);
		if (gameScreen.testingMode == true) {
			g2.drawRect(x,y,gameScreen.tileSize,gameScreen.tileSize);
		}
	}
}