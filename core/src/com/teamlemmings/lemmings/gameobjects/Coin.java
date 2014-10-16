package com.teamlemmings.lemmings.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.Renderer;
import com.teamlemmings.lemmings.screens.GameScreen;

/**
 * A sheep that walks around
 * @author aschmid
 *
 */
public class Coin extends GameObject {
	// Scale of the sprite
	private static final float spriteScale = 1f/235f;
	
    /**
     * Create a new sheep
     * @param screen The screen to attach this sheep to
     * @param x The x position to place the sheep
     * @param y The y position to place the sheep
     */
	public Coin(GameScreen screen, float x, float y) {
		// Setup the game object
		super(screen, x, y);
	}
	
	@Override
	public void render(float deltaTime, Renderer renderer) {
		// Grab our position
		Vector2 pos = this.body.getPosition();
		
		// Render the sprite
		renderer.renderSprite("Items/coinGold", pos.x, pos.y, spriteScale);
	}
	
	@Override
	protected void createFixture() {
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(0.5f, 0.5f, new Vector2(0.5f, -0.5f), 0);
		
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
	public void onCollide(GameObject obj) {
		if(obj instanceof Goal) {
			// Cleanup the sheep
			this.cleanup();
			
			// Tell the user one got home
			System.out.println("A sheep got home!");
		}
	}
	
	@Override
	protected BodyType getBodyType() {
		return BodyType.KinematicBody;
	}
}
