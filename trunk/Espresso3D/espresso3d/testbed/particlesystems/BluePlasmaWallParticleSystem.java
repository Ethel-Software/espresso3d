/*
 * Created on Nov 14, 2004
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
package espresso3d.testbed.particlesystems;

import espresso3d.testbed.particlesystems.particles.BluePlasmaParticle;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.world.sector.particle.E3DParticle;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BluePlasmaWallParticleSystem extends E3DParticleSystem {

    public BluePlasmaWallParticleSystem(E3DEngine engine) throws Exception
    {
        super(engine);
		for(int i=0; i<500; i++)
		{
		    E3DParticle particle = new BluePlasmaParticle(engine);
		    
		    addParticle(particle);
		}
        
    }
    
    public BluePlasmaWallParticleSystem(BluePlasmaWallParticleSystem toCopySystem)
    {
        super(toCopySystem);
    }
    
    /* (non-Javadoc)
     * @see espresso3d.engine.world.sector.particle.E3DParticleSystem#onGetClone()
     */
    public E3DParticleSystem onGetClone() throws Exception {
        BluePlasmaWallParticleSystem copied = new BluePlasmaWallParticleSystem(this);
        return copied;
    }
    
    public boolean isCollideable(){
        return false;
    }
    
    public boolean isCollisionCausedByMovement(){
        return false;
    }
}
