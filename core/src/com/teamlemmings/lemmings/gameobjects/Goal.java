package com.teamlemmings.lemmings.gameobjects;

import com.teamlemmings.lemmings.screens.GameScreen;

public class Goal extends GameObject {
	/**
	 * Creates a new goal for the sheep
	 * @param screen The screen to attach to
	 * @param x The x coordinate of the goal
	 * @param y The y coordinate of the goal
	 */
	public Goal(GameScreen screen, float x, float y) {
		super(screen, x, y);
	}
	
	@Override
	public void onCollide(GameObject obj) {
		if(obj instanceof Sheep) {
			// Cleanup the sheep
			obj.cleanup();
			
			// Tell the user one got home
			System.out.println("A sheep got home!");
		}
	}
}
