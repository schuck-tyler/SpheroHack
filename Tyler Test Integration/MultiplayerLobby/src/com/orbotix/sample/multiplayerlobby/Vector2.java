package com.orbotix.sample.multiplayerlobby;

public class Vector2 {
	double x, y;
	public Vector2() {
		x = y = 0;
	}
	
	public Vector2(double x, double y) {
		 this.x = x;
		 this.y = y;
	}
	
	public Vector2 getVector() {
		 return new Vector2(x, y);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Vector2 add(Vector2 toAdd) {
		return new Vector2(x + toAdd.getX(), y + toAdd.getY());
	}
	
	public Vector2 subtract(Vector2 toSubtract) {
		return new Vector2(x - toSubtract.getX(), y - toSubtract.getY());
	}
	
	public Vector2 multiply(double multiplier) {
		return new Vector2(x*multiplier, y*multiplier);
	}
	
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	public double distance(Vector2 otherVector) {
		return (new Vector2(otherVector.getX() - x, otherVector.getY() - y)).length();
	}
	
	public Vector2 normalize() {
		return new Vector2(x/length(), y/length());
	}
}
