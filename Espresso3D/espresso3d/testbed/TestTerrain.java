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
package espresso3d.testbed;
import org.lwjgl.LWJGLException;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.exceptions.E3DInvalidDisplayModeException;
import espresso3d.engine.fileloaders.E3DTerrainLoader;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.sound.E3DSound3D;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DCameraActor;
import espresso3d.engine.world.sector.light.E3DLight;
import espresso3d.engine.world.sector.terrain.E3DTerrain;
import espresso3d.testbed.actors.BallActor;
import espresso3d.testbed.input.InputEvents;

/**
 * @author espresso3d
 *
 * This class encapsulates a basic pong like game for two players.
 */
public class TestTerrain implements ITestBed {
	
	private E3DEngine engine;
	private E3DWorld world;
	private E3DViewport viewport;
	private InputEvents inputEvents;
	
	//Pong doesn't have many actors or anything, so we'll go ahead and store them in the main class.
	// In a large project, this isn't the best design choice...
	private E3DCameraActor cameraActor = null;

	private BallActor animatedBall = null;
	/**
	 * The main class.  The programs entry point.
	 */
	public static void main(String[] args) {
		TestTerrain testBed = new TestTerrain(); //Create an instance of the PongClone class
		try{
			testBed.startTest(); //Start the tests
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Entry point into the PongCloen class.  This is how we start the game.
	 * @throws Exception
	 */
	public void startTest() throws Exception
	{
		engine = new E3DEngine();

		initGame(); //Initialize the engine and add the world
		addActors(); //Add any actor that isn't in the mapfile programmatically to the world
		gameLoop(); //Begin the gameloop
	}

	/**
	 * This initializes the engine and variables required by the engine.
	 * @throws E3DInvalidDisplayModeException
	 * @throws LWJGLException
	 * @throws Exception
	 */
	private void initGame() throws E3DInvalidDisplayModeException, LWJGLException, Exception
	{
		//Initialize the engine
		engine.initEngine(width, height, 32, false, "Espresso3D TestBed");
		//engine.setBackfaceCullingEnabled(true);
        
		//Setup the world
		world = new E3DWorld(engine, "worldOne"); //create the world and give it an ID
		world.loadTextureSet("espresso3d\\testbed\\media\\textures\\textureseta.txt");

		//Add the world to the engine itself.  The world MUST be added to the engine
		// before preloading of particles occur, otherwise textures will not be found for
		// the particles.
		engine.addWorld(world);
		

		//Load the mapfile.  If there are actors in the mapfile, this must come after preloading the actors
		world.loadWorld("espresso3d//testbed//media//maps//pongroom.e3m");

		
		E3DTerrain terrain = E3DTerrainLoader.loadTerrain(engine, "espresso3d\\testbed\\media\\textures\\heightmap.png");
//		terrain.setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_WIREFRAME));
        terrain.setLit(false);
		world.addSector(terrain);
		
		//Setup the engine's viewport.  There can be many of these in an engine.
		//  A viewport is required to see anything.  The viewport specifies its dimensions and
		//  which world gets rendered in it.
		viewport = new E3DViewport(engine, 0, 0, width, height, "viewport1"); //create the viewport starting at 0,0 and extending to width, height (the entire window)
		engine.getWindow().addViewport(viewport); //add the viewport to the engine.
		viewport.setWorld(world); //set the world we want the viewport to render in it
		
//		viewport.setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_WIREFRAME));
		
		//Bind all of our input handlers.  I do this in another class InputEvents
		inputEvents = new InputEvents(engine, world, this);
		inputEvents.bindInputEvents();
	}
	
	/**
	 * This adds any actor to the world that we are adding programmatically
	 * Currently lights must be loaded this way.  That will change in later versions.
	 * @throws Exception
	 */
	public void addActors() throws Exception
	{
		E3DSector sector = world.getSector("terrain1");  //Grab our main sector.
//		E3DSector sector1 = world.getSector("sector1");  //Grab our main sector.
//		E3DSector sector2 = world.getSector("sector2");  //Grab our main sector.

		E3DViewport viewport = engine.getWindow().getViewport("viewport1"); //Grab the viewport
		viewport.setFarClipPlane(10000);
//		viewport.setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_WIREFRAME));
		
		engine.getDebugFlags().setDebugNormalsRendered(true);
        engine.getDebugFlags().setDebugRenderPortals(true);
        
        cameraActor = new E3DCameraActor(engine, world, "camera");
        sector.addActor(cameraActor);

        viewport.setCameraActor(cameraActor);
        
		E3DLight light = new E3DLight(engine, "light1");
		light.setPosition(new E3DVector3F(-5.0, -5.0, -5.0));
		light.setBrightness(150);
		light.setColor(new E3DVector4F(1.0, 1.0, 1.0, 1.0));
		sector.addLight(light);
	}
	
	/**
	 * The main game loop.  Moves the ball, checks for input, and renders the scene
	 * @throws Exception
	 */
	private void gameLoop() throws Exception
	{
		engine.getWindow().setDisplayingFPS(true);

		while(engine.isRunning()) //isRunning is a variable you can set to false to stop the engine & gameloop
		{
			engine.checkInput(); //Tell the engine to all our input callbacks that we've registered if any keys are pressed
			engine.render(); //Tell the engine to render the scene
		}
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
	 * @return Returns the inputEvents.
	 */
	public InputEvents getInputEvents() {
		return inputEvents;
	}
	/**
	 * @param inputEvents The inputEvents to set.
	 */
	public void setInputEvents(InputEvents inputEvents) {
		this.inputEvents = inputEvents;
	}

	/**
	 * @return
	 */
	public E3DWorld getWorld() {
		return world;
	}
	/**
	 * @param world
	 */	
	public void setWorld(E3DWorld world) {
		this.world = world;
	}
	public E3DSound3D getFootsteps() {
		return null;
	}
}
