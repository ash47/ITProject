package com.teamlemmings.lemmings.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
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
 * Represents a screen in the game where users can interact and play
 * @author Daniel
 *
 */
public class MenuScreen extends LemmingScreen {
	public MenuScreen(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
	}

	private Stage stage = new Stage();
    private Table table = new Table();

    private Skin skin = new Skin(Gdx.files.internal("skins/menu.json"),
        new TextureAtlas(Gdx.files.internal("skins/menu.pack")));

    private TextButton buttonPlay = new TextButton("Play", skin),
        buttonOptions = new TextButton("Options", skin);
    
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    	final MenuScreen sc = this;
    	
    	 buttonPlay.addListener(new ClickListener(){
    	        @Override
    	        public void clicked(InputEvent event, float x, float y) {
    	            ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(sc.game));
    	        }
    	    });
    	    buttonOptions.addListener(new ClickListener(){
    	        @Override
    	        public void clicked(InputEvent event, float x, float y) {
    	            Gdx.app.exit();
    	            // or System.exit(0);
    	        }
    	    });

        table.add(buttonPlay).size(150,60).padBottom(20).row();
        table.add(buttonOptions).size(150,60).padBottom(20).row();

        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
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