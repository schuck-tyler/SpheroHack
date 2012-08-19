package com.orbotix.sample.helloworld;
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
	private Vector2 position;
	private Vector2 Force;
	public Player(String name, int num){
		this.Name = name;
		color_r = (num == 0 ? 255 : 0);
		color_g = (num == 1 ? 255 : 0);
		color_b = (num == 2 ? 255 : 0);
		if(num == 4)
			color_r = color_g = color_b = 255;
		endurence = max_endurence;
		host = num == 0;
		reversed = false;
		number = num;
		Force = new Vector2(0,0);
	}
	public void update(){
		// Not quite sure what the update is supposed to do		
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
