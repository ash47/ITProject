package com.teamlemmings.lemmings.screens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.GestureProcessor;
import com.teamlemmings.lemmings.gameobjects.GameObject;
import com.teamlemmings.lemmings.gameobjects.Goal;
import com.teamlemmings.lemmings.gameobjects.SensorZone;
import com.teamlemmings.lemmings.gameobjects.Sheep;
import com.teamlemmings.lemmings.gameobjects.TouchWall;
import com.teamlemmings.lemmings.gameobjects.Wall;
import com.teamlemmings.lemmings.gameobjects.interactiveobjects.InteractiveRamp;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a screen in the game where users can interact and play
 * @author aschmid
 *
 */
public class GameScreen extends LemmingScreen implements ContactListener {
	// Temp: Used to render the physics world
	private Box2DDebugRenderer debugRenderer;
	
	// The physics world
	private World world;
	
	// The camera
	private OrthographicCamera cam;
	
	// An accumulator used for updating physics smoothly
	private float accumulator = 0;
	
	// Am array of all the GameObjects in this screen
	private ArrayList<GameObject> gameObjects;
	
	// The width of the actual camera size
	private int viewportX = 48;
	
	// The height of the actual camera size
	private int viewportY = 27;
	
	// The sprite batch renderer
	private SpriteBatch batch;
	
	/**
	 * Create a new game screen
	 * @param game The game this screen is attached to
	 */
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
		
		// Move camera into position
		cam.translate(cam.viewportWidth/2, -cam.viewportHeight/2);
		
		// Create the collision handler
		world.setContactListener(this);
		
		// Create the gesture controller
		GestureProcessor ges = new GestureProcessor(this);
		Gdx.input.setInputProcessor(new GestureDetector(ges));
		
		// Create the touch wall
		new TouchWall(this);
		
		// Calculate coords of top left
		float left = 0;
		float top = 0;
		float bottom = -viewportY+1;
		
		// Load a level
		
		/*
		 * 
		 * ADD EXCEPTION HANDLERS HERE!
		 * 
		 */
		
		// Read the data
		FileHandle handle = Gdx.files.internal("maps/level1.json");
		String jsonData = handle.readString();
		
		// Load the json data
		JSONObject json = new JSONObject(jsonData);
		
		// Grab the physics data
		JSONArray physicsData = json.getJSONArray("physicsData");
		JSONArray tileData = json.getJSONArray("tileData");
		
		// Spawn the physics meshes
		for(int i=0; i<physicsData.length(); i++) {
			// Grab the next object
			JSONObject obj = physicsData.getJSONObject(i);
			
			// Grab data
			String sort = obj.getString("sort");
			float x = (float) obj.getDouble("x");
			float y = (float) obj.getDouble("y");
			
			JSONArray jsonVerts = obj.getJSONArray("verts");
			
			// Create the vert array
			int len = jsonVerts.length();
			float[] verts = new float[len];
			
			for(int j=0; j<len; j++) {
				verts[j] = (float) jsonVerts.getDouble(j);
			}
			
			// Check what to make
			if(sort.equals("wall")) {
				// Create a wall
				new Wall(this, left+x, top-y, verts);
			}
		}
		
		// Spawn tiles
		for(int i=0; i<tileData.length(); i++) {
			// Grab the next object
			JSONObject obj = tileData.getJSONObject(i);
			
			// Grab data
			String sort = obj.getString("sort");
			float x = (float) obj.getDouble("x");
			float y = (float) obj.getDouble("y");
			
			// Check what to make
			if(sort.equals("sheep")) {
				// Create a wall
				new Sheep(this, left+x, top-y);
			}
		}
		
		// Create boundary walls
		/*new Wall(this, left, bottom, viewportX, 1f);		// Floor
		new Wall(this, left, top, 1f, viewportY);			// Right Wall
		new Wall(this, left+viewportX-1, 0, 1f, viewportY);	// Left wall
		
		// Top layer
		new Wall(this, left, top-3, 9f, 1f);
		new Wall(this, left+22, top-3, 8f, 1f);
		new Wall(this, left+35, top-3, 14f, 1f);
		
		// 2nd layer
		new Wall(this, left+8, top-7, 22f, 1f);
		
		// 3rd layer
		new Wall(this, left+8, top-12, 39f, 1f);
		
		// 4th layer
		new Wall(this, left+27, top-17, 21f, 1f);
		
		// 5th layer
		new Wall(this, left+15, top-21, 8f, 1f);
		
		// Vertical walls
		new Wall(this, left+8, top-3, 1f, 5f);
		new Wall(this, left+29, top-3, 1f, 5f);
		new Wall(this, left+34, top-3, 1f, 5f);
		
		// Create a ramp to walk up
		new InteractiveRamp(this, left+22f, top-3f, 5f, 1f, 5f, 0f, 0, (float)Math.PI/3, false);
		new InteractiveRamp(this, left+23f, top-21f, 7f, 1f, 0f, 0f, (float)Math.PI/4, (float) (2*Math.PI - Math.PI/4), true);
		
		// Create the goal for the sheep
		new Goal(this, left+45f, top-16f);
		
		// Create some test sheep
		for(int i=0; i<8; i++) {
			new Sheep(this, left + 2 + i, top-2f);
		}*/
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
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Update the camera
		cam.update();
		
		// Begin drawing the sprite batch
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		// Update the physics world
		doPhysicsStep(delta);
		
		// Update all the GameObjects
		Iterator<GameObject> it = gameObjects.iterator();
		while(it.hasNext()) {
			// Grab the next object
			GameObject obj = it.next();
			
			// Check if we should delete this object
			if(obj.shouldDelete()) {
				// Cleanup the body
				Body body = obj.getBody();
				if(body != null) {
					world.destroyBody(body);
					body.setUserData(null);
					body = null;
				}
				
				// Yep, remove the object
				it.remove();
			} else {
				// Render the object
				obj.render(delta, batch);
			}
		}
		
		// Finish drawing the sprite batch
		batch.end();
		
		// Render the world
		debugRenderer.render(world, cam.combined);
	}
	
	@Override
	public void dispose() {
		// Cleanup resources
		batch.dispose();
	}
	
	/**
	 * This function tries to do a smooth physics step, it will limit the max delta time, to ensure the game won't crash
	 * @param deltaTime The time since the last call to this function
	 */
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
	
	/**
	 * Gets a reference to the physics world
	 * @return The physics world
	 */
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
		// Convert to useful coordinates
		float worldX = screenToWorldX(x);
		float worldY = screenToWorldY(y);
		
		// Create a collision zone temporarily
		SensorZone s = new SensorZone(this, worldX, worldY, 1f, 1f);
		s.cleanup();
	}

	@Override
	public void beginContact(Contact contact) {
		// We should setup a listening system for game objects eventually
		// instead of hard coding collision events
		
		// Grab the two game objects that touched
		GameObject a = (GameObject) contact.getFixtureA().getBody().getUserData();
		GameObject b = (GameObject) contact.getFixtureB().getBody().getUserData();
		
		// Ensure if we have an sensor object, that it is stored in a
		if(b instanceof SensorZone) {
			GameObject tmp = a;
			a = b;
			b = tmp;
		}
		
		// Check if we are dealing with sensor
		if(a instanceof SensorZone) {
			// Fire the touch event on this game object
			b.onTouched();
		}
		
		// Check for goals
		if(b instanceof Goal) {
			GameObject tmp = a;
			a = b;
			b = tmp;
		}
		
		if(a instanceof Goal && b instanceof Sheep) {
			a.onCollide(b);
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
