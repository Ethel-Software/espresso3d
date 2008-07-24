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
import java.util.ArrayList;

import org.lwjgl.LWJGLException;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.exceptions.E3DInvalidDisplayModeException;
import espresso3d.engine.fileloaders.E3DSoundLoader;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.sound.E3DSound;
import espresso3d.engine.sound.E3DSound3D;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.window.viewport.image.E3DImageFixedSize;
import espresso3d.engine.window.viewport.image.E3DImageVariableSize;
import espresso3d.engine.window.viewport.text.E3DFontVariableWidth;
import espresso3d.engine.window.viewport.text.E3DMessage;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.actor.E3DCameraActor;
import espresso3d.engine.world.sector.light.E3DLight;
import espresso3d.engine.world.sector.particle.E3DSprite;
import espresso3d.testbed.actors.BallActor;
import espresso3d.testbed.actors.SquareActor;
import espresso3d.testbed.input.InputEvents;
import espresso3d.testbed.particlesystems.FireParticleSystem;

/**
 * @author espresso3d
 *
 * This class encapsulates a basic pong like game for two players.
 */
public class TestBed implements ITestBed {
	private E3DEngine engine;
	private E3DWorld world;
	private E3DViewport viewport;
	private InputEvents inputEvents;
	private E3DFontVariableWidth font;
	
	//Pong doesn't have many actors or anything, so we'll go ahead and store them in the main class.
	// In a large project, this isn't the best design choice...
	private SquareActor square = null;
	private BallActor cameraActor = null;
	
	private ArrayList balls = new ArrayList(); //In case we ever extend it to have multi-ball capabilities, keep a list of balls

	private int player1Score = 0;
	private int player2Score = 0;

    public E3DSound3D footsteps = null;
    
    public E3DSound3D getFootsteps() {
    	return footsteps;
    }
	/**
	 * The main class.  The programs entry point.
	 */
	public static void main(String[] args) {
	    TestBed testBed = new TestBed(); //Create an instance of the PongClone class
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
		initFonts();
		initImages();
		gameLoop(); //Begin the gameloop
	}

	private void initImages()
	{
		E3DImageFixedSize imageA = new E3DImageFixedSize(engine, "IMAGE1", "TESTIMG", 
				new E3DVector2I(200, 200), 100, 100, 50, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_BLEND));
		
//		viewport.addImage(imageA);
		
//		E3DImageFixedSize imageC = new E3DImageFixedSize(engine, "IMAGE2", "TESTIMG", 
//				new E3DVector2I(400, 400), 100, 100, 500, E3DBlendMode.BLENDMODE_BLEND);
//		
//		viewport.addImage(imageC);

		//TODO: FIXME: Animation loops doesn't work
		E3DImageVariableSize imageB = new E3DImageVariableSize(engine, "ANIMATEDIMAGE", 
	        													new E3DAnimatedTextureFrame[]{
		        													new E3DAnimatedTextureFrame(engine, "WALL", 2),
		        													new E3DAnimatedTextureFrame(engine, "FIRE", 2)},-1,
		        												new E3DVector2I(0,10), 25, 25, -1, 
		        												new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_BLEND));
//		viewport.addImage(imageB);	        												
        
        imageB = new E3DImageVariableSize(engine, "ANIMATEDIMAGE2", 
                new E3DAnimatedTextureFrame[]{
                    new E3DAnimatedTextureFrame(engine, "FIRE", 1),
                    new E3DAnimatedTextureFrame(engine, "WATER", 1)},-1,
                new E3DVector2I(50,50), 25, 25, -1, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_BLEND));
//        viewport.addImage(imageB);  
	}
	
	private void initFonts()
	{	
	    font = new E3DFontVariableWidth(engine, "TestFont", "FONTVERDANA", 16, 16);
	 
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
	    

	    E3DMessage message = viewport.getViewportPrinter().printToViewport("Espresso3D V0.5 TestBed", "TestFont", 10, new E3DVector4F(1.0, 0.0, 0.0, 1.0), 
	            new E3DVector2I(viewport.getWidth() / 2, viewport.getHeight() / 2), 
	            10, 
	            new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_BLEND));
	    E3DVector2I cursorPos = message.getCursorPositionAfterMessage();
	    
	    viewport.getViewportPrinter().printToViewport(" \nVariable Width Font Test\nAnother Line", "TestFont", 10, new E3DVector4F(1.0, 0.0, 0.0, 1.0), new E3DVector2I(viewport.getWidth() / 2, ((int)cursorPos.getY())), 10, new E3DBlendMode(getEngine(), E3DBlendMode.BLENDMODE_BLEND));
	    message = viewport.getViewportPrinter().printToViewport("Test Passed!", "TestFont", 10, new E3DVector4F(0.0, 1.0, 0.0, 1.0), new E3DVector2I(viewport.getWidth() / 2, viewport.getHeight() / 4), -1, new E3DBlendMode(getEngine(), E3DBlendMode.BLENDMODE_BLEND));
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
		
		//Preload all the potential actors that can be loaded from the mapfile
		//Preloading is a necessary step before the world is loaded.  Without preloading,
		//the world will have no idea how to load an actor that is in the mapfile
		world.addPreloadedParticleSystem("FIRE", new FireParticleSystem(engine));

		//Load the mapfile.  If there are actors in the mapfile, this must come after preloading the actors
		world.loadWorld("espresso3d//testbed//media//maps//pongroom.e3m");

		//Setup the engine's viewport.  There can be many of these in an engine.
		//  A viewport is required to see anything.  The viewport specifies its dimensions and
		//  which world gets rendered in it.
		viewport = new E3DViewport(engine, 0, 0, width/2, height/2, "viewport1"); //create the viewport starting at 0,0 and extending to width, height (the entire window)
		//viewport.setFovY(150);
		//		viewport.switchToOrthographicMode(-10, 10, 10, -10, 0.1, 100.0);
		engine.getWindow().addViewport(viewport); //add the viewport to the engine.
		viewport.setWorld(world); //set the world we want the viewport to render in it
		
		//Setup the engine's viewport.  There can be many of these in an engine.
		//  A viewport is required to see anything.  The viewport specifies its dimensions and
		//  which world gets rendered in it.
		E3DViewport viewport2 = new E3DViewport(engine, width/2, height/2, width/2, height/2, "viewport2"); //create the viewport starting at 0,0 and extending to width, height (the entire window)

		//viewport.setFovY(150);
		//		viewport.switchToOrthographicMode(-10, 10, 10, -10, 0.1, 100.0);
		E3DCameraActor camActor = new E3DCameraActor(engine, world, "camera2");
		viewport2.setCameraActor(camActor);
		world.getSector("sector0").addActor(camActor);
		engine.getWindow().addViewport(viewport2); //add the viewport to the engine.
		viewport2.setWorld(world); //set the world we want the viewport to render in it
		 

		E3DViewport viewportStats = new E3DViewport(engine, 0, height/2, width/2, height/2, "viewportStats"); //create the viewport starting at 0,0 and extending to width, height (the entire window)

		//viewport.setFovY(150);
		//		viewport.switchToOrthographicMode(-10, 10, 10, -10, 0.1, 100.0);
		camActor = new E3DCameraActor(engine, world, "camera3");
		camActor.setPosition(new E3DVector3F(100, 100, 100));
		viewportStats.setCameraActor(camActor);
		world.getSector("sector0").addActor(camActor);
		engine.getWindow().addViewport(viewportStats); //add the viewport to the engine.
		viewportStats.setWorld(world); //set the world we want the viewport to render in it
		
		
		//Bind all of our input handlers.  I do this in another class InputEvents
		inputEvents = new InputEvents(engine, world, this);
		inputEvents.bindInputEvents();
		
//        engine.setDebugNormalsRendered(true);
//		viewport.switchToOrthographicMode(-20,20, 20, -20, 0.1, 1000);
		//Build and set the skybox for the world
	//	world.setSkyObject(new E3DSkyBox(engine, "WALL", "WALL", "WALL", "WALL", "WALL", "WALL"));
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

//		cameraActor = new E3DCameraActor(engine, world, "camera");
        cameraActor = new BallActor(engine, world, "camera", 1);
        cameraActor.getMesh().setLit(false);
        cameraActor.getMesh().setRendered(false);
//        cameraActor.setRenderMode(E3DRenderMode.RENDERMODE_WIREFRAME);
        sector.addActor(cameraActor);
		viewport.setCameraActor(cameraActor);
        cameraActor.rotate(E3DConstants.PI, cameraActor.getOrientation().getUp());
//		cameraActor.setPosition(new E3DVector3F(0.0, -20.0, 0.0));
//		cameraActor.rotate(E3DConstants.PI, cameraActor.getOrientation().getForward());
        
        
		E3DLight light = new E3DLight(engine, "light1");
		light.setPosition(new E3DVector3F(-5.0, -5.0, -5.0));
		light.setBrightness(150);
		light.setColor(new E3DVector4F(1.0, 1.0, 1.0, 1.0));
		//		light.setFalloff(1);

/*		E3DLight light2 = new E3DLight(engine, "light2");
		light2.setPosition(new E3DVector3F(-5.0, -5.0, 0.0));
		light2.setBrightness(50);
		light2.setColor(new E3DVector3F(0.0, 1.0, 0.0));
//		light2.setFalloff(1000);

		E3DLight light3 = new E3DLight(engine, "light3");
		light3.setPosition(new E3DVector3F(0, 0.0, -20.0));
		light3.setBrightness(0.4);
		light3.setColor(new E3DVector3F(0.0, 0.0, 1.0));
//		light3.setFalloff(1000);
*/
		//Add the 3 lights to the world.
		sector.addLight(light);
//    	sector.addLight(light2);
	//	sector.addLight(light3);

/*		BallActor bactor = new BallActor(engine, world, sector, "ATEST", 0);
		bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
		bactor.scale(10);
		sector.addActor(bactor);*/
/*		BallActor bactor = new BallActor(engine, world, sector, "BTEST", 1);
		bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
		bactor.scale(10);
		bactor.setRenderMode(E3DRenderMode.RENDERMODE_WIREFRAME);
        bactor.rotate(E3DConstants.PI, bactor.getOrientation().getUp());
        sector.addActor(bactor);
        balls.add(bactor);

        bactor = new BallActor(engine, world, sector, "CTEST", 1);
		bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
		bactor.scale(10);
		bactor.translate(new E3DVector3F(0, 0, 10));
		bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
		sector.addActor(bactor);
        balls.add(bactor);

		bactor = new BallActor(engine, world, sector, "DTEST", 1);
		bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
		bactor.scale(10);
        bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
		bactor.translate(new E3DVector3F(0, 0, -10));
		sector.addActor(bactor);
        balls.add(bactor);

		bactor = new BallActor(engine, world, sector, "ETEST", 1);
		bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
		bactor.scale(10);
		bactor.translate(new E3DVector3F(0, 10, 0));
		sector.addActor(bactor);
        balls.add(bactor);
	*/	
		BallActor bactor = new BallActor(engine, world, "FTEST", 1);
//		bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
		bactor.scale(1);
		bactor.translate(new E3DVector3F(-10, 0, 0));
		sector.addActor(bactor);
        balls.add(bactor);

        bactor = new BallActor(engine, world, "FTEST1", 1);
//      bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
        bactor.scale(0.5);
        bactor.translate(new E3DVector3F(10, 0, 0));
        sector.addActor(bactor);        
        
//        bactor = new BallActor(engine, world, "FTEST2", 2);
////      bactor.setRenderMode(E3DRenderMode.RENDERMODE_TEXTURED);
//        bactor.scale(0.5);
//        bactor.translate(new E3DVector3F(10, -10, 0));
//        sector.addActor(bactor);        

        square = new SquareActor(engine, world, "SQUARE1");
        square.setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_WIREFRAME));
        square.rotate(E3DConstants.HALFPI, square.getOrientation().getUp());
        sector.addActor(square);
        
        class Sprite extends E3DSprite
        {
            public Sprite(E3DEngine engine, E3DVector3F position, double size, String textureName)
            {
                super(engine, position, size, textureName);
            }
            
            /* (non-Javadoc)
             * @see espresso3d.engine.collision.base.IE3DCollisionDetectableObject#isCollideable()
             */
            public boolean isCollideable() {
                // TODO Auto-generated method stub
                return false;
            }            
            /* (non-Javadoc)
             * @see espresso3d.engine.world.sector.particle.E3DBillboardSprite#onCollisionActor(espresso3d.engine.collision.E3DCollision)
             */
            public void onCollisionActor(E3DCollision collision) {
                // TODO Auto-generated method stub
            
            }/* (non-Javadoc)
             * @see espresso3d.engine.world.sector.particle.E3DBillboardSprite#onCollisionSprite(espresso3d.engine.collision.E3DCollision)
             */
            public void onCollisionSprite(E3DCollision collision) {
                // TODO Auto-generated method stub
            
              }            
        }
        E3DSprite sprite = new Sprite(engine, new E3DVector3F(20.0, 0.0, 0.0), 20, "ORANGE");
        sprite.rotate(E3DConstants.PI, sprite.getOrientation().getUp());
        sector.addSprite(sprite);
		//viewport.setRenderMode(E3DViewport.RENDERMODE_WIREFRAME);
		/*
		bactor = new BallActor(engine, world, sector, "DTEST", 0);
		bactor.setRenderMode(BallActor.RENDERMODE_TEXTURED);
		bactor.scale(10);
		sector.addActor(bactor);
*/
//		sector.addParticleSystem(new BluePlasmaWallParticleSystem(engine));
        sector.addParticleSystem(new FireParticleSystem(engine));
		
	//	sector.addSprite(new E3DBillboardSprite(engine, new E3DVector3F(0, 0, 0), 5, world.getTextureIDByName("WALL")));

        
	}
	
	/**
	 * The main game loop.  Moves the ball, checks for input, and renders the scene
	 * @throws Exception
	 */
	private void gameLoop() throws Exception
	{
        E3DSound sound = E3DSoundLoader.loadSoundIntoAL(engine, "TESTSOUND", E3DSoundLoader.loadSound("espresso3d/testbed/media/sounds/FootSteps.WAV"));
        engine.getSoundHandler().addSound(sound);
        footsteps = engine.getSoundHandler().loop3DSound("TESTSOUND", (E3DActor)balls.get(0),  1, 1, 10, 1000, 1);
   //     engine.getSoundHandler().loop2DSound("TESTSOUND", 1, 1, 10, 1000, 5);
        
        E3DViewport viewportStats = engine.getWindow().getViewport("viewportStats");
        E3DMessage message = viewportStats.getViewportPrinter().printToViewport("Leaves: ", "TestFont", 10, new E3DVector4F(1.0, 0.0, 0.0, 1.0), 
	            new E3DVector2I(viewportStats.getWidth() / 2, viewportStats.getHeight() / 2), 
	            10, new E3DBlendMode(getEngine(), E3DBlendMode.BLENDMODE_BLEND));
        
//		E3DVector3F translationVec = new E3DVector3F();
//		E3DCollision holdCollision = null;
        
		BallActor ball = (BallActor)balls.get(0);
		ball.getSkeleton().startAnimation("test", 1);
		
		engine.getWindow().setDisplayingFPS(true);
		resetGame(); //call reset to get everything to their starting positions

		while(engine.isRunning()) //isRunning is a variable you can set to false to stop the engine & gameloop
		{
//			E3DVector2I position = message.getCursorPositionAfterMessage();
			for(int i=0; i < balls.size(); i++ ) //Loop through all balls in the scene and move them
			{
				ball = (BallActor)balls.get(i);
				ball.move(); //This will make the ball move in its forward direction & check for collisions (see the method for more details)
			}
			
			engine.checkInput(); //Tell the engine to all our input callbacks that we've registered if any keys are pressed
			engine.render(); //Tell the engine to render the scene
		}
	}
	
	/**
	 * This resets the game (Balls & scores - not paddle positions)
	 * @throws Exception
	 */
	public void resetGame() throws Exception
	{
		//reset scores
	    updateScore(-player1Score, -player2Score);
		
		//remove all balls potentially in the world
	//	removeBalls();
		
		//Add a new ball
//		addBall(engine, world);
	}

	/**
	 * Add a ball to the game
	 * @param engine
	 * @param world
	 * @throws Exception
	 */
	public void addBall(E3DEngine engine, E3DWorld world) throws Exception
	{
		E3DSector sector = world.getSector("sector0");
		
		//Assume that putting size on the end of the name will make the name unique.  
		//Since we always remove all balls before adding (never one at a time).. 
		//If that changes, this will have to change		
		BallActor ball = new BallActor(engine, world, "ball" + balls.size(), 5);  //create the ball
		sector.addActor(ball); //Add the ball to the sector
		
		//Rotate the balls forward vector randomly.  I'm making sure not to set it to an even number
		// because if it was even, it could potentially just back back and forth between the walls and never
		// reach the paddles.  This isn't the perfect way, but it works for a simple demo.
		double rotAmt = (Math.random() * 10) * 36;
		if(rotAmt % 2 == 0)
		    rotAmt += 1;
		rotAmt = Math.toRadians(rotAmt);
		ball.rotate(rotAmt, new E3DVector3F(0.0, 1.0, 0.0));
		balls.add(ball); //add the ball to the ball list.
	}
	/**
	 * Remove all balls from the game
	 */
	public void removeBalls()
	{
		E3DSector sector0 = world.getSector("sector0");
		
		BallActor ball = null;
		for(int i=0; i<balls.size(); i++)
		{
			ball = (BallActor)balls.get(i);
			
			sector0.removeActor(ball.getActorID());
			balls.remove(i);
			i--;
		}
	}
	/**
	 * This is a game logic method that will add the ints to player1 and player2's scores.
	 * @param toAddPlayer1
	 * @param toAddPlayer2
	 */
	public void updateScore(int toAddPlayer1, int toAddPlayer2)
	{
		player1Score += toAddPlayer1;
		player2Score += toAddPlayer2;

		//Reset the title bar to show the score
	//	engine.setWindowTitle("Espresso3D Test Bench - Particles");
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
}
