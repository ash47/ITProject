package com.teamlemmings.lemmings.screens;

import java.net.InetAddress;
import java.util.List;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.teamlemmings.lemmings.networking.Networking;

/**
 * Represents menus
 * @author Daniel
 *
 */
public class MenuScreen extends LemmingScreen {
	/**
	 * Creates a new menu screen
	 * @param game The game to attach this menu to
	 */
	public MenuScreen(Game game) {
		super(game);
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
        
        // Make the stage use able
        Gdx.input.setInputProcessor(stage);
        
        // Create the sprite batch
     	batch = new SpriteBatch();
        
        // Create a background
        background = new Texture(Gdx.files.internal("Backgrounds/bg_castle.png"));
        background.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        
        // Create the networking manager
        network = new Networking();
        
        // Load up the main menu
        menuMain();
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
        	    	ms.loadLevel(mapName);
        	    }
        	});
            table.add(btn).size(150,60).padBottom(20).row();
        }
    }
    
    
    public void createLobby(String mapName) {
    	// Create the server
    	network.makeLobby(mapName);
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
        		// Create the button
        		btn = new TextButton(address.getHostAddress(), skin);
            	table.add(btn).size(150,60).padBottom(20).row();
        	}
        }
    }
    
    /**
     * Loads the given level
     * @param mapName The level to load
     */
    public void loadLevel(String mapName) {
    	// Create a new game screen
    	GameScreen gs = new GameScreen(this.game);
    	
    	// Load up the correct map
    	gs.loadLevel(mapName);
    	
    	// Change to that screen
    	((Game)Gdx.app.getApplicationListener()).setScreen(gs);
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