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
package espresso3d.engine.world.sector.actor.skeleton.animation;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.world.sector.actor.skeleton.E3DBone;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
abstract public class E3DAnimationCommand extends E3DEngineItem 
{
	private String commandKey;
	
    E3DAnimationKeyFrame keyFrame;
    
    private E3DBone manipulatedBone;
    private String boneID;
    private double executionTime;
    private double elapsedTime;
    
    //Calculated here, but used by inheritted objects
    protected double frameInterpolationAmount;
    
//    private double holdTimeAmt = 0.0;
    public E3DAnimationCommand(E3DEngine engine, String boneID, double executionTime)
    {
        super(engine);
        init(boneID, executionTime);
    }
    
    public E3DAnimationCommand(E3DAnimationCommand toCopyCommand)
    {
        super(toCopyCommand.getEngine());
        init(toCopyCommand.getBoneID(), toCopyCommand.executionTime);
        this.keyFrame = toCopyCommand.getKeyFrame();
        this.manipulatedBone = toCopyCommand.getManipulatedBone();
    }
    
    private void init(String boneID, double executionTime)
    {
        this.boneID = boneID;
        this.executionTime = executionTime;
    	elapsedTime = 0.0;
    	commandKey = null; //must be lazily loaded after being added to an animation so e have that data
    }
    abstract public E3DAnimationCommand getClone();

    public E3DAnimationKeyFrame getKeyFrame() {
        return keyFrame;
    }
    
    /**
     * The keyframe gets set automatically when added to a keyframe
     * @param keyFrame
     */
    public void setKeyFrame(E3DAnimationKeyFrame keyFrame) {
        this.keyFrame = keyFrame;
    }
    
    /**
     * This will take a bone (and all sub bones) from where it is currently, to where it should  be within timeLeftToPerform.
     * It will interpolate the move over the keyframe's time
     * @param timeLeftToPerform
     * @param lastFrameTimeSeconds
     * @return
     * 	returns False if the command is finished.
     */
    public boolean executeCommand(double lastFrameTimeSeconds)
    {
    	if(lastFrameTimeSeconds < E3DConstants.DBL_PRECISION_ERROR)
    		return true;
    	
    	elapsedTime += lastFrameTimeSeconds;
    	
//    	if(lastFrameTimeSeconds + holdTimeAmt < 0.02) //only interpolate so much
//    	{
//    		holdTimeAmt += lastFrameTimeSeconds;
//    		return true;
//    	}
//    	else
//    	{
//    		lastFrameTimeSeconds += holdTimeAmt;
//    		holdTimeAmt = 0;
//    	}
//    	double interpolation = getTimeLeft() / lastFrameTimeSeconds;
//    	if(E3DConstants.closeToPrecisionError(interpolation))
//    		return true;
    
    	double denom = executionTime / elapsedTime;
    	if(denom <= 0)
    		return false;
    	
    	double interpolation = 1.0 / denom;
    	if(interpolation < 0)
    		interpolation = 0;
    	else if(interpolation > 1)
    		interpolation = 1; 
    	
    	setFrameInterpolationAmount(interpolation);
    	
    	executeCommand();
    	
    	if(elapsedTime >= getExecutionTime())
    		return false; //false is completed
    	else
    		return true;
    }
    
    abstract public void executeCommand();
    /**
     * Called when command has finished to reset any local variables needed
     */
    public void reset()
    {
    	elapsedTime = 0.0;
    	resetCommand();
    }
    
    abstract void resetCommand();
 
    public String getBoneID() {
        return boneID;
    }

	public double getElapsedTime()
	{
		return elapsedTime;
	}

	public double getExecutionTime()
	{
		return executionTime;
	}

	public double getTimeLeft()
	{
		return executionTime - elapsedTime;
	}
	
	public E3DBone getManipulatedBone()
	{
		if(manipulatedBone == null)
			manipulatedBone = getKeyFrame().getAnimation().getSkeleton().findBoneByID(boneID);
		return manipulatedBone;
	}

	protected double getFrameInterpolationAmount()
	{
		return frameInterpolationAmount;
	}

	protected void setFrameInterpolationAmount(double frameInterpolationAmount)
	{
		this.frameInterpolationAmount = frameInterpolationAmount;
	}
	
	public String getCommandKey()
	{
		if(commandKey == null)
			buildCommandKey();
		return commandKey;
	}
	
	/**
	 * Builds the unique key for this command
	 *
	 */
	private void buildCommandKey()
	{
		commandKey = this.toString();
//		commandKey = keyFrame.getKeyFrameID() + "|" + boneID + "|" + getClass().getName() + "|" + buildCommandKeyEnd();
	}
	
//	protected abstract String buildCommandKeyEnd();

	public void setExecutionTime(double executionTime)
	{
		this.executionTime = executionTime;
	}

	void setElapsedTime(double elapsedTime)
	{
		this.elapsedTime = elapsedTime;
	}
}
