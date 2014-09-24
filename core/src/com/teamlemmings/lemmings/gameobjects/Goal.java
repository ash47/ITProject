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

}
