package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.screens.GameScreen;

public class TouchWall extends GameObject {
	private GameScreen screen;
	
	public TouchWall(GameScreen screen) {
		// Setup the game object
		super(screen, -10000, -10000);
		
		// Store the screen
		this.screen = screen;
	}
	
	@Override
	public void update(float deltaTime) {
		// Check if user is touching something
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			// Move into position
			this.body.setTransform(screen.screenToWorldX(Gdx.input.getX()), screen.screenToWorldY(Gdx.input.getY()), 0.0f);
		} else {
			this.body.setTransform(-10000, -10000, 0.0f);
		}
	}
	
	@Override
	protected void createFixture() {
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(10f, 10f);
		
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
