/*
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
 * 
 */
package espresso3d.testbed;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector3F;

public class MatrixTest
{
	public static void main(String[] args)
	{
		E3DEngine engine = new E3DEngine();
		
		E3DVector3F vec = new E3DVector3F(0, 0, 1);
		
		E3DOrientation orientation = new E3DOrientation(engine);
		
		orientation.rotate(E3DConstants.HALFPI, new E3DVector3F(0, 1, 0)); //should rotate the up to be in the x dir
		System.out.println(orientation);
//		orientation.translate(new E3DVector3F(1, 0, 0));
		System.out.println("Orien x Vec = " + orientation.multiply(vec));
		System.out.println("WorldVector = " + orientation.getWorldVector(vec));
		System.out.println("LocalVector = " + orientation.getLocalVector(vec));
		
		E3DOrientation orientation2 = new E3DOrientation(engine);
		orientation2.rotate(E3DConstants.HALFPI, new E3DVector3F(0, 1, 0));
		System.out.println(orientation2);
		
		E3DOrientation cumuOrientation = orientation.multiply(orientation2.getOrientationMatrix()); //now rotates 180deg. it b ecomes 0, 0, -1 (or close to it)
		System.out.println(cumuOrientation);
		
		System.out.println("cumuOrientation Orien x Vec = " + cumuOrientation.multiply(vec));
		System.out.println("cumuOrientation WorldVector = " + cumuOrientation.getWorldVector(vec)); //The right value
		System.out.println("cumuOrientation LocalVector = " + cumuOrientation.getLocalVector(vec));
		
		
		
		
		
/*		orientation = new E3DOrientation(engine);
		E3DMatrix4x4F orMatrix = orientation.getOrientationMatrix();

		E3DMatrix4x4F rotMatrix = new E3DMatrix4x4F();
		matrix.getMatrix4x4()[0][0] = 
	*/	
	}
	
}
