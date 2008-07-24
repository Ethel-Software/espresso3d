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
package espresso3d.engine.logo.logos;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.logo.actors.E3DLowResLogoActor;
import espresso3d.engine.logo.base.IE3DLogo;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DCameraActor;
/**
 * @author Curt
 *
 * The logo animation created for version 0.1
 */
public final class E3DLogo0dot1 implements IE3DLogo
{
	public void displayEngineLogo(E3DEngine engine)
	{
		E3DWorld logoWorld = new E3DWorld(engine, "logoWorld");
		E3DSector logoSector = new E3DSector(engine, "logoSector");
		
		logoWorld.addSector(logoSector);
		
		E3DCameraActor cameraActor = new E3DCameraActor(engine, logoWorld, "camera1");
		cameraActor.rotate(Math.toRadians(-90), new E3DVector3F(1.0, 0.0, 0.0));
		cameraActor.rotate(Math.toRadians(180), new E3DVector3F(0.0, 1.0, 0.0));
		cameraActor.setPosition(new E3DVector3F(0.0, -50.0, 0.0));
		
		E3DLowResLogoActor logoActor = new E3DLowResLogoActor(engine, logoWorld, false);

		engine.addWorld(logoWorld);

		E3DViewport viewport = new E3DViewport(engine, 0, 0, engine.getWindow().getWidth(), engine.getWindow().getHeight(), "logoViewport");
		viewport.setWorld(logoWorld);
		
		engine.getWindow().addViewport(viewport);
		
		logoSector.addActor(logoActor);
		logoSector.addActor(cameraActor);
		
		viewport.setCameraActor(cameraActor); //Set the actor the viewport uses to render from
		
		logoActor.setPosition(new E3DVector3F(40, 0, 0));

		//Scroll the logo to the center while its spinning
		double totalRotAmt = 0.00;
		double time = 0.0;
		double transAmt = 0.0;
		while(time < 4)
		{
			if(engine.getFpsTimer().getLastUpdateTimeSeconds() != 0)
			{	
				transAmt =  logoActor.getOrientation().getPosition().getX() / ((2-time) * (1/engine.getFpsTimer().getLastUpdateTimeSeconds()));
					
				if(logoActor.getOrientation().getPosition().getX() - transAmt < 0)
					logoActor.translate(new E3DVector3F(-logoActor.getOrientation().getPosition().getX(), 0, 0));
				else if(logoActor.getOrientation().getPosition().getX() > 0)
					logoActor.translate(new E3DVector3F(-transAmt, 0, 0));

				
				if(totalRotAmt < E3DConstants.TWOPI)
				{
					double rotAmt = (E3DConstants.TWOPI - totalRotAmt) / ((2-time)*(1/engine.getFpsTimer().getLastUpdateTimeSeconds()));
					if(rotAmt + totalRotAmt > E3DConstants.TWOPI)
						rotAmt = E3DConstants.TWOPI - totalRotAmt;
					
					logoActor.rotate(rotAmt, new E3DVector3F(0, 0, 1));

					totalRotAmt += rotAmt;
				}
				time += engine.getFpsTimer().getLastUpdateTimeSeconds();

			}
			engine.render();
		}
		//Clean the engine structures up
		engine.removeAllWorlds();
		engine.getWindow().removeAllViewports();
		engine.getFpsTimer().resetTimer();
	}

}
