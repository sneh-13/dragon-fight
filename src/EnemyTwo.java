import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EnemyTwo extends EnemyOne {
	Screen gameScreen;
	Player player;
	TeleportCollisions tC;
	int respawnCounter = 0;
	int activateParachute = 0;
	int lengthOfSpriteCounter = 10;

	public EnemyTwo () {
		gameScreen = null;
		player = null;
	}

	public EnemyTwo (Screen gameScreen, Player player) {
		this.gameScreen = gameScreen;
		this.player = player;
		setEnemyTwoDefaults();
	}

	public void setEnemyTwoDefaults() {
		characterDirection = "Right";
		hitpoints = 1;
		speed = 2;
		timeToSwitchToNewScreen = gameScreen.FPS*7; //7 seconds * 60 frames = 420
		getEnemyTwoSprites();
		collisionBox = new Rectangle(x,y,gameScreen.tileSize,gameScreen.tileSize);
		leftCollisionBox = new Rectangle(x,y,gameScreen.tileSize/2,gameScreen.tileSize); //left half of enemy
		rightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y,gameScreen.tileSize/2,gameScreen.tileSize);//right half of enemy
		balloonLeftCollisionBox = new Rectangle(x,y-gameScreen.tileSize,gameScreen.tileSize/2,gameScreen.tileSize);
		balloonRightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y-gameScreen.tileSize,gameScreen.tileSize/2,gameScreen.tileSize);
		radius = new Rectangle(x -gameScreen.tileSize - (gameScreen.tileSize/2),y-(gameScreen.tileSize*2)-(gameScreen.tileSize/2),gameScreen.tileSize*4,gameScreen.tileSize*5);
	}

	public void getEnemyTwoSprites() {
		try {
			balloon = ImageIO.read(getClass().getResourceAsStream("/enemyTwo/balloon_orange.png"));
			parachute = ImageIO.read(getClass().getResourceAsStream("/enemyOne/parachute.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/enemyTwo/e2_right_1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/enemyTwo/e2_right_2.png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/enemyTwo/e2_left_1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/enemyTwo/e2_left_2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean generateNewMovementDirection() { //overrides the method with the same name from EnemyOne, this is so the movement direction is generated quicker than for enemy one, so that enemy two will not go in one direction for too long
		boolean canRun;
		canRun = (updateMethodCounter == 30) ? (true) : (false); //returns canRun as true every 30 frames
		return (canRun);	
	}

	@Override
	public void enemyTouchesWater() {
		for (int i = 0; i < gameScreen.manageTiles.waterBlocks.size(); i++) {
			if (collisionBox.intersects(gameScreen.manageTiles.waterBlocks.get(i))) {
				gameScreen.startSoundEffect(9);
				gameScreen.e2.remove(this);
				player.points = player.points + 200;
			}
		}
	}

	public void update() {
		if (y > gameScreen.tileSize*gameScreen.maxRowSize) {
			player.points = player.points + 300;
			gameScreen.startSoundEffect(3);
			gameScreen.e2.remove(this);
			player.points = player.points + 200; //THE PLATFORM DETECTION IS AN ISSUE, THE BOTTOM RIGHT CORNER IS ALSO NOT GETTING A RECTANGLE
		}
		enemyTouchesWater();
		goalX = (int)player.collisionBox.getCenterX(); //set target x to the player's x
		goalY = (int)player.collisionBox.getCenterY(); //set target y to the player's y
		//System.out.println(balloonPoppedChecker(player));
		if (balloonPoppedChecker(player) == false && hitpoints > 0) {
			activateParachute = 0;
			hitpoints = 1;
			playerEnteredRadius(player);
			if (foundPlayer == true) { 
				agroOn = true;
			} else if (foundPlayer == false){
				speed = 2;
				checkEnemyScreenPosition(gameScreen);
				/*System.out.println("Top Right: "+enemyInTopRight);
			System.out.println("Bottom Right: "+enemyInBottomRight);
			System.out.println("Top Left: "+enemyInTopLeft);
			System.out.println("Bottom Left: "+enemyInBottomLeft); */
				switchToNewScreenCorner();
				if (enemyInTopRight < timeToSwitchToNewScreen && enemyInBottomRight < timeToSwitchToNewScreen && enemyInTopLeft < timeToSwitchToNewScreen && enemyInBottomLeft < timeToSwitchToNewScreen) {
					enemyRandomMovement();
				}
			}

			if (agroOn == true) {
				agroRadius = new Rectangle(x-gameScreen.tileSize*2-gameScreen.tileSize/4,y-gameScreen.tileSize*3-gameScreen.tileSize/4,gameScreen.tileSize*6-(gameScreen.tileSize/2),gameScreen.tileSize*7-(gameScreen.tileSize/2));
				playerInAgroRadius(player);
				if (huntPlayer == true) {
					if (closeToPlatform(gameScreen) == true) { //if the enemy is close to a platform, run the ai that deals with platforms
						chooseCorrectPath(player);
					} else { //if the enemy is not close to a platform, run the regular player hunting ai
						huntPlayer(player);
					}				
				}
			} 
			if (agroOn == false) {
				agroRadius = null;
			}
			tC = new TeleportCollisions(gameScreen,this);
			hitCharacter(player);
		} else {
			parachuteCollisionBox = new Rectangle(x,y-gameScreen.tileSize,gameScreen.tileSize,gameScreen.tileSize);
			if (deadEnemyTouchingGround(gameScreen) == false) {
				y = y + speed;
				speed = 1;
				balloonPopped = false;
				activateParachute++;
				if (player.collisionBox.intersects(parachuteCollisionBox) && activateParachute >= 10) {//ten frame cooldown to avoid enemy instantly despawning
					player.points = player.points + 300;
					gameScreen.startSoundEffect(3);
					gameScreen.e2.remove(this);
				}
			} else {
				parachuteCollisionBox = new Rectangle(gameScreen.tileSize*20,0,1,1);
				dontDrawBalloon = true;
				respawnCounter++;
				if (respawnCounter == gameScreen.FPS * 10) { //10 seconds
					gameScreen.startSoundEffect(11);
					hitpoints = 1;
					respawnCounter = 0;
					lengthOfSpriteCounter = 10;
					playBubbleSound = true;
					dontDrawBalloon = false;
					balloonPopped = false;
					enemyIsTouchingGround = false;
				}
				if (player.collisionBox.intersects(collisionBox) || y > gameScreen.tileSize*gameScreen.maxRowSize) {
					player.points = player.points + 200;
					gameScreen.startSoundEffect(3);
					gameScreen.e2.remove(this);
				}
			}
		}
		collisionBox = new Rectangle(x,y,gameScreen.tileSize,gameScreen.tileSize); //collision box for enemy that constantly updates x and y positions based on enemy movement
		leftCollisionBox = new Rectangle(x,y-gameScreen.tileSize/4,gameScreen.tileSize/2,gameScreen.tileSize + gameScreen.tileSize/4); //left half of enemy
		rightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y-gameScreen.tileSize/4,gameScreen.tileSize/2,gameScreen.tileSize + gameScreen.tileSize/4);//right half of enemy
		balloonLeftCollisionBox = new Rectangle(x,y-gameScreen.tileSize+10,gameScreen.tileSize/2,gameScreen.tileSize/2);
		balloonRightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y-gameScreen.tileSize+10,gameScreen.tileSize/2,gameScreen.tileSize/2);
		radius = new Rectangle(x -gameScreen.tileSize - (gameScreen.tileSize/2),y-(gameScreen.tileSize*2)-(gameScreen.tileSize/2),gameScreen.tileSize*4,gameScreen.tileSize*5);

		spriteCounter++;
		if (hitpoints == 0) {
			lengthOfSpriteCounter = 30;
		}

		if (enemyIsTouchingGround == false) {
			if (spriteCounter > lengthOfSpriteCounter) {
				if (spriteNumber == 1) {
					spriteNumber = 2;
				} else if (spriteNumber == 2) {
					spriteNumber = 1;
				}
				spriteCounter = 0;
			}
		}
	}

	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		BufferedImage sprite = null;
		if (hitpoints > 0) {
			image = balloon;
		} else if (dontDrawBalloon == true) {
			image = null;
		} else {
			if (playBubbleSound == true) {
				playBubbleSound = false;
				gameScreen.startSoundEffect(0);
			} 
			image = parachute;
		}

		if (characterDirection.equals("Right")) {
			if (spriteNumber == 1) {
				sprite = right1;
			} else if (spriteNumber == 2) {
				sprite = right2;
			}
		} else if (characterDirection.equals("Left")) {
			if (spriteNumber == 1) {
				sprite = left1;
			} else if (spriteNumber == 2) {
				sprite = left2;
			}
		}

		g2.drawImage(sprite,x,y,gameScreen.tileSize,gameScreen.tileSize,null); //enemy sprites
		g2.drawImage(image,x,y-gameScreen.tileSize+5,gameScreen.tileSize,gameScreen.tileSize,null); //balloon or parachute
		//g2.setColor(new Color(208,129,52));
		//g2.fillRect(x, y, gameScreen.tileSize, gameScreen.tileSize);
		if (gameScreen.testingMode == true) {
			g2.setColor(Color.black);
			g2.draw(leftCollisionBox);
			g2.setColor(Color.white);
			g2.draw(rightCollisionBox);
			g2.setColor(Color.red);
			//g2.draw(radius);
			g2.drawRect(x -gameScreen.tileSize - (gameScreen.tileSize/2),y-(gameScreen.tileSize*2)-(gameScreen.tileSize/2),gameScreen.tileSize*4,gameScreen.tileSize*5);
			//g2.drawRect(x-gameScreen.tileSize*2-gameScreen.tileSize/4,y-gameScreen.tileSize*3-gameScreen.tileSize/4,gameScreen.tileSize*6-(gameScreen.tileSize/2),gameScreen.tileSize*7-(gameScreen.tileSize/2));
			if (agroOn == true) {
				g2.draw(agroRadius);
			}
			g2.setColor(Color.yellow);
			g2.draw(parachuteCollisionBox);
		}
	}
}
