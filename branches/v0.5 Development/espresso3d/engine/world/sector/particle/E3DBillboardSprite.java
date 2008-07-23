/*
 * Created on Nov 10, 2004
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
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * @author Curt
 *
 * A billboarding sprite is a sprite that will stay facing a given actor no matter what position that actor
 * is at.  The sprite can be locked on axis' so it won't change along that axis if needed.
 *
 * 
 * Collision detection for particles is based on its bounding quad.
 *
 */

public abstract class E3DBillboardSprite extends E3DSprite 
{
	private static final int DEFAULT_BLEND_MODE = E3DBlendMode.BLENDMODE_BLEND;

	private boolean lockX, lockY, lockZ;
    private E3DOrientation movementOrientation;
    
    public E3DBillboardSprite(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, String textureName)
    {
        this(engine, position, forward, up, size, textureName, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
    }

    public E3DBillboardSprite(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, E3DAnimatedTextureFrame[] animationFrames, int animationLoops)
    {
        this(engine, position, forward, up, size, animationFrames, animationLoops, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
    }

    public E3DBillboardSprite(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, String textureName, E3DBlendMode blendMode)
    {
        super(engine, position, forward, up, size, textureName, blendMode);
        lockX = false;
        lockY = false;
        lockZ = false;
        init(engine, position, forward, up);        
    }

    public E3DBillboardSprite(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, E3DAnimatedTextureFrame[] animationFrames, int animationLoops, E3DBlendMode blendMode)
    {
        super(engine, position, forward, up, size, animationFrames, animationLoops, blendMode);
        lockX = false;
        lockY = false;
        lockZ = false;
        init(engine, position, forward, up);        

    }

    public E3DBillboardSprite(E3DEngine engine, E3DVector3F position, double size, String textureName)
    {
        this(engine, position, size, textureName, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
    }
 
    public E3DBillboardSprite(E3DEngine engine, E3DVector3F position, double size, E3DAnimatedTextureFrame[] animationFrames, int animationLoops)
    {
        this(engine, position, size, animationFrames, animationLoops, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
    }

    public E3DBillboardSprite(E3DEngine engine, E3DVector3F position, double size, String textureName, E3DBlendMode blendMode)
    {
        super(engine, position, size, textureName, blendMode);
        init(engine, position, null, null);        
    }

    public E3DBillboardSprite(E3DEngine engine, E3DVector3F position, double size, E3DAnimatedTextureFrame[] animationFrames, int animationLoops, E3DBlendMode blendMode)
    {
        super(engine, position, size, animationFrames, animationLoops, blendMode);
        init(engine, position, null, null);        
    }
    
    private void init(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up)
    {
        movementOrientation = new E3DOrientation(getEngine());
        movementOrientation.setPosition(new E3DVector3F(position));
        if(forward != null)
        	movementOrientation.setForward(forward);
        if(up != null)
        	movementOrientation.setUp(up);
    }
    /**
     * Calling this makes the billboarding sprite face lookAtActor
     * @param lookAtActor The actor the billboard will turn to face
     * @param lastFrameTimeSeconds The length of time the last frame took to render (for updating the animation if necessary)
     * @param skipRotate makes it skip the rotation piece
     */
    public boolean update(E3DActor lookAtActor, double lastFrameTimeSeconds, boolean skipRotate)
    {
        boolean success = super.update(lookAtActor, lastFrameTimeSeconds);
        
        if(skipRotate || lookAtActor == null)
            return success;

        E3DVector3F actPositionVec = new E3DVector3F(lookAtActor.getOrientation().getPosition());
        //If its locked on axis', don't modify that axis
        E3DVector3F forward = getOrientation().getForward();
        if(lockX)
            actPositionVec.setX(forward.getX());
        if(lockY)
            actPositionVec.setY(forward.getY());
        if(lockZ)
            actPositionVec.setZ(forward.getZ());
        
        E3DVector3F directionVec = actPositionVec.subtract(getMovementOrientation().getPosition());//.subtract(actPositionVec); //get a vec pointing from the sprite to the actor
        if(directionVec.closeTo(0.0, 0.0 ,0.0))
        	return true;
        directionVec.normaliseEqual(); //normalise it

        E3DVector3F upVec = forward.crossProduct(directionVec);
        if(upVec.closeTo(0.0, 0.0, 0.0))
            upVec.set(new E3DVector3F(getOrientation().getUp()));
        upVec.normaliseEqual();

        //Only rotate the sprite, not movement direction, so call supers rotate
        double angle = forward.angleBetweenRads(directionVec);
        if(angle >= E3DConstants.DBL_PRECISION_ERROR || angle <= E3DConstants.DBL_PRECISION_ERROR)
            super.rotate(forward.angleBetweenRads(directionVec),  upVec);

        return success;
    }
    
    public boolean update(E3DActor lookAtActor, double lastFrameTimeSeconds) {
        return this.update(lookAtActor, lastFrameTimeSeconds, false);
    }

    public void rotate(double angle, E3DVector3F upVec) {
    	super.rotate(angle, upVec);
        movementOrientation.rotate(angle, upVec);
    }
    
    public void translate(E3DVector3F translationAmt) {
    	super.translate(translationAmt);
    	movementOrientation.translate(translationAmt);
    }
    
    
    
    public void setPosition(E3DVector3F position) {
    	super.setPosition(position);
    	movementOrientation.setPosition(position);
    }
    
    /**
     * True if the sprite is locked in the X axis (the forward vec won't move in this direction if locked)
     * @return
     */
    public boolean isLockX() {
        return lockX;
    }
    
    /**
     * Set if the sprite should be locked on the X axis (the forward vec won't move in this direction if locked)
     * @param lockX
     */
    public void setLockX(boolean lockX) {
        this.lockX = lockX;
    }
    /**
     * True if the sprite is locked in the Y axis (the forward vec won't move in this direction if locked)
     * @return
     */
    public boolean isLockY() {
        return lockY;
    }
    /**
     * Set if the sprite should be locked on the Y axis (the forward vec won't move in this direction if locked)
     * @param lockY
     */
    public void setLockY(boolean lockY) {
        this.lockY = lockY;
    }
    /**
     * True if the sprite is locked in the Z axis (the forward vec won't move in this direction if locked)
     * @return
     */
    public boolean isLockZ() {
        return lockZ;
    }
    /**
     * Set if the sprite should be locked on the Z axis (the forward vec won't move in this direction if locked)
     * @param lockZ
     */
    public void setLockZ(boolean lockZ) {
        this.lockZ = lockZ;
    }
    
    
    abstract public void onCollisionActor(E3DCollision collision);
    
    abstract public void onCollisionSprite(E3DCollision collision);

	public E3DOrientation getMovementOrientation() {
		return movementOrientation;
	}        
}
