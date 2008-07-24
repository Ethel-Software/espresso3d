/*
 * Created on Jun 27, 2005
 *
 
   	Copyright 2005 Curtis Moxley
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
package espresso3d.engine.renderer.particle;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBPointParameters;
import org.lwjgl.opengl.ARBPointSprite;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.E3DGeometryRenderer;
import espresso3d.engine.renderer.particle.base.IE3DParticleRenderer;
import espresso3d.engine.world.sector.particle.E3DParticle;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DParticleRendererARBPointSprite extends E3DEngineItem implements IE3DParticleRenderer 
{
    private FloatBuffer particleDistanceScalarBuffer;
    private float maxPointSpriteSize = 0.0f;
    
    
    public E3DParticleRendererARBPointSprite(E3DEngine engine)
    {
        super(engine);
        
        //This is used to keep the particle size scaled correctly as the camera moves (so as you get further, it looks smaller).  Keep
        // it at a constant value for our particle systems
        particleDistanceScalarBuffer = BufferUtils.createFloatBuffer(4);

        particleDistanceScalarBuffer.put(0.0f);//1.0f);
        particleDistanceScalarBuffer.put(0.0f);
        particleDistanceScalarBuffer.put(0.000001f);//0.1f);
        particleDistanceScalarBuffer.rewind();
        
        FloatBuffer particleMaxDistanceBuffer = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat( ARBPointParameters.GL_POINT_SIZE_MAX_ARB,  particleMaxDistanceBuffer);
        particleMaxDistanceBuffer.rewind();
        maxPointSpriteSize = particleMaxDistanceBuffer.get();
        particleMaxDistanceBuffer.clear(); 
        
    }

//    //FIXME: Somehow use the rendertree to do arb point sprites too
//    public void renderParticleList(ArrayList particleList)
//    {
////        E3DSortedRenderableMap sortedMap = particleSystem.getBlendAndTextureSortedParticleMap();
////        Iterator it = sortedMap.entrySet().iterator();
////        
////        ArrayList particleList = null;
////        Map.Entry entry = null;
////        while(it.hasNext())
////        {
////            entry = (Map.Entry)it.next();
//            particleList = (ArrayList)entry.getValue();
//            
//            renderParticleList(particleList);
////        }
//    }

    
    private void setupPointSpriteParameters(double spriteSize)
    {
        particleDistanceScalarBuffer.clear();
//        particleDistanceScalarBuffer.put(0.0f);//1.0f);
        particleDistanceScalarBuffer.put(0.0f);//1.0f);
        particleDistanceScalarBuffer.put(0.0f);
        particleDistanceScalarBuffer.put((float)(1.0 / (getEngine().getCurrentViewport().getWidth() * getEngine().getCurrentViewport().getHeight())));//0.1f);
        particleDistanceScalarBuffer.rewind();

        ARBPointParameters.glPointParameterARB(ARBPointParameters.GL_POINT_DISTANCE_ATTENUATION_ARB, particleDistanceScalarBuffer);
        ARBPointParameters.glPointParameterfARB( ARBPointParameters.GL_POINT_SIZE_MIN_ARB, 1f);//(float)particle.getSize());//1f );
        ARBPointParameters.glPointParameterfARB( ARBPointParameters.GL_POINT_SIZE_MAX_ARB, maxPointSpriteSize);
        ARBPointParameters.glPointParameterfARB(ARBPointParameters.GL_POINT_FADE_THRESHOLD_SIZE_ARB, 100f);
/*        GL14.glPointParameter(GL14.GL_POINT_DISTANCE_ATTENUATION, particleDistanceScalarBuffer);
        GL14.glPointParameterf( GL14.GL_POINT_SIZE_MIN, 1f);//(float)particle.getSize());//1f );
        GL14.glPointParameterf( GL14.GL_POINT_SIZE_MAX, maxPointSpriteSize);
        GL14.glPointParameterf(GL14.GL_POINT_FADE_THRESHOLD_SIZE, 100f);
*/
        
        GL14.glPointParameterf(GL20.GL_POINT_SPRITE_COORD_ORIGIN, GL20.GL_LOWER_LEFT);
        GL11.glTexEnvf(  ARBPointSprite.GL_POINT_SPRITE_ARB, ARBPointSprite.GL_COORD_REPLACE_ARB, GL11.GL_TRUE );
//        GL11.glTexEnvf(  GL20.GL_POINT_SPRITE, GL20.GL_COORD_REPLACE, GL11.GL_TRUE );
        
        GL11.glPointSize((float)spriteSize); //POINT_SIZE_WORLD_COORD_SCALAR);
    }
    
    public void renderParticle(E3DParticle particle)
    {
           //Setup textures
        int glTextureID = -1;
        int detail0TextureID = -1;
        int detail1TextureID = -1;

        if(particle.getQuad().isTextureAvailable())
            glTextureID = particle.getQuad().getTexture().getGlTextureID();
        if(particle.getQuad().isTextureDetail0Available())
            detail0TextureID = particle.getQuad().getTextureDetail0().getGlTextureID();
        if(particle.getQuad().isTextureDetail1Available())
            detail1TextureID = particle.getQuad().getTextureDetail1().getGlTextureID();

        setupPointSpriteParameters(particle.getSize());
        
        getEngine().getGeometryRenderer().disableAllTextureUnits();
//        if(textured)
            getEngine().getGeometryRenderer().setupTextureUnits(glTextureID, detail0TextureID, detail1TextureID);

            
        E3DVector3F position = null;
        GL11.glBegin( GL11.GL_POINTS );
        {
            position = particle.getOrientation().getPosition();

            GL14.glPointParameterf( GL14.GL_BLEND_SRC_ALPHA, (float)particle.getAlpha());//(float)particle.getSize());//1f );

            GL11.glColor4f(1f, 1f, 1f, (float)particle.getAlpha());
            GL11.glVertex3f((float)position.getX(), (float)position.getY(), (float)position.getZ());
        }
        GL11.glEnd();

        GL11.glDisable( ARBPointSprite.GL_POINT_SPRITE_ARB );   
       
    }
    
    
    public void renderParticleList(List particleList, int renderMode)
    {
        if(particleList == null)
            return;

        E3DParticle particle = null;
        List renderList;
        
        int listSize = particleList.size();
        if(listSize <= 0)
            return;
        
        int bufferSize = listSize * 12; //use 12 to account for the quad (for vertex colors, etc) even though there aren't that many vertices
        if(bufferSize > E3DGeometryRenderer.MAX_BUFFER_SIZE)
        {
            int maxLength = (int)(E3DGeometryRenderer.MAX_BUFFER_SIZE / 12.0);
            
            renderParticleList(particleList.subList(maxLength, particleList.size()), renderMode);
            renderList = particleList.subList(0, maxLength);
        }        
        else
            renderList = particleList;
        
        getEngine().getGeometryRenderer().vertexBuffer.clear();
        getEngine().getGeometryRenderer().colorBuffer.clear();

        E3DVector3F position = null;
        for(int i=0; i < renderList.size(); i++)
        {
            particle = (E3DParticle)renderList.get(i);

            position = particle.getOrientation().getPosition();

            getEngine().getGeometryRenderer().vertexBuffer.put((float)position.getX());
            getEngine().getGeometryRenderer().vertexBuffer.put((float)position.getY());
            getEngine().getGeometryRenderer().vertexBuffer.put((float)position.getZ());
  
            particle.getQuad().appendVertexColorBuffer(getEngine().getGeometryRenderer().colorBuffer);
        }
        
        setupPointSpriteParameters(particle.getSize());
        
         //Setup textures
        int glTextureID = -1;
        int detail0TextureID = -1;
        int detail1TextureID = -1;

        if(particle.getQuad().isTextureAvailable())
            glTextureID = particle.getQuad().getTexture().getGlTextureID();
        if(particle.getQuad().isTextureDetail0Available())
            detail0TextureID = particle.getQuad().getTextureDetail0().getGlTextureID();
        if(particle.getQuad().isTextureDetail1Available())
            detail1TextureID = particle.getQuad().getTextureDetail1().getGlTextureID();
        
        getEngine().getGeometryRenderer().disableAllTextureUnits();
//        if(textured)
        getEngine().getGeometryRenderer().setupTextureUnits(glTextureID, detail0TextureID, detail1TextureID);
        
        // Put arrays in float buffers and REWIND them (very important).
        getEngine().getGeometryRenderer().vertexBuffer.rewind();
        getEngine().getGeometryRenderer().colorBuffer.rewind();        

        GL11.glEnable( ARBPointSprite.GL_POINT_SPRITE_ARB );

        // Send float buffers to LWJGL.
        GL11.glVertexPointer(3, 0, getEngine().getGeometryRenderer().vertexBuffer);
        GL11.glColorPointer(3, 0, getEngine().getGeometryRenderer().colorBuffer);
   
        GL11.glDrawArrays(GL11.GL_POINTS, 0, renderList.size());

        GL11.glDisable( ARBPointSprite.GL_POINT_SPRITE_ARB );   
    }
}
