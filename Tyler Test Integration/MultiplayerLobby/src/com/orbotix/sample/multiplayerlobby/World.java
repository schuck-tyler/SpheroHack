package com.orbotix.sample.multiplayerlobby;

import android.media.SoundPool;

public class World {
	Player[] players;
	Powerup[] powers;
	Vector2 curBallPos;
	Vector2 powerVec;
	int curUpdateIndex;
	long curTime;
	int secondsPassed;
	boolean gameOver;
	int screenWidth;
	int screenHeight;
	public World(String[] names, int height, int width) {
		curUpdateIndex = 0;
		screenWidth = width;
		screenHeight = height;
		curBallPos = new Vector2(0,0);
		players = new Player[4];
		powers = new Powerup[4];
		powerVec = new Vector2(0,0);
		for(int i = 0; i < players.length; i++) {
			players[i] = new Player(names[i], i, width, height);
			powers[i] = new Powerup(width, height, players[i]);
		}
		secondsPassed = 1;
		gameOver = false;
	}
	public void update(Vector2 curBallPos) {
		powerVec = new Vector2();
		for(int i = 0; i < powers.length; i++){
			powerVec.add(powers[i].update(curBallPos, players));
			if(players[i].score == 0){
				gameOver = true;
			}
		}
		curTime = System.nanoTime();
		if((int)(curTime / 1000000000) == secondsPassed) {
			if(curBallPos.getY() > 0 && Math.abs(curBallPos.getX()) < Math.abs(curBallPos.getY()))
				gameOver = players[0].decrementScore();
			else if (curBallPos.getX() < 0 && Math.abs(curBallPos.getY()) < Math.abs(curBallPos.getY()))
				gameOver = players[1].decrementScore();
			else if (curBallPos.getY() < 0 && Math.abs(curBallPos.getX()) < Math.abs(curBallPos.getY()))
				gameOver = players[2].decrementScore();
			else if (curBallPos.getX() > 0 && Math.abs(curBallPos.getY()) < Math.abs(curBallPos.getX()))
				gameOver = players[3].decrementScore();
			if(++secondsPassed % 10 == 0) {
				for(int i = 0; i < powers.length; i++)
					powers[i] = new Powerup(screenWidth, screenHeight, players[i]);
			}
		}
	}
	public boolean isGameOver() {
		return gameOver;
	}
}
