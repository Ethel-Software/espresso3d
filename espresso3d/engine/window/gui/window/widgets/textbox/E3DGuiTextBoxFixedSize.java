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

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.window.gui.window.E3DGuiWindow;
 
public class E3DGuiTextBoxFixedSize extends E3DGuiTextBox
{
	private E3DVector2I size;
	
	public E3DGuiTextBoxFixedSize(E3DEngine engine, E3DGuiWindow guiWindow, String guiTextBoxID, String textBoxText, E3DVector2I position, E3DVector2I size, E3DRenderMode renderMode, E3DBlendMode blendMode)
	{
		this(engine, guiWindow, guiTextBoxID, textBoxText, position, size, renderMode, blendMode, null);
	}
	
	public E3DGuiTextBoxFixedSize(E3DEngine engine, E3DGuiWindow guiWindow, String guiTextBoxID, String textBoxText, E3DVector2I position, E3DVector2I size,E3DRenderMode renderMode, E3DBlendMode blendMode,
			 String textureName)
	{
		super(engine, guiWindow, guiTextBoxID, textBoxText, position, renderMode, blendMode, textureName);
		init(size);
	}
	
	public E3DGuiTextBoxFixedSize(E3DEngine engine, E3DGuiWindow guiWindow, String guiTextBoxID, String textBoxText, E3DVector2I position, E3DVector2I size,E3DRenderMode renderMode, E3DBlendMode blendMode,
						 E3DAnimatedTextureFrame[] textureFrames, int loops)
	{
		super(engine, guiWindow, guiTextBoxID, textBoxText, position, renderMode,blendMode, textureFrames, loops);
		init(size);
	}
	
	public void init(E3DVector2I size)
	{
		this.size = size;
	}
	
	/**
	 * Always relative to the containing window
	 */
	protected E3DQuad getPositionedQuad(E3DQuad quad)
	{
		return getGuiWindow().getGuiHandler().getViewport().getFixedSizePositionedQuad(quad, getPosition().add(getGuiWindow().getPosition()), size, getGuiWindow().getDepth() + 0.00001);
	}
	
	

}
