package com.teamlemmings.lemmings.gameobjects;

import com.teamlemmings.lemmings.screens.GameScreen;

/**
 * This class represents any objects that the user interacts with using gestures
 * @author aschmid
 *
 */
public class InteractiveObject extends GameObject {
	/**
	 * Creates a new interactive object at the given position
	 * @param screen The screen to attach to
	 * @param x The x coordinate of this object
	 * @param y The y coordinate of this object
	 */
	public InteractiveObject(GameScreen screen, float x, float y) {
		super(screen, x, y);
	}
}
