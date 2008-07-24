/*
 * Created on Nov 9, 2004
 *
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

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.E3DCollisionHandler;
import espresso3d.engine.collision.bounding.E3DBoundingSphere;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.tree.base.IE3DHashableNode;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTexture;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.renderer.texture.E3DTexture;
import espresso3d.engine.window.viewport.image.E3DImage;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * @author Curt
 *
 * A particle uses hardware to simulate billboarding.  It used to be a type of billboard sprite in older versions,
 * however, it is no longer necessary for it to have built-in billboarding features.  The hardware will
 * always make it look at the camera.  Therefore, this class now extends e3DSprite instead of E3DBillboardSprite to remove
 * all the billboarding code (slow).
 * 
 * If a particle is collideable detectable and a collision occurs during its update, it is NOT moved and the 
 * onCollisionDuringUpdate callback is called.  This way, collisions can be handled BEFORE they happen, not after
 * they happen and the particle has already moved through the object.
 * 
 * Collision detection for particles is based on its bounding quad.
 */

public abstract class E3DParticle extends E3DBillboardSprite implements IE3DHashableNode
{
	private static final int DEFAULT_BLEND_MODE = E3DBlendMode.BLENDMODE_BLEND;
	
    private E3DParticleSystem particleSystem;
    private E3DVector3F startPosition; //position to start at
    private E3DVector3F currentMovementDirection; //The current direction the particle should move.  This allows it to temporarily rotate (ie: collisions)
    private E3DVector3F startMovementDirection; //direction the particle moves
    private double currentFrameVelocity; //Gets the specific velocity the particle will move this frame (Scaled)
    private double velocity; //speed of the particle in direction dir.
    private double life;
    private double age; //private, age the particle currently is
    private double startAlpha;
    private double endAlpha;
    private int loops;
    private int curLoop = 0;
    private double startDelay; //time in seconds to delay before a particle starts moving
    private double startSize;
    private double endSize;
    //Current size is stored at hte sprite level
    
    
    private boolean addedToQuadList = false;
    private boolean needRehash = false;

    /**
     * Create a new particle
     * 
     * @param engine The engine the particle resides in
     * @param startPosition The position the particle starts at
     * @param startMovementDirection The direciton the particle starts moving at the beginning of its life.
     * @param velocity The velocity the particle moves at.  This is scaled each frame by the engine
     * @param life The particles lifespan (seconds).  This time begins after the start delay of a life cycle has expired.
     * @param startAlpha Alpha value (0.0 - 1.0) of the particle at beginning of life.  Currently unimplemented (Always 1)
     * @param endAlpha The alpha value (0.0 - 1.0) of the particle at the end of life.  Currently unimplemented (Always 1)
     * @param startSize The start size of the particle
     * @param endSize The end size of the particle
     * @param loops Number times to loop its life cycle (-1 is infinite)
     * @param startDelay Number of seconds to delay before beginning rendering itself and movement at the start of a life cycle.  This only delays for the first loop iteration.
     * @param glTextureID Texture ID the particle should use.  Get this from engine or world.
     */
    public E3DParticle(E3DEngine engine, E3DVector3F startPosition, E3DVector3F startMovementDirection,
            		   double velocity, double life, 
            		   double startAlpha, double endAlpha, 
            		   double startSize, double endSize,
            		   int loops, double startDelay, String textureName)
    {
        this(engine, startPosition, startMovementDirection, velocity, life, startAlpha, endAlpha, startSize, endSize,
             loops, startDelay, textureName, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
    }

    public E3DParticle(E3DEngine engine, E3DVector3F startPosition, E3DVector3F startMovementDirection,
 		   double velocity, double life, 
 		   double startAlpha, double endAlpha, 
 		   double startSize, double endSize,
 		   int loops, double startDelay, E3DAnimatedTextureFrame[] animationFrames, int animationLoops)
	{
		this(engine, startPosition, startMovementDirection, velocity, life, startAlpha, endAlpha, startSize, endSize,
		  loops, startDelay, animationFrames, animationLoops, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
	}
    
    public E3DParticle(E3DEngine engine, E3DVector3F startPosition, E3DVector3F startMovementDirection,
 		   double velocity, double life, 
 		   double startAlpha, double endAlpha, 
 		   double startSize, double endSize,
 		   int loops, double startDelay, String textureName, E3DBlendMode blendMode)
	{
		super(engine, new E3DVector3F(startPosition), startSize, textureName, blendMode);
		
        init(startPosition, startMovementDirection, velocity, life, startAlpha, endAlpha, startSize, endSize, loops, startDelay);
	}    
    
    public E3DParticle(E3DEngine engine, E3DVector3F startPosition, E3DVector3F startMovementDirection,
  		   double velocity, double life, 
  		   double startAlpha, double endAlpha, 
  		   double startSize, double endSize,
  		   int loops, double startDelay, E3DAnimatedTextureFrame[] animationFrames, int animationLoops, E3DBlendMode blendMode)
    {
        super(engine, new E3DVector3F(startPosition), startSize, animationFrames, animationLoops, blendMode);
        
        init(startPosition, startMovementDirection, velocity, life, startAlpha, endAlpha, startSize, endSize, loops, startDelay);
    }

    private void init(E3DVector3F startPosition, E3DVector3F startMovementDirection,
  		   double velocity, double life, 
  		   double startAlpha, double endAlpha, 
  		   double startSize, double endSize,
  		   int loops, double startDelay)
    {
		setStartPosition(new E3DVector3F(startPosition));
		setStartMovementDirection(new E3DVector3F(startMovementDirection));
		setCurrentMovementDirection(new E3DVector3F(startMovementDirection));
		setVelocity(velocity);
		setLife(life); 
		setAlpha(startAlpha);
		setStartAlpha(startAlpha);
		setEndAlpha(endAlpha);
		setStartSize(startSize);
		setEndSize(endSize);
		setLoops(loops);
		setStartDelay(startDelay);
		
		addedToQuadList = false;
        
        //Always has a bounding sphere
        setBoundingObject(new E3DBoundingSphere(getEngine(), super.getSize() / 2.0));
        getBoundingObject().centerAroundParentObject(); //this is key
    }
    
    
    /**
     * Copy an existing particle
     * @param toCopy
     */
    public E3DParticle(E3DParticle toCopy)
    {
        this(toCopy.getEngine(), new E3DVector3F(toCopy.getOrientation().getPosition()), new E3DVector3F(toCopy.getStartMovementDirection()),
             toCopy.getVelocity(),toCopy.getLife(), toCopy.getStartAlpha(), toCopy.getEndAlpha(), 
             toCopy.getStartSize(), toCopy.getEndSize(), toCopy.getLoops(),toCopy.getStartDelay(), 
             ((E3DAnimatedTextureFrame[])toCopy.getAnimatedTexture().getAnimatedTextureFrames().toArray()), 
             toCopy.getAnimatedTexture().getLoops(), new E3DBlendMode(toCopy.getBlendMode()));
    }

    /**
     * Return the translation vector that will be used to translate the particle this frame with a lastFrameUpdateTime of lastFrameTimeSeconds
     * @param gravityDirection
     * @param gravityAmt
     * @return
     */
    public E3DVector3F getTranslationAmt(double lastFrameTimeSeconds)
    {
        currentFrameVelocity = lastFrameTimeSeconds * velocity;
        
        E3DVector3F gravityDirection = particleSystem.getGravityDirection();
        
        E3DVector3F translationVec = currentMovementDirection.scale(currentFrameVelocity);
   
        E3DVector3F scaledGravityVec = null;
        if(gravityDirection != null) //apply gravity if its specified
        {
            scaledGravityVec = gravityDirection.scale(lastFrameTimeSeconds);
            translationVec.addEqual(scaledGravityVec);
        }

        return translationVec;
    }

    /**
     * Update the particle - move in direction at the velocity speed.  
     * Also apply the gravity vector if its not null.  This is an engine speed based movement,
     * so it should move at a constant speed no matter how fast or slow the engine is currently running.
     * @param lastFrameTimeSeconds Pass in the last engine's frame time to use as a scalar for the movement velocity of hte particle (so its the same speed on fast or slow systems
     * Returns false if it needs to be removed from the particlesystem (its dead)
     */
    public boolean update(E3DActor curActor, double lastFrameTimeSeconds)
    {
        age += lastFrameTimeSeconds;
    	if(!isAlive())
    	{
            if(age > (life + startDelay)) //reset its life cycle if the age is greater than its life + the startDelay (start delay is factored in so it will live life seconds after start delay)
            {
            	if(!reset()) //start it up
            		return false; //kill it if its done looping
            }
            else
            	return true;
    	}
        
        if(!addedToQuadList)
        {
        	particleSystem.getRenderTree().getParticleHandler().add(this);
   //             particleSystem.getBlendAndTextureSortedParticleMap().addObject(this);
            addedToQuadList = true;
        }
        
        super.update(curActor, lastFrameTimeSeconds, getEngine().getFeatureChecker().isArbPointSpriteEnabled()); //no need to rotate if arbpoint sprite!!

        /******* Update its translation **********/
        /** This is reimplemented for speed instead of calling getTranslationAmt **/
        currentFrameVelocity = lastFrameTimeSeconds * velocity;
        E3DVector3F gravityDirection = particleSystem.getGravityDirection();
        E3DVector3F translationVec = currentMovementDirection.scale(currentFrameVelocity);
        E3DVector3F scaledGravityVec = null;
        if(gravityDirection != null) //apply gravity if its specified
        {
            scaledGravityVec = gravityDirection.scale(lastFrameTimeSeconds);
            translationVec.addEqual(scaledGravityVec);
        }
        
        updateSize(lastFrameTimeSeconds);
        updateAlpha(lastFrameTimeSeconds);
        
        E3DCollision collision = null;
        //If the system is collisioncausing and this is collisioncausing
        if(getParticleSystem().isCollisionCausedByMovement() && isCollisionCausedByMovement())
        {
            collision = getEngine().getCollisionHandler().checkCollision(E3DCollisionHandler.COLLISIONTYPE_SOURCE_BOUNDINGOBJECT, E3DCollisionHandler.COLLISIONTYPE_COLLIDEE_BOUNDINGOBJECT_IFAVAIL, 
                            true, this, this.getOrientation().getPosition(), this.getOrientation().getPosition().add(translationVec));
            
        }
        //If there will be a collision, let the collisionHandler do something first
        boolean continueTranslateAfterCollision = true;
        if(collision != null)
            continueTranslateAfterCollision  = onCollisionDuringUpdate(collision);

        if(continueTranslateAfterCollision)
        {
            super.translate(translationVec);
            if(particleSystem.getGravityDirection() != null) //Gravity was in the translation vec, but it also needs to be applied directly to current movement direction because gravity compounds over time
                currentMovementDirection.addEqual(scaledGravityVec);
        }

        rehashIfNeeded(); //rehash after changes are made to this particle
        
        return true;
    }
    
    private void updateSize(double lastFrameTimeSeconds)
    {
    	if(startSize == endSize)
    		return;
    	
    	if((startSize > endSize && getSize() < endSize) || //growing
    		(startSize < endSize && getSize() > endSize)) //shrinking
    	{
			setSize(endSize);
			return;
    	}

    	double sizeLeft = endSize - getSize();
    	double lifeLeft = life - age;
    	
    	if(lifeLeft <= 0)
    		setSize(endSize);
    	else
    		setSize(getSize() + ((sizeLeft / lifeLeft) * lastFrameTimeSeconds));//(lifeLeft * lastFrameTimeSeconds));
    }
    
    private void updateAlpha(double lastFrameTimeSeconds)
    {
    	if(startAlpha == endAlpha)
    		return;
     	if((startAlpha > endAlpha && getAlpha() < endAlpha) || //growing
    		(startAlpha < endAlpha && getAlpha() > endAlpha)) //shrinking
    	{
			setAlpha(endAlpha);
			return;
    	}
    	
    	double lifeLeft = life - age;
    	double alphaLeft = endAlpha - getAlpha();

    	if(lifeLeft <= 0)
    		setAlpha(endAlpha);
    	else
    		setAlpha(getAlpha() + ((alphaLeft / lifeLeft) * lastFrameTimeSeconds));//(lifeLeft * lastFrameTimeSeconds));
    }
    
    private boolean reset()
    {
        //Reset its position
        E3DVector3F translationVec = startPosition.subtract(getOrientation().getPosition()); //get vec to move the triangle vertices back to
        super.translate(translationVec); //move the particles position. Do not effect its startposition so call the supers translate

        //Reset its orientation to the start orientation
        currentMovementDirection = new E3DVector3F(startMovementDirection);
        
        startDelay = 0.0;  //Start delay only applies for the first loop
        age = 0.0;
        
        setSize(startSize);
        setAlpha(startAlpha);
        
        if(loops != -1)
        {
            curLoop++;
            if(curLoop > loops)
                return false; //kill it, its done
        }
        return true;
    }
    
    public boolean isAlive()
    {
        return (age >= startDelay) && (age < life+startDelay);
    }
    
    /**
     * Render a single particle.  This is handled by the engine but available here for debuggin
     */
    public void render() 
    {
        getEngine().getGeometryRenderer().renderParticle(this);
        /*
        if(getEngine().getFeatureChecker().isArbPointSpriteEnabled())
            getEngine().getGeometryRenderer().renderParticle(this);
        else
            super.render();*/
    }

    /**
     * Get how many seconds the particle will live after the start delay of the life cycle has expired.
     * @return
     */
    public double getLife() {
        return life;
    }
    /**
     * Set how many seconds the particle will live.  This time begins after the start delay of a life cycle has expired.
     * @param life
     */
    public void setLife(double life) {
        this.life = life;
    }
    /**
     * Get the velocity of the particle. 
     * @return
     */
    public double getVelocity() {
        return velocity;
    }
    /**
     * Set the velocity the particle travels at.  This gets scaled by the speed the engine is running automatically
     * @param velocity
     */
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    
    /**
     * Get number of loops the particle lives. -1 is infinity
     * @return
     */
    public int getLoops() {
        return loops;
    }
    /**
     * Set number of loops the particle lives. -1 is infinity
     * @return
     */
    public void setLoops(int loops) {
        this.loops = loops;
    }
    
    /**
     * The position the particle starts at the beginning of a loop
     * @return
     */
    public E3DVector3F getStartPosition() {
        return startPosition;
    }
    /**
     * Set the position the particle starts at the beginning of a loop
     * @param startPosition
     */
    public void setStartPosition(E3DVector3F startPosition) {
        this.startPosition = startPosition;
    }
    
    public int getCurLoop() {
        return curLoop;
    }
    
    public double getAge() {
        return age;
    }

    /**
     * Get the alpha the particle is at when its at the end of its loops (between 0 and 1)
     * @return  Value between 0 and 1, 1 being opaque
     * TODO: Implement alpha.  This is currently not supported in particles
     */
    public double getEndAlpha() {
        return endAlpha;
    }
    /**
     * Set the alpha the particle is at when its at the end of its loops (between 0 and 1)
     * @param startAlpha  Value between 0 and 1, 1 being opaque
     * TODO: Implement alpha.  This is currently not supported in particles
     */    
    public void setEndAlpha(double endAlpha) {
        this.endAlpha = endAlpha;
    }
    /**
     * Get the alpha the particle starts with (between 0 and 1)
     * @return  Value between 0 and 1, 1 being opaque
     * TODO: Implement alpha.  This is currently not supported in particles
     */
    public double getStartAlpha() {
        return startAlpha;
    }
    /**
     * Set the alpha the particle begins at (between 0 and 1)
     * @param startAlpha  Value between 0 and 1, 1 being opaque
     * TODO: Implement alpha.  This is currently not supported in particles
     */
    public void setStartAlpha(double startAlpha) {
        this.startAlpha = startAlpha;
    }
   
    /**
     * Get the delay the particle waits at the beginning of a loop before it begins moving (Seconds). This only delays for the first loop iteration.
     * @return
     */
    public double getStartDelay() {
        return startDelay;
    }
    /**
     * Set the delay the particle waits at the beginning of a loop before it begins rendering and moving (Seconds).  This only delays for the first loop iteration.
     * @return
     */
    public void setStartDelay(double startDelay) {
        this.startDelay = startDelay;
    }
    /**
     * Get the particlesystem the particle lives in
     * @return
     */
    public E3DParticleSystem getParticleSystem() {
        return particleSystem;
    }
    
    protected void setParticleSystem(E3DParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
    }
    
    /**
     * Translate particle geometry and startPosition
     */
    public void translate(E3DVector3F translationVec)
    {
        super.translate(translationVec); //move the particles position. Do not effect its startposition so call the supers translate
        startPosition.addEqual(translationVec); //move the startPosition too.
    }
    
    /**
     * Rotate the the direction the particle faces.  This is unnecessary in most cases
     * for a particle since they always billboard.  This rotation
     * will be overriden the next frame
     * 
     * @param angle Radian angle
     * @param upVec Normalised up vector to use as the rotation axis
     * 
     * Calls rotate with permanently set to TRUE
     */
    public void rotate(double angle, E3DVector3F upVec)
    {
        super.rotate(angle, upVec);
 //       rotateMovementDirection(angle, upVec, false);
    }

    /**
     * Rotate the particle around upVec angle amount. This rotates the movement direction as well
     * as other necessary internal vectors.
     * 
     * @param angle Radian angle
     * @param upVec Normalised up vector to use as the rotation axis
     * @param permanently False if the particle should reset to its original movement direction at the end of its life cycle.  True if it
     *   should retain this orientation for this and the rest of its life cycles
     */
    public void rotateMovementDirection(double angle, E3DVector3F upVec, boolean permanently)
    {
        if(angle == 0)
		    return;

     //   super.rotate(angle, upVec);

        currentMovementDirection.rotateEqual(angle, upVec);
		currentMovementDirection.normaliseEqual(); //Should stay normalised, but small errors could compound..
		if(permanently) //If it should permanently rotation, make a copy of the rotated vector (don't re-rotate. Its too slow)
		    startMovementDirection = new E3DVector3F(currentMovementDirection);
    }
    
    /**
     * Intercept collision with actor callbacks
     */
    abstract public void onCollisionActor(E3DCollision collision);
    
    /**
     * Intercept collision with sprite callbacks
     */
    abstract public void onCollisionSprite(E3DCollision collision);    
    
    /**
     * This callback gets fired when the particle hits something in the world.  Actors don't have this callback, but 
     * particles do since they're movement is done automatically by the engine
     * @param collision
     * @return
     *   Return true or false.  True will make the particle continue with its normal automated movement after a collision,
     *   false will stop it from moving during the frames where there is a collision.  
     */
    abstract public boolean onCollisionDuringUpdate(E3DCollision collision);
    
	abstract public E3DParticle onGetClone() throws Exception;
    
    
	/**
	 * Get the normalised direction the particle is set to move within this life cycle.  This 
	 * direction resets to startMovementDirection at the end of its life cycle.
	 * @return
	 */
	public E3DVector3F getCurrentMovementDirection() {
        return currentMovementDirection;
    }
    
    /**
     * Set the direction the particle should move within this life cycle.  It will reset to
     * startMovementDirection at the end of the life cycle.  This vector will be auto-normalised.
     * 
     * @param currentMovementDirection
     */
    public void setCurrentMovementDirection(E3DVector3F currentMovementDirection) {
        currentMovementDirection.normaliseEqual();
        this.currentMovementDirection = currentMovementDirection;
    }

    /**
     * Get the normalised direction the particle should move at the beginning of its life cycle.  This will
     * overwrite currentMovementDirection after the end of its life cycle.
     * 
     * @return
     */
    public E3DVector3F getStartMovementDirection() {
        return startMovementDirection;
    }
    
    /**
     * Set the direction the particle should move at the beginning of its life cycles.  This will
     * overwrite currentMovementDirection after the end of its life cycle.  This vector will be auto-normalised
     * @param startMovementDirection
     */
    public void setStartMovementDirection(E3DVector3F startMovementDirection) {
        startMovementDirection.normaliseEqual();
        this.startMovementDirection = startMovementDirection;
    }
    
    /**
     * This will always return the scaled velocity the particle was last moved at.
     * Useful in collision detection to figure out when the particle collided with another object
     * @return
     */
    public double getCurrentFrameVelocity() {
        return currentFrameVelocity;
    }
    	
	/**
	 * Set this to true if the particle in the system needs to cause collisions when it moves.  If set to 
	 * false, it will not cause any collision callbacks to be hit when it moves.  This must also
	 * be set to true in the particle's particle system (A false setting in the particle system
	 * overrides this setting).
	 * 
	 * This does not automatically make it uncollideable.  It just keeps itself from causing
	 * collisions with its own movement.  Other objects movement can still run into this
	 * particle if isCollideable is set to true for the particle and particle system.
	 * 
	 * @return
	 */
	abstract public boolean isCollisionCausedByMovement();
	
  
    public void setTexture(E3DTexture texture) {
        super.setTexture(texture);
        if(addedToQuadList)
            needRehash = true;
    }
    
    public void setTextureDetail0(E3DTexture textureDetail0) {
    	super.setTextureDetail0(textureDetail0);
        if(addedToQuadList)
            needRehash = true;
    }

    public void setTextureDetail1(E3DTexture textureDetail1) {
    	super.setTextureDetail1(textureDetail1);
        if(addedToQuadList)
            needRehash = true;
    }
    
    public void setRenderMode(E3DRenderMode renderMode) {
    	super.setRenderMode(renderMode);
        if(addedToQuadList)
            needRehash = true;
    }
    
    public void setBlendMode(E3DBlendMode blendMode) {
    	super.setBlendMode(blendMode);
        if(addedToQuadList)
            needRehash = true;
    }

    public void setSize(double size)
    {
    	super.setSize(size);
        if(addedToQuadList)
            needRehash = true;
    }

    private void rehashIfNeeded()
    {
        if(needRehash && addedToQuadList)
            getParticleSystem().getRenderTree().getParticleHandler().rehash(this);

    }
    
    public void setAlpha(double alpha)
    {
    	super.setAlpha(alpha);
        if(addedToQuadList)
            needRehash = true;
    }
    
    public void setAnimatedTexture(E3DAnimatedTexture animatedTexture)
    {
    	super.setAnimatedTexture(animatedTexture);
        if(addedToQuadList)
            needRehash = true;
    }

    public void setQuad(E3DQuad quad)
    {
    	super.setQuad(quad);
        if(addedToQuadList)
            needRehash = true;
    }
    
    public double getEndSize() {
		return endSize;
	}

	public void setEndSize(double endSize) {
		this.endSize = endSize;
	}

	public double getStartSize() {
		return startSize;
	}

	public void setStartSize(double startSize) {
		this.startSize = startSize;
	}
	
    public boolean equals(Object arg0)
    {
      	if(arg0 == null || !(arg0 instanceof E3DImage))
    		return false;
    	return arg0 == this;
    }
    
    public int hashCode()
    {
    	return super.hashCode();
    }	
}
