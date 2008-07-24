/*
 * Created on Jul 18, 2004
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
package espresso3d.engine.renderer;


import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBTextureEnvCombine;
import org.lwjgl.opengl.GL11;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.logger.E3DEngineLogger;
import espresso3d.engine.lowlevel.geometry.E3DLine;
import espresso3d.engine.lowlevel.geometry.E3DPoint;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.renderer.particle.E3DParticleRendererARBPointSprite;
import espresso3d.engine.renderer.particle.E3DParticleRendererLegacy;
import espresso3d.engine.renderer.particle.base.IE3DParticleRenderer;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.world.sector.particle.E3DParticle;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;

/**
 * @author espresso3d
 *
 * This performs the raw, lowlevel geometry rendering.  
 * Usually only called by the engine itself.
 */
public class E3DGeometryRenderer extends E3DEngineItem
{
    public static final int MAX_BUFFER_SIZE = 120000;
    
//    private static final int RENDERMODE_POINT = 0;
//    private static final int RENDERMODE_LINE = 1;
//    private static final int RENDERMODE_SOLID = 6;
//    private static final int RENDERMODE_SOLIDTEXTURED = 2;
//    private static final int RENDERMODE_SOLIDNONTEXTURED = 3;
//    private static final int RENDERMODE_BLENDEDTEXTURED = 4;
//    private static final int RENDERMODE_BLENDEDNONTEXTURED = 5;
    
    public FloatBuffer vertexBuffer;
    public FloatBuffer texCoordBuffer ;
    public FloatBuffer colorBuffer;
    public FloatBuffer texCoordDetail0Buffer ;
    public FloatBuffer texCoordDetail1Buffer ;
    
    private IE3DParticleRenderer particleRenderer = null;
    
    public E3DGeometryRenderer(E3DEngine engine)
    {
        super(engine);
        
        engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Initializing geometry renderer");
        
        vertexBuffer = BufferUtils.createFloatBuffer(MAX_BUFFER_SIZE);
        texCoordBuffer =  BufferUtils.createFloatBuffer(MAX_BUFFER_SIZE);
        colorBuffer = BufferUtils.createFloatBuffer(MAX_BUFFER_SIZE);
        texCoordDetail0Buffer =  BufferUtils.createFloatBuffer(MAX_BUFFER_SIZE);
        texCoordDetail1Buffer =  BufferUtils.createFloatBuffer(MAX_BUFFER_SIZE);

        //Initialize correct particle renderer
        if(getEngine().getFeatureChecker().isArbPointSpriteEnabled())
            particleRenderer = new E3DParticleRendererARBPointSprite(getEngine());
        else
            particleRenderer = new E3DParticleRendererLegacy(getEngine());
    }
    
    /**
     * Anytime after engine initialization, ARB point sprite rendering can be disabled.  Once disabled, it cannot be re-enabled
     * until the next engine initialization.
     * 
     * This is typically not necessary, but is available in case point sprites aren't giving the desired rendering effects.
     *
     */
    public void disableArbPointSprite()
    {
        particleRenderer = new E3DParticleRendererLegacy(getEngine());
    }
    
    
    
    /**
     * Automatically render an E3DRenderable
     * 
     * @param renderableItem
     */
    public  void renderItem(E3DRenderable renderableItem)
	{
		renderableItem.render();
	}
	
	/**
	 * Sets the OGL environment up to render points
	 * 
	 * Disables textures, and vertex arrays
	 *
	 */
	public  void initPointRenderer()
	{
	//    if(currentRenderMode == RENDERMODE_POINT)
   //         return;
        
        /*	    GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do
        GL11.glHint (GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);			// Set Perspective Calculations To Most Accurate
*/
        disableAllTextureUnits();        
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);

	}

	/**
	 * Sets OGL environment up to render lines
	 * 
	 * Disables textures, and vertex arrays
	 *
	 */
//	public void initLineRenderer()
//	{
//	//    if(currentRenderMode == RENDERMODE_LINE)
//    //        return;
//        
//        GL11.glDepthMask(true);
//        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
//        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do
//        GL11.glHint (GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);			// Set Perspective Calculations To Most Accurate
//	    
//        disableAllTextureUnits();
//        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
//		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
//        
////        currentRenderMode = RENDERMODE_LINE;
//	}

	/*********** NEW FOR RENDER TREE **********/
	public void initSmoothing()
	{
        GL11.glHint (GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);			// Set Perspective Calculations To Most Accurate
		GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);					// Really Nice Point Smoothing	    
	}
	
	public void initSolidAndLineRendering()
	{
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do
        GL11.glHint (GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);			// Set Perspective Calculations To Most Accurate
	    
        disableAllTextureUnits();
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
	}
	
	public void initTexturedRendering()
	{
//	    GL11.glDepthMask(true);
//        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
  //      GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do
        GL11.glDisable(GL11.GL_BLEND);

        ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
		GL11.glEnable(GL11.GL_TEXTURE_2D);		
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);	
	
	}

	
	
	public void initBlendedRendering()
	{
	    GL11.glDepthMask(false);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL ); // The Type Of Depth Testing To Do
		GL11.glEnable(GL11.GL_BLEND);							// Enable Blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);					// Type Of Blending To Perform
//		GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);					// Really Nice Point Smoothing	    
	}
	
	public void disableBlendedRendering()
	{
	    GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do
//        GL11.glHint (GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);			// Set Perspective Calculations To Most Accurate
        GL11.glDisable(GL11.GL_BLEND);
	}	
	/**
	 * Texture initialization, make sure to init each texture unit you will use after this
	 */
	public void initTexturing()
	{
		disableAllTextureUnits();
	}
	
	public void initTextureUnit(int unitNum, int glTextureID)
	{
		if(unitNum == 0)
			ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
		else if(unitNum == 1)
			ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE1_ARB);
		else if(unitNum == 2)
			ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE2_ARB);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureID); 
//		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
//		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

//        ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	

	
	/*********************************/

    public void setupTextureUnits(int glTextureID, int detail0TextureID, int detail1TextureID)
    {
        ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
        if(glTextureID != -1)
        {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureID); 
/*            if(detail0TextureID != -1)
            {
                GL11.glTexEnvf (GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, ARBTextureEnvCombine.GL_COMBINE_ARB);
                GL11.glTexEnvf (GL11.GL_TEXTURE_ENV, ARBTextureEnvCombine.GL_COMBINE_RGB_ARB, GL11.GL_REPLACE);
            }
            else
            {*/
                GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
//            }
        }
        else
            GL11.glDisable(GL11.GL_TEXTURE_2D);

        if(getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() < 2)
            return;
	    ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE1_ARB);
	    if(detail0TextureID != -1)
	    {
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, detail0TextureID); 
            GL11.glTexEnvf (GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, ARBTextureEnvCombine.GL_COMBINE_ARB);
            GL11.glTexEnvf (GL11.GL_TEXTURE_ENV, ARBTextureEnvCombine.GL_COMBINE_RGB_ARB, GL11.GL_ADD);        
        }
        else
            GL11.glDisable(GL11.GL_TEXTURE_2D);

        if(getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() < 3)
            return;
	    ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE2_ARB);
        if(detail1TextureID != -1)
        {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, detail1TextureID); 
            GL11.glTexEnvf (GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, ARBTextureEnvCombine.GL_COMBINE_ARB);
            GL11.glTexEnvf (GL11.GL_TEXTURE_ENV, ARBTextureEnvCombine.GL_COMBINE_RGB_ARB, GL11.GL_ADD);
        }
        else
            GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public  void disableAllTextureUnits()
    {
        ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
//		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
//		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
//		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        if(getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() < 2)
            return;
        ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE1_ARB);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
//		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
//		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
//		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        
        if(getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() < 3)
            return;
        ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE2_ARB);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
//		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
//		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
//		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

    }
    
	public  void renderPoint(E3DPoint point)
	{
		E3DVector4F color = point.getColor();
		E3DVector3F pos = point.getPos();

		GL11.glPointSize(100f);//(float)point.getSize());
		GL11.glBegin(GL11.GL_POINTS);// Begin Drawing The Line
			GL11.glColor4f((float)color.getA(), (float)color.getB(), (float)color.getC(), (float)color.getD()); //default all white color for the base polygon
		
			//Draw vertices
			GL11.glVertex3f((float)(pos.getX()), 
							(float)(pos.getY()), 
							(float)(pos.getZ()));
		GL11.glEnd();			
	}
	
	public  void renderPointList(ArrayList pointList)
	{
		E3DPoint point = null;
		
		for(int i=0; pointList != null && i < pointList.size(); i++)
		{
			point = (E3DPoint)pointList.get(i);			
			renderPoint(point);
		}
	}


	public  void renderLine(E3DLine line)
	{
		E3DVector3F startColor = line.getStartColor();
		E3DVector3F endColor = line.getEndColor();
		E3DVector3F startPos = line.getStartPos();
		E3DVector3F endPos = line.getEndPos();

		GL11.glBegin(GL11.GL_LINES);								// Begin Drawing The Line
			GL11.glColor4f((float)startColor.getX(), (float)startColor.getY(), (float)startColor.getZ(), 1.0F); //default all white color for the base polygon
			//Draw vertices
			GL11.glVertex3f((float)(startPos.getX()), 
							(float)(startPos.getY()), 
							(float)(startPos.getZ()));

			GL11.glColor4f((float)endColor.getX(), (float)endColor.getY(), (float)endColor.getZ(), 1.0F); //default all white color for the base polygon
			GL11.glVertex3f((float)(endPos.getX()), 
							(float)(endPos.getY()), 
							(float)(endPos.getZ()));
		GL11.glEnd();			

	}
	
	public  void renderLineList(ArrayList lineList)
	{
		E3DLine line = null;
		
		if(lineList == null)
		    return;
		
		int listSize = lineList.size();
		if(listSize <= 0)
		    return;
		
		int bufferSize = listSize * 3;
        if(bufferSize > MAX_BUFFER_SIZE)
        {
            int maxLength = (int)(MAX_BUFFER_SIZE / 3.0);
            
            renderLineList((ArrayList)lineList.subList(maxLength, lineList.size()));
            lineList = (ArrayList)lineList.subList(0, maxLength);
            
        }
//		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(bufferSize);
//		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(bufferSize);
		vertexBuffer.clear();
        colorBuffer.clear();
        
		for(int i=0; i < listSize; i++)
		{
			line = (E3DLine)lineList.get(i);
			
            line.appendFloatVertexBuffer(vertexBuffer);
            line.appendFloatVertexColorBuffer(colorBuffer);
		}

		// Put arrays in float buffers and REWIND them (very important).
		vertexBuffer.rewind();
		colorBuffer.rewind();
		
		// Send float buffers to LWJGL.
		GL11.glVertexPointer(3, 0, vertexBuffer);
		GL11.glColorPointer(4, 0, colorBuffer);

//        vertexBuffer.clear();
//        colorBuffer.clear();
		GL11.glDrawArrays(GL11.GL_LINES, 0, listSize * 2);
	}
	
	public  void renderTriangle(E3DEngine engine, E3DTriangle triangle, int renderMode)
	{
		int viewportRenderMode = engine.getCurrentViewport().getRenderMode().getRenderMode();
	    if(viewportRenderMode == E3DRenderMode.RENDERMODE_WIREFRAME || 
	    	(viewportRenderMode == E3DViewport.RENDERMODE_DEFAULT && renderMode == E3DRenderMode.RENDERMODE_WIREFRAME))
	        renderWireframeTriangle(triangle);
	    else if(viewportRenderMode == E3DRenderMode.RENDERMODE_SOLID ||
	        (viewportRenderMode == E3DViewport.RENDERMODE_DEFAULT && renderMode == E3DRenderMode.RENDERMODE_SOLID))
	        renderSolidTriangle(triangle);
	    else //textured
	    	renderTexturedTriangle(triangle);
	}

	private void renderSolidTriangle(E3DTriangle triangle)
	{
        disableAllTextureUnits();

        GL11.glBegin (GL11.GL_TRIANGLES);	// Begin Drawing Triangles
 		    GL11.glColor4f((float)triangle.getVertexColorA().getA(), (float)triangle.getVertexColorA().getB(),
                            (float)triangle.getVertexColorA().getC(), (float)triangle.getVertexColorA().getD());
            GL11.glVertex3f((float)(triangle.getVertexPosA().getX()), 
							(float)(triangle.getVertexPosA().getY()), 
							(float)(triangle.getVertexPosA().getZ()));
	
		    GL11.glColor4f((float)triangle.getVertexColorB().getA(), (float)triangle.getVertexColorB().getB(),
                    (float)triangle.getVertexColorB().getC(), (float)triangle.getVertexColorB().getD());

            GL11.glVertex3f((float)(triangle.getVertexPosB().getX()), 
							(float)(triangle.getVertexPosB().getY()), 
							(float)(triangle.getVertexPosB().getZ()));
	
            GL11.glColor4f((float)triangle.getVertexColorC().getA(), (float)triangle.getVertexColorC().getB(),
                    (float)triangle.getVertexColorC().getC(), (float)triangle.getVertexColorC().getD());
			GL11.glVertex3f((float)(triangle.getVertexPosC().getX()), 
							(float)(triangle.getVertexPosC().getY()), 
							(float)(triangle.getVertexPosC().getZ()));
		GL11.glEnd();							
	}

	
	private  void renderTexturedTriangle(E3DTriangle triangle)
	{
		int detail0TextureID = -1;
        int detail1TextureID = -1;

        if(triangle.isTextureDetail0Available())
            detail0TextureID = triangle.getTextureDetail0().getGlTextureID();
        if(triangle.isTextureDetail1Available())
            detail1TextureID = triangle.getTextureDetail1().getGlTextureID();

		//Draw vertices
		ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
		
		boolean twoSupported = getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() >= 2;
		boolean threeSupported = getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() >= 3;
		
		GL11.glBegin (GL11.GL_TRIANGLES);	// Begin Drawing Triangles
 			ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)triangle.getTextureCoordA().getX(), (float)triangle.getTextureCoordA().getY());
			if(twoSupported && detail0TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE1_ARB, (float)triangle.getTextureCoordDetail0A().getX(), (float)triangle.getTextureCoordDetail0A().getY());
			if(threeSupported && detail1TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE2_ARB, (float)triangle.getTextureCoordDetail1A().getX(), (float)triangle.getTextureCoordDetail1A().getY());
                
            GL11.glColor4f((float)triangle.getVertexColorA().getA(), (float)triangle.getVertexColorA().getB(),
                            (float)triangle.getVertexColorA().getC(), (float)triangle.getVertexColorA().getD());
            GL11.glVertex3f((float)(triangle.getVertexPosA().getX()), 
							(float)(triangle.getVertexPosA().getY()), 
							(float)(triangle.getVertexPosA().getZ()));
	
			ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)triangle.getTextureCoordB().getX(), (float)triangle.getTextureCoordB().getY());
            if(twoSupported && detail0TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE1_ARB, (float)triangle.getTextureCoordDetail0B().getX(), (float)triangle.getTextureCoordDetail0B().getY());
            if(threeSupported && detail1TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE2_ARB, (float)triangle.getTextureCoordDetail1B().getX(), (float)triangle.getTextureCoordDetail1B().getY());
            GL11.glColor4f((float)triangle.getVertexColorB().getA(), (float)triangle.getVertexColorB().getB(),
                    (float)triangle.getVertexColorB().getC(), (float)triangle.getVertexColorB().getD());

            GL11.glVertex3f((float)(triangle.getVertexPosB().getX()), 
							(float)(triangle.getVertexPosB().getY()), 
							(float)(triangle.getVertexPosB().getZ()));
	
            ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)triangle.getTextureCoordC().getX(), (float)triangle.getTextureCoordC().getY());
            if(twoSupported && detail0TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE1_ARB, (float)triangle.getTextureCoordDetail0C().getX(), (float)triangle.getTextureCoordDetail0C().getY());
            if(threeSupported && detail1TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE2_ARB, (float)triangle.getTextureCoordDetail1C().getX(), (float)triangle.getTextureCoordDetail1C().getY());
            GL11.glColor4f((float)triangle.getVertexColorC().getA(), (float)triangle.getVertexColorC().getB(),
                    (float)triangle.getVertexColorC().getC(), (float)triangle.getVertexColorC().getD());
			GL11.glVertex3f((float)(triangle.getVertexPosC().getX()), 
							(float)(triangle.getVertexPosC().getY()), 
							(float)(triangle.getVertexPosC().getZ()));
		GL11.glEnd();							
	}

	private  void renderWireframeTriangle(E3DTriangle triangle)
	{
//        int glTextureID = -1;
//        int detail0TextureID = -1;
//        int detail1TextureID = -1;
//
//        if(triangle.isTextureAvailable())
//            glTextureID = triangle.getTexture().getGlTextureID();
//        if(triangle.isTextureDetail0Available())
//            detail0TextureID = triangle.getTextureDetail0().getGlTextureID();
//        if(triangle.isTextureDetail1Available())
//            detail1TextureID = triangle.getTextureDetail1().getGlTextureID();
//        
//        setupTextureUnits(glTextureID, detail0TextureID, detail1TextureID);

		//Draw vertices
//		ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
//		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); //default all white color for the base polygon

		GL11.glBegin (GL11.GL_LINE_LOOP);	// Begin Drawing Triangles
//			ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)triangle.getTextureCoordA().getX(), (float)triangle.getTextureCoordA().getY());
            GL11.glColor4f((float)triangle.getVertexColorA().getA(), (float)triangle.getVertexColorA().getB(),
                    (float)triangle.getVertexColorA().getC(), (float)triangle.getVertexColorA().getD());
            GL11.glVertex3f((float)(triangle.getVertexPosA().getX()), 
							(float)(triangle.getVertexPosA().getY()), 
							(float)(triangle.getVertexPosA().getZ()));
	
//			ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)triangle.getTextureCoordB().getX(), (float)triangle.getTextureCoordB().getY());
            GL11.glColor4f((float)triangle.getVertexColorA().getA(), (float)triangle.getVertexColorA().getB(),
                    (float)triangle.getVertexColorA().getC(), (float)triangle.getVertexColorA().getD());
			GL11.glVertex3f((float)(triangle.getVertexPosB().getX()), 
							(float)(triangle.getVertexPosB().getY()), 
							(float)(triangle.getVertexPosB().getZ()));
	
//			ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)triangle.getTextureCoordC().getX(), (float)triangle.getTextureCoordC().getY());
            GL11.glColor4f((float)triangle.getVertexColorA().getA(), (float)triangle.getVertexColorA().getB(),
                    (float)triangle.getVertexColorA().getC(), (float)triangle.getVertexColorA().getD());
			GL11.glVertex3f((float)(triangle.getVertexPosC().getX()), 
							(float)(triangle.getVertexPosC().getY()), 
							(float)(triangle.getVertexPosC().getZ()));
		GL11.glEnd();							
	}

	
	public  void renderTriangleList(E3DEngine engine, ArrayList triangleList, int renderMode)
	{
		int viewportRenderMode = engine.getCurrentViewport().getRenderMode().getRenderMode();
	    if(viewportRenderMode == E3DRenderMode.RENDERMODE_WIREFRAME || 
	    	(viewportRenderMode == E3DViewport.RENDERMODE_DEFAULT && renderMode == E3DRenderMode.RENDERMODE_WIREFRAME))
	        renderWireframeTriangleList(triangleList);
	    else if(viewportRenderMode == E3DRenderMode.RENDERMODE_SOLID ||
	        (viewportRenderMode == E3DViewport.RENDERMODE_DEFAULT && renderMode == E3DRenderMode.RENDERMODE_SOLID))
	        renderSolidTriangleList(triangleList);
	    else //textured
	    	renderTexturedTriangleList(triangleList);
	}


	/**
	 * This will render a list of triangles using vertexarrays.  It assumes that the list
	 * of triangles all share the same texture!
	 * @param triangleList List of triangles to render all containing the same texture
	 */
	private  void renderSolidTriangleList(List triangleList)
	{
        if(triangleList == null)
            return;

        E3DTriangle triangle = null;
		List renderList = null;
		int listSize = triangleList.size();
		if(listSize <= 0)
		    return;
		
        //Get vertex buffers
        int bufferSize = listSize * 12;
        if(bufferSize > MAX_BUFFER_SIZE)
        {
            int maxLength = (int)(MAX_BUFFER_SIZE / 12.0);
            
            renderSolidTriangleList(triangleList.subList(maxLength, triangleList.size()));
            renderList = triangleList.subList(0, maxLength);
        }
        else
            renderList = triangleList;

        vertexBuffer.clear();
        colorBuffer.clear();
        
        for(int i=0; i<renderList.size(); i++)
		{
			triangle = (E3DTriangle)renderList.get(i);
			
            triangle.appendVertexBuffer(vertexBuffer);
			triangle.appendVertexColorBuffer(colorBuffer);
		}

        disableAllTextureUnits();
        
		// Put arrays in float buffers and REWIND them (very important).
		vertexBuffer.rewind();
        colorBuffer.rewind();

		// Send float buffers to LWJGL.
        GL11.glVertexPointer(3, 0, vertexBuffer);
		GL11.glColorPointer(4, 0, colorBuffer);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, renderList.size() * 3);
	}
	
	/**
	 * This will render a list of triangles using vertexarrays.  It assumes that the list
	 * of triangles all share the same texture!
	 * @param triangleList List of triangles to render all containing the same texture
	 */
	private  void renderTexturedTriangleList(List triangleList)
	{
        if(triangleList == null)
            return;

        E3DTriangle triangle = null;
		List renderList = null;
		int listSize = triangleList.size();
		if(listSize <= 0)
		    return;
		
        //Get vertex buffers
        int bufferSize = listSize * 12;
        if(bufferSize > MAX_BUFFER_SIZE)
        {
            int maxLength = (int)(MAX_BUFFER_SIZE / 12.0);
            
            renderTexturedTriangleList(triangleList.subList(maxLength, triangleList.size()));
            renderList = triangleList.subList(0, maxLength);
        }
        else
            renderList = triangleList;

        vertexBuffer.clear();
        colorBuffer.clear();
        texCoordBuffer.clear();
        texCoordDetail0Buffer.clear();
        texCoordDetail1Buffer.clear();
        
        for(int i=0; i<renderList.size(); i++)
		{
			triangle = (E3DTriangle)renderList.get(i);
			
            triangle.appendVertexBuffer(vertexBuffer);
            triangle.appendTexCoordBuffer(texCoordBuffer);
			triangle.appendVertexColorBuffer(colorBuffer);
            
            triangle.appendTexCoordDetail0Buffer(texCoordDetail0Buffer);
            triangle.appendTexCoordDetail1Buffer(texCoordDetail1Buffer);
		}

        int detail0TextureID = -1;
        int detail1TextureID = -1;

        if(triangle.isTextureDetail0Available())
            detail0TextureID = triangle.getTextureDetail0().getGlTextureID();
        if(triangle.isTextureDetail1Available())
            detail1TextureID = triangle.getTextureDetail1().getGlTextureID();
        
		// Put arrays in float buffers and REWIND them (very important).
		vertexBuffer.rewind();
		texCoordBuffer.rewind();
		texCoordDetail0Buffer.rewind();
        texCoordDetail1Buffer.rewind();
        colorBuffer.rewind();

		// Send float buffers to LWJGL.
        GL11.glVertexPointer(3, 0, vertexBuffer);
		GL11.glColorPointer(4, 0, colorBuffer);

        //Draw vertices
        ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
 //       GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glTexCoordPointer(2, 0,texCoordBuffer);

        if(detail0TextureID != -1 && getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() >= 2 )
        {
            ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE1_ARB);
  //          GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glTexCoordPointer(2, 0,texCoordDetail0Buffer);

            if(detail1TextureID != -1 && getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() >= 3)
            {
                ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE2_ARB);
    //            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                GL11.glTexCoordPointer(2, 0,texCoordDetail1Buffer);
            }
        }
        
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, renderList.size() * 3);
	}
	
	/**
	 * This will render a list of triangles using vertexarrays.  It assumes that the list
	 * of triangles all share the same texture!
	 * @param triangleList List of triangles to render all containing the same texture
	 */
	private  void renderWireframeTriangleList(List triangleList)
	{
		E3DTriangle triangle = null;
		List renderList = null;
        
		if(triangleList == null)
		    return;
		
		int listSize = triangleList.size();
		if(listSize <= 0)
		    return;
		
		int bufferSize = listSize * 24;
        if(bufferSize > MAX_BUFFER_SIZE)
        {
            int maxLength = (int)(MAX_BUFFER_SIZE / 24.0);
            
            renderWireframeTriangleList(triangleList.subList(maxLength, triangleList.size()));
            renderList = triangleList.subList(0, maxLength);
        }
        else
            renderList = triangleList;

        vertexBuffer.clear();
        colorBuffer.clear();
        for(int i=0; i<renderList.size(); i++)
		{
			triangle = (E3DTriangle)renderList.get(i);
			
            triangle.appendVertexBuffer(vertexBuffer);
            triangle.appendVertexBuffer(vertexBuffer);
            
            triangle.appendVertexColorBuffer(colorBuffer);
            triangle.appendVertexColorBuffer(colorBuffer);
		}

        disableAllTextureUnits();

		// Put arrays in float buffers and REWIND them (very important).
		vertexBuffer.rewind();
		colorBuffer.rewind();

		// Send float buffers to LWJGL.
		GL11.glVertexPointer(3, 0, vertexBuffer);
		GL11.glColorPointer(4, 0, colorBuffer);

		GL11.glDrawArrays(GL11.GL_LINES, 0, renderList.size() * 6);
	}
	
	public  void renderQuad(E3DQuad quad, int renderMode)
	{
        E3DEngine engine = quad.getEngine();
		int viewportRenderMode = engine.getCurrentViewport().getRenderMode().getRenderMode();
	    if(viewportRenderMode == E3DRenderMode.RENDERMODE_WIREFRAME || 
	    	(viewportRenderMode == E3DViewport.RENDERMODE_DEFAULT && renderMode == E3DRenderMode.RENDERMODE_WIREFRAME))
	        renderWireframeQuad(quad);
	    else if(viewportRenderMode == E3DRenderMode.RENDERMODE_SOLID ||
		        (viewportRenderMode == E3DViewport.RENDERMODE_DEFAULT && renderMode == E3DRenderMode.RENDERMODE_SOLID))
		    renderSolidQuad(quad);
	    else //textured
		    renderTexturedQuad(quad);

	
	}
	
	/**
	 * Render a quad normally (solid or textured)
	 * @param quad
	 */
	private  void renderSolidQuad(E3DQuad quad)
	{
		//Draw vertices
		GL11.glBegin (GL11.GL_QUADS);	// Begin Drawing Triangles
            GL11.glColor4f((float)quad.getVertexColorA().getA(), (float)quad.getVertexColorA().getB(), (float)quad.getVertexColorA().getC(), (float)quad.getVertexColorA().getD());
            GL11.glVertex3f((float)(quad.getVertexPosA().getX()), 
							(float)(quad.getVertexPosA().getY()), 
							(float)(quad.getVertexPosA().getZ()));
	
            GL11.glColor4f((float)quad.getVertexColorB().getA(), (float)quad.getVertexColorB().getB(), (float)quad.getVertexColorB().getC(), (float)quad.getVertexColorB().getD());
			GL11.glVertex3f((float)(quad.getVertexPosB().getX()), 
							(float)(quad.getVertexPosB().getY()), 
							(float)(quad.getVertexPosB().getZ()));
			
            GL11.glColor4f((float)quad.getVertexColorC().getA(), (float)quad.getVertexColorC().getB(), (float)quad.getVertexColorC().getC(), (float)quad.getVertexColorC().getD());
            GL11.glVertex3f((float)(quad.getVertexPosC().getX()), 
							(float)(quad.getVertexPosC().getY()), 
							(float)(quad.getVertexPosC().getZ()));

            GL11.glColor4f((float)quad.getVertexColorD().getA(), (float)quad.getVertexColorD().getB(), (float)quad.getVertexColorD().getC(), (float)quad.getVertexColorD().getD());
			GL11.glVertex3f((float)(quad.getVertexPosD().getX()), 
							(float)(quad.getVertexPosD().getY()), 
							(float)(quad.getVertexPosD().getZ()));	
		GL11.glEnd();							
	}

	/**
	 * Render a quad normally (solid or textured)
	 * @param quad
	 */
	private  void renderTexturedQuad(E3DQuad quad)
	{
        int detail0TextureID = -1;
        int detail1TextureID = -1;

        if(quad.isTextureDetail0Available())
            detail0TextureID = quad.getTextureDetail0().getGlTextureID();
        if(quad.isTextureDetail1Available())
            detail1TextureID = quad.getTextureDetail1().getGlTextureID();
        
		//Draw vertices
		ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);

		boolean twoSupported = getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() >= 2;
		boolean threeSupported = getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() >= 3;
		GL11.glBegin (GL11.GL_QUADS);	// Begin Drawing Triangles
            ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)quad.getTextureCoordA().getX(), (float)quad.getTextureCoordA().getY());
            if(twoSupported && detail0TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE1_ARB, (float)quad.getTextureCoordDetail0A().getX(), (float)quad.getTextureCoordDetail0A().getY());
            if(threeSupported && detail1TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE2_ARB, (float)quad.getTextureCoordDetail1A().getX(), (float)quad.getTextureCoordDetail1A().getY());
            GL11.glColor4f((float)quad.getVertexColorA().getA(), (float)quad.getVertexColorA().getB(), (float)quad.getVertexColorA().getC(), (float)quad.getVertexColorA().getD());
            GL11.glVertex3f((float)(quad.getVertexPosA().getX()), 
							(float)(quad.getVertexPosA().getY()), 
							(float)(quad.getVertexPosA().getZ()));
	
            ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)quad.getTextureCoordB().getX(), (float)quad.getTextureCoordB().getY());
            if(twoSupported && detail0TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE1_ARB, (float)quad.getTextureCoordDetail0B().getX(), (float)quad.getTextureCoordDetail0B().getY());
            if(threeSupported && detail1TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE2_ARB, (float)quad.getTextureCoordDetail1B().getX(), (float)quad.getTextureCoordDetail1B().getY());
            GL11.glColor4f((float)quad.getVertexColorB().getA(), (float)quad.getVertexColorB().getB(), (float)quad.getVertexColorB().getC(), (float)quad.getVertexColorB().getD());
			GL11.glVertex3f((float)(quad.getVertexPosB().getX()), 
							(float)(quad.getVertexPosB().getY()), 
							(float)(quad.getVertexPosB().getZ()));
			
            ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)quad.getTextureCoordC().getX(), (float)quad.getTextureCoordC().getY());
            if(twoSupported && detail0TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE1_ARB, (float)quad.getTextureCoordDetail0C().getX(), (float)quad.getTextureCoordDetail0C().getY());
            if(threeSupported && detail1TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE2_ARB, (float)quad.getTextureCoordDetail1C().getX(), (float)quad.getTextureCoordDetail1C().getY());
            GL11.glColor4f((float)quad.getVertexColorC().getA(), (float)quad.getVertexColorC().getB(), (float)quad.getVertexColorC().getC(), (float)quad.getVertexColorC().getD());
            GL11.glVertex3f((float)(quad.getVertexPosC().getX()), 
							(float)(quad.getVertexPosC().getY()), 
							(float)(quad.getVertexPosC().getZ()));

            ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)quad.getTextureCoordD().getX(), (float)quad.getTextureCoordD().getY());
            if(twoSupported && detail0TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE1_ARB, (float)quad.getTextureCoordDetail0D().getX(), (float)quad.getTextureCoordDetail0D().getY());
            if(threeSupported && detail1TextureID != -1)
                ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE2_ARB, (float)quad.getTextureCoordDetail1D().getX(), (float)quad.getTextureCoordDetail1D().getY());
            GL11.glColor4f((float)quad.getVertexColorD().getA(), (float)quad.getVertexColorD().getB(), (float)quad.getVertexColorD().getC(), (float)quad.getVertexColorD().getD());
			GL11.glVertex3f((float)(quad.getVertexPosD().getX()), 
							(float)(quad.getVertexPosD().getY()), 
							(float)(quad.getVertexPosD().getZ()));	
		GL11.glEnd();							
	}

	/**
	 * Render a quad's wireframe
	 * @param quad
	 */
	private  void renderWireframeQuad(E3DQuad quad)
	{
        //Setup textures
//        int glTextureID = -1;
//        int detail0TextureID = -1;
//        int detail1TextureID = -1;
//
//        if(quad.getTexture() != null)
//            glTextureID = quad.getTexture().getGlTextureID();
//        if(quad.isTextureDetail0Available())
//            detail0TextureID = quad.getTextureDetail0().getGlTextureID();
//        if(quad.isTextureDetail1Available())
//            detail1TextureID = quad.getTextureDetail1().getGlTextureID();
//        
//        setupTextureUnits(glTextureID, detail0TextureID, detail1TextureID);

		//Draw vertices
//		ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
	//	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); //default all white color for the base polygon

		GL11.glBegin (GL11.GL_LINE_LOOP);	// Begin Drawing Triangles
//			ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)quad.getTextureCoordA().getX(), (float)quad.getTextureCoordA().getY());
            GL11.glColor4f((float)quad.getVertexColorA().getA(), (float)quad.getVertexColorA().getB(), 
                            (float)quad.getVertexColorA().getC(), (float)quad.getVertexColorA().getD());
			GL11.glVertex3f((float)(quad.getVertexPosA().getX()), 
							(float)(quad.getVertexPosA().getY()), 
							(float)(quad.getVertexPosA().getZ()));
	
//            ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)quad.getTextureCoordB().getX(), (float)quad.getTextureCoordB().getY());
            GL11.glColor4f((float)quad.getVertexColorB().getA(), (float)quad.getVertexColorB().getB(), 
                            (float)quad.getVertexColorB().getC(), (float)quad.getVertexColorB().getD());
            GL11.glVertex3f((float)(quad.getVertexPosB().getX()), 
                            (float)(quad.getVertexPosB().getY()), 
                            (float)(quad.getVertexPosB().getZ()));

//             ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)quad.getTextureCoordC().getX(), (float)quad.getTextureCoordC().getY());
             GL11.glColor4f((float)quad.getVertexColorC().getA(), (float)quad.getVertexColorC().getB(), 
                             (float)quad.getVertexColorC().getC(), (float)quad.getVertexColorC().getD());
            GL11.glVertex3f((float)(quad.getVertexPosC().getX()), 
                            (float)(quad.getVertexPosC().getY()), 
                            (float)(quad.getVertexPosC().getZ()));

//			ARBMultitexture.glMultiTexCoord2fARB(ARBMultitexture.GL_TEXTURE0_ARB, (float)quad.getTextureCoordD().getX(), (float)quad.getTextureCoordD().getY());
            GL11.glColor4f((float)quad.getVertexColorD().getA(), (float)quad.getVertexColorD().getB(), 
                            (float)quad.getVertexColorD().getC(), (float)quad.getVertexColorD().getD());
			GL11.glVertex3f((float)(quad.getVertexPosD().getX()), 
							(float)(quad.getVertexPosD().getY()), 
							(float)(quad.getVertexPosD().getZ()));

		GL11.glEnd();							
	}
	
	public  void renderQuadList(E3DEngine engine, ArrayList quadList, int renderMode)
	{
		int viewportRenderMode = engine.getCurrentViewport().getRenderMode().getRenderMode();
	    if(viewportRenderMode == E3DRenderMode.RENDERMODE_WIREFRAME || 
	    	(viewportRenderMode == E3DViewport.RENDERMODE_DEFAULT && renderMode == E3DRenderMode.RENDERMODE_WIREFRAME))
	        renderWireframeQuadList(quadList);
	    else if(viewportRenderMode == E3DRenderMode.RENDERMODE_SOLID ||
		        (viewportRenderMode == E3DViewport.RENDERMODE_DEFAULT && renderMode == E3DRenderMode.RENDERMODE_SOLID))
	    	renderSolidQuadList(quadList);
	    else //textured
		    renderTexturedQuadList(quadList);	
	}

	/**
	 * This will render a list of quads using vertexarrays.  It assumes that the list
	 * of quadss all share the same texture!
	 * @param quadList List of quads to render all containing the same texture
	 */
	private  void renderSolidQuadList(List quadList)
	{
        if(quadList == null)
            return;

        E3DQuad quad = null;
        List renderList;
        
		int listSize = quadList.size();
		if(listSize <= 0)
		    return;
		
		int bufferSize = listSize * 16;
        if(bufferSize > MAX_BUFFER_SIZE)
        {
            int maxLength = (int)(MAX_BUFFER_SIZE / 16.0);
            
            renderSolidQuadList(quadList.subList(maxLength, quadList.size()));
            renderList = quadList.subList(0, maxLength);
        }
        else
            renderList = quadList;

        vertexBuffer.clear();
        colorBuffer.clear();

		for(int i=0; i<renderList.size(); i++)
		{
			quad = (E3DQuad)renderList.get(i);
			
            quad.appendVertexBuffer(vertexBuffer);
            quad.appendVertexColorBuffer(colorBuffer);
		}

		// Put arrays in float buffers and REWIND them (very important).
        vertexBuffer.rewind();
        colorBuffer.rewind();        
        
        // Send float buffers to LWJGL.
        GL11.glVertexPointer(3, 0, vertexBuffer);
        GL11.glColorPointer(4, 0, colorBuffer);

		GL11.glDrawArrays(GL11.GL_QUADS, 0, renderList.size() * 4);
	}

	/**
	 * This will render a list of quads using vertexarrays.  It assumes that the list
	 * of quadss all share the same texture!
	 * @param quadList List of quads to render all containing the same texture
	 */
	private  void renderTexturedQuadList(List quadList)
	{
        if(quadList == null)
            return;

        E3DQuad quad = null;
        List renderList;
        
		int listSize = quadList.size();
		if(listSize <= 0)
		    return;
		
		int bufferSize = listSize * 16;
        if(bufferSize > MAX_BUFFER_SIZE)
        {
            int maxLength = (int)(MAX_BUFFER_SIZE / 16.0);
            
            renderTexturedQuadList(quadList.subList(maxLength, quadList.size()));
            renderList = quadList.subList(0, maxLength);
        }
        else
            renderList = quadList;

        vertexBuffer.clear();
        colorBuffer.clear();
        texCoordBuffer.clear();
        texCoordDetail0Buffer.clear();
        texCoordDetail1Buffer.clear();

		for(int i=0; i<renderList.size(); i++)
		{
			quad = (E3DQuad)renderList.get(i);
			
            quad.appendVertexBuffer(vertexBuffer);
            quad.appendTexCoordBuffer(texCoordBuffer);
            
            quad.appendVertexColorBuffer(colorBuffer);
            quad.appendTexCoordDetail0Buffer(texCoordDetail0Buffer);
            quad.appendTexCoordDetail1Buffer(texCoordDetail1Buffer);
		}
        //Setup textures
//        int glTextureID = -1;
        int detail0TextureID = -1;
        int detail1TextureID = -1;
    
//        if(quad.isTextureAvailable())
//        	glTextureID = quad.getTexture().getGlTextureID();
        if(quad.isTextureDetail0Available())
            detail0TextureID = quad.getTextureDetail0().getGlTextureID();
        if(quad.isTextureDetail1Available())
            detail1TextureID = quad.getTextureDetail1().getGlTextureID();
        
//        disableAllTextureUnits();
//        if(textured)
//            setupTextureUnits(glTextureID, detail0TextureID, detail1TextureID);
        
//        int glBuffer;
    
        // Put arrays in float buffers and REWIND them (very important).
        vertexBuffer.rewind();
        texCoordBuffer.rewind();
        texCoordDetail0Buffer.rewind();
        texCoordDetail1Buffer.rewind();
        colorBuffer.rewind();        
        
        // Send float buffers to LWJGL.
        GL11.glVertexPointer(3, 0, vertexBuffer);
        GL11.glColorPointer(4, 0, colorBuffer);

        //Draw vertices
        ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB);
   //     GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glTexCoordPointer(2, 0,texCoordBuffer);

        if(getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() >= 2 && detail0TextureID != -1)
        {
            ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE1_ARB);
     //       GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glTexCoordPointer(2, 0,texCoordDetail0Buffer);

            if(getEngine().getFeatureChecker().getArbMultitextureNumTexUnitsSupported() >= 3 && detail1TextureID != -1)
            {
                ARBMultitexture.glClientActiveTextureARB(ARBMultitexture.GL_TEXTURE2_ARB);
       //         GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                GL11.glTexCoordPointer(2, 0,texCoordDetail1Buffer);
            }
        }
        
		GL11.glDrawArrays(GL11.GL_QUADS, 0, renderList.size() * 4);
	}

	/**
	 * This will render a list of quads using vertexarrays.  It assumes that the list
	 * of quadss all share the same texture!
	 * @param quadList List of quads to render all containing the same texture
	 */
	private  void renderWireframeQuadList(List quadList)
	{
        if(quadList == null)
            return;

        E3DQuad quad = null;
		List renderList;
		
		int listSize = quadList.size();
		if(listSize <= 0)
		    return;
		
		int bufferSize = listSize * 32;
        if(bufferSize > MAX_BUFFER_SIZE)
        {
            int maxLength = (int)(MAX_BUFFER_SIZE / 32.0);
            
            renderWireframeQuadList((ArrayList)quadList.subList(maxLength, quadList.size()));
            renderList = quadList.subList(0, maxLength);
        }
        else
            renderList = quadList;
        
        vertexBuffer.clear();
        colorBuffer.clear();

        for(int i=0; i < renderList.size(); i++)
		{
			quad = (E3DQuad)renderList.get(i);

            quad.appendVertexBuffer(vertexBuffer);
            quad.getVertexPosB().appendToBuffer(vertexBuffer);
            quad.getVertexPosC().appendToBuffer(vertexBuffer);
            quad.getVertexPosD().appendToBuffer(vertexBuffer);
            quad.getVertexPosA().appendToBuffer(vertexBuffer);
            
            quad.appendVertexColorBuffer(colorBuffer);
            quad.getVertexPosB().appendToBuffer(colorBuffer);
            quad.getVertexPosC().appendToBuffer(colorBuffer);
            quad.getVertexPosD().appendToBuffer(colorBuffer);
            quad.getVertexPosA().appendToBuffer(colorBuffer);
		}
		
        disableAllTextureUnits();
        
		// Put arrays in float buffers and REWIND them (very important).
		vertexBuffer.rewind();
		colorBuffer.rewind();
		
		// Send float buffers to LWJGL.
		GL11.glVertexPointer(3, 0, vertexBuffer);
		GL11.glColorPointer(4, 0, colorBuffer);

		GL11.glDrawArrays(GL11.GL_LINES, 0, renderList.size() * 8); //five things since we list the first vertex twice (to make the line list complete
    }
    
	public void renderParticleSystem(E3DParticleSystem particleSystem)
	{
	    particleRenderer.renderParticleList(particleSystem.getParticleList(), particleSystem.getRenderMode().getRenderMode());
	}
	
	public void renderParticleList(List particleList, int renderMode)
	{
	    particleRenderer.renderParticleList(particleList, renderMode);
	}

	public void renderParticle(E3DParticle particle)
	{
	    particleRenderer.renderParticle(particle);
	}
}
