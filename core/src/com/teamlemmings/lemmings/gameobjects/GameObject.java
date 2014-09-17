package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.teamlemmings.lemmings.screens.GameScreen;
import com.teamlemmings.lemmings.Constants;

public class GameObject {
	// The body for this GameObject
	protected Body body;
	
	/**
	 * 
	 */
	public GameObject(GameScreen screen, float x, float y) {
		// Create and position the body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = this.getBodyType();
		bodyDef.position.set(x, y);
		
		// Add the body into the world
		body = screen.getWorld().createBody(bodyDef);
		
		// Store reference from body to GameObject
		body.setUserData(this);
		
		// Create the fixture
		this.createFixture();
		
		// Add this object into the screen
		screen.addObject(this);
	}
	
	/**
	 * Called to update this GameObject
	 * @param deltaTime Time since the last call
	 */
	public void render(float deltaTime) {};
	
	/**
	 * Creates the physics fixture for this GameObject
	 */
	protected void createFixture() {
		// Create a circle
		CircleShape circle = new CircleShape();
		circle.setRadius(6f);

		// Create a fixture definition to apply our shape to it
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit
		
		// Default to world collisions
		fixtureDef.filter.categoryBits = Constants.CATEGORY_WORLD;
		fixtureDef.filter.maskBits = Constants.MASK_WORLD;

		// Create our fixture and attach it to the body
		body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();
	}
	
	/**
	 * Returns the type of body this game object is
	 * @return
	 */
	protected BodyType getBodyType() {
		return BodyType.DynamicBody;
	}
}
