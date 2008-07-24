/*
 * Created on Nov 9, 2004
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
package espresso3d.engine.world.sector.particle;

import java.util.ArrayList;
import java.util.List;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.base.E3DPortalEnabledRenderable;

/**
 * @author Curt
 *
 * A particle system is a grouping of particles used for creating animated
 * particle systems in a world/sector.
 */
public abstract class E3DParticleSystem extends E3DPortalEnabledRenderable // TODO: make this : implements IE3DCollisionDetectableObject . 
{
	private static final int DEFAULT_BLEND_MODE = E3DBlendMode.BLENDMODE_BLEND;
	
    private E3DOrientation orientation;
    
    private ArrayList particleList;
//	private HashMap textureSortedTriangleMap; //contains a hashmap keyed off textureID & each entry has an ArrayList of triangles
//	private E3DSortedRenderableMap blendAndTextureSortedParticleMap;
    
	private E3DVector3F gravityDirection;
	
    private double lifeSeconds = -1;
	private double startTime = -1;

	private E3DRenderTree renderTree;
//	private boolean needRenderTreeRehash = false;
	/**
	 * Create a new unnamed particle system (can't be quickly accessed from a map once its added).  Very useful for temporary systems)
	 * @param engine
	 */
    public E3DParticleSystem(E3DEngine engine)
    {
        this(engine, null, -1, -1);
    }
	
    /**
     * Create a named particle system.  This is useful for quick access to the system from the sectors map or particle systems.
     * @param engine
     * @param life  How long in seconds for the system to live
     */
    public E3DParticleSystem(E3DEngine engine, double lifeSeconds)
    {
        this(engine, null, -1, lifeSeconds);
    }

    /**
     * Create an unnamed particle system with gravity.  
     * @param engine
     * @param gravityDirection Normalised direction of gravity
     * @param gravityStrength strenth of the gravity
     */
    public E3DParticleSystem(E3DEngine engine, E3DVector3F gravityDirection, double gravityStrength)
    {
        this(engine, gravityDirection, gravityStrength, -1);
    }

    /**
     * Create an named particle system with gravity.  
     * @param engine
     * @param gravityDirection Normalised direction of gravity
     * @param gravityStrength strenth of the gravity
     * @param lifeSeconds number of seconds for the particle system to stay around.  -1 for infinite
     */
    public E3DParticleSystem(E3DEngine engine, E3DVector3F gravityDirection, double gravityStrength, double lifeSeconds)
    {
        super(engine);
        this.orientation = new E3DOrientation(engine);
        this.particleList = new ArrayList();
        setGravityDirection(gravityDirection);
        setPosition(new E3DVector3F());
        this.lifeSeconds = lifeSeconds;
        startTime = -1.0;
        setBlendMode(new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
        renderTree = new E3DRenderTree(engine);
    }
    
    /**
     * Create a new particle system as a copy of the old
     * @param toCopy
     */
    public E3DParticleSystem(E3DParticleSystem toCopy)
    {
        super(toCopy.getEngine());
        
        this.orientation = new E3DOrientation(toCopy.getOrientation());

        //don't copy the ID. Its useless. It needs to be given a new id and set by hand
        E3DParticle particle = null;
        this.renderTree = new E3DRenderTree(getEngine());
//        this.blendAndTextureSortedParticleMap = new E3DSortedRenderableMap();
        setParticleList(new ArrayList());
        
        for(int i=0; i<toCopy.getParticleList().size(); i++) //make actual copies of all the particles
        {
            particle = (E3DParticle)toCopy.getParticleList().get(i);
            try{
                addParticle(particle.onGetClone());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        setGravityDirection(new E3DVector3F(toCopy.getGravityDirection()));
        this.lifeSeconds = toCopy.getLifeSeconds();
        startTime = -1.0;
        
        setBlendMode(new E3DBlendMode(toCopy.getBlendMode()));
        setRenderMode(new E3DRenderMode(toCopy.getRenderMode()));
    }
    /**
     * This will add a particle to the system.
     * 
     * @param particle
     */
    public void addParticle(E3DParticle particle)
    {
        particle.setSector(getSector());
        particleList.add(particle);
        particle.setParticleSystem(this);

        renderTree.getParticleHandler().add(particle);
    }
    
    public void removeParticle(E3DParticle particle)
    {
        renderTree.getParticleHandler().remove(particle);
        particleList.remove(particle);
    }

    /**
     * Get the list of all the particles in the system
     * @return
     */
    public ArrayList getParticleList() {
        return particleList;
    }
    /**
     * Set the list of particles to be inthe system.  addParticle should be used to add particles
     * @param particleList
     */
    private void setParticleList(ArrayList particleList) {
        this.particleList = particleList;
    }
    
    /**
     * This updates the particles in the system (makes them all move 1 unit)
     *
     */
    public void updateParticles(E3DActor curActor)
    {
        double lastFrameTime = getEngine().getFpsTimer().getLastUpdateTimeSeconds();
        
        if(lifeSeconds > 0.0 && startTime < 0.0)
            startTime = getEngine().getFpsTimer().getCurrentEngineTimeSeconds();
        
        E3DParticle particle = null;

        if(particleList != null)
        {
        	for(int i=0; i < particleList.size(); i++)
			{
			    particle = (E3DParticle)particleList.get(i);
                
			    if(!particle.update(curActor, lastFrameTime))
			    {
			        removeParticle(particle);
			        i--;
			    }
            }
        }
    }

    /**
     * Returns false if the engine has expired its lifespan
     * @return
     */
    public boolean isAlive()
    {
        if(lifeSeconds < 0)
            return true;
        
        if(getEngine().getFpsTimer().getCurrentEngineTimeSeconds() - startTime > lifeSeconds)
            return false;
        return true;
    }
    /**
     * This will render this particle system individually.  However, it will be taken care of
     * in a better fashion by the engine so shouldn't be called under normal circumstances
     */
    public void render() 
    {
        getRenderTree().render();
    }

    /**
     * Returns the list of all the quads of all the particles in it
     * @return
     */
    public ArrayList getQuadList()
    {
        ArrayList quadList = new ArrayList(particleList.size() + 1);
        E3DParticle particle = null;
        for(int i=0; i<particleList.size(); i++)
        {
            particle = (E3DParticle)particleList.get(i);
            if(particle.isAlive())
                quadList.add(particle.getQuad());
        }
        
        return quadList;
    }
    
    public static ArrayList getQuadList(List particleList)
    {
        ArrayList quadList = new ArrayList(particleList.size() + 1);
        E3DParticle particle = null;
        E3DQuad quad = null;
        E3DOrientation orientation;
        for(int i=0; i<particleList.size(); i++)
        {
            particle = (E3DParticle)particleList.get(i);
            if(particle.isAlive())
            {
                quad = new E3DQuad((E3DQuad)particle.getQuad());
                orientation = particle.getOrientation();
                
                quad.setVertexPosA(orientation.getWorldVector(quad.getVertexPosA()));
                quad.setVertexPosB(orientation.getWorldVector(quad.getVertexPosB()));
                quad.setVertexPosC(orientation.getWorldVector(quad.getVertexPosC()));
                quad.setVertexPosD(orientation.getWorldVector(quad.getVertexPosD()));
                
                quadList.add(quad);
            }
        }
        
        return quadList;
    }
    
    /**
     * Override setSector to automatically set the sector the particles are in as well as the system.
     *  This is done automatically when adding it to a sector
     * so normally should be modified.
     * @param sector
     */
    public void setSector(E3DSector sector) {
    	super.setSector(sector);
        if(particleList != null)
        {
	        for(int i=0; i < particleList.size(); i++) //update all particles to have this sector... Particles could also point to the parent for the getter??
	            ((E3DParticle)particleList.get(i)).setSector(sector);
        }
    }
    
    /**
     * Get the list of particle's quads sorted by texture name.  Each entry of the map is keyed off texture name and contains 
     * an ArrayList of quads for that texture as the value
     * @return
     */
//    public E3DSortedRenderableMap getBlendAndTextureSortedParticleMap() {
//        return blendAndTextureSortedParticleMap;
//	}
//	private void setBlendAndTextureSortedParticleMap(
//			E3DSortedRenderableMap blendAndTextureSortedParticleMap) {
//	    this.blendAndTextureSortedParticleMap = blendAndTextureSortedParticleMap;
//	}
 
	/**
	 * Get the normalised direciton vector of gravity for this system
	 * @return
	 *  Gravity direciton.  Can be null.
	 */
	public E3DVector3F getGravityDirection() {
        return gravityDirection;
    }
	/**
	 * Set the direction of the gravity vector for this system or 
	 * essentially, a force that constantly pulls on a particle.  This
	 * value must include the scales of the amounts of pull (its not normalised
	 * any longer)
	 * @param gravityDirection
	 */
    public void setGravityDirection(E3DVector3F gravityDirection) {
        this.gravityDirection = gravityDirection;
    }
   
    /**
     * Get the position of the particle system
     * This is origin of the positions of all the particles are based around.  It begins at 0, 0, 0 
     * @return
     */
  //  public E3DVector3F getPosition() {
  //      return position;
  //  }

    /**
     * Set the position of the particle system
     * This is origin of the positions of all the particles are based around.  It begins at 0, 0, 0.
     * 
     * Changing the position will translate particles currently in the system.  It will NOT
     * translate particles that are added after the translation.  New particles will
     * be assumed to know it is at its position when they're startPositions are set
     * @return
     */
    public void setPosition(E3DVector3F position) {
        E3DVector3F translationAmt = position.subtract(orientation.getPosition());
        translate(translationAmt);
        
        
    }

    /**
     * Translate the particleSystem by translationVec amt.
     * 
     * Translating will translate particles currently in the system.  It will NOT
     * translate particles that are added after the translation.  New particles will
     * be assumed to know it is at its position when they're startPositions are set
     * @return
     */
    public void translate(E3DVector3F translationAmt)
    {
		checkSectorChangeDuringMovement(orientation.getPosition(), orientation.getPosition().add(translationAmt));

        E3DParticle particle = null;
        for(int i=0; i<particleList.size(); i++)
        {
            particle = (E3DParticle)particleList.get(i);
            particle.translate(translationAmt);
        }
        
        orientation.translate(translationAmt);
    }

    /**
     * Rotate all particles, and movement directions(permanently) in the system 
     * around upVec by angle amount
     * @param angle Radian angle
     * @param upVec Normalised up vector to use as the rotation axis
     */
    public void rotate(double angle, E3DVector3F upVec)
    {
        orientation.rotate(angle, upVec);
        
        E3DParticle particle = null;
        for(int i=0; i<particleList.size(); i++)
        {
            particle = (E3DParticle)particleList.get(i);
            particle.rotateMovementDirection(angle, upVec, true);
        }
    }
    
	/**
	 * This method must be overriden to return a clone of the particle system.
	 * This normally gets called when a specific actor in the preloadedActorMap of the world is specified in a world's map that is getting loaded
	 * What it wants is a clone of the preloaded actor returned (CLONE, not ref to the same object!).  This is exposed
	 * to you so you can modify the actor if necessary when cloning it.
	 * @param world  This is the world trying to load this actor
	 *	 
	 **/
	abstract public E3DParticleSystem onGetClone() throws Exception;
    
    public double getLifeSeconds() {
        return lifeSeconds;
    }
    public void setLifeSeconds(double lifeSeconds) {
        this.lifeSeconds = lifeSeconds;
    }
    
    /**
     * If a particle systems renderMode is changed, so are all the particles contained
     * in the particle system.  To change a single particle, change that particles rendermode
     * 
     * This will overwrite an individual particle's rendermode.  It will not be able
     * to revert back to its original renderMode automatically, you will be responsible
     * for resetting any particle to a different render mode as you see fit.
     */
    public void setRenderMode(E3DRenderMode renderMode)
    {
    	super.setRenderMode(renderMode);
    	
		E3DParticle particle;
		for(int i=0; i<particleList.size(); i++)
		{
			particle = (E3DParticle)particleList.get(i);
			particle.setRenderMode(new E3DRenderMode(renderMode));
		}
    }
    
    public void setBlendMode(E3DBlendMode blendMode)
    {
    	super.setBlendMode(blendMode);
    	
		E3DParticle particle;
		for(int i=0; i<particleList.size(); i++)
		{
			particle = (E3DParticle)particleList.get(i);
			particle.setBlendMode(new E3DBlendMode(blendMode));
		}
    }

	/**
	 * If any of the particles are needing to be collision detected, set this to true.  If set to false,
	 * its a shortcut to disable all collision detection for particles in the system
	 * 
	 * This does not automatically make particles stop causing collisions with their own movement,
	 * it just keeps the particles from being run into by other object's movement.
	 * 
	 * @return
	 */
	abstract public boolean isCollideable();    
	
	/**
	 * Set this to true if any particles in the system need to cause collisions when they move.  If set to false,
	 * its a shortcut to disable all collisions caused by particles in the system.
	 * 
	 * This does not automatically make particles uncollideable.  It just keeps the particles from causing
	 * collisions with their own movement.
	 * 
	 * @return
	 */
	abstract public boolean isCollisionCausedByMovement();
    public E3DOrientation getOrientation() {
        return orientation;
    }

	public E3DRenderTree getRenderTree() 
	{
//		if(needRenderTreeRehash)
//		{
//		renderTree.getParticleSystemHandler().rehash(this);
//			needRenderTreeRehash = false;
//		}
		return renderTree;
	}
}
