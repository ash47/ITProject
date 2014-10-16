package com.teamlemmings.lemmings.gameobjects.interactiveobjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Transform;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.Renderer;
import com.teamlemmings.lemmings.gameobjects.InteractiveObject;
import com.teamlemmings.lemmings.screens.GameScreen;

public class InteractiveRamp extends InteractiveObject {
	// The current ramp state (false = initial position, true = final position)
	private boolean rampState = false;
	
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
	
	// The speed to rotate at
	private float rotationSpeed = 1f;
	
	// The amount of rotation required to complete the toggle
	private float totalRotation;
	
	// The amount of rotation done in the current toggle
	private float rotationSoFar = 0;
	
	/**
	 * Creates a ramp that can be interacted with
	 * @param screen The screen to attach to
	 * @param x The x coordinate of this object
	 * @param y The y coordinate of this object
	 * @param width The width of the ramp
	 * @param height The height of the ramp
	 * @param originX The x origin to rotate about
	 * @param originY The y origin to rotate about
	 * @param initialAngle The initial rotate to start at (in radians)
	 * @param finalAngle The final angle to finish at
	 * @param clockwise Should we rotate clockwise or not?
	 */
	public InteractiveRamp(GameScreen screen, float x, float y, float width, float height, float originX, float originY, float initialAngle, float finalAngle, boolean clockwise) {
		super(screen, x, y);
		
		// Ensure correct angle ranges
		initialAngle = (float) (initialAngle % (2 * Math.PI));
		finalAngle = (float) (finalAngle % (2 * Math.PI));
		
		// Store the vars
		this.width = width;
		this.height = height;
		this.originX = originX;
		this.originY = originY;
		this.initialAngle = initialAngle;
		this.finalAngle = finalAngle;
		this.clockwise = clockwise;
		
		// Create the fixture
		createFixture();
		
		// Set the initial rotation
		setRotation(this.initialAngle);
		
		// Calculate how much needs to be added in order to reach our goal final angle
		if(clockwise) {
			if(initialAngle > finalAngle) {
				this.totalRotation = initialAngle - finalAngle;
			} else {
				this.totalRotation = (float) (2*Math.PI - (finalAngle - initialAngle));
			}
		} else {
			if(initialAngle < finalAngle) {
				this.totalRotation = finalAngle - initialAngle;
			} else {
				this.totalRotation = (float) (2*Math.PI - (initialAngle - finalAngle));
			}
		}
	}
	
	/**
	 * Sets the angle of the ramp
	 * This should NOT be called by any physics interrupts, such as onTouched!
	 * @param angle The new angle
	 */
	protected void setRotation(float angle) {
		// Grab our position
		Vector2 pos = this.body.getPosition();
		
		// Update our transformation
		this.body.setTransform(pos.x, pos.y, angle);
	}
	
	/**
	 * Adds to our current rotation
	 * This should NOT be called by any physics interrupts, such as onTouched!
	 * @param angle The new angle
	 */
	protected void addRotation(float angle) {
		// Grab our transformation
		Transform trans = this.body.getTransform();
		
		// Grab our position
		Vector2 pos = trans.getPosition();
		
		// Grab the current rotation
		float rot = trans.getRotation();
		
		// Update our transformation
		this.body.setTransform(pos.x, pos.y, rot + angle);
	}
	
	@Override
	protected void createFixture() {
		// Ensure we have been given a size
		if(this.width <= 0 || this.height <= 0) return;
		
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(this.width/2, this.height/2, new Vector2(this.width/2-this.originX, -this.height/2-this.originY), 0);
		
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
		rampState = !rampState;
	}
	
	@Override
	public void render(float deltaTime, Renderer renderer) {
		// How much we should change in this update
		float change = deltaTime * this.rotationSpeed;
		
		if(rampState) {
			// Is more rotation required?
			if(this.rotationSoFar < this.totalRotation) {
				// Add to the rotation so far
				this.rotationSoFar += change;
				
				// Invert direction if clockwise
				if(clockwise) change *= -1;
				
				// Check if this will complete our rotation
				if(this.rotationSoFar >= this.totalRotation) {
					// Set the exact values
					this.rotationSoFar = this.totalRotation;
					
					// Set the final angle
					setRotation(this.finalAngle);
				} else {
					// Add the rotation to our physics fixture
					addRotation(change);
				}
			}
		} else {
			// Is more rotation required?
			if(this.rotationSoFar > 0) {
				// Change the rotation so far
				this.rotationSoFar -= change;
				
				// Invert direction if counter clockwise
				if(!clockwise) change *= -1;
				
				// Check if this will complete our rotation
				if(this.rotationSoFar <= 0) {
					// Set the exact values
					this.rotationSoFar = 0;
					
					// Set the final angle
					setRotation(this.initialAngle);
				} else {
					// Add the rotation to our physics fixture
					addRotation(change);
				}
			}
		}
	}
}
