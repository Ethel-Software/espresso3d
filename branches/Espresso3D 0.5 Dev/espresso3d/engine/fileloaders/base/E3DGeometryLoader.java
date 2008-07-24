/*
 * Created on Mar 4, 2005
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
package espresso3d.engine.fileloaders.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.fileloaders.E3DFileLoader;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DGeometryLoader extends E3DFileLoader
{
    protected static final String TOKENIZER_CHARS = " ;,\t";
    
    protected static final String START_BLOCK = "{";
    protected static final String END_BLOCK = "}";
    protected static final String COMMENT = "#";
    protected static final String VERTICES = "VERTICES";
    protected static final String TEXTURE = "TEXTURE";    
    protected static final String TRIANGLE_TEXTUREDETAIL0 = "TEXTUREDETAIL0";    
    protected static final String TRIANGLE_TEXTUREDETAIL1 = "TEXTUREDETAIL1";    
    protected static final String TRIANGLE_BONELINK = "BONELINK";    
    protected static final String POSITION = "POSITION";
    protected static final String FORWARD = "FORWARD";
    protected static final String UP = "UP";
    

    /**
     * Reads a chunk of a tokenizer that contains vertex information for at triangle
     * @param tokenizer
     * @return
     */
    protected static E3DVector3F[] readTriangleVertexInformation(StringTokenizer tokenizer)
    {
        double ax, ay, az, bx, by, bz, cx, cy, cz;
        E3DVector3F[] vertices = new E3DVector3F[3];
        
        //Get vertices
        ax = Double.parseDouble(readNextValue(tokenizer));
        ay = Double.parseDouble(readNextValue(tokenizer));
        az = Double.parseDouble(readNextValue(tokenizer));
        bx = Double.parseDouble(readNextValue(tokenizer));
        by = Double.parseDouble(readNextValue(tokenizer));
        bz = Double.parseDouble(readNextValue(tokenizer));
        cx = Double.parseDouble(readNextValue(tokenizer));
        cy = Double.parseDouble(readNextValue(tokenizer));
        cz = Double.parseDouble(readNextValue(tokenizer));

        vertices[0] = new E3DVector3F(ax, ay, az);
        vertices[1] = new E3DVector3F(bx, by, bz);
        vertices[2] = new E3DVector3F(cx, cy, cz);
        
        return vertices;
    }
    
    protected static E3DVector2F[] readTriangleTextureCoordinateInformation(StringTokenizer tokenizer)
    {
        double tax, tay, tbx, tby, tcx, tcy;
        E3DVector2F[] texCoords = new E3DVector2F[3];
        
        tax = Double.parseDouble(readNextValue(tokenizer));
        tay = Double.parseDouble(readNextValue(tokenizer));
        tbx = Double.parseDouble(readNextValue(tokenizer));
        tby = Double.parseDouble(readNextValue(tokenizer));
        tcx = Double.parseDouble(readNextValue(tokenizer));
        tcy = Double.parseDouble(readNextValue(tokenizer));
        
        texCoords[0] = new E3DVector2F(tax, tay);
        texCoords[1] = new E3DVector2F(tbx, tby);
        texCoords[2] = new E3DVector2F(tcx, tcy);
        
        return texCoords;
    }
    
    protected static String readNextValue(StringTokenizer tokenizer)
    {  
    	if(!tokenizer.hasMoreTokens())
    		return null;
    	
        String input = tokenizer.nextToken();
        while(tokenizer.hasMoreTokens() && (input == null || "".equalsIgnoreCase(input.trim())))
            input = tokenizer.nextToken();
        return input;
    }
    
    protected static class TempTriangle
    {
        public E3DTriangle triangle;
        public VertexBoneInformation vertexBoneInformation;
    }
    
    protected static TempTriangle readTriangle(E3DEngine engine, BufferedReader file) throws IOException
    {
        String line, str;
        boolean startBlockFound = false;
        E3DVector3F[] vertices = new E3DVector3F[3];
        E3DVector2F[] texCoords = new E3DVector2F[3];
        E3DVector2F[] detail0Coords = new E3DVector2F[3];
        E3DVector2F[] detail1Coords = new E3DVector2F[3];
        VertexBoneInformation vertexBoneInformation = null;
        String textureName = null, textureDetail0Name=null,textureDetail1Name=null;
        
        while((line = file.readLine()) != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(line, TOKENIZER_CHARS);
            str = readNextValue(tokenizer);
            
            if(START_BLOCK.equals(str))
                startBlockFound = true;
            else if(VERTICES.equals(str))
            {
                if(!startBlockFound)
                    continue;
             
                String subLine = file.readLine();
                StringTokenizer subTokenizer = new StringTokenizer(subLine, TOKENIZER_CHARS);

                //Get vertices
                vertices = readTriangleVertexInformation(subTokenizer);
            }
            else if(TRIANGLE_BONELINK.equals(str))
            {
                if(!startBlockFound)
                    continue;
                
                String subLine = file.readLine();
                StringTokenizer subTokenizer = new StringTokenizer(subLine, TOKENIZER_CHARS);

                //Get bone info
                vertexBoneInformation = readVertexBoneInformation(subTokenizer);
            }
            else if(TEXTURE.equals(str))
            {
                if(!startBlockFound)
                    continue;

                String subLine = file.readLine();
                StringTokenizer subTokenizer = new StringTokenizer(subLine, TOKENIZER_CHARS);

                texCoords = readTriangleTextureCoordinateInformation(subTokenizer);
                textureName = readNextValue(subTokenizer); //texture Name for the coords
            }
            else if(TRIANGLE_TEXTUREDETAIL0.equals(str))
            {
                if(!startBlockFound)
                    continue;

                String subLine = file.readLine();
                StringTokenizer subTokenizer = new StringTokenizer(subLine, TOKENIZER_CHARS);

                detail0Coords = readTriangleTextureCoordinateInformation(subTokenizer);
                textureDetail0Name = readNextValue(subTokenizer); //texture Name for the coords
            }
            else if(TRIANGLE_TEXTUREDETAIL1.equals(str))
            {
                if(!startBlockFound)
                    continue;

                String subLine = file.readLine();
                StringTokenizer subTokenizer = new StringTokenizer(subLine, TOKENIZER_CHARS);

                detail1Coords = readTriangleTextureCoordinateInformation(subTokenizer);
                textureDetail1Name = readNextValue(subTokenizer); //texture Name for the coords
            }
            else if(END_BLOCK.equals(str))
            {
                E3DTriangle triangle;        
                if(textureDetail0Name == null && textureDetail1Name == null)
                {
                    triangle = new E3DTriangle(engine, vertices[0], vertices[1], vertices[2], 
                                                           texCoords[0], texCoords[1], texCoords[2], textureName);
                }
                else if(textureDetail0Name != null && textureDetail1Name == null)
                {
                    triangle = new E3DTriangle(engine, vertices[0], vertices[1], vertices[2], 
                            texCoords[0], texCoords[1], texCoords[2], textureName,
                            detail0Coords[0], detail0Coords[1], detail0Coords[2], textureDetail0Name);
                    
                }
                else// if(detail0 and detail1 not null
                {
                    triangle = new E3DTriangle(engine, vertices[0], vertices[1], vertices[2], 
                            texCoords[0], texCoords[1], texCoords[2], textureName,
                            detail0Coords[0], detail0Coords[1], detail0Coords[2], textureDetail0Name,
                            detail1Coords[0], detail1Coords[1], detail1Coords[2], textureDetail1Name);
                    
                }

                TempTriangle tempTri = new TempTriangle();
                tempTri.triangle = triangle;
                tempTri.vertexBoneInformation = vertexBoneInformation;
                return tempTri;    
            }
        }
        return null;
    }    

    protected static class VertexBoneInformation
    {
        public String[] boneIDs;
        public VertexBoneInformation()
        {
            boneIDs = new String[3];

            boneIDs[0] = null;
            boneIDs[1] = null;
            boneIDs[2] = null;
        }
    }
    
    private static VertexBoneInformation readVertexBoneInformation(StringTokenizer tokenizer )
    {
        VertexBoneInformation vertexBoneInformation = new VertexBoneInformation();
        
        int i=0;
        while(tokenizer.hasMoreTokens() && i < 3)
        {
            vertexBoneInformation.boneIDs[i] = (String)readNextValue(tokenizer);
            i++;
        }
        
        return vertexBoneInformation;
    }    
    
    protected static E3DVector3F readVector3F(String vectorLine)
    {
        StringTokenizer vectorTokenizer = new StringTokenizer(vectorLine, TOKENIZER_CHARS);
        
        double posX, posY, posZ;
        posX = Double.parseDouble(readNextValue(vectorTokenizer));
        posY = Double.parseDouble(readNextValue(vectorTokenizer));
        posZ = Double.parseDouble(readNextValue(vectorTokenizer));
        return new E3DVector3F(posX, posY, posZ);

    }
    protected static E3DVector3F readVector3F(StringTokenizer vectorTokenizer)
    {
        double posX, posY, posZ;
        posX = Double.parseDouble(readNextValue(vectorTokenizer));
        posY = Double.parseDouble(readNextValue(vectorTokenizer));
        posZ = Double.parseDouble(readNextValue(vectorTokenizer));
        return new E3DVector3F(posX, posY, posZ);

    }
    
}
