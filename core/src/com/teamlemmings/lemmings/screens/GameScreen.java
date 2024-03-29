package com.teamlemmings.lemmings.screens;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.teamlemmings.lemmings.MapInfo;
import com.teamlemmings.lemmings.Renderer;
import com.teamlemmings.lemmings.gameobjects.Coin;
import com.teamlemmings.lemmings.gameobjects.GameObject;
import com.teamlemmings.lemmings.gameobjects.Goal;
import com.teamlemmings.lemmings.gameobjects.Liquid;
import com.teamlemmings.lemmings.gameobjects.SensorZone;
import com.teamlemmings.lemmings.gameobjects.Sheep;
import com.teamlemmings.lemmings.gameobjects.TouchWall;
import com.teamlemmings.lemmings.gameobjects.Wall;
import com.teamlemmings.lemmings.gameobjects.interactiveobjects.InteractiveRamp;
import com.teamlemmings.lemmings.networking.Networking;

import org.json.JSONObject;
import org.json.JSONArray;

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
	public final int viewportX = 48;
	
	// The height of the actual camera size
	public final int viewportY = 27;
	
	// The sprite batch renderer
	public SpriteBatch batch;
	
	// The background for this level
	private Texture background;
	
	// Background tile scale
	private int bgScale = 5;
	
	// Our network
	private Networking network;
	
	// The renderer
	private Renderer renderer;
	
	// The visual data
	private String[] visualArray;
	
	// The scale to render the world at
	private float worldScale = 1 / 235f;
	
	// The score
	private int score;
	
	// The number of sheep that got home
	private int sheepHome;
	
	// The number of sheep required to get home in order to win
	private int sheepToWin;
	
	// The font to use to draw text
	private BitmapFont font;
	
	// Should we return to the lobby next tick?
	public int returnToLobbyNextTick;
	
	// Should we return to the main menu?
	public boolean returnToMenu;
	
	Sound backgroundSound;
	Long id;
		
	/**
	 * Create a new game screen
	 * @param game The game this screen is attached to
	 */
	public GameScreen(Game game) {
		super(game);
		
		// Create the list to store game objects into
		gameObjects = new ArrayList<GameObject>();
		
		// Create the sprite batch
		batch = new SpriteBatch();
		
		// Load font
		//font = new BitmapFont(Gdx.files.internal("menu/font.fnt"), false);
		font = new BitmapFont();
		font.setUseIntegerPositions(false);
        
		// Create the renderer
		renderer = new Renderer(batch);
		
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
	}
	
	@Override
	public void show () {
		// Create the gesture controller
		GestureProcessor ges = new GestureProcessor(this);
		Gdx.input.setInputProcessor(new GestureDetector(ges));
		
		// Init networking
		//network = new Networking();
		//network.findServer();
		
		// Load up a level
		//loadLevel("level1");
		
		// Stop from returning to the lobby
		returnToLobbyNextTick = 0;
		returnToMenu = false;
		
		// Create a background
		background = new Texture(Gdx.files.internal("Backgrounds/bg_castle.png"));
		background.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		backgroundSound = Gdx.audio.newSound(Gdx.files.internal("sounds/background.wav"));
		id = backgroundSound.loop();
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
		
		// Draw the background
		batch.draw(background, -cam.viewportWidth/2, -cam.viewportHeight,
				  background.getWidth()*bgScale,
				  background.getHeight()*bgScale,
				  0, background.getWidth(), background.getHeight(), 0);
		
		// Render the world
		for(int y=0; y<viewportY;y++) {
			for(int x=0; x<viewportX; x++) {
				// Grab the image
				String image = visualArray[x+y*viewportX];
				
				// Should we render something?
				if(!image.equals("")) {
					// Render the sprite
					renderer.renderSprite(image, x+0.5f, -y-0.5f, worldScale);
				}
			}
		}
		
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
				obj.render(delta, renderer);
			}
		}
		
		// Workout if we are in exit / win mode
		String hintText;
		if(this.sheepHome >= this.sheepToWin) {
			hintText = "Long hold here to win the map.";
		} else {
			hintText = "Long hold here to exit to the menu.";
		}
		
		// Render the hud
		font.setScale(0.05f, 0.05f);
		font.setColor(Color.BLACK);
		font.drawMultiLine(batch, hintText+"\nScore: "+this.score+"\nSheep: "+this.sheepHome+'/'+this.sheepToWin, 0, 0);
		
		// Finish drawing the sprite batch
		batch.end();
		
		// Are we meant to return to the lobby?
		if(returnToLobbyNextTick == 2) {
			returnToLobby(false);
		} else if(returnToLobbyNextTick == 1) {
			returnToLobby(true);
		}
		
		if(returnToMenu) {
			returnToMenu();
		}
		
		// DEBUG: Render the world
		//debugRenderer.render(world, cam.combined);
	}
	
	/**
	 * Cleans up the current level
	 */
	public void cleanupLevel() {
		Iterator<GameObject> it = gameObjects.iterator();
		while(it.hasNext()) {
			// Grab the next object
			GameObject obj = it.next();
			
			// Cleanup the object
			obj.cleanup();
			
			// Cleanup the body
			Body body = obj.getBody();
			if(body != null) {
				world.destroyBody(body);
				body.setUserData(null);
				body = null;
			}
			
			// Remove the object
			it.remove();
		}
	}
	
	@Override
	public void dispose() {
		// Cleanup resources
		batch.dispose();
		renderer.dispose();
		font.dispose();
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
	
	/**
	 * Called when the user long presses the screen
	 * @param x The x coordinate they held
	 * @param y The y coordinate they held
	 */
	public void onLongPress(float x, float y) {
		// Convert to something useful
		x /= Gdx.graphics.getWidth();
		y /= Gdx.graphics.getHeight();
		
		// Check if the user was trying to quit
		if(x <= 0.14 && y <= 0.18) {
			returnToLobby(true);
		}
	}
	
	/**
	 * Returns to hte lobby
	 * @param shouldNetwork Should we network this?
	 */
	public void returnToLobby(boolean shouldNetwork) {
		// Grab the menu screen
		MenuScreen ms = this.network.getMenuScreen();
		
		// Change to the menu
		((Game)Gdx.app.getApplicationListener()).setScreen(ms);
		
		// Cleanup
		this.dispose();
		
		// Remove network reference
		this.network.setGameScreen(null);
		this.network.setInLobby(true);
		
		if(shouldNetwork) {
			// Network it
			network.returnToLobby();
		}
		
		// Check if it is a quit or a win
		if(this.sheepHome >= this.sheepToWin) {
			// Menu for winners!
			ms.menuVictory(this.sheepHome, this.sheepToWin, this.score);
			//end music
			backgroundSound.stop(id);
		} else {
			// Menu for losers >_>
			ms.menuVictory(this.sheepHome, this.sheepToWin, this.score);
			//end music
			backgroundSound.stop(id);
		}
	}
	
	/**
	 * Returns to the menu after a dropout
	 */
	public void returnToMenu() {
		// Grab the menu screen
		MenuScreen ms = this.network.getMenuScreen();
		
		//end music
		backgroundSound.stop(id);
		
		// Change to the menu
		((Game)Gdx.app.getApplicationListener()).setScreen(ms);
		
		// Cleanup
		this.dispose();
		
		// Remove network reference
		this.network.setGameScreen(null);
		this.network.setInLobby(true);
		
		// Main menu please (maybe show a error screen next time?)
		ms.menuMain();
	}
	
	@Override
	public void beginContact(Contact contact) {
		// Grab the two game objects that touched
		GameObject a = (GameObject) contact.getFixtureA().getBody().getUserData();
		GameObject b = (GameObject) contact.getFixtureB().getBody().getUserData();
		
		// Fire collision events
		a.onCollide(b);
		b.onCollide(a);
		
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
	
	public void loadLevel(String mapName, int screenNumber) {
		// Cleanup the current level
		cleanupLevel();
		
		// Create the touch wall
		new TouchWall(this);
		
		// Calculate coords of top left
		float left = 0;
		float top = 0;
		
		// Reset score
		this.score = 0;
		this.sheepHome = 0;
		
		// Load a level
		
		MapInfo mi = new MapInfo(mapName);
		this.sheepToWin = mi.sheepToWin;
		
		/*
		 * 
		 * ADD EXCEPTION HANDLERS HERE!
		 * 
		 */
		
		// Read the data
		FileHandle handle = Gdx.files.internal("maps/"+mapName+".json");
		String jsonData = handle.readString();
		
		// Load the json data
		JSONObject json = new JSONObject(jsonData);
		
		// Grab data for my screen
		JSONObject myScreen = json.getJSONObject(""+(screenNumber+1));
		
		// Grab the physics data
		JSONArray physicsData = myScreen.getJSONArray("physicsData");
		JSONArray tileData = myScreen.getJSONArray("tileData");
		JSONArray visualData = myScreen.getJSONArray("visualData");
		
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
				// Create a sheep
				new Sheep(this, left+x, top-y);
			} else if(sort.equals("goal")) {
				// Create a goal
				new Goal(this, left+x, top-y);
			} else if(sort.equals("coin")) {
				// Create a coin
				new Coin(this, left+x, top-y);
			} else if(sort.equals("liquid")) {
				// Create a liquid
				new Liquid(this, left+x, top-y);
			} else if(sort.equals("ramp")) {
				// Create the ramp
				new InteractiveRamp(this, left+x, top-y,
						(float)obj.getDouble("width"), (float)obj.getDouble("height"),
						(float)obj.getDouble("originX"), (float)obj.getDouble("originY"),
						(float)obj.getDouble("initialAngle"), (float)obj.getDouble("finalAngle"),
						obj.getBoolean("clockwise")
				);
				
				// float x, float y, float width, float height, float originX, float originY, float initialAngle, float finalAngle, boolean clockwise
			}
		}
		
		// Load the visual data
		visualArray = new String[visualData.length()];
		for(int i=0; i<visualData.length(); i++) {
			visualArray[i] = visualData.getString(i);
		}
	}
	
	/**
	 * Connects this game to the game network
	 * @param network The network to connect to
	 */
	public void setNetwork(Networking network) {
		this.network = network;
	}
	
	/**
	 * Adds (and networks) score
	 * @param amount Amount of score to add
	 * @param shouldNetwork Do you want this networked?
	 */
	public void addToScore(int amount, boolean shouldNetwork) {
		// Increase score
		this.score += amount;
		
		// Ensure we have a network to send to
		if(shouldNetwork && this.network != null) {
			this.network.updateScore(this.score, amount);
		}
	}
	
	/**
	 * Sets the score
	 * @param score The new score to display
	 */
	public void setScore(int score) {
		this.score = score;
	}
	
	/**
	 * Stores that a sheep got home, and networks it
	 * @param shouldNetwork Do we need to network this?
	 */
	public void sheepGotHome(boolean shouldNetwork) {
		// Increase total sheep that got home
		this.sheepHome++;
		
		// Ensure we have a network to send to
		if(shouldNetwork && this.network != null) {
			this.network.sheepGotHome(this.sheepHome);
		}
	}
	
	/**
	 * Directly sets how many sheep have gotten home
	 * @param totalSheep The total number of sheep that have gotten home
	 */
	public void setTotalSheepHome(int totalSheep) {
		this.sheepHome = totalSheep;
	}
	
	/**
	 * Gets our networking
	 * @return The network of this game screen
	 */
	public Networking getNetworking() {
		return this.network;
	}
	
	/**
	 * Creates a new sheep
	 * @param x x coord to spawn at
	 * @param y y coord to spawn at
	 * @param dir Direction to march in
	 */
	public void createSheep(float x, float y, int dir) {
		Sheep sheep = new Sheep(this, x, y);
		sheep.setDirection(dir);
	}
}
