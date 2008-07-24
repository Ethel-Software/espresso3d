/*
 * Created on Jul 14, 2005
 *
 
   	Copyright 2005 Curtis Moxley
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
package espresso3d.engine.lowlevel.thread;

import java.util.ArrayList;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;

/**
 * @author cm58568
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class E3DThreadPool extends E3DEngineItem
{
	protected static final int DEFAULT_THREADPOOL_SIZE = 10;
	
	private ArrayList threadPool;
	int poolSize;
	int curThread;
	
	/**
	 * Create a threadpool with default size (10 currently)
	 *
	 */
	public E3DThreadPool(E3DEngine engine)
	{
		this(engine, DEFAULT_THREADPOOL_SIZE);
		curThread = 0;
	}

	public E3DThreadPool(E3DEngine engine, int poolSize)
	{
	    super(engine);
		threadPool = new ArrayList(poolSize);
		this.poolSize = poolSize;
	}

	/**
	 * Attempt to start a thread.  If the pool is full, false is returned.  You can simply call start in a loop if you can wait to start the thread
	 * Otherwise, for automation an E3DQueuedThreadPool will automatically start the thread when a thread in the pool frees up.
	 * @param thread
	 * @return
	 */
	public boolean startThread(E3DThread thread)
	{
		if(threadPool.size() < poolSize)
		{
			threadPool.add(thread);
			thread.start();			
			return true;
		}
		else
		{
		    curThread++; //start where we left off, will probably be a little faster then starting from the beginning each time
			for(int i=curThread; i < poolSize; i++)
			{
				if(!((E3DThread)threadPool.get(i)).isAlive())							
				{
					threadPool.remove(i);
					threadPool.add(thread);
					thread.start();
					curThread = i;
					return true;
				}
			}
			curThread = 0;
		}
		return false;
	}

	/**
	 * Check if all threads in the threadpool have completed
	 * @return
	 */ 
	public boolean isThreadPoolIdle()
	{
	    for(int i=0; i < threadPool.size(); i++)
	    {
			if(((E3DThread)threadPool.get(i)).isAlive())							
			    return false;
	    }
	    return true;
	}
}
