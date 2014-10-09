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
	
	// The width of the ramp
	private float width;
	
	// The height of the ramp
	private float height;
	
	// The x origin of the ramp
	private float originX;
	
	// The y origin of the ramp
	private float originY;
	
	// The starting angle of the ramp
	private float initialAngle;
	
	// The final angle of the ramp
	private float finalAngle;
	
	// Should we rotate clockwise or not?
	private boolean clockwise;
	
	/**
	 * Creates a ramp that can be interacted with
	 * @param screen The screen to attach to
	 * @param x The x coordinate of this object
	 * @param y The y coordinate of this object
	 * @param width The width of the ramp
	 * @param height The height of the ramp
	 * @param The x origin to rotate about
	 * @param The y origin to rotate about
	 * @param initialAngle The initial rotate to start at (in radians)
	 * @param finalAngle The final angle to finish at
	 * @param clockwise Should we rotate clockwise or not?
	 */
	public InteractiveRamp(GameScreen screen, float x, float y, float width, float height, float originX, float originY, float initialAngle, float finalAngle, boolean clockwise) {
		super(screen, x, y);
		
		// Store the vars
		this.width = width;
		this.height = height;
		this.originX = originX;
		this.originY = originY;
		this.initialAngle = initialAngle;
		this.finalAngle = finalAngle;
		this.clockwise = clockwise;
		
		// Set the initial rotation
		setRotation(this.initialAngle);
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
		// Ensure we have been given a size
		if(this.width <= 0 || this.height <= 0) return;
		
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(4f, 0.5f, new Vector2(-4f, 0), 0);
		
		// Create a fixture definition to apply our shape to it
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundBox;
		
		// Allow sheep to easily walk up
		fixtureDef.friction = 1;
		
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
				setRotation(0.5f);
			}
		}
	}
}
