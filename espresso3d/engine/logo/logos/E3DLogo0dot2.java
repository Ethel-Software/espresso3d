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
import espresso3d.engine.logo.actors.E3DHighResLogoActor;
import espresso3d.engine.logo.base.IE3DLogo;
import espresso3d.engine.logo.particlesystems.BluePlasmaParticleFountain;
import espresso3d.engine.logo.particlesystems.RedPlasmaParticleFountain;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DCameraActor;
import espresso3d.engine.world.sector.light.E3DLight;
/**
 * @author Curt
 *
 * The logo animation created for version 0.2
 */
public final class E3DLogo0dot2 implements IE3DLogo
{
	public void displayEngineLogo(E3DEngine engine) throws Exception
	{
		E3DWorld logoWorld = new E3DWorld(engine, "logoWorld");
		E3DSector logoSector = new E3DSector(engine, "logoSector");
		
		logoWorld.addSector(logoSector);
		
		E3DCameraActor cameraActor = new E3DCameraActor(engine, logoWorld, "camera1");
		cameraActor.rotate(Math.toRadians(90), new E3DVector3F(1.0, 0.0, 0.0));
		cameraActor.rotate(Math.toRadians(-180), new E3DVector3F(0.0, 1.0, 0.0));
		cameraActor.setPosition(new E3DVector3F(0.0, 50.0, 0.0));
		
		E3DHighResLogoActor logoActor = new E3DHighResLogoActor(engine, logoWorld, true);
		logoActor.scale(20);
		
		engine.addWorld(logoWorld);

		E3DViewport viewport = new E3DViewport(engine, 0, 0, engine.getWindow().getWidth(), engine.getWindow().getHeight(), "logoViewport");
		viewport.setWorld(logoWorld);
		
		engine.getWindow().addViewport(viewport);
		
		logoSector.addActor(logoActor);
		logoSector.addActor(cameraActor);
		
		viewport.setCameraActor(cameraActor); //Set the actor the viewport uses to render from
		
		logoActor.setPosition(new E3DVector3F(0, 0, 0));

		BluePlasmaParticleFountain blueFountain = new BluePlasmaParticleFountain(engine, logoSector, new E3DVector3F(0, 0, 1.5));
		blueFountain.setPosition(new E3DVector3F(-14, 5, 9));
		blueFountain.rotate(Math.toRadians(-90), new E3DVector3F(1, 0, 0));
		logoSector.addParticleSystem(blueFountain);
		
		RedPlasmaParticleFountain redFountain = new RedPlasmaParticleFountain(engine, logoSector, new E3DVector3F(0,0,1.5));
		redFountain.setPosition(new E3DVector3F(14, 5, 9));
		redFountain.rotate(Math.toRadians(-90), new E3DVector3F(1, 0, 0));
		logoSector.addParticleSystem(redFountain);
		
		
		//Scroll the logo to the center while its spinning
		double time = 0.0;
		boolean startLastPiece = false;
//		boolean hasParticles = true;

		E3DLight lightEnd = new E3DLight(engine, "lightEnd");
		lightEnd.setBrightness(0);
		lightEnd.setColor(new E3DVector4F(1, 1, 1, 1));
		lightEnd.setFalloff(200);

		boolean startFadeIn = false;
		//while(time < 2)
		while(time < 5)
		{
		    
		    //When their done shooting, time for 1 second
	    	if(redFountain.getParticleList().size() <= 0 && 
	    		blueFountain.getParticleList().size() <= 0 && startLastPiece == false)
	    	{
	    		startLastPiece = true;
	  
		        logoSector.removeParticleSystem(blueFountain);
		        logoSector.removeParticleSystem(redFountain);
//		        hasParticles = false;
		        
	    	}
	
	    	if(time > 1.5)
	    	{
		        logoSector.addLight(lightEnd);
	    	    startFadeIn = true;
	    	}
	    	
	    	if(startFadeIn)
	    		lightEnd.setBrightness(((time - 1.5) / 3) * 175); //scale it up until time is out
	    	
	    	if(engine.getFpsTimer().getLastUpdateTimeSeconds() != 0)
				time += engine.getFpsTimer().getLastUpdateTimeSeconds();

	    	engine.render();
		}
		//Clean the engine structures up
		engine.removeAllWorlds();
		engine.getWindow().removeAllViewports();
		engine.getFpsTimer().resetTimer();
	}

}
