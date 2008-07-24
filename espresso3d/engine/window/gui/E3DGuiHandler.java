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
package espresso3d.engine.window.gui;
 
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.opengl.GL11;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.window.E3DWindow;
import espresso3d.engine.window.gui.window.E3DGuiWindow;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.world.sector.actor.E3DCameraActor;

public class E3DGuiHandler extends E3DRenderable
{
	private ArrayList guiWindows; //this is an ordered stack, last is rendered on top (ie: focused)
	private HashMap guiWindowMap;
	
	private E3DViewport viewport;
	private E3DCameraActor viewportCamera;
	private boolean windowChanged = true; //if window changes, viewport must change
	
	private E3DWindow window;
	
	public E3DGuiHandler(E3DEngine engine)
	{
		super(engine);
		
		guiWindows = new ArrayList();
		guiWindowMap = new HashMap();
	}
	
	public void render()
	{
		if(!isAnyGuiDisplaying())
			return;

		GL11.glPushMatrix();
			getViewport().switchToViewport();
			GL11.glLoadIdentity();
		
			for(int i=0; i < guiWindows.size(); i++)
			{
				E3DGuiWindow window = (E3DGuiWindow)guiWindows.get(i);
				window.render();
			}
		GL11.glPopMatrix();
	}
	
	public boolean isAnyGuiDisplaying()
	{
		return guiWindows != null && guiWindows.size() > 0;
	}

	public ArrayList getGuiWindows()
	{
		return guiWindows;
	}
	
	public void addGuiWindow(E3DGuiWindow window)
	{
		if(guiWindowMap.containsKey(window.getGuiWindowID()))
			return;
		
		window.setGuiHandler(this); //Make sure to set the handler!
		
		guiWindows.add(window);
		guiWindowMap.put(window.getGuiWindowID(), window);
		
		resetDepths();		
	}
	
	public void removeGuiWindow(E3DGuiWindow window)
	{
		if(!guiWindowMap.containsKey(window.getGuiWindowID()))
			return;
		
		guiWindows.remove(window);
		guiWindowMap.remove(window.getGuiWindowID());
	}
	
	public void setFocus(String guiWindowID)
	{
		if(guiWindowMap.containsKey(guiWindowID))
			setFocus((E3DGuiWindow)guiWindowMap.get(guiWindowID));
	}
	
	public void setFocus(E3DGuiWindow window)
	{
		guiWindows.remove(window);
		guiWindows.add(window); //put it on the top
		
		resetDepths();
	}

	//Resets their depth 
	private void resetDepths()
	{
		double depth = 1.0; //sets depths so the windows display in back if on front of list
		if(!isAnyGuiDisplaying())
			return;
		double depthDec = depth / guiWindows.size();

		E3DGuiWindow window = null;
		for(int i=0; i < guiWindows.size(); i++)
		{
			window = (E3DGuiWindow)guiWindows.get(i);
			if(i == 0)
				window.setDepth(depth);
			else
				window.setDepth(depth - (depthDec * i-1));
		}
	}
	private void updateViewport()
	{
		viewport = new E3DViewport(getEngine(), 0, 0, window.getWidth(), window.getHeight(), "GUIVIEWPORTFORWINDOW" + window.getWindowTitle()); //cover the entire viewport
		viewportCamera = new E3DCameraActor(getEngine(), null, "GUIVIEWPORTCAMERAACTOR");
		viewport.setCameraActor(viewportCamera);
	}

	public E3DWindow getWindow()
	{
		return window;
	}

	/**
	 * Internal use only
	 * @param window
	 */
	public void setWindow(E3DWindow window)
	{
		this.window = window;
	}

	public E3DViewport getViewport()
	{
		if(windowChanged)
		{
			updateViewport();
			windowChanged = false;
		}
		return viewport;
	}
}
