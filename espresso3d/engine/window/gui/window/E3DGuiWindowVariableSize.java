/*
 *  *
 
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
package espresso3d.engine.window.gui.window;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
 
public class E3DGuiWindowVariableSize extends E3DGuiWindow
{
	private E3DVector2F sizePercentage;
	
	public E3DGuiWindowVariableSize(E3DEngine engine,
			String guiWindowID, String title, int x, int y, double widthPercentage, double heightPercentage, E3DRenderMode renderMode, E3DBlendMode blendMode)
	{
		this(engine, guiWindowID, "", x, y, widthPercentage, heightPercentage, renderMode,
				blendMode, null);
	}

	public E3DGuiWindowVariableSize(E3DEngine engine,
			String guiWindowID, String title, int x, int y, double widthPercentage, double heightPercentage, E3DRenderMode renderMode, E3DBlendMode blendMode,
			String textureName)
	{
		this(engine, guiWindowID, title, new E3DVector2I(x, y), new E3DVector2F(widthPercentage, heightPercentage), renderMode, blendMode,
				textureName);
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
	public E3DGuiWindowVariableSize(E3DEngine engine, 
			String guiWindowID, String title, E3DVector2I min, E3DVector2F sizePercentage,
			E3DRenderMode renderMode, E3DBlendMode blendMode,
			E3DVector4F color)
	{
		super(engine, guiWindowID, title, min, renderMode, blendMode, color);
		this.sizePercentage = sizePercentage;
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
	public E3DGuiWindowVariableSize(E3DEngine engine,
			String guiWindowID, String title, E3DVector2I min, E3DVector2F sizePercentage,
			E3DRenderMode renderMode, E3DBlendMode blendMode,
			String textureName)
	{
		super(engine, guiWindowID, title, min, renderMode, blendMode, textureName);
		this.sizePercentage = sizePercentage;
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
	public E3DGuiWindowVariableSize(E3DEngine engine,String guiWindowID, String title,
			E3DVector2I min, E3DVector2F sizePercentage, E3DRenderMode renderMode,
			E3DBlendMode blendMode, E3DAnimatedTextureFrame[] textureFrames,
			int loops)
	{
		super(engine, guiWindowID, title, min, renderMode, blendMode, textureFrames, loops);
		this.sizePercentage = sizePercentage;
	}
	
	protected E3DQuad getPositionedQuad(E3DQuad quad)
	{
		return getGuiHandler().getViewport().getVariableSizePositionedQuad(quad, getPosition(), sizePercentage, 1.0);
	}

	public E3DVector2F getSizePercentage()
	{
		return sizePercentage;
	}

	public void setSizePercentage(E3DVector2F sizePercentage)
	{
		this.sizePercentage = sizePercentage;
	}
	
}
