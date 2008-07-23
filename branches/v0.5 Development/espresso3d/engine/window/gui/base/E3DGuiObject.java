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
package espresso3d.engine.window.gui.base;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DAnimatedTextureRenderable;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
 
public abstract class E3DGuiObject extends E3DAnimatedTextureRenderable
{
	private boolean positionChanged;
	private E3DVector2I position;
	private E3DVector4F color;
	
	private E3DQuad displayQuad = null;

	public E3DGuiObject(E3DEngine engine, E3DVector2I position, E3DVector4F color, E3DRenderMode renderMode, E3DBlendMode blendMode,
			 String textureName)
	{
		super(engine, renderMode, blendMode, textureName);
		init(position, color);
	}
	
	public E3DGuiObject(E3DEngine engine,  E3DVector2I position, E3DVector4F color, E3DRenderMode renderMode, E3DBlendMode blendMode,
						 E3DAnimatedTextureFrame[] textureFrames, int loops)
	{
		super(engine, renderMode,blendMode, textureFrames, loops);
		init(position, color);
	}		
	
	private void init(E3DVector2I position, E3DVector4F color)
	{
		this.position = position;
		this.color = color;
	}

	abstract public void onFocusSet();
	
	abstract public void onLeftMouseDown();
	abstract public void onLeftMouseUp();
	abstract public void onLeftMouseDoubleClick();

	abstract public void onRightMouseDown();
	abstract public void onRightMouseUp();
	abstract public void onRightMouseDoubleClick();
	
	abstract public void onMiddleMouseDown();
	abstract public void onMiddleMouseUp();
	abstract public void onMiddleDoubleClick();

	protected abstract E3DQuad getPositionedQuad(E3DQuad quad);

	protected E3DQuad getDisplayQuad()
	{
		if(displayQuad == null ) //first time, set quad properties
		{
			displayQuad = getPositionedQuad(displayQuad);
			displayQuad.setBlendMode(getBlendMode());
			displayQuad.setRenderMode(getRenderMode());
		}
		else if(isPositionChanged())
			displayQuad = getPositionedQuad(displayQuad); //don't need to set properties, it will copy them

		displayQuad.setVertexColor(color, color, color, color);
		
		return displayQuad;
	}
	
	public E3DVector2I getPosition()
	{
		return position;
	}

	public void setPosition(E3DVector2I position)
	{
		this.position = position;
		positionChanged = true;
	}

	public E3DVector4F getColor()
	{
		return color;
	}

	public void setColor(E3DVector4F color)
	{
		this.color = color;
	}

	public boolean isPositionChanged()
	{
		return positionChanged;
	}

	public void setPositionChanged(boolean positionChanged)
	{
		this.positionChanged = positionChanged;
	}
	
}
