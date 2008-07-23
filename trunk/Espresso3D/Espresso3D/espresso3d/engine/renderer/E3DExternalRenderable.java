/*
 * Created on Jan 29, 2005
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
package espresso3d.engine.renderer;

import espresso3d.engine.renderer.base.E3DRenderable;

/**
 * @author Curt
 *
 *
 *  ExternalRenderable's are essentially timed E3DRenderable objects.  They can be created
 *  and added to either a viewport or the engine itself and can be used
 *  to do any sort of rendering you see fit.  You could use them to make a completely
 *  new rendering algorithm (maybe BSP trees, etc) for you application.  Simply create it,
 * make it do whatever it needs to do in the E3DRenderable's render procedure, and
 * add it to a viewport to make it viewport specific, or the engine to make it engine specific.
 */
public class E3DExternalRenderable {
	private E3DRenderable renderable = null;
	private int timeoutFrames = 0;
	private int curFrame = 0;
	
	public E3DExternalRenderable(E3DRenderable renderable, int timeoutFrames)
	{
		setTimeoutFrames(timeoutFrames);
		setRenderable(renderable);
		curFrame = 0;
	}
	
	/**
	 * Returns true if the item has been in the engine's externalRenderable list longer than its timeout is set to allow it to
	 * @return
	 */
	public boolean update()
	{
		curFrame ++;
		if(timeoutFrames < 0)
			return true;
		else if(timeoutFrames - curFrame <= 0)
			return false;
		else
			return true;
	}
	
	/**
	 * @return Returns the renderable.
	 */
	public E3DRenderable getRenderable() {
		return renderable;
	}
	/**
	 * @param renderable The renderable to set.
	 */
	public void setRenderable(E3DRenderable renderable) {
		this.renderable = renderable;
	}
	/**
	 * @return Returns the timeoutFrames.
	 */
	public int getTimeoutFrames() {
		return timeoutFrames;
	}
	/**
	 * @param timeoutFrames The timeoutFrames to set.
	 */
	public void setTimeoutFrames(int timeoutFrames) {
		this.timeoutFrames = timeoutFrames;
	}
}
