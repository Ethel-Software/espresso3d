/*
 * Created on Jan 6, 2005
 *
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
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;

/**
 * @author Curt
 *
 * A fixed size image is a type of image that will always be the same size no matter how large or small the
 * viewport and window are
 */
public class E3DImageFixedSize extends E3DImage {
    private int width;
    private int height;
    
    /**
     * Construct an E3DImage that has a static width.
     * 
     * Defaults blending to BLENDMODE_SOLID
     * 
     * @param engine
     * @param imageID
     * @param textureName
     * @param position Position the iamge will be at starting at 0,0 in the lower left corner of the window and extending up and left
     * @param width Width from x going to the right
     * @param height Height form y going up
     */
    public E3DImageFixedSize(E3DEngine engine, String imageID, String textureName, E3DVector2I position, int width, int height, int life)
    {
    	this(engine, imageID, textureName, position, width, height, life, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
    }

    /**
     * Construct an E3DImage that has a static width.
     * 
     * @param engine
     * @param imageID
     * @param textureName
     * @param position Position the iamge will be at starting at 0,0 in the lower left corner of the window and extending up and left
     * @param width Width from x going to the right
     * @param height Height form y going up
     * @param blendMode The type of blending that should be used to render the object
     */
    public E3DImageFixedSize(E3DEngine engine, String imageID, String textureName, E3DVector2I position, int width, int height, int life, E3DBlendMode blendMode)
    {
    	super(engine, imageID, textureName, position, life, blendMode);
    	this.height = height;
    	this.width = width;
    }
    
    /**
     * Create this image as a copy of another, only giving it different ID
     * @param toCopyImage  The image to copy
     * @param imageID A new unique imageID for this image
     */
    public E3DImageFixedSize(E3DImageFixedSize toCopyImage, String imageID)
    {
        super((E3DImage)toCopyImage, imageID);
        this.width = toCopyImage.getWidth();
        this.height = toCopyImage.getHeight();
    }

    /**
     * Create a fixed size image that has an animated texture
     * @param engine
     * @param imageID Uniue imageID for this image
     * @param animatedTextureFrames Array of E3DAnimatedTextureFrame objects.  Each specifying a frame of the animation for the image.
     * @param animationLoops Number of loops to animate the image
     * @param position 2D position (Viewport coords with (0,0) at the bottom left of the viewport)
     * @param width  How many pixels wide to make it
     * @param height How many pixels high to make it
     * @param life How long it is displayed.  -1 is infinite
     * @param blendMode One of E3DImage.RENDERMODE_ options
     */
    public E3DImageFixedSize(E3DEngine engine, String imageID, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops, E3DVector2I position, int width, int height, int life, E3DBlendMode blendMode)
    {
        super(engine, imageID, animatedTextureFrames, animationLoops, position, life, blendMode);

        this.width = width;
        this.height = height;
    }
    
    /**
     * Create a fixed size image that has an animated texture
     * @param engine
     * @param imageID Uniue imageID for this image
     * @param animatedTextureFrames Array of E3DAnimatedTextureFrame objects.  Each specifying a frame of the animation for the image.
     * @param animationLoops Number of loops to animate the image
     * @param position 2D position (Viewport coords with (0,0) at the bottom left of the viewport)
     * @param width  How many pixels wide to make it
     * @param height How many pixels high to make it
     * @param life How long it is displayed.  -1 is infinite
     */
    public E3DImageFixedSize(E3DEngine engine, String imageID, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops, E3DVector2I position, int width, int height, int life)
    {
    	this(engine, imageID, animatedTextureFrames, animationLoops, position, width, height, life, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
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

    	E3DQuad quadCopy = getViewport().getFixedSizePositionedQuad(quad, getPosition(), new E3DVector2I(width, height), 0.1);
        quadCopy.setBlendMode(new E3DBlendMode(getBlendMode()));
        quadCopy.setRenderMode(new E3DRenderMode(getRenderMode()));
        
        return quadCopy;
    }
    
    /**
     * Multiply the width and height by a scalar
     */
    public void scale(double scaleAmt) {
        width *= scaleAmt;
        height *= scaleAmt;
    }

    /**
     * Return the number of pixels high the image is
     * @return
     */
    public int getHeight() {
        return height;
    }
    /**
     * Set the height of the image
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * Return the number of pixels wide the image is
     * @return
     */
    public int getWidth() {
        return width;
    }
    /**
     * Set the width of the image
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }
}
