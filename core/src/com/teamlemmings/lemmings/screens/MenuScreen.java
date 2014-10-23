package com.teamlemmings.lemmings.screens;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.teamlemmings.lemmings.Constants;
import com.teamlemmings.lemmings.networking.Networking;

/**
 * Represents menus
 * @author dberglie
 *
 */
public class MenuScreen extends LemmingScreen {
	/**
	 * Creates a new menu screen
	 * @param game The game to attach this menu to
	 */
	public MenuScreen(Game game) {
		super(game);
		
		// Create the networking manager
        network = new Networking();
        network.setMenuScreen(this);
	}
	
	// The stage to render to
	private Stage stage;
	
	// The table used to make the menu nice
    private Table table;

    // The skin of the menu items
    private Skin skin;
    
	// The sprite batch renderer
	private SpriteBatch batch;
		
	// The background to draw in the menu
	private Texture background;
	
	// Background tile scale
	private int bgScale = 5;
	
	// The networking manager
	private Networking network;
	
	// Should we load the map on the next tick
	private boolean loadNextTick = false;
	
    @Override
    public void show() {
    	// Load the skin
    	skin = new Skin(Gdx.files.internal("skins/menu.json"), new TextureAtlas(Gdx.files.internal("skins/menu.pack")));
    	
    	// Create the stage
    	stage = new Stage();
    	
    	// Crate the table
    	table = new Table();
    	table.clear();
    	table.setFillParent(true);
        stage.addActor(table);
        
        // Create the sprite batch
     	batch = new SpriteBatch();
        
        // Create a background
        background = new Texture(Gdx.files.internal("Backgrounds/bg_castle.png"));
        background.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        
        // Make the stage use able
        Gdx.input.setInputProcessor(stage);
    }
    
    /**
     * Shows the main menu
     */
    public void menuMain() {
    	// Clear the menu
    	table.clear();
    	
    	// A reference to this
    	final MenuScreen ms = this;
    	
    	// Play
        TextButton btn = new TextButton("Maps", skin);
        btn.addListener(new ClickListener(){
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) {
    	    	// Change to the maps screen
    	    	ms.menuMaps();
    	    }
    	});
        table.add(btn).size(150,60).padBottom(20).row();
        
        // Find Games
        btn = new TextButton("Servers", skin);
        btn.addListener(new ClickListener(){
    		@Override
    	    public void clicked(InputEvent event, float x, float y) {
    			// Go to the find games menu
    	    	ms.menuFindGames();
    	    }
    	});
        table.add(btn).size(150,60).padBottom(20).row();
        
        // Exit
        btn = new TextButton("Exit", skin);
        btn.addListener(new ClickListener(){
    		@Override
    	    public void clicked(InputEvent event, float x, float y) {
    	    	Gdx.app.exit(); //TODO
    	    }
    	});
        table.add(btn).size(150,60).padBottom(20).row();
    }
    
    /**
     * Shows the map selection screen
     */
    public void menuMaps() {
    	// Clear the menu
    	table.clear();
    	
    	// A reference to this
    	final MenuScreen ms = this;
    	
    	// Back
        TextButton btn = new TextButton("Back", skin);
        btn.addListener(new ClickListener(){
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) {
    	    	// Go back to the main menu
    	    	ms.menuMain();
    	    }
    	});
        table.add(btn).size(150,60).padBottom(20).row();
        
        /**
         * NOTE: Should have exception handler here!
         */
        
        // Find all maps
        FileHandle dirHandle;
        if (Gdx.app.getType() == ApplicationType.Android) {
        	dirHandle = Gdx.files.internal("maps");
        } else {
        	// ApplicationType.Desktop ..
        	dirHandle = Gdx.files.internal("./bin/maps");
        }
        
        // Loop over each map
        for(FileHandle map : dirHandle.list()) {
        	// Grab the name of the map
        	final String mapName = map.nameWithoutExtension();
        	
        	btn = new TextButton(mapName, skin);
            btn.addListener(new ClickListener(){
        	    @Override
        	    public void clicked(InputEvent event, float x, float y) {
        	    	//ms.loadLevel(mapName);
        	    	network.makeLobby(mapName);
        	    }
        	});
            table.add(btn).size(150,60).padBottom(20).row();
        }
    }
    
    /**
     * Creates a lobby for the given map
     * @param mapName The map to make a lobby for
     */
    public void createLobby(String mapName) {
    	// Create the server
    	network.makeLobby(mapName);
    }
    
    /**
     * Shows the lobby menu
     */
    public void menuLobby() {
    	// Clear the menu
    	table.clear();
    	
    	// A reference to this
    	final MenuScreen ms = this;
    	
    	// Back
    	TextButton btn = new TextButton("Back", skin);
        btn.addListener(new ClickListener(){
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) {
    	    	if(ms.network.isServer()) {
    	    		// Go back to the map selection screen
        	    	ms.menuMaps();
        	    	
        	    	// Cleanup network connection
        	    	ms.network.closeServer();
    	    	} else {
    	    		// Go back to the find games screen
    	    		ms.menuFindGames();
    	    		
    	    		// Cleanup client
    	    		ms.network.disconnectClient();
    	    	}
    	    }
    	});
        table.add(btn).size(150,60).padBottom(20).row();
        
        // Highscores
        btn = new TextButton("Highscores", skin);
        btn.addListener(new ClickListener(){
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) {
    	    	// Show the highscores
    	    	ms.menuHighScores(network.getLobby().mapName);
    	    }
    	});
        table.add(btn).size(150,60).padBottom(20).row();
        
        // Add list of players
        for(String player : ms.network.getLobby().players) {
        	if(player != null) {
        		// A player
        		btn = new TextButton(player, skin);
            	table.add(btn).size(150,60).padBottom(20).row();
        	} else {
        		// An empty slot
        		btn = new TextButton("<awaiting player>", skin);
            	table.add(btn).size(150,60).padBottom(20).row();
        	}
        }
        
        // Work out how many players are needed
        int playersNeeded = ms.network.getLobby().totalScreens - ms.network.getLobby().connectedPlayers;
        
        // Can they start the game?
        if(playersNeeded <= 0) {
        	if(ms.network.isServer()) {
	        	// Start game
	        	btn = new TextButton("Start", skin);
	        	btn.addListener(new ClickListener(){
	        	    @Override
	        	    public void clicked(InputEvent event, float x, float y) {
	        	    	// Starts the game
	        	    	ms.network.startGame();
	        	    }
	        	});
	        	table.add(btn).size(150,60).padBottom(20).row();
        	} else {
        		btn = new TextButton("Waiting for host", skin);
	        	table.add(btn).size(150,60).padBottom(20).row();
        	}
        } else {
        	// Nope, tell them how many are needed
        	btn = new TextButton("Waiting for "+playersNeeded+" players", skin);
        	table.add(btn).size(150,60).padBottom(20).row();
        }
    }
    
    /**
     * Shows the victory screen
     * @param sheepHome Number of sheep that got home
     * @param sheepTotal Number of sheep that were needed
     * @param score Score collected
     */
    public void menuVictory(int sheepHome, int sheepTotal, int score) {
    	this.menuLobby();
    	
    	TextButton btn;
    	
    	// Did they win?
    	if(sheepHome >= sheepTotal) {
    		btn = new TextButton("VICTORY!", skin);
            table.add(btn).size(150,60).padBottom(20).row();
    	}
    	
    	// Add info
    	btn = new TextButton("Sheep: "+sheepHome+"/"+sheepTotal, skin);
        table.add(btn).size(150,60).padBottom(20).row();
        
        btn = new TextButton("Score: "+score, skin);
        table.add(btn).size(150,60).padBottom(20).row();
    }
    
    /**
     * Shows the highscores
     */
    public void menuHighScores(String mapName) {
    	// Clear the menu
    	table.clear();
    	
    	// A reference to this
    	final MenuScreen ms = this;
    	
    	// Back
        TextButton btn = new TextButton("Back", skin);
        btn.addListener(new ClickListener(){
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) {
    	    	// Go back to the lobby
    	    	ms.menuLobby();
    	    }
    	});
        table.add(btn).size(150,60).padBottom(20).row();
        
        // Setup the request
        HttpRequest httpGet = new HttpRequest(HttpMethods.GET);
        httpGet.setUrl(String.format(Constants.HIGHSCORE_SERVER, mapName));
        
        // Do the request
        Gdx.net.sendHttpRequest (httpGet, new HttpResponseListener() {
        	@Override
        	public void handleHttpResponse(HttpResponse httpResponse) {
        		// Grab the response
        		String status = httpResponse.getResultAsString();
                
        		// Parse it
        		JSONObject json = new JSONObject(status);
        		
        		// Check for failure to find highscores
        		if(json.has("failure") || !json.has("scores")) {
        			// No highscores!
        			TextButton btn = new TextButton("No highscores found", skin);
                    table.add(btn).size(150,60).padBottom(20).row();
        		} else {
        			// Add the scores
        			JSONArray scores = json.getJSONArray("scores");
        			
        			// Add them
        			for(int i=0; i<scores.length(); i++) {
        				// Ensure it's not null
        				if(!scores.isNull(i)) {
        					// Grab data
            				JSONObject data = scores.getJSONObject(i);
        					
            				// Grab info
        					String user = data.getString("user");
            				int score = data.getInt("score");
            				
            				// Display the score
            				TextButton btn = new TextButton(user+" - "+score, skin);
                            table.add(btn).size(150,60).padBottom(20).row();
        				}
        			}
        		}
            }
               
            @Override
			public void failed(Throwable t) {
            	// Add the failure notice
            	
            	System.out.println(t.getMessage());
            	
            	TextButton btn = new TextButton("Failed to connect", skin);
                table.add(btn).size(150,60).padBottom(20).row();
			}
	
			@Override
			public void cancelled() {
				// TODO Auto-generated method stub
					
			}
        });
    }
    
    /**
     * Shows the find games menu
     */
    public void menuFindGames() {
    	// Clear the menu
    	table.clear();
    	
    	// A reference to this
    	final MenuScreen ms = this;
    	
    	// Back
        TextButton btn = new TextButton("Back", skin);
        btn.addListener(new ClickListener(){
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) {
    	    	// Go back to the main menu
    	    	ms.menuMain();
    	    }
    	});
        table.add(btn).size(150,60).padBottom(20).row();
        
        // Search for network games
        List<InetAddress> servers = network.findServers();
        
        // Did we find any servers?
        if(servers.isEmpty()) {
        	// None found
        	btn = new TextButton("No Servers Found", skin);
        	table.add(btn).size(150,60).padBottom(20).row();
        } else {
        	// Add all the servers
        	for(InetAddress address : servers) {
        		// Grab the address
        		final String adr = address.getHostAddress();
        		
        		// Create the button
        		btn = new TextButton(adr, skin);
            	btn.addListener(new ClickListener(){
            	    @Override
            	    public void clicked(InputEvent event, float x, float y) {
            	    	// Attempt to join the lobby
            	    	network.joinLobby(adr);
            	    }
            	});
            	table.add(btn).size(150,60).padBottom(20).row();
        	}
        }
    }
    
    /**
     * Loads the given level
     * @param mapName The level to load
     */
    public void loadLevel(String mapName, int screenNumber) {
    	// Create a new game screen
    	GameScreen gs = new GameScreen(this.game);
    	
    	// Check if we are using networking
    	if(this.network != null) {
    		gs.setNetwork(this.network);
    		this.network.setGameScreen(gs);
    	}
    	
    	// Load up the correct map
    	gs.loadLevel(mapName, screenNumber);
    	
    	// Change to that screen
    	((Game)Gdx.app.getApplicationListener()).setScreen(gs);
    }
    
    /**
     * Tells the menu to load the map on the next tick
     */
    public void loadMapNextTick() {
    	loadNextTick = true;
    }
    
    @Override
    public void render(float delta) {
        // Clear the screen
    	Gdx.gl.glClearColor(208, 244, 247, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        //Generate the background image
        batch.begin();
		
        // Render the background
		batch.draw(background, 0, 0,
				  background.getWidth()*bgScale, 
				  background.getHeight()*bgScale, 
				  0, background.getWidth(), background.getHeight(), 0);	
		
		// Finish drawing
		batch.end();
        stage.act();
        stage.draw();
        
        // Should we load the map?
        if(loadNextTick) {
        	// No longer need to load the map
        	loadNextTick = false;
        	
        	// Load the map
        	loadLevel(network.getLobby().mapName, network.screenNumber);
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}