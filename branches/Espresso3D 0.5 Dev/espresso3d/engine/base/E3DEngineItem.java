/*
 * Created on Jul 17, 2004
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
package espresso3d.engine.base;

import espresso3d.engine.E3DEngine;

/**
 * @author espresso3d
 *
 * Any item that gets put in the engine must extend this or a variant that extends this
 */
public abstract class E3DEngineItem
{
	private E3DEngine engine;
	
	/**
	 * All engine items must know about the engine, so it is required in the constructor
	 * @param engine
	 */
	public E3DEngineItem(E3DEngine engine)
	{
		setEngine(engine);
	}
	/**
	 * @return Returns the engine the item is a part of
	 */
	public E3DEngine getEngine() {
		return engine;
	}
	/**
	 * @param engine Set the engine the item is a part of.
	 */
	public void setEngine(E3DEngine engine) {
		this.engine = engine;
	}
	
}
