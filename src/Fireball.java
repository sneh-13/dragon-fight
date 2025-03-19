import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Fireball extends Character{

	Screen gameScreen;
	KeyboardListener kL;
	Player player;
	BufferedImage fireballSpriteRight;
	BufferedImage fireballSpriteLeft;
	ArrayList<Fireball> fbArray = new ArrayList<>();
	boolean fbShot = false;
	int distanceMoved;
	int startingX;
	int startingY;
	String fireballDirection;

	public Fireball (Screen gameScreen, KeyboardListener kL, Player player) {
		this.player = player;
		this.gameScreen = gameScreen;
		this.kL = kL;

		setFireballDefaults();
	}

	public void setFireballDefaults () {
		collisionBox = new Rectangle(0,0,gameScreen.tileSize,gameScreen.tileSize);
		fireballDirection = "Right";
		x= 0;
		y = 0;
		speed = 8;
		getFireballSprites();
	}

	public void getFireballSprites () {
		try {
			fireballSpriteRight = ImageIO.read(getClass().getResourceAsStream("/player/fireball_right.png"));
			fireballSpriteLeft = ImageIO.read(getClass().getResourceAsStream("/player/fireball_left.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void update() {

		if (kL.shotKeyPressed == true) {
			if (fbArray.size() == 0) {
				startingX = (int) player.collisionBox.getCenterX();
				startingY = (int) player.collisionBox.getCenterY();
				fbArray.add(new Fireball(gameScreen,kL,player));
				fbArray.get(0).x = startingX;
				fbArray.get(0).y = startingY;
				fbShot = true;
				gameScreen.startSoundEffect(6);

				if (kL.fireballDirection.equals("Right")) {
					fbArray.get(0).fireballDirection = "Right";
				} else if (kL.fireballDirection.equals("Left")) {
					fbArray.get(0).fireballDirection = "Left";
				}
			} 
		}

		if (fbArray.size() > 0) {
			kL.shotKeyPressed = false;
			if (fbArray.get(0).fireballDirection.equals("Right")) {
				fbArray.get(0).x = fbArray.get(0).x + speed;
			} else if (fbArray.get(0).fireballDirection.equals("Left")) {
				fbArray.get(0).x = fbArray.get(0).x - speed;
			}
			fbArray.get(0).distanceMoved = fbArray.get(0).distanceMoved + speed;
			if (fbArray.get(0).distanceMoved > gameScreen.tileSize*3) {
				fbArray.remove(0);
				collisionBox = null;
			} else {
				collisionBox = new Rectangle(fbArray.get(0).x,fbArray.get(0).y,gameScreen.tileSize/3,gameScreen.tileSize/3);
			}
			if (collisionBox != null) {
				enemyEnterRadius();
			}
		}
	}

	//check if the fireball collision box collides with enemy so you can remove hp from enemy 
	public void enemyEnterRadius() {


		if(gameScreen.e1.size()>0 && collisionBox != null)
		{
			for(int i=0; i<gameScreen.e1.size(); i++ )
			{

				if (collisionBox != null && gameScreen.e1.get(i).collisionBox.intersects(collisionBox)) {
					fbArray.remove(0);
					collisionBox = null;
					//System.out.println("enemy1 hit");
					player.points = player.points + 50;
					gameScreen.startSoundEffect(3);
					gameScreen.e1.remove(i);
					break;
				} else if (collisionBox != null && gameScreen.e1.get(i).balloonLeftCollisionBox.intersects(collisionBox) || gameScreen.e1.get(i).balloonRightCollisionBox.intersects(collisionBox)) {
					//System.out.println("balloon pop");
					fbArray.remove(0);
					collisionBox = null;
					gameScreen.e1.get(i).balloonPopped = true;
					break;
				}

			}
		}
		if (gameScreen.e2.size()>0 && collisionBox != null) {
			for(int i=0; i<gameScreen.e2.size(); i++ ) {
				if (collisionBox != null && gameScreen.e2.get(i).collisionBox.intersects(collisionBox)) {
					fbArray.remove(0);
					collisionBox = null;
					//System.out.println("enemy2 hit");
					player.points = player.points + 150;
					gameScreen.startSoundEffect(3);
					gameScreen.e2.remove(i);
					break;
				} 
				else if (collisionBox != null && gameScreen.e2.get(i).balloonLeftCollisionBox.intersects(collisionBox) || gameScreen.e2.get(i).balloonRightCollisionBox.intersects(collisionBox))
				{
					//System.out.println("balloon pop");
					fbArray.remove(0);
					collisionBox = null;
					gameScreen.e2.get(i).balloonPopped = true;
					break;
				}

			}
		}

		if (gameScreen.e3.size()>0 && collisionBox != null) {
			for(int i=0; i<gameScreen.e3.size(); i++ ) {
				if (collisionBox != null && gameScreen.e3.get(i).collisionBox.intersects(collisionBox)) {
					fbArray.remove(0);
					collisionBox = null;

					if (gameScreen.e3.get(i).hitpoints == 2) {
						gameScreen.e3.get(i).hitpoints = 1;
						break;
					}  else if (gameScreen.e3.get(i).hitpoints == 1) {
						//System.out.println("enemy3 hit");
						player.points = player.points + 250;
						gameScreen.startSoundEffect(3);
						gameScreen.e3.remove(i);
						break;
					}
					collisionBox = new Rectangle(0,0,gameScreen.tileSize/3,gameScreen.tileSize/3);

				} 
				else if (collisionBox != null && gameScreen.e3.get(i).balloonLeftCollisionBox.intersects(collisionBox) || gameScreen.e3.get(i).balloonRightCollisionBox.intersects(collisionBox))
				{
					collisionBox = new Rectangle(fbArray.get(0).x,fbArray.get(0).y,gameScreen.tileSize/3,gameScreen.tileSize/3);
					//System.out.println("balloon pop");
					fbArray.remove(0);
					collisionBox = null;
					gameScreen.startSoundEffect(0);

					if (gameScreen.e3.get(i).hitpoints == 2) {
						gameScreen.e3.get(i).hitpoints = 1;
						break;
					} else if (gameScreen.e3.get(i).hitpoints == 1) {
						gameScreen.startSoundEffect(3);
						gameScreen.e3.get(i).hitpoints = 0;
						gameScreen.e3.get(i).balloonPopped = true;
						break;
					}
				}
			}
		}
	}

	//draw fireball
	public void draw(Graphics2D g2) {
		BufferedImage image = null;

		if (gameScreen.testingMode == true && collisionBox != null) {
			g2.draw(collisionBox);
		}
		g2.setColor(Color.yellow);
		if (fbShot == true && collisionBox != null) {
			if (fbArray.size() > 0) {
				if (fbArray.get(0).fireballDirection.equals("Right")) {
					image = fireballSpriteRight;
				} else {
					image = fireballSpriteLeft;
				}
			}
			//g2.fillOval(collisionBox.x,collisionBox.y,gameScreen.tileSize/3,gameScreen.tileSize/3);
			if (collisionBox != null) {
				g2.drawImage(image,collisionBox.x,collisionBox.y,gameScreen.tileSize/3,gameScreen.tileSize/3,null);
			}
		}
	}

	/*
public void enemyThreeFb () {
	for(int i =0; i<gameScreen.e1.size(); i++) {
		if (gameScreen.e1.get(i).y == player.y) {
			System.out.println("Enemy3 y = player y. shoot e3 fireball");
		}
	}
}
	 */
}