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
package espresso3d.engine.world.sector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.E3DCollisionHandler;
import espresso3d.engine.collision.base.IE3DCollisionDetector;
import espresso3d.engine.lowlevel.geometry.E3DLine;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.window.viewport.frustum.E3DViewFrustum2D;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.base.E3DPortalEnabledRenderable;
import espresso3d.engine.world.sector.base.IE3DPortalEnabledItem;
import espresso3d.engine.world.sector.light.E3DLight;
import espresso3d.engine.world.sector.light.E3DTriangleLighter;
import espresso3d.engine.world.sector.mesh.E3DMesh;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;
import espresso3d.engine.world.sector.particle.E3DSprite;
import espresso3d.engine.world.sector.portal.E3DPortal;
import espresso3d.engine.world.sector.portal.E3DPortalCollision;

/**
 * @author espresso3d
 *
 * Worlds are divided up into multiple sectors.  Sectors contain actors, lights
 * and geometry.  A portaling algorithm then connects sectors together through portals.
 * This is a way of reducing the potentially visibile triangles quickly. (portaling will come soon)
 * 
 * Outdoor scenes will have special terrain sectors that will work in the same sort of fashion (not implemented).
 */
public class E3DSector extends E3DRenderable
{
	private String sectorID; //id of this sector
	private E3DWorld world; //The world the sector resides in

	//Store the portals in the sector
	private HashMap portalMap;
	
	private E3DMesh mesh;
	private HashMap actorMap; //Map of all the E3DActor objects in the sector keyed off actorName string
	private HashMap lightMap; //List of lights in this sector
	private ArrayList particleSystemList; //List of particle systems in the sector.
	private ArrayList spriteList; //List of sprites (billboarding or not) in the sector
	
    private E3DRenderTree renderTree;
	
	public E3DSector(E3DEngine engine, String sectorID)
	{
		super(engine);
		setSectorID(sectorID);
		setPortalMap(new HashMap());
		setLightMap(new HashMap());
		setActorMap(new HashMap());
		setParticleSystemList(new ArrayList());
		setSpriteList(new ArrayList());
		renderTree = new E3DRenderTree(engine);
		mesh = new E3DMesh(engine, this);
	}
	
    public void moveToSector(IE3DPortalEnabledItem renderableMovingHere)
    {
        if(renderableMovingHere instanceof E3DActor)
        {
            E3DActor actor = (E3DActor)renderableMovingHere;
            addActor(actor);
            moveToSector(renderableMovingHere, actor.getOrientation().getPosition());
        }
        else if(renderableMovingHere instanceof E3DLight)
        {
            E3DLight light =(E3DLight)renderableMovingHere;
            addLight(light);
            moveToSector(renderableMovingHere, light.getPosition());
        }
        else if(renderableMovingHere instanceof E3DParticleSystem)
        {
            E3DParticleSystem ps = (E3DParticleSystem)renderableMovingHere;
            addParticleSystem(ps);
            moveToSector(renderableMovingHere, ps.getOrientation().getPosition());
        }
        else if(renderableMovingHere instanceof E3DSprite)
        {
            E3DSprite sprite = (E3DSprite)renderableMovingHere;
            addSprite(sprite);
            moveToSector(renderableMovingHere, sprite.getOrientation().getPosition());
        }
        else
            System.out.println("Unknown object type: " + this.getClass() + " for sector.moveToSector portal object");

    }
    
    protected void moveToSector(IE3DPortalEnabledItem renderableMovingHere, E3DVector3F position)
    {
        return; //do nothing in this type - e3dterrain does something with it
    }
    
    public void moveFromSector(IE3DPortalEnabledItem renderableMovingAway)
    {
        if(renderableMovingAway instanceof E3DActor)
            removeActor(((E3DActor)renderableMovingAway).getActorID());
        else if(renderableMovingAway instanceof E3DLight)
            removeLight(((E3DLight)renderableMovingAway).getLightID());
        else if(renderableMovingAway instanceof E3DParticleSystem)
            removeParticleSystem((E3DParticleSystem)renderableMovingAway);
        else if(renderableMovingAway instanceof E3DSprite)
            removeSprite((E3DSprite)renderableMovingAway);
        else
            System.out.println("Unknown object type: " + this.getClass() + " for sector.moveFromSector portal object");
    }
    
    
	/**
	 * Add a portal to the sector.  Portals link sectors to other sectors
	 * @return
	 */
	public void addPortal(E3DPortal portal)
	{
	    portal.setSector(this);
	    getPortalMap().put(portal.getPortalID(), portal);
	}
	
	/**
	 * @return Returns the sectorID.
	 */
	public String getSectorID() {
		return sectorID;
	}
	/**
	 * @param sectorID The sectorID to set.
	 */
	private void setSectorID(String sectorID) {
		this.sectorID = sectorID;
	}

    /**
	 * @return Returns the triangleList sorted by distance
	 */
/*	public ArrayList getTriangleListSortedByDistance() {
		ArrayList newList = new ArrayList();
		ArrayList triangleList = getTriangleList();
		E3DTriangle triangle = null;
		E3DVector3F vertPos;
		double distance = -1.0;
		double closestDistance = -1.0;
		
		for(int i=0; i < triangleList.size(); i++)
		{
			distance = -1.0;
			triangle = (E3DTriangle)triangleList.get(i);
			
			for(int a = 0; a < 3; a++)
			{
				vertPos = triangle.getVertex()[a];
				distance = E3DCollisionHandler.getSegmentTriangleDistance(vertPos, triangle);
				
				if(closestDistance == -1.0)
					closestDistance = distance;
				
				else if(distance < closestDistance)
					closestDistance = distance;
			}
		}
		
		return triangleList;
	}
*/
	/**
	 * @return Returns the list of triangles that comprise the geometry of the sector.
	 * 
	 * All other triangle operations are accessed through the mesh
	 */
	public ArrayList getTriangleList() {
		return mesh.getTriangleList();
	}

	/**
	 * This renders the sector geometry and actors in the sector atPosition
	 * This is handled by the engine's rendering loop
	 */
	public void render()
	{
		//Start the first frustum from the bounds of the viewport (not window as was previously done). Y Coords are reversed from window, so mins/maxs of Y is also reversed
		HashMap renderedSectors = new HashMap();
		E3DViewport currentViewport = getEngine().getCurrentViewport();
		
		E3DViewFrustum2D frustum = new E3DViewFrustum2D(getEngine(), currentViewport, new E3DVector2F(currentViewport.getX(), currentViewport.getY() + currentViewport.getHeight()), new E3DVector2F(currentViewport.getX(), currentViewport.getY()),
				 new E3DVector2F(currentViewport.getX() + currentViewport.getWidth(), currentViewport.getY()), new E3DVector2F(currentViewport.getX() + currentViewport.getWidth(), currentViewport.getY() + currentViewport.getHeight()));
	
		render(frustum, currentViewport.getCameraActor(), renderedSectors);
	}
	
    public void render(E3DViewFrustum2D frustum, E3DActor curActor, HashMap alreadyRenderedSectors)
    {
        Iterator it = null;
        Map.Entry entry = null;
        
        double lastFrameTimeSeconds = getEngine().getFpsTimer().getLastUpdateTimeSeconds();

        /********Actors***********/
        /** Do actors first so they can see the sector's lighting flag **/
        //Actors: Build a hashmap of all the triangles that are going to be rendered.
        if(getActorMap().entrySet() != null)
        {
            it = getActorMap().entrySet().iterator();
            E3DActor actor = null;
            while(it.hasNext())
            {
                entry = (Map.Entry)it.next();
                actor = (E3DActor)entry.getValue();
                
                actor.update(lastFrameTimeSeconds);
//                	tmpTree.addAll(actor.getRenderTree());
                actor.render();
            }
        }

        /******Sector******/
        update();
        if(getMesh().isRendered())
        	this.renderTree.render();

        /****** Sprites *******/
        if(spriteList != null)
        {
            E3DRenderTree spriteRenderTree = new E3DRenderTree(getEngine());
            E3DSprite sprite;
        	int spriteListSize = spriteList.size();
            for(int i=0; i<spriteListSize; i++)
            {
            	sprite = (E3DSprite)spriteList.get(i);
                    
                sprite.update(curActor, lastFrameTimeSeconds);
                spriteRenderTree.getSpriteHandler().add(sprite);
            }
            spriteRenderTree.render();
        }

        /********Particle Systems******/
        E3DParticleSystem particleSystem;
        if(particleSystemList != null)
        {
            for(int i=0; i < particleSystemList.size(); i++)
            {
                particleSystem = (E3DParticleSystem)particleSystemList.get(i);

                particleSystem.updateParticles(curActor); //Update particles first
                if(!particleSystem.isAlive())
                {
                    removeParticleSystem(particleSystem);
                    i--;
                }
                else
                	particleSystem.render();
            }
        }

        /*******Portals********/
        //Iterate through portals, checking visibility, and grab all their linked sectors triangles if they are
        E3DPortal portal = null;
        E3DSector linkedSector = null;
        if(getPortalMap().entrySet() != null)
        {
            it = getPortalMap().entrySet().iterator();
            while(it.hasNext())
            {
                entry = (Map.Entry)it.next();
                portal = (E3DPortal)entry.getValue();
                
                portal.render();
                //Don't render a sector already rendered again
                if(alreadyRenderedSectors.containsKey(portal.getLinkSectorID()))
                    continue;
                                
                //TODO: Fix this to use this so we get around the projection issues: ARBOcclusionQuery
                if(frustum.isPortalInFrustum(portal))
                {
                    if(getWorld().getSectorMap().containsKey(portal.getLinkSectorID()))
                    {
                        linkedSector = (E3DSector)getWorld().getSectorMap().get(portal.getLinkSectorID());
                        
                        alreadyRenderedSectors.put(getSectorID(), null); //Make sure we indicate before recursion this has been rendered (so its children won't do it again)
                        
                        linkedSector.render(portal.get2DFrustum(frustum.getViewport()), curActor, alreadyRenderedSectors);
                    }
                }
            }
        }
    }
    
    private void update()
    {
        if(mesh.isRendered() && mesh.isLit() && mesh.isNeedingLightRecalc())
        {
            E3DTriangleLighter.lightTriangles(this, getTriangleList(), null);
            mesh.setNeedingLightRecalc(false);
        }
    }
    
	private void debugRenderNormals()
	{
		Iterator it = null;
		Map.Entry entry = null;
	    
		//TODO: temp: render the normals
		ArrayList lineList = new ArrayList();
		ArrayList triList = getTriangleList();
		E3DTriangle triangle = null;
		E3DLine line = null;
		for(int i=0; triList != null && i < triList.size(); i++)
		{
			triangle = (E3DTriangle)triList.get(i);
			
			line = new E3DLine(getEngine(), triangle.getCentroid(), triangle.getCentroid().add(triangle.getNormal()), new E3DVector3F(0.8, 0.8, 0.2));
			lineList.add(line);
		}
		getEngine().getGeometryRenderer().initSolidAndLineRendering();
		getEngine().getGeometryRenderer().renderLineList(lineList);

		if(actorMap != null && actorMap.entrySet() != null)
		{
			it = actorMap.entrySet().iterator();
			while(it.hasNext())
			{
				entry = (Map.Entry)it.next();
				E3DActor actor = (E3DActor)entry.getValue();
				triList = actor.getTriangleList();
				for(int i=0; triList != null && i<triList.size(); i++)
				{
					triangle = (E3DTriangle)triList.get(i);
					
					line = new E3DLine(getEngine(), triangle.getCentroid(), triangle.getCentroid().add(triangle.getNormal()), new E3DVector3F(0.8, 0.8, 0.2));
					lineList.add(line);
				}
			}
			getEngine().getGeometryRenderer().initSolidAndLineRendering();
			getEngine().getGeometryRenderer().renderLineList(lineList);
		}			
	}

	/**
	 * @return Returns the lightMap.
	 */
	public HashMap getLightMap() {
		return lightMap;
	}
	/**
	 * @param lightMap The lightMap to set.
	 */
	public void setLightMap(HashMap lightMap) {
		this.lightMap = lightMap;
	}
	
	/**
	 * Add a light to the sector
	 * @param light
	 */
	public void addLight(E3DLight light)
	{
		light.setEngine(getEngine());
		light.setSector(this);
		getLightMap().put(light.getLightID(), light);
		mesh.setNeedingLightRecalc(true);
	}
	
	/**
	 * Remove a light from the sector
	 * @param lightID
	 */
	public void removeLight(String lightID)
	{
		if(lightMap.containsKey(lightID))
		{
			lightMap.remove(lightID);
			mesh.setNeedingLightRecalc(true);
		}
	}
	
    /**
     * Remove a light by the E3DLight object
     * @param light
     */
    public void removeLight(E3DLight light)
    {
        if(light != null)
            removeLight(light.getLightID());
    }

    /**
	 * Remove all lights from the sector
	 */
	public void removeAllLights()
	{
		lightMap.clear();
		mesh.setNeedingLightRecalc(true);
	}
	
	
	/**
	 * @return Returns a HashMap of all E3DActor's in the sector keyed off actorID
	 */
	public HashMap getActorMap() {
		return actorMap;
	}
	/**
	 * @param actorMap The actorMap to set.
	 */
	private void setActorMap(HashMap actorMap) {
		this.actorMap = actorMap;
	}
	
	/**
	 * Add an actor to this sector
	 * @param actor
	 */
	public void addActor(E3DActor actor)
	{
		actorMap.put(actor.getActorID(), actor);
        actor.setSector(this);
        actor.setWorld(this.world);
	}
	
	/**
	 * Get an actor in this sector by its ID
	 * @param actorID
	 * @return
	 */
	public E3DActor getActorByID(String actorID)
	{
		if(actorMap.containsKey(actorID))
			return (E3DActor)actorMap.get(actorID);
		else
			return null;
	}
	
	/**
	 * Remove an actor with actorID from this sector
	 * @param actorID
	 */
	public void removeActor(String actorID)
	{
		if(actorMap.containsKey(actorID))
			actorMap.remove(actorID);
	}

    /**
     * Remove an actor by its object
     * @param actor
     */
    public void removeActor(E3DActor actor)
    {
        if(actor != null)
            removeActor(actor.getActorID());
    }
	/**
	 * Remove all actors from the sector
	 */
	public void removeAllActors()
	{
		actorMap.clear();
	}
    /**
     * Add a particle system to the engine
     * @param particleSystem
     */
    public void addParticleSystem(E3DParticleSystem particleSystem)
    {
        particleSystemList.add(particleSystem);
        particleSystem.setSector(this);
    }
	
    /**
     * Only ID's particle systems can be removed
     * @param particleSystemID
     */
    public void removeParticleSystem(E3DParticleSystem particleSystem)
    {
        particleSystemList.remove(particleSystem);
    }

    /**
     * Remove all particle systems from the sector
     */
    public void removeAllParticleSystems()
    {
    	particleSystemList.clear();
    }
    
    
    /**
     * Add a sprite to the sector.  This can be billboarding or non
     * @param sprite
     */
    public void addSprite(E3DSprite sprite)
    {
        sprite.setSector(this);
        spriteList.add(sprite);
//        renderTree.addQuad(sprite.getQuad());//addRenderable(sprite);
    }
    
    /**
     * Remove a sprite from the sector
     * @param sprite
     */
    public void removeSprite(E3DSprite sprite)
    {
        spriteList.remove(sprite);
    }
    
    /**
     * Remove all sprites from the sector
     */
    public void removeAllSprites()
    {
    	spriteList.clear();
    }
    
    /**
	 * @return Returns the world the sector is in
	 */
	public E3DWorld getWorld() {
		return world;
	}
	/**
	 * @param world The world the sector is part of.  This should only be used by the engine. If the world
	 * the sector is in changes, it needs more than to just be set here...
	 */
	public void setWorld(E3DWorld world) {
		this.world = world;
	}
	
//	
//	public void setNeedingLightRecalc(boolean needingLightRecalc) 
//	{
//		if(needingLightRecalc != this.needingLightRecalc)
//		{
//			this.needingLightRecalc = needingLightRecalc;
//			//If we set this to true, we need to inform all the actors that they too need to be relit.
//			//This does not work the other way around (We can't turn off lights for everything)
//			if(needingLightRecalc && actorMap.size() > 0)
//			{
//				Iterator it = actorMap.entrySet().iterator();
//				Map.Entry entry = null;
//				E3DActor actor = null;
//				while(it.hasNext())
//				{
//					entry = (Map.Entry)it.next();
//					actor = (E3DActor)entry.getValue();
//					actor.getMesh().setNeedingLightRecalc(true);
//				}
//			}
//		}
//	}
	/**
	 * Return the hashmap of portals in the sector keyed off portalID
	 * @return
	 */
	public HashMap getPortalMap() {
        return portalMap;
    }
    private void setPortalMap(HashMap portalMap) {
        this.portalMap = portalMap;
    }
    
//    private E3DSortedRenderableMap getTextureSortedTriangleMap() {
//        return textureSortedTriangleMap;
//    }
//    private void setTextureSortedTriangleMap(E3DSortedRenderableMap textureSortedTriangleMap) {
//        this.textureSortedTriangleMap = textureSortedTriangleMap;
//    }
//    private E3DSortedRenderableMap getTextureSortedQuadMap() {
//        return textureSortedQuadMap;
//    }
//    private void setTextureSortedQuadMap(E3DSortedRenderableMap textureSortedQuadMap) {
//        this.textureSortedQuadMap = textureSortedQuadMap;
//    }
    
    /**
     * Get the list of E3DParticleSystem's in the sector
     * @return
     */
    public ArrayList getParticleSystemList() {
        return particleSystemList;
    }

    private void setParticleSystemList(ArrayList particleSystemList) {
        this.particleSystemList = particleSystemList;
    }
    
    /**
     * Get the list of E3DSprite's (or E3DBillboardSprite's) in the sector
     * @return
     */
    public ArrayList getSpriteList() {
        return spriteList;
    }

    private void setSpriteList(ArrayList spriteList) {
        this.spriteList = spriteList;
    }
    
    
    /**
     * This gets a list of sectors directly linked to this sector via portals.  It will contain
     * only 1 copy of each sector it is linked to (even if it has multiple portals going to the same sector).
     * This only traversed 1 level deep (e.g.: directly adjoining sectors).  This does not include this
     * sector.
     * @return
     *  Returns an arraylist containing 1...n linked sectors (not including this sector)
     *  or null if no sectors are linked
     */
    public ArrayList getUniqueDirectlyLinkedSectorList()
    {
		//Build a list of all the sectors that need to be recursed through
		//This is a list of this sector and 1 unique instance of every adjoining sector via a portal
		HashMap alreadyAddedSector = new HashMap(); //Will keep copies of sectors we have traversed to from portals, so we don't duplicate

		if(portalMap == null || portalMap.entrySet() == null)
		    return null;
		
		ArrayList linkedSectorList = new ArrayList();

		Iterator portalIterator = portalMap.entrySet().iterator();
		Map.Entry entry = null;
		
		E3DPortal portal = null;
		E3DSector linkedSector = null;
		while(portalIterator.hasNext())
		{
		    entry = (Map.Entry)portalIterator.next();
		    
		    portal = (E3DPortal)entry.getValue();
		    //If it hasn't already been added and the sector actually exists in the world
		    if(!alreadyAddedSector.containsKey(portal.getLinkSectorID()) && 
		        getWorld().getSectorMap().containsKey(portal.getLinkSectorID()))
		    {
		        linkedSector = getWorld().getSector(portal.getLinkSectorID());
		        linkedSectorList.add(linkedSector); //add it to the list
		        alreadyAddedSector.put(portal.getLinkSectorID(), null);
		    }
		}
		
		return linkedSectorList;
    }

	public E3DMesh getMesh() {
		return mesh;
	}

	public E3DRenderTree getRenderTree() {
		return renderTree;
	}
    
    /**
     * This method is used mainly by the engine to see when it translates objects, if they collide with a portal.  This
     * is necessary for the engine to keep track of the sector objects are in.
     * 
     * This always uses a line segment (vs triangle) collision detector.
     * @param startPos
     * @param endPos
     * @return
     */
    public E3DPortalCollision checkPortalCollision(E3DVector3F startPos, E3DVector3F endPos)
    {
        HashMap portalMap = getPortalMap();
        
        IE3DCollisionDetector detector = E3DCollisionHandler.getCOLLISIONDETECTOR_SEGMENT();
        
        Iterator it = portalMap.entrySet().iterator();
        Map.Entry entry = null;
        E3DPortal portal = null;

        E3DCollision collisionA = null, collisionB = null;
        E3DCollision furthestCollision = null;
        double furthestDistance = 0.0;
        double localDistance = 0.0;
        E3DPortal furthestCollisionPortal = null;
        
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            portal = (E3DPortal)entry.getValue();
            
            //Should only collide with 1 of the two triangles since it is point collisions
            collisionA = detector.checkForCollisionWithTriangle(null, startPos, endPos, portal.getTriangleA());
            if(collisionA == null)
                collisionB = detector.checkForCollisionWithTriangle(null, startPos, endPos, portal.getTriangleB());
          
            if(collisionA == null && collisionB == null)
                continue;
            else if(collisionA != null)
            {
                localDistance = Math.abs(collisionA.getIntersectionPt().subtract(startPos).getLengthSquared());
                if(furthestCollision == null || localDistance > furthestDistance)
                {
                    furthestDistance = localDistance;
                    furthestCollision = collisionA;
                    furthestCollisionPortal = portal;
                }
            }
            else // if(collisionB != null)
            {
                localDistance = Math.abs(collisionB.getIntersectionPt().subtract(startPos).getLengthSquared());
                if(furthestCollision == null || localDistance > furthestDistance)
                {
                    furthestDistance = localDistance;
                    furthestCollision = collisionB;
                    furthestCollisionPortal = portal;
                }
            }
            
        }
        if(furthestCollision != null)
        {
            E3DPortalCollision portalCollision = new E3DPortalCollision();
            portalCollision.setCurrentSector(this);
            portalCollision.setCurrentSectorID(this.getSectorID());
            portalCollision.setToSectorID(furthestCollisionPortal.getLinkSectorID());
            portalCollision.setToSector(getWorld().getSector(furthestCollisionPortal.getLinkSectorID()));
            
            return portalCollision;
        }
        else
            return null;
    }
}
