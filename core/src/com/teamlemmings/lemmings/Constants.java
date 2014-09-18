package com.teamlemmings.lemmings;

public class Constants {
	// How often we should update
	public static final float TIME_STEP = 1/60f;
	
	// How many iterations for velocity we should run
	public static final int VELOCITY_ITERATIONS = 6;
	
	// How many iterations we should run for position
	public static final int POSITION_ITERATIONS = 2;
	
	// Collision Categories
	// Different categories for things that should / shouldn't collide
	// Upto 16 categories can exist
	
	// World Collision Category
	public static final short CATEGORY_WORLD = 1;
	
	// Sheep Collision Category
	public static final short CATEGORY_SHEEP = 2;
	
	// Collision Masks
	// The mask contains the bits of the categories it should collide with
	// -1 will collide with all groups
	
	// World Mask
	public static final short MASK_WORLD = -1;
	
	// Sheep Mask
	public static final short MASK_SHEEP = CATEGORY_WORLD;
}
