package com.teamlemmings.lemmings;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.teamlemmings.lemmings.screens.GameScreen;

public class Lemmings extends Game {
	public static final boolean isRelease = false;
	
	@Override
	public void create () {
		setScreen(new GameScreen(this));
	}
	
	public void setLogLevel() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		if(isRelease) {
			Gdx.app.setLogLevel(Application.LOG_NONE);
		}	
	}
}
