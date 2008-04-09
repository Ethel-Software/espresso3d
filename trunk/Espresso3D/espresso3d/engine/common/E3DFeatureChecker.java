/*
 * Created on May 22, 2005
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
package espresso3d.engine.common;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.logger.E3DEngineLogger;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DFeatureChecker extends E3DEngineItem
{
    private boolean arbMultitextureEnabled;
    private boolean arbPointParametersEnabled;
    private boolean arbPointSpriteEnabled;
    private boolean arbTextureEnvCombineEnabled;
    private int arbMultitextureNumTexUnitsSupported;
    private boolean glslVertexShadersEnabled;
    private boolean glslFragmentShadersEnabled;

    private static String allExtensions = null;
    
    public E3DFeatureChecker(E3DEngine engine)
    {
        super(engine);

        engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Checking supported OpenGL features");
        
        IntBuffer buffer = BufferUtils.createIntBuffer(16);
        buffer.rewind();
        
        //For multi-texturing
        arbMultitextureEnabled = checkFeature("GL_ARB_multitexture");
        if(!arbMultitextureEnabled)
            engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_ERROR, "GL_ARB_multitexture is not support by this video card.  Try upgrading your drivers as most support this extension now.  If that doesn't work, submit a bug report at www.espresso3d.com.");
        else
            engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "GL_ARB_multitexture is supported!");
        GL11.glGetInteger(ARBMultitexture.GL_MAX_TEXTURE_UNITS_ARB, buffer);
        arbMultitextureNumTexUnitsSupported = buffer.get(0);
        if(arbMultitextureNumTexUnitsSupported < 3)
            engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_WARNING, "" + arbMultitextureNumTexUnitsSupported + " multitexture texture units supported simultaneously by this video card.  E3D currently supports 3, but the 3rd will have to be disabled on all objects so some textures may be msising.");
        else
            engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "" + arbMultitextureNumTexUnitsSupported + " multitexture texture units supported simultaneously by this video card.");
        arbMultitextureNumTexUnitsSupported = 2;
        arbTextureEnvCombineEnabled = checkFeature("GL_ARB_texture_env_combine");
        if(!arbTextureEnvCombineEnabled)
        {
            arbMultitextureEnabled = false;
            engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_ERROR, "GL_ARB_texture_env_combine is not support by this video card.  Try upgrading your drivers as most support this extension now.  If that doesnt work, submit a bug report at www.espresso3d.com.");
        }
        else
            engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "GL_ARB_texture_env_combine is supported!");

        //For point sprites
        arbPointSpriteEnabled = checkFeature("GL_ARB_point_sprite");
        if(!arbPointSpriteEnabled)
            engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_WARNING, "GL_ARB_point_sprite is not supported on this video card.  Particle performance will be severely decreased as legacy particles will be used.");
        else
            engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "GL_ARB_point_sprite is supported!");
        
        arbPointParametersEnabled = checkFeature("GL_ARB_point_parameters");
        if(!arbPointParametersEnabled)
        {
             engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_WARNING, "GL_ARB_point_parameters is not supported on this video card.  SpriteParticle performance will be severely decreased as legacy particles will be used.");
             arbPointSpriteEnabled = false; //disable this too
        }
        else
            engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "GL_ARB_point_parameters is supported!");
   //     arbPointSpriteEnabled = false;
        
        //For 2.0 GLSL Shaders
        glslVertexShadersEnabled = checkFeature("GL_ARB_vertex_shader");
        glslVertexShadersEnabled = checkFeature("GL_ARB_fragment_shader");
        
    }

    private boolean checkFeature(String featureName)
    {
        //Only query it once per launch
        if(allExtensions == null)
//        {
            allExtensions = GL11.glGetString(GL11.GL_EXTENSIONS);
  //          getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Extensions supported === \n" + allExtensions);
  //      }
        return allExtensions.indexOf(featureName) >= 0;
        
    }
    
    public boolean isArbPointSpriteEnabled()
    {
        return arbPointSpriteEnabled;
    }
    public boolean isArbMultitextureEnabled() {
        return arbMultitextureEnabled;
    }
    public static String getAllExtensions() {
        return allExtensions;
    }
    public boolean isArbPointParametersEnabled() {
        return arbPointParametersEnabled;
    }
    public boolean isArbTextureEnvCombineEnabled() {
        return arbTextureEnvCombineEnabled;
    }
    public int getArbMultitextureNumTexUnitsSupported() {
        return arbMultitextureNumTexUnitsSupported;
    }
    
	public boolean isGlslVertexShadersEnabled() {
		return glslVertexShadersEnabled;
	}

	public void setGlslVertexShadersEnabled(boolean glslVertexShadersEnabled) {
		this.glslVertexShadersEnabled = glslVertexShadersEnabled;
	}
}
