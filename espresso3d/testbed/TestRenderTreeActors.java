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
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.exceptions.E3DInvalidDisplayModeException;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.sound.E3DSound3D;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.window.viewport.text.E3DFontVariableWidth;
import espresso3d.engine.window.viewport.text.E3DMessage;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DCameraActor;
import espresso3d.engine.world.sector.light.E3DLight;
import espresso3d.testbed.actors.BallActor;
import espresso3d.testbed.input.InputEvents;

/**
 * @author espresso3d
 *
 * This class encapsulates a basic pong like game for two players.
 */
public class TestRenderTreeActors implements ITestBed {
	
	private E3DEngine engine;
	private E3DWorld world;
	private E3DViewport viewport;
	private InputEvents inputEvents;
	
	//Pong doesn't have many actors or anything, so we'll go ahead and store them in the main class.
	// In a large project, this isn't the best design choice...
	private BallActor ball = null;
	private E3DCameraActor cameraActor = null;
	
	/**
	 * The main class.  The programs entry point.
	 */
	public static void main(String[] args) {
		TestRenderTreeActors testBed = new TestRenderTreeActors(); //Create an instance of the PongClone class
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
		engine.getLogger().setDebug(true);
		//engine.setBackfaceCullingEnabled(true);
        
		//Setup the world
		world = new E3DWorld(engine, "worldOne"); //create the world and give it an ID
		world.loadTextureSet("espresso3d\\testbed\\media\\textures\\textureseta.txt");

		//Add the world to the engine itself.  The world MUST be added to the engine
		// before preloading of particles occur, otherwise textures will not be found for
		// the particles.
		engine.addWorld(world);
		world.addSector(new E3DSector(engine, "sector0"));
		
		//Preload all the potential actors that can be loaded from the mapfile
		//Preloading is a necessary step before the world is loaded.  Without preloading,
		//the world will have no idea how to load an actor that is in the mapfile
//		world.addPreloadedParticleSystem("FIRE", new FireParticleSystem(engine));

		//Load the mapfile.  If there are actors in the mapfile, this must come after preloading the actors
//		world.loadWorld("espresso3d//testbed//media//maps//pongroom.e3m", false);

		//Setup the engine's viewport.  There can be many of these in an engine.
		//  A viewport is required to see anything.  The viewport specifies its dimensions and
		//  which world gets rendered in it.
		viewport = new E3DViewport(engine, 0, 0, width, height, "viewport1"); //create the viewport starting at 0,0 and extending to width, height (the entire window)
		engine.getWindow().addViewport(viewport); //add the viewport to the engine.
		viewport.setWorld(world); //set the world we want the viewport to render in it
		
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
		E3DSector sector = world.getSector("sector0");  //Grab our main sector.
//		E3DSector sector1 = world.getSector("sector1");  //Grab our main sector.
//		E3DSector sector2 = world.getSector("sector2");  //Grab our main sector.

		E3DViewport viewport = engine.getWindow().getViewport("viewport1"); //Grab the viewport

        cameraActor = new E3DCameraActor(engine, world, "camera");
        cameraActor.rotate(E3DConstants.PI, cameraActor.getOrientation().getUp());
        sector.addActor(cameraActor);

        viewport.setCameraActor(cameraActor);
        
		E3DLight light = new E3DLight(engine, "light1");
		light.setPosition(new E3DVector3F(-5.0, -5.0, -5.0));
		light.setBrightness(150);
		light.setColor(new E3DVector4F(1.0, 1.0, 1.0, 1.0));
		sector.addLight(light);

		ball = new BallActor(engine, world, "ball1", 1);
        sector.addActor(ball);

//        E3DQuad quad = new E3DQuad(engine, new E3DVector3F(0, 0, 0), new E3DVector3F(0, 100, 0), new E3DVector3F(100, 100, 0), new E3DVector3F(100, 0, 0),
//        							new E3DVector2F(0, 0), new E3DVector2F(0, 1), new E3DVector2F(1, 1), new E3DVector2F(1, 0), "ORANGE");
//        engine.addExternalRenderable(quad, 10000);
        

//		E3DParticleSystem particleSystem = new FireParticleSystem(engine);
//		sector.addParticleSystem(particleSystem);
		
		//		ball = new BallActor(engine, world, "ball2", 1);
//		ball.setPosition(new E3DVector3F(0, 10, 0));
////      sprite = new DoomBillboardSprite(engine, new E3DVector3F(0,0,0), 10, "ORANGE");
////      sector.addSprite(sprite);
//		sector.addActor(ball);

        //initFonts();
	}
	
	private void initFonts()
	{	
		E3DFontVariableWidth font = new E3DFontVariableWidth(engine, "TestFont", "FONTVERDANA", 16, 16);
	 
	    font.bindSymbolToTexturePosition(' ', 1, 1, 6);
	    font.bindSymbolToTexturePosition('!', 1, 2, 7);
	    font.bindSymbolToTexturePosition('"', 1, 3, 11);
	    font.bindSymbolToTexturePosition('#', 1, 4, 16);
	    font.bindSymbolToTexturePosition('$', 1, 5, 13);
	    font.bindSymbolToTexturePosition('%', 1, 6, 23);
	    font.bindSymbolToTexturePosition('&', 1, 7, 16);
	    font.bindSymbolToTexturePosition('\'', 1, 8, 6);
	    font.bindSymbolToTexturePosition('(', 1, 9, 10);
	    font.bindSymbolToTexturePosition(')', 1, 10, 10);
	    font.bindSymbolToTexturePosition('*', 1, 11, 13);
	    font.bindSymbolToTexturePosition('+', 1, 12, 16);
	    font.bindSymbolToTexturePosition(',', 1, 13, 7);
	    font.bindSymbolToTexturePosition('-', 1, 14, 8);
	    font.bindSymbolToTexturePosition('.', 1, 15, 7);
	    font.bindSymbolToTexturePosition('/', 1, 16, 12);

	    font.bindSymbolToTexturePosition('0', 2, 1, 13);
	    font.bindSymbolToTexturePosition('1', 2, 2, 13);
	    font.bindSymbolToTexturePosition('2', 2, 3, 13);
	    font.bindSymbolToTexturePosition('3', 2, 4, 13);
	    font.bindSymbolToTexturePosition('4', 2, 5, 13);
	    font.bindSymbolToTexturePosition('5', 2, 6, 13);
	    font.bindSymbolToTexturePosition('6', 2, 7, 13);
	    font.bindSymbolToTexturePosition('7', 2, 8, 13);
	    font.bindSymbolToTexturePosition('8', 2, 9, 13);
	    font.bindSymbolToTexturePosition('9', 2, 10, 13);
	    font.bindSymbolToTexturePosition(':', 2, 11, 7);
	    font.bindSymbolToTexturePosition(';', 2, 12, 7);
	    font.bindSymbolToTexturePosition('<', 2, 13, 16);
	    font.bindSymbolToTexturePosition('=', 2, 14, 16);
	    font.bindSymbolToTexturePosition('>', 2, 15, 16);
	    font.bindSymbolToTexturePosition('?', 2, 16, 11);
	    
	    font.bindSymbolToTexturePosition('@', 3, 1, 17);
	    font.bindSymbolToTexturePosition('A', 3, 2, 14);
	    font.bindSymbolToTexturePosition('B', 3, 3, 14);
	    font.bindSymbolToTexturePosition('C', 3, 4, 13);
	    font.bindSymbolToTexturePosition('D', 3, 5, 15);
	    font.bindSymbolToTexturePosition('E', 3, 6, 12);
	    font.bindSymbolToTexturePosition('F', 3, 7, 12);
	    font.bindSymbolToTexturePosition('G', 3, 8, 15);
	    font.bindSymbolToTexturePosition('H', 3, 9, 15);
	    font.bindSymbolToTexturePosition('I', 3, 10, 10);
	    font.bindSymbolToTexturePosition('J', 3, 11, 10);
	    font.bindSymbolToTexturePosition('K', 3, 12, 14);
	    font.bindSymbolToTexturePosition('L', 3, 13, 11);
	    font.bindSymbolToTexturePosition('M', 3, 14, 17);
	    font.bindSymbolToTexturePosition('N', 3, 15, 15);
	    font.bindSymbolToTexturePosition('O', 3, 16, 15);

	    font.bindSymbolToTexturePosition('P', 4, 1, 13);
	    font.bindSymbolToTexturePosition('Q', 4, 2, 15);
	    font.bindSymbolToTexturePosition('R', 4, 3, 14);
	    font.bindSymbolToTexturePosition('S', 4, 4, 13);
	    font.bindSymbolToTexturePosition('T', 4, 5, 12);
	    font.bindSymbolToTexturePosition('U', 4, 6, 15);
	    font.bindSymbolToTexturePosition('V', 4, 7, 14);
	    font.bindSymbolToTexturePosition('W', 4, 8, 20);
	    font.bindSymbolToTexturePosition('X', 4, 9, 14);
	    font.bindSymbolToTexturePosition('Y', 4, 10, 13);
	    font.bindSymbolToTexturePosition('Z', 4, 11, 12);
	    font.bindSymbolToTexturePosition('[', 4, 12, 10);
	    font.bindSymbolToTexturePosition('\\', 4, 13, 12);
	    font.bindSymbolToTexturePosition(']', 4, 14, 10);
	    font.bindSymbolToTexturePosition('^', 4, 15, 16);
	    font.bindSymbolToTexturePosition('_', 4, 16, 13);

	    font.bindSymbolToTexturePosition('`', 5, 1, 13);
	    font.bindSymbolToTexturePosition('a', 5, 2, 12);
	    font.bindSymbolToTexturePosition('b', 5, 3, 13);
	    font.bindSymbolToTexturePosition('c', 5, 4, 11);
	    font.bindSymbolToTexturePosition('d', 5, 5, 13);
	    font.bindSymbolToTexturePosition('e', 5, 6, 12);
	    font.bindSymbolToTexturePosition('f', 5, 7, 8);
	    font.bindSymbolToTexturePosition('g', 5, 8, 13);
	    font.bindSymbolToTexturePosition('h', 5, 9, 13);
	    font.bindSymbolToTexturePosition('i', 5, 10, 6);
	    font.bindSymbolToTexturePosition('j', 5, 11, 7);
	    font.bindSymbolToTexturePosition('k', 5, 12, 12);
	    font.bindSymbolToTexturePosition('l', 5, 13, 6);
	    font.bindSymbolToTexturePosition('m', 5, 14, 19);
	    font.bindSymbolToTexturePosition('n', 5, 15, 13);
	    font.bindSymbolToTexturePosition('o', 5, 16, 12);
	    
	    font.bindSymbolToTexturePosition('p', 6, 1, 13);
	    font.bindSymbolToTexturePosition('q', 6, 2, 13);
	    font.bindSymbolToTexturePosition('r', 6, 3, 9);
	    font.bindSymbolToTexturePosition('s', 6, 4, 11);
	    font.bindSymbolToTexturePosition('t', 6, 5, 8);
	    font.bindSymbolToTexturePosition('u', 6, 6, 13);
	    font.bindSymbolToTexturePosition('v', 6, 7, 12);
	    font.bindSymbolToTexturePosition('w', 6, 8, 18);
	    font.bindSymbolToTexturePosition('x', 6, 9, 12);
	    font.bindSymbolToTexturePosition('y', 6, 10, 12);
	    font.bindSymbolToTexturePosition('z', 6, 11, 11);
	    font.bindSymbolToTexturePosition('{', 6, 12, 13);
	    font.bindSymbolToTexturePosition('|', 6, 13, 10);
	    font.bindSymbolToTexturePosition('}', 6, 14, 13);
	    font.bindSymbolToTexturePosition('~', 6, 15, 16);
//	    font.bindSymbolToTexturePosition('~', 6, 18); Not a valid position for the font

/*	not using anymore of these
 *     font.bindSymbolToTexturePosition('`', 7, 1);
	    font.bindSymbolToTexturePosition('a', 7, 2);
	    font.bindSymbolToTexturePosition('b', 7, 3);
	    font.bindSymbolToTexturePosition('c', 7, 4);
	    font.bindSymbolToTexturePosition('d', 7, 5);
	    font.bindSymbolToTexturePosition('e', 7, 6);
	    font.bindSymbolToTexturePosition('f', 7, 7);
	    font.bindSymbolToTexturePosition('g', 7, 8);
	    font.bindSymbolToTexturePosition('h', 7, 9);
	    font.bindSymbolToTexturePosition('i', 7, 10);
	    font.bindSymbolToTexturePosition('j', 7, 11);
	    font.bindSymbolToTexturePosition('k', 7, 12);
	    font.bindSymbolToTexturePosition('l', 7, 13);
	    font.bindSymbolToTexturePosition('m', 7, 14);
	    font.bindSymbolToTexturePosition('n', 7, 15);
	    font.bindSymbolToTexturePosition('o', 7, 16);
*/	    
	    viewport.getWindow().addFont(font);
	    

	    E3DMessage message = viewport.getViewportPrinter().printToViewport("Espresso3D V0.3 TestBed", "TestFont", 10, new E3DVector4F(1.0, 0.0, 0.0, 1.0), 
	            new E3DVector2I(viewport.getWidth() / 2, viewport.getHeight() / 2), 
	            10, 
	            new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_BLEND));
	    E3DVector2I cursorPos = message.getCursorPositionAfterMessage();
	    
	    viewport.getViewportPrinter().printToViewport(" \nVariable Width Font Test\nAnother Line", "TestFont", 10, new E3DVector4F(1.0, 0.0, 0.0, 1.0), new E3DVector2I(viewport.getWidth() / 2, ((int)cursorPos.getY())), 10, new E3DBlendMode(getEngine(), E3DBlendMode.BLENDMODE_BLEND));
	    message = viewport.getViewportPrinter().printToViewport("Test Passed!", "TestFont", 10, new E3DVector4F(0.0, 1.0, 0.0, 1.0), new E3DVector2I(viewport.getWidth() / 2, viewport.getHeight() / 4), -1, new E3DBlendMode(getEngine(), E3DBlendMode.BLENDMODE_BLEND));
	}
	/**
	 * The main game loop.  Moves the ball, checks for input, and renders the scene
	 * @throws Exception
	 */
	private void gameLoop() throws Exception
	{
		engine.getWindow().setDisplayingFPS(true);
		int i=0;
		while(engine.isRunning()) //isRunning is a variable you can set to false to stop the engine & gameloop
		{
			if(i == 0)
				ball.setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_WIREFRAME));
			else if(i == 1000)
				ball.setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_SOLID));
			else if(i == 2000)
				ball.setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED));
			if(i == 3000)
				ball.setBlendMode(new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_BLEND));
			else if(i == 4000)
				ball.setBlendMode(new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
			else if(i >= 4999)
				i = -1;

			engine.checkInput(); //Tell the engine to all our input callbacks that we've registered if any keys are pressed
			engine.render(); //Tell the engine to render the scene
			i++;
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
