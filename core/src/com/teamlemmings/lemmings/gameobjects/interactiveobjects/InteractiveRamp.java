package com.teamlemmings.lemmings.gameobjects.interactiveobjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.gameobjects.InteractiveObject;
import com.teamlemmings.lemmings.screens.GameScreen;

public class InteractiveRamp extends InteractiveObject {
	// The current ramp state
	private int rampState = 0;
	
	// If we need to update the rotation
	private boolean needsUpdate = false;
	
	/**
	 * Creates a ramp that can be interacted with
	 * @param screen The screen to attach to
	 * @param x The x coordinate of this object
	 * @param y The y coordinate of this object
	 */
	public InteractiveRamp(GameScreen screen, float x, float y) {
		super(screen, x, y);
		
		// Set the initial rotation
		setRotation(0);
	}
	
	/**
	 * Sets the angle of the ramp
	 * @param angle The new angle
	 */
	private void setRotation(float angle) {
		// Grab our position
		Vector2 pos = this.body.getPosition();
		
		// Update our transformation
		this.body.setTransform(pos.x, pos.y, angle);
	}
	
	@Override
	protected void createFixture() {
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(4f, 0.5f);
		
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
	
	@Override
	public void onTouched() {
		System.out.println("hello");
		
		// Check which ramp state we are in
		if(rampState == 0) {
			// Change to angled
			rampState = 1;
		} else {
			// Change to flat
			rampState = 0;
		}
		
		// We need to update our rotation
		needsUpdate = true;
	}
	
	@Override
	public void render(float deltaTime, Batch batch) {
		// Check if we need an update
		if(needsUpdate) {
			// We don't anymore
			needsUpdate = false;
			
			// Check which rotation to set
			if(rampState == 0) {
				setRotation(0);
			} else {
				setRotation(0.17f);
			}
		}
	}
}
