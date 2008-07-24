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

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
 
public class E3DGuiWindowFixedSize extends E3DGuiWindow
{
	private E3DVector2I size; // min + size = upper right
	
	
	public E3DGuiWindowFixedSize(E3DEngine engine,
			String guiWindowID, String title, int x, int y, int width, int height, E3DRenderMode renderMode, E3DBlendMode blendMode)
	{
		this(engine, guiWindowID, "", x, y, width, height, renderMode,
				blendMode, null);
	}

	public E3DGuiWindowFixedSize(E3DEngine engine,
			String guiWindowID, String title, int x, int y, int width, int height, E3DRenderMode renderMode, E3DBlendMode blendMode,
			String textureName)
	{
		this(engine, guiWindowID, title, new E3DVector2I(x, y), new E3DVector2I(width, height), renderMode, blendMode,
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
	public E3DGuiWindowFixedSize(E3DEngine engine, 
			String guiWindowID, String title, E3DVector2I min, E3DVector2I size,
			E3DRenderMode renderMode, E3DBlendMode blendMode,
			E3DVector4F color)
	{
		super(engine, guiWindowID, title, min, renderMode, blendMode, color);
		this.size = size;
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
	public E3DGuiWindowFixedSize(E3DEngine engine,
			String guiWindowID, String title, E3DVector2I min, E3DVector2I size,
			E3DRenderMode renderMode, E3DBlendMode blendMode,
			String textureName)
	{
		super(engine, guiWindowID, title, min, renderMode, blendMode, textureName);
		this.size = size;
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
	public E3DGuiWindowFixedSize(E3DEngine engine,String guiWindowID, String title,
			E3DVector2I min, E3DVector2I size, E3DRenderMode renderMode,
			E3DBlendMode blendMode, E3DAnimatedTextureFrame[] textureFrames,
			int loops)
	{
		super(engine, guiWindowID, title, min, renderMode, blendMode, textureFrames, loops);
		this.size = size;
	}
	
	protected E3DQuad getPositionedQuad(E3DQuad quad)
	{
		return getGuiHandler().getViewport().getFixedSizePositionedQuad(quad, getPosition(), size, 0.5);
	}
}
