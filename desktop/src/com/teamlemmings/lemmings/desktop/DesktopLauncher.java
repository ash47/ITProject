package com.teamlemmings.lemmings.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.teamlemmings.lemmings.Lemmings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Create a new config
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		// Set the width and height
		config.width = 10920/2;
		config.height = 1080/2;
		
		// Create the application
		new LwjglApplication(new Lemmings(), config);
	}
}
