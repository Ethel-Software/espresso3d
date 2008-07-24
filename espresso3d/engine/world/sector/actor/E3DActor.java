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
package espresso3d.engine.world.sector.actor;

import java.util.ArrayList;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.base.IE3DCollisionDetectableObject;
import espresso3d.engine.collision.bounding.IE3DBoundingObject;
import espresso3d.engine.exceptions.E3DMissingEngineException;
import espresso3d.engine.fileloaders.E3DActorLoader;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.sound.E3DSound3D;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.skeleton.E3DSkeleton;
import espresso3d.engine.world.sector.base.E3DPortalEnabledRenderable;
import espresso3d.engine.world.sector.light.E3DTriangleLighter;
import espresso3d.engine.world.sector.light.base.IE3DLightableObject;
import espresso3d.engine.world.sector.mesh.E3DMesh;


/**
 * @author espresso3d
 *
 * An actor is an object made of triangle geometry.
 * 
 * Actors are (or can be) textured, lit, and animated (not yet) 3d objects in a world/sector
 */
public abstract class E3DActor extends E3DPortalEnabledRenderable implements IE3DCollisionDetectableObject
{
	private E3DWorld world;

	private IE3DBoundingObject boundingObject;
	private String actorID; //this is the key used in the sectors actor map (ie: the name the user gives the actor)
	
	private E3DMesh mesh = null;
    private boolean debugSkeletonDisplayed = false;
    private boolean debugBoundingObjectDisplayed = false;
	
    private ArrayList associatedSound3DList;
    
    private E3DOrientation orientation;
//	private E3DVector3F position = new E3DVector3F(0.0, 0.0, 0.0); //actual position of the camera
//	private E3DVector3F forward = new E3DVector3F(0.0, 0.0, 1.0); //normalised vector pointing forward
//	private E3DVector3F up = new E3DVector3F(0.0, 1.0, 0.0); //normalised vector pointing up

    //Bones and animation
	private E3DSkeleton skeleton;
	
	/**** TEST OF RENDERTREES***/
	private E3DRenderTree renderTree;
//	private boolean needRenderTreeRehash = false;
	
	/**********/
	/**
	 * Create an actor without a bounding object 
	 * 
	 * @param engine The engine the actor is part of
	 * @param world Currently required because an actors textureset is loaded into the world's textureset so it can be shared across all objects (better
	 * for memory allocation)
	 * @param actorID  A unique ID for the actor
	 */
    public E3DActor(E3DEngine engine, E3DWorld world, String actorID)
	{
		this(engine, world, null, actorID);
	}

    /**
     * Create an actor with a bounding object 
     * 
	 * @param engine The engine the actor is part of
	 * @param world Currently required because an actors textureset is loaded into the world's textureset so it can be shared across all objects (better
	 * for memory allocation)
     * @param boundingObject Bounding object for the actor
	 * @param actorID  A unique ID for the actor
     */
	public E3DActor(E3DEngine engine, E3DWorld world, IE3DBoundingObject boundingObject, String actorID)
	{
		super(engine);
		
        this.world = world; //gets set when added to a sector
        this.actorID = actorID;
        this.orientation = new E3DOrientation(engine);
        associatedSound3DList = new ArrayList();
        
        setMesh(new E3DMesh(engine, this));
        setBoundingObject(boundingObject);
		setSkeleton(new E3DSkeleton(engine));
		
		//TEST
		renderTree = new E3DRenderTree(engine);
        
	}

	/**
	 * This copies all the actor geometry into another actor.
	 * @param toCopyActor
	 */
	public E3DActor(E3DActor toCopyActor)
	{
		super(toCopyActor.getEngine());
        
        associatedSound3DList = new ArrayList();
        renderTree = new E3DRenderTree(getEngine());
          
        //These three must happen in the same order
        this.orientation = new E3DOrientation(toCopyActor.getOrientation()); //1
        setMesh(new E3DMesh(toCopyActor.getMesh(), this)); //2
        setSkeleton(new E3DSkeleton(toCopyActor.getSkeleton(), this)); //3
        //these three
        
        this.world = toCopyActor.getWorld();
		this.debugBoundingObjectDisplayed = toCopyActor.isDebugBoundingObjectDisplayed();
        this.debugSkeletonDisplayed = toCopyActor.isDebugSkeletonDisplayed();
        
        if(toCopyActor.getBoundingObject() != null)
            setBoundingObject(toCopyActor.getBoundingObject().onGetClone());

        renderTree.getActorHandler().rehash(this);
//        setPosition(new E3DVector3F(toCopyActor.getPosition()));
	}
	
	/**
	 * Load an actor file's geometry into this actor
	 * @param actorFilename 
	 * @throws E3DMissingEngineException
	 * @throws Exception
	 */
	public void loadActor(String actorFilename) throws E3DMissingEngineException, Exception
	{
		if(getEngine() == null)
			throw new E3DMissingEngineException();
		
		E3DActorLoader.loadActor(actorFilename, this);
	}


	
    /**
     * This will keep the listener position synchronized with this actor
     *
     */
    private void synchronizeSoundListenerPosition()
    {
        if(getEngine().getPrimarySoundViewport() != null && getEngine().getPrimarySoundViewport().getCameraActor() == this)
            getEngine().getSoundHandler().setListener(this);
    }
    
    /**
     * Used to move a sound with the actor.  Translation amt can be null if actor is just rotating
     * @param translationAmt
     */
    private void synchronizeAssociatedSound3Ds(E3DVector3F translationAmt)
    {
        int size = associatedSound3DList.size();
        E3DSound3D sound = null;
        for(int i=0; i < size; i++)
        {
            sound = (E3DSound3D)associatedSound3DList.get(i);
            sound.updateOrientation(translationAmt);
        }
    }
    
	/**
	 * Scale the actors triangles and bounding object (if it exists)
	 * @param scaleAmt
	 */
	public void scale(double scaleAmt)
	{
        if(scaleAmt == 0.0)
            return;
        
        mesh.scale(scaleAmt);

        if(boundingObject != null)
            boundingObject.scale(scaleAmt);

        if(this.skeleton != null)
        	skeleton.scale(scaleAmt);
	}	
	
	/**
	 * Rotates the actor around the aroundVec.  This rotates the forward vector, upVector as well as all the model vertices.
	 * @param angle
	 * 	Rotation angle in radians
	 * @param upVec
	 * 	Normalised central axis vector to rotate around
	 */
	public void rotate(double angle, E3DVector3F upVec)
	{
		if(angle == 0.0)
		    return;

        //Rotate actors orientation
        orientation.rotate(angle, upVec);

        //Rotate skeleton (necessary??)
        //DO NOT ROTATE SKELETON! To render, mult by orientation
        // to figure anything out (Attached stuff, etc) get local
        if(skeleton != null)
        	skeleton.rotateWithoutAttachedVertices(angle, upVec);

        //Rotate bounding objects orientation
        if(boundingObject != null)
            boundingObject.getOrientation().rotate(angle, upVec);

        //Update the sound listener position if necessary to keep synchronized with this actors position
        synchronizeSoundListenerPosition();

        mesh.setNeedingLightRecalc(true);
//        needRenderTreeRehash = true;
	}	
	
	/**
	 * Rotates around the up vector
	 * @param angle
	 * 	Angle to rotate in radians
	 */
	public void rotate(double angle)
	{
		rotate(angle, orientation.getUp());
	}
		
    /**
	 * This procedure will move the actor to the new position.  It takes into account moving all the actor vertices
	 *  as well as the actors position vec.
	 */
	public void setPosition(E3DVector3F positionVec)
	{
        if(positionVec == null || getOrientation().getPosition().equals(positionVec))
            return;
		E3DVector3F translationAmt = positionVec.subtract(orientation.getPosition()); //get how far its moved from where it started now

		translate(translationAmt);
	}
	
	/**
	 * Move all the triangles of the actor and the position vec by translationAmt.
	 */
	public void translate(E3DVector3F translationAmt)
	{
        if(translationAmt.equals(0.0, 0.0, 0.0))
            return;

        E3DVector3F startPosition, endPosition;
        startPosition = orientation.getPosition();
        endPosition = orientation.getPosition().add(translationAmt);
        
	    orientation.translate(translationAmt); //translate the position

        if(boundingObject != null)
            boundingObject.getOrientation().translate(translationAmt);
        
        if(skeleton != null)
        	skeleton.translateWithoutAttachedVertices(translationAmt);
        
        //Update the sound listener position if necessary to keep synchronized with this actors position
        synchronizeSoundListenerPosition();
        synchronizeAssociatedSound3Ds(translationAmt);

        mesh.setNeedingLightRecalc(true);
//        needRenderTreeRehash = true;
        
        //See if the portal should change
        checkSectorChangeDuringMovement(startPosition, endPosition);
	}	
	
	/**
	 * moves the actor along the vector it is facing (forward/backward)
	 */
	public void moveForward(double speed)
	{
        if(speed == 0.0)
            return;

        translate(orientation.getForward().scale(speed));
	}
	
	/**
	 * Actors are renderable, so this renders the actor directly but usually shouldn't be used.
	 * This will render the actor outside of the main rendering loop, so it won't be as efficient as the main rendering loop
	 */
	public void render()
	{
        if(mesh.isRendered())
        {
			//If the actor has moved, it will set its needingLightCalc to true, so if it is true, we need to recalc it.
			// If the sectors flag is set, that means a new light has been added or the light has moved, so we need to re-light in this case too. Do -not- set the sector's back to false, it will do that when its done
			if(mesh.isLit() && (mesh.isNeedingLightRecalc() || getSector().getMesh().isNeedingLightRecalc())) 
			{
				E3DTriangleLighter.lightTriangles((E3DSector)this.getSector(), getTriangleList(), getOrientation()); //TODO: Only do this if a light is added or moves.. This is too much processing!
				mesh.setNeedingLightRecalc(false);
			}
	
//			System.out.println(getActorID() + "\n" + getRenderTree());
			getRenderTree().render();
        }
        else //make sure that even if we don't recalc now, we know that it will need to be recalc'd the next time its displayed
        {
        	if(getSector().getMesh().isNeedingLightRecalc())
        		mesh.setNeedingLightRecalc(true);
        }
        
        if(isDebugBoundingObjectDisplayed() && boundingObject != null)
            boundingObject.render();
        
        if(isDebugSkeletonDisplayed())
        	skeleton.render();
	}
	
	/**
	 * @return Returns the boundingObject for the actor
	 */
	public IE3DBoundingObject getBoundingObject() {
		return boundingObject;
	}
	
	/**
	 * @param boundingObject Set the bounding object the actor uses.  Eventually, lists of boundingobjects 
	 * will be supported, so you'll use add
	 */
	public void setBoundingObject(IE3DBoundingObject boundingObject) {
		this.boundingObject = boundingObject;
        if(boundingObject != null)
            this.boundingObject.setParentObject(this);
	}

	/**
	 * @return Returns the world the actor is in
	 */
	public E3DWorld getWorld() {
		return world;
	}

	/**
	 * @param world Set the world the actor is in
	 */
	public void setWorld(E3DWorld world) {
		this.world = world;
	}
	
	/**
	 * @return Returns the actorID for this actor.
	 */
	public String getActorID() {
		return actorID;
	}

	/**
	 * @param actorID The actorID to set.  This can't be changed outside of the constructor
	 */
	public void setActorID(String actorID) {
		this.actorID = actorID;
	}
	
	/**
	 * Returns a list of only the unique triangle vertices in the actor (no duplicates).  The
     * vertices returned are in local coordinate space (NOT world).
     * 
	 * @param sourceActor
	 * @return List of E3DVector3F's representing unique triangle vertices in the actors mesh in local coordinate space.
	 */
	public ArrayList getUniqueVertexPositionList()
	{
        return mesh.getUniqueVertexPosList();
	}
	
	/**
	 * This gets called when an actor collides with this actor (collider or collidee.  We don't know what to do with a collision here, but anything extending this should
	 */
	abstract public void onCollisionActor(E3DCollision collision);

	/**
	 * This gets called when a particle collides with this actor
	 */
	abstract public void onCollisionSprite(E3DCollision collision);
	
	/**
	 * This method must be overriden to return a clone of the actor.
	 * This normally gets called when a specific actor in the preloadedActorMap of the world is specified in a world's map that is getting loaded
	 * What it wants is a clone of the preloaded actor returned (CLONE, not ref to the same object!).  This is exposed
	 * to you so you can modify the actor if necessary when cloning it.
	 * @param world  This is the world trying to load this actor
	 *	 
	 **/
	abstract public E3DActor onGetClone() throws Exception;

	
    /**
     * When an actor's render mode is set, all of its geometry must
     *  also be converted over to that render mode.  Having a single piece
     * of geometry in an actor different than the rest of the actor is NOT allowed.
     */
    public void setRenderMode(E3DRenderMode renderMode)
    {
    	if(renderMode.getRenderMode() == getRenderMode().getRenderMode())
    		return;
    	super.setRenderMode(renderMode);
    	
    	E3DTriangle triangle = null;
    	ArrayList triangleList = getTriangleList();
    	for(int i=0; i<triangleList.size(); i++)
		{
			triangle = (E3DTriangle)triangleList.get(i);
			triangle.setRenderMode(new E3DRenderMode(renderMode));
		}
    	
//    	needRenderTreeRehash = true;
    	
/*        E3DSortedRenderableMap textureSortedTriangleMap = mesh.getTextureSortedTriangleMap();
        if(textureSortedTriangleMap == null || textureSortedTriangleMap.entrySet() == null)
    	    return;

        E3DMesh copiedMesh = new E3DMesh(getEngine());
        
    	Iterator it = textureSortedTriangleMap.entrySet().iterator();
    	Map.Entry entry;
    	ArrayList triangleList;
    	E3DTriangle triangle;
    	while(it.hasNext())
    	{
    		entry = (Map.Entry)it.next();
    		
    		triangleList = (ArrayList)entry.getValue();
    		for(int i=0; i<triangleList.size(); i++)
    		{
    			triangle = (E3DTriangle)triangleList.get(i);
    			triangle.setRenderMode(renderMode);
                
                copiedMesh.addTriangle(triangle);
    		}
    	}
        
        setMesh(copiedMesh);*/
    }
    
    public void setBlendMode(E3DBlendMode blendMode) 
    {
    	if(blendMode.getBlendMode() == getBlendMode().getBlendMode())
    		return;
    	super.setBlendMode(blendMode);
    	
    	E3DTriangle triangle = null;
    	ArrayList triangleList = getTriangleList();
    	for(int i=0; i<triangleList.size(); i++)
		{
			triangle = (E3DTriangle)triangleList.get(i);
			triangle.setBlendMode(new E3DBlendMode(blendMode));
		}
    	
//    	needRenderTreeRehash = true;
    }
    
    /**
     * Override and return true if this actor is something other actors/objects can collide with.
     * If false, it will be ignore for other actor/object's collision detection
     */
    abstract public boolean isCollideable();
    
    
    public E3DSkeleton getSkeleton() {
        return skeleton;
    }
    
    public void setSkeleton(E3DSkeleton skeleton) {
        this.skeleton = skeleton;
        skeleton.setActor(this);
    }
    
    /**
     * Automatically updates any actor information that needs to be updated/checked every frame. (sound data, skeleton, etc).
     * This is called by the engine only.
     * @param lastFrameTimeSeconds
     */
    public void update(double lastFrameTimeSeconds)
    {
		if(mesh.isRendered() && mesh.isLit() && (mesh.isNeedingLightRecalc() || getSector().getMesh().isNeedingLightRecalc())) 
		{
			E3DTriangleLighter.lightTriangles((E3DSector)this.getSector(), getTriangleList(), getOrientation()); //TODO: Only do this if a light is added or moves.. This is too much processing!
			mesh.setNeedingLightRecalc(false);
		}
    	
        skeleton.update(lastFrameTimeSeconds);

        //If a sound has been discarded, be sure to remove it from the actors associated list
        E3DSound3D sound = null;
        for(int i=0; i <associatedSound3DList.size(); i++)
        {
            sound = (E3DSound3D)associatedSound3DList.get(i);
            if(sound.isDiscarded())
            {
                associatedSound3DList.remove(i);
                i--;
            }
        }
    }
    public boolean isDebugSkeletonDisplayed() {
        return debugSkeletonDisplayed;
    }
    public void setDebugSkeletonDisplayed(boolean debugSkeletonDisplayed) {
        this.debugSkeletonDisplayed = debugSkeletonDisplayed;
    }
    public boolean isDebugBoundingObjectDisplayed() {
        return debugBoundingObjectDisplayed;
    }
    public void setDebugBoundingObjectDisplayed(
            boolean debugBoundingObjectDisplayed) {
        this.debugBoundingObjectDisplayed = debugBoundingObjectDisplayed;
    }
    public E3DMesh getMesh() {
        return mesh;
    }
    private void setMesh(E3DMesh mesh) {
        mesh.setParentRenderable(this);
        this.mesh = mesh;
    }
    public E3DOrientation getOrientation() {
        return orientation;
    }
    
    public ArrayList getAssociatedSound3DList() {
        return associatedSound3DList;
    }
    public void addSound3DToAssociatedList(E3DSound3D sound3D)
    {
        associatedSound3DList.add(sound3D);
    }
    public void removeSound3DFromAssociatedList(E3DSound3D sound3D)
    {
        associatedSound3DList.remove(sound3D);
    }

	public ArrayList getTriangleList() {
		if(mesh != null)
			return mesh.getTriangleList();
		return null;
	}    
    
	public E3DRenderTree getRenderTree() {
		//We don't need to rehash an actor's own rendertree because
		//the nodes are already pointing to its objects.
//		if(needRenderTreeRehash)
//		{
//			renderTree.getActorHandler().rehash(this);
//			needRenderTreeRehash = false;
//		}
		return renderTree;
	}

}
