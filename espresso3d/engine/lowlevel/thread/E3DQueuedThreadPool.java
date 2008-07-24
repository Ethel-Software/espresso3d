/*
 * Created on Jul 14, 2005
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
package espresso3d.engine.lowlevel.thread;

import java.util.ArrayList;

import espresso3d.engine.E3DEngine;


/**
 * @author cm58568
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class E3DQueuedThreadPool extends E3DThreadPool 
{
	private ArrayList queue;
	private E3DQueuedThreadPoolMonitorThread queueWatcher = null;
	
	public E3DQueuedThreadPool(E3DEngine engine)
	{
		this(engine, DEFAULT_THREADPOOL_SIZE);
	}

	public E3DQueuedThreadPool(E3DEngine engine, int poolSize)
	{
		super(engine, poolSize);
		
		queue = new ArrayList();
	}

	public boolean startThread(E3DThread thread) 
	{
		//Try to start it, if it doesn't start, add it to the queue to be
		// started as soon as possible
		if(!super.startThread(thread))
		{
			addThreadToQueue(thread);
			
			if(queueWatcher == null || !queueWatcher.isAlive() || !queueWatcher.isActive())
			{
				queueWatcher = new E3DQueuedThreadPoolMonitorThread(this);
				queueWatcher.start();
			}
		}

		return true;
	}
	
	private boolean startThreadFromQueue(E3DThread thread)
	{
		return super.startThread(thread);
	}
	
	private void addThreadToQueue(E3DThread thread)
	{
		synchronized(queue){
			queue.add(thread);	
		}
	}
	
	public boolean isThreadPoolIdle()
	{
	    return((queue == null || queue.isEmpty()) && super.isThreadPoolIdle());
	}
	
	//Thread to automatically monitor a queued threadpool's queue
	// until there is a free spot to start one of the queued threads
	private class E3DQueuedThreadPoolMonitorThread extends Thread
	{
		private static final int MONITOR_SLEEP_TIME_MS = 10;

		private E3DQueuedThreadPool monitoredThreadPool;
				
		private Boolean active;
		
		public E3DQueuedThreadPoolMonitorThread(E3DQueuedThreadPool monitoredThreadPool)
		{
			this.monitoredThreadPool = monitoredThreadPool;
			active = new Boolean(false);
		}
		
		public void run() 
		{
			setActive(true);
			
			E3DThread firstInLineThread = null;			

			int queueSize = 0;
			synchronized(monitoredThreadPool.queue) //synchronize on the queue since it will be accessed by multiple threads
			{
				queueSize = monitoredThreadPool.queue.size();
			}			
			while(queueSize > 0)
			{
				synchronized(monitoredThreadPool.queue) //synchronize on the queue since it will be accessed by multiple threads
				{
					if(firstInLineThread == null)
						firstInLineThread = (E3DThread)monitoredThreadPool.queue.get(0);
				}
				
				//if we can start this thread, remove it from the queue and grab the next one
				if(monitoredThreadPool.startThreadFromQueue(firstInLineThread))
				{
					synchronized(monitoredThreadPool.queue) //synchronize on the queue since it will be accessed by multiple threads
					{
						monitoredThreadPool.queue.remove(0);
					}
					firstInLineThread = null;										
				}
				else //only sleep if we didn't start one, if we started one, keep trying to churn through the queue
				{					
					try{
						sleep(MONITOR_SLEEP_TIME_MS); //sleep 10
					}catch(InterruptedException e){
						setActive(false);
						break;
					}
				}

				synchronized(monitoredThreadPool.queue) //synchronize on the queue since it will be accessed by multiple threads
				{
					queueSize = monitoredThreadPool.queue.size();
					if(queueSize <= 0)
					{
						setActive(false);
						return;	
					}
				}
			}
			
			setActive(false);
		}
		/**
		 * @return
		 */
		public boolean isActive() {
			synchronized(active)
			{
				return active.booleanValue();
			}
		}

		/**
		 * @param b
		 */
		public void setActive(boolean b) {
			synchronized(active)
			{
				active = new Boolean(b);
			}
		}



	}
}
