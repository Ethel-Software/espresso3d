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
package espresso3d.engine.logo.particlesystems;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.logo.particlesystems.particles.RedPlasmaParticle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;

/**
 * @author Curt
 *
 * The red particle system used in v0.2 logo
 */
public class RedPlasmaParticleFountain extends E3DParticleSystem {

    public RedPlasmaParticleFountain(E3DEngine engine, E3DSector sector, E3DVector3F gravityDirection) throws Exception
    {
        super(engine);

        setGravityDirection(gravityDirection);

        for(int i=0; i<100; i++)
		{
            RedPlasmaParticle particle = new RedPlasmaParticle(engine, new E3DVector3F(Math.random() / 2, Math.random() / 2, Math.random() / 2),
					new E3DVector3F((Math.random()-0.5)/4, 1.0, (Math.random() - 0.5)/4), 
					50, 2, Math.random()/2, 6, Math.random() < 0.2);
		    
		    addParticle(particle);
		    if(particle.getLight() != null)
		    	sector.addLight(particle.getLight());
		}
    }
    
    public RedPlasmaParticleFountain(RedPlasmaParticleFountain toCopySystem)
    {
        super(toCopySystem);
    }
    
    /* (non-Javadoc)
     * @see espresso3d.engine.world.sector.particle.E3DParticleSystem#onGetClone()
     */
    public E3DParticleSystem onGetClone() throws Exception {
        RedPlasmaParticleFountain copied = new RedPlasmaParticleFountain(this);
        return copied;
    }

    public boolean isCollideable(){
        return false;
    }
    
    public boolean isCollisionCausedByMovement(){
        return false;
    }

}
