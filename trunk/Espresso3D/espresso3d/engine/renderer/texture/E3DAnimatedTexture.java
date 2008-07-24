/*
 * Created on Jan 13, 2005
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
package espresso3d.engine.renderer.texture;

import java.util.ArrayList;

import espresso3d.engine.E3DEngine;

/**
 * @author Curt
 *
 * Class that supports texture animation and used by certain objects that support texture animation
 * 
 */
public class E3DAnimatedTexture extends E3DTexture 
{
    private ArrayList animatedTextureFrames;
    private int loops;
    private int curLoop;
    private int curFrameNum;
    private E3DAnimatedTextureFrame curFrame;
    
    public boolean frameChanged = false;
    
    /**
     * Instantiate an animated texture using an array of texture names and array of framelife's.
     * @param engine
     * @param textureNames  Array of names of the texture for each frame
     * @param frameLife Life of each frame of texture. -1 is forever. This array must be the same length as the textureNames array
     * @param loops How many times to loop the animation.  -1 is forever.  Otherwise, when it will stay on the last image of the loop
     */
    public E3DAnimatedTexture(E3DEngine engine, String[] textureNames, double[] frameLife, int loops)
    {
        super(engine, (textureNames[0] == null ? "" : textureNames[0]));
        animatedTextureFrames = new ArrayList();
        this.loops = loops;
        if(textureNames == null || frameLife == null ||
        		textureNames.length != frameLife.length)
        {
        	//TODO: Replace this with an exception
        	System.out.println("TextureNames and frameLife must be the same length array.  This will be replaced with a more descriptive Exception in the future.");
        }
        
        E3DAnimatedTextureFrame frame;
        for(int i=0; textureNames != null && i<textureNames.length; i++)
        {
        	frame = new E3DAnimatedTextureFrame(engine, textureNames[i], frameLife[i]);
        	addFrame(frame);
        }
        setCurFrameNum(0);
    }

    public E3DAnimatedTexture(E3DEngine engine, E3DAnimatedTextureFrame[] animatedTextureFrames, int loops)
    {
        super(engine, (animatedTextureFrames != null && animatedTextureFrames.length > 0 ? animatedTextureFrames[0].getTexture().getTextureName() : ""));
        
        this.loops = loops;
        this.animatedTextureFrames = new ArrayList();
        for(int i=0; animatedTextureFrames != null && i < animatedTextureFrames.length; i++)
            addFrame(animatedTextureFrames[i]);
    }
    
    public void addFrame(String textureName, double frameLife)
    {
        E3DAnimatedTextureFrame frame = new E3DAnimatedTextureFrame(getEngine(), textureName, frameLife);
        animatedTextureFrames.add(frame);
    }
    
    public void addFrame(E3DAnimatedTextureFrame frame)
    {
        animatedTextureFrames.add(frame);
    }
    
    public ArrayList getAnimatedTextureFrames() {
        return animatedTextureFrames;
    }
    public void setAnimatedTextureFrames(ArrayList animatedTextureFrames) {
        this.animatedTextureFrames = animatedTextureFrames;
    }
    public int getLoops() {
        return loops;
    }
    public void setLoops(int loops) {
        this.loops = loops;
    }
    public int getCurFrameNum() {
        return curFrameNum;
    }
    public void setCurFrameNum(int curFrameNum) {
        this.curFrameNum = curFrameNum;
        
        curFrame = (E3DAnimatedTextureFrame)animatedTextureFrames.get(curFrameNum);
        if(curFrame != null)
        {
        	curFrame.reset();
        	this.set(curFrame.getTexture());
        }
    }
    
    public boolean isFrameChangeNeeded(double lastFrameTimeSeconds)
    {
      	E3DAnimatedTextureFrame frame = (E3DAnimatedTextureFrame)animatedTextureFrames.get(curFrameNum);
      	if(frame == null)
      	    return false;
      	else
      	    return frame.isFrameChangeNeeded(lastFrameTimeSeconds);
    }
    
    /**
     * Returns true if the texture changes
     * @param lastFrameTimeSeconds
     * @return
     */
    public boolean update(double lastFrameTimeSeconds)
    {
    	E3DAnimatedTextureFrame frame = (E3DAnimatedTextureFrame)animatedTextureFrames.get(curFrameNum);

    	if(frame != null && !frame.update(lastFrameTimeSeconds))
    	{ 
        	if(curFrameNum + 1 >= animatedTextureFrames.size())
        	{
        		curLoop++;
        		if(loops > -1 && this.curLoop > loops) //finished its life (if not -1 meaning loop forever), so stay on the last frame forever now
        			return false;
        		setCurFrameNum(0);
        	}
        	else
        		setCurFrameNum(curFrameNum+1);
        	return true;
    	}
    	return false;
    }
    /**
     * This will be set to true if the frame was changed after the last call to update.
     * @return
     */
	public boolean isFrameChanged()
	{
		return frameChanged;
	}
	private void setFrameChanged(boolean frameChanged)
	{
		this.frameChanged = frameChanged;
	}
    public E3DAnimatedTextureFrame getCurFrame() {
        return curFrame;
    }
}
