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
import espresso3d.engine.logo.font.E3DLogoVerdanaFont;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DCameraActor;
/**
 * @author Curt
 *
 * The logo animation created for version 0.1
 */
public final class E3DLogo0dot3 implements IE3DLogo
{
	public void displayEngineLogo(E3DEngine engine)
	{
		E3DWorld logoWorld = new E3DWorld(engine, "logoWorld");
		E3DSector logoSector = new E3DSector(engine, "logoSector");
		
		logoWorld.addSector(logoSector);
		
		E3DCameraActor cameraActorFront = new E3DCameraActor(engine, logoWorld, "cameraFront");
		cameraActorFront.rotate(Math.toRadians(-90), new E3DVector3F(1.0, 0.0, 0.0));
		cameraActorFront.rotate(Math.toRadians(180), new E3DVector3F(0.0, 1.0, 0.0));
		cameraActorFront.setPosition(new E3DVector3F(0.0, -50.0, 0.0));
		
		E3DCameraActor cameraActorTop = new E3DCameraActor(engine, logoWorld, "cameraTop");
		cameraActorTop.rotate(Math.toRadians(180), new E3DVector3F(0.0, 1.0, 0.0));
		cameraActorTop.setPosition(new E3DVector3F(0.0, 0.0, 50.0));

		E3DCameraActor cameraActorSide = new E3DCameraActor(engine, logoWorld, "cameraSide");
		cameraActorSide.rotate(Math.toRadians(-90), new E3DVector3F(1.0, 0.0, 0.0));
		cameraActorSide.rotate(Math.toRadians(-90), new E3DVector3F(0.0, 0.0, 1.0));
		cameraActorSide.setPosition(new E3DVector3F(-50.0, 0.0, 0.0));
		
		E3DLowResLogoActor logoActor = new E3DLowResLogoActor(engine, logoWorld, false);
		logoActor.setPosition(new E3DVector3F(40, 0, 0));
		logoActor.setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_WIREFRAME));
		engine.addWorld(logoWorld);

		int halfWidth = engine.getWindow().getWidth() / 2;
		int halfHeight = engine.getWindow().getHeight() / 2;
		
		//Lower right (wireframe, top)
		E3DViewport viewportLowerRight = new E3DViewport(engine, halfWidth, 0, halfWidth, halfHeight, "logoViewportLowerRight");
		viewportLowerRight.setWorld(logoWorld);
		viewportLowerRight.setCameraActor(cameraActorTop); //Set the actor the viewport uses to render from
		viewportLowerRight.switchToOrthographicMode(-20 * (viewportLowerRight.getWidth() / ((double)viewportLowerRight.getHeight())), 20 * (viewportLowerRight.getWidth() / ((double)viewportLowerRight.getHeight())), 20, -20, 0.1, 1000);
		engine.getWindow().addViewport(viewportLowerRight);

		//Lower left (wireframe, front view)
		E3DViewport viewportLowerLeft = new E3DViewport(engine, 0, 0, halfWidth, halfHeight, "logoViewportLowerLeft");
		viewportLowerLeft.setWorld(logoWorld);
		viewportLowerLeft.setCameraActor(cameraActorFront); //Set the actor the viewport uses to render from
		viewportLowerLeft.switchToOrthographicMode(-20 * (viewportLowerLeft.getWidth() / ((double)viewportLowerLeft.getHeight())), 20 * (viewportLowerLeft.getWidth() / ((double)viewportLowerLeft.getHeight())), 20, -20, 0.1, 1000);
		engine.getWindow().addViewport(viewportLowerLeft);

		//Upper Left (textured, front)
		E3DViewport viewportUpperLeft = new E3DViewport(engine, 0, halfHeight, halfWidth, halfHeight, "logoViewportUpperLeft");
		viewportUpperLeft.setWorld(logoWorld);
		viewportUpperLeft.setCameraActor(cameraActorFront); //Set the actor the viewport uses to render from
		viewportUpperLeft.setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED));
		engine.getWindow().addViewport(viewportUpperLeft);

		//Upper right (wireframe, side)
		E3DViewport viewportUpperRight = new E3DViewport(engine, halfWidth, halfHeight, halfWidth, halfHeight, "logoViewportUpperRight");
		viewportUpperRight.setWorld(logoWorld);
		viewportUpperRight.setCameraActor(cameraActorSide); //Set the actor the viewport uses to render from
		viewportUpperRight.switchToOrthographicMode(-20 * (viewportUpperRight.getWidth() / ((double)viewportUpperRight.getHeight())), 20 * (viewportUpperRight.getWidth() / ((double)viewportUpperRight.getHeight())), 20, -20, 0.1, 1000);
		engine.getWindow().addViewport(viewportUpperRight);

		logoSector.addActor(logoActor);
		logoSector.addActor(cameraActorFront);
		logoSector.addActor(cameraActorSide);
		logoSector.addActor(cameraActorTop);

		//Add font to all viewports
		E3DLogoVerdanaFont verdanaFont = new E3DLogoVerdanaFont(engine);
		engine.getWindow().addFont(verdanaFont);
		
		//Add the label text
		viewportLowerRight.getViewportPrinter().printToViewport("Top", "VerdanaBold", 12, new E3DVector4F(0.4, 0.4, 0.4, 1.0),
		        new E3DVector2I(((int)(viewportLowerRight.getWidth() - (engine.getWindow().getWidth() / 10.0))),  
		                		(int)(engine.getWindow().getHeight() / 20.0)), 4.0);
		viewportLowerLeft.getViewportPrinter().printToViewport("Front", "VerdanaBold", 12, new E3DVector4F(0.4, 0.4, 0.4, 1.0), 
		        new E3DVector2I(((int)(viewportLowerRight.getWidth() - (engine.getWindow().getWidth() / 10.0))), 
		                		(int)(engine.getWindow().getHeight() / 20.0)), 4.0);
		viewportUpperLeft.getViewportPrinter().printToViewport("3D", "VerdanaBold", 12, new E3DVector4F(0.4, 0.4, 0.4, 1.0),  
		        new E3DVector2I(((int)(viewportLowerRight.getWidth() -  (engine.getWindow().getWidth() / 10.0))), 
		                		(int)(engine.getWindow().getHeight() / 20.0)), 3.5);
		viewportUpperRight.getViewportPrinter().printToViewport("Side", "VerdanaBold", 12, new E3DVector4F(0.4, 0.4, 0.4, 1.0), 
		        new E3DVector2I(((int)(viewportLowerRight.getWidth() -  (engine.getWindow().getWidth() / 10.0))),  
		                		(int)(engine.getWindow().getHeight()/ 20.0)), 4.0);
		
		//Scroll the logo to the center while its spinning
		double totalRotAmt = 0.00;
		double time = 0.0;
		double transAmt = 0.0;
		boolean removedViewports = false;
		while(time < 5)
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
			
			if(time > 3.5 && time <= 4)
			{
                int newWidth = viewportUpperLeft.getWidth() + 
                        ((int)(((engine.getWindow().getWidth() - viewportUpperLeft.getWidth()) / (4 - time))  * engine.getFpsTimer().getLastUpdateTimeSeconds()));
                if(newWidth < 0 || newWidth > engine.getWindow().getWidth())
                    newWidth = engine.getWindow().getWidth();
                viewportUpperLeft.setWidth(newWidth);
                
                int newHeight = viewportUpperLeft.getHeight() +                             
                    ((int)(((engine.getWindow().getHeight() - viewportUpperLeft.getHeight()) / (4 - time))  * engine.getFpsTimer().getLastUpdateTimeSeconds()));
                if(newHeight < 0 || newHeight > engine.getWindow().getHeight())
                    newHeight = engine.getWindow().getHeight();
			    viewportUpperLeft.setHeight(newHeight);
                
                int newY = viewportUpperLeft.getY() +
                        ((int)(((0 - viewportUpperLeft.getY()) / (4 - time))  * engine.getFpsTimer().getLastUpdateTimeSeconds()));
                if(newY < 0 || newY > engine.getWindow().getHeight())
                    newY = 0;
			    viewportUpperLeft.setY(newY);
			}
            else if(time > 4 && !removedViewports)
			{
			    engine.getWindow().removeViewport("logoViewportUpperRight");
			    engine.getWindow().removeViewport("logoViewportLowerRight");
			    engine.getWindow().removeViewport("logoViewportLowerLeft");

			    viewportUpperLeft.setWidth(engine.getWindow().getWidth());
			    viewportUpperLeft.setHeight(engine.getWindow().getHeight());
			    viewportUpperLeft.setY(0);
			    
			    removedViewports = true;
			}
			engine.render();
		}
		//Clean the engine structures up
		engine.removeAllWorlds();
		engine.getWindow().removeAllViewports();
		engine.getFpsTimer().resetTimer();
	}

}
