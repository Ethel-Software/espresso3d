/*
 * Created on Apr 17, 2005

 
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
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DSound3D extends E3DSound 
{
    E3DActor sourceActor;
    double referenceDistance = 10;
    double maxDistance = 100;
    double falloffFactor = 1;

    public E3DSound3D(E3DEngine engine, String soundName, int soundBufferID)
    {
        super(engine, soundName, soundBufferID);
    }
    
    public E3DSound3D(E3DSound toCopySound)
    {
        super(toCopySound);
    }
    
    public E3DSound3D(E3DSound3D toCopySound3D)
    {
        super(toCopySound3D);
    }
    public int getAlSourceID() {
        return alSourceID;
    }
    public E3DActor getSourceActor() {
        return sourceActor;
    }

    
    /**
     * This is called automatically by the engine when this sounds source actor moves or rotates.  This
     * shouldn't need to be accessed by a user.
     *
     *@param translationAmt  This is the amount the actor (and sound) are moving.  This can be null
     * if it is just rotating
     */
    public void updateOrientation(E3DVector3F translationAmt)
    {
        getEngine().getSoundHandler().update3DSoundOrientation(this, translationAmt);
    }
    public double getFalloffFactor() {
        return falloffFactor;
    }
    public double getMaxDistance() {
        return maxDistance;
    }
    public double getReferenceDistance() {
        return referenceDistance;
    }
}
