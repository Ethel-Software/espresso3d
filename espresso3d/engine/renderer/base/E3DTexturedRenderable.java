/*
 * Created on Nov 12, 2004
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
package espresso3d.engine.renderer.base;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.renderer.texture.E3DTexture;

/**
 * @author Curt
 *
 * The base class to be extended for texturedrenderable objects.  Usually used
 * for primitives like Triangles or Quads.
 */
public abstract class E3DTexturedRenderable extends E3DRenderable {
//    ArrayList textureNames;
	
    /**
     * Base (main) texture
     */
    private E3DTexture texture;
    private String textureName;
    
    /**
     * Optional detail texture 0
     */
    private E3DTexture textureDetail0;
    private String textureDetail0Name;

    /**
     * Option detail texture 1
     */
    private E3DTexture textureDetail1;
    private String textureDetail1Name;
    
    //Used to generate the key without concat'ing lots of strings
  //  private StringBuffer keyBuffer;
    
    public E3DTexturedRenderable(E3DEngine engine, String textureName)
    {
        this(engine, new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED), new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID), textureName);
    }
    
    public E3DTexturedRenderable(E3DEngine engine, E3DRenderMode renderMode, E3DBlendMode blendMode, String textureName)
    {
        this(engine, renderMode, blendMode, textureName, null, null);
    }

    public E3DTexturedRenderable(E3DEngine engine, E3DRenderMode renderMode, E3DBlendMode blendMode, String textureName, String textureDetail0Name)
    {
        this(engine, renderMode, blendMode, textureName, textureDetail0Name, null);
    }

    public E3DTexturedRenderable(E3DEngine engine, E3DRenderMode renderMode, E3DBlendMode blendMode, String textureName, String textureDetail0Name, String textureDetail1Name)
    {
        super(engine, renderMode, blendMode);
        
        this.textureName = textureName;
        texture = engine.getTexture(textureName);
        
        if(textureDetail0Name != null)
        {
	        this.textureDetail0Name = textureDetail0Name;
	        textureDetail0 = engine.getTexture(textureDetail0Name);
        }
        
        if(textureDetail1Name != null)
        {
	        this.textureDetail1Name = textureDetail1Name;
	        textureDetail1 = engine.getTexture(textureDetail1Name);
        }
    //    keyBuffer = new StringBuffer();
    }
    
    public E3DTexture getTexture() 
    {
    	if(texture == null) //if it couldn't find it at create time, try again, maybe its there now
    	{
    		texture =  getEngine().getTexture(textureName);
    		if(texture == null)
    			return new E3DTexture(getEngine(), textureName); //never return null
    		else
    			return texture;
    	}
    	else
    		return texture;
	}

    public void setTexture(E3DTexture texture) {
	    this.texture = texture;
        if(texture != null)
            this.textureName = texture.getTextureName();
	}

    public boolean isTextureAvailable(){
        return !(textureName == null || "".equals(textureName));
    }

    public boolean isTextureDetail0Available(){
        return !(textureDetail0Name == null || "".equals(textureDetail0Name));
    }

    public boolean isTextureDetail1Available(){
        return !(textureDetail1Name == null || "".equals(textureDetail1Name));
    }

    public E3DTexture getTextureDetail0() 
    {
        if(textureDetail0 == null) //if it couldn't find it at create time, try again, maybe its there now
        {
            textureDetail0 =  getEngine().getTexture(textureDetail0Name);
            return textureDetail0;
        }
        else
            return textureDetail0;
    }

    public void setTextureDetail0(E3DTexture textureDetail0) {
        if(this.textureDetail0 != textureDetail0)
        {
            this.textureDetail0 = textureDetail0;
            if(textureDetail0 != null)
                this.textureDetail0Name = textureDetail0.getTextureName();
        }
    }
    
    public E3DTexture getTextureDetail1() 
    {
        if(textureDetail1 == null) //if it couldn't find it at create time, try again, maybe its there now
        {
            textureDetail1 =  getEngine().getTexture(textureDetail1Name);
            return textureDetail1;
        }
        else
            return textureDetail1;
    }
    
    public void setTextureDetail1(E3DTexture textureDetail1) {
        if(this.textureDetail1 != textureDetail1)
        {
            this.textureDetail1 = textureDetail1;
            if(textureDetail1 != null)
                this.textureDetail1Name = textureDetail1.getTextureName();
        }
    }
}
