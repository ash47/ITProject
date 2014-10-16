package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.Renderer;
import com.teamlemmings.lemmings.screens.GameScreen;

/**
 * A sheep that walks around
 * @author aschmid
 *
 */
public class Sheep extends GameObject {
	// The current direction the sheep is walking, 1 = right, -1 = left
	private int direction = 1;
	
	// The max speed the sheep can move at (meters per second)
	private float maxSpeed = 1f;
	
	// The lowest speed a sheep can be going before it will try to turn around (meters per second)
	private float minSpeed = 0.1f;
	
	// How fast the sheep gains acceleration (meters per second)
	private float speedIncrease = 1f;
	
	// How long to wait before we can turn around at a wall (seconds)
	private float waitTime = 0.1f;
	
	// How long this sheep has been waiting (seconds)
	private float timeWaited = 0;
	
	// Scale of the sheep (meters)
	private static final float scale = 1f;
	
	// Scale of the sprite
	private static final float spriteScale = scale/256f;
	
    /**
     * Create a new sheep
     * @param screen The screen to attach this sheep to
     * @param x The x position to place the sheep
     * @param y The y position to place the sheep
     */
	public Sheep(GameScreen screen, float x, float y) {
		// Setup the game object
		super(screen, x, y);
	}
	
	@Override
	public void render(float deltaTime, Renderer renderer) {
		// Grab our position and velocity
		Vector2 vel = this.body.getLinearVelocity();
		Vector2 pos = this.body.getPosition();
		
		// Render the sprite
		if(direction == 1) {
			renderer.renderSprite("sheepRight", pos.x, pos.y, spriteScale);
		} else {
			renderer.renderSprite("sheepLeft", pos.x, pos.y, spriteScale);
		}
		
		// Make it walk

		float xSpeed = Math.abs(vel.x);
		
		// Ensure the sheep is on the ground and not moving too fast
		if (xSpeed < maxSpeed && vel.y >= 0) {          
		     this.body.applyLinearImpulse(speedIncrease*direction, 0, pos.x, pos.y, true);
		     
		     // See if the sheep needs to change directions
		     if(timeWaited > waitTime) {
		    	 if(xSpeed < minSpeed) {
			    	 direction *= -1;
			    	 timeWaited = 0;
			     }
		     } else {
		    	 // Allow the changing of direction
		    	 timeWaited += deltaTime;
		     }
		}
	}
	
	@Override
	protected void createFixture() {
		// Create a circle
		CircleShape circle = new CircleShape();
		circle.setRadius(scale/2);

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
	
	@Override
	public void onCollide(GameObject obj) {
		if(obj instanceof Goal) {
			// Cleanup the sheep
			this.cleanup();
			
			// Tell the user one got home
			System.out.println("A sheep got home!");
		} else if(obj instanceof Coin) {
			// Cleanup coin
			obj.cleanup();
			
			// Add to our score
			System.out.println("Got a coin!");
		}
	}
}
