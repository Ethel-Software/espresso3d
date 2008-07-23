/*
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
 * 
 */
package espresso3d.engine.world.sector.actor.skeleton.animation;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.matrix.E3DMatrix4x4F;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.matrix.E3DQuaternion;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.rendertree.nodes.E3DOrientationNode;
import espresso3d.engine.world.sector.actor.skeleton.E3DBone;

public class E3DAnimationCommandRotate extends E3DAnimationCommand 
{
	E3DVector3F rotationAmt;
	E3DVector3F curRotationAmt;
	
	E3DOrientation curRotationMatrix;
	
	
	E3DQuaternion startQuat;
	E3DQuaternion curRotationQuat;
	E3DQuaternion destQuat;
	double lastInterpolation=0.0;
	double totalInterpolation = 0.0;
	
	double lastFrameTime = 0.0;
	
    public E3DAnimationCommandRotate(E3DEngine engine, String boneID, double timeToExecuteCommand, E3DVector3F rotation)
    {
        super(engine, boneID, timeToExecuteCommand);
        this.rotationAmt = rotation;
        curRotationAmt = new E3DVector3F(0, 0, 0);
        curRotationMatrix = new E3DOrientation(engine);
    }
    
    public E3DAnimationCommandRotate(E3DAnimationCommandRotate toCopyCommand)
    {
    	super(toCopyCommand);
    	rotationAmt = new E3DVector3F(toCopyCommand.rotationAmt);
        this.curRotationAmt = new E3DVector3F(toCopyCommand.curRotationAmt);
        this.curRotationMatrix = new E3DOrientation(toCopyCommand.curRotationMatrix);

        if(toCopyCommand.curRotationQuat != null)
        	curRotationQuat = new E3DQuaternion(toCopyCommand.curRotationQuat);

    }

    public E3DAnimationCommand getClone()
    {
    	return new E3DAnimationCommandRotate(this);
    }
    
//    public void executeCommand() 
//    {
//    	E3DBone manipulatedBone = getManipulatedBone();
//    	if(manipulatedBone == null)
//    		return;
//
//    	E3DVector3F scaledRotationAmts = new E3DVector3F(rotationAmt.subtract(curRotationAmt));
//
//    	double interpolation = getFrameInterpolationAmount();
//    	if(!E3DConstants.closeToPrecisionError(interpolation))
//    	{
//	    	scaledRotationAmts.setX(scaledRotationAmts.getX() / interpolation);
//	    	scaledRotationAmts.setY(scaledRotationAmts.getY() / interpolation);
//	    	scaledRotationAmts.setZ(scaledRotationAmts.getZ() / interpolation);
//    	}
//    	if(scaledRotationAmts.closeTo(0, 0, 0))
//    		return;
//    	
//    	E3DOrientation rotation = new E3DOrientation(getEngine());
//    	if(!E3DConstants.closeToPrecisionError(scaledRotationAmts.getX()))
//    	{
//    		rotation.rotateXEqual(scaledRotationAmts.getX());
//    		curRotationAmt.setX(curRotationAmt.getX() + scaledRotationAmts.getX());
//    	}
//    	if(!E3DConstants.closeToPrecisionError(scaledRotationAmts.getY()))
//    	{
//    		rotation.rotateYEqual(scaledRotationAmts.getY());
//    		curRotationAmt.setY(curRotationAmt.getY() + scaledRotationAmts.getY());
//    	}
//    	if(!E3DConstants.closeToPrecisionError(scaledRotationAmts.getX()))
//    	{
//    		rotation.rotateZEqual(scaledRotationAmts.getZ());
//    		curRotationAmt.setZ(curRotationAmt.getZ() + scaledRotationAmts.getZ());
//    	}
//
//    	rotation.normaliseForwardAndUpEqual();
//    	curRotationMatrix.multiplyEqual(rotation.getOrientationMatrix());
//    	curRotationMatrix.normaliseForwardAndUpEqual();
//    	
//    	manipulatedBone.addTransformationToQueue(rotation.getOrientationMatrix());
//    }
//    
    public void executeCommand() 
    {
    	E3DBone manipulatedBone = getManipulatedBone();
    	if(manipulatedBone == null)
    		return;

    	//If we haven't set these, then set them
    	if(startQuat == null && destQuat == null)
    	{
	    	startQuat = new E3DQuaternion(getEngine());
//	    	E3DOrientation currentOrientation = new E3DOrientation(manipulatedBone.getCurrentOrientation());
//	    	currentOrientation.setPosition(new E3DVector3F(0,0,0));
//	    	startQuat.setByRotationMatrix(currentOrientation.getOrientationMatrix());
	    	startQuat.normaliseEqual();
	    	
	    	System.out.println("Start quat: " + startQuat);
    		destQuat = new E3DQuaternion(getEngine());
	    	E3DOrientation destOrientation = new E3DOrientation(manipulatedBone.getStartOrientation());
	    	destOrientation.setPosition(new E3DVector3F(0,0,0));

//    		destQuat.setByRotationMatrix(destOrientation.getOrientationMatrix()); //its destination is based off where it should end up on the unmodified skeleton.  Its start orientation is dependant on where it is now
    		destQuat.rotateQuatFromEulerEqual(rotationAmt.getX(), rotationAmt.getY(), rotationAmt.getZ());
//    		destQuat.normaliseEqual();
    		
    		destQuat = startQuat.multiply(destQuat);
    		destQuat.normaliseEqual();
    		System.out.println("Dest quat: " + destQuat);
    	}

    	double interpolation = getFrameInterpolationAmount();
//    	double denom = super.getExecutionTime() / super.getElapsedTime();
//    	if(denom <= 0)
//    		return;
//    	
//    	double interpolation = 1.0 / denom;
//    	if(interpolation < 0 || interpolation > 1)
//    		return;

    	E3DQuaternion rotationQuat;
    	
    	if(interpolation <= 0)
    		return;
    	else
	    	rotationQuat = startQuat.slerp(destQuat, interpolation, 0).normalise();

    	rotationQuat.normaliseEqual();
//    	if(!rotationQuat.equals(0,0,0,1))
//    	{
    		if(curRotationQuat != null)
    		{
//    			manipulatedBone.addRotationToQueue(curRotationQuat.invert().normalise());
//    			manipulatedBone.addRotationToQueue(rotationQuat);
        		manipulatedBone.addRotationToQueue(rotationQuat.multiply(curRotationQuat.invert()));
    		}
    		else
        		manipulatedBone.addRotationToQueue(rotationQuat);
    		
    		curRotationQuat = rotationQuat;
//    	}
    	
    	/////////////////////
//    	double timeDelta = 
//    
//    
//    	float timeDelta = curFrame.m_time-prevFrame.m_time;
//		float interpValue = ( float )(( time-prevFrame.m_time )/timeDelta );
//
//		assert( interpValue >= 0 && interpValue <= 1 );
//
//#if 0
//		Quaternion qPrev( prevFrame.m_parameter );
//		Quaternion qCur( curFrame.m_parameter );
//		Quaternion qFinal( qPrev, qCur, interpValue );
//		transform.setRotationQuaternion( qFinal );    
//    
//    
//    
//    
    
    }

    
    //This uses matrices
//    protected void resetCommand()
//    {
//    	E3DBone manipulatedBone = getManipulatedBone();
//    	if(manipulatedBone != null)
//    	{
////    		curRotationMatrix.normaliseForwardAndUp();
//    		curRotationMatrix.invertFastEqual();
//	        
//        	manipulatedBone.addTransformationToQueue(curRotationMatrix.getOrientationMatrix());
//    	}
//    	curRotationMatrix.loadIdentity();
//    	curRotationAmt.set(0, 0, 0);
//    }
    
//    This uses quaternions
    protected void resetCommand()
    {
    	System.out.println("Reset");
    	E3DBone manipulatedBone = getManipulatedBone();
    	if(manipulatedBone != null && curRotationQuat != null)
    	{
//    		System.out.println(manipulatedBone.getCurrentOrientation());
//    		curRotationMatrix.normaliseForwardAndUp();
    		
    		
//        	E3DQuaternion rotationQuat = startQuat.slerp(destQuat, 1, 0).invert(); //destQuat.slerp(, 1, 0);
//        	rotationQuat.normaliseEqual();
//        	if(!rotationQuat.equals(0,0,0,1))
//        	{
//           		manipulatedBone.addRotationToQueue(rotationQuat);
        		manipulatedBone.addRotationToQueue(curRotationQuat.invert());
//        	}
//        	System.out.println(curRotationQuat.getRotationOrientation());
//    		curRotationQuat.invertEqual();
//    		curRotationQuat.normaliseEqual();
//        	System.out.println(curRotationQuat.getRotationOrientation());
//    		curRotationMatrix.invertFastEqual();
//    		manipulatedBone.addTransformationToQueue(manipulatedBone.getStartOrientation().getOrientationMatrix().invertFast());
//        	getManipulatedBone().getSkeleton().getRootBone().applyQueuedTransformations();
//        	manipulatedBone.addTransformationToQueue(curRotationQuat.getRotationMatrix());
    	}
        startQuat = null;
        destQuat = null;
//    	curRotationMatrix.loadIdentity();
//    	curRotationAmt.set(0, 0, 0);
    	totalInterpolation = 0.0;
    	
    	curRotationQuat = null;
    	
    	lastFrameTime = 0.0;
    	
    }

    
    //    protected String buildCommandKeyEnd()
//    {
//    	return rotationAmt.toString();
//    }

	public E3DVector3F getRotationAmt()
	{
		return rotationAmt;
	}

	public void setRotationAmt(E3DVector3F rotationAmt)
	{
		this.rotationAmt = rotationAmt;
	}

	public E3DVector3F getCurRotationAmt()
	{
		return curRotationAmt;
	}

}
