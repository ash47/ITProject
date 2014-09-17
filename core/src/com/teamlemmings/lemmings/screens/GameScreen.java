package com.teamlemmings.lemmings.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen extends LemmingScreen {
	private Box2DDebugRenderer debugRenderer;
	private World world;
	
	private OrthographicCamera camera;
	
	public GameScreen(Game game) {
		super(game);
		
		// Create a physics world and debug renderer
		// We need to replace the debug renderer with
		//  actual graphics at some point
		this.world = new World(new Vector2(0, -10), true);
		this.debugRenderer = new Box2DDebugRenderer();
		
		// Create the camera
		this.camera = new OrthographicCamera(24, 16);
	}
	
	@Override
	public void show () {
		
	}

	@Override
	public void render(float delta) {
		// Update the camera
		camera.update();
		
		// Update the physics world
		world.step(1/60f, 6, 2);
		
		// Render the world
		debugRenderer.render(world, camera.combined);
	}

}
