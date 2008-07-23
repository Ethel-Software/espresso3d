/*
 * Created on Oct 21, 2004
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
package espresso3d.engine.window.viewport;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.E3DExternalRenderable;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.window.E3DWindow;
import espresso3d.engine.window.viewport.image.E3DImage;
import espresso3d.engine.window.viewport.text.E3DViewportPrinter;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.actor.E3DActor;
/**
 * @author Curt
 *
 * A viewport is the region that defines where in the window to perform rendering, and what world to render in it
 * Viewports can be switched to either perspective or orthographic rendering modes.  They default
 * in a perspective mode.
 */
public class E3DViewport extends E3DRenderable{
	/**
	 * If a viewports rendermode is something other than default,
	 *  that rendermode will overwrite any other rendermode for objects in the scene.
	 * For example, if it the viewport rendermode is switched to RENDERMODE_WIREFRAME,
	 *  all objects will be rendered as a wireframe object
	 * 
	 * This defaults to RENDERMODE_DEFAULT meaning each object can have its own rendermode
	 */
	public static final int RENDERMODE_DEFAULT = -1;
	
	/**
     * Orthographic mode for the viewport.  This is used for comparing with getMode if necessary, nto for setting the mode
     */
    public static final int VIEWPORT_MODE_ORTHOGRAPHIC = 0;
    /**
     * Perspectve mode for the viewport.  This is used for comparing with getMode if necessary, not for setting the mode
     */
    public static final int VIEWPORT_MODE_PERSPECTIVE = 1;

    private int mode; //can be ortho of perspective

    private String viewportID;

    private E3DWindow window;
    
	//This is the world that the viewport will render
	private E3DWorld world;
	private E3DActor cameraActor;
	
	private int x = 0, y = 0;
	private int width, height;
	
	//perspective
	private double fovY=45.0, farClipPlane = 1000.0, nearClipPlane = 0.1;
	
	//orthographic
	private double left=10.0, right=10.0, top=10.0, bottom=10.0;
	private double orthoZoom;
	
	//When this is true, the perspective will be re set by calling gluPerspective.
	//A boolean is kept to keep this call to a minimum.
	private boolean perspectiveChanged = true; 
	
	//For projecting/unprojecting
	private FloatBuffer modelView;
	private FloatBuffer projView;

	//For the new gluLookAt (reimp)
//	private static FloatBuffer lookAtBuffer;
    
    private float[][] modelViewArray;
    private float[][] projViewArray;
    private int[] intViewArray;
    private boolean needViewArrayRecalc = true;

    
    //Keep track of where the camera was last looking.  If it has changed, we need to
    // recalc the view matrices for projection/unprojection
    private E3DVector3F lastPosition = new E3DVector3F(0.0, 0.0, 0.0);
    private E3DVector3F lastForward = new E3DVector3F(0.0, 0.0, 0.0);
    private E3DVector3F lastUp = new E3DVector3F(0.0, 0.0, 0.0);
    

	// Viewport Printer for 2D fonts and text
	private E3DViewportPrinter viewportPrinter;
	
	private HashMap imageMap;
	private E3DRenderTree renderTree;
//	private E3DSortedRenderableMap sortedImageMap;
    
    private ArrayList externalRenderables;
    /**
	 * This is used to create a perspective viewport
	 * @param engine The engine the viewport will be part of
	 * @param x The x coordinate of the bottom left corner of the viewport
	 * @param y The y coordinate of the bottom left corner of the viewport
	 * @param width Width is how many pixels from X, NOT from 0
	 * @param height Height is how many pixels from Y, NOT from 0
	 * @param viewportID
	 */
	public E3DViewport(E3DEngine engine, int x, int y, int width, int height, String viewportID){
		super(engine);
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		setViewportID(viewportID);
		setMode(VIEWPORT_MODE_PERSPECTIVE); //default perspective
		viewportPrinter = new E3DViewportPrinter(engine, this);
		
		lastPosition = new E3DVector3F(0, 0, 0);
		
		modelView = BufferUtils.createFloatBuffer(16); 
		projView = BufferUtils.createFloatBuffer(16);
//	    lookAtBuffer = BufferUtils.createFloatBuffer(16);
	    
	    //Images go on two maps
	    imageMap = new HashMap();
	    renderTree = new E3DRenderTree(engine);
//	    sortedImageMap = new E3DSortedRenderableMap();
	    
	    setRenderMode(new E3DRenderMode(engine, RENDERMODE_DEFAULT));
	    
	    this.externalRenderables = new ArrayList();
	    
	    needViewArrayRecalc = true;
	}

	/**
	 * @return Returns the height.
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height The height of the viewport.  This is not a coordinate, it is how high to make it from y.  
	 * Remember, Y gets larger as it goes up the screen
	 */
	public void setHeight(int height) {
		this.height = height;
		perspectiveChanged = true;	
		needViewArrayRecalc = true;		
	}
	/**
	 * @return Returns the width.
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width The width of the viewport.  This is not a coordinate, it is how wide to make it from x
	 */
	public void setWidth(int width) {
		this.width = width;
		perspectiveChanged = true;	
		needViewArrayRecalc = true;		
	}
	/**
	 * @return Returns the x coordinate of the bottom left corner of the viewport
	 */
	public int getX() {
		return x;
	}
	/**
	 * Sets the x coordinate of the bottom left corner of the viewport.
	 * The bottom left corner is 0, and X gets larger as it mvoes right
	 * @param x 
	 */
	public void setX(int x) {
		this.x = x;
		perspectiveChanged = true;	
		needViewArrayRecalc = true;		
	}
	/**
	 * @return Returns the y coordinate of the bottom left corner of the viewport.  
	 * The upper bottom corner is 0, and Y gets larger as it goes up the screen
	 */
	public int getY() {
		return y;
	}
	/**
	 * Sets the y coordinate of the bottom left corner of the viewport
	 * @param y 
	 */
	public void setY(int y) {
		this.y = y;
		perspectiveChanged = true;	
		needViewArrayRecalc = true;
	}
	/**
	 * @return Returns the farClipPlane.  This is how far the scene will be drawn away from the cameara.  Defaults to 100
	 */
	public double getFarClipPlane() {
		return farClipPlane;
	}
	/**
	 * Sets the far clipping plane.  The large the value, the farther away you will be able to see. However, too large of 
	 * a value can hurt performance.
	 * @param farClipPlane The farClipPlane to set.
	 */
	public void setFarClipPlane(double farClipPlane) {
		this.farClipPlane = farClipPlane;
		perspectiveChanged = true;
		needViewArrayRecalc = true;		
	}
	/**
	 * @return Returns the fovY.  This is how many degrees you can see in the Y dir
	 */
	public double getFovY() {
		return fovY;
	}
	/**
	 * Sets how many degress you can see in the Y dir.
	 * @param fovY The fovY to set.
	 */
	public void setFovY(double fovY) {
		this.fovY = fovY;
		perspectiveChanged = true;
		needViewArrayRecalc = true;		
	}

	/**
	 * @return Returns the nearClipPlane.
	 */
	public double getNearClipPlane() {
		return nearClipPlane;
	}
	/**
	 * Sets the near clipping plane.  The smaller the value, the closer you will be able to see objects
	 * to the camera.Defaults to 0.1;
	 * @param nearClipPlane The nearClipPlane to set.
	 */
	public void setNearClipPlane(double nearClipPlane) {
	    if(nearClipPlane <= 0.0) //can't be less than/equal 0.0 or project/unproject can have issues
	        this.nearClipPlane = 0.0001;
	    else
	        this.nearClipPlane = nearClipPlane;
		perspectiveChanged = true;
		needViewArrayRecalc = true;		
	}
	
	/**
	 * This will automatically be called by the engine.  Users needn't worry about this.
	 * Sets the perspective to the values stored in this viewport.  This keeps track of whether
	 * the values have changed or not, so it will keep the GL calls to a minimum (only set the perspective
	 * if something has actually changed).
	 *
	 */
	private void setPerspective()
	{
		if(perspectiveChanged)
		{
	        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
	        GL11.glLoadIdentity(); // Reset The Projection Matrix

	        if(mode == VIEWPORT_MODE_PERSPECTIVE)
	        {
		        // Calculate The Aspect Ratio Of The Window
		        GLU.gluPerspective(
		          (float)fovY,
		          (float)((float)width / (float)height),
		          (float)nearClipPlane,
		          (float)farClipPlane);
	        }
	        else if(mode == VIEWPORT_MODE_ORTHOGRAPHIC)
	            GL11.glOrtho(left*orthoZoom, right*orthoZoom, bottom*orthoZoom, top*orthoZoom, nearClipPlane, farClipPlane); //todo: this needs to center around the camera.  Need to determin whether to use X/Y, Y/Z, or X/Z to center on

        	recalcProjectionViewMatrix();
        
        	perspectiveChanged = false;
		}

	    GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Projection Matrix
		GL11.glLoadIdentity();											// Reset The Modelview Matrix
	
		GL11.glDepthMask(true); //Re-enable this.  IF particles turn this off, it has to be turned on to be able to clear the depth buffer
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear (GL11.GL_DEPTH_BUFFER_BIT);		// Clear Screen And Depth Buffer

        GLU.gluLookAt((float)cameraActor.getOrientation().getPosition().getX(), (float)cameraActor.getOrientation().getPosition().getY(), (float)cameraActor.getOrientation().getPosition().getZ(),
                ((float)(cameraActor.getOrientation().getPosition().getX()+cameraActor.getOrientation().getForward().getX())), ((float)(cameraActor.getOrientation().getPosition().getY()+cameraActor.getOrientation().getForward().getY())),((float)(cameraActor.getOrientation().getPosition().getZ()+cameraActor.getOrientation().getForward().getZ())),
                (float)cameraActor.getOrientation().getUp().getX() , (float)cameraActor.getOrientation().getUp().getY(),(float)cameraActor.getOrientation().getUp().getZ());

        //If something has changed camera wise, we need to recalculate the viewport matrix 
		// the next time something asks for a projection or unprojection
		if(!cameraActor.getOrientation().getPosition().equals(lastPosition) || !cameraActor.getOrientation().getForward().equals(lastForward) ||
		    !cameraActor.getOrientation().getUp().equals(lastUp))
		{
		    lastPosition.set(cameraActor.getOrientation().getPosition());
		    lastForward.set(cameraActor.getOrientation().getForward());
		    lastUp.set(cameraActor.getOrientation().getUp());
		    needViewArrayRecalc = true;
		}
	}
	
	public void switchToViewport()
	{
//        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glViewport(x, y, width, height);
		setPerspective();
	}
	/**
	 * @return Returns the world that the viewport needs to display
	 */
	public E3DWorld getWorld() {
		return world;
	}
	/**
	 * @param world The world to set for the viewport to display. This can be null if it shouldn't render a world.
	 */
	public void setWorld(E3DWorld world) {
		this.world = world;
	}
	
	public String getViewportID() {
		return viewportID;
	}
	
	private void setViewportID(String viewportID) {
		this.viewportID = viewportID;
	}

	/**
	 * This is the actor that the viewport uses as the camera.  If this is
     * the primarySoundViewport, the listener position will automatically
     * be set to match this actor.
	 * @param cameraActor
	 */
	public void setCameraActor(E3DActor cameraActor) {
		this.cameraActor = cameraActor;
        if(getEngine().getPrimarySoundViewport() == this)
            getEngine().getSoundHandler().setListener(cameraActor);
        
		needViewArrayRecalc = true;
	//	perspectiveChanged = true;
	}
	
	/**
	 * Gets the actor the camera is using as its camera
	 * @return
	 */
	public E3DActor getCameraActor() {
		return cameraActor;
	}
	
	/**
	 * Gets what mode the viewport is in.  Orthographic or perspective
	 * @return
	 */
    public int getMode() {
        return mode;
    }
    
    /**
     * set what mode this viewport is in (ortho or perspective)
     * @param mode
     */
    private void setMode(int mode){
        this.mode = mode;
        perspectiveChanged = true;
		needViewArrayRecalc = true;		
    }
    /**
     * Switch this viewport to orthographic rendering.  Ortho rendering doesn't give the illusion of perspective.
     * 
     * @param left How far left to render of the viewport's camera actor
     * @param right How right left to render of the viewport's camera actor
     * @param top How far above to render of the viewport's camera actor
     * @param bottom How far below to render of the viewport's camera actor
     * @param near How close of objects to render by the camera
     * @param far How far away to render objects from the camera
     */
    public void switchToOrthographicMode(double left, double right, double top, double bottom, double near, double far)
    {
        setMode(VIEWPORT_MODE_ORTHOGRAPHIC);
        setLeft(left);
        setRight(right);
        setTop(top);
        setBottom(bottom);
        setNearClipPlane(near);
        setFarClipPlane(far);
        setOrthoZoom(1);
        
        needViewArrayRecalc = true;
    }
    
    /**
     * Switch this viewport to perspective rendering.  Perspective is a standard 3d rendering that simulates what our eyes see.
     * 
     * @param fovY  The field of view angle in degress in the Y direciton. 45 is a standard value.  Too high makes things look fisheyed.
     * @param farClipPlane This specifies the maximum distance that objects can be from the camera and still be rendered
     * @param nearClipPlane Specifies the minimum distance that objects can be from the camera and still be rendered
     */
    public void switchToPerspectiveMode(double fovY, double farClipPlane, double nearClipPlane)
    {
        setMode(VIEWPORT_MODE_PERSPECTIVE);
        setFovY(fovY);
        setFarClipPlane(farClipPlane);
        setNearClipPlane(nearClipPlane);
        
        perspectiveChanged = true;	
        needViewArrayRecalc = true;
    }
    
    /**
     * set all the boundaries for the ortho view mode in one method
     * @param left
     * @param right
     * @param top
     * @param bottom
     * @param near
     * @param far
     */
    public void setOrthoBounds(double left, double right, double top, double bottom, double near, double far)
    {
        setLeft(left);
        setRight(right);
        setTop(top);
        setBottom(bottom);
        setNearClipPlane(near);
        setFarClipPlane(far);

        perspectiveChanged = true;	
        needViewArrayRecalc = true;
    }

    /**
     * This makes the current ortho view zoom out(positive # greater than 1), 
     * or zoom in (positive number > 0.0 and < 1.0).  1 will reset it to the original zoom.
     * Negative numbers and 0 are not allowed and will be converted to 1
     * The actual ortho bounds are not modified, this is a scalar multiplied to them at runtime.
     * @param zoomAmt
     */
    public void setOrthoZoom(double zoomScalar)
    {
        if(zoomScalar <= 0)
            zoomScalar = 1;
        this.orthoZoom = zoomScalar;
		perspectiveChanged = true;	
		needViewArrayRecalc = true;		
    }
    
    /**
     * Gets what the current ortho zoom factor is
     */
    public double getOrthoZoom()
    {
        return orthoZoom;
    }
    
    /**
     * How far below the camera to render in Ortho Mode
     * @return
     */
    public double getBottom() {
        return bottom;
    }
    /**
     * Set how far below the camera to render in Ortho Mode
     * @return
     */
    public void setBottom(double bottom) {
        this.bottom = bottom;
		perspectiveChanged = true;	
		needViewArrayRecalc = true;		
    }
    /**
     * How far left of the camera to render in Ortho Mode
     * @return
     */
    public double getLeft() {
        return left;
    }
    /**
     * Set how far left of the camera to render in Ortho Mode
     * @param left
     */
    public void setLeft(double left) {
        this.left = left;
		perspectiveChanged = true;	
		needViewArrayRecalc = true;		
    }
    /**
     * How far right of the camera to render in Ortho Mode
     * @param left
     */    
    public double getRight() {
        return right;
    }
    /**
     * Set how far right of the camera to render in Ortho Mode
     * @param left
     */
    public void setRight(double right) {
        this.right = right;
		perspectiveChanged = true;	
		needViewArrayRecalc = true;		
    }
    /**
     * How far above the camera to render in Ortho Mode
     * @param left
     */
    public double getTop() {
        return top;
    }
    /**
     * Set how far above the camera to render in Ortho Mode
     * @param left
     */
    public void setTop(double top) {
        this.top = top;
		perspectiveChanged = true;	
		needViewArrayRecalc = true;		
    }
   
    public E3DViewportPrinter getViewportPrinter() {
        return viewportPrinter;
    }
    public void setViewportPrinter(E3DViewportPrinter viewportPrinter) {
        this.viewportPrinter = viewportPrinter;
    }


    /****** For Projections *****/
    float[] projArray = new float[3];
    
    /**
     * Convert a view matrix float buffer into 2D float array
     * @param fb
     * @return
     */
    private static float[][] getFloatBufferAs2DArray(FloatBuffer fb)
    {
        float[] array = new float[16];
        
        fb.get(array);
        
        fb = (FloatBuffer)fb.rewind();
        float[][] fa = new float[4][4];

        int col = 0;
        for(int i=0; i < array.length; i++)
        {
            if(i != 0 && i%4 == 0)
                col++;

            fa[col][i%4] = array[i];
        }
        
        return fa;
    }    
    /**
     * Convert a viewport matrix float buffer into 2D int array
     * @param fb
     * @return
     */
//    private static int[] getIntBufferAsArray(IntBuffer ib)
//    {
//        int[] ia = new int[4];
//        
//        ib.get(ia);
//        
//        return ia;
//    }   
//    

    public void recalcProjectionViewMatrix()
    {
		projView.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projView); //TODO: only do this on perspective change
        projViewArray = getFloatBufferAs2DArray(projView);
    }

    public void recalcModelViewMatrix()
    {
		modelView.clear();
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView); //TODO: grab right out of the gluLookat
		modelViewArray = getFloatBufferAs2DArray(modelView);
    }

    public void recalcViewportMatrix()
    {
        intViewArray = new int[]{x, y, width, height}; //getIntBufferAsArray(viewport); //TODO: Use this isntead, its faster
    }
    
    private void recalcViewMatrices()
    {
        if(needViewArrayRecalc)		
        {
           recalcProjectionViewMatrix();
           recalcModelViewMatrix();
           recalcViewportMatrix();
	       needViewArrayRecalc = false;
        }
    }

    /**
     * Projecting a point converts a point in 3D worldspace to 
     * 2D screen coordinates (x, y).  Z of the projected point
     * is a value between 0 and 1 which is where its at between
     * the near and far clip plane
     * @param point
     * @return
     */
    public E3DVector3F projectPoint(E3DVector3F point3D)
    {
        recalcViewMatrices();
        
	    GLU.gluProject((float)point3D.getX(), (float)point3D.getY(), (float)point3D.getZ(),
	    		modelViewArray, projViewArray, intViewArray, projArray);
	    
	    return new E3DVector3F(projArray[0], projArray[1], projArray[2]);
    }
    
    /**
     * Projecting a point converts a point in 3D worldspace to 
     * 2D screen coordinates (x, y).  Z, or where its at between the near
     * and far clip plane is ignored
     * @param point3D
     * @return
     */
    public E3DVector2F projectPointIgnoreZ(E3DVector3F point3D)
    {
        recalcViewMatrices();
        
	    GLU.gluProject((float)point3D.getX(), (float)point3D.getY(), (float)point3D.getZ(),
	    		modelViewArray, projViewArray, intViewArray, projArray);
	    
	    return new E3DVector2F(projArray[0], projArray[1]);
    }

    /**
     * Unprojecting takes a 2D point that is in screen coordinate space (x, y)
     * and returns its 3d counterpart in world corodinate space. Z in the point
     * passed in gives the position between the near and far clip plane (value between 0 and 1)
     * 
     * @param point2D
     * @return
     */
    public E3DVector3F unprojectPoint(E3DVector3F point2D)
    {
        recalcViewMatrices();
        GLU.gluUnProject((int)point2D.getX(), (int)point2D.getY(), (float)point2D.getZ(),
	    		modelViewArray, projViewArray, intViewArray, projArray);
	
//        System.out.println("Unprojecting: " + point2D + " TO " + new E3DVector3F(projArray[0], projArray[1], projArray[2]));
        
	    return new E3DVector3F(projArray[0], projArray[1], projArray[2]);
    }

    
    public HashMap getImageMap() {
        return imageMap;
    }
//    public void setImageMap(E3DSortedRenderableMap imageMap) {
//        this.imageMap = imageMap;
//    }
    /**
     * Add a 2D image to the viewport
     * @param image
     */
    public void addImage(E3DImage image){
        image.setViewport(this);
        imageMap.put(image.getImageID(), image);
        renderTree.getImageHandler().add(image);
//        sortedImageMap.addObject(image);
    }
    /**
     * Remove a 2D image from the viewport by imageID
     * @param imageID
     */
    public void removeImage(String imageID)
    {
        if(imageMap.containsKey(imageID))
        {
        	E3DImage image = (E3DImage)imageMap.get(imageID);
        	renderTree.getImageHandler().remove(image);
//            sortedImageMap.removeObject((E3DImage)imageMap.get(imageID));
            imageMap.remove(imageID);
        }
    }
    
	public void render()
	{
		//Render viewport external renderables
		renderExternalRenderables();
		
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
			recalcModelViewMatrix(); //loaded identity, force the recalc for unprojection: TODO: Consider making a stack of these as well??
			
			//Render 2D Images in the viewport
			updateImages();
//			getRenderTree().render();
			
			//Render 2D Font text
			getViewportPrinter().render(); //Put font on top of any image
			
//            getRenderTree().render();
            
		GL11.glPopMatrix();
		recalcModelViewMatrix(); //put it back to the way it should be for everything else rendered
	}    
	
	private void renderExternalRenderables()
	{
		E3DExternalRenderable renderable = null;
		for(int i=0; i < externalRenderables.size(); i++)
		{
			renderable = (E3DExternalRenderable)externalRenderables.get(i);
			if(!renderable.update())
			{
			    externalRenderables.remove(i);
				i--;
			}
			else
			{
			    GL11.glPushMatrix(); //Each external renderable should be on its own stack so it can make GL calls and not affect other things
			    	renderable.getRenderable().render();
			    GL11.glPopMatrix();
			}
		}
	}
	
	private void updateImages()
	{
        if(imageMap == null || imageMap.size() <= 0)
			return;
		
		E3DImage image = null;
        double lastFrameTime = getEngine().getFpsTimer().getLastUpdateTimeSeconds();
        
        //Grab all the messages and sort the symbol quads by textureID for efficient rendering
        Iterator it = imageMap.entrySet().iterator();
        Map.Entry entry = null;
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            image = (E3DImage)entry.getValue();
            
    		if(!image.update(lastFrameTime))
    		{
                it.remove();
                renderTree.getImageHandler().remove(image);
            }
        }
        renderTree.render();
	}
	
	/**
	 * @return Returns the externalRenderables list.  This is private to the negine
	 */
	private ArrayList getExternalRenderables() {
		return externalRenderables;
	}
	
	/**
	 * This adds an E3DRenderable item to the ExternalRenderable list.  
	 *   All E3DRenderables in this list will be rendered after the main
	 *  rendering hierarchy is rendered (world, actors, particles, etc) but before any 2D static images or text (so the text/images will be on top of external renderables)
	 * Each item on the list will be rendered for the # of frames passed in 
	 * This is very useful for debugging (adding CMLines to the rendering loop to see direction vectors and things)
	 * It could even potentially be used to plug in your own rendering algorithm.
	 * @param renderableItem An E3DRenderable item that you want to render outside of the optimized rendering loop.
	 * @param timeoutFrames How many frames to render this renderable for or -1 for infinity
	 */
	public void addExternalRenderable(E3DRenderable renderableItem, int timeoutFrames)
	{
		getExternalRenderables().add(new E3DExternalRenderable(renderableItem, timeoutFrames));
	}	
	
	/**
	 * Remove an external renderable from the viewport even if its life hasn't expired
	 * @param renderableItem
	 */
	public void removeExternalRenderable(E3DRenderable renderableItem)
	{
	    getExternalRenderables().remove(renderableItem);
	}

    public FloatBuffer getModelView() {
        return modelView;
    }

	public E3DRenderTree getRenderTree() {
		return renderTree;
	}

	public E3DWindow getWindow() {
		return window;
	}

	public void setWindow(E3DWindow window) {
		this.window = window;
	}
	
	   public E3DQuad getFixedSizePositionedQuad(E3DVector2I min, E3DVector2I size, double nearZ) 
	    {
	    	return getFixedSizePositionedQuad(null, min, size, nearZ);
	    }

		/**
	     * Returns a quad in 3D space that is positioned correctly to be rendered in the correct position for 2D space
	     * Usually used only by the engine.
	     */
	    public E3DQuad getFixedSizePositionedQuad(E3DQuad quad, E3DVector2I position, E3DVector2I size, double nearZ) 
	    {
	        if(quad == null)
	        	quad = new E3DQuad(getEngine());
	        else
	            quad = new E3DQuad(quad);

	        E3DVector3F unprojA, unprojB, unprojC, unprojD;
	        
	        //bottom left
	        unprojA = unprojectPoint(new E3DVector3F(position.getX(), position.getY(), nearZ));
	        //top left
	        unprojB = unprojectPoint(new E3DVector3F(position.getX(), position.getY()+size.getY(), nearZ));
	        //top right
	        unprojC = unprojectPoint(new E3DVector3F(position.getX()+size.getX(), position.getY()+size.getY(), nearZ));
	        //bottom right
	        unprojD = unprojectPoint(new E3DVector3F(position.getX()+size.getX(), position.getY(), nearZ));
	        
	        quad.setVertexPos(unprojA, unprojB, unprojC, unprojD);

	        return quad;
	    }
	    
	    /**
	     * @param viewport Viewport this takes place in
	     * @param position x,y position for the quad to start in the viewport
	     * @param sizePercentage x is width, y is height.  Both are whole percentages : ie 50.00 is 50%
	     * @return
	     *  Quad that will appear in 3D space what how you specified it in 2D space
	     */
	    public E3DQuad getVariableSizePositionedQuad(E3DVector2I position, E3DVector2F sizePercentage, double nearZ) 
	    {
	    	return getVariableSizePositionedQuad(null, position, sizePercentage, nearZ);
	    }
	    /**
	     * 
	     * @param viewport Viewport this takes place in
	     * @param quad A quad if we want to make a copy of all its values (texture coords, etc), or null to get a new quad
	     * @param position x,y position for the quad to start in the viewport
	     * @param sizePercentage x is width, y is height.  Both are whole percentages : ie 50.00 is 50%
	     * @return
	     *  Quad that will appear in 3D space what how you specified it in 2D space
	     */
	    public E3DQuad getVariableSizePositionedQuad(E3DQuad quad, E3DVector2I position, E3DVector2F sizePercentage, double nearZ) 
	    {
	    	  if(quad == null)
	          	quad = new E3DQuad(getEngine());
	          else
	              quad = new E3DQuad(quad);
		
		    
		    E3DVector3F unprojA, unprojB, unprojC, unprojD;
		    
		    
		    //bottom left
		    unprojA = unprojectPoint(new E3DVector3F(position.getX(), position.getY(), nearZ));
		    //top left
		    unprojB = unprojectPoint(new E3DVector3F(position.getX(), position.getY()+(getHeight() * sizePercentage.getY() * 0.01), nearZ));
		    //top right
		    unprojC = unprojectPoint(new E3DVector3F(position.getX()+(getWidth() * sizePercentage.getY() *.01), position.getY()+(getHeight() * sizePercentage.getY() * 0.01), nearZ));
		    //bottom right
		    unprojD = unprojectPoint(new E3DVector3F(position.getX()+(getWidth() * sizePercentage.getY() *.01), position.getY(), nearZ));
		    
		    quad.setVertexPos(unprojD, unprojC, unprojB, unprojA); //reverse so backface culling works

		    return quad;    
	    }
}
