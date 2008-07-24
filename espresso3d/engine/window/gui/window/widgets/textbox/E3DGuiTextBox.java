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
package espresso3d.engine.window.gui.window.widgets.textbox;

import org.lwjgl.opengl.GL11;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.window.gui.base.E3DGuiConstants;
import espresso3d.engine.window.gui.window.E3DGuiWindow;
import espresso3d.engine.window.gui.window.widgets.base.E3DGuiWidget;
 
public abstract class E3DGuiTextBox extends E3DGuiWidget
{
	private String guiTextBoxID;
	private String textBoxText;
	
	public E3DGuiTextBox(E3DEngine engine, E3DGuiWindow guiWindow, String guiTextBoxID, String textBoxText, E3DVector2I position, E3DRenderMode renderMode, E3DBlendMode blendMode)
	{
		this(engine, guiWindow, guiTextBoxID, textBoxText, position, renderMode, blendMode, null);
	}
	
	public E3DGuiTextBox(E3DEngine engine, E3DGuiWindow guiWindow, String guiButtonID, String textBoxText, E3DVector2I position, E3DRenderMode renderMode, E3DBlendMode blendMode,
			 String textureName)
	{
		super(engine, guiWindow, position, E3DGuiConstants.DEFAULT_COLOR_TEXTBOX, renderMode, blendMode, textureName);
		init(guiTextBoxID, textBoxText);
	}

	public E3DGuiTextBox(E3DEngine engine, E3DGuiWindow guiWindow, String guiTextBoxID, String textBoxText, E3DVector2I position, E3DVector4F color, E3DRenderMode renderMode, E3DBlendMode blendMode)
	{
		super(engine, guiWindow, position, color, renderMode, blendMode, null);
		init(guiTextBoxID, textBoxText);
	}
	
	public E3DGuiTextBox(E3DEngine engine, E3DGuiWindow guiWindow, String guiTextBoxID, String textBoxText, E3DVector2I position, E3DRenderMode renderMode, E3DBlendMode blendMode,
						 E3DAnimatedTextureFrame[] textureFrames, int loops)
	{
		super(engine, guiWindow, position, E3DGuiConstants.DEFAULT_COLOR_TEXTURED, renderMode,blendMode, textureFrames, loops);
		init(guiTextBoxID, textBoxText);
	}
	

	public void init(String guiTextBoxID, String textBoxText)
	{
		this.guiTextBoxID = guiTextBoxID;
		this.textBoxText = textBoxText;
	}

	public void render()
	{
		GL11.glPushMatrix();
			getEngine().getGeometryRenderer().renderQuad(getDisplayQuad(), getRenderMode().getRenderMode());
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

	public String getTextBoxText()
	{
		return textBoxText;
	}

	public void setTextBoxText(String textBoxText)
	{
		this.textBoxText = textBoxText;
	}
}
