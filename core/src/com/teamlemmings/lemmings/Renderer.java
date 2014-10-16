package com.teamlemmings.lemmings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
	
	/**
	 * Creates a new renderer
	 */
	public Renderer() {
		// Load up the atlas
		textureAtlas = new TextureAtlas(Gdx.files.internal("game.atlas"));
		
		// Create  the sprite
		AtlasRegion region = textureAtlas.findRegion("sheepRight");
        sprite = new Sprite(region);
	}
	
	/**
	 * Called when this object will be disposed
	 */
	public void dispose() {
		// Cleanup the atlas
        textureAtlas.dispose();
    }
}
