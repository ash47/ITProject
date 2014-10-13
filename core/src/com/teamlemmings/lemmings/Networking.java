package com.teamlemmings.lemmings;

import java.io.IOException;
import java.net.InetAddress;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

public class Networking {
	// The server
	private Server server;
	
	// The networking client
	private Client client;
	
	// Have we connected / are we running a server
	private boolean started = false;
	
	/**
	 * Handles all networking
	 */
	public Networking() {
		// Tell the user to wait
		System.out.println("Searching for a server...");
	}
	
	/**
	 * Finds and connects to a server, if non exist, it makes one
	 */
	public boolean findServer() {
		// Check if we have already started
		if(started) return false;
		
		// Create a client
		client = new Client();
		client.start();
		
		// Search for servers
		InetAddress address = client.discoverHost(54777, 1500);
		
	    // Did we find a server?
	    if(address != null) {
	    	try {
	    		// Try to connect
				client.connect(5000, address, 54555, 54777);
				
				// Connected!
				started = true;
				
				// Success
				return true;
			} catch (IOException e1) {
				// Tell the user
				System.out.println("Failed to find a server, starting one...");
			}
	    }
	    
	    // Failed to find / connect to a server
	    
	    // Make a server
		return makeServer();
	}
	
	private boolean makeServer() {
		// Check if we have already started
		if(started) return false;
		
		// Tell the user what is going on
		System.out.println("Attempting to start a server...");
	    
		// Create a new server instance
	    server = new Server();
	    server.start();
	    try {
	    	// Attempt to bind to the given ports
			server.bind(54555, 54777);
			
			// Success
			return true;
		} catch (IOException e) {
			// Failure! GGWP
			System.out.println("Failed to make a server!");
			
			// Doh!
			return false;
		}
	}
}
