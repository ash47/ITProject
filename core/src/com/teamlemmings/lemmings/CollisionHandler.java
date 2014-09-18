package com.teamlemmings.lemmings;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionHandler implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		System.out.println("Begin!");
		
	}

	@Override
	public void endContact(Contact contact) {
		System.out.println("End!");
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	
}
