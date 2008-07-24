/*
 * Created on Jan 6, 2005
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
package espresso3d.engine.window.viewport.image;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;

/**
 * @author Curt
 *
 * A variable size image will scale itself to be the size to always fill the same amount of space
 * in the viewport no matter how large or small it gets.
 */
public class E3DImageVariableSize extends E3DImage
{
    private double widthPercentage = 0.0;
    private double heightPercentage = 0.0;

    /**
     * Construct an E3DImage that has a percentage height and width.  That means
     * as the viewport scales, the image will always scale with it.
     * 
     * This defaults the blendMode of the image to BLENDMODE_SOLID
     * 
     * @param engine
     * @param imageID
     * @param textureName
     * @param position The position the bottom left corner of the image is at in the viewport. (0,0) starts in the bottom left corner of the viewport and extends up and to the right.
     * @param widthPercent Width Spercentage from x going to the right
     * @param heightPercent Height percentage form y going up
     */
    public E3DImageVariableSize(E3DEngine engine, String imageID, String textureName, E3DVector2I position, double widthPercentage, double heightPercentage, int life)
    {
    	this(engine, imageID, textureName, position, widthPercentage, heightPercentage, life, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
    }

    /**
     * Construct an E3DImage that has a percentage height and width.  That means
     * as the viewport scales, the image will always scale with it.
     * 
     * @param engine
     * @param imageID
     * @param textureName
     * @param position The position the bottom left corner of the image is at in the viewport. (0,0) starts in the bottom left corner of the viewport and extends up and to the right.
     * @param widthPercent Width percentage from x going to the right
     * @param heightPercent Height percentage form y going up
     * @param blendMode  The type of blending that should be used to render the object
     */
    public E3DImageVariableSize(E3DEngine engine, String imageID, String textureName, E3DVector2I position, double widthPercentage, double heightPercentage, int life, E3DBlendMode blendMode)
    {
        super(engine, imageID, textureName, position, life, blendMode);
        this.widthPercentage = widthPercentage;
        this.heightPercentage = heightPercentage;
    }
    
    
    /**
     * Create this image as a copy of another, only giving it different ID
     * @param toCopyImage  The image to copy
     * @param imageID A new unique imageID for this image
     */
    public E3DImageVariableSize(E3DImageVariableSize toCopyImage, String imageID)
    {
        super((E3DImage)toCopyImage, imageID);
        this.widthPercentage = toCopyImage.widthPercentage;
        this.heightPercentage = toCopyImage.heightPercentage;
    }
    
    /**
     * Create a variable size image that has an animated texture
     * @param engine
     * @param imageID Uniue imageID for this image
     * @param animatedTextureFrames Array of E3DAnimatedTextureFrame objects.  Each specifying a frame of the animation for the image.
     * @param animationLoops Number of loops to animate the image
     * @param position 2D position (Viewport coords with (0,0) at the bottom left of the viewport)
     * @param widthPercentage  Percentage of the viewport the image should fill width-wise
     * @param heightPercentage Percentage of the viewport the image should fill height-wise
     * @param life How long it is displayed.  -1 is infinite
     * @param blendMode One of E3DImage.RENDERMODE_ options
     */
    public E3DImageVariableSize(E3DEngine engine, String imageID, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops, E3DVector2I position, double widthPercentage, double heightPercentage, int life, E3DBlendMode blendMode)
    {
        super(engine, imageID, animatedTextureFrames, animationLoops, position, life, blendMode);

        this.widthPercentage = widthPercentage;
        this.heightPercentage = heightPercentage;
    }
    
    /**
     * Create a variable size image that has an animated texture
     * @param engine
     * @param imageID Uniue imageID for this image
     * @param animatedTextureFrames Array of E3DAnimatedTextureFrame objects.  Each specifying a frame of the animation for the image.
     * @param animationLoops Number of loops to animate the image
     * @param position 2D position (Viewport coords with (0,0) at the bottom left of the viewport)
     * @param widthPercentage  Percentage of the viewport the image should fill width-wise
     * @param heightPercentage Percentage of the viewport the image should fill height-wise
     * @param life How long it is displayed.  -1 is infinite
     */
    public E3DImageVariableSize(E3DEngine engine, String imageID, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops, E3DVector2I position, double widthPercentage, double heightPercentage, int life)
    {
    	this(engine, imageID, animatedTextureFrames, animationLoops, position, widthPercentage, heightPercentage, life, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
    }    
    
    /**
     * Returns a quad in 3D space that is positioned correctly to be rendered in the correct position for 2D space
     * Usually used only by the engine.
     */
    public E3DQuad getPositionedQuad() 
    {
    	E3DQuad quad = getQuad();
        if(quad == null)
        	return null;

       	E3DQuad quadCopy = getViewport().getVariableSizePositionedQuad(quad, getPosition(), new E3DVector2F(widthPercentage, heightPercentage), 0.1);
       	quadCopy.setBlendMode(new E3DBlendMode(getBlendMode()));
       	quadCopy.setRenderMode(new E3DRenderMode(getRenderMode()));
    	
       	return quadCopy;
    }
    
    /**
     * Scale the widthPercentage and heightPercentage by scaleAmt
     */
    public void scale(double scaleAmt) {
        widthPercentage *= scaleAmt;
        heightPercentage *= scaleAmt;
    }

    
}
