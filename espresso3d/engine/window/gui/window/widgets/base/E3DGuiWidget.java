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
package espresso3d.engine.window.gui.window.widgets.base;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.window.gui.base.E3DGuiObject;
import espresso3d.engine.window.gui.window.E3DGuiWindow;
 
public abstract class E3DGuiWidget extends E3DGuiObject
{
	private E3DGuiWindow guiWindow;
	
	public E3DGuiWidget(E3DEngine engine, E3DGuiWindow guiWindow, E3DVector2I position, E3DVector4F color, E3DRenderMode renderMode, E3DBlendMode blendMode,
			 String textureName)
	{
		super(engine, position, color, renderMode, blendMode, textureName);
		init(guiWindow);
	}
	
	public E3DGuiWidget(E3DEngine engine, E3DGuiWindow guiWindow, E3DVector2I position, E3DVector4F color, E3DRenderMode renderMode, E3DBlendMode blendMode,
						 E3DAnimatedTextureFrame[] textureFrames, int loops)
	{
		super(engine, position, color, renderMode, blendMode, textureFrames, loops);
		init(guiWindow);
	}

	public void init(E3DGuiWindow guiWindow)
	{
		this.guiWindow = guiWindow;
	}
	
	protected E3DGuiWindow getGuiWindow()
	{
		return guiWindow;
	}
}
