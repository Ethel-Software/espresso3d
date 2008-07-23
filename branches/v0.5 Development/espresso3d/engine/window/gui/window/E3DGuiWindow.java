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
package espresso3d.engine.window.gui.window;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.window.gui.E3DGuiHandler;
import espresso3d.engine.window.gui.base.E3DGuiConstants;
import espresso3d.engine.window.gui.base.E3DGuiObject;
import espresso3d.engine.window.gui.window.widgets.button.E3DGuiButton;
import espresso3d.engine.window.gui.window.widgets.textbox.E3DGuiTextBox;
import espresso3d.engine.window.viewport.E3DViewport;
  
/**
 * E3DGuiWindow - This is a basic window (or dialog) that is displayed that will
 * be filled with various elements such as textboxes, buttons, etc.
 * 
 * @author curt
 * 
 */
public abstract class E3DGuiWindow extends E3DGuiObject
{
	private E3DGuiHandler guiHandler;
	
	private String guiWindowID;
	private String title;
	private E3DViewport guiViewport;
	private double depth = 0.5;
	
	//Items in the window
	private ArrayList buttons;
	private ArrayList textBoxes;

	
	public E3DGuiWindow(E3DEngine engine,
			String guiWindowID, String title, int x, int y, E3DRenderMode renderMode, E3DBlendMode blendMode)
	{
		this(engine, guiWindowID, title, x, y, renderMode,
				blendMode, null);
	}

	public E3DGuiWindow(E3DEngine engine,
			String guiWindowID, String title, int x, int y, E3DRenderMode renderMode, E3DBlendMode blendMode,
			String textureName)
	{
		this(engine, guiWindowID, title, new E3DVector2I(x, y), renderMode, blendMode,
				textureName);
	}

	public E3DGuiWindow(E3DEngine engine,
			String guiWindowID, String title, int x, int y, E3DVector4F color, E3DRenderMode renderMode, E3DBlendMode blendMode)
	{
		this(engine, guiWindowID, title, new E3DVector2I(x, y), renderMode, blendMode,
				color);
	}
	/**
	 * This is used for creating a window without a texture and just a bgcolor
	 * 
	 * @param handler
	 * @param window
	 * @param title
	 * @param min
	 * @param size
	 * @param renderMode
	 * @param blendMode
	 * @param color
	 */
	public E3DGuiWindow(E3DEngine engine, 
			String guiWindowID, String title, E3DVector2I position,
			E3DRenderMode renderMode, E3DBlendMode blendMode,
			E3DVector4F color)
	{
		super(engine, position, color, renderMode, blendMode, null);
		init(guiWindowID, title);
	}

	/**
	 * Used to create a window with a background texture
	 * 
	 * @param handler
	 * @param window
	 * @param title
	 * @param min
	 * @param size
	 * @param renderMode
	 * @param blendMode
	 * @param textureName
	 */
	public E3DGuiWindow(E3DEngine engine,
			String guiWindowID, String title, E3DVector2I position,
			E3DRenderMode renderMode, E3DBlendMode blendMode,
			String textureName)
	{
		super(engine, position, E3DGuiConstants.DEFAULT_COLOR_WINDOW, renderMode, blendMode, textureName);
		init(guiWindowID, title);
	}

	/**
	 * Used to createa window with an animated texture
	 * 
	 * @param handler
	 * @param window
	 * @param guiWindowID
	 * @param title
	 * @param min
	 * @param size
	 * @param renderMode
	 * @param blendMode
	 * @param textureFrames
	 * @param loops
	 */
	public E3DGuiWindow(E3DEngine engine,String guiWindowID, String title,
			E3DVector2I position, E3DRenderMode renderMode,
			E3DBlendMode blendMode, E3DAnimatedTextureFrame[] textureFrames,
			int loops)
	{
		super(engine, position, E3DGuiConstants.DEFAULT_COLOR_WINDOW, renderMode, blendMode, textureFrames, loops);
		init(guiWindowID, title);
	}

	public void init(String guiWindowID, String title)
	{
		this.guiWindowID = guiWindowID;
		this.title = title;
	}

	public void render()
	{
		GL11.glPushMatrix();
			getEngine().getGeometryRenderer().renderQuad(getDisplayQuad(), getRenderMode().getRenderMode());
			
			//Render textboxes
			if(textBoxes != null)
			{
				E3DGuiTextBox textBox = null;
				for(int i=0; i < textBoxes.size(); i++)
				{
					textBox = (E3DGuiTextBox)textBoxes.get(i);
					textBox.render();
				}
			}			

			//render buttons
			if(buttons != null)
			{
				E3DGuiButton button = null;
				for(int i=0; i < buttons.size(); i++)
				{
					button = (E3DGuiButton)buttons.get(i);
					button.render();
				}
			}			
			
		GL11.glPopMatrix();
	}	

	public void onFocusSet()
	{
		// TODO Auto-generated method stub
	}

	public void onLeftMouseDoubleClick()
	{
		// TODO Auto-generated method stub
	}

	public void onLeftMouseDown()
	{
		// TODO Auto-generated method stub
	}

	public void onLeftMouseUp()
	{
		// TODO Auto-generated method stub
	}

	public void onMiddleDoubleClick()
	{
		// TODO Auto-generated method stub
	}

	public void onMiddleMouseDown()
	{
		// TODO Auto-generated method stub
	}

	public void onMiddleMouseUp()
	{
		// TODO Auto-generated method stub
	}

	public void onRightMouseDoubleClick()
	{
		// TODO Auto-generated method stub
	}

	public void onRightMouseDown()
	{
		// TODO Auto-generated method stub
	}

	public void onRightMouseUp()
	{
		// TODO Auto-generated method stub
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public ArrayList getButtons()
	{
		return buttons;
	}

	public void addButton(E3DGuiButton button)
	{
		buttons.add(button);
	}

	public void removeButton(E3DGuiButton button)
	{
		buttons.remove(button);
	}

	public void removeAllButtons()
	{
		buttons.clear();
	}

	public ArrayList getTextBoxes()
	{
		return textBoxes;
	}

	public void addTextBox(E3DGuiTextBox textBox)
	{
		textBoxes.add(textBox);
	}

	public void removeTextBox(E3DGuiTextBox textBox)
	{
		textBoxes.remove(textBox);
	}

	public void removeAllTextBoxes()
	{
		textBoxes.clear();
	}

	public String getGuiWindowID()
	{
		return guiWindowID;
	}
	
	/**
	 * Used by the system when a window is added to the handler
	 * @param guiHandler
	 */
	public void setGuiHandler(E3DGuiHandler guiHandler)
	{
		this.guiHandler = guiHandler;
	}

	public E3DGuiHandler getGuiHandler()
	{
		return guiHandler;
	}

	public E3DViewport getGuiViewport()
	{
		return guiViewport;
	}



	public double getDepth()
	{
		return depth;
	}

	public void setDepth(double depth)
	{
		this.depth = depth;
	}
}
