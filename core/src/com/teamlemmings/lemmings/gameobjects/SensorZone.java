package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.screens.GameScreen;

/**
 * This class represents a sensor zone
 * @author aschmid
 *
 */
public class SensorZone extends GameObject {
	// Width of this wall (meters)
	private float width;
	
	// Height of this wall (meters)
	private float height;
	
	/**
	 * Create a new wall of the given size
	 * @param screen The screen to attach to
	 * @param x The x position of the screen
	 * @param y The y position of the screen
	 * @param w The width of the wall
	 * @param h The height of the wall
	 */
	public SensorZone(GameScreen screen, float x, float y, float w, float h) {
		// Setup the game object
		super(screen, x, y);
		
		// Store vars
		this.width = w;
		this.height = h;
		
		// Create the fixture
		createFixture();
	}
	
	@Override
	protected void createFixture() {
		// Ensure vars are actually set, if not, don't setup physics
		if(this.width <= 0 || this.height <= 0) return;
		
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(this.width, this.height);
		
		// Create a fixture definition to apply our shape to it
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundBox;
		fixtureDef.isSensor = true;
		
		// Default to world collisions
		fixtureDef.filter.categoryBits = Constants.CATEGORY_SENSOR;
		fixtureDef.filter.maskBits = Constants.MASK_SENSOR;

		// Create our fixture and attach it to the body
		body.createFixture(fixtureDef);
		
		// Clean up after ourselves
		groundBox.dispose();
	}
	
	@Override
	protected BodyType getBodyType() {
		return BodyType.DynamicBody;
	}
}
