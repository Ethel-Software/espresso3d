/*
 * Created on Jul 23, 2004
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
package espresso3d.engine.fileloaders;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.exceptions.E3DInvalidFileFormatException;
import espresso3d.engine.logger.E3DEngineLogger;
import espresso3d.engine.renderer.texture.E3DTexture;

/**
 * 
 * @author espresso3d
 * 
 * Loads image (PNG, JPG, etc) files
 */
public class E3DImageLoader extends E3DFileLoader
{
	/**
	 * Loads a texture set of format :  <textureName>, <path to texture>;\n
	 * into the textureSet map passed in with entries: value: Integer(glTextureAddress), key:texturename
	 * It will -not- replace an item if its already in the map under texturename
	 * @param textureSetFileName
	 * @return
	 * @throws E3DInvalidFileFormatException
	 */
	public static void loadTextureSet(E3DEngine engine, String textureSetFileName, HashMap textureSet) throws E3DInvalidFileFormatException, IOException
	{
        InputStream is = null;
        BufferedReader textureSetFileReader = null;
        try 
        {
            is = openFile(textureSetFileName);
            
            if(is == null)
            {
                engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_ERROR, "Unable to load textureset from jar or filesystem: " + textureSetFileName);
                return;
            }
    
            textureSetFileReader = new BufferedReader(new InputStreamReader(is));
    		
    		readTextureSet(engine, textureSetFileReader, textureSet);
        }finally{
            if(textureSetFileReader != null)
                textureSetFileReader.close();
            closeFile(is);
        }
	}

	private static void readTextureSet(E3DEngine engine, BufferedReader textureSetFile, HashMap textureSet)  throws E3DInvalidFileFormatException, IOException
	{
		StringTokenizer tokenizer = null;
		String line = "";
		String path = "", textureName = "";
		
		int i=0;
		while((line = textureSetFile.readLine()) != null)
		{
			i=0;
			tokenizer = new StringTokenizer(line, " ,;");
			while(tokenizer.hasMoreTokens())
			{
				if(i == 0)
					textureName = tokenizer.nextToken();
				else if(i == 1)
				{
					path = tokenizer.nextToken();
					if(!textureSet.containsKey(textureName)) //dont' load it twice
					{
						E3DTexture texture;
                        texture = loadImageIntoGL(engine, textureName, loadImage(path));
						
						addTexture(textureSet, textureName, texture);
					}
				}
				else if(i >= 2) //too many tokens
					throw new E3DInvalidFileFormatException();
				
				i++;
			}
			if(i == 0)
				continue;
			else if(i < 2) //too few tokens
				throw new E3DInvalidFileFormatException();
		
		}
		
	}

	public static BufferedImage loadImage(String name) throws IOException
	{
        BufferedImage image = null;
        InputStream is = null;
        
        try{
            is = openFile(name);
            image = ImageIO.read(is);
        }finally{
            closeFile(is);
        }
		return image;
	}
	
    public static E3DTexture loadImageIntoGL(E3DEngine engine, String textureName, Image image)
    {
		// Extract The Image
		BufferedImage tex = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D g = (Graphics2D) tex.getGraphics();
		g.drawImage(image, null, null);
		g.dispose();

		// Flip Image
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		tex = op.filter(tex, null);

		// Put Image In Memory
		ByteBuffer scratch = ByteBuffer.allocateDirect(4 * tex.getWidth() * tex.getHeight());

		byte data[] = (byte[]) tex.getRaster().getDataElements(0, 0, tex.getWidth(), tex.getHeight(), null);
		scratch.clear();
		scratch.put(data);
		scratch.rewind();

		// Create A IntBuffer For Image Address In Memory   
		IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		GL11.glGenTextures(buf); // Create Texture In OpenGL   

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, buf.get(0));
		// Typical Texture Generation Using Data From The Image

		// Linear Filtering
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		// Linear Filtering
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		// Generate The Texture
		GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, 3, tex.getWidth(), tex.getHeight(), GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, scratch);
//		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tex.getWidth(), tex.getHeight(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, scratch);
		
		E3DTexture texture = new E3DTexture(engine, textureName, buf.get(0));
		texture.setWidth(image.getWidth(null));
		texture.setHeight(image.getHeight(null));
		
		return texture; // Return Image Address In Memory    	
    }
    
    private static void addTexture(HashMap textureSet, String textureName, E3DTexture texture)
    {
		textureSet.put(textureName, texture);
    }
    
    public static void unloadImageFromGL(int glTextureID)
    {
		IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		buf.put(glTextureID);
    	GL11.glDeleteTextures(buf);
    }
}
