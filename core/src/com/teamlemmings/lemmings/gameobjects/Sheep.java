package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.screens.GameScreen;

public class Sheep extends GameObject {
	private int direction = 1;
	private float maxSpeed = 10f;
	private float speedIncrease = 2f;
	private boolean canChangeDir = false;
	
	public Sheep(GameScreen screen, float x, float y) {
		// Setup the game object
		super(screen, x, y);
	}
	
	@Override
	public void update(float deltaTime) {
		Vector2 vel = this.body.getLinearVelocity();
		Vector2 pos = this.body.getPosition();

		float xSpeed = Math.abs(vel.x);
		
		// Ensure the sheep is on the ground and not moving too fast
		if (xSpeed < maxSpeed && vel.y >= 0) {          
		     this.body.applyLinearImpulse(speedIncrease*direction, 0, pos.x, pos.y, true);
		     
		     // See if the sheep needs to change directions
		     if(canChangeDir) {
		    	 if(xSpeed < 0.5f) {
			    	 direction *= -1;
			    	 canChangeDir = false;
			     }
		     } else {
		    	 if(xSpeed > 2f) {
		    		 canChangeDir = true;
		    	 }
		     }
		}
	}
	
	@Override
	protected void createFixture() {
		// Create a circle
		CircleShape circle = new CircleShape();
		circle.setRadius(2f);

		// Create a fixture definition to apply our shape to it
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.0f;
		
		// Default to world collisions
		fixtureDef.filter.categoryBits = Constants.CATEGORY_SHEEP;
		fixtureDef.filter.maskBits = Constants.MASK_SHEEP;

		// Create our fixture and attach it to the body
		body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();
	}
}
