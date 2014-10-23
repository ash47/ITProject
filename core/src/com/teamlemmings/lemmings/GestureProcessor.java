package com.teamlemmings.lemmings;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.teamlemmings.lemmings.screens.GameScreen;

/**
 * This class handles the gesture controls
 * @author aschmid
 *
 */
public class GestureProcessor implements GestureListener {
	// The game screen we are attached to
	private GameScreen screen;
	
	/**
	 * Creates a new gesture proessor for the given screen
	 * @param screen The game screen we report events back to
	 */
	public GestureProcessor(GameScreen screen) {
		// Store the screen
		this.screen = screen;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
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
		// Pass the event to the screen
		screen.onLongPress(x, y);
		
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
