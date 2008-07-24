/*
 * Created on Mar 9, 2005
 *
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
package espresso3d.engine.sound;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DSound extends E3DEngineItem 
{
    int alSoundBufferID = -1;
    int alSourceID = -1;
    String soundName;
    double pitch = 1;
    double volume = 1;
    boolean discarded = false;
    boolean paused = false;
    
    public E3DSound(E3DEngine engine, String soundName, int alSoundBufferID)
    {
        super(engine);
        this.alSoundBufferID = alSoundBufferID;
        this.soundName = soundName;
        this.alSourceID = -1;
    }
    
    public E3DSound(E3DSound toCopySound)
    {
        super(toCopySound.getEngine());
        this.alSoundBufferID = toCopySound.alSoundBufferID;
        this.alSourceID = -1;
        this.soundName = toCopySound.soundName;
    }
    
    public int getAlSoundBufferID() {
        return alSoundBufferID;
    }
    public void setAlSoundBufferID(int alSoundID) {
        this.alSoundBufferID = alSoundID;
    }
    public String getSoundName() {
        return soundName;
    }
    public double getPitch() {
        return pitch;
    }
    
    public double getVolume() {
        return volume;
    }
    
    public String getKey() {
        return Integer.toString(alSourceID);
    }
    public int getAlSourceID() {
        return alSourceID;
    }
    public boolean isDiscarded() {
        return discarded;
    }
    public boolean isPaused() {
        return paused;
    }
}
