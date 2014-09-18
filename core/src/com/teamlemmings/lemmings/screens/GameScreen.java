package com.teamlemmings.lemmings.screens;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.teamlemmings.lemmings.CollisionHandler;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.GestureProcessor;
import com.teamlemmings.lemmings.gameobjects.GameObject;
import com.teamlemmings.lemmings.gameobjects.Sheep;
import com.teamlemmings.lemmings.gameobjects.TouchWall;
import com.teamlemmings.lemmings.gameobjects.Wall;

public class GameScreen extends LemmingScreen {
	// Temp: Used to render the physics world
	private Box2DDebugRenderer debugRenderer;
	
	// The physics world
	private World world;
	
	// The camera
	private OrthographicCamera cam;
	
	// An accumulator used for updating phyics smoothly
	private float accumulator = 0;
	
	// Am array of all the GameObjects in this screen
	private ArrayList<GameObject> gameObjects;
	
	// The width of the actual camera size
	private int viewportX = 20;
	
	// The height of the actual camera size
	private int viewportY = 15;
	
	// The sprite batch renderer
	private SpriteBatch batch;
	
	public GameScreen(Game game) {
		super(game);
	}
	
	@Override
	public void show () {
		// Create the list to store game objects into
		gameObjects = new ArrayList<GameObject>();
		
		// Create the sprite batch
		batch = new SpriteBatch();
		
		// Create a physics world and debug renderer
		// We need to replace the debug renderer with
		//  actual graphics at some point
		world = new World(new Vector2(0, -10), true);
		debugRenderer = new Box2DDebugRenderer();
		
		// Create the camera
		cam = new OrthographicCamera(viewportX, viewportY);
		
		// Create the collision handler
		CollisionHandler col = new CollisionHandler();
		world.setContactListener(col);
		
		// Create the gesture controller
		GestureProcessor ges = new GestureProcessor(this);
		Gdx.input.setInputProcessor(new GestureDetector(ges));
		
		// Create the touch wall
		new TouchWall(this);
		
		// Create some walls
		new Wall(this, 0f, -7.5f, cam.viewportWidth, 2f);
		new Wall(this, -10f, -7.5f, 2f, cam.viewportHeight);
		new Wall(this, 10f, -7.5f, 2f, cam.viewportHeight);
		
		// Create a new sheep
		for(int i=0; i<8; i++) {
			new Sheep(this, i-5, 0f);
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
		
		// Begin drawing the sprite batch
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		// Update all the GameObjects
		Iterator<GameObject> it = gameObjects.iterator();
		while(it.hasNext()) {
			GameObject obj = it.next();
			obj.render(delta, batch);
		}
		
		// Finish drawing the sprite batch
		batch.end();
		
		// Update the physics world
		doPhysicsStep(delta);
		
		// Render the world
		debugRenderer.render(world, cam.combined);
	}
	
	@Override
	public void dispose() {
		// Cleanup resources
		batch.dispose();
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
	
	/**
	 * Called when the user taps the screen
	 * @param x The x coordinate they tapped
	 * @param y The y coordinate they tapped
	 * @param count
	 * @param button The button they pressed (left click, right click, etc)
	 */
	public void onTap(float x, float y, int count, int button) {
		System.out.println("User tapped the screen!");
		
		// Convert to useful coordinates
		float worldX = screenToWorldX(x);
		float worldY = screenToWorldX(y);
	}
}
