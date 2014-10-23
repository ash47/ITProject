package com.teamlemmings.lemmings.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.teamlemmings.lemmings.Lemmings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Build texture atlas
		Settings settings = new Settings();
		settings.pot = true;
	    //settings.maxWidth = 512;
	    //settings.maxHeight = 512;
	    TexturePacker.process(settings, "../Images", "../android/assets", "game");
		
		// Create a new config
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		// Set the width and height
		config.width = 1920/2;
		config.height = 1080/2;
		
		// Create the application
		new LwjglApplication(new Lemmings(), config);
	}
}
