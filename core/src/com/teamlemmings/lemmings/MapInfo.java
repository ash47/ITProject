package com.teamlemmings.lemmings;

import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

/**
 * Contains info on a given map
 * @author aschmid
 *
 */
public class MapInfo {
	// The total number of screens needed
	public int totalScreens;
	
	// The name of the map
	public String mapName;
	
	// The title of the map
	public String mapTitle;
	
	// The number of sheep to win
	public int sheepToWin;
	
	// Have we beaten it?
	public boolean beaten;
	
	/**
	 * Gets info on the given map
	 * @param mapName The name of the map to get info on
	 */
	public MapInfo(String mapName) {
		
		/**
		 * ADD EXCEPTION HANDLER HERE
		 */
		
		// Load up the map
		FileHandle handle = Gdx.files.internal("maps/"+mapName+".json");
		String jsonData = handle.readString();
		
		// Load the json data
		JSONObject json = new JSONObject(jsonData);
		
		// Load up map info
		this.totalScreens = json.getInt("totalScreens");
		this.mapTitle = json.getString("mapTitle");
		this.mapName = mapName;
		this.sheepToWin = json.getInt("sheepToWin");
		
		// Default to not beaten
		this.beaten = false;
		
		// Check if the map has been beaten
		Preferences pref = Settings.getSettings();
		if(pref.contains("beaten_"+mapName)) {
			beaten = true;
		}
	}
}
