package com.orbotix.sample.helloworld;
import java.util.*;

public class Powerup {
	private Type kindOfPower;
	private boolean active;
	private Vector2 position;
	private Player owner;
	private long startTime, duration;
	
	public enum Type {
		wind,
		reverse
	}
	
	public Powerup(int dimension, Player owner) {
		Random randGenerator = new Random();
		int type = randGenerator.nextInt(2);
		position = new Vector2(randGenerator.nextInt((int)(dimension * 0.85)), randGenerator.nextInt((int)(dimension * 0.85)));
		switch (type) {
		case 0:
			kindOfPower = Type.wind;
			duration = (long)4000000000.1; // 4 billion nanoseconds, 4 seconds
			break;
		case 1:
			kindOfPower = Type.reverse;
			duration = (long)7000000000.1; // 7 billion nanoseconds, 7 seconds
			break;
		}
		active = false;
		this.owner = owner;
	}
	
	public void update(Vector2 curPosition, Vector2 curDirection, Player[] players) {
		if(position.distance(curPosition) < 10) {
			active = true;
			startTime = System.nanoTime();
		}
		
		if(active) {
			if(kindOfPower == Type.reverse)
				for(int i = 0; i < players.length; i++) {
					if (owner != players[i]) 
						players[i].setReversed(!players[i].isReversed());
				}
			else if (kindOfPower == Type.wind) {
				Vector2 windDirection = curPosition.subtract(owner.getPosition());
				windDirection = windDirection.normalize().multiply(15);
				curDirection = curDirection.add(windDirection);
			}
		}
		
		long curTime = System.nanoTime();
		if(curTime - startTime > duration) {
			active = false;
			if (kindOfPower == Type.reverse)
				for(int i = 0; i < players.length; i++)
					if(owner != players[i])
						players[i].setReversed(!players[i].isReversed());
		}
	}
}
