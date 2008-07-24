/*
 * Created on Dec 15, 2004
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
package espresso3d.engine.world.sky;

import java.util.ArrayList;
import java.util.Arrays;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;

/**
 * @author Curt
 *
 * A SkyBox is a sky object with 6 sides.  It completely surrounds the viewport's camera actor and
 * always stays centered around the viewport's camera actor.
 * 
 */
public class E3DSkyBox extends E3DSkyObject 
{
    private E3DQuad[] quads;
    
    /**
     * Construct a new sky box
     * 
     * @param engine  Enine the skybox resides in
     * @param frontTexture Texture name for the front face of the box
     * @param backTexture Texture name for the back face of the box
     * @param leftTexture Texture name for the left face of the box
     * @param rightTexture Texture name for the right face of the box
     * @param topTexture Texture name for the top face of the box
     * @param bottomTexture Texture name for the bottom face of the box
     */
    public E3DSkyBox(E3DEngine engine, String frontTexture, String backTexture, String leftTexture, String rightTexture, String topTexture, String bottomTexture)
    {
        super(engine);
        
        quads = new E3DQuad[6];
        quads[0] = new E3DQuad(engine, new E3DVector3F(-0.5, -0.5, 0.5), new E3DVector3F(-0.5, 0.5, 0.5), new E3DVector3F(0.5, 0.5, 0.5), 
                        new E3DVector3F(0.5, -0.5, 0.5), new E3DVector2F(0, 0), new E3DVector2F(0, 1), new E3DVector2F(1, 1), new E3DVector2F(1, 0), frontTexture); //front
        
        quads[1] = new E3DQuad(engine, new E3DVector3F(-0.5, -0.5, -0.5), new E3DVector3F(-0.5, 0.5, -0.5), 
                        new E3DVector3F(0.5, 0.5, -0.5), new E3DVector3F(0.5, -0.5, -0.5), 
                        new E3DVector2F(1, 0), new E3DVector2F(1, 1), new E3DVector2F(0, 1), new E3DVector2F(0, 0), 
                        backTexture); //back
        
        quads[2] = new E3DQuad(engine, new E3DVector3F(-0.5, -0.5, -0.5), new E3DVector3F(-0.5, 0.5, -0.5), 
                        new E3DVector3F(-0.5, 0.5, 0.5), new E3DVector3F(-0.5, -0.5, 0.5), 
                        new E3DVector2F(0, 0), new E3DVector2F(0, 1), new E3DVector2F(1, 1), new E3DVector2F(1, 0), 
                        leftTexture); //left
        
        quads[3] = new E3DQuad(engine, new E3DVector3F(0.5, -0.5, -0.5), new E3DVector3F(0.5, 0.5, -0.5), 
                        new E3DVector3F(0.5, 0.5, 0.5), new E3DVector3F(0.5, -0.5, 0.5), 
                        new E3DVector2F(1, 0), new E3DVector2F(1, 1), new E3DVector2F(0, 1), new E3DVector2F(0, 0), 
                        rightTexture); //right
        
        quads[4] = new E3DQuad(engine, new E3DVector3F(-0.5, -0.5, -0.5), new E3DVector3F(-0.5, -0.5, 0.5), 
                         new E3DVector3F(0.5, -0.5, 0.5), new E3DVector3F(0.5, -0.5, -0.5), 
                         new E3DVector2F(0, 0), new E3DVector2F(0, 1), new E3DVector2F(1, 1), new E3DVector2F(1, 0), bottomTexture);
        
        quads[5] = new E3DQuad(engine, new E3DVector3F(-0.5, 0.5, -0.5), new E3DVector3F(-0.5, 0.5, 0.5), 
                        new E3DVector3F(0.5, 0.5, 0.5), new E3DVector3F(0.5, 0.5, -0.5), 
                        new E3DVector2F(1, 0), new E3DVector2F(1, 1), new E3DVector2F(0, 1), new E3DVector2F(0, 0), 
                        topTexture);
    }
    
    /**
     * Get the quad for the front face of the SkyBox
     * @return
     */
    public E3DQuad getFront()
    {
        return quads[0];
    }
    
    /**
     * Set the quad for the front face of the SkyBox
     * @param front
     */
    private void setFront(E3DQuad front)
    {
        quads[0] = front;
    }
    
    /**
     * Get the quad for the back face of the SkyBox
     * @return
     */
    public E3DQuad getBack()
    {
        return quads[1];
    }
    
    /**
     * Set the quad for the back face of the SkyBox
     * @param back
     */
    private void setBack(E3DQuad back)
    {
        quads[1] = back;
    }
    
    /**
     * Get the quad for the left face of the SkyBox
     * @return
     */
    public E3DQuad getLeft()
    {
        return quads[2];
    }
    
    /**
     * Set the quad for the left face of the SkyBox
     * @param left
     */
    private void setLeft(E3DQuad left)
    {
        quads[2] = left;
    }
    
    /**
     * Get the quad for the right face of the SkyBox
     * @return
     */
    public E3DQuad getRight()
    {
        return quads[3];
    }

    /**
     * Set the quad for the right face of the SkyBox
     * @param right
     */
    private void setRight(E3DQuad right)
    {
        quads[3] = right;
    }
    
    /**
     * Get the quad for the bottom face of the SkyBox
     * @return
     */
    public E3DQuad getBottom()
    {
        return quads[4];
    }
    
    private void setBottom(E3DQuad bottom)
    {
        quads[4] = bottom;
    }
    
    /**
     * Get the quad for the top face of the SkyBox
     * @return
     */
    public E3DQuad getTop()
    {
        return quads[5];
    }

    private void setTop(E3DQuad top)
    {
        quads[5] = top;
    }
    
    /* (non-Javadoc)
     * @see espresso3d.engine.renderer.base.E3DRenderable#renderAtPosition(espresso3d.engine.lowlevel.E3DVector3F)
     */
    public void render() 
    {
        ArrayList quadList = new ArrayList(6);
        for(int i=0; i<6; i++)
        	quadList.add(quads[i]);
        	
    	E3DRenderTree renderTree = new E3DRenderTree(getEngine());
    	renderTree.getQuadHandler().addAll(quadList);
    	renderTree.render();
    }
    
    /**
     * Set the position the sky box should center around.
     * This is normally only used by the engine
     */
    public void setPosition(E3DVector3F position)
    {
        E3DVector3F translationAmt = position.subtract(this.position);
        if(translationAmt.equals(0.0, 0.0, 0.0))
            return;

        this.position.addEqual(translationAmt);
        
        for(int i=0; i<6; i++)
            quads[i].translate(translationAmt);
    }

    /**
     * Translate the skybox.  Normally only used by the engine
     */
    public void translate(E3DVector3F translationAmt)
    {
        if(translationAmt.equals(0.0, 0.0, 0.0))
            return;
        
        this.position.addEqual(translationAmt);
        
        for(int i=0; i<6; i++)
            quads[i].translate(translationAmt);
    }

    /**
     * Scale the skybox.  Normally only used by the engine.
     */
    public void scale(double scaleAmt)
    {
        E3DVector3F positionHold = this.position; //hold onto current position
        setPosition(new E3DVector3F(0.0, 0.0, 0.0)); //have to put it back to origin to scale
        
        for(int i=0; i<6; i++)
            quads[i].scale(scaleAmt);

        translate(positionHold); //put it back where it should be

        this.scale *= scaleAmt;
    }
    
}