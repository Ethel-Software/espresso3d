/*
 * Created on Oct 26, 2004
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
package espresso3d.engine.timer;

import org.lwjgl.Sys;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;

/**
 * @author Curt
 *
 * A basic timer template that can be extended or used.
 */
public class E3DTimer extends E3DEngineItem
{
	private long lastTickCount=0;
	private long lastUpdateTime=0;

	public E3DTimer(E3DEngine engine)
	{
		super(engine);
	}
	
	/**
	 * Updates the last frame time and sets the lastTickCount to be the current
	 *
	 */
	public void update()
	{
		long currentTime = Sys.getTime();
		
		if(getLastTickCount() != 0)
			lastUpdateTime = currentTime - getLastTickCount();
		lastTickCount = currentTime;
	}

	/**
	 * Gets the start tick time that is stored for the last frame
	 * @return Returns the lastTickCount.
	 */
	public long getLastTickCount() {
		return lastTickCount;
	}

	/**
	 * Gets the current tick time.
	 * @return
	 */
	public long getCurrentTickCount(){
		return Sys.getTime();
	}

	/**
	 * Returns what the tick count needs to be for 1 second.  Can be useful to see how long a frame actually took to render.
	 * Dividing lastFrameTime by tickResolution gets lastFrameTime in seconds (the same as getLastFrameTimeSeconds).
	 * @return
	 */
	public long getTickResolution(){
		return Sys.getTimerResolution();
	}

	/**
	 * Get how long it was between the last update and this update.  Useful in FPS and making speeds of items be time based (multiply speed by this amt)
	 * @return
	 */
	public double getLastUpdateTimeSeconds(){
	    return lastUpdateTime / (double)getTickResolution();
	}

	public void resetTimer(){
		lastTickCount = 0;
		lastUpdateTime = 0;
	}
	
	/**
	 *Returns the current engine time in seconds
	 */
	public double getCurrentEngineTimeSeconds(){
	    return Sys.getTime() / (double)getTickResolution();
	}
}
