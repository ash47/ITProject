package com.teamlemmings.lemmings.screens;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.gameobjects.GameObject;
import com.teamlemmings.lemmings.gameobjects.Sheep;
import com.teamlemmings.lemmings.gameobjects.TouchWall;
import com.teamlemmings.lemmings.gameobjects.Wall;

public class GameScreen extends LemmingScreen {
	private Box2DDebugRenderer debugRenderer;
	private World world;
	
	private OrthographicCamera cam;
	
	private float accumulator = 0;
	
	// Am array of all the GameObjects in this screen
	private ArrayList<GameObject> gameObjects;
	
	// The width of the actual camera size
	private int viewportX = 200;
	
	// The height of the actual camera size
	private int viewportY = 150;
	
	public GameScreen(Game game) {
		super(game);
	}
	
	@Override
	public void show () {
		// Create the list to store game objects into
		gameObjects = new ArrayList<GameObject>();
		
		// Create a physics world and debug renderer
		// We need to replace the debug renderer with
		//  actual graphics at some point
		world = new World(new Vector2(0, -10), true);
		debugRenderer = new Box2DDebugRenderer();
		
		// Create the camera
		cam = new OrthographicCamera(viewportX, viewportY);
		
		// Create the touch wall
		new TouchWall(this);
		
		// Create some walls
		new Wall(this, 0f, -75f, cam.viewportWidth, 10f);
		new Wall(this, -100f, -75f, 20f, cam.viewportHeight);
		new Wall(this, 100f, -75f, 20f, cam.viewportHeight);
		
		// Create a new sheep
		for(int i=0; i<10; i++) {
			new Sheep(this, 4f*i, 0f);
		}
	}
	
	/**
	 * Adds a given gameObject into the scene
	 * @param obj
	 */
	public void addObject(GameObject obj) {
		// Add the GameObject
		this.gameObjects.add(obj);
	}

	@Override
	public void render(float delta) {
		// Reset the background
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Update the camera
		cam.update();
		
		// Update all the GameObjects
		Iterator<GameObject> it = gameObjects.iterator();
		while(it.hasNext()) {
			GameObject obj = it.next();
			obj.update(delta);
		}
		
		// Update the physics world
		doPhysicsStep(delta);
		
		// Render the world
		debugRenderer.render(world, cam.combined);
	}

	private void doPhysicsStep(float deltaTime) {
	    // fixed time step
	    // max frame time to avoid spiral of death (on slow devices)
	    float frameTime = Math.min(deltaTime, 0.25f);
	    accumulator += frameTime;
	    while (accumulator >= Constants.TIME_STEP) {
	        world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
	        accumulator -= Constants.TIME_STEP;
	    }
	}
	
	public World getWorld() {
		return this.world;
	}
	
	/**
	 * Converts a screen x coordinate into a world x coordinate, adjusting for camera and viewport
	 * @param x screen x coordinate
	 * @return world x coordinate
	 */
	public float screenToWorldX(float x) {
		return x / Gdx.graphics.getWidth() * cam.viewportWidth - cam.viewportWidth/2 + cam.position.x;
	}
	
	/**
	 * Converts a screen y coordinate into a world y coordinate, adjusting for camera and viewport
	 * @param y screen y coordinate
	 * @return world y coordinate
	 */
	public float screenToWorldY(float y) {
		return -y / Gdx.graphics.getHeight() * cam.viewportHeight + cam.viewportHeight/2 + cam.position.y;
	}
}
