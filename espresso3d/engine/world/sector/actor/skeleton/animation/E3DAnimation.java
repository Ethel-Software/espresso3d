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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.sector.actor.skeleton.E3DSkeleton;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DAnimation extends E3DEngineItem 
{
	private static final double MAX_FPS_FOR_INTERPOLATION = 60;
	
    private String animationID;
    
    private E3DSkeleton skeleton;
    private HashMap keyFrames;
    private ArrayList orderedKeyFrames; //use seperate list to keep order instead of treemap for speed
    
    private E3DAnimationKeyFrame currentKeyFrame;
    private int currentKeyFrameNum;

    private HashMap runningCommands; //commands are frame independent once they get started.  Frames just tell them when to start.
    private HashMap stoppedCommands;
    
    //Used only for copies when the animation starts
    private int loops;
    private int curLoop = 0;
    
    private double interpolationTotalFrameTime = 0.0;
    
    public E3DAnimation(E3DEngine engine, String animationID)
    {
        super(engine);
        this.keyFrames = new HashMap();
        this.orderedKeyFrames = new ArrayList();
        this.runningCommands = new HashMap();
        this.stoppedCommands = new HashMap();
        currentKeyFrame = null;
        
        this.animationID = animationID;
    }
    
    public E3DAnimation(E3DAnimation toCopyAnimation)
    {
        super(toCopyAnimation.getEngine());
        this.keyFrames = new HashMap();
        this.orderedKeyFrames = new ArrayList();
        this.runningCommands = new HashMap(); //don't need to copy the state of the animation as of now
        this.stoppedCommands = new HashMap();
        currentKeyFrame = null;
        
        this.animationID = toCopyAnimation.getAnimationID();
        this.skeleton = toCopyAnimation.getSkeleton();
        //Copy keyframes, use ordered information
        E3DAnimationKeyFrame keyFrame = null, copiedKeyFrame = null;
        for(int i=0; i<toCopyAnimation.getOrderedKeyFrames().size(); i++)
        {
            keyFrame = (E3DAnimationKeyFrame)toCopyAnimation.getOrderedKeyFrames().get(i);
            
            copiedKeyFrame = new E3DAnimationKeyFrame(keyFrame);
            
            //If its the current one, copy that info
            if(toCopyAnimation.getCurrentKeyFrame() == keyFrame)
            {
                this.currentKeyFrame = copiedKeyFrame;
                this.currentKeyFrameNum = toCopyAnimation.getCurrentKeyFrameNum();
            }
            
            addKeyFrame(copiedKeyFrame);
        }
        
    }
    
    /**
     * Add a keyframe to the animation.  The order the keyframes are animated will be the same order they
     *  are added to the animation
     * @param keyFrame
     */
    public void addKeyFrame(E3DAnimationKeyFrame keyFrame)
    {
        keyFrames.put(keyFrame.getKeyFrameID(), keyFrame);
        orderedKeyFrames.add(keyFrame);
        keyFrame.setAnimation(this);
    }
    
    public void removeKeyFrame(E3DAnimationKeyFrame keyFrame)
    {
        orderedKeyFrames.remove(keyFrame);
        if(keyFrames.containsKey(keyFrame.getKeyFrameID()))
            keyFrames.remove(keyFrame.getKeyFrameID());
    }

    public void removeKeyFrame(String keyFrameID)
    {
        E3DAnimationKeyFrame keyFrame = getKeyFrame(keyFrameID);
        if(keyFrame != null)
            removeKeyFrame(keyFrame);
    }

    public E3DAnimationKeyFrame getKeyFrame(String keyFrameID)
    {
        if(keyFrames.containsKey(keyFrameID))
            return (E3DAnimationKeyFrame)keyFrames.get(keyFrameID);
        else
            return null;
    }
    
    public void reset()
    {
        if(orderedKeyFrames.size() <= 0)
            currentKeyFrame = null;
        else
        {
//	        resetCommandsForAnimation();	        
        	
	        //Must reset keyframes in reverse order, which reset commands in reverse order
	        for(int i=orderedKeyFrames.size()-1; i >= 0; i--)
	        {
	            currentKeyFrame = (E3DAnimationKeyFrame)orderedKeyFrames.get(i);
	            currentKeyFrame.reset();
	        }

	        //rotations, then translations??
	        
	        Iterator it;
	        boolean hadTransformation = false;
	        E3DAnimationCommand command;

//	        System.out.println(currentKeyFrameNum + " " + currentKeyFrame.getElapsedTime() + " " + currentKeyFrame.getCommands().size());
//	        System.out.println("Resetting: Running: " + runningCommands.size());
	        if(runningCommands != null && !runningCommands.isEmpty())
	        {
	        	it = runningCommands.entrySet().iterator();
	        	while(it.hasNext())
	        	{
	        		command = (E3DAnimationCommand)((Map.Entry)it.next()).getValue();
	        		hadTransformation = true;
	        		it.remove();
		        	command.reset();
	        	}
	        }
	        
//	        System.out.println("Resetting: Stopped: " + stoppedCommands.size());
	        if(stoppedCommands != null && !stoppedCommands.isEmpty())
	        {
	        	it = stoppedCommands.entrySet().iterator();
	        	while(it.hasNext())
	        	{
	        		command = (E3DAnimationCommand)((Map.Entry)it.next()).getValue();
	        		hadTransformation = true;
	        		it.remove();
		        	command.reset();
	        	}
	        }
	        
	        runningCommands.clear();
	        stoppedCommands.clear();
	        
	        currentKeyFrameNum = 0; 
            currentKeyFrame = (E3DAnimationKeyFrame)orderedKeyFrames.get(currentKeyFrameNum);

            //currentKeyFrame will now be at 0

//	        if(hadTransformation)
//	        	this.skeleton.getRootBone().applyQueuedTransformations();
        }
    }
    
    public void resetTest()
    {
        if(orderedKeyFrames.size() <= 0)
            currentKeyFrame = null;
        else
        {
        	currentKeyFrameNum = 0;
//	        resetCommandsForAnimation();	        


        	//Some commands happen over a number of frames, so we have to split it up
        	ArrayList nextFrameCommands = new ArrayList();
            ArrayList thisFrameCommands = new ArrayList();
            E3DAnimationCommand command;
            E3DAnimationCommandRotate thisFrameRotateCommand, nextFrameRotateCommand;
            E3DAnimationCommandTranslate thisFrameTranslateCommand, nextFrameTranslateCommand;
            
            ArrayList commands = new ArrayList();
	        for(int i=0; i < orderedKeyFrames.size(); i++)
	        {
	            currentKeyFrame = (E3DAnimationKeyFrame)orderedKeyFrames.get(i);
	            commands.clear();
	            if(!nextFrameCommands.isEmpty())
	            {
	            	commands.addAll(nextFrameCommands);
	            	nextFrameCommands.clear();
	            }
	            commands.addAll(currentKeyFrame.getCommands());
	            //Current keyframe commands, see if they need to split or can just be reset
	            for(int a=0; a < commands.size(); a++)
	            {
	            	command = (E3DAnimationCommand)commands.get(a);
	            	if(command.getExecutionTime() > currentKeyFrame.getFrameLength()) //needs to split
	            	{
	            		if(command instanceof E3DAnimationCommandTranslate)
	            		{
//	            			System.out.println("Splitting translation..");
//		            		System.out.println("ExecutionTime: " + command.getExecutionTime() + " FrameLength: " + currentKeyFrame.getFrameLength());
		            		
	            			E3DAnimationCommandTranslate totalCommand = (E3DAnimationCommandTranslate)command;
	            			if(runningCommands.containsKey(totalCommand.getCommandKey()))
	            				totalCommand = (E3DAnimationCommandTranslate)runningCommands.get(totalCommand.getCommandKey());
	            			else if(stoppedCommands.containsKey(totalCommand.getCommandKey()))
	            				totalCommand = (E3DAnimationCommandTranslate)stoppedCommands.get(totalCommand.getCommandKey());
	            					
	            			thisFrameTranslateCommand = new E3DAnimationCommandTranslate(totalCommand);
	            			nextFrameTranslateCommand = new E3DAnimationCommandTranslate(totalCommand);

	            			thisFrameTranslateCommand.getCurTranslationAmt().scaleEqual(currentKeyFrame.getFrameLength() / command.getExecutionTime());
	            			thisFrameCommands.add(thisFrameTranslateCommand); 
	            			nextFrameCommands.add(nextFrameTranslateCommand);
	            			
	            			totalCommand.getCurTranslationAmt().set(0, 0, 0);
	            		}
	            		else if(command instanceof E3DAnimationCommandRotate)
	            		{
//	            			System.out.println("Splitting rotate..");
//		            		System.out.println("ExecutionTime: " + command.getExecutionTime() + " FrameLength: " + currentKeyFrame.getFrameLength());
	            			E3DAnimationCommandRotate totalCommand = (E3DAnimationCommandRotate)command;
	            			if(i == 0)
	            				System.out.println("Rotation: " + totalCommand.getCurRotationAmt());
	            			if(runningCommands.containsKey(totalCommand.getCommandKey()))
	            			{
	            				totalCommand = (E3DAnimationCommandRotate)runningCommands.get(totalCommand.getCommandKey());
	            				runningCommands.remove(totalCommand.getCommandKey());
	            			}
	            			else if(stoppedCommands.containsKey(totalCommand.getCommandKey()))
	            			{
	            				totalCommand = (E3DAnimationCommandRotate)stoppedCommands.get(totalCommand.getCommandKey());
	            				stoppedCommands.remove(totalCommand.getCommandKey());
	            			}

	            			thisFrameRotateCommand = new E3DAnimationCommandRotate(totalCommand);

            				thisFrameRotateCommand.getCurRotationAmt().scaleEqual(currentKeyFrame.getFrameLength() / command.getExecutionTime());
	            			nextFrameRotateCommand = new E3DAnimationCommandRotate(totalCommand);
	            			nextFrameRotateCommand.getCurRotationAmt().set(totalCommand.getCurRotationAmt().subtract(thisFrameRotateCommand.getCurRotationAmt()));
	            			nextFrameRotateCommand.setExecutionTime(command.getExecutionTime() - currentKeyFrame.getFrameLength());
	            			nextFrameCommands.add(nextFrameRotateCommand);
	            			thisFrameCommands.add(thisFrameRotateCommand); 
	            			
	            			totalCommand.getCurRotationAmt().set(0, 0, 0); //have to reset for the next time
	            		}
	            	}
	            	else
	            		thisFrameCommands.add(command);
	            	
	          		command.setElapsedTime(0);
	            }

	            if(thisFrameCommands.size() > 0)
	            {
		            for(int a=thisFrameCommands.size() - 1; a >= 0; a--)
		            {
		            	command = (E3DAnimationCommand)thisFrameCommands.get(a);

		            	command.reset();
		            }
//	            	this.skeleton.getRootBone().applyQueuedTransformations();
		            
	            	thisFrameCommands.clear();
	            }
	            
	            currentKeyFrame.reset();
	        }
	        
	        //any leftover
	        if(nextFrameCommands.size() > 0)
	        {
	            for(int a=nextFrameCommands.size() - 1; a >= 0; a--)
	            {
	            	command = (E3DAnimationCommand)nextFrameCommands.get(a);
	            	command.reset();
	            }
	            
	        }
        	this.skeleton.getRootBone().applyQueuedTransformations();
	        currentKeyFrameNum = 0;
            currentKeyFrame = (E3DAnimationKeyFrame)orderedKeyFrames.get(0);
            currentKeyFrame.resetTimer();

        	
//	        //Must reset keyframes in reverse order, which reset commands in reverse order
//	        for(int i=orderedKeyFrames.size()-1; i >= 0; i--)
//	        {
//	            currentKeyFrame = (E3DAnimationKeyFrame)orderedKeyFrames.get(i);
//	            
//	            ArrayList commands = currentKeyFrame.getCommands();
//	            ArrayList rotCommands = new ArrayList();
//	            ArrayList transCommands = new ArrayList();
//	            
//	            for(int a=commands.size() -1 ; a > 0; a--)
//	            {
//	            	if(commands.get(a) instanceof E3DAnimationCommandRotate)
//	            		rotCommands.add(commands.get(a));
//	            	else if(commands.get(a) instanceof E3DAnimationCommandTranslate)
//	            		transCommands.add(commands.get(a));
//	            }
//	            
//	            for(int a=0; a < rotCommands.size(); a++)
//	            {
//	            	((E3DAnimationCommandRotate)rotCommands.get(a)).reset();
//	            }
//	            
//	            for(int a=0; a < transCommands.size(); a++)
//	            {
//	            	((E3DAnimationCommandTranslate)transCommands.get(a)).reset();
//	            }
//	            
//	            currentKeyFrame.reset();
//	        	this.skeleton.getRootBone().applyQueuedTransformations();
//	        }

	        //rotations, then translations??
	        
/*	        Iterator it;
	        boolean hadTransformation = false;
	        E3DAnimationCommand command;
	        
	        if(runningCommands != null && !runningCommands.isEmpty())
	        {
	        	it = runningCommands.entrySet().iterator();
	        	while(it.hasNext())
	        	{
	        		command = (E3DAnimationCommand)((Map.Entry)it.next()).getValue();
	        		hadTransformation = true;
	        		it.remove();
		        	command.reset();
	        	}
	        }
	        
	        if(stoppedCommands != null && !stoppedCommands.isEmpty())
	        {
	        	it = stoppedCommands.entrySet().iterator();
	        	while(it.hasNext())
	        	{
	        		command = (E3DAnimationCommand)((Map.Entry)it.next()).getValue();
	        		hadTransformation = true;
	        		it.remove();
		        	command.reset();
	        	}
	        }
	        */
	        runningCommands.clear();
	        stoppedCommands.clear();
	        currentKeyFrameNum = 0; 
            //currentKeyFrame will now be at 0

//	        if(hadTransformation)
        }
    }
    
    
    public boolean update(double lastFrameTimeSeconds)
    {
        if(currentKeyFrame == null)
        {
        	currentKeyFrameNum = 0;
        	if(orderedKeyFrames.size() > 0 )
        		currentKeyFrame = (E3DAnimationKeyFrame)orderedKeyFrames.get(currentKeyFrameNum);
        }
//            reset();
        
        if(currentKeyFrame != null && !currentKeyFrame.update(lastFrameTimeSeconds))
        {
            if(currentKeyFrameNum >= orderedKeyFrames.size() - 1)
            {
            	System.out.println("Resetting");
//            	resetCommandsForAnimation();
                reset();
 //           	resetTest();             
                if(loops >= 0)
                {
                	curLoop++;
	                if(curLoop >= loops)
	                {
	                	curLoop = 0;
	                	return false;
	                }
                }
                else
                	curLoop = 0;
            }
            else
            {
                currentKeyFrameNum++;
                currentKeyFrame = (E3DAnimationKeyFrame)orderedKeyFrames.get(currentKeyFrameNum);
                currentKeyFrame.resetTimer();
            }
        }
    
//        interpolationTotalFrameTime += lastFrameTimeSeconds;
//        if(interpolationTotalFrameTime >= 1.0/MAX_FPS_FOR_INTERPOLATION)
//        {
	        if(runningCommands != null && runningCommands.size() > 0)
	        {
		        Iterator it = runningCommands.entrySet().iterator();
		        while(it.hasNext())
		    	{
		    		E3DAnimationCommand command = (E3DAnimationCommand)((Map.Entry)it.next()).getValue();
		    		if(!command.executeCommand(lastFrameTimeSeconds))
		    		{
		    			it.remove();
		    			stoppedCommands.put(command.getCommandKey(), command);  		
		    		}
		    	}
	        }
//        	interpolationTotalFrameTime = 0.0;
//        }
    	return true;
    }
    
    public String getAnimationID() {
        return animationID;
    }
    
    
    /**
     *  This will rotate all the keyframes' commands along with the actor so animation will stay oriented with the actor
     *  
     *  This is normally called only internally by the engine (Actor as it rotates)
     *  
     * @param angle
     * @param aroundVec
     */
    public void rotate(double angle, E3DVector3F aroundVec)
    {
        E3DAnimationKeyFrame keyFrame = null;
        for(int i=0; i<orderedKeyFrames.size(); i++)
        {
            keyFrame = (E3DAnimationKeyFrame)orderedKeyFrames.get(i);
            keyFrame.rotate(angle, aroundVec);
        }        
    }
    
    public E3DSkeleton getSkeleton() {
        return skeleton;
    }
    public void setSkeleton(E3DSkeleton skeleton) {
        this.skeleton = skeleton;
    }
    public E3DAnimationKeyFrame getCurrentKeyFrame() {
        return currentKeyFrame;
    }
    public HashMap getKeyFrames() {
        return keyFrames;
    }
    public ArrayList getOrderedKeyFrames() {
        return orderedKeyFrames;
    }
    public int getCurrentKeyFrameNum() {
        return currentKeyFrameNum;
    }

	public HashMap getRunningCommands()
	{
		return runningCommands;
	}

	void addRunningCommand(E3DAnimationCommand command)
	{
		runningCommands.put(command.getCommandKey(), command);
	}
	
	void removeRunningCommand(E3DAnimationCommand command)
	{
		runningCommands.remove(command.getCommandKey());
	}

	public int getLoops()
	{
		return loops;
	}

	public void setLoops(int loops)
	{
		this.loops = loops;
	}

}
