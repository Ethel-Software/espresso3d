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
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.matrix.E3DQuaternion;

public class TestSkeletalRotationJUnit
{
	public static void main(String[] args)
	{
		E3DEngine engine = new E3DEngine();
		try{
//			engine.initEngine(320, 240, 32, false, "Test");
		
			E3DOrientation orientation = new E3DOrientation(engine);
			orientation.loadIdentity();
			
			E3DOrientation rotation = new E3DOrientation(engine);
			rotation.loadIdentity();
			
			System.out.println("Before rotation=======\n" + orientation);
			rotation.rotateXEqual(1);
			rotation.rotateYEqual(1);
			rotation.rotateZEqual(1);
			System.out.println("Rotation====\n" + rotation.getOrientationMatrix());
			orientation.multiplyEqual(rotation.getOrientationMatrix());

			E3DQuaternion quat = new E3DQuaternion(engine);
			E3DQuaternion quat2 = new E3DQuaternion(engine);
			quat.rotateQuatFromEulerEqual(1, 1, 1);
			quat2.rotateQuatFromEulerEqual(1, 1, 1);
			System.out.println("QuatRotation====\n" + quat.getRotationMatrix());
			
			E3DOrientation rotation3 = new E3DOrientation(engine);
			rotation3.rotateXEqual(1);
			rotation3.rotateYEqual(1);
			rotation3.rotateZEqual(1);
			System.out.println("Rotation3====\n" + rotation3.getOrientationMatrix());
			orientation.multiplyEqual(rotation3.getOrientationMatrix());
			
			rotation.multiplyEqual(rotation3.getOrientationMatrix());
			
			quat.multiplyEqual(quat2);
			System.out.println("Quat 2====\n" + quat.getRotationMatrix());
			
			E3DOrientation rotation2 = new E3DOrientation(engine);
			rotation2.rotateXEqual(2);
			rotation2.rotateYEqual(2);
			rotation2.rotateZEqual(2);
			
			System.out.println("Total Rotation Combined\n" + rotation);
			System.out.println("Single Rotation\n" + rotation2);
//			rotation.rotateXEqual(1);
//			rotation.rotateYEqual(1);
//			rotation.rotateZEqual(1);
			
//			orientation = rotation.multiply(orientation.getOrientationMatrix()); //multiplyEqual(rotation.getOrientationMatrix());
			
//			System.out.println("After rotation=======\n" + orientation);
			
//			E3DOrientation rotation2 = new E3DOrientation(rotation);
			rotation.invertFastEqual();
			
//			System.out.println("Rotation====\n" + rotation.getOrientationMatrix());
//			System.out.println("Rotation====\n" + rotation);
			orientation.multiplyEqual(rotation.getOrientationMatrix());
			orientation.normaliseForwardAndUpEqual();
			System.out.println("After rotation Back=======\n" + orientation);
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
