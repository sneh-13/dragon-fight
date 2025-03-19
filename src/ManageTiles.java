import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ManageTiles {

	Screen gameScreen;
	public static Tile [] t;
	public static int tileNumberInMap [] []; //has to store x and y components
	//public ArrayList <Rectangle> platforms = new ArrayList<>();
	public ArrayList <Rectangle> platformBlocks = new ArrayList<>();
	public ArrayList <Rectangle> platformBlockRadius = new ArrayList<>();
	public ArrayList <Rectangle> groundBlocks = new ArrayList<>();
	public ArrayList <Rectangle> waterBlocks = new ArrayList<>();
	public boolean fillPlatformBlocksArray = true;
	public boolean fillGroundBlocksArray = true;
	public boolean fillWaterBlocksArray = true;
	public int lengthOfPlatformBlock = 0;
	public int lengthOfGroundBlock = 0;
	public int lengthOfWaterBlock = 0;
	public int tileNumber;

	public ManageTiles (Screen gameScreen) {
		this.gameScreen = gameScreen;
		t = new Tile [10]; //create a new tile array that will store ten different types of tiles 
		tileNumberInMap = new int[gameScreen.maxColumnSize][gameScreen.maxRowSize];//this will store the numbers from the map in an array
		loadTileImage(); 
	}

	public void loadTileImage () {
		try {
			t[0] = new Tile ();
			t[0].image = ImageIO.read(getClass().getResource("/tiles/brick_wall.png"));
			t[1] = new Tile ();
			t[1].image = ImageIO.read(getClass().getResource("/tiles/cloud.png"));
			t[2] = new Tile ();
			t[2].image = ImageIO.read(getClass().getResource("/tiles/grass.png"));
			t[2].ground = true;
			t[2].collision = true;
			t[3] = new Tile ();
			t[3].image = ImageIO.read(getClass().getResource("/tiles/platform.png"));
			t[3].platform = true;
			t[4] = new Tile ();
			t[4].image = ImageIO.read(getClass().getResource("/tiles/sky.png"));
			t[5] = new Tile ();
			t[5].image = ImageIO.read(getClass().getResource("/tiles/water.png"));
			t[5].water = true;
			t[6] = new Tile ();
			t[6].image = ImageIO.read(getClass().getResource("/tiles/window_bottom.png"));
			t[7] = new Tile ();
			t[7].image = ImageIO.read(getClass().getResource("/tiles/window_top.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetLevelArrays() {
		//clear all arrays
		platformBlocks.removeAll(platformBlocks);
		platformBlockRadius.removeAll(platformBlockRadius);
		groundBlocks.removeAll(groundBlocks);
		waterBlocks.removeAll(waterBlocks);

		//refill array
		fillPlatformBlocksArray = true;
		fillGroundBlocksArray = true;
		fillWaterBlocksArray = true;
	}

	public void loadMap() { //load this in the screen method
		if (gameScreen.level == 1) {
			loadMapNumbers("/maps/level01.txt");
		} else if (gameScreen.level == 2) {
			loadMapNumbers("/maps/level02.txt");
		} else if (gameScreen.level == 3) {
			loadMapNumbers("maps/level03.txt");
		}
	}

	public void loadMapNumbers (String mapName) {
		try {
			InputStream iS = getClass().getResourceAsStream(mapName); 
			BufferedReader bR = new BufferedReader (new InputStreamReader(iS));

			int column = 0;
			int row = 0;

			while (column < gameScreen.maxColumnSize && row < gameScreen.maxRowSize) {
				String line = bR.readLine();
				//reads a single line and stores it in this string variable

				while (column < gameScreen.maxColumnSize) {
					String mapNumbers [] = line.split(" ");//this splits the string every time a space is found and then adds it into the mapNumbers array

					int number = Integer.parseInt(mapNumbers[column]);//cast the numbers stored in the string array to integers, and search at the index that the collumn is currently at
					tileNumberInMap[column][row] = number; //set the x and y at the current column and row to the current number read from the map at that same column and row spot
					column++;
				}
				if (column == gameScreen.maxColumnSize) {
					column = 0;//reset column to 0 to start the column search over again
					row++; //increment row so that the first column in the next row is loaded from
				}
			}
			bR.close();
		} catch (Exception e) {

		}
	}

	public void draw (Graphics2D g2) {

		loadMap();

		int x = 0;
		int y = 0;
		int column = 0;
		int row = 0;

		while (column < gameScreen.maxColumnSize && row < gameScreen.maxRowSize) {
			tileNumber = tileNumberInMap[column][row]; //temporary variable that stores the current x and y values of the tiles, as column and row increment, so will the tile being searched and drawn			
			g2.drawImage(t[tileNumber].image,x,y,gameScreen.tileSize,gameScreen.tileSize,null);
			//g2.setColor(Color.blue);
			//g2.drawRect(x,y,gameScreen.tileSize,gameScreen.tileSize);

			if (t[this.tileNumber] != null) {
				if (t[tileNumber].ground == true) {
					int tileToLeft;
					if (x == 0) { //if the tile to the left of the first grass tile is in bounds
						lengthOfGroundBlock = 1;
						int temporaryRightChecker = tileNumberInMap[column+lengthOfGroundBlock][row];
						while (temporaryRightChecker == 2) {
							lengthOfGroundBlock++;
							temporaryRightChecker = tileNumberInMap[column+lengthOfGroundBlock][row];
						}
						groundBlocks.add(new Rectangle(x,y,gameScreen.tileSize*lengthOfGroundBlock,gameScreen.tileSize));

					} else if (x < (gameScreen.maxColumnSize*gameScreen.tileSize)-gameScreen.tileSize) { //720  
						if (fillGroundBlocksArray == true) {
							tileToLeft = tileNumberInMap[column-1][row];
							if (tileToLeft != 2) {

								lengthOfGroundBlock = 1;
								int temporaryRightChecker = tileNumberInMap[column+lengthOfGroundBlock][row];
								int tempX = x;
								//System.out.println(gameScreen.maxColumnSize +" "+ gameScreen.maxRowSize);
								//System.out.println(gameScreen.maxColumnSize*gameScreen.tileSize);

								while (temporaryRightChecker == 2) {
									lengthOfGroundBlock++;
									if (tempX == (gameScreen.maxColumnSize*gameScreen.tileSize) - (gameScreen.tileSize*2)) {
										break;
									} else {
										temporaryRightChecker = tileNumberInMap[column+lengthOfGroundBlock][row];
									}
									tempX = tempX + gameScreen.tileSize;
								}
								groundBlocks.add(new Rectangle(x,y,gameScreen.tileSize*lengthOfGroundBlock,gameScreen.tileSize));
								//System.out.println(groundBlocks.size());
							}
						}
					} else if (x == (gameScreen.tileSize*gameScreen.maxColumnSize) - gameScreen.tileSize && tileNumberInMap[column-1][row] != 2) {
						groundBlocks.add(new Rectangle(x,y,gameScreen.tileSize,gameScreen.tileSize));
					}
				} else if (t[tileNumber].platform == true) {
					int temp = tileNumberInMap[column][row-1];
					int tileToRight = tileNumberInMap[column+1][row];
					int tileToLeft = tileNumberInMap[column-1][row];

					g2.drawImage(t[temp].image,x,y,gameScreen.tileSize,gameScreen.tileSize,null);
					if (t[3] != null) {
						g2.drawImage(t[3].image,x,y,gameScreen.tileSize,gameScreen.tileSize,null);	
					}

					if (tileToLeft != 3) { //this if statement is to find the first platform in a block of platforms, it does this by running if the tile to the left of the current platform is not another platform(3)
						lengthOfPlatformBlock = 1; //set the length of the platform integer variable to 1, this is because we have now found the first platform in the block, so we know that there is at least 1 platform
						int temporaryRightChecker = tileNumberInMap[column+lengthOfPlatformBlock][row]; //create a temporary checker variable that checks the tile to the right of the current platform, if this value is 3, that means the tile to the right is also a platform, which means the below while loop will run to see how many more platforms there are to the right 

						while (temporaryRightChecker == 3) { //this while loop runs while the tile to the right of the current tile being checked is still another platform
							lengthOfPlatformBlock++; //increment the length, this will tell us how many platforms are in the block, while at the same time acting as a value to add to the column check in the array below
							temporaryRightChecker = tileNumberInMap[column+lengthOfPlatformBlock][row]; //check the next platform to the right
						}
						//platformBlocks.add(new Rectangle(x-1,y+gameScreen.tileSize/2-1,gameScreen.tileSize*lengthOfPlatformBlock+2,gameScreen.tileSize/2+2)); //create a new rectangle and add it to the array, the width of the rectangle is one tile * the length integer from above, I also add one pixel around the entire platform, to ensure that there is a small place for other rectangles to intersect
						if (fillPlatformBlocksArray == true) {
							platformBlocks.add(new Rectangle(x,y+gameScreen.tileSize/2,gameScreen.tileSize*lengthOfPlatformBlock,gameScreen.tileSize/2));
							platformBlockRadius.add(new Rectangle(x-gameScreen.tileSize/2,y,gameScreen.tileSize*lengthOfPlatformBlock+gameScreen.tileSize,gameScreen.tileSize/2 + gameScreen.tileSize));
						}
						lengthOfPlatformBlock = 0; //reset the length to 0 so that the next block will be created from a length of 0,
					}

					/*if (tileToRight != 3) {
					platforms.add(new Rectangle(x,y+gameScreen.tileSize/2-1,gameScreen.tileSize+1,gameScreen.tileSize/2+2));
				} else if (tileToLeft != 3) {
					platforms.add(new Rectangle(x-1,y+gameScreen.tileSize/2-1,gameScreen.tileSize+1,gameScreen.tileSize/2+2));
				} else {
					platforms.add(new Rectangle(x,y+gameScreen.tileSize/2-1,gameScreen.tileSize,gameScreen.tileSize/2+2));
				}*/

				} else if (t[tileNumber].water == true) {
					int tileToLeft = tileNumberInMap[column-1][row];

					if (tileToLeft != 5) {
						lengthOfWaterBlock = 1;
						int temporaryRightChecker = tileNumberInMap[column+1][row];

						while (temporaryRightChecker == 5) {
							lengthOfWaterBlock++;
							temporaryRightChecker = tileNumberInMap[column+lengthOfWaterBlock][row];
						}
						if (fillWaterBlocksArray == true) {
							waterBlocks.add(new Rectangle(x,y+(gameScreen.tileSize/2),gameScreen.tileSize*lengthOfWaterBlock,gameScreen.tileSize - (gameScreen.tileSize/4)));
						}
					}
				}

				column++; //increment column so that the next tile is drawn on the next spot in the column 
				x = gameScreen.tileSize + x; //set the x value where the next tile should be placed to x plus the tile size which is 48, so that it will be placed next to the tile that was drawn before 
				if (column == gameScreen.maxColumnSize) {
					row++; //this if statement will run if a tile has been drawn on every spot in the column because the column number equals the maximum number of tiles a column in the screen can hold
					y = gameScreen.tileSize + y; //start drawing from the next row, so the first time this will be 48+0 so it will draw in the second row, then the next time it will be 48+48 so it will paint from the third row, and so on
					column = 0; //reset the column value to 0 because we are now checking the first tile spot in the new row
					x = 0; //set x to 0, because we now want to start drawing from the start again, so every spot in the column is filled
				}
			}
			if (gameScreen.level == 1) {
				g2.drawImage(t[7].image,gameScreen.tileSize*14,gameScreen.tileSize*3,gameScreen.tileSize,gameScreen.tileSize,null);
			}
		}
	}
}
