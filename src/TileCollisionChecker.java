
public class TileCollisionChecker extends Character {

	Screen gamePanel;

	//public TileCollisionChecker(Screen gamePanel ) {// constructor
	//	this.gamePanel = gamePanel; // get screen info
	//}

	public TileCollisionChecker() {
		// TODO Auto-generated constructor stub
	}

	//Methods
	public void Tile_Chara(Player user){ //Overload for player

		//finding/creating the edges/sides of the collision box and creating separate sensors for each
		//TALL RECTANGLE (1) for up and down collisions
		int leftCollision1 = (user.x + 2)/(gamePanel.tileSize); //(u+2,0) 
		//System.out.println(cBoxLeft1);
		int rightCollision1 = (user.x + gamePanel.tileSize - 2)/(gamePanel.tileSize); //(u+46,0)
		//System.out.println(cBoxRight1);
		int topCollision1 = (user.y - (gamePanel.tileSize)*5/6)/(gamePanel.tileSize);  //(0,u-16)due to flappy jump
		//System.out.println(cBoxTop1);
		int bottomCollision1 = (user.y + gamePanel.tileSize + 1)/(gamePanel.tileSize); //(0,u+49)
		//System.out.println(cBoxbottom1);

		//Checking for collision to a blacklisted tile in the specific side. Uses tile size format to check
		//System.out.println(leftCollision1);
		//System.out.println(rightCollision1);
		//System.out.println(topCollision1);
		//System.out.println(bottomCollision1);

		//WIDE RECTANGLE (2) for left and right collisions			
		int leftCollision2 = (user.x - 2)/(gamePanel.tileSize); //(u-2,0) 
		//System.out.println(cBoxLeft1);
		int rightCollision2 = (user.x + gamePanel.tileSize + 2)/(gamePanel.tileSize); //(u+50,0)
		//System.out.println(cBoxRight1);
		int topCollision2 = (user.y + 1)/(gamePanel.tileSize);  //(0,u+1)
		//System.out.println(cBoxTop1);
		int midCollision2 = (user.y + (gamePanel.tileSize/2))/(gamePanel.tileSize) ; // (0, u+24)

		int bottomCollision2 = (user.y + gamePanel.tileSize - 1) /(gamePanel.tileSize); //(0,u+47)
		//System.out.println(cBoxbottom1);

		//Checking for collision to a blacklisted tile in the specific side. Uses tile size format to check
		//System.out.println(leftCollision2);
		//System.out.println(rightCollision2);
		//System.out.println(topCollision2);
		//System.out.println(bottomCollision2);

		//check collision in the direction between 2 possible tiles player can overlap/collide with at a time
		int tile1 = 0, tile2 = 0, tileMid = 0, tileMid2 = 0; // hold tile value for comparison


		///CHECK PLAYER FUTURE MOVEMENT

		//collision below default (due to gravity) check 
		//check bottom left
		try {
			tile1 = ManageTiles.tileNumberInMap[leftCollision1][bottomCollision1];
			//System.out.println(tile1 +"tile1" + " collision: " + ManageTiles.t[tile1].collision);
			//check bottom right
			try {
				tileMid = ManageTiles.tileNumberInMap[leftCollision2][midCollision2];

				tileMid2 = ManageTiles.tileNumberInMap[rightCollision2][midCollision2];

				tile2 = ManageTiles.tileNumberInMap[rightCollision1][bottomCollision1];
			} catch (Exception e) {
				user.gravity = user.y+(user.speed/2);
			}

		} catch(Exception e) {// if tile sensor array is out of bound downward continue movement with no issue
			user.y = user.y - (user.speed/2);//move player up back into screen
		}
		//PLATFORM and TILE COLLISION
		if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ((ManageTiles.t[tile1].platform == true && ManageTiles.t[tileMid].platform == true) || (ManageTiles.t[tile2].platform == true && ManageTiles.t[tileMid2].platform == true) )) { //check if tile collide = true
			user.gravity = user.y + 0; //stop gravity

		} else {user.gravity = user.y+(user.speed/2);} //create gravity




		//Check if there is a blacklisted tile above	
		if (user.kL.spacePressed == true) {
			try {
				//check top left
				tile1 = ManageTiles.tileNumberInMap[leftCollision1][topCollision1];
				//System.out.println(tile1 +"tile1" + " collision: " + ManageTiles.t[tile1].collision);

				try {
					//check top right
					tile2 = ManageTiles.tileNumberInMap[rightCollision1][topCollision1];
					//System.out.println(tile2 +"tile2" + " collision: " + ManageTiles.t[tile2].collision);
				}catch(Exception e) {
					user.moveUp = true;
				}

			}catch(Exception e) {// if tile sensor array is out of bound upward, do NOT allow movement
				//System.out.println("out of bounds");
				user.y = user.y + Screen.tileSize;
				user.moveUp = false;
			}

			//PLATFORM and TILE COLLISION (top)
			if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ManageTiles.t[tile1].platform == true || ManageTiles.t[tile2].platform == true) { //check if tile collide = true
				user.moveUp = false;
			} else {user.moveUp = true;}; //allow jump


		}


		if (user.kL.rightPressed) {
			try {
				//check top right
				tile1 = ManageTiles.tileNumberInMap[rightCollision2][topCollision2];
				//System.out.println(tile1 +"tile1" + " collision: " + ManageTiles.t[tile1].collision);
				//check bottom right
				//System.out.println(tile1 + "tile1" + " platform: " + ManageTiles.t[tile1].platform);
				tileMid = ManageTiles.tileNumberInMap [rightCollision2][midCollision2];
				//System.out.println(tileMid + "tileMid" + " platform: " + ManageTiles.t[tileMid].platform);

				tile2 = ManageTiles.tileNumberInMap[rightCollision2][bottomCollision2];
				//System.out.println(tile2 +"tile2" + " collision: " + ManageTiles.t[tile2].collision);

				//check for full block
				if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true) { //check if tile collide = true
					user.moveRight = false;
				} else {user.moveRight = true;}//allow right movement


				///FOR PLATFORM MOVEMENT
				//Mid collision Only, player can NOT Move
				if (ManageTiles.t[tileMid].platform == true) {
					user.moveRight = false;
					if (user.gravity == user.y + 0) {
						user.moveRight = true;
					}
				}
				//Top half collision of the player can NOT move (mid and top sensors)
				if ((ManageTiles.t[tile1].platform == true || ManageTiles.t[tileMid].platform == true) && !( ManageTiles.t[tile2].platform == true)) {
					user.moveRight = false;
				}
			}catch(Exception e) {// if tile sensor array is out of bound, allow movement for right teleporter
				user.moveRight = true;
			}
		}


		if (user.kL.leftPressed) {
			try {
				//check top left
				tile1 = ManageTiles.tileNumberInMap[leftCollision2][topCollision2];
				//System.out.println(tile1 +"tile1" + " collision: " + ManageTiles.t[tile1].collision);

				tileMid = ManageTiles.tileNumberInMap [leftCollision2][midCollision2];
				//System.out.println(tileMid + "tileMid" + " platform: " + ManageTiles.t[tileMid].platform);

				//check bottom left
				tile2 = ManageTiles.tileNumberInMap[leftCollision2][bottomCollision2];
				//System.out.println(tile2 +"tile2" + " collision: " + ManageTiles.t[tile2].collision);

				if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true) { //check if tile collide = true
					user.moveLeft = false;
				} else {user.moveLeft = true;}//allow left movement

				///FOR PLATFORM MOVEMENT
				//Mid collision Only, player can NOT Move
				if (ManageTiles.t[tileMid].platform == true) {
					user.moveLeft= false;
					if (user.gravity == user.y + 0) {
						user.moveLeft = true;
					}
				}
				//Top half collision of the player can NOT move (mid and top sensors)
				if ((ManageTiles.t[tile1].platform == true || ManageTiles.t[tileMid].platform == true) && !( ManageTiles.t[tile2].platform == true)) {
					user.moveLeft = false;
				}
			}catch(Exception e) {// if tile sensor array is out of bound, allow movement for left teleporter
				user.moveLeft = true;
			}
		}
	}

	public void Tile_Chara(EnemyOne mon){
		//finding/creating the edges/sides of the collision box and creating separate sensors for each
		//TALL RECTANGLE (1) for up and down collisions
		int leftCollision1 = (mon.x + 1)/(gamePanel.tileSize); //(u+2,0) 
		int rightCollision1 = (mon.x + gamePanel.tileSize - 1)/(gamePanel.tileSize); //(u+46,0)
		int topCollision1 = (mon.y - 1)/(gamePanel.tileSize);  //(0,u-16)due to flappy jump
		int bottomCollision1 = (mon.y + gamePanel.tileSize + 1)/(gamePanel.tileSize); //(0,u+49)

		//WIDE RECTANGLE (2) for left and right collisions			
		int leftCollision2 = (mon.x - 1)/(gamePanel.tileSize); //(u-2,0) 
		int rightCollision2 = (mon.x + gamePanel.tileSize + 1)/(gamePanel.tileSize); //(u+50,0)
		int topCollision2 = (mon.y + 1)/(gamePanel.tileSize);  //(0,u+1)
		int midCollision2 = (mon.y + (gamePanel.tileSize/2))/(gamePanel.tileSize) ; // (0, u+24)
		int bottomCollision2 = (mon.y + gamePanel.tileSize - 1) /(gamePanel.tileSize); //(0,u+47)

		int tile1 = 0, tile2 = 0, tileMid = 0, tileMid2 = 0; // hold tile value for comparison


		//MOVEMENT PERMISSION
		if (mon.huntPlayer == false) {
			if (mon.moveRight == true) {
				try {
					//check top right
					tile1 = ManageTiles.tileNumberInMap[rightCollision2][topCollision2];
					//check mid right
					tileMid = ManageTiles.tileNumberInMap [rightCollision2][midCollision2];
					//check bottom right
					tile2 = ManageTiles.tileNumberInMap[rightCollision2][bottomCollision2];

					//FOR TILE MOVEMENT
					if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true) { //check if tile collide = true
						mon.moveRight = false;
					} else {mon.moveRight = true;}//allow right movement


					///FOR PLATFORM MOVEMENT
					//Mid collision Only, MON can NOT Move
					if (ManageTiles.t[tileMid].platform == true || ManageTiles.t[tile1].platform == true) {
						mon.moveRight = false;
					}
					//Top half collision of the player can NOT move (mid and top sensors)
					if ((ManageTiles.t[tile2].platform == true && !ManageTiles.t[tileMid].platform == true)) {
						mon.moveRight = true;
					}

				}catch(Exception e) {// if tile sensor array is out of bound, allow movement for right teleporter
					mon.moveRight = true;
				}
			}





			if (mon.moveLeft == true) {
				try {
					//check top right
					tile1 = ManageTiles.tileNumberInMap[leftCollision2][topCollision2];
					//check mid right
					tileMid = ManageTiles.tileNumberInMap [leftCollision2][midCollision2];
					//check bottom right
					tile2 = ManageTiles.tileNumberInMap[leftCollision2][bottomCollision2];

					//FOR TILE MOVEMENT
					if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true) { //check if tile collide = true
						mon.moveLeft = false;
					} else {mon.moveLeft = true;}//allow right movement

					///FOR PLATFORM MOVEMENT
					//Mid collision Only, MON can NOT Move
					if (ManageTiles.t[tileMid].platform == true) {
						mon.moveLeft = false;
						if (mon.moveDown == false) {
							mon.moveLeft = true;
						}
					}
					//Top half collision of the player can NOT move (mid and top sensors)
					if ((ManageTiles.t[tile1].platform == true || ManageTiles.t[tileMid].platform == true) && !( ManageTiles.t[tile2].platform == true)) {
						mon.moveLeft = false;
					}

				}catch(Exception e) {// if tile sensor array is out of bound, allow movement for right teleporter
					mon.moveLeft = true;
				}
			}

			if (mon.moveUp == true) {
				try {
					//check top left
					tile1 = ManageTiles.tileNumberInMap[leftCollision1][topCollision1];
					//check top right
					tile2 = ManageTiles.tileNumberInMap[rightCollision1][topCollision1];
				}catch(Exception e) {// if tile sensor array is out of bound upward, do NOT allow movement
					mon.moveUp = false;
					mon.y = mon.y + mon.speed;//move mon down back into screen
				}

				//PLATFORM and TILE COLLISION (top)
				if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ManageTiles.t[tile1].platform == true || ManageTiles.t[tile2].platform == true) { //check if tile collide = true
					mon.moveUp = false;
				} else {mon.moveUp = true;}; //allow up movement
			}

			if (mon.moveDown == true) {
				try {
					//check bottom left
					tile1 = ManageTiles.tileNumberInMap[leftCollision1][bottomCollision1];

					try {
						tileMid = ManageTiles.tileNumberInMap[leftCollision2][midCollision2];

						tileMid2 = ManageTiles.tileNumberInMap[rightCollision2][midCollision2];

						tile2 = ManageTiles.tileNumberInMap[rightCollision1][bottomCollision1];
					} catch (Exception e) {

					}

				}catch(Exception e) {// if tile sensor array is out of bound downward, do NOT allow movement
					mon.moveDown = false;
					mon.y = mon.y - mon.speed;//move mon up back into screen
				}

				//PLATFORM AND TILE COLLISION
				if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ((ManageTiles.t[tile1].platform == true && ManageTiles.t[tileMid].platform == true) || (ManageTiles.t[tile2].platform == true && ManageTiles.t[tileMid2].platform == true) )) { //check if tile collide = true
					mon.moveDown = false; //stop
				} else {mon.moveDown = true;} //create
			}
		} else {

			int sense1, sense2, sense3, sense4, sense5, sense6;



		}


	}



	public void tilePlayer_Monster(Player user, Character c1){ //OverLoad Method with enemyOne between player collision and if there is an obstruction
		//get PLAYER future push coordinates (left right)
		int leftCollision = (user.x - Screen.tileSize)/(Screen.tileSize); //(u-48,0) 
		int rightCollision = (user.x + Screen.tileSize*2)/(Screen.tileSize); //(u+96,0)
		int topCollision = (user.y)/(Screen.tileSize);  //(0,u)
		int midCollision =  (user.y + Screen.tileSize/2)/(Screen.tileSize);
		int bottomCollision = (user.y + Screen.tileSize) /(Screen.tileSize); //(0,u+48)

		//get MONSTER future push coordinates(left right)
		int leftCollision2 = (c1.x - Screen.tileSize)/(Screen.tileSize); //(u-48,0) 
		int rightCollision2 = (c1.x + Screen.tileSize*2)/(Screen.tileSize); //(u+96,0)
		int topCollision2 = (c1.y)/(Screen.tileSize);  //(0,u)
		int midCollision2 =  (c1.y + Screen.tileSize/2)/(Screen.tileSize);
		int bottomCollision2 = (c1.y + Screen.tileSize) /(Screen.tileSize); //(0,u+48)

		//tile holders
		int tile1 = 0, tile2 = 0, tile3 = 0;

		//If player collides with left side of Monster
		if (user.collisionBox.intersects(c1.leftCollisionBox)) {
			try {
				tile1 = ManageTiles.tileNumberInMap[leftCollision][topCollision];
				tile2 = ManageTiles.tileNumberInMap[leftCollision][midCollision];
				tile3 = ManageTiles.tileNumberInMap[leftCollision][bottomCollision];
			}catch(Exception e) {
				c1.playerPush = false; //push monster
			}

			if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ManageTiles.t[tile3].collision == true 
					|| ManageTiles.t[tile1].platform == true || ManageTiles.t[tile2].platform == true || ManageTiles.t[tile1].platform == true) {
				c1.playerPush = false; //push monster
			}else{c1.playerPush = true;} //push the player
		}
		//If player collides with right side of Monster
		else if (user.collisionBox.intersects(c1.rightCollisionBox)) { //has to be else if because when it was if you could go through the middle of the enemy because the +48 and minus+48 were cancelling out to no movement
			try {
				tile1 = ManageTiles.tileNumberInMap[rightCollision][topCollision];
				tile2 = ManageTiles.tileNumberInMap[rightCollision][midCollision];
				tile3 = ManageTiles.tileNumberInMap[rightCollision][bottomCollision];
			}catch(Exception e) {
				c1.playerPush = false; //push monster
			}

			if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ManageTiles.t[tile3].collision == true 
					|| ManageTiles.t[tile1].platform == true || ManageTiles.t[tile2].platform == true || ManageTiles.t[tile1].platform == true) {
				c1.playerPush = false; //push monster
			}else{c1.playerPush = true;} // push the player
		}

		//If Player collides with Montser's Balloons
		else if (user.collisionBox.intersects(c1.balloonLeftCollisionBox)) {
			try {
				tile1 = ManageTiles.tileNumberInMap[leftCollision2][topCollision2];
				tile2 = ManageTiles.tileNumberInMap[leftCollision2][midCollision2];
				tile3 = ManageTiles.tileNumberInMap[leftCollision2][bottomCollision2];
			}catch(Exception e) {
				c1.playerPush = true; //push 
			}

			if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ManageTiles.t[tile3].collision == true 
					|| ManageTiles.t[tile1].platform == true || ManageTiles.t[tile2].platform == true || ManageTiles.t[tile1].platform == true) {
				c1.playerPush = true; // push player
			} else{ c1.playerPush = false;} // push monster since its the one getting hurt
		}

		else if (user.collisionBox.intersects(c1.balloonRightCollisionBox)) {
			try {
				tile1 = ManageTiles.tileNumberInMap[rightCollision2][topCollision2];
				tile2 = ManageTiles.tileNumberInMap[rightCollision2][midCollision2];
				tile3 = ManageTiles.tileNumberInMap[rightCollision2][bottomCollision2];
			}catch(Exception e) {
				c1.playerPush = true; //push monster
			}

			if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ManageTiles.t[tile3].collision == true 
					|| ManageTiles.t[tile1].platform == true || ManageTiles.t[tile2].platform == true || ManageTiles.t[tile1].platform == true) {
				c1.playerPush = true; // push player
			} else{c1.playerPush = false;} // push monster since its the one getting hurt
		}

	}

	/*public void tilePlayer_Monster(Player user, EnemyThree Three){ //OverLoad Method with enemyThree between player collision and if there is an obstruction
		//get PLAYER future push coordinates
		int leftCollision = (user.x - Screen.tileSize)/(Screen.tileSize); //(u-48,0) 
		int rightCollision = (user.x + Screen.tileSize*2)/(Screen.tileSize); //(u+96,0)
		int topCollision = (user.y)/(Screen.tileSize);  //(0,u)
		int midCollision =  (user.y + Screen.tileSize/2)/(Screen.tileSize);
		int bottomCollision = (user.y + Screen.tileSize) /(Screen.tileSize); //(0,u+48)

		//get MONSTER future push coordinates
		int leftCollision2 = (Three.x - Screen.tileSize*2)/(Screen.tileSize); //(u-72,0) 
		int rightCollision2 = (Three.x + Screen.tileSize*3); //(u+120,0)

		int topCollision2 = (Three.y)/(Screen.tileSize);  //(0,u)
		int midCollision2 =  (Three.y + Screen.tileSize/2)/(Screen.tileSize);
		int bottomCollision2 = (Three.y + Screen.tileSize) /(Screen.tileSize); //(0,u+48)

		//tile holders
		int tile1 = 0, tile2 = 0, tile3 = 0;

		//If Player collides with Montser's Balloons
		if (user.collisionBox.intersects(Three.balloonLeftCollisionBox)) {
			try {
				tile1 = ManageTiles.tileNumberInMap[leftCollision2][topCollision2];
				System.out.println("tile1: " + tile1);
				tile2 = ManageTiles.tileNumberInMap[leftCollision2][midCollision2];
				System.out.println("tile2: " + tile2);
				tile3 = ManageTiles.tileNumberInMap[leftCollision2][bottomCollision2];
				System.out.println("tile3: " + tile3);
			}catch(Exception e) {
				Three.playerPush = true; //push player
			}

			if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ManageTiles.t[tile3].collision == true 
					|| ManageTiles.t[tile1].platform == true || ManageTiles.t[tile2].platform == true || ManageTiles.t[tile1].platform == true) {
				Three.playerPush = true; System.out.println(" !monster push false");// push player
			} else{ Three.playerPush = false;System.out.println(" !monster push true");} // push monster since its the one getting hurt
		}

		if (user.collisionBox.intersects(Three.balloonRightCollisionBox)) {
			try {
				tile1 = ManageTiles.tileNumberInMap[rightCollision2][topCollision2];
				System.out.println("tile1: " + tile1);
				tile2 = ManageTiles.tileNumberInMap[rightCollision2][midCollision2];
				System.out.println("tile2: " + tile2);
				tile3 = ManageTiles.tileNumberInMap[rightCollision2][bottomCollision2];
				System.out.println("tile3: " + tile3);
			}catch(Exception e) {
				Three.playerPush = true; //push player
			}

			if(ManageTiles.t[tile1].collision == true || ManageTiles.t[tile2].collision == true || ManageTiles.t[tile3].collision == true 
					|| ManageTiles.t[tile1].platform == true || ManageTiles.t[tile2].platform == true || ManageTiles.t[tile1].platform == true) {
				Three.playerPush = true; System.out.println(" !monster push false");// push player 
			} else{ Three.playerPush = false;System.out.println(" !monster push true");} // push monster since its the one getting hurt
		}
	}*/
}