/*
 * Created on Jul 24, 2004
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
package espresso3d.testbed.input;

import java.util.ArrayList;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.input.E3DInputHandler;
import espresso3d.engine.input.base.IE3DInputCallback;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * @author espresso3d
 *
 * A callback class for movement input operations.
 */
public class MovementHandler implements IE3DInputCallback {
	private E3DEngine engine;
	private E3DWorld world;
	
	public MovementHandler(E3DEngine engine, E3DWorld world){
		setEngine(engine);
		setWorld(world);
	}
	
	/**
	 * This is called whenever the keys bound to this class are pressed.
	 * Execute is a common procedure for all input callbacks
	 */
	public void onKeyboardInput(ArrayList keyCodes)
	{
		E3DWorld curWorld = world;
		
		//These are the two paddles in the pong game.  Grab them now.
	//  	PaddleActor paddleActor1 = (PaddleActor)curWorld.getActor("PADDLE1");
	 // 	PaddleActor paddleActor2 = (PaddleActor)curWorld.getActor("PADDLE2");

	  	//These will the the translation vectors for the paddles that will be set depending on what keys are depressed
	  	E3DVector3F translationVec1 = new E3DVector3F();
	  	E3DVector3F translationVec2 = new E3DVector3F();

	  	E3DActor cameraActor = curWorld.getActor("camera");
	  	
	  	//Loop through all the keys that are pressed (and bound to this class) checking
	  	// each key and taking the appropriate action
	  	for(int i=0; cameraActor != null && keyCodes != null && i<keyCodes.size(); i++)
	  	{
	  		//The actor is translated by an amount that is scaled by the speed the engine is running.
	  		//This ensures that if the engine is running fast or slow (slow computer, fast computer, etc),
	  		// the paddles still move the same amount in a given unit of time
	  		
	  		
	  		//Left and right move paddle1 left or right.
            double speedScalar = 0;
            if(engine.getFpsTimer().getLastUpdateTimeSeconds() > 0)
                speedScalar = 1.0 / engine.getFpsTimer().getLastUpdateTimeSeconds();
            
			if(E3DInputHandler.KEY_D.equals(keyCodes.get(i)))
				translationVec1.addEqual((cameraActor.getOrientation().getLeft().scale(-1).scale(speedScalar)));
			if(E3DInputHandler.KEY_A.equals(keyCodes.get(i)))
				translationVec1.addEqual(cameraActor.getOrientation().getLeft().scale(1).scale(speedScalar));
			if(E3DInputHandler.KEY_W.equals(keyCodes.get(i)))
				cameraActor.rotate(0.01, cameraActor.getOrientation().getLeft().scale(speedScalar));
			if(E3DInputHandler.KEY_S.equals(keyCodes.get(i)))
				cameraActor.rotate(-0.01, cameraActor.getOrientation().getLeft().scale(speedScalar));

			if(E3DInputHandler.KEY_Q.equals(keyCodes.get(i)))
				translationVec1.addEqual(cameraActor.getOrientation().getUp().scale(speedScalar));
			if(E3DInputHandler.KEY_Z.equals(keyCodes.get(i)))
				translationVec1.addEqual(cameraActor.getOrientation().getUp().scale(-1).scale(speedScalar));

			//A and D move paddle2 left or right
			if(E3DInputHandler.KEY_UP.equals(keyCodes.get(i)))
				translationVec2.addEqual(cameraActor.getOrientation().getForward().scale(1).scale(speedScalar));
			if(E3DInputHandler.KEY_DOWN.equals(keyCodes.get(i)))
				translationVec2.addEqual(cameraActor.getOrientation().getForward().scale(-1).scale(speedScalar));
			if(E3DInputHandler.KEY_LEFT.equals(keyCodes.get(i)))
			    cameraActor.rotate(0.01, cameraActor.getOrientation().getUp().scale(speedScalar));
			if(E3DInputHandler.KEY_RIGHT.equals(keyCodes.get(i)))
			    cameraActor.rotate(-0.01, cameraActor.getOrientation().getUp().scale(speedScalar));
			if(E3DInputHandler.KEY_J.equals(keyCodes.get(i)))
			    cameraActor.rotate(-0.01, cameraActor.getOrientation().getForward().scale(speedScalar));
			if(E3DInputHandler.KEY_L.equals(keyCodes.get(i)))
			    cameraActor.rotate(0.01, cameraActor.getOrientation().getForward().scale(speedScalar));
			
	  	}
	  	
//	  	E3DLight light = (E3DLight)curWorld.getLightMap().get("light1");
//	  	light.translate(translationVec1);
//	  	light.translate(translationVec2);
//	  	paddleActor1.move(translationVec1); //Move paddle1 however much it needs to move
//	  	paddleActor2.move(translationVec2); //Move paddle2 however much it needs to move
        cameraActor.translate(translationVec1);
	  	cameraActor.translate(translationVec2);
	  	
	}

	/* (non-Javadoc)
     * @see espresso3d.engine.input.base.IE3DInputCallback#onMouseButtonInput(java.util.ArrayList)
     */
    public void onMouseButtonInput(ArrayList keyCodes) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see espresso3d.engine.input.base.IE3DInputCallback#onMouseMovementInput(int, int)
     */
    public void onMouseMovementInput(int changeInX, int changeInY, int changeInScrollWheel) {
        // TODO Auto-generated method stub

    }
	/**
	 * @return Returns the engine.
	 */
	public E3DEngine getEngine() {
		return engine;
	}
	/**
	 * @param engine The engine to set.
	 */
	public void setEngine(E3DEngine engine) {
		this.engine = engine;
	}
	/**
	 * @return Returns the world.
	 */
	public E3DWorld getWorld() {
		return world;
	}
	/**
	 * @param world The world to set.
	 */
	public void setWorld(E3DWorld world) {
		this.world = world;
	}
}
