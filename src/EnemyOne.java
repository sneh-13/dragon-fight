/*************************************************
 * Group D
 * ICS4U
 * January 20, 2023
 * Dragon Fight
 * This class contains all data and methods for EnemyOne, it is a child of EnemyThree to avoid retyping the majority
 * of the ai. This enemy introduces some new ai that EnemyThree does not have, most of it being in the method switchToNewScreenCorner() (line 298)
 * and in checkEnemyScreenPoition() (line 363), this ai will move the enemy to a new part of the map if it has been in one spot for too long, to avoid
 * the enemies wandering around and never following the player. This enemy also has ai to follow the player and avoid platforms when the player is in its
 * sensor radius, and also has ai to follow another EnemyOne that senses the player, when the current enemy does not. 
 ************************************************/

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EnemyOne extends EnemyThree { 

	Screen gameScreen;
	TeleportCollisions TC;
	Player player;
	int gravity = 1;
	boolean moveUp, moveRight, moveLeft, moveDown = false;
	int movement;
	BufferedImage balloon;
	BufferedImage parachute;
	boolean foundPlayer;
	boolean agroOn = false;
	boolean huntPlayer = false;
	int chooseMovementDirection;
	int enemyInTopLeft, enemyInTopRight, enemyInBottomLeft, enemyInBottomRight = 0;
	boolean newRandomTopRight = true;
	boolean newRandomBottomRight = true;
	boolean newRandomTopLeft = true;
	boolean newRandomBottomLeft = true;
	TileCollisionChecker CheckCol = new TileCollisionChecker();
	ManageTiles mT = new ManageTiles(gameScreen);
	int timeToSwitchToNewScreen;
	boolean balloonPopped = false;
	int activateParachute = 0;
	boolean playBubbleSound = true;
	int lengthOfSpriteCounter = 10;

	public EnemyOne() { //Blank overload constructor, without the blank constructor there are inheritance issues, however this constructor is never actually called
		gameScreen = null;
		player = null;
	}

	public EnemyOne (Screen gameScreen, Player player) { //Main constructor, requires the screen and the player, so that data can be taken from those classes
		this.player = player;
		this.gameScreen = gameScreen;
		setEnemyOneDefaults(); //set the defaults for the enemy
	}

	public void setEnemyOneDefaults () { //This method sets all of the defaults for the enemy
		//x = gameScreen.tileSize*10;
		//y = gameScreen.tileSize*8;
		characterDirection = "Right"; //default the enemy position to right so that the sprite will show right on spawn
		speed = 1; //default speed to 1
		hitpoints = 1; //enemy one dies in one hitpoint so set hitpoints to 1, this has to be done because the parent class has hitpoints set to 2
		timeToSwitchToNewScreen = gameScreen.FPS*10; // 60 frames * 10 seconds, therefore the enemy will switch to a new screen if they have been in the same area for 10 seconds (600 frames)
		getEnemyOneSprites(); //load the sprites for the enemy
		//Default all of the rectangles to their starting positions
		leftCollisionBox = new Rectangle(x,y-gameScreen.tileSize/2,gameScreen.tileSize/2,gameScreen.tileSize + gameScreen.tileSize/2); //left half of enemy
		rightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y-gameScreen.tileSize/2,gameScreen.tileSize/2,gameScreen.tileSize + gameScreen.tileSize/2);//right half of enemy
		balloonLeftCollisionBox = new Rectangle(x,y-gameScreen.tileSize,gameScreen.tileSize/2,gameScreen.tileSize/2);
		balloonRightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y-gameScreen.tileSize,gameScreen.tileSize/2,gameScreen.tileSize/2);
		radius = new Rectangle(x-gameScreen.tileSize,y-(gameScreen.tileSize*2),gameScreen.tileSize*3,gameScreen.tileSize*4);
		collisionBox = new Rectangle(x,y,gameScreen.tileSize,gameScreen.tileSize);
	}

	public void getEnemyOneSprites() {//This method loads the sprites for enemy one and sets them each to their respective BufferedImage variables
		try {
			balloon = ImageIO.read(getClass().getResourceAsStream("/enemyOne/balloon_green.png"));
			parachute = ImageIO.read(getClass().getResourceAsStream("/enemyOne/parachute.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/enemyOne/e1_right_1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/enemyOne/e1_right_2.png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/enemyOne/e1_left_1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/enemyOne/e1_left_2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void enemyTouchesWater() { //This method checks if the enemy has touched the water, this is accomplished with a for loop that runs through all of the different spots on the map where there are water, and then checks if the enemy intersects the rectangle at that spot
		for (int i = 0; i < gameScreen.manageTiles.waterBlocks.size(); i++) { 
			if (collisionBox.intersects(gameScreen.manageTiles.waterBlocks.get(i))) { //if the enemy intersects the water at the current index
				gameScreen.startSoundEffect(9); //play the water sound effect
				gameScreen.e1.remove(this); //remove this iteration of the EnemyOne from the array because it has died 
				player.points = player.points + 100; //give the player points
			}
		}
	}

	public void update() { //This update method controls all of the ai, and movement, and is what changes the values from frame to frame
		enemyTouchesWater(); //call the check water method to see if the enemy has hit the water
		goalX = (int)player.collisionBox.getCenterX(); //set target x to the player's x, these values are what the enemy will be following when its ai is active
		goalY = (int)player.collisionBox.getCenterY(); //set target y to the player's y
		if (balloonPoppedChecker(player) == false && hitpoints > 0) { //This runs if the enemies hitpoints are greater than 0 meaning it is alive, and also if its balloon has not been popped 
			playerEnteredRadius(player);//check to see if the player entered the radius of the enemy to see if the ai should be activated
			if (foundPlayer == true || checkForMobOpportunity() == true) { //runs if the player is inside of the enemies activation radius, or if another enemy that senses the player is in the activation radius
				agroOn = true; //setting this variable to true will activate the ai below
				speed = 2; //increase the speed so the enemies are more dangerous
			} else if (foundPlayer == false){ //if the enemy does not sense the player
				speed = 1; //make sure speed is reset to default speed inrease it had been increased
				checkEnemyScreenPosition(gameScreen); //check if the enemy has been in the same area for too long
				/*System.out.println("Top Right: "+enemyInTopRight);
			System.out.println("Bottom Right: "+enemyInBottomRight);
			System.out.println("Top Left: "+enemyInTopLeft);
			System.out.println("Bottom Left: "+enemyInBottomLeft);*/
				switchToNewScreenCorner(); //if the enemy has been in the same area for too long, this method will make them move to a new area
				//If all of the screen counters are less than the 10 seconds set at the top of the class, this means the enemy has not been in the same area for too long yet, so random movement is allowed
				if (enemyInTopRight < timeToSwitchToNewScreen && enemyInBottomRight < timeToSwitchToNewScreen && enemyInTopLeft < timeToSwitchToNewScreen && enemyInBottomLeft < timeToSwitchToNewScreen) {
					enemyRandomMovement(); //run the random movement, this will let the enemy move in a random direction, and will continue to change directions
				}
			}
			if (agroOn == true) { //if the enemy senses the player, its ai radius is created, and it is larger than the original activation radius
				agroRadius = new Rectangle(x-gameScreen.tileSize*2,y-(gameScreen.tileSize*3),gameScreen.tileSize*5,gameScreen.tileSize*6); //set the new expanded radius
				playerInAgroRadius(player);//checks if the player intersects the expanded radius
				if (huntPlayer == true) { //if the player does intersect the expanded radius, check if the enemy is close to a platform
					if (closeToPlatform(gameScreen) == true) { //if the enemy is close to a platform, run the ai that deals with platforms, this ai and the hunt player ai below are inherited from EnemyThree
						chooseCorrectPath(player);
					} else { //if the enemy is not close to a platform, run the regular player hunting ai
						huntPlayer(player);
					}
				}
			} 
			if (agroOn == false) { //if the player leaves the activation radius, make it null because it should no longer exist again until the player enters it again
				agroRadius = null;
			}
			TC = new TeleportCollisions(gameScreen, this); //Instantiate the teleport collisions class, this class checks if the enemy has left the screen, and if it has, it will teleport it out the other side
			hitCharacter(player); //check if the enemy has hit the player
		} else { //this else runs if balloon popped checker is true or hitpoints is 0, meaning that the enemy has either been killed by a fireball, or their balloon has been popped and they should be falling
			balloonPopped = false;
			speed = 1; //reset speed to 1 so they fall slowly
			y = y + speed; //force the enemies to fall down
			activateParachute++;

			if (y == gameScreen.tileSize*13) { //if the enemy falls out of the screen, remove it from the game
				player.points = player.points + 100; //add points to the player
				gameScreen.startSoundEffect(3); //play the death sound effect
				gameScreen.e1.remove(this); //remove the enemy from the array
			} else if (player.collisionBox.intersects(parachuteCollisionBox) && activateParachute >= 10) { //ten frame cooldown to avoid enemy instantly despawning
				player.points = player.points + 200; //add extra points since the parachute being popped should reward more points
				gameScreen.startSoundEffect(3); //play the death sound effect
				gameScreen.e1.remove(this); //remove the enemy from the array
			}
		}
		
		//Re-instantiate every single rectangle, they are created with the current x and y values that have just been changed by the code above, this allows the rectangles to move along with the enemy
		radius = new Rectangle(x-gameScreen.tileSize,y-(gameScreen.tileSize*2),gameScreen.tileSize*3,gameScreen.tileSize*4);		
		collisionBox = new Rectangle(x,y,gameScreen.tileSize,gameScreen.tileSize); //collision box for enemy that constantly updates x and y positions based on enemy movement
		leftCollisionBox = new Rectangle(x,y-gameScreen.tileSize/4,gameScreen.tileSize/2,gameScreen.tileSize + gameScreen.tileSize/4); //left half of enemy
		rightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y-gameScreen.tileSize/4,gameScreen.tileSize/2,gameScreen.tileSize + gameScreen.tileSize/4);//right half of enemy
		balloonLeftCollisionBox = new Rectangle(x,y-gameScreen.tileSize+10,gameScreen.tileSize/2,gameScreen.tileSize/2);
		balloonRightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y-gameScreen.tileSize+10,gameScreen.tileSize/2,gameScreen.tileSize/2);
		parachuteCollisionBox = new Rectangle(x,y-gameScreen.tileSize,gameScreen.tileSize,gameScreen.tileSize);

		spriteCounter++;//increment the sprite counter
		if (hitpoints == 0) { //if the enemy is dead make the sprite counter a larger number, this means the sprites will not change as quickly, since the enemy is dead its animation will move slower
			lengthOfSpriteCounter = 30;
		}
		if (spriteCounter > lengthOfSpriteCounter) { //if the sprite counter is larger than the sprite variable, then change the sprite number
			if (spriteNumber == 1) {
				spriteNumber = 2;
			} else if (spriteNumber == 2) {
				spriteNumber = 1;
			}
			spriteCounter = 0; //reset the counter to 0 so that the sprite will change again the next time the condition is met
		}
	}

	public boolean checkForMobOpportunity () { //This method searches for if another enemy senses the player, if it does, then this method returns true, and this enemy will begin hunting the player as well, thus creating the potential for a mob
		boolean foundAnotherAgroEnemy = false;
		for (int i = 0; i < gameScreen.e1.size(); i++) {//search through all the EnemyOne entities that exist
			if (radius.intersects(gameScreen.e1.get(i).radius) && gameScreen.e1.get(i) != this) { //runs if the current index enemy intersects this enemies radius, and the current index is also not this enemies index, if I did not exclude this enemy, the code would think the enemy is intersecting itself, and create strange issues
				if (gameScreen.e1.get(i).agroOn == true) { //runs if the enemy that is intersecting the current method, has its ai activated on the player
					foundAnotherAgroEnemy = true;//set the variable to true
					break;//break out of the for loop, because if the correct enemy has been found, no more should be searched through
				}
			}
		}
		return(foundAnotherAgroEnemy); //return the result of the for loop above, if it never finds an enemy, this method just returns false
	}

	public boolean generateNewMovementDirection() {//this method returns true if the counter has reached 60 frames, and false if it has not
		boolean canRun;
		canRun = (updateMethodCounter == 60) ? (true) : (false);//returns canRun as true every 60 frames (every second), I used a ternary operator because I wanted to learn how they worked, but this works exactly the same as an if, else statement
		return (canRun);
	}
	public void enemyRandomMovement() { //this method controls the random movement of the enemies
		updateMethodCounter++;//increment the counter
		if (generateNewMovementDirection() == true) {//if the method above is true meaning the counter has reached 60, then reset all directions to false, and reset the counter to 0, also generate a new random number
			int randNum = (int) (Math.random()*100 + 1); //random number between 1 and 100
			movement = randNum;
			moveLeft = false;
			moveRight = false;
			moveUp = false;
			moveDown = false;
			updateMethodCounter = 0;
		}


		if (movement <= 25) { //if the random number is 25 or less, and no other direction is currently being moved, move right
			if (moveLeft == false && moveDown == false && moveUp == false) {
				moveRight = true;
			}
		}
		if (movement > 25 && movement <= 50) { //if the random number is between 25 and 50 (50 included) move left
			if (moveRight == false && moveUp == false && moveDown == false) {
				moveLeft = true;
			}
		}

		if (movement > 50 && movement <= 75) { //if the random number is between 50 and 75 (75 included) move up
			if (moveRight == false && moveLeft == false && moveDown == false) {
				moveUp = true;
			}
		}
		if (movement > 75 && movement <= 100) { //if the random number is between 75 and 100 (100 included) move down
			if (moveRight == false && moveLeft == false && moveUp == false) {
				moveDown = true;
			}
		}

		CheckCol.Tile_Chara(this); //check if the enemy is going to collide with a platform

		if (moveRight == true) { //move right
			characterDirection = "Right";
			x = x + speed;
		}
		if (moveLeft == true) { //move left
			characterDirection = "Left";
			x = x - speed;
		}
		if (moveUp == true) { //move up
			y = y - speed;
		}
		if (moveDown == true) { //move down
			y = y + speed;
		}
	}

	public void hitCharacter(Player player) { //this method runs if the enemy hits the character, and deals with the results
		this.player = player;
		if (player.invincibility == false) { //runs if the player is not currently invincible
			CheckCol.tilePlayer_Monster(player, this); //method checks if player or monster should be pushed back based on if there are blacklisted platforms or collisions behind it. (Prevents player/monster from clipping into Tiles) 
			if (this.player.collisionBox.intersects(leftCollisionBox)) { //if the player hits the enemy from the left, they are pushed to the left
				if (playerPush == false) {
					this.player.x = this.player.x + 0; // don't move player
					x = x + gameScreen.tileSize; // push monster back instead
				} else {
					this.player.x = this.player.x - gameScreen.tileSize;
				}
				this.player.playerIsHit();//run the player is hit method that deals with subtracting lives and hitpoints
				this.player.invincibility = true; //make the player invincible since they just got hit
			}
			else if (this.player.collisionBox.intersects(rightCollisionBox)) { //has to be else if because when it was if you could go through the middle of the enemy because the +48 and minus 48 were cancelling out to no movement
				if (playerPush == false) { 
					this.player.x = this.player.x + 0; // don't move player
					x = x - gameScreen.tileSize; // push monster back instead
				} else {
					this.player.x = this.player.x + gameScreen.tileSize; //if the player hits the right side of the enemy, push them to the right
				}
				this.player.playerIsHit();
				this.player.invincibility = true;
			}
		} 
	}

	public void playerInAgroRadius(Player player) { //this method turns on and off the ai based on if the player is in the radius or not
		this.player = player;
		if (this.player.collisionBox.intersects(agroRadius)) { //player inside of the radius, activate player hunt ai
			huntPlayer = true;
		}
		else if (!this.player.collisionBox.intersects(agroRadius)) { //player not inside of the radius, turn off the ai 
			huntPlayer = false;
			agroOn = false;
		}
	}


	public void playerEnteredRadius(Player player) {//This method turns on and off the expanded radius based on if the player is inside of it or not
		this.player = player;
		if(this.player.collisionBox.intersects(radius)) { //turn on the expanded radius if the player intersects the radius
			//System.out.println("You Have Entered My Box");
			foundPlayer = true;			
		} else if (!this.player.collisionBox.intersects(radius)) { //turn off the expanded radius if the player does not intersect the radius
			foundPlayer = false;
		}
	}

	public void switchToNewScreenCorner() { //this method is one part of the ai, and it checks if the enemy has been in one area of the screen for too long, and then forces it to move if it has been
		if (enemyInTopRight == timeToSwitchToNewScreen) { //runs if the enemy has been in the top right quadrant of the screen for 10 seconds (10*60 = 600)
			if (newRandomTopRight == true) {//generate a new random number
				chooseMovementDirection = (int) (Math.random()*50+1);
				newRandomTopRight = false; //set to false to make sure a new random is not generated
			}
			//An enemy in the top right of the screen can only go down or left
			if (chooseMovementDirection % 2 == 0) { //if the number is divisible by 2 move left
				if (monTouchingPlatform(gameScreen)== false) {
					characterDirection = "Left";
					x = x - speed;
				} else {y = y + speed;}
			} else if (chooseMovementDirection % 2 != 0) { //if the number is not divisible by 2 move down
				if (monTouchingPlatform(gameScreen)== false) {
					y = y + speed;
				} else {
					characterDirection = "Left";
					x = x - speed;
				}
			}
		} else if (enemyInBottomRight == timeToSwitchToNewScreen) {
			if (newRandomBottomRight == true) {
				chooseMovementDirection = (int) (Math.random()*50+1);
				newRandomBottomRight = false;
			}
			if (chooseMovementDirection % 2 == 0) {
				//System.out.println("Even Number: "+chooseMovementDirection);
				if (monTouchingPlatform(gameScreen)== false) {
					characterDirection = "Left";
					x = x - speed;
				}else {y=y-speed;}
			} else if (chooseMovementDirection % 2 != 0) {
				//System.out.println("Odd Number: "+chooseMovementDirection);
				if (monTouchingPlatform(gameScreen)== false) {
					y = y - speed;
				}else {x=x-speed;}
			}
		} else if (enemyInBottomLeft == timeToSwitchToNewScreen) {
			if (newRandomBottomLeft == true) {
				chooseMovementDirection = (int) (Math.random()*50+1);
				newRandomBottomLeft = false;
			}
			if (chooseMovementDirection % 2 == 0) {
				if (monTouchingPlatform(gameScreen)== false) {
					characterDirection = "Right";
					x = x + speed;
				} else {y=y-speed;}
			} else if (chooseMovementDirection % 2 != 0) {
				if (monTouchingPlatform(gameScreen)== false) {
					y = y - speed;
				} else {x=x+speed;}
			}
		} else if (enemyInTopLeft == timeToSwitchToNewScreen) {
			if (newRandomTopLeft == true) {
				chooseMovementDirection = (int) (Math.random()*50+1);
				newRandomTopLeft = false;
			}
			if (chooseMovementDirection % 2 == 0) {
				if (monTouchingPlatform(gameScreen)== false) {
					characterDirection = "Right";
					x = x + speed;
				}else {y=y+speed;}
			} else if (chooseMovementDirection % 2 != 0) {
				if (monTouchingPlatform(gameScreen)== false) {
					y = y + speed;
				}else {x=x+speed;}
			}
		}
	}

	public void checkEnemyScreenPosition(Screen gameScreen) {
		this.gameScreen = gameScreen;
		if (collisionBox.intersects(this.gameScreen.topRight) && !collisionBox.intersects(this.gameScreen.topLeft) && !collisionBox.intersects(this.gameScreen.bottomLeft) && !collisionBox.intersects(this.gameScreen.bottomRight)) {
			if (enemyInTopRight < timeToSwitchToNewScreen) {
				enemyInTopRight++;
			}
			enemyInBottomRight = 0;
			enemyInTopLeft = 0;
			enemyInBottomLeft = 0;
			newRandomBottomLeft = true;
			newRandomBottomRight = true;
			newRandomTopLeft = true;
		} else if (collisionBox.intersects(this.gameScreen.bottomRight) && !collisionBox.intersects(this.gameScreen.topLeft) && !collisionBox.intersects(this.gameScreen.bottomLeft) && !collisionBox.intersects(this.gameScreen.topRight)) {
			if (enemyInBottomRight < timeToSwitchToNewScreen) {
				enemyInBottomRight++;
			}
			enemyInBottomLeft = 0;
			enemyInTopRight = 0;
			enemyInTopLeft = 0;
			newRandomTopRight = true;
			newRandomBottomLeft = true;
			newRandomTopLeft = true;
		} else if (collisionBox.intersects(this.gameScreen.topLeft) && !collisionBox.intersects(this.gameScreen.topRight) && !collisionBox.intersects(this.gameScreen.bottomLeft) && !collisionBox.intersects(this.gameScreen.bottomRight)) {
			if (enemyInTopLeft < timeToSwitchToNewScreen) {
				enemyInTopLeft++;
			}
			enemyInTopRight = 0;
			enemyInBottomLeft = 0;
			enemyInBottomRight = 0;
			newRandomTopRight = true;
			newRandomBottomRight = true;
			newRandomBottomLeft = true;
		} else if (collisionBox.intersects(this.gameScreen.bottomLeft) && !collisionBox.intersects(this.gameScreen.topLeft) && !collisionBox.intersects(this.gameScreen.topRight) && !collisionBox.intersects(this.gameScreen.bottomRight)) {
			if (enemyInBottomLeft < timeToSwitchToNewScreen) {
				enemyInBottomLeft++;
			}
			enemyInBottomRight = 0;
			enemyInTopRight = 0;
			enemyInTopLeft = 0;
			newRandomTopRight = true;
			newRandomBottomRight = true;
			newRandomTopLeft = true;
		}
	}

	public boolean balloonPoppedChecker(Player player) {
		this.player = player;
		if (player.collisionBox.intersects(balloonLeftCollisionBox) && player.collisionBox.intersects(balloonRightCollisionBox)) {
			player.y = player.y-gameScreen.tileSize/2;
			balloonPopped = true;
		} else if (this.player.collisionBox.intersects(balloonLeftCollisionBox)) {
			if (playerPush == true) {
				this.player.x = this.player.x - gameScreen.tileSize/2; //push player back since monster will be obstructed back
				balloonPopped = true;
			} else {
				this.player.x = this.player.x - 0; // don't move player
				balloonPopped = true;
				x = x + gameScreen.tileSize/2; // push back monster instead since its getting hurt
			}
		} else if (this.player.collisionBox.intersects(balloonRightCollisionBox)) {
			if (playerPush == true) {
				this.player.x = this.player.x + gameScreen.tileSize/2; //push player back since monster will be obstructed back
				balloonPopped = true;
			} else {
				this.player.x = this.player.x + 0; // don't move player
				x = x - gameScreen.tileSize/2; // push back monster instead instead since its getting hurt
				balloonPopped = true;
			}
		}
		if (balloonPopped == true) {
			y = y + gameScreen.tileSize;
			hitpoints = 0;
			//System.out.println("BALLOON POPPED");
		}
		return (balloonPopped);
	}

	public void draw (Graphics2D g2) {
		BufferedImage sprite = null;
		BufferedImage fallingSprites = null;

		if (characterDirection.equals("Right")) {
			if (spriteNumber == 1) {
				sprite = right1;
			} else if (spriteNumber == 2) {
				sprite = right2;
			}
		} else {
			if (spriteNumber == 1) {
				sprite = left1;
			} else if (spriteNumber == 2) {
				sprite = left2;
			}
		}
		g2.drawImage(sprite,x,y,gameScreen.tileSize,gameScreen.tileSize,null); //draw enemy sprite

		if (hitpoints > 0) {
			fallingSprites = balloon;
		} else {
			if (playBubbleSound == true) {
				playBubbleSound = false;
				gameScreen.startSoundEffect(0);
			} 
			fallingSprites = parachute;
		}
		g2.setColor(new Color(73,174,17)); //same colour I used to make the balloon
		/*g2.setColor(Color.green);
		g2.fillRect(x,y,gameScreen.tileSize/2,gameScreen.tileSize);
		g2.setColor(Color.black);
		g2.fillRect(x+(gameScreen.tileSize/2),y,gameScreen.tileSize/2,gameScreen.tileSize);*/
		if (gameScreen.testingMode == true) {
			g2.setColor(Color.pink);
			g2.draw(balloonLeftCollisionBox);
			g2.setColor(Color.yellow);
			g2.draw(balloonRightCollisionBox);
			g2.draw(rightCollisionBox);
			g2.draw(leftCollisionBox);
			g2.setColor(Color.red);
			g2.setColor(Color.black);
			g2.draw(parachuteCollisionBox);
			g2.draw(radius);
			if (agroOn == true) {
				g2.drawRect(x-gameScreen.tileSize*2,y-(gameScreen.tileSize*3),gameScreen.tileSize*5,gameScreen.tileSize*6);
			}
		}
		g2.drawImage(fallingSprites,x,y-gameScreen.tileSize + 5,gameScreen.tileSize,gameScreen.tileSize,null);

	}
}
