package com.teamlemmings.lemmings.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * Represents a screen in the game
 * @author aschmid
 *
 */
public abstract class LemmingScreen implements Screen {
	// The game this screen relates to
	protected Game game;

	/**
	 * Creates a new screen
	 * @param game The game this screen is attached to
	 */
	public LemmingScreen (Game game) {
		// Store the reference to the game
		this.game = game;
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void show () {
	}

	@Override
	public void hide () {
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}
	
	/**
	 * Gets the game this screen is attached to
	 * @return The game this screen is related to
	 */
	public Game getGame() {
		return this.game;
	}
}
