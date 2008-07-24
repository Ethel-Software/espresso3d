/*
 * Created on Mar 12, 2005
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
package espresso3d.engine.lowlevel.matrix;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import espresso3d.engine.lowlevel.vector.E3DVector3F;

/**
 * @author Curt
 *
 *  stored  [row][col]
 */
public class E3DMatrix4x4F {
    //rowXcolumn
    double[][] matrix4x4 = new double[4][4];
    private static FloatBuffer buffer16;
    public E3DMatrix4x4F()
    {
        loadIdentity();
        if(buffer16 == null)
            buffer16 = BufferUtils.createFloatBuffer(16);
        
    }
    
    public void loadIdentity()
    {
        matrix4x4[0][0] = 1;
        matrix4x4[0][1] = 0;
        matrix4x4[0][2] = 0;
        matrix4x4[0][3] = 0;

        matrix4x4[1][0] = 0;
        matrix4x4[1][1] = 1;
        matrix4x4[1][2] = 0;
        matrix4x4[1][3] = 0;

        matrix4x4[2][0] = 0;
        matrix4x4[2][1] = 0;
        matrix4x4[2][2] = 1;
        matrix4x4[2][3] = 0;

        matrix4x4[3][0] = 0;
        matrix4x4[3][1] = 0;
        matrix4x4[3][2] = 0;
        matrix4x4[3][3] = 1;
    }
    
    public E3DMatrix4x4F(E3DMatrix4x4F toCopyMatrix)
    {
        matrix4x4[0][0] = toCopyMatrix.matrix4x4[0][0];
        matrix4x4[0][1] = toCopyMatrix.matrix4x4[0][1];
        matrix4x4[0][2] = toCopyMatrix.matrix4x4[0][2];
        matrix4x4[0][3] = toCopyMatrix.matrix4x4[0][3];

        matrix4x4[1][0] = toCopyMatrix.matrix4x4[1][0];
        matrix4x4[1][1] = toCopyMatrix.matrix4x4[1][1];
        matrix4x4[1][2] = toCopyMatrix.matrix4x4[1][2];
        matrix4x4[1][3] = toCopyMatrix.matrix4x4[1][3];

        matrix4x4[2][0] = toCopyMatrix.matrix4x4[2][0];
        matrix4x4[2][1] = toCopyMatrix.matrix4x4[2][1];
        matrix4x4[2][2] = toCopyMatrix.matrix4x4[2][2];
        matrix4x4[2][3] = toCopyMatrix.matrix4x4[2][3];

        matrix4x4[3][0] = toCopyMatrix.matrix4x4[3][0];
        matrix4x4[3][1] = toCopyMatrix.matrix4x4[3][1];
        matrix4x4[3][2] = toCopyMatrix.matrix4x4[3][2];
        matrix4x4[3][3] = toCopyMatrix.matrix4x4[3][3];
    }

    /**
     * Gets a colxrow intead of row x col
     * @return
     */
   
    public FloatBuffer getFloatBuffer()
    {
        buffer16.clear();
        //give col x row ordering
        for(int col = 0; col < 4; col++)
        {
            for(int row = 0; row < 4; row ++)
                buffer16.put((float)matrix4x4[row][col]);
        }
        buffer16.rewind();
        return buffer16;
    }    
    
    public E3DMatrix4x4F multiply(E3DMatrix4x4F multiplyMatrix)
    {
        int i, j, k;

        E3DMatrix4x4F newMatrix = new E3DMatrix4x4F();

        for(i = 0; i < 4; i++)
        {
          for(j = 0; j < 4; j++)
          {
            newMatrix.matrix4x4[i][j] = 0;
            for(k = 0; k < 4; k++)
               newMatrix.matrix4x4[i][j] += matrix4x4[i][k] * multiplyMatrix.matrix4x4[k][j];
          }
        }

        return newMatrix;
    }

    public void multiplyEqual(E3DMatrix4x4F multiplyMatrix)
    {
        int i, j, k;

        double[][] scratchMatrix4x4 = new double[4][4];
        
        for(i = 0; i < 4; i++)
        {
          for(j = 0; j < 4; j++)
          {
            scratchMatrix4x4[i][j] = 0.0;
            for(k = 0; k < 4; k++)
                scratchMatrix4x4[i][j] += matrix4x4[i][k] * multiplyMatrix.matrix4x4[k][j];
          }
        }        
        
        this.matrix4x4 = scratchMatrix4x4;
    }

    public E3DMatrix4x4F add(E3DMatrix4x4F addMatrix)
    {
        E3DMatrix4x4F newMatrix = new E3DMatrix4x4F();
        
        int row, col;
        for(row = 0; row < 4; row++)
        {
            for(col = 0; col < 4; col++)
                matrix4x4[row][col] = matrix4x4[row][col] + addMatrix.matrix4x4[row][col];
        }
        
        return newMatrix;
    }
    
    public void addEqual(E3DMatrix4x4F addMatrix)
    {
        int row, col;
        for(row = 0; row < 4; row++)
        {
            for(col = 0; col < 4; col++)
                matrix4x4[row][col] += addMatrix.matrix4x4[row][col];
        }
    }
    
    public E3DVector3F multiplyVector(E3DVector3F multiplyVector)
    {
        double x = multiplyVector.getX(), y = multiplyVector.getY(), z = multiplyVector.getZ();
        
        return new E3DVector3F((matrix4x4[0][0] * x) + (matrix4x4[0][1] * y) + (matrix4x4[0][2] * z),
                                (matrix4x4[1][0] * x) + (matrix4x4[1][1] * y) + (matrix4x4[1][2] * z),
                                (matrix4x4[2][0] * x) + (matrix4x4[2][1] * y) + (matrix4x4[2][2] * z));
    }
    
    public double[][] getMatrix4x4()
    {
    	return matrix4x4;
    }
    //Not sure below actually works, need to do a multiply ?
/*    public void setPosition(E3DVector3F position)
    {
        matrix4x4[0][3] = position.getX();
        matrix4x4[1][3] = position.getY();
        matrix4x4[2][3] = position.getZ();
    } */
 

    public String toString()
    {
    	return "[" + matrix4x4[0][0] + "\t" + matrix4x4[0][1] + "\t" + matrix4x4[0][2] + "\t" + matrix4x4[0][3] + "]\n" + 
               "[" + matrix4x4[1][0] + "\t" + matrix4x4[1][1] + "\t" + matrix4x4[1][2] + "\t" + matrix4x4[1][3] + "]\n" +
    	       "[" + matrix4x4[2][0] + "\t" + matrix4x4[2][1] + "\t" + matrix4x4[2][2] + "\t" + matrix4x4[2][3] + "]\n" +
    	       "[" + matrix4x4[3][0] + "\t" + matrix4x4[3][1] + "\t" + matrix4x4[3][2] + "\t" + matrix4x4[3][3] + "]\n";
    }
    
    /**
     * Not a true inverse.  It only inverts the rotation portion of the matrix NOT the translation or scale/scew
     *
     */
    public void invertFastEqual()
	{
		E3DMatrix4x4F scratchMatrix = new E3DMatrix4x4F();
		
//		scratchMatrix.matrix4x4[0][0] =  scratchMatrix.matrix4x4[]
		scratchMatrix.matrix4x4[0][1] = matrix4x4[1][0];
		scratchMatrix.matrix4x4[0][2] = matrix4x4[2][0];
		scratchMatrix.matrix4x4[1][0] = matrix4x4[0][1];
//		scratchMatrix.matrix4x4[1][1] = 
		scratchMatrix.matrix4x4[1][2] = matrix4x4[2][1];
		scratchMatrix.matrix4x4[2][0] = matrix4x4[0][2];
		scratchMatrix.matrix4x4[2][1] = matrix4x4[1][2];
//		scratchMatrix.matrix4x4[2][2] =
		
		matrix4x4[0][1] = scratchMatrix.matrix4x4[0][1];
		matrix4x4[0][2] = scratchMatrix.matrix4x4[0][2];
		matrix4x4[1][0] = scratchMatrix.matrix4x4[1][0];
		matrix4x4[1][2] = scratchMatrix.matrix4x4[1][2];
		matrix4x4[2][0] = scratchMatrix.matrix4x4[2][0];
		matrix4x4[2][1] = scratchMatrix.matrix4x4[2][1];
	}

    public E3DMatrix4x4F invertFast()
	{
		E3DMatrix4x4F scratchMatrix = new E3DMatrix4x4F();
		
		scratchMatrix.matrix4x4[0][0] = matrix4x4[0][0];
		scratchMatrix.matrix4x4[0][1] = matrix4x4[1][0];
		scratchMatrix.matrix4x4[0][2] = matrix4x4[2][0];
		scratchMatrix.matrix4x4[1][0] = matrix4x4[0][1];
		scratchMatrix.matrix4x4[1][1] = matrix4x4[1][1];
		scratchMatrix.matrix4x4[1][2] = matrix4x4[2][1];
		scratchMatrix.matrix4x4[2][0] = matrix4x4[0][2];
		scratchMatrix.matrix4x4[2][1] = matrix4x4[1][2];
		scratchMatrix.matrix4x4[2][2] = matrix4x4[2][2];
		
		return scratchMatrix;
	}

}
