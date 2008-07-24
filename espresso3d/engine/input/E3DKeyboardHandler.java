/*
 * Created on Dec 21, 2004
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
package espresso3d.engine.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import espresso3d.engine.input.base.IE3DInputCallback;

/**
 * @author Curt
 *
 * Class to handle keyboard input
 */
class E3DKeyboardHandler {
	
	private HashMap keysDown = new HashMap(); //when a key goes down, it gets put in here.. When it goes up, taken out
    private HashMap keyToEventMap = new HashMap();

	/**
	 * Bind a key to a IE3DInputCallback implementing class.  Once bound,
	 * when this key is pressed, callback's onInput method will be called
	 * 
	 * @param key
	 * @param callback
	 */
	void bindKeyToHandler(Integer key, IE3DInputCallback callback)
	{
		keyToEventMap.put(key, callback);
	}
	
    void checkKeyboardInput()
	{
       while(Keyboard.next())
       {
			if(Keyboard.getEventKeyState() == true)
			{
				Integer key = new Integer(Keyboard.getEventKey());
				keysDown.put(key, key);
			}
			else if(Keyboard.getEventKeyState() == false)
			{
				Integer key = new Integer(Keyboard.getEventKey());
				if(keysDown.containsKey(key))
					keysDown.remove(new Integer(Keyboard.getEventKey()));
			}	
		}

		executeKeyCallback(keysDown);
	}
	
	private void executeKeyCallback(HashMap keysDown)
	{
		IE3DInputCallback callback = null;
		HashMap keyEventBindings = new HashMap(); //Will contain a map of CMInputCallbacks with values being an arraylist of all the keys to be sent to that callback
		
		
		//Build an array of the keys to be sent to each CMINputCallback so we can send a list of them to each
		Iterator it = keysDown.entrySet().iterator();
		Map.Entry entry = null;
		Integer keyCode = null;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			keyCode = (Integer)entry.getValue();

			if(keyToEventMap.containsKey(keyCode))
			{
				callback = (IE3DInputCallback)keyToEventMap.get(keyCode);
				if(keyEventBindings.containsKey(callback)) //if it already has some keys for this event, just add this one
				{
					ArrayList keyList = (ArrayList)keyEventBindings.get(callback);
					keyList.add(keyCode);
				}
				else //otherwise createa  new array list for this one in the event bindings map
				{
					ArrayList keyList = new ArrayList();
					keyList.add(keyCode);
					keyEventBindings.put(callback, keyList);
				}
				
			}
		}

		it = keyEventBindings.entrySet().iterator();
		entry  = null;
		callback = null;
		ArrayList callbackKeys = null;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			
			callback = (IE3DInputCallback)entry.getKey();
			callbackKeys = (ArrayList)entry.getValue();
			
			callback.onKeyboardInput(callbackKeys);
		}
	}

	boolean isKeyDown(Integer keyCode)
	{
	    return keysDown.containsKey(keyCode);
	}
}
