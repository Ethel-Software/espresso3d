/*
 * Created on Jul 18, 2004
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
package espresso3d.engine.renderer.texture;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;

/**
 * @author espresso3d
 *
 * This is a texture item in the texture map containing the textureID in memory of the loaded texture
 *  and the texture's name
 */
public class E3DTexture extends E3DEngineItem
{
	private int glTextureID;
	private String textureName;
	private int width;
	private int height;

	public E3DTexture(E3DEngine engine, String textureName, int glTextureID)
	{
	    super(engine);
	    this.textureName = textureName;
	    this.glTextureID = glTextureID;
	}
	
	public E3DTexture(E3DEngine engine, String textureName)
	{
	    super(engine);
        this.textureName = textureName;
        if(textureName != null && !"".equals(textureName.trim()))
        {
        	E3DTexture texture = engine.getTexture(textureName);
        	if(texture != null)
        		this.glTextureID = texture.getGlTextureID();
        	else
        		this.glTextureID = -1;
        }
	}
	/**
	 * @return Returns the glTextureID.
	 */
	public int getGlTextureID() {
        if(glTextureID == -1 && textureName != null && !"".equals(textureName.trim()))
        {
        	E3DTexture texture = getEngine().getTexture(textureName);
        	if(texture != null)
        		this.glTextureID = texture.getGlTextureID();
        	else
        		this.glTextureID = -1;
        }
	    return glTextureID;
	}
	
	public void set(E3DTexture texture)
	{
		this.glTextureID = texture.getGlTextureID();
		this.textureName = texture.getTextureName();
		this.width = texture.getWidth();
		this.height = texture.getHeight();
	}
	/**
	 * @param glTextureID The glTextureID to set.
	 */
	public void setGlTextureID(int glTextureID) {
		this.glTextureID = glTextureID;
	}
	/**
	 * @return Returns the textureName.
	 */
	public String getTextureName() {
		return textureName;
	}
	/**
	 * @param textureName The textureName to set.
	 */
	public void setTextureName(String textureName) {
		this.textureName = textureName;
	}
	public int getHeight()
	{
		return height;
	}
	public void setHeight(int height)
	{
		this.height = height;
	}
	public int getWidth()
	{
		return width;
	}
	public void setWidth(int width)
	{
		this.width = width;
	}
}
