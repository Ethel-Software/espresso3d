/*
 * Created on Jul 24, 2004
 *
 
   	Copyright 2004 Curtis Moxley
   	This file is part of Espresso3D.
 
   	Espresso3D is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Espresso3D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Espresso3D.  If not, see <http://www.gnu.org/licenses/>.
 */
package espresso3d.testbed.input;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.input.E3DInputHandler;
import espresso3d.engine.world.E3DWorld;
import espresso3d.testbed.ITestBed;
/**
 * @author espresso3d
 *
 * This class binds keys to callback classes.
 */
public class InputEvents {
	private E3DEngine engine;
	private E3DWorld world;
	private ITestBed testBed;

	public InputEvents(E3DEngine engine, E3DWorld world, ITestBed testBed)
	{
		setEngine(engine);
		setWorld(world);
		setTestBed(testBed);
	}
	
	/**
	 * This binds keys to handlers.  When the key is pressed, the handler is called.
	 * Multiple keys can go the the same handler.  The handler will get a list of all the keys being
	 * pressed when it is called.
	 *
	 */
	public void bindInputEvents()
	{
		//Multiple handlers for this game is overkill, but its a good example of the ability
		EscapeHandler escapeHandler = new EscapeHandler(engine, getTestBed());//Create an input handler just for the escape key
		MovementHandler movementHandler = new MovementHandler(engine, world); //Create an input handler that handles all the paddle movement related keys
		
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_ESCAPE, escapeHandler); //Tell the engine what class to call when the escape key is pressed
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_LEFT, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_RIGHT, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_A, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_D, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_W, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_S, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_UP, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_DOWN, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_L, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_J, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_Q, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_Z, movementHandler);
		engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_R, escapeHandler);

        engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_0, escapeHandler);
        engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_9, escapeHandler);
        engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_8, escapeHandler);
        engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_7, escapeHandler);
        engine.getInputHandler().bindKeyToHandler(E3DInputHandler.KEY_P, escapeHandler);
        
		engine.getInputHandler().bindMouseButtonToHandler(E3DInputHandler.MOUSE_BUTTON_LEFT, escapeHandler);
		engine.getInputHandler().bindMouseButtonToHandler(E3DInputHandler.MOUSE_BUTTON_RIGHT, escapeHandler);
		engine.getInputHandler().bindMouseButtonToHandler(E3DInputHandler.MOUSE_BUTTON_MIDDLE, escapeHandler);

		engine.getInputHandler().bindMouseMovementToHandler(escapeHandler);
		
	}
	
	/**
	 * @return Returns the engine.
	 */
	public E3DEngine getEngine() {
		return engine;
	}
	/**
	 * @param engine The engine to set.
	 */
	public void setEngine(E3DEngine engine) {
		this.engine = engine;
	}
	/**
	 * @return Returns the world.
	 */
	public E3DWorld getWorld() {
		return world;
	}
	/**
	 * @param world The world to set.
	 */
	public void setWorld(E3DWorld world) {
		this.world = world;
	}
    public ITestBed getTestBed() {
        return testBed;
    }
    public void setTestBed(ITestBed testBed) {
        this.testBed = testBed;
    }
}