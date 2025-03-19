
/*************************************************
 * Group D
 * ICS4U
 * January 20, 2023
 * Dragon Fight
 * This class is purely for testing, and is never used in the program
 ************************************************/
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EnemyFour extends Character {
	Screen gameScreen;
	Player player;
	BufferedImage balloon;
	BufferedImage balloons;
	BufferedImage parachute;
	Rectangle intersectionRectangle;
	int indexOfIntersection;
	int goalX;
	int goalY;
	boolean xEqualPlayer = false;
	boolean chooseNewDirection = true;
	int chooseDirection;
	boolean balloonPopped = false;
	TileCollisionChecker CheckCol = new TileCollisionChecker();


	public EnemyFour (Screen gameScreen, Player player) {
		this.gameScreen = gameScreen;
		this.player = player;
		setEnemyDefaults();
	}

	public void setEnemyDefaults() {
		//x = gameScreen.tileSize * 10;
		//y = gameScreen.tileSize * 2;
		speed = 1;
		getEnemyThreeSprites();
	}

	public void setSpawn(int x, int y) {
		this.x = x;
		this.y = y;
		//gameScreen.e1.remove(this);
	}

	public void getEnemyThreeSprites() {
		try {
			balloon = ImageIO.read(getClass().getResourceAsStream("/enemyTwo/balloon_orange.png"));
			balloons = ImageIO.read(getClass().getResourceAsStream("/enemyThree/balloons_orange.png"));
			parachute = ImageIO.read(getClass().getResourceAsStream("/enemyOne/parachute.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playerHitsEnemyBalloon() {
		if (player.collisionBox.intersects(balloonRightCollisionBox)) {
			hitpoints --;
			player.x = player.x + gameScreen.tileSize; 
		}
		else if (player.collisionBox.intersects(balloonLeftCollisionBox)) {
			hitpoints --;
			player.x = player.x - gameScreen.tileSize; 
		}
	}

	public void update() {
		System.out.println("HELLO");
		goalX = (int)player.collisionBox.getCenterX(); //set target x to the player's x
		goalY = (int)player.collisionBox.getCenterY(); //set target y to the player's y
		if (balloonPoppedChecker(player) == false && hitpoints > 0) {
				if (closeToPlatform() == true) { //if the enemy is close to a platform, run the ai that deals with platforms
					chooseCorrectPath();
				} else { //if the enemy is not close to a platform, run the regular player hunting ai
					huntPlayer();
				}
				playerHitsEnemyBalloon();
				hitCharacter(player);
		} else {
			y = y + speed;
		}
		collisionBox = new Rectangle(x,y,gameScreen.tileSize,gameScreen.tileSize); //collision box for enemy that constantly updates x and y positions based on enemy movement
		leftCollisionBox = new Rectangle(x,y-gameScreen.tileSize/2,gameScreen.tileSize/2,gameScreen.tileSize + gameScreen.tileSize/2); //left half of enemy
		rightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y-gameScreen.tileSize/2,gameScreen.tileSize/2,gameScreen.tileSize + gameScreen.tileSize/2);//right half of enemy
		balloonLeftCollisionBox = new Rectangle(x,y-gameScreen.tileSize+10,gameScreen.tileSize/2,gameScreen.tileSize/2);
		balloonRightCollisionBox = new Rectangle(x+(gameScreen.tileSize/2),y-gameScreen.tileSize+10,gameScreen.tileSize/2,gameScreen.tileSize/2);
	}

	public boolean closeToPlatform() {//this method checks if the enemy is intersecting the radius around a platform
		boolean closeToPlatform = false; //create variable to be returned
		for (int i = 0; i < gameScreen.manageTiles.platformBlockRadius.size(); i++) { //for loop that searches through the radius array
			if (collisionBox.intersects(gameScreen.manageTiles.platformBlockRadius.get(i))) { //runs if the enemy intersects the current radius rectangle being checked
				closeToPlatform = true; //change variable to true since enemy is close to a platform
				indexOfIntersection = i; //set index of intersection variable to the current index, this is to avoid having multiple for loops throughout the class, this variable allows me to only have to run the check through one for loop
				break; //break out of the loop because the enemy can not intersect more than one radius at a time, so if it is intersecting one, the loop should be broken out of
			} else { //if the enemy does not intersect any of the radius rectangles, return false
				closeToPlatform = false;
			}
		}
		return(closeToPlatform); //return results of the above for loop
	}

	public void huntPlayer() { //player hunting ai when the enemy is not close to any platforms
		xEqualPlayer = false; // this variable and the below variable are for the other ai, but they should be reset to their defaults when the enemy returns the the regular player hunting ai
		chooseNewDirection = true;
		if (collisionBox.getCenterX() < goalX) { //if the player is to the right of the enemy, enemy should move right
			x = x + speed;
		} 
		else if (collisionBox.getCenterX() > goalX) { // if the player is left of the enemy, enemy should move left
			x = x - speed;
		}
		if (collisionBox.getCenterY() < goalY) { //if player is below enemy, enemy should move down, vertical movement has a different if statement than horizontal, so that the enemy has the ability to move diagonally (vertical and horizontal in one update)
			y = y + speed;
		}
		else if (collisionBox.getCenterY() > goalY) { //if player is above enemy, enemy should move up
			y = y - speed;
		}
	}

	public void chooseCorrectPath() { //ai that runs if the enemy is close to a platform (intersects a platform radius rectangle)
		intersectionRectangle = collisionBox.intersection(gameScreen.manageTiles.platformBlockRadius.get(indexOfIntersection)); //create an intersection rectangle, this rectangle represents the entire intersection between the enemy and the platform block radius, for example, if the enemy only intersects 1 pixel of the platform radius, then this intersection rectangle would have a width of 1 and a height of 1, with the x and y values of the pixel that is intersected

		if (player.collisionBox.intersects(gameScreen.manageTiles.platformBlockRadius.get(indexOfIntersection))) { //runs if the player also intersects the platform block radius
			int distanceLeftBelow; // create variables that will be used to check left and right distances to get around a platform if the enemy is stuck underneath
			int distanceRightBelow;
			distanceLeftBelow = (int)collisionBox.getCenterX() - gameScreen.manageTiles.platformBlockRadius.get(indexOfIntersection).x; // the distance to go left around the platform is set to the enemies x - the x of the platform block radius, since x and y start in the top left corner of a rectangle, the left check can just use the x value
			distanceRightBelow = (gameScreen.manageTiles.platformBlockRadius.get(indexOfIntersection).x + gameScreen.manageTiles.platformBlockRadius.get(indexOfIntersection).width) - (int)collisionBox.getCenterX(); //the right distance is set to the x value of the platform radius + the width of the platform radius, - the enemies x, the width has to be added to the x of the platform radius since I want to measure the right now, and the x starts at the left, therefore to get the right x value, I must add the width of the rectangle to the x value
			//System.out.println("Right: "+distanceRight+", Left:"+distanceLeft); 
			if (collisionBox.y == player.collisionBox.y || (player.collisionBox.y < gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).y - (gameScreen.tileSize/2) && collisionBox.y < gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).y - gameScreen.tileSize)) { //first check if the enemy and player have the same y value, meaning that they are at the same height, and the enemy will only have to go right or left, if that is false, then check if the player and enemy are both above the platform 
				if (collisionBox.getCenterX() < goalX) { //if the player is right of the enemy, the enemy should move right
					x = x + speed;
				} else if (collisionBox.getCenterX() > goalX) {//if the player is left of the enemy, the enemy should move left
					x = x - speed;
				}
			} else if (collisionBox.y + gameScreen.tileSize > gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).y && player.collisionBox.y < gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).y) { //enemy below platform
				if (intersectionRectangle.x > gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).x + gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).width || intersectionRectangle.x + intersectionRectangle.width < gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).x) { //if the enemy is to the right or left of the platform, meaning it is not vertically obstructed
					y = y - speed; //since enemy is below platform, enemy should move up
				} else { //else meaning the enemy is under the platform, and can not freely move up without being obstructed
					if (distanceRightBelow <= distanceLeftBelow) { //the goal is for the enemy to take the quickest path to the player, so if the right distance is less than or equal to the left distance, the enemy should go right (equal to so that the enemy never gets stuck when the two distances are equal)
						x = x + speed;
					} else {
						x = x - speed; //if the left route is faster, go left
					}
				}
			} else { //above platform, this else statement runs identically to the else if above, however this time the enemy will move down since it is above the platform in this scenario
				if (intersectionRectangle.x > gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).x + gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).width || intersectionRectangle.x + intersectionRectangle.width < gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).x) { //if the enemy is to the right or left of the platform, meaning it is not vertically obstructed
					y = y + speed;
				} else {
					if (distanceRightBelow <= distanceLeftBelow) {
						x = x + speed;
					} else {
						x = x - speed;
					}
				}
			}

		} else { // else meaning this part of the ai runs if the enemy is intersecting the platform radius, but the player is not
			if (collisionBox.getCenterX() == goalX) { //if the enemy and player have the same x value (meaning that the player is on the opposite side of a platform than the enemy, but they are lined up in terms of their x value)
				xEqualPlayer = true; //boolean that is required to make sure in the else statement at the bottom of the method, the correct code is running
				if (chooseNewDirection == true) { //if the choose new direction is true, then a new random number is allowed to be generated
					chooseDirection = (int) (Math.random()*50+1); //generate a random number between 1 and 50
					chooseNewDirection = false; //make the choose new direction variable false so that a new number can not be generated again until it is supposed to be
				}
				if (chooseDirection % 2 == 0) { //if the number is even (its remainder is 0) go right, otherwise go left, this will only move the enemy once, but this is necessary so that when the code at the bottom takes over, it will not get stuck
					x = x + speed;
				} else {
					x = x - speed;
				}
			} else if (intersectionRectangle.getCenterY() > gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).y + gameScreen.tileSize/2 && xEqualPlayer == false) { //runs if enemy is below the platform
				if (intersectionRectangle.x + intersectionRectangle.width < gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).x || intersectionRectangle.x > gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).x + gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).width ) { //if the enemy is to the right or left of the platform and is not vertically obstructed
					if (collisionBox.getCenterY() > goalY) { //move up if the player is above, otherwise move down
						y = y - speed;
					} else {
						y = y + speed;
					}
				} else { //runs if the enemy is vertically obstructed
					if (collisionBox.getCenterX() < goalX) { //if the player is to the right, move right, otherwise move left
						x = x + speed;
					} else {
						x = x - speed;
					}
				}
				//System.out.println("Below Platform");
			} else if (intersectionRectangle.y  < gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).y && xEqualPlayer == false) { //runs if the enemy is above the platform
				//System.out.println("Above Platform");
				if (intersectionRectangle.x > gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).x + gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).width || intersectionRectangle.x + intersectionRectangle.width < gameScreen.manageTiles.platformBlocks.get(indexOfIntersection).x) { //runs if the enemy is not vertically obstructed
					if (collisionBox.getCenterY() > goalY) { //move up if the player is above, otherwise move down
						y = y - speed;
						//System.out.println("GOING UP");
					} else if (collisionBox.getCenterY() < goalY) { 
						y = y + speed;
						//System.out.println("GOING DOWN");
					} else {
						if (collisionBox.getCenterX() < goalX) {
							x = x + speed;
						} else {
							x = x - speed;
						}
					}
				} else {
					if (collisionBox.getCenterX() < goalX) { //if the player is to the right, move right, otherwise move down
						x = x + speed;
					} else {
						x = x - speed;
					}
				}
			} else { 
				//System.out.println("NOT ABOVE OR BELOW");
				if (xEqualPlayer == false) { //runs if the player's x is not equal to enemy x
					if (collisionBox.getCenterY() > goalY) {//move up if the player is above the enemy, otherwise move left
						y = y - speed;
					} else {
						y = y + speed;
					}
				} else { //runs if the player's x is equal to enemy x
					if (chooseDirection % 2 == 0) { //if the random number is even go right, otherwise go left
						x = x + speed;
						//System.out.println("Move Right: "+chooseDirection);
					} else {
						x = x - speed;
						//System.out.println("Move Left: "+chooseDirection);
					}
				}
			}
		}
		//System.out.println("Width: "+intersectionRectangle.width+", Height: "+intersectionRectangle.height);
	}

	public void hitCharacter(Player player) {
		this.player = player;
		if (player.invincibility == false) { 
			CheckCol.tilePlayer_Monster(player, this); //method checks if player or monster should be pushed back based on if there are blacklisted platforms or collisions behind it. (Prevents player/monster from clipping into Tiles) 
			if (this.player.collisionBox.intersects(leftCollisionBox)) {
				if (playerPush == false) {
					this.player.x = this.player.x + 0; // don't move player
					x = x + gameScreen.tileSize; // push monster back instead
				} else {
					this.player.x = this.player.x - gameScreen.tileSize;
				}
				this.player.playerIsHit();
				this.player.invincibility = true;
			}
			else if (this.player.collisionBox.intersects(rightCollisionBox)) { //has to be else if because when it was if you could go through the middle of the enemy because the +48 and minus+48 were cancelling out to no movement
				if (playerPush == false) {
					this.player.x = this.player.x + 0; // don't move player
					x = x - gameScreen.tileSize; // push monster back instead
				} else {
					this.player.x = this.player.x + gameScreen.tileSize;
				}
				this.player.playerIsHit();
				this.player.invincibility = true;
			}
		} 
	}

	public boolean balloonPoppedChecker(Player player) {
		this.player = player;
		if (player.collisionBox.intersects(balloonLeftCollisionBox) && player.collisionBox.intersects(balloonRightCollisionBox)) {
			player.y = player.y-gameScreen.tileSize/2;
			if (hitpoints == 0) {
				balloonPopped = true;
			}
		} else if (this.player.collisionBox.intersects(balloonLeftCollisionBox)) {
			if (playerPush == true) {
				this.player.x = this.player.x - gameScreen.tileSize/2; //push player back since monster will be obstructed back
				if (hitpoints == 0) {
					balloonPopped = true;
				}
			} else {
				this.player.x = this.player.x - 0; // don't move player
				x = x + gameScreen.tileSize/2; // push back monster instead since its getting hurt
				if (hitpoints == 0) {
					balloonPopped = true;
				}
			}
		} else if (this.player.collisionBox.intersects(balloonRightCollisionBox)) {
			if (playerPush == true) {
				this.player.x = this.player.x + gameScreen.tileSize/2; //push player back since monster will be obstructed back
				if (hitpoints == 0) {
					balloonPopped = true;
				}
			} else {
				this.player.x = this.player.x + 0; // don't move player
				x = x - gameScreen.tileSize/2; // push back monster instead instead since its getting hurt
				if (hitpoints == 0) {
					balloonPopped = true;
				}
			}
		}
		return (balloonPopped);
	}

	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		if (hitpoints == 2) {
			image = balloons;
		} else if (hitpoints == 1) {
			image = balloon;
		} else {
			image = parachute;
		}
		g2.setColor(new Color(213, 49, 49)); //same colour I used to make the balloon
		g2.fillRect(x, y, gameScreen.tileSize, gameScreen.tileSize);

		g2.setColor(Color.blue);
		g2.draw(balloonLeftCollisionBox);
		g2.setColor(Color.black);
		g2.draw(balloonRightCollisionBox);
		g2.drawImage(image,x,y-gameScreen.tileSize,gameScreen.tileSize,gameScreen.tileSize,null);
	}

}
