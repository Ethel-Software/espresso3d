/*
 * Created on Nov 10, 2004
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
 */
package espresso3d.engine.world.sector.particle;

import java.util.ArrayList;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.base.IE3DCollisionDetectableObject;
import espresso3d.engine.collision.bounding.IE3DBoundingObject;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.texture.E3DAnimatedTexture;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.renderer.texture.E3DTexture;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.base.E3DPortalEnabledAnimatedTextureRenderable;

/**
 * @author Curt
 *
 * A sprite is a texture mapped quad.  
 * TODO: Alpha blending & masking is currently not supported, but will be.
 * 
 * Collision detection for particles is based on its bounding quad.
 * 
 */
public abstract class E3DSprite extends E3DPortalEnabledAnimatedTextureRenderable implements IE3DCollisionDetectableObject
{
	private static final int DEFAULT_BLEND_MODE = E3DBlendMode.BLENDMODE_BLEND;
	
    private E3DQuad quad;
    private ArrayList triangleList;
    private E3DOrientation orientation;
    
    private double size;
    private double alpha;
    
    private IE3DBoundingObject boundingObject = null;
    
	abstract public void onCollisionActor(E3DCollision collision);
    abstract public void onCollisionSprite(E3DCollision collision);    
    
    public E3DSprite(E3DEngine engine, E3DVector3F position, double size, String textureName)
    {
        this(engine, position, new E3DVector3F(0.0, 0.0, 1.0), new E3DVector3F(0.0, 1.0, 0.0), size, textureName);
    }

    //For animation
    public E3DSprite(E3DEngine engine, E3DVector3F position, double size, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops)
    {
        this(engine, position, new E3DVector3F(0.0, 0.0, 1.0), new E3DVector3F(0.0, 1.0, 0.0), size, animatedTextureFrames, animationLoops);
    }

    public E3DSprite(E3DEngine engine, E3DVector3F position, double size, String textureName, E3DBlendMode blendMode)
    {
        this(engine, position, new E3DVector3F(0.0, 0.0, 1.0), new E3DVector3F(0.0, 1.0, 0.0), size, textureName, blendMode);
    }

    public E3DSprite(E3DEngine engine, E3DVector3F position, double size, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops, E3DBlendMode blendMode)
    {
        this(engine, position, new E3DVector3F(0.0, 0.0, 1.0), new E3DVector3F(0.0, 1.0, 0.0), size, animatedTextureFrames, animationLoops, blendMode);
    }

    /**
     * Create a sprite
     * 
     * Defaults rendering blendMode to BLENDMODE_BLEND
     * 
     * @param engine
     * @param position
     * @param forward
     * @param up
     * @param size
     * @param textureName
     */
    public E3DSprite(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, String textureName)
    {
        this(engine, position, forward, up, size, textureName, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
    }

    public E3DSprite(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops)
    {
        this(engine, position, forward, up, size, animatedTextureFrames, animationLoops, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
    }

    public E3DSprite(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, String textureName, E3DBlendMode blendMode)
    {
        super(engine, textureName, blendMode);
        
        init(engine, textureName, position, forward, up, size, blendMode);
    }

    public E3DSprite(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops, E3DBlendMode blendMode)
    {
        super(engine, animatedTextureFrames, animationLoops, blendMode);
        
        String textureName="";
        if(animatedTextureFrames != null && animatedTextureFrames.length > 0)
            textureName = animatedTextureFrames[0].getTexture().getTextureName();
        
        init(engine, textureName, position, forward, up, size, blendMode);
       	if(getAnimatedTexture().getCurFrame() != null)
       		setTexture(getAnimatedTexture().getCurFrame().getTexture());
    }

    private void init(E3DEngine engine, String textureName, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, E3DBlendMode blendMode)
    {
        //The quad is the object actually used for rendering
        this.size = 2.0; //initialize the size variable and quads to that size.  Use the setSize to resize it
        quad = new E3DQuad(engine, 
                new E3DVector3F(1, -1, 0),
                new E3DVector3F(1, 1, 0),
                new E3DVector3F(-1, 1, 0), 
                new E3DVector3F(-1, -1, 0), 
                new E3DVector2F(1, 0), 
                new E3DVector2F(1, 1), 
                new E3DVector2F(0, 1), 
                new E3DVector2F(0, 0), 
                textureName);
       
        quad.setBlendMode(blendMode);
        
        // Keep a list of the triangles for the particle to for use in collision detection, NOT rendering.  Share the quads vertices so if the quad rotates, these rotate
        triangleList = new ArrayList(2);
        E3DTriangle triangle = new E3DTriangle(engine, quad.getVertexPosA(), quad.getVertexPosB(), quad.getVertexPosC());
        triangleList.add(triangle);
        triangle = new E3DTriangle(engine, quad.getVertexPosA(), quad.getVertexPosC(), quad.getVertexPosD());
        triangleList.add(triangle);
        
        this.orientation = new E3DOrientation(engine);
        orientation.setPosition(new E3DVector3F(position));
        orientation.setForward(forward);
        orientation.setUp(up);

        //Set the size and resize the quad (make sure quad is initialized first!!)
        setSize(size);
        
        //Default the alpha to 1
        setAlpha(1);
        
        boundingObject = null;
    }
    
    /* (non-Javadoc)
     * @see espresso3d.engine.renderer.base.E3DRenderable#renderAtPosition(espresso3d.engine.lowlevel.E3DVector3F)
     */
    public void render() {
    	E3DRenderTree renderTree = new E3DRenderTree(getEngine());
    	renderTree.getSpriteHandler().add(this);
    	renderTree.render();
//        FloatBuffer buffer = orientation.getFloatBuffer();
//        GL11.glPushMatrix();
//           GL11.glMultMatrix(buffer);//orientation.getFloatBuffer());
//           buffer.clear();
//           initCorrectGeometryRenderer();
//           getEngine().getGeometryRenderer().renderQuad(getEngine(), quad, getRenderMode().getRenderMode());
//        GL11.glPopMatrix();
//        buffer = null;
    }

    public void setPosition(E3DVector3F position) 
    {
    	checkSectorChangeDuringMovement(orientation.getPosition(), position);
	    
		//Get the triangles to where they should be, so grab the translation vec for them
		E3DVector3F translationAmt = position.subtract(orientation.getPosition()); //get how far its moved from where it started now
		
        orientation.translate(translationAmt);

        if(getBoundingObject() != null)
            getBoundingObject().getOrientation().translate(translationAmt);
    }
    
    public void scale(double scaleAmt)
    {
        quad.scale(scaleAmt); //This also scales the shared vertices
        
        size *= scaleAmt;
        
        if(getBoundingObject() != null)
            getBoundingObject().scale(scaleAmt);
    }
    
    public void translate(E3DVector3F translationAmt)
    {
    	checkSectorChangeDuringMovement(orientation.getPosition(), orientation.getPosition().add(translationAmt));

        orientation.translate(translationAmt);
        
        if(getBoundingObject() != null)
            getBoundingObject().getOrientation().translate(translationAmt);
    }
    
    /**
     * Rotate the sprite around upVec angle amount
     * @param angle Radian angle
     * @param upVec Normalised up vector to use as the rotation axis
     */
    public void rotate(double angle, E3DVector3F upVec)
    {
		if(angle == 0.0)
		    return;

        orientation.rotate(angle, upVec);
 //       quad.rotate(angle, upVec, new E3DVector3F());
        if(getBoundingObject() != null)
            getBoundingObject().getOrientation().rotate(angle, upVec);
    }
    
    public double getSize() {
        return size;
    }
    
    /**
     * Set the size of the sprite
     * @param size A position number greater than 0
     */
    public void setSize(double size) 
    {
        if(size <= 0.0)
            return;
        
        if(getBoundingObject() != null)
            getBoundingObject().setSize(size);
//this.size = size;
        quad.scale(size / this.size); //this should scale triangles too. FIXME: This won't work if the current size is 0 (shouldn't happen though)
        
        this.size = size;
    }
    
	public E3DQuad getQuad() {
	    return quad;
	}
	public void setQuad(E3DQuad quad) {
	    this.quad = quad;
	}

    public ArrayList getTriangleList() {
        return triangleList;
    }
    
    public ArrayList getUniqueVertexPositionList(){
        ArrayList list = new ArrayList(4);

        list.add(quad.getVertexPosA());
        list.add(quad.getVertexPosB());
        list.add(quad.getVertexPosC());
        list.add(quad.getVertexPosD());
        
        return list;
    }
    
    public IE3DBoundingObject getBoundingObject(){
        return boundingObject;
    }
    
    public void setBoundingObject(IE3DBoundingObject boundingObject)
    {
        this.boundingObject = boundingObject;
        if(boundingObject != null)
            this.boundingObject.setParentObject(this);
    }

    /**
     * If a sprite's rendermode is set, so is the the rendermode for the quad it contains
     */
    public void setRenderMode(E3DRenderMode renderMode)
    {
    	super.setRenderMode(renderMode);
    	quad.setRenderMode(renderMode);
    }
    
    public void setBlendMode(E3DBlendMode blendMode) 
    {
    	super.setBlendMode(blendMode);
    	quad.setBlendMode(blendMode);
    }
    
    /**
     * Called by the engine after each frame to update the sprites animation (if necessary)
     *@return
     * Returns false if the sprite should be removed from the engine
     * lookAtActor is ignored and only used for classes that extend this
     */
    public boolean update(E3DActor lookAtActor, double lastFrameTimeSeconds)
    {
        if(super.getAnimatedTexture().update(lastFrameTimeSeconds)) //update the animated texture
        {
        	setTexture(getAnimatedTexture().getCurFrame().getTexture());
            return true;
        }
        else
            return false;
    }    
    public E3DOrientation getOrientation() {
        return orientation;
    }
    
    public void setAnimatedTexture(E3DAnimatedTexture animatedTexture) 
    {
    	super.setAnimatedTexture(animatedTexture);
    	setTexture(animatedTexture);
    }
    
    public void setTexture(E3DTexture texture) 
    {
    	super.setTexture(texture);
    	quad.setTexture(texture);
    }
    
    public void setTextureDetail0(E3DTexture textureDetail0) 
    {
    	super.setTextureDetail0(textureDetail0);
    	quad.setTextureDetail0(textureDetail0);
    }
    
    public void setTextureDetail1(E3DTexture textureDetail1) 
    {
    	super.setTextureDetail1(textureDetail1);
    	quad.setTextureDetail1(textureDetail1);
    }
	public double getAlpha()
	{
		return alpha;
	}
	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
		quad.setAlpha(alpha);
	}
}
