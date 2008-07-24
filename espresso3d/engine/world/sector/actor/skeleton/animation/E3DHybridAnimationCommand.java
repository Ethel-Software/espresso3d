/*
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
 * 
 */
package espresso3d.engine.world.sector.actor.skeleton.animation;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.matrix.E3DQuaternion;
import espresso3d.engine.lowlevel.vector.E3DVector3F;

public class E3DHybridAnimationCommand extends E3DAnimationCommand
{
	//Rotate, then
	E3DVector3F rotationAmt;
	E3DQuaternion startQuat;
	E3DQuaternion curRotationQuat;
	E3DQuaternion destQuat;

	//Translate
	private E3DVector3F translationAmt;
	private E3DVector3F curTranslationAmt;
	
	public E3DHybridAnimationCommand(E3DEngine engine, String boneID, double executionTime)
	{
		super(engine, boneID, executionTime);
		
		startQuat = null;
		curRotationQuat = null;
		destQuat = null;
		
		translationAmt = null;
		curTranslationAmt = new E3DVector3F(0,0,0);
	}
	
	public E3DHybridAnimationCommand(E3DHybridAnimationCommand toCopyCommand)
	{
		super(toCopyCommand.getEngine(), toCopyCommand.getBoneID(), toCopyCommand.getExecutionTime());
		
		startQuat = new E3DQuaternion(toCopyCommand.startQuat);
		curRotationQuat = new E3DQuaternion(toCopyCommand.curRotationQuat);
		destQuat = new E3DQuaternion(toCopyCommand.destQuat);
		
		translationAmt = new E3DVector3F(toCopyCommand.translationAmt);
		curTranslationAmt = new E3DVector3F(toCopyCommand.curTranslationAmt);
	}
	
	public void setTranslationAmt(E3DVector3F translationAmt)
	{
		this.translationAmt = new E3DVector3F(translationAmt);
	}
	
	
	public E3DAnimationCommand getClone()
	{
		return new E3DHybridAnimationCommand(this);
	}

	public void executeCommand()
	{
		// TODO Auto-generated method stub
	}

	void resetCommand()
	{
		// TODO Auto-generated method stub
	}

	public E3DVector3F getRotationAmt()
	{
		return rotationAmt;
	}

	public void setRotationAmt(E3DVector3F rotationAmt)
	{
		this.rotationAmt = rotationAmt;
	}

	public E3DVector3F getTranslationAmt()
	{
		return translationAmt;
	}
	
	private void executeRotate()
	{
		
		
	}
	
	private void executeTranslate()
	{
		
		
	}
}
