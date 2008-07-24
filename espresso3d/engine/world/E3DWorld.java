/*
 * Created on Jul 17, 2004
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
package espresso3d.engine.world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.exceptions.E3DInvalidFileFormatException;
import espresso3d.engine.exceptions.E3DMissingEngineException;
import espresso3d.engine.fileloaders.E3DImageLoader;
import espresso3d.engine.fileloaders.E3DMapLoader;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.renderer.texture.E3DTexture;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;
import espresso3d.engine.world.sky.E3DSkyObject;

/**
 * @author espresso3d
 *
 *  A world is the largest grouping for a portal engine.  The engine can have several worlds, but only
 * 1 world will be rendered at a time.
 */
public class E3DWorld extends E3DRenderable
{
	private String worldID;
	
	private HashMap sectorMap;
	private HashMap textureSet;
	private HashMap preloadedActorMap; //This is the map that contains the actor definitions.  It must be loaded for ACTOR tags to work in the mapfile
	private HashMap preloadedParticleSystemMap; //Map that contains particleSystem definitions.
	private E3DSkyObject skyObject; //the skyobject for the world;
	
	
	public E3DWorld(E3DEngine engine, String worldID)
	{
		super(engine);
		
		this.worldID = worldID;
		this.sectorMap = new HashMap();
		this.preloadedActorMap = new HashMap();
		this.preloadedParticleSystemMap = new HashMap();
		this.textureSet = new HashMap();
		this.skyObject = null;
	}

	protected void finalize() throws Throwable 
	{
		//Free up texture memory
		if(textureSet != null && !textureSet.isEmpty())
		{
			Iterator it = textureSet.entrySet().iterator();
			Map.Entry entry;
			E3DTexture texture;
			while(it.hasNext())
			{
				entry = (Map.Entry)it.next();
				texture = (E3DTexture)entry.getValue();
				removeTexture(texture);
			}
		}
		super.finalize();
	}
	
	/**
	 * This removes a texture from the texture set as well
	 * as unloads it from opengl.  Be careful with this.  Don't remove
	 * a texture from the world that's being used by an object in the world
	 * or it'll blow up.
	 * 
	 * @param texture
	 */
	public void removeTexture(E3DTexture texture)
	{
		if(textureSet.containsKey(texture.getTextureName()))
		{
			E3DImageLoader.unloadImageFromGL(texture.getGlTextureID());
			textureSet.remove(texture.getTextureName());
		}
	}

	/**
	 * Add a texture (loaded via the image loader: 1) loadImage 2) loadImageIntoGL 3) add texture
	 * This is typically unecessary and much slower than
	 * the automatic handling of pre-loaded texturesets
	 * defined in actor definitions and map (world) definitions.
	 * @param texture
	 */
	public void addTexture(E3DTexture texture)
	{
		if(!textureSet.containsKey(texture.getTextureName()))
			textureSet.put(texture.getTextureName(), texture);
	}
	
	/**
	 * Load a texture set into the world to be used by actors, worlds, particles, etc.
	 * This is usually only necessary if preloading of actors/particle systems is necessary.
	 * Otherwise, normal loading of actors and maps will load a texture set automatically.
	 * @param textureSetFileName
\	 * @throws E3DMissingEngineException
	 * @throws E3DInvalidFileFormatException
	 * @throws IOException
	 */
	public void loadTextureSet(String textureSetFileName) throws E3DMissingEngineException, E3DInvalidFileFormatException, IOException
	{
		if(getEngine() == null)
			throw new E3DMissingEngineException();
		
		E3DImageLoader.loadTextureSet(getEngine(), textureSetFileName, getTextureSet());
	}

	/**
	 * Load a map's geometry into the world
	 * @param mapFileName
	 * @param fromJar Set to true if the mapfile is packaged within a JAR file
	 * @throws E3DMissingEngineException
	 * @throws Exception
	 */
	public void loadWorld(String mapFileName) throws E3DMissingEngineException, Exception
	{
		if(getEngine() == null)
			throw new E3DMissingEngineException();
	
	    E3DMapLoader.loadMap(mapFileName, this);
	}
	
	/**
	 * Add a sector to this world with a unique name sectorID
	 * @param sectorID
	 * @param sector
	 */
	public void addSector(E3DSector sector)
	{
		sector.setEngine(getEngine());
		sector.setWorld(this);
		sectorMap.put(sector.getSectorID(), sector);
	}

	/**
	 * Remove a sector from this world 
	 * @param sectorID
	 * @return
	 */
	public void removeSector(String sectorID)
	{
		if(sectorMap.containsKey(sectorID))
		    sectorMap.remove(sectorID);
	}

	/**
	 * Remove all the sectors from the world
	 */
	public void removeAllSectors()
	{
		sectorMap.clear();
	}
	
	/**
	 * Get an actor that is in the world by its actorID.
	 * This will look in all sectors of the world for the actor.
	 * 
	 * @param actorID
	 * @return
	 */
	public E3DActor getActor(String actorID)
	{
        HashMap actorMap = getActorMap();
		if(actorMap.containsKey(actorID))
			return (E3DActor)actorMap.get(actorID);
		else
			return null;
	}
	
	
	/**
	 * Traverses all sectors in the world and generates a map of all the actors.  If there are actors
	 * in two different sectors that have the same actorID, the last encountered will be used with
	 * all previous actors with that id being overwritten in this map.
	 * 
	 * World's do not have a list already built of actors, so it has to be built on the fly
	 * and is therefore not the fastest way at getting to an actor.  This probably
	 * shouldn't be used if at all possible, and actors should be accessed from their parent
	 * sector that they are native to.
	 * @return Returns the actorMap of all E3DActor objects in all sectors of this world keyed off the String actorID
	 */
	public HashMap getActorMap() {
		if(sectorMap == null)
			return null;
		
		HashMap actorMap = new HashMap();
		Iterator it = sectorMap.entrySet().iterator();
		Map.Entry entry = null;
		E3DSector sector = null;
		
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			sector = (E3DSector)entry.getValue();
			
			actorMap.putAll(sector.getActorMap());
		}
		
		return actorMap;
	}

	/**
	 * @return Returns the sectorMap.
	 */
	public HashMap getSectorMap() {
		return sectorMap;
	}
	/**
	 * @param sectorMap The sectorMap to set.
	 */
	public void setSectorMap(HashMap sectorMap) {
		this.sectorMap = sectorMap;
	}
	
	/**
	 * Gets the sector in this world that was keyed off sectorID
	 * @param sectorID
	 * @return
	 * 	Returns the sector or null if it was not found
	 */
	public E3DSector getSector(String sectorID)
	{
		if(sectorMap.containsKey(sectorID))
			return (E3DSector)sectorMap.get(sectorID);
		else
			return null;
	}
	
	/**
	 * @return Returns the world's textureMap.
	 */
	public HashMap getTextureSet() {
		return textureSet;
	}
	
	/**
	 * Return the E3DTexture that is loaded in a textureSet and named textureName
	 * @param textureName The name of the texture in the textureMap
	 * @return
	 *  the E3DTexture if loaded, otherwise null
	 */
	public E3DTexture getTexture(String textureName){
		if(getTextureSet().containsKey(textureName))
			return (E3DTexture)getTextureSet().get(textureName);
		else
			return null;
	}
	
	/**
	 * Render the world in the current viewport
	 */
	public void render()
	{
		E3DEngine engine = getEngine();
		
		E3DViewport currentViewport = engine.getCurrentViewport();
		
		//Grab the viewports camera
		E3DActor curActor = currentViewport.getCameraActor();
		if(curActor == null)
			return;
		
		HashMap sectorMap = getSectorMap();
		if(sectorMap == null || sectorMap.values() == null)
			return;

		//********Sky Object ****************//
		//Update the skybox if there is one to be centered around the viewports camera & set its size to just fit in the viewable region
		if(getSkyObject() != null)
		{
			getSkyObject().update(curActor.getOrientation().getPosition(), (currentViewport.getFarClipPlane())); //set its size to just smaller than the far clip plane
		    getSkyObject().render(); //TODO: Maybe not the best way to do this on its own?? Just render the skybox on its own
		}

		//*******Sectors and All Contained Geometry***********//
		//Render the sector geometry starting from the current sector and recursing portals

		//Start the first frustum from the bounds of the viewport (not window as was previously done). Y Coords are reversed from window, so mins/maxs of Y is also reversed
//		E3DViewFrustum2D frustum = new E3DViewFrustum2D(engine, currentViewport, new E3DVector2F(currentViewport.getX(), currentViewport.getY() + currentViewport.getHeight()), new E3DVector2F(currentViewport.getX(), currentViewport.getY()),
//				 new E3DVector2F(currentViewport.getX() + currentViewport.getWidth(), currentViewport.getY()), new E3DVector2F(currentViewport.getX() + currentViewport.getWidth(), currentViewport.getY() + currentViewport.getHeight()));
		
		//Render just hte current sector, adn that sector will recurse from there.
		E3DSector sector = curActor.getSector(); //start rendering from the current actors sector and recurse from there
		sector.render();
//		HashMap renderedSectors = new HashMap();
//		sector.render(frustum, curActor, renderedSectors);
	}	
	
	/**
	 * Get all the triangles taht are in the world.  This means it gets all the triangles out of all the sectors and
	 *  all the actors in all the sectors world.
	 * Rendering should use a lower level approach at this (like just getting triangles out of the sector)
	 * @return
	 *  ArrayList of E3DTriangle objects or null if none exist.
	 */
	public ArrayList getTriangleList()
	{
		E3DSector sector = null;
		E3DActor actor = null;
		ArrayList triangleList = new ArrayList();

		Map.Entry entry = null;
		Iterator it = null;
		
		if(sectorMap != null)
		{
			it = sectorMap.entrySet().iterator();;
			while(it.hasNext())
			{
				entry = (Map.Entry)it.next();
				
				sector = (E3DSector)entry.getValue();
				triangleList.addAll(sector.getTriangleList());
			}
		}
		
		HashMap actorMap = getActorMap(); //build the actormap from all the actors in the sectors
		if(actorMap != null)
		{
			it = actorMap.entrySet().iterator();;
			while(it.hasNext())
			{
				entry = (Map.Entry)it.next();
				
				actor = (E3DActor)entry.getValue();
				triangleList.addAll(actor.getTriangleList());
			}
		}
		
		if(triangleList.size() <= 0)
			return null;
		else
			return triangleList;
	}
	
	/**
	 * Returns all the lights in all the sectors of the world
	 * Rendering should use a lower level approach at this (like just getting lights out of the sector)
	 * If lights have the same ID, the last one read will be used (the others overwritten in this map0
	 * @return
	 *  ArrayList of E3DLight objects or null if none exist
	 */
	public HashMap getLightMap()
	{
		E3DSector sector = null;
		HashMap lightMap = new HashMap();

		Map.Entry entry = null;
		Iterator it = null;
		
		if(sectorMap != null)
		{
			it = sectorMap.entrySet().iterator();;
			while(it.hasNext())
			{
				entry = (Map.Entry)it.next();
				
				sector = (E3DSector)entry.getValue();

				lightMap.putAll(sector.getLightMap());
			}
		}

		if(lightMap.size() <= 0)
			return null;
		else
			return lightMap;
	}	
	/**
	 * @return Returns the preloadedActorMap.
	 */
	public HashMap getPreloadedActorMap() {
		return preloadedActorMap;
	}
	
	/**
	 * This preloads actors into the world, so when the world's map is loaded, it will
	 * be able to also add the actors defined in the mapfile
	 * @param actorsNameInMapfile  This is what the actor is called in the mapfile
	 * @param actor This is the actual actors object.
	 */
	public void addPreloadedActor(String actorsNameInMapfile, E3DActor actor)
	{
		preloadedActorMap.put(actorsNameInMapfile, actor);
        actor.setWorld(this);
        actor.setSector(null);
	}
	
	/**
	 * Remove a preloaded actor from the world.  It will no longer be accessible during map loading.
	 * @param actorsNameInMapfile
	 */
	public void removePreloadedActor(String actorsNameInMapfile)
	{
		if(preloadedActorMap.containsKey(actorsNameInMapfile))
			preloadedActorMap.remove(actorsNameInMapfile);
	}

	/**
	 * Remove all the preloaded actors from the world
	 */
	public void removeAllPreloadedActors()
	{
		preloadedActorMap.clear();
	}
	
	/**
	 * This preloads particlesystems into the world, so when the world's map is loaded, it will
	 * be able to also add the actors defined in the mapfile
	 * @param particleSystemsNameInMapfile  This is what the particleSystem is called in the mapfile
	 * @param particleSystem This is the actual particleSystem's object.
	 */
	public void addPreloadedParticleSystem(String particleSystemsNameInMapfile, E3DParticleSystem particleSystem)
	{
	    preloadedParticleSystemMap.put(particleSystemsNameInMapfile, particleSystem);
	}

	/**
	 * Remove a preloaded particleSystem from the world.  It will no longer be accessible during map loading.
	 * @param actorsNameInMapfile
	 */
	public void removePreloadedParticleSystem(String partcleSystemsNameInMapfile)
	{
		if(preloadedParticleSystemMap.containsKey(partcleSystemsNameInMapfile))
		    preloadedParticleSystemMap.remove(partcleSystemsNameInMapfile);
	}
	
	/**
	 * Remove all the preloaded particle systems from the world
	 */
	public void removeAllPreloadParticleSystems()
	{
		preloadedParticleSystemMap.clear();
	}
	
	/**
	 * @return Returns the worldID.
	 */
	public String getWorldID() {
		return worldID;
	}
	
	/**
	 * Return a hashmap of particle systems that have been preloaded into the world.
	 * @return
	 */
    public HashMap getPreloadedParticleSystemMap() {
        return preloadedParticleSystemMap;
    }
    
    public E3DSkyObject getSkyObject() {
        return skyObject;
    }
    
    public void setSkyObject(E3DSkyObject skyObject) {
        if(skyObject != null)
            skyObject.setWorld(this);
        this.skyObject = skyObject;
    }
}
