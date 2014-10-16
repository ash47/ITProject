package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.teamlemmings.lemmings.screens.GameScreen;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.Renderer;

/**
 * This is the class all objects used in the game are derived from
 * @author aschmid
 *
 */
public class GameObject {
	// The physics body for this GameObject
	protected Body body;
	
	// Used to track whether this object should be deleted or not
	private Boolean shouldDelete = false;
	
	/**
	 * Creates a new game object based on the given screen and position
	 * @param screen The screen this object should be stored into
	 * @param x The x position on screen to put this object
	 * @param y The y position on screen to put this object
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
	 * Called to update/draw this GameObject
	 * @param deltaTime Time since the last call
	 * @param renderer The batch we are rendering in
	 */
	public void render(float deltaTime, Renderer renderer) {};
	
	/**
	 * Called when this GameObject is disposed of, cleanup resources here
	 */
	public void dispose() {};
	
	/**
	 * Creates the physics fixture for this GameObject
	 * If not overridden, the physics object will be a small circle
	 */
	protected void createFixture() {
		// Create a circle
		CircleShape circle = new CircleShape();
		circle.setRadius(1f);

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
	 * @return The type of body this game object is
	 */
	protected BodyType getBodyType() {
		return BodyType.DynamicBody;
	}
	
	/**
	 * Marks this object for deletion
	 */
	public void cleanup() {
		// Check if this game object has already been deleted
		if(!this.shouldDelete) {
			// Nope, delete it
			this.shouldDelete = true;
			
			// Run cleanup functions
			this.dispose();
		}
	}
	
	/**
	 * Checks if this object has been marked for deletion
	 * @return If we should delete this object or not
	 */
	public boolean shouldDelete() {
		return this.shouldDelete;
	}
	
	/**
	 * Gets this game object's body
	 * @return This game object's body
	 */
	public Body getBody() {
		return this.body;
	}
	
	/**
	 * An event that is fired when the object is touched
	 */
	public void onTouched() {}
	
	/**
	 * Fired when this object collides with an object it register to collide with
	 * @param obj The object it collided with
	 */
	public void onCollide(GameObject obj) {}
}
