package com.teamlemmings.lemmings;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.teamlemmings.lemmings.screens.MenuScreen;

/**
 * This is the common entry point for our game on all platforms
 * @author aschmid
 *
 */
public class Lemmings extends Game {
	// Whether the game is being compiled for release, it will turn off logging if it's on
	public static final boolean isRelease = false;
	
	@Override
	public void create () {
		// Change to the game screen
		setScreen(new MenuScreen(this));
	}
	
	/**
	 * Sets the log level based on release mode
	 */
	public void setLogLevel() {
		// Set to the appropriate logging level
		if(isRelease) {
			Gdx.app.setLogLevel(Application.LOG_NONE);
		} else {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}
	}
}
