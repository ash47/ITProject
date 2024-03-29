package com.teamlemmings.lemmings;

/**
 * This class contains constants used throughout the game, and should never be instantiated
 * @author aschmid
 *
 */
public abstract class Constants {
	// How often we should update the physics simulation
	public static final float TIME_STEP = 1/60f;
	
	// How many iterations for velocity we should run in the physics simulation
	public static final int VELOCITY_ITERATIONS = 6;
	
	// How many iterations we should run for position in the physics simulation
	public static final int POSITION_ITERATIONS = 2;
	
	// Collision Categories
	// Different categories for things that should / shouldn't collide
	// Upto 16 categories can exist, they should be 1, 2, 4, 8, etc
	
	// World Collision Category
	public static final short CATEGORY_WORLD = 1;
	
	// Sensor Collision Category
	public static final short CATEGORY_SENSOR = 2;
	
	// Sheep Collision Category
	public static final short CATEGORY_SHEEP = 4;
	
	// Collision Masks
	// The mask contains the bits of the categories it should collide with
	// -1 will collide with all groups
	
	// World Mask
	public static final short MASK_WORLD = -1;
	
	// Sensor Mask
	public static final short MASK_SENSOR = -1;
	
	// Sheep Mask
	public static final short MASK_SHEEP = CATEGORY_WORLD + CATEGORY_SENSOR;
	
	// The server address for getting highscores
	public static final String HIGHSCORE_SERVER = "http://127.0.0.1/highscores/%s";
	
	// The server address for adding highscores
	public static final String HIGHSCORE_SERVER_ADD = "http://127.0.0.1/highscores/%s/%s/%s";
}
