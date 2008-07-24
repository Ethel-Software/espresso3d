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

public class TestQuaternionRotationJUnit
{
	public static void main(String[] args)
	{
		E3DEngine engine = new E3DEngine();
		try{
//			testRotateAndUnRotate(engine);
			testSlerpRotateAndFullUnRotate(engine);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void testRotateAndUnRotate(E3DEngine engine) throws Exception
	{
		E3DOrientation orientation = new E3DOrientation(engine);
		orientation.loadIdentity();

		System.out.println("Start Orientation\n" + orientation);
		
		E3DQuaternion rotationQuat = new E3DQuaternion(engine);
		
		rotationQuat.rotateQuatFromEulerEqual(1, 1, 0);
		System.out.println("Rotation Quat Full\n" + rotationQuat.getRotationMatrix());

		E3DOrientation rotatedOrientation = orientation.multiply(rotationQuat.getRotationMatrix());
		System.out.println("Rotated Orientation\n" + rotatedOrientation);
		
		E3DOrientation inverseRotation = rotationQuat.getRotationOrientation();
		inverseRotation.invertFastEqual();
		System.out.println("Reverse Rotated With Inverse (original pos)\n" + rotatedOrientation.multiply(inverseRotation.getOrientationMatrix()));

	}
	
	public static void testSlerpRotateAndFullUnRotate(E3DEngine engine) throws Exception
	{
		E3DOrientation orientation = new E3DOrientation(engine);
		orientation.loadIdentity();

//		System.out.println("Start Orientation\n" + orientation);
		
		E3DQuaternion rotationQuat = new E3DQuaternion(engine);
		rotationQuat.rotateQuatFromEulerEqual(1, 1, 0);
		
		E3DOrientation destOrientation = orientation.multiply(rotationQuat.getRotationMatrix());

		E3DQuaternion destQuat = new E3DQuaternion(engine);
		destQuat.setByRotationMatrix(destOrientation);

		System.out.println("Destination Orientation: \n" + destOrientation.getOrientationMatrix() + "\n" + destQuat.getRotationOrientation().getOrientationMatrix());
		
		E3DQuaternion orientationQuat = new E3DQuaternion(engine);
		orientationQuat.setByRotationMatrix(orientation);
		
		System.out.println("Orientation\n" + orientation.getOrientationMatrix() + "\n" + orientationQuat.getRotationOrientation().getOrientationMatrix());
		//slerp and rotate 4 times
		E3DQuaternion slerpQuat;
		E3DQuaternion finalOrientation = new E3DQuaternion(orientationQuat);
		E3DQuaternion finalRotation = new E3DQuaternion(engine);
		for(int i=0; i < 4; i++)
		{
			slerpQuat = orientationQuat.slerp(destQuat, 0.25, 0);
			finalOrientation.multiplyEqual(slerpQuat);//(slerpQuat.getX(), slerpQuat.getY(), slerpQuat.getZ(), slerpQuat.getW());
			finalRotation.multiplyEqual(slerpQuat);
		}
		finalOrientation.normaliseEqual();
		finalRotation.normaliseEqual();
		System.out.println("Rotation Quat Full After Slerping 4 times\n" + finalOrientation.getRotationMatrix());
		System.out.println("Rotated orientation by slerp\n" + finalOrientation.getRotationOrientation());
		System.out.println("Final rotation \n" + finalRotation.getRotationOrientation());

		System.out.println("Inverse rotation \n" + finalRotation.getRotationMatrix().invertFast());
		E3DOrientation finalPos = finalOrientation.getRotationOrientation().multiply(finalRotation.getRotationMatrix().invertFast()).normaliseForwardAndUp();//multiply(rotationQuat2).getRotationOrientation();
		finalPos.normaliseForwardAndUpEqual();
		System.out.println("Reverse Rotated With Inverse to Original Pos\n" +finalPos);

		
		
		E3DQuaternion rotationQuat2 = new E3DQuaternion(engine);
		rotationQuat2.rotateQuatFromEulerEqual(3.14, 3.14, 0);
//		rotationQuat2.invertEqual();
//		rotationQuat2.normaliseEqual();
		System.out.println("Rotation quat2: \n" + rotationQuat2.getRotationMatrix());
		
		
		finalPos = rotationQuat2.multiply(finalOrientation).getRotationOrientation().normaliseForwardAndUp();
		System.out.println("Final orientation using reverse nums\n" + finalPos);

	}
}
