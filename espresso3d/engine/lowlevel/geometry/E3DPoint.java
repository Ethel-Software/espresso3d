/*
 * Created on Aug 17, 2004
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
 */
package espresso3d.engine.lowlevel.geometry;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DRenderable;


/**
 * @author espresso3d
 *
 * Point class used in the engine
 */
public class E3DPoint extends E3DRenderable 
{
	E3DVector3F pos;
    E3DVector4F color;
	double size = 1.0;
	
	public E3DPoint(E3DEngine engine, E3DVector3F pos)
	{
		this(engine, pos, new E3DVector4F(1.0, 1.0, 1.0, 1.0), 1.0);
	}

	public E3DPoint(E3DEngine engine, E3DVector3F pos, double size)
	{
		this(engine, pos, new E3DVector4F(1.0, 1.0, 1.0, 1.0), size);
	}
	
	public E3DPoint(E3DEngine engine, E3DVector3F pos, E3DVector4F color)
	{
		this(engine, pos, color, 1);
	}

	public E3DPoint(E3DEngine engine, E3DVector3F pos, E3DVector4F color, double size)
	{
		super(engine);
		setPos(pos);
		setSize(size);
		setColor(color);
	}
	
	/**
	 * 
	 */
	public void render() 
	{
        getEngine().getGeometryRenderer().initPointRenderer();
        getEngine().getGeometryRenderer().renderPoint(this);
	}
	
	/**
	 * @return Returns the pos.
	 */
	public E3DVector3F getPos() {
		return pos;
	}
	/**
	 * @param pos The pos to set.
	 */
	public void setPos(E3DVector3F pos) {
		this.pos = pos;
	}
	/**
	 * @return Returns the size.
	 */
	public double getSize() {
		return size;
	}
	/**
	 * @param size The size to set.
	 */
	public void setSize(double size) {
		this.size = size;
	}
	/**
	 * @return Returns the color.
	 */
	public E3DVector4F getColor() {
		return color;
	}
	/**
	 * @param color The color to set.
	 */
	public void setColor(E3DVector4F color) {
		this.color = color;
	}
}
