package com.orbotix.sample.multiplayerlobby;
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
	
	public Powerup(int width, int height, Player owner) {
		Random randGenerator = new Random();
		int type = randGenerator.nextInt(2);
		position = new Vector2(randGenerator.nextInt((int)(width * 0.8 * (9.0/16.0))) + width * (3.5 / 16.0), 
				randGenerator.nextInt((int)(height * 0.6)) + height * 0.2);
		switch (type) {
		case 0:
			kindOfPower = Type.wind;
			duration = (long)4000000000.1; // 4 billion nanoseconds, 4 seconds
			break;
		case 1:
			kindOfPower = Type.reverse;
			duration = (long)6000000000.1; // 7 billion nanoseconds, 7 seconds
			break;
		}
		active = false;
		this.owner = owner;
	}
	
	public Vector2 update(Vector2 curPosition, Player[] players) { // curPosition needs to be in pixels from top left
		if(position.distance(curPosition) < 10) {
			active = true;
			startTime = System.nanoTime();
			if(kindOfPower == Type.reverse)
				for(int i = 0; i < players.length; i++) {
					if (owner != players[i]) 
						players[i].setReversed(!players[i].isReversed());
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
		
		if(active) {
			if (kindOfPower == Type.wind) {
				Vector2 windDirection = curPosition.subtract(owner.getPosition());
				double distance = windDirection.length();
				return windDirection.normalize().multiply(60/distance);
			}
		}
		return new Vector2();
	}
}
