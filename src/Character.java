/*************************************************
 * Group D
 * ICS4U
 * January 20, 2023
 * Dragon Fight
 * This class is the parent class of all enemies and the player, and the fireball. It contains
 * all of the important variables that are shared between all the entities, and also the set spawn method, 
 * so that the x and y positions of any entity can be changed at any time. 
 ************************************************/

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Character {
	public int x;
	public int y;
	public int speed;
	public BufferedImage up1, up2, left1, left2, right1, right2;//variables for the different sprites to be set to
	public String characterDirection; //string that will store which direction the character is facing 
	public Rectangle collisionBox = new Rectangle(x,y,1,1);//default all of the rectangles so that the program will never crash because a rectangle is called before it is instantiated, since they are all update in the first frame to be in their correct position, having a default spot and size that is not real is never an issue
	public Rectangle leftCollisionBox = new Rectangle(x,y,1,1);
	public Rectangle rightCollisionBox = new Rectangle(x,y,1,1);
	public Rectangle balloonLeftCollisionBox = new Rectangle(x,y,1,1);
	public Rectangle balloonRightCollisionBox = new Rectangle(x,y,1,1);
	Rectangle parachuteCollisionBox = new Rectangle(x,y,1,1);
	public Rectangle radius;
	public Rectangle agroRadius;
	public int spriteCounter = 0;
	public int spriteNumber = 1;	
	public int updateMethodCounter = 0;
	public int lives = 3; //player starts with 3 lives
	public int hitpoints = 2; //player starts with 2 hitpoints
	boolean playerPush = true;
	
	public void setSpawn (int x, int y) { //set the x and y of the entity to the integers input into this method
		this.x = x;
		this.y = y;
	}
}
