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
package espresso3d.engine.window.gui.window.widgets.button;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.window.gui.base.E3DGuiConstants;
import espresso3d.engine.window.gui.window.E3DGuiWindow;
import espresso3d.engine.window.gui.window.widgets.base.E3DGuiWidget;
 

public abstract class E3DGuiButton extends E3DGuiWidget 
{
	private String guiButtonID;
	private String buttonText;
	
	public E3DGuiButton(E3DEngine engine, E3DGuiWindow guiWindow, String guiButtonID,  String buttonText, E3DVector2I position, E3DRenderMode renderMode, E3DBlendMode blendMode)
	{
		this(engine, guiWindow, guiButtonID, buttonText, position, renderMode, blendMode, null);
	}
	
	public E3DGuiButton(E3DEngine engine, E3DGuiWindow guiWindow, String guiButtonID, String buttonText, E3DVector2I position, E3DRenderMode renderMode, E3DBlendMode blendMode,
			 String textureName)
	{
		super(engine, guiWindow, position, E3DGuiConstants.DEFAULT_COLOR_BUTTON, renderMode, blendMode, textureName);
		init(guiButtonID, buttonText);
	}

	public E3DGuiButton(E3DEngine engine, E3DGuiWindow guiWindow, String guiButtonID, String buttonText,  E3DVector2I position, E3DVector4F color, E3DRenderMode renderMode, E3DBlendMode blendMode)
	{
		super(engine, guiWindow, position, color, renderMode, blendMode, null);
		init(guiButtonID, buttonText);
	}
	
	public E3DGuiButton(E3DEngine engine, E3DGuiWindow guiWindow, String guiButtonID, String buttonText, E3DVector2I position, E3DRenderMode renderMode, E3DBlendMode blendMode,
						 E3DAnimatedTextureFrame[] textureFrames, int loops)
	{
		super(engine, guiWindow, position, E3DGuiConstants.DEFAULT_COLOR_TEXTURED, renderMode,blendMode, textureFrames, loops);
		init(guiButtonID, buttonText);
	}

	public void init(String guiButtonID, String buttonText)
	{
		this.guiButtonID = guiButtonID;
		this.buttonText = buttonText;
	}
	
	public void render()
	{
		// TODO Auto-generated method stub
		
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

	protected String getGuiButtonID()
	{
		return guiButtonID;
	}

	public String getButtonText()
	{
		return buttonText;
	}

	public void setButtonText(String buttonText)
	{
		this.buttonText = buttonText;
	}
}
