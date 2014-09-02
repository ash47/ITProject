package com.teamlemmings.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.teamlemmings.lemmings.GdxTestRunner;
import com.teamlemmings.lemmings.Lemmings;

@RunWith(GdxTestRunner.class)
public class ReleaseCheckListTests {

	@Test
	public void debugLevelIsDisabledOnReleaseStatus() {
		assumeTrue(Lemmings.isRelease);

		Lemmings app = new Lemmings();
		app.setLogLevel();

		assertEquals(Application.LOG_NONE, Gdx.app.getLogLevel());

	}
}
