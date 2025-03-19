import java.awt.event.KeyEvent;

import java.awt.event.KeyListener;


public class KeyboardListener implements KeyListener {

	Screen screen;
	Fireball fb;
	String fireballDirection = "Right";
	//int lr = 0; 
	//1 left
	//2 right 

	public static boolean upPressed, rightPressed, leftPressed, spacePressed, shotKeyPressed;

	KeyboardListener(Screen screen) {
		this.screen = screen;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int buttonCode = e.getKeyCode();

		/*if (buttonCode == KeyEvent.VK_W) {
			upPressed = true;
		}*/
		if (buttonCode == KeyEvent.VK_D) {
			rightPressed = true;
			//lr = 2;
			fireballDirection = "Right";
		}
		else if (buttonCode == KeyEvent.VK_A) {
			leftPressed = true;
			//lr = 1;
			fireballDirection = "Left";
		} else if (buttonCode == KeyEvent.VK_T) {
			if (screen.testingMode == true) {
				screen.testingMode = false;
			} else {
				screen.testingMode = true;
			}
		}

		if (buttonCode == KeyEvent.VK_ESCAPE) { 
			if(screen.gameState == screen.runState) {
				screen.gameState = screen.menuState;

			}else if (screen.gameState == screen.menuState){
				screen.gameState = screen.runState;

			}
		}
	}



	@Override
	public void keyReleased(KeyEvent e) {
		int buttonCode = e.getKeyCode();

		/*if (buttonCode == KeyEvent.VK_W) {
			upPressed = false;
		}*/
		if (buttonCode == KeyEvent.VK_D) {
			rightPressed = false;
		}

		else if (buttonCode == KeyEvent.VK_A) {
			leftPressed = false;
		}
		else if (buttonCode == KeyEvent.VK_SPACE) {
			spacePressed = true;
		} else if (buttonCode == KeyEvent.VK_S) {
			shotKeyPressed = true;
		}
	}
}