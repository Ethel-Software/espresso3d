/*
 
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
 * 
 */
package espresso3d.engine.renderer.shader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;

public class E3DShader extends E3DEngineItem 
{
    public static final int SHADER_TYPE_FRAGMENT = GL20.GL_FRAGMENT_SHADER;
    public static final int SHADER_TYPE_VERTEX = GL20.GL_VERTEX_SHADER;
    
    private String shaderFileName;
    private int shaderType;
    
    public E3DShader(E3DEngine engine, int shaderType, String shaderFileName)
    {
        super(engine);
        init(shaderType, shaderFileName);
    }
    
    public void init(int shaderType, String shaderFileName)
    {
        this.shaderType =  shaderType;
        
        int shaderID = GL20.glCreateShader(shaderType);
      //  GL20.glShaderSource(shaderID, )
        
    }
    
    
    
}
