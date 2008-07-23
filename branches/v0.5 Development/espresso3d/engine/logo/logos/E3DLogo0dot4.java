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
import espresso3d.engine.logo.actors.E3DHighResLogoActor;
import espresso3d.engine.logo.actors.E3DReceiverActor;
import espresso3d.engine.logo.actors.E3DSpeakerActor;
import espresso3d.engine.logo.base.IE3DLogo;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.sound.exceptions.E3DSoundOutOfSourcesException;
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
public final class E3DLogo0dot4 implements IE3DLogo
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
		
		//Load actors
        E3DHighResLogoActor logoActor = new E3DHighResLogoActor(engine, logoWorld, true);
		logoActor.scale(20);
        logoActor.setPosition(new E3DVector3F(0, 0, 0));

        E3DSpeakerActor speakerLeftActor = new E3DSpeakerActor(engine, logoWorld, "speakerLeft");
        speakerLeftActor.scale(2);
        speakerLeftActor.rotate(E3DConstants.HALFPI, new E3DVector3F(-1.0, 0.0, 0.0)); //make it look at the Y axis
        speakerLeftActor.setPosition(new E3DVector3F(-18, 0, 14));
        
        E3DSpeakerActor speakerRightActor = new E3DSpeakerActor(engine, logoWorld, "speakerRight");
        speakerRightActor.scale(2);
        speakerRightActor.rotate(E3DConstants.HALFPI, new E3DVector3F(-1.0, 0.0, 0.0)); //make it look at the Y axis
        speakerRightActor.setPosition(new E3DVector3F(18, 0, 14));

        E3DReceiverActor receiverActor = new E3DReceiverActor(engine, logoWorld, "receiver");
        receiverActor.scale(2);
        receiverActor.rotate(E3DConstants.HALFPI, new E3DVector3F(-1.0, 0.0, 0.0)); //make it look at the Y axis
        receiverActor.setPosition(new E3DVector3F(0, 0, 14));
        
		engine.addWorld(logoWorld);

		E3DViewport viewport = new E3DViewport(engine, 0, 0, engine.getWindow().getWidth(), engine.getWindow().getHeight(), "logoViewport");
		viewport.setWorld(logoWorld);
		
		engine.getWindow().addViewport(viewport);
		
		logoSector.addActor(logoActor);
		//Add Actors to world
        logoSector.addActor(speakerLeftActor);
        logoSector.addActor(speakerRightActor);
        logoSector.addActor(receiverActor);
        logoSector.addActor(cameraActor);
		
		viewport.setCameraActor(cameraActor); //Set the actor the viewport uses to render from

        engine.getSoundHandler().loadSoundSet("/espresso3d/engine/logo/sounds/logosoundset.txt");
        
        //Add a light
        E3DLight dimLight1 = new E3DLight(engine, "dimLight1");
        dimLight1.setBrightness(35);
        dimLight1.setColor(new E3DVector4F(1, 1, 1, 1));
        dimLight1.setFalloff(10);
        dimLight1.setPosition(new E3DVector3F(-18, 7, 15));
        logoSector.addLight(dimLight1);

        E3DLight dimLight2 = new E3DLight(engine, "dimLight2");
        dimLight2.setBrightness(35);
        dimLight2.setColor(new E3DVector4F(1, 1, 1, 1));
        dimLight2.setFalloff(10);
        dimLight2.setPosition(new E3DVector3F(18, 7, 15));
        logoSector.addLight(dimLight2);
        
        E3DLight dimLight3 = new E3DLight(engine, "dimLight3");
        dimLight3.setBrightness(50);
        dimLight3.setColor(new E3DVector4F(1, 1, 1, 1));
        dimLight3.setFalloff(10.5);
        dimLight3.setPosition(new E3DVector3F(0, 0, 19));
        logoSector.addLight(dimLight3);
        
        E3DLight laserLight = new E3DLight(engine, "laserlight");
        laserLight.setBrightness(0);
        laserLight.setColor(new E3DVector4F(1, 0.0, 0.1, 1.0));
        laserLight.setFalloff(400);
        laserLight.setPosition(new E3DVector3F(-10, 10, 0));
        
        E3DLight explosionLight = new E3DLight(engine, "explosionlight");
        explosionLight.setBrightness(0);
        explosionLight.setColor(new E3DVector4F(1, 0.3, 0.1, 1.0));
        explosionLight.setFalloff(200);

        E3DLight fadeLight = new E3DLight(engine, "fadelight");
        fadeLight.setBrightness(100);
        fadeLight.setColor(new E3DVector4F(1, 1, 1, 1));
        fadeLight.setFalloff(200);
        
        
        E3DCameraActor soundLeftActor = new E3DCameraActor(engine, logoWorld, "soundLeftActor");
        soundLeftActor.setPosition(new E3DVector3F(-100, 50, 0)); //set to direct left of camera
        logoSector.addActor(soundLeftActor);
        
        E3DCameraActor soundRightActor = new E3DCameraActor(engine, logoWorld, "soundRightActor");
        soundRightActor.setPosition(new E3DVector3F(100, 50, 0)); //set to direct right of camera
        logoSector.addActor(soundRightActor);

        double time = 0.0;
		//while(time < 2)
        
        boolean monsterGrowl = false;
        int laserShotCount = 0;
        boolean explosion = false;
        boolean startedApplause = false;
        
        double laserBrightness = -1;
        boolean laserLightAdded = false;
        double explosionBrightness = -1;
        boolean explosionLightAdded = false;
        boolean fadeLightAdded = false;
        double fadeBrightness = -1;
        while(time < 5)
		{
            try
            {
                if(time >= 0.2 && !monsterGrowl)
                {
                    engine.getSoundHandler().play3DSound("MONSTERGROWL", soundRightActor, 0.8, 500.0, 1.0, 30.0, 1.0);
                    monsterGrowl = true;
                }
                
                if(laserBrightness > -1)
                {
                    laserBrightness -= engine.getFpsTimer().getLastUpdateTimeSeconds() * 100;
                    if(laserBrightness < 0)
                    {
                        laserLight.setBrightness(0);
                        if(laserShotCount > 2)
                            logoSector.removeLight(laserLight);
                    }
                    else
                        laserLight.setBrightness(laserBrightness);

                }

                if((time >= 1.2 && laserShotCount <= 0) ||
                   (time >= 1.5 && laserShotCount > 0 && laserShotCount <= 1) ||
                   (time >= 1.7 && laserShotCount > 1 && laserShotCount <= 2))
                {
                    engine.getSoundHandler().play3DSound("LASER", soundLeftActor, 1.0, 50.0, 1.0, 30.0, 1.0);
                    laserLight.setBrightness(125);
                    laserBrightness = 125;
                    if(!laserLightAdded)
                    {
                        logoSector.addLight(laserLight);
                        laserLightAdded = true;
                    }
                    laserShotCount ++;
                }
                else if(laserShotCount > 2)
                {
                    laserLight.setBrightness(0);
                    laserBrightness = -1;
                }


                if(explosionBrightness > -1)
                {
                    explosionBrightness -= engine.getFpsTimer().getLastUpdateTimeSeconds() * 100;
                    if(explosionBrightness < 0)
                    {
                        logoSector.removeLight(explosionLight);
                        explosionBrightness = -1;
                    }
                    else
                        explosionLight.setBrightness(explosionBrightness);
                }
                if(time >= 1.85 && !explosion)
                {
                    engine.getSoundHandler().play2DSound("EXPLOSION", 0.8, 3.0);
                    explosionLight.setBrightness(200);
                    if(!explosionLightAdded)
                    {        
                        logoSector.addLight(explosionLight);
                        explosionLightAdded = true;
                    }
                    explosionBrightness = 200;
                    explosion = true;
                }
                
                
                if(fadeBrightness > -1)
                {
                    fadeBrightness += engine.getFpsTimer().getLastUpdateTimeSeconds() * 75;
                    if(fadeBrightness > 150)
                    {
                        fadeLight.setBrightness(150);
                        fadeBrightness = -1;
                    }
                    else
                        fadeLight.setBrightness(fadeBrightness);
                }
                if(time > 2.3 && !startedApplause)
                {
                    engine.getSoundHandler().play2DSound("APPLAUSE", 1.0, 2.5);
                    if(!fadeLightAdded)
                    {
                        fadeBrightness = 0.0;
                        fadeLight.setBrightness(0);
                        logoSector.addLight(fadeLight);
                        fadeLightAdded = true;
                    }
                    startedApplause = true;
                }

                if(time > 4.5)
                {
                    logoSector.removeActor(speakerLeftActor);
                    logoSector.removeActor(speakerRightActor);
                    logoSector.removeActor(receiverActor);
                }
                
            }
            catch(E3DSoundOutOfSourcesException e){
                System.out.println("Exception");
            }

                
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
