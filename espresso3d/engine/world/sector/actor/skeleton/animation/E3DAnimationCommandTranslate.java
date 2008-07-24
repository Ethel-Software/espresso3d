/*
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
 * 
 */
package espresso3d.engine.world.sector.actor.skeleton.animation;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.sector.actor.skeleton.E3DBone;

public class E3DAnimationCommandTranslate extends E3DAnimationCommand 
{
//	private E3DOrientation translation;
	
	private E3DVector3F translationAmt;
	private E3DVector3F curTranslationAmt;
	
    public E3DAnimationCommandTranslate(E3DEngine engine, String boneID, double timeToExecuteCommand, E3DVector3F translationAmt)
    {
    	super(engine, boneID, timeToExecuteCommand);
    	this.translationAmt = translationAmt;
    	this.curTranslationAmt = new E3DVector3F(0, 0, 0);
    }
    
    public E3DAnimationCommandTranslate(E3DAnimationCommandTranslate toCopyCommand)
    {
    	super(toCopyCommand);
    	translationAmt = new E3DVector3F(toCopyCommand.translationAmt);
    	this.curTranslationAmt = new E3DVector3F(toCopyCommand.curTranslationAmt);
    }
    
    public E3DAnimationCommand getClone()
    {
    	return new E3DAnimationCommandTranslate(this);
    }

//    public void executeCommand() 
//    {
//    	E3DBone manipulatedBone = getManipulatedBone();
//    	if(manipulatedBone == null)
//    		return;
//
//    	E3DVector3F leftToTranslate = translationAmt.subtract(curTranslationAmt);
//    	if(leftToTranslate.equals(0.0, 0.0, 0.0))
//    		return;
//
//        double interpolation = getFrameInterpolationAmount();
//
//    	if(!E3DConstants.closeToPrecisionError(interpolation))
//    	{
//	    	double inverseInterpolate = 1.0 / interpolation;
//	    	E3DVector3F divider = new E3DVector3F(inverseInterpolate, inverseInterpolate, inverseInterpolate);
//	    	leftToTranslate.multiplyEqual(divider); //This is now the scaled translation amount for the frame
//    	}
//    	if(leftToTranslate.closeTo(0, 0, 0))
//    		return;
//    	
//    	if(!E3DConstants.closeToPrecisionError(leftToTranslate.getX()))
//    	{
//    		leftToTranslate.setX(leftToTranslate.getX());
//    		curTranslationAmt.setX(curTranslationAmt.getX() + leftToTranslate.getX());
//    	}
//    	if(!E3DConstants.closeToPrecisionError(leftToTranslate.getY()))
//    	{
//    		leftToTranslate.setY(leftToTranslate.getY());
//    		curTranslationAmt.setY(curTranslationAmt.getY() + leftToTranslate.getY());
//    	}
//    	if(!E3DConstants.closeToPrecisionError(leftToTranslate.getX()))
//    	{
//    		leftToTranslate.setZ(leftToTranslate.getZ());
//    		curTranslationAmt.setZ(curTranslationAmt.getZ() + leftToTranslate.getZ());
//    	}    	
//
//    	E3DOrientation translation = new E3DOrientation(getEngine());
//    	translation.translate(leftToTranslate);
//    	
//    	manipulatedBone.addTransformationToQueue(translation.getOrientationMatrix());
//    }
    

    public void executeCommand() 
    {
    	E3DBone manipulatedBone = getManipulatedBone();
    	if(manipulatedBone == null)
    		return;

    	E3DVector3F leftToTranslate = translationAmt.subtract(curTranslationAmt);
    	if(leftToTranslate.equals(0.0, 0.0, 0.0))
    		return;

        double interpolation = getFrameInterpolationAmount();

        E3DVector3F thisFrameTranslate;
        if(interpolation == 0)
        	return;
        else if(interpolation == 1)
        	thisFrameTranslate = translationAmt;
        else
        	thisFrameTranslate = translationAmt.scale(interpolation);
        
        manipulatedBone.addTranslationToQueue(thisFrameTranslate.subtract(curTranslationAmt));
        
        curTranslationAmt.set(thisFrameTranslate);
    }
    
    
    protected void resetCommand()
    {
		E3DBone manipulatedBone = getManipulatedBone();
    	if(manipulatedBone != null)
    		manipulatedBone.addPreTranslationToQueue(curTranslationAmt.scale(-1));

    	curTranslationAmt.set(0, 0, 0);
    }
    
//    protected String buildCommandKeyEnd()
//    {
//    	return translationAmt.toString();
//    }

	public E3DVector3F getCurTranslationAmt()
	{
		return curTranslationAmt;
	}
}
