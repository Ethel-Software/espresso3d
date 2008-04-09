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
package espresso3d.engine.logo.particlesystems.particles;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.light.E3DLight;
import espresso3d.engine.world.sector.particle.E3DParticle;

/**
 * @author Curt
 *
 * The blue plasma particle used in v0.2 logos particle system
 */
public class BluePlasmaParticle extends E3DParticle 
{
    private E3DLight light = null;
    
    public BluePlasmaParticle(E3DEngine engine, E3DVector3F startPosition, E3DVector3F startMovementDirection, double velocity, double life, double startDelay, double size, boolean createLight)
    {
        super(engine,startPosition, startMovementDirection,
				velocity, life, 1, 1, size, size, 0, 
				startDelay, "BLUEPLASMAPARTICLE");        
        
        if(createLight)
        {
	        light = new E3DLight(engine, ("light" + Math.random()));
	        light.setColor(new E3DVector4F(0.0, 0.0, 1.0, 1.0));
	        light.setBrightness(6);
	        light.setFalloff(20);
	        light.setPosition(startPosition);
        }
    } 

    public void onCollisionActor(E3DCollision collision) {
    }

    public void onCollisionSprite(E3DCollision collision) {
    }

    public boolean onCollisionDuringUpdate(E3DCollision collision){
        return true;
    }
    
    public E3DParticle onGetClone() throws Exception {
        return new BluePlasmaParticle(getEngine(), new E3DVector3F(getStartPosition()), new E3DVector3F(getStartMovementDirection()), getVelocity(), getLife(), getStartDelay(), getSize(), getLight() != null);
    }
    
	public boolean isCollideable(){
	    return true;
	}
	
	public boolean isCollisionCausedByMovement(){
	    return true;
	}
    public E3DLight getLight() {
        return light;
    }
    public void setLight(E3DLight light) {
        this.light = light;
    }

    public boolean update(E3DActor curActor, double lastFrameTimeSeconds) {
        if(super.update(curActor,
                lastFrameTimeSeconds))
        {
        	if(light != null)
        		light.setPosition(getOrientation().getPosition());
            return true;
        }
        else
        {
            if(light != null && getSector() != null)
                getSector().removeLight(light.getLightID());
            return false;
        }
    }
}
