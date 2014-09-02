package com.teamlemmings.tests;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.teamlemmings.lemmings.Lemmings;

//@RunWith(GdxTestRunner.class)
public class TestingAppApplicationTest {

	@Mock
	Lemmings listener;

	HeadlessApplication app;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		app = new HeadlessApplication(listener);

		try {
			Thread.sleep(100);
		}
		catch (InterruptedException e) {
		}

	}

	/**
	 * If you take a look at the mainLoop() method in HeadlessApplication class
	 * you can see that the listener's methods are invoked like this:
	 * 
	 * create(): at the beginning
	 * render(): again and again...
	 * pause(): not invoked, since render() is not stopped
	 * dispose(): not invoked, since render() is not stopped
	 * 
	 */

	@Test
	public void renderMethodIsInvokedAtLeastOnce() {
		verify(listener, atLeast(1)).render();
	}

	@Test
	public void createMethodIsInvokedAtLeastOnce() {
		verify(listener, atLeast(1)).create();
	}

	@Test
	public void pauseIsNotInvoked() {
		verify(listener, times(0)).pause();
		app.exit();
		try {
			Thread.sleep(100);
		}
		catch (InterruptedException e) {
		}
		verify(listener, times(1)).pause();
	}

	@Test
	public void disposeIsNotInvoked() {
		verify(listener, times(0)).dispose();
		app.exit();
		try {
			Thread.sleep(100);
		}
		catch (InterruptedException e) {
		}

		verify(listener, times(1)).dispose();
	}

}
