package com.teamlemmings.lemmings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Gives easy access to settings
 * @author aschmid
 *
 */
public class Settings {
	private static Preferences pref;
	
	public static Preferences getSettings() {
		// Ensure we have pref defined
		if(pref == null) {
			pref = Gdx.app.getPreferences("AwesomeSheepGame");
		}
		
		// Return it
		return pref;
	}
}
