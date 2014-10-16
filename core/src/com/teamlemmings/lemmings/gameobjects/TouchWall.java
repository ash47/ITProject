package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.Renderer;
import com.teamlemmings.lemmings.screens.GameScreen;

/**
 * This class represents a wall that is created when the user touches the screen
 * @author aschmid
 *
 */
public class TouchWall extends GameObject {
	// The screen this TouchWall is attached to
	private GameScreen screen;
	
	/**
	 * Create a new touch screen
	 * @param screen The screen to attach to
	 */
	public TouchWall(GameScreen screen) {
		// Setup the game object
		super(screen, -10000, -10000);
		
		// Store the screen
		this.screen = screen;
	}
	
	@Override
	public void render(float deltaTime, Renderer renderer) {
		// Check if user is touching something
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			// Move into position
			this.body.setTransform(screen.screenToWorldX(Gdx.input.getX()), screen.screenToWorldY(Gdx.input.getY()), 0.0f);
		} else {
			// Remove the wall
			this.body.setTransform(-10000, -10000, 0.0f);
		}
	}
	
	@Override
	protected void createFixture() {
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(2f, 2f);
		
		// Create a fixture definition to apply our shape to it
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundBox;
		
		// Default to world collisions
		fixtureDef.filter.categoryBits = Constants.CATEGORY_WORLD;
		fixtureDef.filter.maskBits = Constants.MASK_WORLD;

		// Create our fixture and attach it to the body
		body.createFixture(fixtureDef);
		
		// Clean up after ourselves
		groundBox.dispose();
	}
	
	@Override
	protected BodyType getBodyType() {
		return BodyType.KinematicBody;
	}
}
