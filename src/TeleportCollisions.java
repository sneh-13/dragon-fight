
import java.awt.Rectangle;

public class TeleportCollisions extends Character {

	Screen gameScreen; //instantiate the game panel
	Character c1; //instantiate a character (parent class of player and all enemies)

	TeleportCollisions (Screen gameScreen, Character c1) { //constructor
		this.gameScreen = gameScreen; //set this game screen to the actual game screen from main
		this.c1 = c1; //set this character to the character that this class is being called for
		x = 0; //default x
		y = 0; //default y
		leftCollisionBox = new Rectangle(x-gameScreen.tileSize-(gameScreen.tileSize/2),y,gameScreen.tileSize,gameScreen.screenHeight); //collision box on the left side of the screen
		rightCollisionBox = new Rectangle(x+gameScreen.screenWidth+gameScreen.tileSize+(gameScreen.tileSize/2),y,gameScreen.tileSize,gameScreen.screenHeight); //collision box on the right side of the screen
		teleportCharacter(); //call teleport character method
	}

	public void teleportCharacter() {
		if (c1.collisionBox.intersects(leftCollisionBox)) { //checks if the chosen characters collision box intersects with the left collision box
			c1.x = gameScreen.screenWidth + (gameScreen.tileSize/2); //if player leaves on the left side then set their x position to be the max screen size plus an additional tile plus half a tile, this is to avoid an infinite teleportation loop
		}
		else if (c1.collisionBox.intersects(rightCollisionBox)) { //runs if the characters collision box intersects with the right collision box from this class
			c1.x = 0 - (gameScreen.tileSize/2); //change characters x position
		}
	}
}
