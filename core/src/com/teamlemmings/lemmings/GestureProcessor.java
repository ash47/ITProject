package com.teamlemmings.lemmings;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.teamlemmings.lemmings.screens.GameScreen;

public class GestureProcessor implements GestureListener {
	// The game screen we are attached to
	private GameScreen screen;
	
	public GestureProcessor(GameScreen screen) {
		// Store the screen
		this.screen = screen;
	}
	
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		System.out.println("touchDown");
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// Pass the event to our game screen
		screen.onTap(x, y, count, button);
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		System.out.println("Long Press");
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}
