/*
 * Created on Mar 3, 2005
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
package espresso3d.engine.world.sector.actor.skeleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.string.E3DStringHelper;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.actor.skeleton.animation.E3DAnimation;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DSkeleton extends E3DRenderable 
{
    private E3DActor actor;
    private E3DBone rootBone;
    
    private HashMap animations; //All animations registered with the skeleton
    private HashMap currentAnimations; //all the animations that are currently running  (or need to run) on the skeleton
    
    private boolean needReset = false; //set to true when no animations are running to be sure to reset the model correctly
    
    public E3DSkeleton(E3DEngine engine)
    {
        super(engine);
//        this.actor = actor; //actor gets set when added to an actor
        
        this.animations = new HashMap();
        this.currentAnimations = new HashMap();
    }
    
    public E3DSkeleton(E3DSkeleton toCopySkeleton, E3DActor newActor)
    {
        super(toCopySkeleton.getEngine());
        this.actor = newActor;

        this.animations = new HashMap();
        this.currentAnimations = new HashMap();

        //Copy bone groups (which in-turn rebuild the entire bone-group's structure recursivelyi)
        if(toCopySkeleton != null && toCopySkeleton.getRootBone() != null)
            this.rootBone = new E3DBone(toCopySkeleton.getRootBone());
        
        //Copy animation info
        if(toCopySkeleton.getAnimations() != null)
        {
            Map.Entry entry;
            Iterator it = toCopySkeleton.getAnimations().entrySet().iterator();
            E3DAnimation animation = null, copiedAnimation = null;
            while(it.hasNext())
            {
                entry = (Map.Entry)it.next();
                animation = (E3DAnimation)entry.getValue();
                
                copiedAnimation = new E3DAnimation(animation);
                
                //If its in the current animation, add that to our current animation list too
                if(toCopySkeleton.getCurrentAnimations().containsKey(copiedAnimation.getAnimationID()))
                    currentAnimations.put(copiedAnimation.getAnimationID(), copiedAnimation);
                
                addAnimation(copiedAnimation);
            }
        }
    }

    /* (non-Javadoc)
     * @see espresso3d.engine.renderer.base.E3DRenderable#renderAtPosition(espresso3d.engine.lowlevel.vector.E3DVector3F)
     */
    public void render() 
    {
    	if(rootBone == null)
    		return;

 //   	System.out.println("====RENDER START====");
    	
    	getEngine().getGeometryRenderer().initSolidAndLineRendering();
		rootBone.render(); //should recurse
    }

    public E3DActor getActor() {
        return actor;
    }
    
    public void rotate(double angle, E3DVector3F aroundVec)
    {
    	if(rootBone != null)
    	{
        	E3DOrientation orientation = new E3DOrientation(getEngine());
        	orientation.rotate(angle, aroundVec);

	    	rootBone.addTransformationToQueue(orientation.getOrientationMatrix());
	    	rootBone.applyQueuedTransformations();
    	}
    }
    
    public void rotateWithoutAttachedVertices(double angle, E3DVector3F aroundVec)
    {
    	if(rootBone != null)
    	{
        	E3DOrientation orientation = new E3DOrientation(getEngine());
        	orientation.rotate(angle, aroundVec);

	    	rootBone.addTransformationToQueue(orientation.getOrientationMatrix());
	    	rootBone.applyQueuedTransformationsWithoutVertices();
    	}
    }

    public void translate(E3DVector3F translationAmt)
    {
    	if(rootBone != null)
    	{
        	E3DOrientation orientation = new E3DOrientation(getEngine());
        	orientation.translate(translationAmt);

	    	rootBone.addTransformationToQueue(orientation.getOrientationMatrix());
	    	rootBone.applyQueuedTransformations();
    	}
    }

    public void translateWithoutAttachedVertices(E3DVector3F translationAmt)
    {
    	if(rootBone != null)
    	{
        	E3DOrientation orientation = new E3DOrientation(getEngine());
        	orientation.translate(translationAmt);

	    	rootBone.addTransformationToQueue(orientation.getOrientationMatrix());
	    	rootBone.applyQueuedTransformationsWithoutVertices();
    	}
    }
    
    /**
     * Reset a skeleton to its startPosition
     */
    public void reset()
    {
    	if(needReset)
    	{
    		rootBone.resetAll();
    		needReset = false;
    	}
    }
    
    public HashMap getAnimations() {
        return animations;
    }
    
    public void addAnimation(E3DAnimation animation){
        animations.put(animation.getAnimationID(), animation);
        animation.setSkeleton(this);
    }

    public void removeAnimation(String animationID)
    {
        if(animations.containsKey(animationID))
        {
            E3DAnimation animation = (E3DAnimation)animations.get(animationID);
            currentAnimations.remove(animation);
            animations.remove(animationID);
        }
    }
    
    public void stopAnimation(String animationID)
    {
        if(currentAnimations.containsKey(animationID))
        {
            E3DAnimation animation = (E3DAnimation)currentAnimations.get(animationID);
            currentAnimations.remove(animationID);
            animation.reset();
        }
    }
    
    /**
     * 
     * @param animationID
     * @param loops -1 is infinity or until stopped
     */
    public void startAnimation(String animationID, int loops)
    {
        if(animations.containsKey(animationID))
        {
            E3DAnimation animation = (E3DAnimation)animations.get(animationID);
            E3DAnimation copiedAnimation = new E3DAnimation(animation);
            copiedAnimation.setLoops(loops);
            currentAnimations.put(copiedAnimation.getAnimationID(), copiedAnimation);
            needReset = true;
//            animation.reset();
        }        
    }
  
    public HashMap getCurrentAnimations() {
        return currentAnimations;
    }
    
    public void update(double lastFrameTimeSeconds)
    {
    	if(currentAnimations.isEmpty())
    		return;
    	
    	E3DAnimation animation = null;
        Iterator it = currentAnimations.entrySet().iterator();
        Map.Entry entry;
        
        while(it.hasNext())
        {
        	entry = (Map.Entry)it.next();
        	animation = (E3DAnimation)entry.getValue();
            if(!animation.update(lastFrameTimeSeconds))
            {
            	stopAnimation(animation.getAnimationID());
            }
        }
        
        rootBone.applyQueuedTransformations();
        
        if(currentAnimations.isEmpty())
        {
        	reset();
        }
    }
    
    
    public void setActor(E3DActor actor) {
        this.actor = actor;
    }

	public E3DBone getRootBone() {
		return rootBone;
	}
	
	/**
	 * Add a bone to the skeleton.
	 * If parentBoneID is null or empty, it will become the root
	 * bone of this skeleton.
	 * @param bone  Bone to add
	 * @param parentBoneID Parent bone ID or null
	 * @return
	 *  True if the bone was added (parentBoneFound or rootBone)
	 *  or false if parent couldn't be located.  During object load
	 *  since loading can be out of order, this doesn't mean the bone
	 *  won't exist later, so hold a queue of them and keep trying.
	 */
	public boolean addBone(E3DBone bone, String parentBoneID)
	{
		if(bone == null)
			return false;
		bone.setSkeleton(this);

		//We need to rotate the bone to be with the actor when added
//		System.out.println("Actor orientation:\n" + getActor().getOrientation());
		bone.setCurrentOrientation(getActor().getOrientation().multiply(bone.getCurrentOrientation().getOrientationMatrix()));
		
		if(E3DStringHelper.isNullOrEmpty(parentBoneID))
		{
			rootBone = bone;
			return true;
		}
		
		if(rootBone == null)
			return false;

		E3DBone search = rootBone.findBoneByID(parentBoneID);
		if(search != null)
		{
			search.attachBone(bone);
			return true;
		}
		return false;
	}
	
	public E3DBone findBoneByID(String boneID)
	{
		if(E3DStringHelper.isNullOrEmpty(boneID))
			return null;
		
		if(rootBone == null)
			return null;
		
		return rootBone.findBoneByID(boneID);
	}
	
	public void scale(double scaleAmt)
	{
		scale(getRootBone(), scaleAmt);
	}
	
	public void scale(E3DBone curBone, double scaleAmt)
	{
		if(curBone == null)
			return;
		
		curBone.getStartOrientation().setPosition(curBone.getStartOrientation().getPosition().scale(scaleAmt));
		curBone.getCurrentOrientation().setPosition(curBone.getCurrentOrientation().getPosition().scale(scaleAmt));
		
		if(curBone.getChildBones() == null || curBone.getChildBones().isEmpty())
			return;
		
		Iterator it = curBone.getChildBones().entrySet().iterator();
		Map.Entry entry;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			scale((E3DBone)entry.getValue(), scaleAmt);
		}
	}
}
