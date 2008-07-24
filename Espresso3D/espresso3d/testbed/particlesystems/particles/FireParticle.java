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
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.world.sector.particle.E3DParticle;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FireParticle extends E3DParticle 
{
    public FireParticle(E3DEngine engine)
    {
        super(engine, new E3DVector3F(Math.random() * 1.2, Math.random() * 1.2, Math.random() * 1.2), new E3DVector3F(0, 1, 0),
				0.2, 10/**Math.random()*/, 0.1, 1, 10, 10, -1, 
				Math.random() * 1.5, 
				new E3DAnimatedTextureFrame[]{
                  new E3DAnimatedTextureFrame(engine, "WATER", 3), 
                  new E3DAnimatedTextureFrame(engine, "FIRE",  3)}, -1); 
    }

    public boolean onCollisionDuringUpdate(E3DCollision collision) {
        return true;        
    }
    
    public void onCollisionActor(E3DCollision collision) {
        // TODO Auto-generated method stub

    }

    public void onCollisionSprite(E3DCollision collision) {
        // TODO Auto-generated method stub

    }

    public E3DParticle onGetClone() throws Exception {
        return new FireParticle(getEngine());
    }
	public boolean isCollideable(){
	    return true;
	}
	
	public boolean isCollisionCausedByMovement(){
	    return true;
	}
	
}
