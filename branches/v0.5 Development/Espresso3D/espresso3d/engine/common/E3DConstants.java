/*
 * Created on Oct 16, 2004
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
package espresso3d.engine.common;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DConstants {
	/**
	 * PI approximation
	 */
	public static final double PI = Math.PI; 

	/**
	 * PI * 2 approximation
	 */
	public static final double TWOPI = Math.PI * 2;
	
	/**
	 * PI / 2 approximation
	 */
	public static final double HALFPI = Math.PI / 2.0;
	
	/**
	 * An acceptable error factor that occurs in double mathematics
	 */
	public static final double DBL_PRECISION_ERROR = 0.00000001;

	/**
	 * Acceptable error factor for normalization
	 */
	public static final double DBL_NORMALIZATION_PRECISION_ERROR = 0.0001;
	
	/**
	 * Returns true if numToCheck is between +DBL_PRECISION_ERROR and -DBL_PRECISION_ERROR
	 */
	public static boolean closeToPrecisionError(double numToCheck)
	{
		return (numToCheck <= DBL_PRECISION_ERROR && numToCheck >= -DBL_PRECISION_ERROR);
		
	}
	
	public static boolean closeToNormalizationPrecisionError(double numToCheck)
	{
		return (numToCheck <= DBL_NORMALIZATION_PRECISION_ERROR && numToCheck >= -DBL_NORMALIZATION_PRECISION_ERROR);
		
	}
	
	public static double clampIfGreater(double val, int maxVal)
	{
		if(val > maxVal)
			return maxVal;
		else
			return val;
	}

	public static double clampIfLess(double val, int minVal)
	{
		if(val < minVal)
			return minVal;
		else
			return val;
	}

}
