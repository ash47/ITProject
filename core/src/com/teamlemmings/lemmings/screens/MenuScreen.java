package com.teamlemmings.lemmings.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sun.javafx.applet.Splash;

/**
 * Represents the main menu
 * @author Daniel
 *
 */
public class MenuScreen extends LemmingScreen {
	public MenuScreen(Game game) {
		super(game);
	}

	private Stage stage = new Stage();
    private Table table = new Table();

    // The skin of the menu items
    private Skin skin = new Skin(Gdx.files.internal("skins/menu.json"),
        new TextureAtlas(Gdx.files.internal("skins/menu.pack")));

    // The buttons for the menu
    private TextButton buttonPlay = new TextButton("Play", skin),
        buttonOptions = new TextButton("Options", skin);
    
	// The sprite batch renderer
	private SpriteBatch batch;
		
	// The background for this level
	private Texture background;
	
	// Background tile scale
	private int bgScale = 5;
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(208, 244, 247, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        
        //Generate the background image
        batch.begin();
		
		batch.draw(background, 0, 0,
				  background.getWidth()*bgScale, 
				  background.getHeight()*bgScale, 
				  0, background.getWidth(), background.getHeight(), 0);		
		batch.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    	final MenuScreen sc = this;
    	
    	// Play button
    	buttonPlay.addListener(new ClickListener(){
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) {
    	    	((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(sc.game));
    	    }
    	});
    	
    	// Options button
    	buttonOptions.addListener(new ClickListener(){
    		@Override
    	    public void clicked(InputEvent event, float x, float y) {
    	    	Gdx.app.exit(); //TODO
    	    }
    	});
    	 
    	// Add the buttons to the screen
        table.add(buttonPlay).size(150,60).padBottom(20).row();
        table.add(buttonOptions).size(150,60).padBottom(20).row();
        
        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
        
        // Create the sprite batch
     	batch = new SpriteBatch();
        
        // Create a background
        background = new Texture(Gdx.files.internal("bg_castle.png"));
        background.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
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