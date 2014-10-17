package com.teamlemmings.lemmings.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Listener;
import com.teamlemmings.lemmings.MapInfo;
import com.teamlemmings.lemmings.screens.MenuScreen;

public class Networking {
	// The server
	private Server server;
	
	// The networking client
	private Client client;
	
	// Have we connected / are we running a server
	private boolean started = false;
	
	// Are we the server?
	private boolean isServer = false;
	
	// Are we in the lobby
	private boolean inLobby = false;
	
	// Lobby info
	private NetworkLobby lobby;
	
	// The menu we are attached to
	private MenuScreen menuScreen;
	
	/**
	 * Handles all networking
	 */
	public Networking() {
		// Nothing :O
	}
	
	/**
	 * Finds and returns a list of servers
	 */
	public List<InetAddress> findServers() {
		// Tell the user to wait
		System.out.println("Searching for a server...");
		
		// Ensure we have a client
		if(client == null) {
			client = new Client();
			client.start();
		}
		
		// Search for servers
		return client.discoverHosts(54777, 1500);
	}
	
	/**
	 * Creates a lobby if not already connected or hosting
	 * @return If a server was created
	 */
	public boolean makeLobby(String mapName) {
		// Check if we have already started
		if(server != null) {
			// Close server
			server.close();
			server.stop();
		}
		
		// Tell the user what is going on
		System.out.println("Attempting to start a server...");
		
		// Our player's name
		String playerName = "Player 1";
	    
		// Create a new server instance
	    server = new Server() {
            protected Connection newConnection () {
                // Is there a spare slot?
            	if(lobby.connectedPlayers >= lobby.totalScreens) {
            		return null;
            	}
            	
            	// Create the connection
            	LemmingConnection con = new LemmingConnection();
            	con.screenNumber = lobby.connectedPlayers+1;
            	
            	// Increase the number of connected players
            	lobby.connectedPlayers++;
            	
                return con;
            }
	    };
	    server.start();
	    try {
	    	// Attempt to bind to the given ports
			server.bind(54555, 54777);
			
			// Register classes
			registerClasses(server.getKryo());
			
			// Setup the listener
			listenForMessages();
			
			// YAY!
			started = true;
			
			// We are the server
			isServer = true;
			
			// We are in a lobby
			inLobby = true;
			
			// Grab info on the map
			MapInfo info = new MapInfo(mapName);
			
			// Create the lobby
			lobby = new NetworkLobby();
			lobby.mapName = info.mapName;
			lobby.totalScreens = info.totalScreens;
			lobby.players = new String[info.totalScreens];
			lobby.connectedPlayers = 1;
			
			// Store our name
			lobby.players[0] = playerName;
			
			// Success
			return true;
		} catch (IOException e) {
			// Failure! GGWP
			System.out.println("Failed to make a server!");
			
			// Doh!
			return false;
		}
	}
	
	/**
	 * Registers classes for networking
	 */
	private static void registerClasses(Kryo kryo) {
		kryo.register(NetworkLobby.class);
	}
	
	/**
	 * Listens for messages if we are the server
	 */
	private void listenForMessages() {
		// Stop from listening if not the server
		if(!started || !isServer) return;
		
		server.addListener(new Listener() {
	       public void received (Connection connection, Object object) {
	          if (object instanceof NetworkingRequest) {
	        	  // Client asked for map data
	        	  NetworkingRequest r = (NetworkingRequest)object;
	        	  System.out.println("Client asked for the map data");

	             // Send them some map data
	          }
	       }
	    });
	}
	
	/**
	 * Returns if this is operating as a server or not
	 * @return If this is operating as a server or not
	 */
	public boolean isServer() {
		return this.isServer;
	}
	
	/**
	 * Sends a request to the server for the level data
	 */
	public void requestLevelData() {
		// Don't do anything if we shoudln't
		if(!started || isServer) return;
		
		// Send out the request
		NetworkingRequest r = new NetworkingRequest();
		client.sendTCP(r);
	}
	
	/**
	 * Attaches this to a menu screen
	 * @param menuScreen The menu screen to attach to
	 */
	public void setMenuScreen(MenuScreen menuScreen) {
		this.menuScreen = menuScreen;
	}
}
