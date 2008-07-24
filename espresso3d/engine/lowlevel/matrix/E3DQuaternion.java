/*
 
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

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;

public class E3DQuaternion extends E3DEngineItem
{
	E3DVector4F quat;
	
	public E3DQuaternion(E3DEngine engine)
	{
		this(engine, 0, 0, 0, 1);
	}
	
	public E3DQuaternion(E3DEngine engine, double x, double y, double z, double w)
	{
		this(engine, new E3DVector4F(x, y, z, w));
	}
	
	public E3DQuaternion(E3DEngine engine, E3DVector4F quat)
	{
		super(engine);
		this.quat = quat;
	}
	
	public E3DQuaternion(E3DQuaternion toCopyQuat)
	{
		this(toCopyQuat.getEngine(),  new E3DVector4F(toCopyQuat.quat));
	}

	public void loadIdentity()
	{
		set(0,0,0,1);
	}
	
	public void set(double x, double y, double z, double w)
	{
		quat.set(x, y, z, w);
	}

	public void set(E3DVector4F vecToCopy)
	{
		quat.set(vecToCopy.getA(), vecToCopy.getB(), vecToCopy.getC(), vecToCopy.getD());
	}
	public double getLength()
	{
		return quat.getLength();
	}
	
	public double getLengthSquared()
	{
		return quat.getLengthSquared();
	}
	
	public E3DQuaternion multiply(E3DQuaternion quatToMult)
	{
		double x1, x2, y1, y2, z1, z2, w1, w2;
		
		x1 = quat.getA();
		x2 = quatToMult.quat.getA();
		y1 = quat.getB();
		y2 = quatToMult.quat.getB();
		z1 = quat.getC();
		z2 = quatToMult.quat.getC();
		w1 = quat.getD();
		w2 = quatToMult.quat.getD();
		
		return new E3DQuaternion(getEngine(), (w1 * x2) + (x1 * w2) + (y1 * z2) - (z1 * y2),
				 (w1 * y2) - (x1 * z2) + (y1 * w2) + (z1 * x2),
				 (w1 * z2) + (x1 * y2) - (y1 * x2) + (z1 * w2),
				 (w1 * w2) - (x1 * x2) - (y1 * y2) - (z1 * z2));
	}

	public void multiplyEqual(E3DQuaternion quatToMult)
	{
//		(Q1 * Q2).x = (w1x2 + x1w2 + y1z2 - z1y2)
//		(Q1 * Q2).y = (w1y2 - x1z2 + y1w2 + z1x2)
//		(Q1 * Q2).z = (w1z2 + x1y2 - y1x2 + z1w2
//		(Q1 * Q2).w = (w1w2 - x1x2 - y1y2 - z1z2)

		double x1, x2, y1, y2, z1, z2, w1, w2;
		
		x1 = quat.getA();
		x2 = quatToMult.quat.getA();
		y1 = quat.getB();
		y2 = quatToMult.quat.getB();
		z1 = quat.getC();
		z2 = quatToMult.quat.getC();
		w1 = quat.getD();
		w2 = quatToMult.quat.getD();
		
		quat.set((w1 * x2) + (x1 * w2) + (y1 * z2) - (z1 * y2),
				 (w1 * y2) - (x1 * z2) + (y1 * w2) + (z1 * x2),
				 (w1 * z2) + (x1 * y2) - (y1 * x2) + (z1 * w2),
				 (w1 * w2) - (x1 * x2) - (y1 * y2) - (z1 * z2));
		quat.normaliseEqual();
		
	}
	
	public E3DQuaternion add(E3DQuaternion addQuat)
	{
		E3DQuaternion newQuat = new E3DQuaternion(this);
		newQuat.addEqual(addQuat);
		return newQuat;
	}
	
	public void addEqual(E3DQuaternion addQuat)
	{
		set(this.quat.add(addQuat.quat));
	}
	/**
	 * Return a new normalised quaternion vector
	 * @return
	 */
	public E3DQuaternion normalise()
	{
		E3DQuaternion ret = new E3DQuaternion(this);
		ret.normaliseEqual();
		return ret;
	}
	
	/**
	 * Normalise this vector.. Sometimes takes double normalisation for precision
	 */
	public void normaliseEqual()
	{
		quat.normaliseEqual();
		if(E3DConstants.closeToNormalizationPrecisionError(quat.getA()))
			quat.setA(0.0);
		if(E3DConstants.closeToNormalizationPrecisionError(quat.getB()))
			quat.setB(0.0);
		if(E3DConstants.closeToNormalizationPrecisionError(quat.getC()))
			quat.setC(0.0);
		if(E3DConstants.closeToNormalizationPrecisionError(quat.getD()))
			quat.setD(0.0);
		quat.normaliseEqual();
	}

	/**
	 * Returns the rotation matrix corresponding to this quaternion
	 * @return
	 */
	public E3DMatrix4x4F getRotationMatrix()
	{
	 	double xx      = quat.getA() * quat.getA();
	 	double xy      = quat.getA() *  quat.getB();
	 	double xz      = quat.getA() *  quat.getC();
	 	double xw      = quat.getA() *  quat.getD();

	 	double yy      = quat.getB() *  quat.getB();
	 	double yz      = quat.getB() *  quat.getC();
	 	double yw      = quat.getB() *  quat.getD();

	 	double zz      = quat.getC() * quat.getC();
	 	double zw      = quat.getC() * quat.getD();

	 	E3DMatrix4x4F matrix = new E3DMatrix4x4F();
	 	matrix.matrix4x4[0][0] = 1.0 - 2.0 * ( yy + zz );
	    matrix.matrix4x4[1][0] = 2.0 * ( xy - zw );
	    matrix.matrix4x4[2][0] = 2 * ( xz + yw );

	    matrix.matrix4x4[0][1]  = 2.0 * ( xy + zw );
	    matrix.matrix4x4[1][1]  = 1.0 - 2.0 * ( xx + zz );
	    matrix.matrix4x4[2][1]  = 2.0 * ( yz - xw );

	    matrix.matrix4x4[0][2] = 2.0 * ( xz - yw );
	    matrix.matrix4x4[1][2] = 2.0 * ( yz + xw );
	    matrix.matrix4x4[2][2] = 1.0 - 2.0 * ( xx + yy );

	    return matrix;
	}
	
	/**
	 * Gets an E3DOrientation with the orientation matrix already in it.  Slightly
	 * slower than getting the direct matrix
	 * @return
	 */
	public E3DOrientation getRotationOrientation()
	{
		return new E3DOrientation(getEngine(), getRotationMatrix());
	}
	
	/**
	 * Create a quaternion from a rotation matrix This ignores the
	 * scale/position portion of the 4x4 (only uses the 3x3 rotation portion)
	 * @param rotationOrientation
	 */
	public void setByRotationMatrix(E3DOrientation rotationOrientation)
	{
		setByRotationMatrix(rotationOrientation.getOrientationMatrix());
	}
	
	/**
	 * Create a quaternion from a rotation orientation matrix.  This ignores the
	 * scale/position portion of the 4x4 (only uses the 3x3 rotation portion)
	 * @param rotationOrientation
	 */
	public void setByRotationMatrix(E3DMatrix4x4F rotationMatrix)
	{
		double t = rotationMatrix.matrix4x4[0][0] + rotationMatrix.matrix4x4[1][1] + rotationMatrix.matrix4x4[2][2] + 1;
	
		double s, qx, qy, qz, qw;
		
		if(E3DConstants.closeToPrecisionError(4.0 - t))// > E3DConstants.DBL_PRECISION_ERROR)
		{
			s = Math.sqrt(t) * 2.0;
			qw = 0.25 * s;
			qx = (rotationMatrix.matrix4x4[1][2] - rotationMatrix.matrix4x4[2][1]) / s;
			qy = (rotationMatrix.matrix4x4[2][0] - rotationMatrix.matrix4x4[2][0]) / s;
			qz = (rotationMatrix.matrix4x4[0][1] - rotationMatrix.matrix4x4[1][0]) / s;
		}
		else
		{
			if(rotationMatrix.matrix4x4[0][0] > rotationMatrix.matrix4x4[1][1] && rotationMatrix.matrix4x4[0][0] > rotationMatrix.matrix4x4[2][2])
			{
				s  = Math.sqrt(1.0 + rotationMatrix.matrix4x4[0][0] - rotationMatrix.matrix4x4[1][1] - rotationMatrix.matrix4x4[2][2]) * 2;

				qx = 0.25 * s;
				qy = (rotationMatrix.matrix4x4[0][1] + rotationMatrix.matrix4x4[1][0]) / s;
				qz = (rotationMatrix.matrix4x4[2][0] + rotationMatrix.matrix4x4[0][2]) / s;
				qw = (rotationMatrix.matrix4x4[1][2] - rotationMatrix.matrix4x4[2][1]) / s;
			}
			else if ( rotationMatrix.matrix4x4[1][1] > rotationMatrix.matrix4x4[2][2] ) 
			{
				s  = Math.sqrt(1.0 + rotationMatrix.matrix4x4[1][1] - rotationMatrix.matrix4x4[0][0] - rotationMatrix.matrix4x4[2][2]) * 2;

				qx = (rotationMatrix.matrix4x4[0][1] + rotationMatrix.matrix4x4[1][0]) / s;
				qy = 0.25 * s;
				qz = (rotationMatrix.matrix4x4[1][2] + rotationMatrix.matrix4x4[2][1]) / s;
				qw = (rotationMatrix.matrix4x4[2][0] - rotationMatrix.matrix4x4[0][2]) / s;
			}
			else //mag 2
			{
		        s  = Math.sqrt(1.0 + rotationMatrix.matrix4x4[2][2] - rotationMatrix.matrix4x4[0][0] - rotationMatrix.matrix4x4[1][1]) * 2;

				qx = (rotationMatrix.matrix4x4[2][0] + rotationMatrix.matrix4x4[0][2]) / s;
				qy = (rotationMatrix.matrix4x4[1][2] + rotationMatrix.matrix4x4[2][1]) / s;
				qz = 0.25 * s;
				qw = (rotationMatrix.matrix4x4[0][1] - rotationMatrix.matrix4x4[1][0]) / s;
			}
		}
		quat.set(qx, qy, qz, qw);
		quat.normaliseEqual();
	}
	
	public E3DQuaternion rotate(double angle, E3DVector3F aroundVec)
	{
		E3DQuaternion quaternion = new E3DQuaternion(this);
		
		quaternion.rotateEqual(angle, aroundVec);
		
		return quaternion;
	}
	
	public void rotateEqual(double angle, E3DVector3F aroundVec)
	{
		angle = angle / 2.0;
		double sinAng = Math.sin(angle);
		double cosAng = Math.cos(angle);

		quat.set(aroundVec.getX() * sinAng, aroundVec.getY() * sinAng, aroundVec.getZ() * sinAng, cosAng);
		normaliseEqual();
	}
	
	public E3DQuaternion rotateQuatFromEuler(E3DVector3F eulers)
	{
		return rotateQuatFromEuler(eulers.getX(), eulers.getY(), eulers.getZ());
	}
	
	public E3DQuaternion rotateQuatFromEuler(double x, double y, double z)
	{
		E3DQuaternion retQuat = new E3DQuaternion(this);
		retQuat.rotateQuatFromEulerEqual(x, y, z);
		return retQuat;
	}
	
	public E3DQuaternion rotateX(double x)
	{
		return rotate(x, new E3DVector3F(1, 0, 0));
	}
	
	public void rotateXEqual(double x)
	{
		rotateEqual(x, new E3DVector3F(1, 0, 0));
	}

	public E3DQuaternion rotateY(double y)
	{
		return rotate(y, new E3DVector3F(0, 1, 0));
	}
	
	public void rotateYEqual(double y)
	{
		rotateEqual(y, new E3DVector3F(0, 1, 0));
	}	
	
	public E3DQuaternion rotateZ(double z)
	{
		return rotate(z, new E3DVector3F(0, 0, 1));
	}
	
	public void rotateZEqual(double z)
	{
		rotateEqual(z, new E3DVector3F(0, 0, 1));
	}	
	
	public void rotateQuatFromEulerEqual(E3DVector3F vec)
	{
		rotateQuatFromEulerEqual(vec.getX(), vec.getY(), vec.getZ());
	}
	public void rotateQuatFromEulerEqual(double x, double y, double z)
	{
//		E3DOrientation orientation = getRotationOrientation();
		
		
//		E3DQuaternion rotationQuat = new E3DQuaternion(getEngine());
//		rotationQuat.setQuatFromEulerEqual(x, y, z);
		
		E3DVector3F vx = new E3DVector3F(1, 0, 0);
		E3DVector3F vy = new E3DVector3F(0, 1, 0);
		E3DVector3F vz = new E3DVector3F(0, 0, 1);

		E3DQuaternion qx = rotate(x, vx);
		E3DQuaternion qy = rotate(y, vy);
		E3DQuaternion qz = rotate(z, vz);
//	
//		this.quat = qx.multiply(qy.multiply(qz)).quat;
		
//		this.multiplyEqual(rotationQuat);
//		normaliseEqual();

		
		
		this.quat = (((multiply(qx)).multiply(qy)).multiply(qz)).quat;
//		this.quat =((this.multiply(multiply(qx)).multiply(qy)).multiply(qz)).quat;
//		quat.normaliseEqual();
	}
	
	/**
	 * Assumes identity orientation and sets the quaternion to this euler. Does NOT add rotation to the current
	 * quaternions rotation, it overwrites it.
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setQuatFromEulerEqual(double x, double y, double z)
	{
		double sx = Math.sin( x / 2.0);
		double cx = Math.cos(x / 2.0);
		double sy = Math.sin( y / 2.0 );
		double cy = Math.cos( y / 2.0);
		double sz = Math.sin( z / 2.0 );
		double cz = Math.cos( z / 2.0 );

		double cxcy = cx*cy;
		double sxsy = sx*sy;
		
	    quat.set((sx*cy*cz) - (cx*sy*sz),
	    		 (cx*sy*cz) + (sx*cy*sz),
	    		 (cxcy*sz) - (sxsy * cz),
	    		 (cxcy*cz) + (sxsy*sz));
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public E3DQuaternion setQuatFromEuler(double x, double y, double z)
	{
		E3DQuaternion quat = new E3DQuaternion(getEngine());
		quat.setQuatFromEulerEqual(x, y, z);
		return quat;
	}
	
	/**
	 * Get the dot product of thisQuaternion.otherQuaternion
	 * @param quaternion
	 * @return
	 */
	public double dotProduct(E3DQuaternion otherQuaternion)
	{
		return this.quat.dotProduct(otherQuaternion.quat);
	}
	
	/**
	 * Slerp from this to destinationQuat.  Alpha interpolation, spin number of extra spins
	 * @param destinationQuat slerp to this quat
	 * @param alpha Alpha interpolation value
	 * @param spin Extra spins to throw in
	 * @return
	 * 	Quaternion representing the slerp'd rotation
	 */
	public E3DQuaternion slerp(E3DQuaternion destinationQuat, double alpha, int spin)
	{
		E3DQuaternion retQuat = new E3DQuaternion(this);
		retQuat.slerpEqual(destinationQuat, alpha, spin);
		return retQuat;
	}

	
	public void slerpEqual(E3DQuaternion destinationQuat, double alpha, int spin)
	{
		//a = this
		//b = destinationQuat
		//q = return
		//alpha = alpha
		double beta; //complimentary interp parameter
		double theta; //angle between thisQuat and destinationQuat
		double sinTheta, cosTheta; //sin and cos of theta
		double phi; //theta + spins
		boolean flipB; //use negations
		
		cosTheta = this.dotProduct(destinationQuat); //cosTheta == dotproduct in quats
		
		//If B is on opposite hemisphere than A, use B instead
		if(cosTheta < 0)
		{
			cosTheta = -cosTheta;
			flipB = true;
		}
		else
			flipB = false;
		
		//If B is (within precision limits) the same as A
		// just linearly interpolate between A and B
		// Can't do spins because we don't know what direction to spin
		if(1.0 - cosTheta < E3DConstants.DBL_PRECISION_ERROR)
			beta = 1.0 - alpha;
		else //normal case
		{
			theta = Math.acos(cosTheta);
			phi = theta + (spin * E3DConstants.PI);
			sinTheta = Math.sin(theta);
			beta = Math.sin(theta - (alpha*phi)) / sinTheta;
			alpha = Math.sin(alpha * phi) / sinTheta;
		}
		
		if(flipB)
			alpha = -alpha;
		
		set((beta * quat.getA()) + (alpha * destinationQuat.quat.getA()),
			(beta * quat.getB()) + (alpha * destinationQuat.quat.getB()),
			(beta * quat.getC()) + (alpha * destinationQuat.quat.getC()),
			(beta * quat.getD()) + (alpha * destinationQuat.quat.getD()));		
//		normaliseEqual();
		
	}
	
	public double getX()
	{
		return quat.getA();
	}
	
	public void setX(double x)
	{
		quat.setA(x);
	}

	public double getY()
	{
		return quat.getB();
	}
	
	public void setY(double y)
	{
		quat.setB(y);
	}

	public double getZ()
	{
		return quat.getC();
	}
	
	public void setZ(double z)
	{
		quat.setC(z);
	}

	public double getW()
	{
		return quat.getD();
	}
	
	public void setW(double w)
	{
		quat.setD(w);
	}
	
	public E3DQuaternion scale(double scaleAmt)
	{
		E3DQuaternion quat = new E3DQuaternion(this);
		quat.scaleEqual(scaleAmt);
		return quat;
	}
	
	public void scaleEqual(double scaleAmt)
	{
		quat.scaleEqual(scaleAmt);
	}
	public E3DQuaternion invert()
	{
		E3DQuaternion ret = new E3DQuaternion(this);
		ret.invertEqual();
		return ret;
	}
	
	public void invertEqual()
	{
		quat.setA(-quat.getA());
		quat.setB(-quat.getB());
		quat.setC(-quat.getC());
		scaleEqual(1.0 / getLengthSquared());
	}

	public E3DQuaternion reverse()
	{
		E3DQuaternion ret = new E3DQuaternion(this);
		ret.reverseEqual();
		return ret;
	}
	public void reverseEqual()
	{
		quat.setA(-quat.getA());
		quat.setB(-quat.getB());
		quat.setC(-quat.getC());
		quat.setD(-quat.getD());
	}
	public boolean closeTo(E3DVector4F vec)
	{
		return quat.closeTo(vec);
	}
	
	public boolean closeTo(double x, double y, double z, double w)
	{
		return quat.closeTo(x, y, z, w);
	}

	public boolean equals(E3DVector4F vec)
	{
		return quat.equals(vec);
	}
	
	public boolean equals(double x, double y, double z, double w)
	{
		return quat.equals(x, y, z, w);
	}

	public String toString()
	{
		return "(" + quat.getA() + ", " + quat.getB() + ", " + quat.getC() + ", " + quat.getD() + ")";
	}
	
	/**
	 * Will rotate a vector by the quaternion
	 * @param vecToRotate
	 * @return
	 */
	public E3DVector3F rotateVector(E3DVector3F vecToRotate)
	{
		E3DQuaternion quat = new E3DQuaternion(getEngine(), vecToRotate.getX(), vecToRotate.getY(), vecToRotate.getZ(), 0);
		
		quat = this.multiply(quat).multiply(invert());
		
		return new E3DVector3F(quat.getX(), quat.getY(), quat.getZ());
	}
}
