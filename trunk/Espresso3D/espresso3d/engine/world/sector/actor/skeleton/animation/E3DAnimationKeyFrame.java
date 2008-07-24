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

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.lowlevel.vector.E3DVector3F;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DAnimationKeyFrame extends E3DEngineItem 
{
	private E3DAnimation animation;
	
    private String keyFrameID;
    private double frameLength; //seconds
    private double elapsedTime;
    private ArrayList commands;
    
    private boolean commandsStarted;
    
    /**
     * Create a keyframe named keyFrameID and have the frame last frameLength seconds
     * @param engine
     * @param keyFrameID ID of the keyframe
     * @param frameLength Length of time the frame should take to complete (Seconds)
     */
    public E3DAnimationKeyFrame(E3DEngine engine, String keyFrameID, double frameLength)
    {
        super(engine);
        this.keyFrameID = keyFrameID;
        this.frameLength = frameLength;
        elapsedTime = 0.0;
        commands = new ArrayList();
        commandsStarted = false;
    }

    public E3DAnimationKeyFrame(E3DAnimationKeyFrame toCopyAnimationKeyFrame)
    {
        super(toCopyAnimationKeyFrame.getEngine());
        this.keyFrameID = toCopyAnimationKeyFrame.getKeyFrameID();
        this.frameLength = toCopyAnimationKeyFrame.getFrameLength();
        this.elapsedTime = toCopyAnimationKeyFrame.getElapsedTime();
        commands = new ArrayList();
        commandsStarted = false;
        
        E3DAnimationCommand command = null, copiedCommand = null;
        for(int i=0; i < toCopyAnimationKeyFrame.getCommands().size(); i++)
        {
            command = (E3DAnimationCommand)toCopyAnimationKeyFrame.getCommands().get(i);
            
            if(command instanceof E3DAnimationCommandRotate)
            	copiedCommand = new E3DAnimationCommandRotate((E3DAnimationCommandRotate)command);
            else
            	copiedCommand = new E3DAnimationCommandTranslate((E3DAnimationCommandTranslate)command);
            addAnimationCommand(copiedCommand);
        }
        
        this.animation = toCopyAnimationKeyFrame.animation;
    }
    
    /**
     * A command is a single action that will happen during the keyframe.  Commands
     * can be things like translate a bone or rotate a bone.  The command will
     * take frameLength seconds to fully complete (it will be interpolated over the frame)
     * @param command
     */
    public void addAnimationCommand(E3DAnimationCommand command)
    {
        commands.add(command);
        command.setKeyFrame(this);
    }

    /**
     * Remove a command from the frame
     * @param command
     */
    public void removeAnimationCommand(E3DAnimationCommand command)
    {
        commands.remove(command);
    }
    
    /**
     * Reset the frame timer
     *
     */
    public void resetTimer()
    {
    	elapsedTime = 0.0;
    }

    /**
     * Resets the elapsed time timer of the frame and reverse all commands in the frame
     */
    public void reset()
    {
    	resetTimer();
//        if(commands != null)
//        {
//        	//Must reset commands in reverse order!
//	        for(int i=commands.size()-1; i >= 0; i--)
//	        	((E3DAnimationCommand)commands.get(i)).reset();
//        }
        commandsStarted = false;
    }
    
    public String getKeyFrameID() {
        return keyFrameID;
    }
    
    /**
     * Returns true if it updates and hasn't yet expired
     * Returns false if it is expired (and finished animating) and the next frame should be called
     * @param lastFrameTimeSeconds
     * @return
     */
    public boolean update(double lastFrameTimeSeconds)
    {
    	elapsedTime += lastFrameTimeSeconds;

    	if(!commandsStarted)
    	{
	        E3DAnimationCommand command = null;
	        for(int i=0; i<commands.size(); i++)
	        {
	            command = (E3DAnimationCommand)commands.get(i);
//	            command.executeCommand(lastFrameTimeSeconds);
	            animation.addRunningCommand(command);
	        }
	        commandsStarted = true;
    	}
        if(elapsedTime >= frameLength)
            return false;
        else
            return true;
    }
    
    /**
     *  This will rotate all commands along with the actor so animation will stay oriented with the actor
     * @param angle
     * @param aroundVec
     */
    void rotate(double angle, E3DVector3F aroundVec)
    {
        E3DAnimationCommand command = null;
        for(int i=0; i < commands.size(); i++)
        {
            command = (E3DAnimationCommand)commands.get(i);
     //       command.rotate(angle, aroundVec);
        }
    }
    public ArrayList getCommands() {
        return commands;
    }
    public double getElapsedTime() {
        return elapsedTime;
    }
    public double getFrameLength() {
        return frameLength;
    }

	public E3DAnimation getAnimation() {
		return animation;
	}

	public void setAnimation(E3DAnimation animation) {
		this.animation = animation;
	}

	public boolean isCommandsStarted()
	{
		return commandsStarted;
	}

	void setCommandsStarted(boolean commandsStarted)
	{
		this.commandsStarted = commandsStarted;
	}
}
