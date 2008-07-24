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
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.geometry.E3DLine;
import espresso3d.engine.lowlevel.geometry.E3DVertex;
import espresso3d.engine.lowlevel.matrix.E3DMatrix4x4F;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.matrix.E3DQuaternion;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderable;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DBone extends E3DRenderable 
{
    private String boneID;

    private E3DSkeleton skeleton;

    private ArrayList attachedVertices;
    private E3DOrientation startOrientation;
    private E3DOrientation currentOrientation;
    
    private E3DOrientation queuedTransformation;
    private E3DQuaternion queuedRotations;
    private E3DQuaternion queuedParentRotations;
    private E3DVector3F queuedParentTranslations;
    
    private E3DQuaternion totalRotation;
    
    private E3DVector3F queuedTranslations;
    private E3DVector3F queuedPreTranslations;
    
    private E3DBone parentBone;
    private HashMap childBones;
    
    public E3DBone(E3DEngine engine, String boneID, E3DVector3F position, E3DVector3F forward, E3DVector3F up)
    {
        super(engine);
        attachedVertices = new ArrayList();
        childBones = new HashMap();

        this.boneID = boneID;
        startOrientation = new E3DOrientation(engine);
        startOrientation.setPosition(position);
        startOrientation.setUp(up);
        startOrientation.setForward(forward);
        //Make sure to make a copy
        currentOrientation = new E3DOrientation(startOrientation);
        
        //Accumulate transformations
        queuedTransformation = new E3DOrientation(engine);
        queuedRotations = new E3DQuaternion(engine);
        queuedParentRotations = new E3DQuaternion(engine);
        totalRotation = new E3DQuaternion(engine);
        queuedTranslations = new E3DVector3F();
        queuedPreTranslations = new E3DVector3F();
        queuedParentTranslations = new E3DVector3F();
    }
    
    //Copy a bone and recursively rebuild all sub-bones
    public E3DBone(E3DBone toCopyBone)
    {
        super(toCopyBone.getEngine());
        attachedVertices = new ArrayList(toCopyBone.getAttachedVertices());
        childBones = new HashMap();
        this.boneID = toCopyBone.getBoneID();

        startOrientation = new E3DOrientation(toCopyBone.getStartOrientation());
        currentOrientation = new E3DOrientation(toCopyBone.getCurrentOrientation());

        //Recursively copy all child bones now to recreate the bone structure
        Iterator it = toCopyBone.getChildBones().entrySet().iterator();
        Map.Entry entry = null;
        E3DBone childBone = null, copiedChildBone = null;
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            childBone = (E3DBone)entry.getValue();
            
            copiedChildBone = new E3DBone(childBone);
            attachBone(copiedChildBone);
        }
    }

    /* (non-Javadoc)
     * @see espresso3d.engine.renderer.base.E3DRenderable#renderAtPosition(espresso3d.engine.lowlevel.vector.E3DVector3F)
     */
    public void render() 
    {
        //Render the bone.  For now, use red for origin, green for end of bone
        getEngine().getGeometryRenderer().initSolidAndLineRendering();
  //      System.out.println("Rendering bone: " + boneID + " at position : " + getCurrentOrientation().getPosition());
        getCurrentOrientation().render(); //render the endpoint of the bone
        if(parentBone != null)
        {
            E3DLine line = new E3DLine(getEngine(), parentBone.getCurrentOrientation().getPosition(), getCurrentOrientation().getPosition(),
                                        new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(0.0, 1.0, 0.0));    
            getEngine().getGeometryRenderer().renderLine(line);
        }

        //Now call children's rendering
        Iterator it = childBones.entrySet().iterator();
        Map.Entry entry = null;
        E3DBone childBone = null;
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            childBone = (E3DBone)entry.getValue();
            
            childBone.render();
        }
    }

    /**
     * Attach a vertex to this bone
     * @param vertex
     */
    public void attachVertex(E3DVertex vertex)
    {
        attachedVertices.add(vertex);
    }

    /**
     * Attach a child bone to this bone.  This child will become property of this bone exclusively.
     * 
     * Bones must NOT be referenced by more than one other parent bone.  This will cause animation and other functions to completely fail.
     * IE: A bone can only be in 1 bone group and only have 1 parent -- so it can only be attached to 1 parent bone at a time
     */
    void attachBone(E3DBone bone)
    {
        childBones.put(bone.getBoneID(), bone);
        bone.parentBone = this;
        bone.setSkeleton(skeleton);
    }
    
    public void applyQueuedTransformations()
    {
    	//http://www.devmaster.net/forums/showthread.php?t=4590
    	
    	
    	E3DVector3F rotatedQueuedPreTranslations = currentOrientation.multiply(queuedPreTranslations);
    	currentOrientation.translate(rotatedQueuedPreTranslations);

    	E3DOrientation rotation = queuedParentRotations.multiply(queuedRotations).getRotationOrientation();
    	
    	E3DVector3F position = currentOrientation.getPosition();
    	
		E3DVector3F toParent = new E3DVector3F(0,0,0);
    	if(parentBone != null)
    	{
    		toParent = position.subtract(parentBone.getCurrentOrientation().getPosition()); //.subtract(parentBone.getCurrentOrientation().getPosition());
    		toParent = queuedParentRotations.getRotationMatrix().multiplyVector(toParent); //rotate it
    	}
    	
    	currentOrientation.setPosition(new E3DVector3F(0,0,0));
    	currentOrientation = rotation.multiply(currentOrientation.getOrientationMatrix());

    	if(parentBone != null)
    		currentOrientation.setPosition(parentBone.getCurrentOrientation().getPosition().add(toParent));
    	else
    		currentOrientation.setPosition(position);
    	
  		currentOrientation.translate(currentOrientation.multiply(queuedTranslations));

//  		 bone.setRotation(q1*q2); 
//  		 bone.setPosition( t1*q2 ).add( t2 )  		
  		
//    	currentOrientation.translate(rotatedQueuedPreTranslations);
//
//    	E3DOrientation queuedTransformation = new E3DOrientation(getEngine());
//    	queuedTransformation.multiplyEqual(queuedParentRotations.multiply(queuedRotations).getRotationMatrix());
//    	
//    	E3DVector3F position = currentOrientation.getPosition();
//    	currentOrientation.setPosition(new E3DVector3F(0,0,0));
//    	currentOrientation = queuedTransformation.multiply(currentOrientation.getOrientationMatrix());
//    	
//		currentOrientation.setPosition(position);
//    	
//  	currentOrientation.translate(currentOrientation.multiply(queuedTranslations));

  		if(attachedVertices != null && !attachedVertices.isEmpty())
    	{
    		//Apply the orientation change to the attached
    		for(int i=0; i < attachedVertices.size(); i++)
    		{
    			E3DVertex vertex = (E3DVertex)attachedVertices.get(i);

    			//Pre-translations (before rotating)
    			vertex.translate(rotatedQueuedPreTranslations);

    			vertex.translate(position.scale(-1));
    			vertex.setVertexPos(queuedParentRotations.multiply(queuedRotations).getRotationMatrix().multiplyVector(vertex.getVertexPos()));
    			
    			if(parentBone != null)
    				vertex.translate(parentBone.getCurrentOrientation().getPosition().add(toParent));
    	    	else
    	    		vertex.translate(position);
    			
//    			vertex(toParent);
//    			if(parentBone != null)
//    	    		currentOrientation.setPosition(parentBone.getCurrentOrientation().getPosition().add(toParent));
    			
    			
    			vertex.translate(currentOrientation.multiply(queuedTranslations));
    		}
    	}

    	if(childBones != null && !childBones.isEmpty())
    	{
	    	Iterator it = childBones.entrySet().iterator();
	    	while(it.hasNext())
	    	{
	    		//Make sure to propogate the transformation through the children
	    		E3DBone childBone = (E3DBone)((Map.Entry)it.next()).getValue();
//	    		childBone.addTransformationToQueue(queuedTransformation.getOrientationMatrix());

	    		childBone.addParentRotationToQueue(queuedParentRotations.multiply(queuedRotations));
	    		childBone.addParentTranslationToQueue(currentOrientation.multiply(queuedTranslations));
//	    		childBone.addParentRotationMatrixToQueue(queuedTransformation);
	    		childBone.addTranslationToQueue(currentOrientation.multiply(queuedTranslations));
	    		childBone.addPreTranslationToQueue(queuedPreTranslations);
	    		childBone.applyQueuedTransformations();
	    	}
    	}
    	
    	//Done with it, load identity
    	queuedTransformation.loadIdentity();
    	queuedRotations.loadIdentity();
    	queuedTranslations.set(0,0,0);
    	queuedPreTranslations.set(0,0,0);
    	queuedParentRotations.loadIdentity();
    	queuedParentTranslations.set(0,0,0);
    }
    
    public void applyQueuedTransformationsWithoutVertices()
    {
    	currentOrientation = queuedTransformation.multiply(currentOrientation.getOrientationMatrix()); //.multiplyEqual(queuedTransformation.getOrientationMatrix());

    	if(childBones != null && !childBones.isEmpty())
    	{
	    	Iterator it = childBones.entrySet().iterator();
	    	while(it.hasNext())
	    	{
	    		//Make sure to propogate the transformation through the children
	    		E3DBone childBone = (E3DBone)((Map.Entry)it.next()).getValue();
	    		childBone.addTransformationToQueue(queuedTransformation.getOrientationMatrix());
	    		
	    		childBone.applyQueuedTransformationsWithoutVertices();
	    	}
    	}
    	
    	//Done with it, load identity
    	queuedTransformation.loadIdentity();
    }
    /**
      *Applies a transformation to the currently queued transformation that will
      *be applied when applyQueuedTransformation is called.
     */
    public void addTransformationToQueue(E3DMatrix4x4F transformation)
    {
    	queuedTransformation.multiplyEqual(transformation);
    }
    
    public void addRotationToQueue(E3DQuaternion rotationQuat)
    {
    	queuedRotations.multiplyEqual(rotationQuat);
    	queuedRotations.normaliseEqual();
    }
    
    public void addParentRotationToQueue(E3DQuaternion rotationQuat)
    {
    	queuedParentRotations.multiplyEqual(rotationQuat);
    	queuedParentRotations.normaliseEqual();
    	
    }
    public void addParentTranslationToQueue(E3DVector3F translation)
    {
    	queuedParentTranslations.addEqual(translation);
    }
    
    public void addTranslationToQueue(E3DVector3F translation)
    {
    	queuedTranslations.addEqual(translation);
    }
    
    public void addPreTranslationToQueue(E3DVector3F translation)
    {
    	queuedPreTranslations.addEqual(translation);
    }
    /**
     * Translate the bone and all attached vertices with the bone and all children bones)
     * @param translationAmt
     */
    public void translateBone(E3DVector3F translationAmt)
    {
    	currentOrientation.translate(translationAmt);
        E3DVertex vertex;
        E3DVector3F vertexPos = null;
        for(int i=0; i < attachedVertices.size(); i++)
        {
            vertex = (E3DVertex)attachedVertices.get(i);
            vertexPos = vertex.getVertexPos();
            vertexPos.addEqual(translationAmt);
        }

        //Do all children
        Iterator it = childBones.entrySet().iterator();
        Map.Entry entry = null;
        E3DBone childBone = null;
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            childBone = (E3DBone)entry.getValue();
            
            childBone.translateBone(translationAmt); //translate it to move it as much as this one moved
        }
        
    }
    
    /**
     * This is used by the skeleton when the entire skeleton needs to rotate to stay oriented with the actor
     * This should not modify the attached vertices because the actor's rotation will already do that.
     * This just provides a way to keep it oriented correctly.
     * @param angle
     */
    public void rotateBoneWithoutVertices(double angle, E3DVector3F aroundVec)
    {

       	//Not enough to bother
    	if(angle <= E3DConstants.DBL_PRECISION_ERROR && 
    	   angle >= -E3DConstants.DBL_PRECISION_ERROR)
    		return;
    	
    	currentOrientation.rotate(angle, aroundVec);

        Iterator it = childBones.entrySet().iterator();
        Map.Entry entry = null;
        E3DBone childBone = null;
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            childBone = (E3DBone)entry.getValue();
            
//            childBone.translateBoneWithoutVertices(translationAmt); //translate it to move it as much as this one moved
            childBone.rotateBoneWithoutVertices(angle, aroundVec);
//            childBone.rotateBoneAroundParentWithoutVertices(angle, aroundVec, this); //rotate it after translation so it keeps the same angle of orientation with its parent
        }
    }
    
    public void rotateBoneWithChildren(E3DQuaternion rotation)
    {
    	if(attachedVertices != null && !attachedVertices.isEmpty())
    	{
    		//Apply the orientation change to the attached
    		for(int i=0; i < attachedVertices.size(); i++)
    		{
    			E3DVertex vertex = (E3DVertex)attachedVertices.get(i);
//    			vertex.setVertexPos(queuedTransformation.getWorldVector(vertex.getVertexPos()));
    			vertex.setVertexPos(rotation.getRotationOrientation().getWorldVector(vertex.getVertexPos()));
    		}
    	}

    	if(childBones != null && !childBones.isEmpty())
    	{
	    	Iterator it = childBones.entrySet().iterator();
	    	while(it.hasNext())
	    	{
	    		//Make sure to propogate the transformation through the children
	    		E3DBone childBone = (E3DBone)((Map.Entry)it.next()).getValue();
	    		childBone.rotateBoneWithChildren(rotation);
	    	}
    	}
    }

    public void rotateBoneWithoutChildren(E3DQuaternion rotation)
    {
    	if(attachedVertices != null && !attachedVertices.isEmpty())
    	{
    		//Apply the orientation change to the attached
    		for(int i=0; i < attachedVertices.size(); i++)
    		{
    			E3DVertex vertex = (E3DVertex)attachedVertices.get(i);
//    			vertex.setVertexPos(queuedTransformation.getWorldVector(vertex.getVertexPos()));
    			vertex.setVertexPos(rotation.getRotationOrientation().getWorldVector(vertex.getVertexPos()));
    		}
    	}
    }

    /**
     * Translates a bone without actually translating linked vertices.
     * This is only used by the skeleton for something like an entire actor translation
     * to keep it oriented correctly with the actor
     * @param translationAmt
     */
    public void translateBoneWithoutVertices(E3DVector3F translationAmt)
    {
    	currentOrientation.translate(translationAmt);
    	
        Iterator it = childBones.entrySet().iterator();
        Map.Entry entry = null;
        E3DBone childBone = null;
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            childBone = (E3DBone)entry.getValue();
            
            childBone.translateBoneWithoutVertices(translationAmt);
        }
    	
    }

    /**
     * This will reset all animation for the bone and all child bones recursively.  It ensures
     * that the new position of the bone will be centered around the actor position correctly (not the origin as
     * would be specified by the bone definition)
     */
    public void resetAll()
    {
    	E3DVector3F translation = getCurrentOrientation().getPosition().subtract(getStartOrientation().getPosition());
    	this.translateBone(translation);
    	
    	E3DQuaternion currentOrien = new E3DQuaternion(getEngine());
    	currentOrien.setByRotationMatrix(getCurrentOrientation());
    	
    	E3DQuaternion startOrien = new E3DQuaternion(getEngine());
    	startOrien.setByRotationMatrix(getStartOrientation());
    	E3DQuaternion rotation = startOrien.slerp(currentOrien, 1, 0).invert();
    	
    	this.rotateBoneWithoutChildren(rotation);
    	
    	Iterator it = childBones.entrySet().iterator();
    	Map.Entry entry = null;
    	while(it.hasNext())
    	{
    		entry = (Map.Entry)it.next();
    		E3DBone bone = (E3DBone)entry.getValue();
    		
    		bone.resetAll();
    	}
    }
    
    public ArrayList getAttachedVertices() {
        return attachedVertices;
    }
    public void setAttachedVertices(ArrayList attachedVertices) {
        this.attachedVertices = attachedVertices;
    }

    public String getBoneID() {
        return boneID;
    }
    public HashMap getChildBones() {
        return childBones;
    }
    public E3DBone getParentBone() {
        return parentBone;
    }
    public E3DSkeleton getSkeleton() {
        return skeleton;
    }
    public void setSkeleton(E3DSkeleton skeleton) {
        this.skeleton = skeleton;
    }

    /**
     * Searches this and all sub-bones for the correct bone with ID == boneID
     * @param boneID
     * @return Either a bone with boneID == boneID or null
     */
    public E3DBone findBoneByID(String boneID)
    {
        if(boneID == null)
            return null;
        
        if(boneID.equals(this.boneID))
            return this;
        else
        {
            Iterator it = childBones.entrySet().iterator();
            Map.Entry entry = null;
            E3DBone searchBone = null;
            E3DBone foundBone = null;
            
            while(it.hasNext())
            {
                entry = (Map.Entry)it.next();
                searchBone = (E3DBone)entry.getValue();
                
                foundBone = searchBone.findBoneByID(boneID);
                if(foundBone != null)
                    return foundBone;
            }
        }
        
        return null;
    }

	public E3DOrientation getStartOrientation() {
		return startOrientation;
	}

	public E3DOrientation getCurrentOrientation() {
		return currentOrientation;
	}

	public E3DOrientation getQueuedTransformation()
	{
		return queuedTransformation;
	}

	public void setQueuedTransformation(
			E3DOrientation queuedTransformation)
	{
		this.queuedTransformation = queuedTransformation;
	}

	void setCurrentOrientation(E3DOrientation currentOrientation)
	{
		this.currentOrientation = currentOrientation;
	}
}
