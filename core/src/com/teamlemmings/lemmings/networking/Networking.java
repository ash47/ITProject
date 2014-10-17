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
	public NetworkLobby lobby;
	
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
		// Close any existing servers
		closeServer();
		
		// Tell the user what is going on
		System.out.println("Attempting to start a server...");
		
		// Our player's name
		String playerName = "Player 1";
	    
		// Create a new server instance
	    server = new Server() {
            protected Connection newConnection () {
                // Is there a spare slot?
            	if(lobby.connectedPlayers >= lobby.totalScreens) {
            		return new Connection();
            	}
            	
            	// Increase the number of connected players
            	lobby.connectedPlayers++;
            	
            	// The slot number this player will be allocated into
            	int slotNumber = -1;
            	
            	// Find slot for this player
            	for(int i=0; i<lobby.totalScreens; i++) {
            		if(lobby.players[i] == null) {
            			slotNumber = i;
            		}
            	}
            	
            	// Did we find a slot?
            	if(slotNumber == -1) {
            		return new Connection();
            	}
            	
            	// Create the connection
            	LemmingConnection con = new LemmingConnection();
            	con.screenNumber = slotNumber;
            	con.playerName = "Player "+(slotNumber+1);
            	
            	// Store this slot as taken
            	lobby.players[slotNumber] = con.playerName; 
            	
            	// Update the menu
            	menuScreen.menuLobby();
            	
            	// Send the update to everyone
            	server.sendToAllTCP(lobby);
            	
            	// Return the connection
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
			listenForMessagesServer();
			
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
			
			// Change to the lobby screen
			menuScreen.menuLobby();
			
			// Success
			return true;
		} catch (IOException e) {
			// Failure! GGWP
			System.out.println("Failed to make a server: "+e.getMessage());
			
			// Doh!
			return false;
		}
	}
	
	public boolean joinLobby(String address) {
		// Disconnect any old clients
		disconnectClient();
		
		// Create new client
		client = new Client();
		new Thread(client).start();
	    //client.start();
	    
	    // Attempt to connect
	    try {
	    	// Connect
			client.connect(5000, address, 54555, 54777);
			
			// Settings
			started = true;
			isServer = false;
			inLobby = true;
			
			// Register classes
			registerClasses(client.getKryo());
			
			// Handler client messages
			listenForMessagesClient();
			
			// Ask for lobby info
			client.sendTCP(new NetworkRequestLobby());
			
			// Success
			return true;
		} catch (IOException e) {
			System.out.println("Failed to connect to "+address+": "+e.getMessage());
			
			// Failire
			return false;
		}
	    
	}
	
	/**
	 * Closes the server
	 */
	public void closeServer() {
		// Check if we have already started
		if(server != null) {
			// Close server
			server.close();
			server.stop();
			server = null;
		}
	}
	
	/**
	 * Disconnects client
	 */
	public void disconnectClient() {
		// Check if we have a client
		if(client != null) {
			// Kill it
			client.close();
			client.stop();
			client = null;
		}
	}
	
	/**
	 * Registers classes for networking
	 */
	private static void registerClasses(Kryo kryo) {
		kryo.register(NetworkLobby.class);
		kryo.register(NetworkRequestLobby.class);
		kryo.register(String[].class);
	}
	
	/**
	 * Listens for messages if we are the server
	 */
	private void listenForMessagesServer() {
		// Stop from listening if not the server
		if(!started || !isServer) return;
		
		// Listen for messages
		server.addListener(new Listener() {
	       public void received (Connection connection, Object object) {
	    	   System.out.println("GOT A MESSAGE!");
	    	   
	    	   // Ensure they are allowed to talk to us
	    	   if(!(connection instanceof LemmingConnection)) {
	    		   connection.close();
	    		   return;
	    	   }
	    	   
	    	   // Process messages
	    	   if (object instanceof NetworkRequestLobby) {
	    		   // Send out the info
	    		   connection.sendTCP(lobby);
	    	   }
	       }
	    });
	}
	
	public void listenForMessagesClient() {
		// Stop from listening if not client
		if(!started || isServer) return;
		
		// Listen for messages
		client.addListener(new Listener() {
	       public void received (Connection connection, Object object) {
	    	   if (object instanceof NetworkLobby) {
	    		   // Store the lobby
	    		   lobby = (NetworkLobby) object;
	    		   
	    		   // Update menu
	    		   menuScreen.menuLobby();
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
