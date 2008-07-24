/*
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
 * 
 */
package espresso3d.engine.window;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.exceptions.E3DInvalidDisplayModeException;
import espresso3d.engine.logger.E3DEngineLogger;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.window.gui.E3DGuiHandler;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.window.viewport.text.E3DFont;

public class E3DWindow extends E3DEngineItem 
{
	//Viewports for the engine
	private HashMap viewportMap;
    private HashMap fontMap;
    
	private String windowTitle = "";
	private boolean displayingFPS = false;
	private int width, height, bitsPerPixel;
	private boolean fullscreen;
	private boolean initialized = false;
	private int frameCountBetweenUpdate = 0;
	
	//For GUI in the window
	private E3DGuiHandler guiHandler;
	
	E3DVector2I position;
	
	public E3DWindow(E3DEngine engine, String windowTitle, int posX, int posY, int width, int height, int bitsPerPixel, boolean fullscreen)
	{
		super(engine);
		viewportMap = new HashMap();
		this.windowTitle = windowTitle;
		this.width = width;
		this.height = height;
		this.bitsPerPixel = bitsPerPixel;
		this.fullscreen = fullscreen;
        fontMap = new HashMap();
        
        position = new E3DVector2I(posX, posY);
        
        setGuiHandler(new E3DGuiHandler(engine));
        
		try{
			initWindow();
			initialized = true;
		}catch(Exception e){
			engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_ERROR, "Unable to initialize window: " + e.getMessage());
		}
	}
	
	public E3DWindow(E3DEngine engine, String windowTitle, int width, int height, int bitsPerPixel, boolean fullscreen)
	{
		this(engine, windowTitle, -1, -1, width, height, bitsPerPixel, fullscreen);
	}

	private void initWindow() throws LWJGLException, E3DInvalidDisplayModeException
	{
        getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Initializing Window");
		
		DisplayMode d[] = Display.getAvailableDisplayModes();
		int mode = -1;
		//iterate through available display modes choosing the matching one
		for (int i = 0; i < d.length; i++) 
		{
			if (d[i].getWidth() == width
				&& d[i].getHeight() == height
				&& d[i].getBitsPerPixel() == bitsPerPixel) 
			{
				mode = i;
				break;
			}
		}
		if(mode == -1) //didn't find a mode match
        {
            getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_ERROR, "An invalid display mode was selected.");
            throw new E3DInvalidDisplayModeException();
        }
        
		try
        {
			Display.setDisplayMode(d[mode]);
			Display.setFullscreen(fullscreen);
			if(position.getX() >= 0 && position.getY() >= 0)
				Display.setLocation(position.getX(), position.getY());
			Display.create(new PixelFormat(0, 8, 0));
			Display.setTitle(windowTitle);
		}catch(LWJGLException e){
	    	throw e;
		}		
	}
	
	/**
	 * @return Returns the windowTitle.
	 */
	public String getWindowTitle() {
		return windowTitle;
	}
	/**
	 * @param windowTitle The windowTitle to set.
	 */
	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
		Display.setTitle(windowTitle);
	}
	

	/**
	 * Set whether the engine should display the FPS time or not.
	 * Currently it shows the FPS in the window's title.
	 * @param displayingFPS
	 */
	public void setDisplayingFPS(boolean displayingFPS) {
		if(!displayingFPS)
			Display.setTitle(windowTitle);
		
		this.displayingFPS = displayingFPS;
	}
	/**
	 * @return Returns the displayingFPS.  Private to the engine
	 */
	public boolean isDisplayingFPS() {
		return displayingFPS;
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	public void update()
	{
		if(isDisplayingFPS())
		{
			if(frameCountBetweenUpdate > 60)
			{
				Display.setTitle(getWindowTitle() + " - " + getEngine().getFpsTimer().getFPS());
				frameCountBetweenUpdate = 0;
			}
			else
				frameCountBetweenUpdate+= 1;
		}
	}

	public int getBitsPerPixel() {
		return bitsPerPixel;
	}

	public int getFrameCountBetweenUpdate() {
		return frameCountBetweenUpdate;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
	
	/**
	 * @return Returns the viewportMap.
	 */
	public HashMap getViewportMap() {
		return viewportMap;
	}

	public E3DViewport getViewport(String viewportID)
	{
		if(viewportMap.containsKey(viewportID))
			return (E3DViewport)viewportMap.get(viewportID);
		return null;
	}
	/**
	 * Add a new viewport to the list of viewports
     * If the primarySoundViewport hasn't been set, it will automatically be set
     * to this viewport.
	 * @param viewport
	 */
	public void addViewport(E3DViewport viewport)
	{
		viewport.setWindow(this);
		viewportMap.put(viewport.getViewportID(), viewport);
        if(getEngine().getPrimarySoundViewport() == null)
        	getEngine().setPrimarySoundViewport(viewport);
	}
	
	/**
	 * Remove a viewport with viewportID from the engine 
     * If removing a viewport that is set to the primarySoundViewport,
     * the primarySoundViewport will automatically be set
     * to the first viewport in the viewportMap.  If the map is empty
     * after removing this viewport, the primarySoundViewport will be set to null.
	 * @param viewportID
	 */
	public void removeViewport(String viewportID)
	{
		if(viewportMap.containsKey(viewportID))
        {
            E3DViewport viewport = (E3DViewport)viewportMap.get(viewportID);
			viewportMap.remove(viewportID);
            E3DViewport primarySoundViewport = getEngine().getPrimarySoundViewport();
            if(viewport == primarySoundViewport)
            {
                Iterator it = viewportMap.entrySet().iterator();
                Map.Entry entry = null;
                if(it.hasNext())
                {
                    entry = (Map.Entry)it.next();
                    primarySoundViewport = (E3DViewport)entry.getValue();
                }
                else
                    primarySoundViewport = null;
            }
        }
	}
	
	/**
	 * Remove all viewports from the engine
	 *
	 */
	public void removeAllViewports()
	{
		viewportMap.clear();
		getEngine().setPrimarySoundViewport(null);
	}
	
    /**
     * Add (register) a font to the viewport printer.  
     * @param font
     */
    public void addFont(E3DFont font)
    {
        if(font != null)
            fontMap.put(font.getFontID(), font);
    }
    
    /**
     * Get a registered font object from the viewportprinter named fontID
     * @param fontID ID of the font registered in the viewportPrinter
     * @return
     */
    public E3DFont getFont(String fontID)
    {
        if(fontMap.containsKey(fontID))
            return (E3DFont)fontMap.get(fontID);
        return null;
    }
    
    /**
     * Remove a font from the viewportprinter
     * @param fontID Unique ID of the font to remove
     */
    public void removeFont(String fontID)
    {
        if(fontMap.containsKey(fontID))
            fontMap.remove(fontID);
    }

	public HashMap getFontMap() {
		return fontMap;
	}

	public E3DGuiHandler getGuiHandler()
	{
		return guiHandler;
	}

	private void setGuiHandler(E3DGuiHandler guiHandler)
	{
		this.guiHandler = guiHandler;
        this.guiHandler.setWindow(this);
	}

	/**
	 * Set the position of the upper left corner of the window on the screen. (0,0) is the top left corner
	 * @param position
	 */
	public void setPosition(E3DVector2I position)
	{
		setPosition(position.getX(), position.getY());
	}

	public void setPosition(int x, int y)
	{
		Display.setLocation(0, 500);
	}

}
