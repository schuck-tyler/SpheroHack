package com.orbotix.sample.multiplayerlobby;
import java.*;
import android.content.*;
import android.content.Context.*;
import android.hardware.*;
public class Player {
	private String Name;
	private int color_r;
	private int color_g;
	private int color_b;
	private double endurence;
	private static final double max_endurence = 1.0;
	private boolean host;
	private boolean reversed;
	private int number;
	private Vector2 position; // In centimeters from center of square
	private Vector2 Force;
	public int score;
	public Player(String name, int num, int width, int height){
		score = 30;
		this.Name = name;
		color_r = (num == 0 ? 255 : 0);
		color_g = (num == 1 ? 255 : 0);
		color_b = (num == 2 ? 255 : 0);
		switch (num) {
		case 0:
			position = new Vector2(width * 0.5, height);
			break;
		case 1:
			position = new Vector2(width * (3.5 / 16.0), height * 0.5);
			break;
		case 2:
			position = new Vector2(width * 0.5, 0);
			break;
		case 3:
			position = new Vector2(width * (12.5 / 16.0), height * 0.5);
			color_r = color_g = color_b = 255;
			break;
		}
		
		endurence = max_endurence;
		host = num == 0;
		reversed = false;
		number = num;
		Force = new Vector2(0,0);
	}
	
	public Vector2 getPosition(){
		return position; // Who needs privacy.
	}
	public int[] getColor() {
		int[] color = { color_r, color_g, color_b };
		return color;
	}
	public void setColor(int[] newColor) {
		if(newColor.length >= 3) {
			color_r = newColor[0];
			color_g = newColor[1];
			color_b = newColor[2];
		}
	}
	public boolean decrementScore() {
		score--;
		return score == 0;
	}
	public String getName() {
		return Name;
	}
	public boolean isReversed() {
		return reversed;
	}
	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}
}
