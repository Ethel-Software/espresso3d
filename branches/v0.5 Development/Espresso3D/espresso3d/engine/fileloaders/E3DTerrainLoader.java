/*
 
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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.exceptions.E3DInvalidFileFormatException;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.world.sector.terrain.E3DTerrain;


public class E3DTerrainLoader extends E3DImageLoader
{
//	public static E3DTerrain loadTerrain(E3DEngine engine, String workingDirectory, String fullPathTerrainName) throws E3DInvalidFileFormatException, IOException
//	{
//		Image terrainImage = loadImage(fullPathTerrainName, false);
//		
//        if(!fromJar)
//            E3DImageLoader.loadTextureSet(getEngine(), getEngine().getWorkingDirectory(), getEngine().getWorkingDirectory() + "\\" + textureSetFileName, getTextureSet());
//        else
//            E3DImageLoader.loadTextureSetFromJar(getEngine(), textureSetFileName, getTextureSet());
//        
//		return loadTerrain(engine, (BufferedImage)terrainImage);
//		
//	}
	
	public static E3DTerrain loadTerrain(E3DEngine engine, String terrainImageName) throws E3DInvalidFileFormatException, IOException
	{   
       Image terrainImage = loadImage(terrainImageName);
	
       return loadTerrain(engine, (BufferedImage)terrainImage);
	}

	private static E3DTerrain loadTerrain(E3DEngine engine, BufferedImage terrainImage)
	{
		if(terrainImage == null)
			return null;
		
		
		int width = terrainImage.getWidth(null);
		int height = terrainImage.getHeight(null);
		
		System.out.println("Width: " + width + " Height: " + height);

		int[] rgbs = new int[width*height];
		terrainImage.getRGB(0, 0, width, height, rgbs, 0, width);

		double[][] heights = new double[width][height];
		E3DVector4F rgba = null;
	    for(int x=0; x < width; x++)
		{
			for(int y=0; y < height; y++)
			{
				String argbStr = Integer.toHexString(terrainImage.getRGB(x, y));
				
				//Don't use all 3 now, but maybe in the future?
				rgba = new E3DVector4F();
				rgba.setA(Integer.parseInt(argbStr.substring(2, 4), 16)); //R
				rgba.setB(Integer.parseInt(argbStr.substring(4, 6), 16)); //G
				rgba.setC(Integer.parseInt(argbStr.substring(6, 8), 16)); //B
				rgba.setD(Integer.parseInt(argbStr.substring(0, 2), 16)); //A
				
				double addedHeight = rgba.getA() +  + rgba.getB() + rgba.getC(); //add them.  Gives more height optiosn
				if(addedHeight > 765)
					addedHeight = 765.0;
				
				heights[x][y] = addedHeight;
//				System.out.println(rgba);
			}
		}
		
		
		return new E3DTerrain(engine, "terrain1", new E3DVector2F(0,0), new E3DVector2F(2048,2048), heights, 0.7);
	}
}
