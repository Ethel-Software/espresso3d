/*
 * Created on Jul 17, 2004
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
package espresso3d.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import espresso3d.engine.collision.E3DCollisionHandler;
import espresso3d.engine.common.E3DFeatureChecker;
import espresso3d.engine.exceptions.E3DMissingInputHandlerException;
import espresso3d.engine.input.E3DInputHandler;
import espresso3d.engine.logger.E3DEngineLogger;
import espresso3d.engine.logo.E3DLogoRunner;
import espresso3d.engine.renderer.E3DExternalRenderable;
import espresso3d.engine.renderer.E3DGeometryRenderer;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.renderer.texture.E3DTexture;
import espresso3d.engine.sound.E3DSoundHandler;
import espresso3d.engine.timer.E3DFPSTimer;
import espresso3d.engine.window.E3DWindow;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.world.E3DWorld;

/**
 * @author espresso3d
 *
 * This is the main class for espresso3d.  The engine does all of the work.
 */
public final class E3DEngine extends E3DRenderable
{
	public static final String ENGINE_VERSION = "0.5";
    /**
     * This is always true except for debugging purposes during development.
     */
    public static final boolean IN_JAR = true;
    
    private E3DWindow window;
    
	private E3DViewport currentViewport; //holds the viewport that is currently being rendered to
	private E3DViewport primarySoundViewport; //holds the viewport used to get the actor for the listener position

    private E3DDebugFlags debugFlags = new E3DDebugFlags();
    
    //Worlds
	private HashMap worldMap = new HashMap(); //a hashmap of all the E3DWorld objects in the engine keyed on worldID string

	//External renderable list for non display-tree type rendering
	private ArrayList externalRenderables = new ArrayList(); //This is a list that allows external apps to add renderable items to be rendered after rendering the scene (not in the normal rendering loop)

	private boolean running = false;

    //Handlers///////
    //Collisions - inited in constructor
	private E3DCollisionHandler collisionHandler = null;
    //Input - inited in Init method
    private E3DInputHandler inputHandler = null;
    //Sound - inited in the Init method.
    private E3DSoundHandler soundHandler = null;
    //logger
    private E3DEngineLogger logger = null;
    //Feature checker
    private E3DFeatureChecker featureChecker = null;
    
    //Geometry renderer
    private E3DGeometryRenderer geometryRenderer = null;

	//FPS
	private E3DFPSTimer fpsTimer;
	
    private boolean backfaceCullingEnabled = false;

//    private E3DThreadPool threadPool;
    
	public E3DEngine()
	{
		super(null); //this is the root
		
		setCollisionHandler(new E3DCollisionHandler(this));
		
		fpsTimer = new E3DFPSTimer(this);

        //Initialize logger at start
        logger = new E3DEngineLogger(this);
        
        //Initialize a threadPool that the engine can use
//        threadPool = new E3DThreadPool(this, 10);
	}

	/**
	 * Builds a textureSet out of all the texture sets in all the worlds
	 *   If two worlds have the same entry name, the E3DTexture used for the last checked
	 *   world is used.
	 * @return
	 */
	public HashMap getTextureSet()
	{
		if(getWorldMap() == null || getWorldMap().entrySet() == null)
			return null;

		HashMap textureSet = new HashMap();
		E3DWorld world;

		
		Iterator it = getWorldMap().entrySet().iterator();
		Map.Entry entry = null;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			world = (E3DWorld)entry.getValue();
			
			textureSet.putAll(world.getTextureSet());
		}

		return textureSet;
	
	}

	/**
	 * Searches all worlds for this texture.  This returns
	 * the first instance of this textureName it encounters.
	 * It's faster to get the texture from the world its in if you know
	 * that ahead of time.
	 * @param textureName
	 * @return
	 */ 
	public E3DTexture getTexture(String textureName)
	{
		if(worldMap == null || worldMap.isEmpty())
			return null;

		E3DWorld world;
		E3DTexture texture;
		Iterator it = worldMap.entrySet().iterator();
		Map.Entry entry = null;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			world = (E3DWorld)entry.getValue();
			
			texture = world.getTexture(textureName);
			if(texture != null)
				return texture;
		}

		return null;
	}

	/**
	 * Gets the current working directory WITHOUT trailing slashes
	 * @return
	 */
	public String getWorkingDirectory()
	{
		return System.getProperty("user.dir");		
	}
	
	public void initEngine(int width, int height, int bitsPerPixel, boolean fullscreen, String windowTitle) throws Exception
	{
		initEngine(-1, -1, width, height, bitsPerPixel, fullscreen, windowTitle);
	}
	/**
	 * Create window and initialize OpenGL
	 */
	public void initEngine(int posX, int posY, int width, int height, int bitsPerPixel, boolean fullscreen, String windowTitle) throws Exception
	{
        logger.writeLine(E3DEngineLogger.SEVERITY_INFO, "Initializing Espresso3D");
        
        window = new E3DWindow(this, windowTitle, posX, posY, width, height, bitsPerPixel, fullscreen);
		if(!window.isInitialized())
			throw new Exception("Unable to initialize engine window (E3DWindow). Please check the logs.");
		
		initGL();

		//This must be done after the display is created
        featureChecker = new E3DFeatureChecker(this);
	    soundHandler = new E3DSoundHandler(this);
		inputHandler =  new E3DInputHandler(this);
        geometryRenderer = new E3DGeometryRenderer(this);
 
		setRunning(true);

		E3DLogoRunner.displayEngineLogo(this);	
	}
	
	private void initGL()
	{
        logger.writeLine(E3DEngineLogger.SEVERITY_INFO, "Initializing OpenGL");
        
		GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background  
        GL11.glClearDepth(1.0); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do
        GL11.glHint (GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);			// Set Perspective Calculations To Most Accurate
    }

	/**
	 * change the screen resolution
	 */		
	public void changeScreenResolution (int width, int height, int bitsPerPixel, boolean fullscreen, String windowTitle)
	{
		Display.destroy();
	    try {
	    	initEngine(width, height, bitsPerPixel, fullscreen, windowTitle);
	    }
	    catch(Exception e) 
		{
	    	e.printStackTrace();
	    	System.exit(0);
		}
	}
	


	/**
	 * -------Rendering Operations--------
	 */
	/**
	 *  Render the current world at a given position.  This is automatically called by the engine
	 */
	public void render()
	{
	    //Loop through each viewport
		// rendering the viewport's world at the viewport's cameraActors position/orientation
		Iterator viewPortIterator = window.getViewportMap().entrySet().iterator();
		Map.Entry viewPortEntry = null;
		
		//Loop through each viewport rendering the scene
		 
		//Be sure to clear the screen only once, and depth buffer for each viewport
		GL11.glClear (GL11.GL_COLOR_BUFFER_BIT );		// Clear Screen
		while(viewPortIterator.hasNext())
		{
			viewPortEntry = (Map.Entry)viewPortIterator.next();
			currentViewport = (E3DViewport)viewPortEntry.getValue();

            if(currentViewport == primarySoundViewport)
                soundHandler.setListener(currentViewport.getCameraActor());
            
            GL11.glPushMatrix();
			//Setup the viewport - switch to this viewport for rendering
			currentViewport.switchToViewport();
			
			//Render the world which in turn renders all sectors, actors, etc.
			E3DWorld curWorld = currentViewport.getWorld();
			if(curWorld != null) //Viewports don't necessarily have to have worlds, so skip the world rendering things in that case
			{
			    GL11.glPushMatrix(); //Each external renderable should be on its own stack so it can make GL calls and not affect other things
			    	curWorld.render();
			    GL11.glPopMatrix();
			}
            
			//****************External Renderables****************//
			//render the external renderables added by the users
			E3DExternalRenderable renderable = null;
			for(int i=0; i < externalRenderables.size(); i++)
			{
				renderable = (E3DExternalRenderable)externalRenderables.get(i);
				if(!renderable.update())
				{
				    externalRenderables.remove(i);
					i--;
				}
				else
				{
				    GL11.glPushMatrix(); //Each external renderable should be on its own stack so it can make GL calls and not affect other things
				    	renderable.getRenderable().render();
				    GL11.glPopMatrix();
				}
			}
			//****************2D Text and Image Rendering - Should be last to be on top of everythign else *************//
			//FIXME: TODO: There is a serious bug in projection/unprojection.
			// This fixes fonts by loading the identity matrix, but it doesn't
			// resolve the issue NOR does it resolve the portal projection issue (which is a much bigger deal).
			GL11.glPushMatrix();
			    currentViewport.render();
            GL11.glPopMatrix();

              
            GL11.glPopMatrix();
		} //End viewport list loop
		
		//Render the windows GUI stuff that is viewport independent
		GL11.glPushMatrix();
			window.getGuiHandler().render();
		GL11.glPopMatrix();
		
        Display.update();
		
		//************FPS Timer*********************//
		fpsTimer.update();
		window.update();

        /*** Sound Handler ******/
        soundHandler.discardOldSounds(); //clear out any stopped sound source from memory
	}
	
	public void checkInput() throws E3DMissingInputHandlerException
	{
		if(inputHandler == null)
			throw new E3DMissingInputHandlerException();
		
		getInputHandler().checkInput();
	}

	/**
	 * ------World Operations------
	 */

	/**
	 * Add a world to the engines world list
	 */
	public void addWorld(E3DWorld world)
	{
		world.setEngine(this);
		worldMap.put(world.getWorldID(), world);
	}
	
	/**
	 * Remove a world from the engine's world list
	 * @return
	 */
	public void removeWorld(String worldID)
	{
		if(worldMap.containsKey(worldID))
			worldMap.remove(worldID);
	}

	/**
	 * Remove all the worlds in the engine.
	 *
	 */
	public void removeAllWorlds()
	{
		worldMap.clear();
	}
	
	/**
	 * Get a world from the map of worlds by its ID
	 * @param worldID
	 * @return
	 */
	public E3DWorld getWorld(String worldID)
	{
		if(getWorldMap().containsKey(worldID))
			return (E3DWorld)getWorldMap().get(worldID);
		else
			return null;
	}
	
	/**
	 * @return Returns the worldMap.
	 */
	public HashMap getWorldMap() {
		return worldMap;
	}	
	
	/**
	 * @param inputHandler The inputHandler to set.
	 */
	public void setInputHandler(E3DInputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}

	/**
	 * @return Returns the inputHandler.
	 */
	public E3DInputHandler getInputHandler() {
		return inputHandler;
	}

	/**
	 * @return Returns whether the engine is running
	 */
	public boolean isRunning() {
		return (running && !Display.isCloseRequested());
	}
	
	/**
	 * @param running Set the engines running state
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @return Returns the engine's  used for determining collisions
	 */
	public E3DCollisionHandler getCollisionHandler() {
		return collisionHandler;
	}

	/**
	 * @param collisionDetect The collisionDetect to set.  This is private to the engine
	 */
	private void setCollisionHandler(E3DCollisionHandler collisionHandler) {
		this.collisionHandler = collisionHandler;
	}

	/**
	 * @return Returns the externalRenderables list.  This is private to the negine
	 */
	private ArrayList getExternalRenderables() {
		return externalRenderables;
	}
	
	/**
	 * This adds an E3DRenderable item to the ExternalRenderable list.  
	 *   All E3DRenderables in this list will be rendered after the main
	 *  rendering hierarchy is rendered  (world, actors, particles, etc) but before any 2D static images or text (so the text/images will be on top of external renderables)
	 * Each item on the list will be rendered for the # of frames passed in 
	 * This is very useful for debugging (adding CMLines to the rendering loop to see direction vectors and things)
	 * It could even potentially be used to plug in your rendering algorithm.
	 * as timeoutFrames
	 * @param renderableItem An E3DRenderable item that you want to render outside of the optimized rendering loop.
	 * @param timeoutFrames How many frames to render this renderable for
	 */
	public void addExternalRenderable(E3DRenderable renderableItem, int timeoutFrames)
	{
		getExternalRenderables().add(new E3DExternalRenderable(renderableItem, timeoutFrames));
	}
	
	public void removeExternalRenderable(E3DRenderable renderableItem)
	{
	    getExternalRenderables().remove(renderableItem);
	}
	
    /**
     * A primary viewport for sound must be set.  The cameraActor in the viewport this is set to
     * will be used as the listener for the sound.  This automatically sets up the soundHandler's
     * listener.  The viewport's camera actor must NOT be null at this point.
     * 
     * @param viewport
     */
    public void setPrimarySoundViewport(E3DViewport viewport)
    {
        if(viewport == null)
            primarySoundViewport = null;
        else
        {
            primarySoundViewport = viewport;
            soundHandler.setListener(viewport.getCameraActor());
        }
    }
    


/*
 * 	 * Todo: Move these to a window when it supports multiple windows
 */
	/**
	 * Returns the timer the engine uses to get its FPS.
	 * @return
	 */
	public E3DFPSTimer getFpsTimer() {
		return fpsTimer;
	}
	
	/**
	 * Returns the viewport the engine is currently rendering to.  This cannot
	 * be set by the user, it is for informational purposes only
	 * @return
	 */
	public E3DViewport getCurrentViewport()
	{
		return currentViewport;
	}
	
    public boolean isBackfaceCullingEnabled() {
        return backfaceCullingEnabled;
    }

    public void setBackfaceCullingEnabled(boolean backfaceCullingEnabled) {
        this.backfaceCullingEnabled = backfaceCullingEnabled;
        if(backfaceCullingEnabled)
        {
            GL11.glCullFace(GL11.GL_BACK);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        else
            GL11.glDisable(GL11.GL_CULL_FACE);
    }
    public E3DSoundHandler getSoundHandler() {
        return soundHandler;
    }
    public E3DViewport getPrimarySoundViewport() {
        return primarySoundViewport;
    }
    public E3DGeometryRenderer getGeometryRenderer() {
        return geometryRenderer;
    }
    public E3DEngineLogger getLogger() {
        return logger;
    }
    public E3DFeatureChecker getFeatureChecker() {
        return featureChecker;
    }

	public E3DWindow getWindow() {
		return window;
	}

    public E3DDebugFlags getDebugFlags() {
        return debugFlags;
    }
}
