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
	private int viewportX = 20;
	
	// The height of the actual camera size
	private int viewportY = 15;
	
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
		
		// Create the collision handler
		world.setContactListener(this);
		
		// Create the gesture controller
		GestureProcessor ges = new GestureProcessor(this);
		Gdx.input.setInputProcessor(new GestureDetector(ges));
		
		// Create the touch wall
		new TouchWall(this);
		
		// Create some walls
		new Wall(this, 0f, -7.5f, cam.viewportWidth, 2f);
		new Wall(this, -10f, -7.5f, 2f, cam.viewportHeight);
		new Wall(this, 10f, -7.5f, 2f, cam.viewportHeight);
		
		// Create a ramp to walk up
		new InteractiveRamp(this, 8f, -2f, 4f, 0.5f, -4f, 0.25f, 0, (float)Math.PI/3, false);
		
		// Create the goal for the sheep
		new Goal(this, 7f, -1f);
		
		// Create some test sheep
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
