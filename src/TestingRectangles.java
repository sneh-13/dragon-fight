import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class TestingRectangles {

	Screen gameScreen;
	Rectangle rec1 = new Rectangle(gameScreen.tileSize*8,gameScreen.tileSize*6,gameScreen.tileSize,gameScreen.tileSize);
	Point rec1BottomMiddle = new Point((int)rec1.getCenterX(),(int)rec1.getCenterY()+(gameScreen.tileSize)/2 - 1);
	Rectangle rec2 = new Rectangle(gameScreen.tileSize*2 + gameScreen.tileSize/2,gameScreen.tileSize*6,gameScreen.tileSize,gameScreen.tileSize);
	Point rec2BottomRight;
	Rectangle rec3 = rec1.intersection(rec2);
	int x = gameScreen.tileSize*2 + gameScreen.tileSize/2;

	TestingRectangles(Screen gameScreen) {
		this.gameScreen = gameScreen;
	}

	public void update () {
		if (rec3.getWidth() <= gameScreen.tileSize/2) {
			x = x+1;
		}
		rec2 = new Rectangle(x,gameScreen.tileSize*6,gameScreen.tileSize,gameScreen.tileSize);
		rec2BottomRight = new Point((int)rec2.getX()+(gameScreen.tileSize - 1),(int)rec2.getY() + (gameScreen.tileSize-1));
		rec3 = rec1.intersection(rec2);
		System.out.println("Width: "+rec3.getWidth()+ ", Height: " + rec3.getHeight());
	}

	public void draw(Graphics2D g2) { 
		g2.draw(rec1);
		g2.setColor(Color.cyan);
		g2.draw(rec2);
		g2.setColor(Color.darkGray);
		g2.drawOval((int)rec2BottomRight.getX(),(int)rec2BottomRight.getY(),1,1);
		g2.drawOval((int)rec1BottomMiddle.getX(),(int)rec1BottomMiddle.getY(),1,1);
		g2.setColor(Color.black);
		g2.fillRect((int)rec3.getX(),(int)rec3.getY(),(int)rec3.getWidth(),(int)rec3.getWidth());
	}

}
