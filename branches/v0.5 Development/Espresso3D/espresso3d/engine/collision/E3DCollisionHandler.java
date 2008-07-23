/*
 * Created on Aug 1, 2004
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
package espresso3d.engine.collision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.collision.base.IE3DCollisionDetectableObject;
import espresso3d.engine.collision.base.IE3DCollisionDetector;
import espresso3d.engine.collision.detectors.E3DCollisionDetectorSegment;
import espresso3d.engine.collision.detectors.E3DCollisionDetectorTriangles;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;
import espresso3d.engine.world.sector.particle.E3DSprite;
import espresso3d.engine.world.sector.portal.E3DPortal;

/**
 * @author espresso3d
 *
 *This contains collision detection helpers and the main collision detection routine for checking collisions 
 * in the world.
 */
public class E3DCollisionHandler extends E3DEngineItem
{
    private static IE3DCollisionDetector COLLISIONDETECTOR_SEGMENT;
    private static IE3DCollisionDetector COLLISIONDETECTOR_TRIANGLE;
    //Other detectors will be in their respective bounding object
    public static final int COLLISIONTYPE_SOURCE_SEGMENT = 0;
    public static final int COLLISIONTYPE_SOURCE_TRIANGLE = 1;
    public static final int COLLISIONTYPE_SOURCE_BOUNDINGOBJECT = 2;
    
    public static final int COLLISIONTYPE_COLLIDEE_TRIANGLE = 0;
    public static final int COLLISIONTYPE_COLLIDEE_BOUNDINGOBJECT_IFAVAIL = 1;
    
    
    private static E3DTriangle scratchTriangle = null; //used by collision handlers
    
	public E3DCollisionHandler(E3DEngine engine)
	{
	    super(engine);
        COLLISIONDETECTOR_SEGMENT = new E3DCollisionDetectorSegment(engine);
        COLLISIONDETECTOR_TRIANGLE = new E3DCollisionDetectorTriangles(engine);
	}

    public E3DCollision checkCollision(int collisionTypeSource, int collisionTypeDest, boolean notifyAllObjectsCollided, IE3DCollisionDetectableObject sourceObject, E3DVector3F sourceStartPos, E3DVector3F sourceEndPos)
    {
        IE3DCollisionDetector collisionDetector = null;
        //FIXME: don't use this registered mumbo jumbo
        if(collisionTypeSource == COLLISIONTYPE_SOURCE_SEGMENT)
            collisionDetector = (IE3DCollisionDetector)COLLISIONDETECTOR_SEGMENT;
        else if(collisionTypeSource == COLLISIONTYPE_SOURCE_TRIANGLE)
            collisionDetector = (IE3DCollisionDetector)COLLISIONDETECTOR_TRIANGLE;
        else if(collisionTypeSource == COLLISIONTYPE_SOURCE_BOUNDINGOBJECT)
        {
            if(sourceObject.getBoundingObject() == null)
            {
                System.out.println("Source collision type of bounding object was specified, but the source object has no bounding object.  Collision check will be ignored.");
                return null;
            }
            collisionDetector = sourceObject.getBoundingObject().getCollisionDetector();
        }
        else
        {
            System.out.println("Invalid source collision type. Collision check will be ignored.");
            return null;
        }
        
        //Check collision with sector & then check collision with actor
        //TODO: actor collisions should be nested in sector so we only check with actors that are in the current sector
        //TODO: Also, I'm checking every one so we can get the closest collision.. Maybe we should sort by distance first?
        boolean useBoundingObjectsIfPossible = (collisionTypeDest == COLLISIONTYPE_COLLIDEE_BOUNDINGOBJECT_IFAVAIL);
        
        //Instantiate one time so all collision handlers/detectors can use it for temporary calcs.
        if(scratchTriangle == null)
            scratchTriangle = new E3DTriangle(getEngine());
        
        ArrayList collisions = new ArrayList();
        collisions.add(getClosestCollisionWithWorld(collisionDetector, sourceObject, sourceStartPos, sourceEndPos, sourceObject.getSector().getWorld()));
        collisions.add(getClosestCollisionWithActor(collisionDetector, useBoundingObjectsIfPossible, notifyAllObjectsCollided, sourceObject, sourceStartPos, sourceEndPos, sourceObject.getSector().getWorld()));
        collisions.add(getClosestCollisionWithSprite(collisionDetector, useBoundingObjectsIfPossible, notifyAllObjectsCollided, sourceObject, sourceStartPos, sourceEndPos, sourceObject.getSector().getWorld()));
        collisions.add(getClosestCollisionWithParticleSystem(collisionDetector, useBoundingObjectsIfPossible, notifyAllObjectsCollided, sourceObject, sourceStartPos, sourceEndPos, sourceObject.getSector().getWorld()));
        
        E3DCollision collision = getClosestCollisionToPoint(sourceStartPos, collisions);

        //If we haven't already notified all objects collided (and thus notified the thing we hit already) 
        // and the thing we run into isn't the world (collidee is only null for the world), notify the thing
        if(!notifyAllObjectsCollided && collision != null && collision.getCollideeObject() != null)
            notifyCollidedObject(sourceObject, collision);

        return collision;
    }
    
	/**
	 * This method is used mainly by the engine to see when it translates objects, if they collide with a portal.  This
	 * is necessary for the engine to keep track of the sector objects are in.
	 * 
	 * This always uses a line segment (vs triangle) collision detector.
	 * 
	 * @param startPos The starting position of the object we are checking against portals
	 * @param endPos The ending position of the object we are checking against portals
	 * @param sector The sector the object is currently in
	 * 
	 * @return
	 *  Returns the sector the object should be placed in or null if there is
	 *  no portal collision
	 */
//	public E3DSector checkForPortalCollisionsAndGetSectorChange(E3DVector3F startPos, E3DVector3F endPos, E3DSector sector)
//	{
//	    if(sector == null)
//	        return null;
//	    
//	    HashMap portalMap = sector.getPortalMap();
//	    
//	    IE3DCollisionDetector detector = (IE3DCollisionDetector)COLLISIONDETECTOR_SEGMENT;
//	    
//	    Iterator it = portalMap.entrySet().iterator();
//	    Map.Entry entry = null;
//	    E3DPortal portal = null;
//
//	    E3DCollision collisionA = null, collisionB = null;
//	    E3DCollision furthestCollision = null;
//	    double furthestDistance = 0.0;
//	    double localDistance = 0.0;
//	    E3DPortal furthestCollisionPortal = null;
//	    
//	    while(it.hasNext())
//	    {
//	        entry = (Map.Entry)it.next();
//	        portal = (E3DPortal)entry.getValue();
//	        
//	        //Should only collide with 1 of the two triangles since it is point collisions
//	        collisionA = detector.checkForCollisionWithTriangle(null, startPos, endPos, portal.getTriangleA());
//	        if(collisionA == null)
//	            collisionB = detector.checkForCollisionWithTriangle(null, startPos, endPos, portal.getTriangleB());
//		  
//	        if(collisionA == null && collisionB == null)
//	            continue;
//	        else if(collisionA != null)
//	        {
//	            localDistance = Math.abs(collisionA.getIntersectionPt().subtract(startPos).getLengthSquared());
//	            if(furthestCollision == null || localDistance > furthestDistance)
//	            {
//	                furthestDistance = localDistance;
//	                furthestCollision = collisionA;
//	                furthestCollisionPortal = portal;
//	            }
//	        }
//	        else // if(collisionB != null)
//	        {
//	            localDistance = Math.abs(collisionB.getIntersectionPt().subtract(startPos).getLengthSquared());
//	            if(furthestCollision == null || localDistance > furthestDistance)
//	            {
//	                furthestDistance = localDistance;
//	                furthestCollision = collisionB;
//	                furthestCollisionPortal = portal;
//	            }
//	        }
//	        
//	    }
//	    if(furthestCollision != null)
//	        return sector.getWorld().getSector(furthestCollisionPortal.getLinkSectorID());
//	    else
//	        return null;
//	}
	
	/**
	 * Notifies an object that it has been run into
	 * @param sourceObject
	 * @param collision
	 */
	private void notifyCollidedObject(IE3DCollisionDetectableObject sourceObject, E3DCollision collision)
	{
		E3DCollision collideeCollision = new E3DCollision(collision);

        //Reverse the normals since collider/collidee are reversing
        E3DVector3F colliderCollisionNormal = collideeCollision.getColliderCollisionNormal();
		collideeCollision.setColliderCollisionNormal(collideeCollision.getCollideeCollisionNormal()); 
		collideeCollision.setCollideeCollisionNormal(colliderCollisionNormal);
        
		//Reverse the collided actor and set the collided actor to be the source actor (since that's the actor that the collidee hits)
		collideeCollision.setCollideeObject(sourceObject);
		
		//Notify the collided actor (still in the original collision) with the newly reversed collision
		if(sourceObject instanceof E3DActor)
		    collision.getCollideeObject().onCollisionActor(collideeCollision);
		else if(sourceObject instanceof E3DSprite)
		    collision.getCollideeObject().onCollisionSprite(collideeCollision);
		    
	}
	/**
	 * Returns the closest collision to sourceObject that happens in the world.
	 * 
	 * @param collisionDetector
	 * @param sourceObject
	 * @param startPos
	 * @param endPos
	 * @param world
	 * @return
	 */
	private E3DCollision getClosestCollisionWithWorld(IE3DCollisionDetector collisionDetector, IE3DCollisionDetectableObject sourceObject, E3DVector3F sourceStartPos, E3DVector3F sourceEndPos, E3DWorld world)
	{
		ArrayList triangleList = null;
        E3DSector sector = null;
		E3DTriangle triangle = null;
		E3DCollision partialCollision = null;
        E3DCollision closestCollision = null;
		
		sector = sourceObject.getSector(); //only check collision with the sector the actor is in
			
		triangleList = sector.getTriangleList(); //TODO: sort by how likely it is
		
		for(int i=0; i<triangleList.size(); i++)
		{
			triangle = (E3DTriangle)triangleList.get(i);
			
            partialCollision = collisionDetector.checkForCollisionWithTriangle(sourceObject, sourceStartPos, sourceEndPos, triangle);
			if(partialCollision != null)
			{
                partialCollision.setCollideeBoundingObject(triangle);
                
				if(closestCollision == null)
					closestCollision = partialCollision;
				else //see if its closer than the previous collision.. We want the closest collision
				{
					if(Math.abs(partialCollision.getIntersectionPt().subtract(sourceStartPos).getLengthSquared()) < 
					   Math.abs(closestCollision.getIntersectionPt().subtract(sourceStartPos).getLengthSquared()))
					{
						closestCollision = partialCollision;
					}
				}
			}
		}
        
        if(closestCollision != null)
        {
            closestCollision.setCollideeObject(null); //it did run into the world, but the world can't be notified
            closestCollision.setCollisionSector(sector);
            closestCollision.setCollisionWorld(world);

            return closestCollision;
        }
        else
            return null;
	}
    
	/**
	 * Returns the closest actor sourceObject collides with
	 * 
	 * @param collisionDetector
	 * @param useBoundingObjectsIfPossible
	 * @param notifyAllObjectsCollided
	 * @param sourceObject
	 * @param startPos
	 * @param endPos
	 * @param world
	 * @return
	 */
	private E3DCollision getClosestCollisionWithActor(IE3DCollisionDetector collisionDetector, boolean useBoundingObjectsIfPossible, boolean notifyAllObjectsCollided, IE3DCollisionDetectableObject sourceObject, E3DVector3F sourceStartPos, E3DVector3F sourceEndPos, E3DWorld world)
	{
        ArrayList collisions = new ArrayList();
		HashMap actorMap = sourceObject.getSector().getActorMap();
        
        Iterator it = actorMap.entrySet().iterator();
        Map.Entry entry = null;
        E3DActor actor = null;

        //Loop through all actors seeing if this actor runs into it
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			actor = (E3DActor) entry.getValue();
			if(!actor.isCollideable())
			    continue;

			if((IE3DCollisionDetectableObject)actor != sourceObject) //dont collide with self!
			{
			    collisions.add(getClosestCollisionWithCollisionDetectableObject(collisionDetector, useBoundingObjectsIfPossible, notifyAllObjectsCollided,
                                sourceObject, sourceStartPos, sourceEndPos, actor,  world));
            }
		}
        return getClosestCollisionToPoint(sourceStartPos, collisions);
	}
	
	/**
	 * This returns the closest collision with a sprite in the sector.  This includes
	 * sprites, billboard sprites, and particles
	 * @param collisionDetector
	 * @param useBoundingObjectsIfPossible
	 * @param notifyAllObjectsCollided
	 * @param sourceObject
	 * @param startPos
	 * @param endPos
	 * @param world
	 * @return
	 */
	private E3DCollision getClosestCollisionWithSprite(IE3DCollisionDetector collisionDetector, boolean useBoundingObjectsIfPossible, 
                                                        boolean notifyAllObjectsCollided, IE3DCollisionDetectableObject sourceObject, 
                                                        E3DVector3F sourceStartPos, E3DVector3F sourceEndPos, E3DWorld world)
	{
        ArrayList collisions = new ArrayList();
		ArrayList spriteList = sourceObject.getSector().getSpriteList();

		E3DSprite sprite = null;
		for(int a=0; a<spriteList.size(); a++)
		{
			sprite = (E3DSprite)spriteList.get(a);
			if(!sprite.isCollideable())
			    continue;
			
			if((IE3DCollisionDetectableObject)sprite != sourceObject) //dont collide with self!
			{
                collisions.add(getClosestCollisionWithCollisionDetectableObject(collisionDetector, useBoundingObjectsIfPossible, notifyAllObjectsCollided,
                        sourceObject, sourceStartPos, sourceEndPos, sprite,  world));
            }
        }
		return getClosestCollisionToPoint(sourceStartPos, collisions);
	}
	
    private E3DCollision getClosestCollisionWithParticleSystem(IE3DCollisionDetector collisionDetector, boolean useBoundingObjectsIfPossible, 
            boolean notifyAllObjectsCollided, IE3DCollisionDetectableObject sourceObject, 
            E3DVector3F sourceStartPos, E3DVector3F sourceEndPos, E3DWorld world)
    {
        ArrayList particleSystemList = sourceObject.getSector().getParticleSystemList();
        ArrayList particleList = null;
        E3DParticleSystem system = null;
        E3DSprite sprite = null;
        ArrayList collisions = new ArrayList();
        
        for(int a = 0; a < particleSystemList.size(); a++)
        {
            system = (E3DParticleSystem)particleSystemList.get(a);
            if(system.isCollideable())
            {
                particleList = system.getParticleList();

                for(int b=0; b<particleList.size(); b++)
                {
                    sprite = (E3DSprite)particleList.get(b);
                    if(!sprite.isCollideable())
                        continue;
                    
                    if((IE3DCollisionDetectableObject)sprite != sourceObject) //dont collide with self!
                    {
                        collisions.add(getClosestCollisionWithCollisionDetectableObject(collisionDetector, useBoundingObjectsIfPossible, notifyAllObjectsCollided,
                                sourceObject, sourceStartPos, sourceEndPos, sprite,  world));
                    }
                }
            }
        }
        
        return getClosestCollisionToPoint(sourceStartPos, collisions);
    }
    /**
     * Generic method used to check if sourceObject runs into collision detectable object
     * @param collisionDetector
     * @param useBoundingObjectsIfPossible  If true, the possibleCollidee's boundingObject will be used if its not null, otherwise, its trianlges will be used for the detection
     * @param notifyAllObjectsCollided If true, if a collisions is found with possibleCollidee, it will be notified
     * @param sourceObject The object that is moving that might run into possibleCollidee
     * @param sourceStartPos Starting pos of sourceObject
     * @param sourceEndPos Ending pos of sourceObject (where it wants to go)
     * @param possibleCollidee The object that we need to see if sourceObject runs into
     * @param world the world this all happens in
     * @return
     */
    private E3DCollision getClosestCollisionWithCollisionDetectableObject(IE3DCollisionDetector collisionDetector, boolean useBoundingObjectsIfPossible,
            boolean notifyAllObjectsCollided, IE3DCollisionDetectableObject sourceObject, 
            E3DVector3F sourceStartPos, E3DVector3F sourceEndPos, IE3DCollisionDetectableObject possibleCollidee, E3DWorld world)
    {
        E3DCollision collision = null, closestCollision = null;
        if(useBoundingObjectsIfPossible && possibleCollidee.getBoundingObject() != null)
        {
            collision = collisionDetector.checkForCollisionWithBoundingObject(sourceObject, sourceStartPos, sourceEndPos, possibleCollidee.getBoundingObject());
            if(collision != null)
            {
                collision.setCollideeObject(possibleCollidee); 
                collision.setCollideeBoundingObject(possibleCollidee.getBoundingObject());
                collision.setCollisionSector(possibleCollidee.getSector());
                collision.setCollisionWorld(world);
            }
        }
        else
        {
            ArrayList triangleList = possibleCollidee.getTriangleList();
            E3DTriangle triangle = null;
            for(int i=0; i<triangleList.size(); i++)
            {
                triangle = (E3DTriangle)triangleList.get(i);
                
                //put the detectable's triangles into worldspace coords
                scratchTriangle.setVertexPosA(possibleCollidee.getOrientation().getWorldVector(triangle.getVertexPosA()));
                scratchTriangle.setVertexPosB(possibleCollidee.getOrientation().getWorldVector(triangle.getVertexPosB()));
                scratchTriangle.setVertexPosC(possibleCollidee.getOrientation().getWorldVector(triangle.getVertexPosC()));
                
                collision = collisionDetector.checkForCollisionWithTriangle(sourceObject, sourceStartPos, sourceEndPos, scratchTriangle);
                if(collision != null)
                {
                    collision.setCollideeObject(possibleCollidee); 
                    collision.setCollideeBoundingObject(triangle);
                    collision.setCollisionSector(possibleCollidee.getSector());
                    collision.setCollisionWorld(world);

                    if(closestCollision == null)
                        closestCollision = collision;
                    else
                    {
                        if(Math.abs(collision.getIntersectionPt().subtract(sourceStartPos).getLengthSquared()) < 
                           Math.abs(closestCollision.getIntersectionPt().subtract(sourceStartPos).getLengthSquared()))
                            closestCollision = collision;
                    }
                }
            }
            collision = closestCollision;
        }
        
        //Only notify one time if necessary for the closest collision (before it was notifying for every triangle it hit, UGH!)
        if(collision != null && notifyAllObjectsCollided && collision.getCollideeObject() != null) //notify the object this one collided with.
            notifyCollidedObject(sourceObject, collision);

        return collision;
    }
            
	/**
	 * This returns a list of triangles sorted by how close they are to startPos/endPos.
	 * @param triangleList
	 * @param startPos
	 * @param endPos
	 * @return
	 */
	private ArrayList sortTriangleListByDistance(ArrayList triangleList, E3DVector3F startPos, E3DVector3F endPos)
	{
		SortedMap triangleDistanceMap = new TreeMap(); //create a map of the triangles keyed off of distance

		//create a map keyed off of distance from avg distance of startPos + distance endPos.  use the closest vertex as the distance
		E3DTriangle triangle = null;
		E3DVector3F pointToVertexVector = null;

		int i=0, v=0;
		double distanceStart = 0.0;
		double avg = 0.0;
		double closestDistance = -1.0;
		Double key = null;
		for(i=0; i<triangleList.size(); i++)
		{
			triangle = (E3DTriangle)triangleList.get(i);
			
			for(v = 0; v < 3; v++)
			{
				pointToVertexVector = endPos.subtract(startPos);
	//			pointToVertexVector = triangle.getVertex()[v].subtract(startPos);
				//			distanceStart = pointToVertexVector.getLengthSquared();
	//			pointToVertexVector = triangle.getVertex()[v].subtract(endPos);
	//			distanceEnd = pointToVertexVector.getLengthSquared();
		
				distanceStart = Math.abs(pointToVertexVector.dotProduct(triangle.getVertexPos(v)));
				if(closestDistance < 0)
					closestDistance = distanceStart; //(distanceStart + distanceEnd) / 2;
				else
				{
					avg = distanceStart; //(distanceStart + distanceEnd) / 2;

					if(avg < closestDistance)
						closestDistance = avg;
				}
			}
			
			//Since there can be multiple with the same closest distance, we need a list of triangles for each distance key
			key = new Double(closestDistance);
			if(triangleDistanceMap.containsKey(key))
				((ArrayList)triangleDistanceMap.get(key)).add(triangle);
			else
			{
				ArrayList thisDistanceTriangleList = new ArrayList();
				thisDistanceTriangleList.add(triangle);
				triangleDistanceMap.put(key, thisDistanceTriangleList);
			}
		}
		
		//Create the arraylist out of this sorted map

		ArrayList retList = new ArrayList();
		Map.Entry entry = null;
		ArrayList keysList = null;
		Iterator it = triangleDistanceMap.entrySet().iterator();
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			keysList = (ArrayList)entry.getValue();
			
			retList.addAll(keysList);
		}
		return retList;
	}

	/*** Base Collision Detectors and utilities **/
    /**
     * Takes a list of E3DCollision object's and returns which collision was closest to the point
     */
    public static E3DCollision getClosestCollisionToPoint(E3DVector3F point, ArrayList collisions)
    {
        E3DCollision collisionSearch = null;
        E3DCollision closestCollision = null;
        
        if(collisions == null || collisions.size() <= 0)
            return null;
        
        for(int i=0; i < collisions.size(); i++)
        {
            collisionSearch = (E3DCollision)collisions.get(i);
            if(collisionSearch == null)
                continue;
            
            if(closestCollision == null)
                closestCollision = collisionSearch;
            else
            {
                if(Math.abs(collisionSearch.getIntersectionPt().subtract(point).getLengthSquared()) < 
                   Math.abs(closestCollision.getIntersectionPt().subtract(point).getLengthSquared()))
                    closestCollision = collisionSearch;
            }
        }
        
        return closestCollision;
    }

    public static E3DCollision getFurthestCollisionFromPoint(E3DVector3F point, ArrayList collisions)
    {
        E3DCollision collisionSearch = null;
        E3DCollision closestCollision = null;
        
        if(collisions == null || collisions.size() <= 0)
            return null;
        
        for(int i=0; i < collisions.size(); i++)
        {
            collisionSearch = (E3DCollision)collisions.get(i);
            if(collisionSearch == null)
                continue;
            
            if(closestCollision == null)
                closestCollision = collisionSearch;
            else
            {
                if(Math.abs(collisionSearch.getIntersectionPt().subtract(point).getLengthSquared()) >
                   Math.abs(closestCollision.getIntersectionPt().subtract(point).getLengthSquared()))
                    closestCollision = collisionSearch;
            }
        }
        
        return closestCollision;
    }

    public static IE3DCollisionDetector getCOLLISIONDETECTOR_SEGMENT() {
        return COLLISIONDETECTOR_SEGMENT;
    }

    public static IE3DCollisionDetector getCOLLISIONDETECTOR_TRIANGLE() {
        return COLLISIONDETECTOR_TRIANGLE;
    }

}
