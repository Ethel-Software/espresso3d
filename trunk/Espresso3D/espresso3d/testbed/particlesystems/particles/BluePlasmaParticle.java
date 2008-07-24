/*
 * Created on Nov 24, 2004
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
package espresso3d.testbed.particlesystems.particles;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.sector.particle.E3DParticle;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BluePlasmaParticle extends E3DParticle 
{
    public BluePlasmaParticle(E3DEngine engine)
    {
        super(engine,new E3DVector3F(0.0+Math.random()*12, 0.0, -6.0+Math.random()*12), new E3DVector3F(0, 1, 0),
				2, 1+Math.random(), 1, 1, 0.5, 2, -1, 
				Math.random()*.9, "WATER");        
    }

    public void onCollisionActor(E3DCollision collision) {
    }

    public void onCollisionSprite(E3DCollision collision) {
    }

    public boolean onCollisionDuringUpdate(E3DCollision collision){
        return true;        
    }
    
    public E3DParticle onGetClone() throws Exception {
        return new BluePlasmaParticle(getEngine());
    }
    
	public boolean isCollideable(){
	    return true;
	}
	
	public boolean isCollisionCausedByMovement(){
	    return true;
	}
}
