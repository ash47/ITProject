package com.teamlemmings.lemmings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * This class handles image rendering
 * @author aschmid
 *
 */
public class Renderer {
	// The texture atlas used to render everything
	private TextureAtlas textureAtlas;
	
	// The sprite we will render with
	private Sprite sprite;
	
	// The batch we will be rendering to
	private SpriteBatch batch;
	
	/**
	 * Creates a new renderer
	 * @param batch The sprite batch to render to
	 */
	public Renderer(SpriteBatch batch) {
		// Store the batch
		this.batch = batch;
		
		// Load up the atlas
		textureAtlas = new TextureAtlas(Gdx.files.internal("game.atlas"));
		
		// Create  the sprite
		AtlasRegion region = textureAtlas.findRegion("sheepRight");
        sprite = new Sprite(region);
	}
	
	/**
	 * Renders the given sprite
	 * @param name The name of the sprite, look in Images/*, where * is the name / path to the image you want to use, including slashes
	 * @param x The x position to draw it at
	 * @param y The y position to draw it at
	 * @param scale The scale to render it at
	 */
	public void renderSprite(String name, float x, float y, float scale) {
		// Find and validate the region
		AtlasRegion region = textureAtlas.findRegion(name);
		if(region == null) return;
		
		// Change the sprite
		sprite.setRegion(region);
		
		// Reset origin
		sprite.setOriginCenter();
		
		// Set the scale
		sprite.setScale(scale);
		
		// Reset rotation
		sprite.setRotation(0);
		
		// Set the position
		sprite.setCenter(x, y);
		
		// Render the sprite
		sprite.draw(this.batch);
	}
	
	/**
	 * 
	 * @param name The name of the sprite, look in Images/*, where * is the name / path to the image you want to use, including slashes
	 * @param x The x position to draw it at
	 * @param y The y position to draw it at
	 * @param scaleX The scale in the x direction
	 * @param scaleY The scale in the y direction
	 * @param rotation The rotation 
	 * @param originX The x origin
	 * @param originY The y origin
	 */
	public void renderSprite(String name, float x, float y, float scaleX, float scaleY, float rotation, float originX, float originY) {
		// Find and validate the region
		AtlasRegion region = textureAtlas.findRegion(name);
		if(region == null) return;
		
		// Change the sprite
		sprite.setRegion(region);
		
		// Update origin
		sprite.setOrigin(0, 0);
		
		// Set the scale
		sprite.setScale(scaleX, scaleY);
		
		// Set the rotation
		sprite.setRotation(rotation);
		
		// Fix rotation issue
		Vector2 pos = new Vector2(0, -1);
		pos.rotate(rotation);
		
		// Set the position
		sprite.setPosition(x+pos.x, y+pos.y);
		
		// Render the sprite
		sprite.draw(this.batch);
	}
	
	/**
	 * Called when this object will be disposed
	 */
	public void dispose() {
		// Cleanup the atlas
        textureAtlas.dispose();
    }
}
