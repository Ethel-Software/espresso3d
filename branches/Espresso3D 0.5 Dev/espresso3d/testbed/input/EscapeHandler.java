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
import espresso3d.testbed.ITestBed;
import espresso3d.testbed.TestParticleSystems;
/**
 * @author espresso3d
 *
 * An input callback class for escape and other game logic (could be better named..)
 */
public class EscapeHandler implements IE3DInputCallback  //be sure to implement IE3DInputCallback for this to work
{
	private E3DEngine engine;
	private ITestBed testBed;
	
	public EscapeHandler(E3DEngine engine, ITestBed testBed)
	{
		setEngine(engine);
		setTestBed(testBed);
	}
	
	/**
	 * This is called whenever the keys bound to this class are pressed.
	 * Execute is a common procedure for all input callbacks
	 */
	public void onKeyboardInput(ArrayList keyCodes)
	{
	    Integer keyPressed;
	    //A list of all the keys pressed that are bound to this class are passed in, so we looop through them all	    
		for(int i=0; i<keyCodes.size(); i++) 
		{
		    keyPressed = (Integer)keyCodes.get(i);
		    
			if(E3DInputHandler.KEY_ESCAPE.equals(keyPressed)) //If the key is escape
			{
				engine.setRunning(false); //Stop the engine (our loop must be checking for this to be false & when it is, stop the game loop)
			}
			
			//R resets the game (Scores and ball)
            else if(E3DInputHandler.KEY_R.equals(keyPressed))
			{
			    testBed.getEngine().getWindow().getViewport("viewport1").getCameraActor().setPosition(new E3DVector3F(0.0, -20.0, 0.0));
//			    pongClone.getEngine().getViewport("viewport1").getCameraActor().rotate(-E3DConstants.PI/2.0, pongClone.getEngine().getViewport("viewport1").getCameraActor().getLeft());

/*			    pongClone.getEngine().getViewport("viewport1").getCameraActor().translate(new E3DVector3F(0, -2, 0));
			    
			    try{
			        pongClone.resetGame();
			    }catch(Exception e){
			        e.printStackTrace();
			    }
	*/		    
			}
            else if(E3DInputHandler.KEY_0.equals(keyPressed))
            {
                System.out.println("Stopping sound");
                if(getTestBed().getFootsteps() != null)
                	getEngine().getSoundHandler().stopSound(getTestBed().getFootsteps());
            }
            else if(E3DInputHandler.KEY_8.equals(keyPressed))
            {
                System.out.println("Unpausing sound");
                getEngine().getSoundHandler().unpauseSound(getTestBed().getFootsteps());
            }
            else if(E3DInputHandler.KEY_9.equals(keyPressed))
            {
                System.out.println("Pausing sound");
                getEngine().getSoundHandler().pauseSound(getTestBed().getFootsteps());
            }
            else if(E3DInputHandler.KEY_7.equals(keyPressed))
            {
                System.out.println("Rewinding sound");
                getEngine().getSoundHandler().rewindSound(getTestBed().getFootsteps());
            }
            if(E3DInputHandler.KEY_P.equals(keyPressed))
            {
                if(getTestBed() instanceof TestParticleSystems)
                    ((TestParticleSystems)getTestBed()).pause = true;//!((TestParticleSystems)getTestBed()).pause;
            }
		}
	}
	
	public void onMouseButtonInput(ArrayList keyCodes) 
	{
	    Integer keyPressed;
		for(int i=0; i<keyCodes.size(); i++) 
		{
		    keyPressed = (Integer)keyCodes.get(i);

		    if(E3DInputHandler.MOUSE_BUTTON_MIDDLE.equals(keyPressed))
		    {
				engine.setRunning(false); //Stop the engine (our loop must be checking for this to be false & when it is, stop the game loop)
		    }
		}
	}	
	
	public void onMouseMovementInput(int changeInX, int changeInY, int changeInScrollWheel) 
	{

	
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
    public ITestBed getTestBed() {
        return testBed;
    }
    public void setTestBed(ITestBed testBed) {
        this.testBed = testBed;
    }
}
