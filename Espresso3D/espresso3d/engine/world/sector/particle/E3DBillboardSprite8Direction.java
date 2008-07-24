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
package espresso3d.engine.world.sector.particle;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.logger.E3DEngineLogger;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTexture;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * Class used for "Doom" like sprites that change their texture 
 * depending on their rotation and camera position.
 * 
 * This allows the sprite to have 8 different textures for its rotation
 * (Front, front right, right, back right, back, back left, left, front left)
 * 
 * @author espresso
 */
abstract public class E3DBillboardSprite8Direction extends E3DBillboardSprite
{
	private static final int DEFAULT_BLEND_MODE = E3DBlendMode.BLENDMODE_BLEND;
	
	//These can be animated or single frame textures
	public E3DAnimatedTexture[] animatedTextures = new E3DAnimatedTexture[8];
	
	/**
	 * Create
	 * @param engine
	 * @param position
	 * @param size
	 * @param textureNames Must be array of length 8.  Order of textures will = front, front right, right, back right, back, back left, left, front left
	 */
	public E3DBillboardSprite8Direction(E3DEngine engine, E3DVector3F position, double size, String[] textureNames)
	{
		this(engine, position, size, textureNames, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
	}

	public E3DBillboardSprite8Direction(E3DEngine engine, E3DVector3F position, double size, String[] textureNames, E3DBlendMode blendMode)
	{
		super(engine, position, size, (textureNames.length <=0 ? null : textureNames[0]), blendMode);
		setTextures(textureNames[0], textureNames[1], textureNames[2], textureNames[3], textureNames[4], textureNames[5], textureNames[6], textureNames[7]);
	}

	public E3DBillboardSprite8Direction(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, String[] textureNames, E3DBlendMode blendMode)
	{
		super(engine, position, forward, up, size, (textureNames.length <=0 ? null : textureNames[0]), blendMode);
		setTextures(textureNames[0], textureNames[1], textureNames[2], textureNames[3], textureNames[4], textureNames[5], textureNames[6], textureNames[7]);
	}

	public E3DBillboardSprite8Direction(E3DEngine engine, E3DVector3F position, double size, String textureFront, String textureFrontRight, String textureRight, String textureBackRight, String textureBack, String textureBackLeft, String textureLeft, String textureFrontLeft)
	{
		this(engine, position, size, textureFront, textureFrontRight, textureRight, textureBackRight, textureBack, textureBackLeft, textureLeft, textureFrontLeft, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));
	}

	public E3DBillboardSprite8Direction(E3DEngine engine, E3DVector3F position, double size, String textureFront, String textureFrontRight, String textureRight, String textureBackRight, String textureBack, String textureBackLeft, String textureLeft, String textureFrontLeft, E3DBlendMode blendMode)
	{
		super(engine, position, size, textureFront, blendMode);
		setTextures(textureFront, textureFrontRight, textureRight, textureBackRight, textureBack, textureBackLeft, textureLeft, textureFrontLeft);
	}

	public E3DBillboardSprite8Direction(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, String textureFront, String textureFrontRight, String textureRight, String textureBackRight, String textureBack, String textureBackLeft, String textureLeft, String textureFrontLeft, E3DBlendMode blendMode)
	{
		super(engine, position, forward, up, size, textureFront, blendMode);
		setTextures(textureFront, textureFrontRight, textureRight, textureBackRight, textureBack, textureBackLeft, textureLeft, textureFrontLeft);
	}

	public E3DBillboardSprite8Direction(E3DEngine engine, E3DVector3F position, double size, E3DAnimatedTextureFrame[] frontFrames, int animationLoopsFront, E3DAnimatedTextureFrame[] frontRightFrames, int animationLoopsFrontRight, E3DAnimatedTextureFrame[] rightFrames, int animationLoopsRight, E3DAnimatedTextureFrame[] backRightFrames, int animationLoopsBackRight, E3DAnimatedTextureFrame[] backFrames, int animationLoopsBack, E3DAnimatedTextureFrame[] backLeftFrames, int animationLoopsBackLeft, E3DAnimatedTextureFrame[] leftFrames, int animationLoopsLeft, E3DAnimatedTextureFrame[] frontLeftFrames, int animationLoopsFrontLeft)
	{
		this(engine, position, size, frontFrames, animationLoopsFront, frontRightFrames, animationLoopsFrontRight, rightFrames, animationLoopsRight, backRightFrames, animationLoopsBackRight, backFrames, animationLoopsBack, backLeftFrames, animationLoopsBackLeft, leftFrames, animationLoopsLeft, frontLeftFrames, animationLoopsFrontLeft, new E3DBlendMode(engine, DEFAULT_BLEND_MODE));	
	}
	
	public E3DBillboardSprite8Direction(E3DEngine engine, E3DVector3F position, double size, E3DAnimatedTextureFrame[] frontFrames, int animationLoopsFront, E3DAnimatedTextureFrame[] frontRightFrames, int animationLoopsFrontRight, E3DAnimatedTextureFrame[] rightFrames, int animationLoopsRight, E3DAnimatedTextureFrame[] backRightFrames, int animationLoopsBackRight, E3DAnimatedTextureFrame[] backFrames, int animationLoopsBack, E3DAnimatedTextureFrame[] backLeftFrames, int animationLoopsBackLeft, E3DAnimatedTextureFrame[] leftFrames, int animationLoopsLeft, E3DAnimatedTextureFrame[] frontLeftFrames, int animationLoopsFrontLeft, E3DBlendMode blendMode)
	{
		super(engine, position, size, frontFrames, animationLoopsFront, blendMode);
		setTextures(frontFrames, animationLoopsFront, frontRightFrames, animationLoopsFrontRight, rightFrames, animationLoopsRight, backRightFrames, animationLoopsBackRight, backFrames, animationLoopsBack, backLeftFrames, animationLoopsBackLeft, leftFrames, animationLoopsLeft, frontLeftFrames, animationLoopsFrontLeft);
	}

	public E3DBillboardSprite8Direction(E3DEngine engine, E3DVector3F position, E3DVector3F forward, E3DVector3F up, double size, E3DAnimatedTextureFrame[] frontFrames, int animationLoopsFront, E3DAnimatedTextureFrame[] frontRightFrames, int animationLoopsFrontRight, E3DAnimatedTextureFrame[] rightFrames, int animationLoopsRight, E3DAnimatedTextureFrame[] backRightFrames, int animationLoopsBackRight, E3DAnimatedTextureFrame[] backFrames, int animationLoopsBack, E3DAnimatedTextureFrame[] backLeftFrames, int animationLoopsBackLeft, E3DAnimatedTextureFrame[] leftFrames, int animationLoopsLeft, E3DAnimatedTextureFrame[] frontLeftFrames, int animationLoopsFrontLeft, E3DBlendMode blendMode)
	{
		super(engine, position, forward, up, size, frontFrames, animationLoopsFront, blendMode);
		setTextures(frontFrames, animationLoopsFront, frontRightFrames, animationLoopsFrontRight, rightFrames, animationLoopsRight, backRightFrames, animationLoopsBackRight, backFrames, animationLoopsBack, backLeftFrames, animationLoopsBackLeft, leftFrames, animationLoopsLeft, frontLeftFrames, animationLoopsFrontLeft);
	}
	
	public void setTextures(String front, String frontRight, String right, String backRight, String back, String backLeft, String left, String frontLeft)
	{
		E3DAnimatedTexture frontTex = new E3DAnimatedTexture(getEngine(), new String[]{front}, new double[]{-1}, -1);
		E3DAnimatedTexture frontRightTex = new E3DAnimatedTexture(getEngine(), new String[]{frontLeft}, new double[]{-1}, -1);
		E3DAnimatedTexture rightTex = new E3DAnimatedTexture(getEngine(), new String[]{left}, new double[]{-1}, -1);
		E3DAnimatedTexture backRightTex = new E3DAnimatedTexture(getEngine(), new String[]{backLeft}, new double[]{-1}, -1);
		E3DAnimatedTexture backTex = new E3DAnimatedTexture(getEngine(), new String[]{back}, new double[]{-1}, -1);
		E3DAnimatedTexture backLeftTex = new E3DAnimatedTexture(getEngine(), new String[]{backRight}, new double[]{-1}, -1);
		E3DAnimatedTexture leftTex = new E3DAnimatedTexture(getEngine(), new String[]{right}, new double[]{-1}, -1);
		E3DAnimatedTexture frontLeftTex = new E3DAnimatedTexture(getEngine(), new String[]{frontRight}, new double[]{-1}, -1);
		
		setTextures(frontTex, frontRightTex, rightTex, backRightTex, backTex, backLeftTex, leftTex, frontLeftTex);
	}
	
	public void setTextures(E3DAnimatedTextureFrame[] frontFrames, int animationLoopsFront, E3DAnimatedTextureFrame[] frontRightFrames, int animationLoopsFrontRight, E3DAnimatedTextureFrame[] rightFrames, int animationLoopsRight, E3DAnimatedTextureFrame[] backRightFrames, int animationLoopsBackRight, E3DAnimatedTextureFrame[] backFrames, int animationLoopsBack, E3DAnimatedTextureFrame[] backLeftFrames, int animationLoopsBackLeft, E3DAnimatedTextureFrame[] leftFrames, int animationLoopsLeft, E3DAnimatedTextureFrame[] frontLeftFrames, int animationLoopsFrontLeft)
	{
		E3DAnimatedTexture front, frontRight, right, backRight, back, backLeft, left, frontLeft;
		front = new E3DAnimatedTexture(getEngine(), frontFrames, animationLoopsFront);
		frontRight = new E3DAnimatedTexture(getEngine(), frontRightFrames, animationLoopsFrontRight);
		right = new E3DAnimatedTexture(getEngine(), rightFrames, animationLoopsRight);
		backRight = new E3DAnimatedTexture(getEngine(), backRightFrames, animationLoopsBackRight);
		back = new E3DAnimatedTexture(getEngine(), backFrames, animationLoopsBack);
		backLeft = new E3DAnimatedTexture(getEngine(), backLeftFrames, animationLoopsBackLeft);
		left = new E3DAnimatedTexture(getEngine(), leftFrames, animationLoopsLeft);
		frontLeft = new E3DAnimatedTexture(getEngine(), frontLeftFrames, animationLoopsFrontLeft);
		
		setTextures(front, frontRight, right, backRight, back, backLeft, left, frontLeft);
	}
	
	public void setTextures(E3DAnimatedTexture front, E3DAnimatedTexture frontRight, E3DAnimatedTexture right, E3DAnimatedTexture backRight, E3DAnimatedTexture back, E3DAnimatedTexture backLeft, E3DAnimatedTexture left, E3DAnimatedTexture frontLeft)
	{
		animatedTextures[0] = front;
		animatedTextures[1] = frontRight;
		animatedTextures[2] = right;
		animatedTextures[3] = backRight;
		animatedTextures[4] = back;
		animatedTextures[5] = backLeft;
		animatedTextures[6] = left;
		animatedTextures[7] = frontLeft;
	}
	
	public boolean update(E3DActor lookAtActor, double lastFrameTimeSeconds) 
	{
		boolean success = super.update(lookAtActor, lastFrameTimeSeconds);
		
		E3DVector3F spriteForward = getMovementOrientation().getForward();
		E3DVector3F spritePosition = getMovementOrientation().getPosition();
		E3DVector3F actorPosition = lookAtActor.getOrientation().getPosition();
		E3DVector3F spriteToActor = actorPosition.subtract(spritePosition);

		if(spriteToActor.closeTo(0,0,0))
			return success;
		
		spriteToActor.normaliseEqual();

		//Get angle between the sprites forward and where we are
		
		//Angle is always between 0 and PI, need to use dotProduct to determine if its between >PI and 2PI
		double angle = spriteForward.angleBetweenRads(spriteToActor);

		angle =  spriteForward.getDirectionOfAngleBetween(spriteToActor);
//		int sign = spriteForward.getDirectionOfAngleBetween(spriteToActor);
//		
//		if(sign < 0)
//			angle = -angle;

		if(angle >= -.392699 && angle < .392699 )
		{
			//front
			System.out.println("Front");
			setAnimatedTexture(getFrontTexture());
		}
		else if(angle >= .392699 && angle < 1.178097)
		{
			//front right
			System.out.println("Front Right");
			setAnimatedTexture(getFrontRightTexture());
		}
		else if(angle >= 1.178097 && angle < 1.963495)
		{
			System.out.println("Right");
			//right
			setAnimatedTexture(getRightTexture());
		}
		else if(angle >= 1.963495 && angle < 2.748893)
		{
			//back right
			System.out.println("Back Right");
			setAnimatedTexture(getBackRightTexture());

		}
		else if(angle >= 2.748893 || angle < -2.748893)
		{
			//back
			System.out.println("Back");

			setAnimatedTexture(getBackTexture());

		}
		else if(angle >= -2.748893 && angle < -1.963495)
		{
			//back left
			System.out.println("Back left");

			setAnimatedTexture(getBackLeftTexture());

		}
		else if(angle >= -1.963495 && angle < -1.178097)
		{
			//left
			System.out.println("Left");

			setAnimatedTexture(getLeftTexture());
		}
		else if(angle >= -1.178097 && angle < -.392699)
		{
			//front left
			System.out.println("Front left");

			setAnimatedTexture(getFrontLeftTexture());
		}
		else
			getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_WARNING, "Invalid angle for billboard 8 direction sprite " + angle);
		
		return success;
	}
	
	public E3DAnimatedTexture getFrontTexture()
	{
		return animatedTextures[0];
	}

	public E3DAnimatedTexture getFrontRightTexture()
	{
		return animatedTextures[1];
	}
	public E3DAnimatedTexture getRightTexture()
	{
		return animatedTextures[2];
	}
	public E3DAnimatedTexture getBackRightTexture()
	{
		return animatedTextures[3];
	}
	public E3DAnimatedTexture getBackTexture()
	{
		return animatedTextures[4];
	}
	public E3DAnimatedTexture getBackLeftTexture()
	{
		return animatedTextures[5];
	}
	public E3DAnimatedTexture getLeftTexture()
	{
		return animatedTextures[6];
	}
	public E3DAnimatedTexture getFrontLeftTexture()
	{
		return animatedTextures[7];
	}
}	
