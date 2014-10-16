package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.Renderer;
import com.teamlemmings.lemmings.screens.GameScreen;

/**
 * Represents the goal the sheep need to get to
 * @author aschmid
 *
 */
public class Goal extends GameObject {
	// Scale of the sprite
	private static final float spriteScale = 1f/235f;
	
	/**
	 * Creates a new goal for the sheep
	 * @param screen The screen to attach to
	 * @param x The x coordinate of the goal
	 * @param y The y coordinate of the goal
	 */
	public Goal(GameScreen screen, float x, float y) {
		super(screen, x, y);
	}
	
	@Override
	public void render(float deltaTime, Renderer renderer) {
		// Grab our position
		Vector2 pos = this.body.getPosition();
		
		// Render the sprite
		renderer.renderSprite("Tiles/door_openTop", pos.x+0.5f, pos.y+0.5f, spriteScale);
		renderer.renderSprite("Tiles/door_openMid", pos.x+0.5f, pos.y-0.5f, spriteScale);
	}
	
	@Override
	protected void createFixture() {
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(0.5f, 0.75f, new Vector2(0.5f, -0.5f), 0);
		
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
		return BodyType.KinematicBody;
	}
}
