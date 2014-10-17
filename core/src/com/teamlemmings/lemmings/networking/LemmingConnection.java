package com.teamlemmings.lemmings.networking;

import com.esotericsoftware.kryonet.Connection;

/**
 * Represents a connection to a client
 * @author aschmid
 *
 */
public class LemmingConnection extends Connection {
	// The screen index of this player
	public int screenNumber;
	
	// The name of this player
	public String playerName;
}
