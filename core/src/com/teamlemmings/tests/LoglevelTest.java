package com.teamlemmings.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.teamlemmings.lemmings.GdxTestRunner;
import com.teamlemmings.lemmings.Lemmings;

@RunWith(GdxTestRunner.class)
public class LoglevelTest {

	@Test
	public void loglevelIsDebug() {
		assumeFalse(Lemmings.isRelease);
		Lemmings app = new Lemmings();
		app.setLogLevel();

		assertEquals(Application.LOG_DEBUG, Gdx.app.getLogLevel());
	}
}
