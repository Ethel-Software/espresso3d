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

import espresso3d.engine.E3DEngine;


/**
 * @author Curt
 *
 * A FramesPerSecond counter.  The engine has an instance of this in it already.
 */
public class E3DFPSTimer extends E3DTimer {
	private int updateCount=0;
	private double totalTime=0;

	private double FPS = 0.0;

	public E3DFPSTimer(E3DEngine engine)
	{
		super(engine);
	}
	
	public void update()
	{
		super.update();
		
		if(updateCount >= 30)
		{
			FPS = updateCount / totalTime;
			updateCount = 0;
			totalTime = 0;
		}
		totalTime+=getLastUpdateTimeSeconds();
		updateCount++;
	}

	public double getFPS()
	{
		return FPS;
	}
	
	public void resetTimer()
	{
		super.resetTimer();
		FPS = 0.0;
	}
}
