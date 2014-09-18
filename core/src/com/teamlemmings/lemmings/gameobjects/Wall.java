package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.screens.GameScreen;

public class Wall extends GameObject {
	// Width of this wall
	private float width;
	
	// Height of this wall
	private float height;
	
	public Wall(GameScreen screen, float x, float y, float w, float h) {
		// Setup the game object
		super(screen, x, y);
		
		// Store vars
		this.width = w;
		this.height = h;
		
		// Create the fixture
		createFixture();
	}
	
	@Override
	public void render(float deltaTime, Batch batch) {}
	
	@Override
	protected void createFixture() {
		// Ensure vars are actually set
		if(this.width <= 0 || this.height <= 0) return;
		
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(this.width, this.height);
		
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
		return BodyType.StaticBody;
	}
}
