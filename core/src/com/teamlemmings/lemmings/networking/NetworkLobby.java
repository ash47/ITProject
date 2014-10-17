package com.teamlemmings.lemmings.networking;

/**
 * Represents a Lobby
 * @author aschmid
 *
 */
public class NetworkLobby {
	// The name of the map we are playing
	public String mapName;
	
	// The total number of screens needed
	public int totalScreens;
	
	// An array of the players in this session
	public String[] players;
}
