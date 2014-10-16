package com.teamlemmings.lemmings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

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
	
	public void renderSprite(String name, float x, float y, float scale) {
		// Find and validate the region
		AtlasRegion region = textureAtlas.findRegion(name);
		if(region == null) return;
		
		// Change the sprite
		sprite.setRegion(region);
		
		// Set the scale
		sprite.setScale(scale);
		
		// Set the position
		sprite.setCenter(x, y);
		
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
