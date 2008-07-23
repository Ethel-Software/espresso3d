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
package espresso3d.engine.renderer.base;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.window.viewport.E3DViewport;

/**
 * @author espresso3d
 *
 * Base class for all renderable objects in the engine
 */
public abstract class E3DRenderable extends E3DEngineItem implements IE3DRenderable
{
    private E3DRenderMode renderMode;
    private E3DBlendMode blendMode;
    
    public abstract void render();
    
    public E3DRenderable(E3DEngine engine)
    {
        this(engine, new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED), new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
    }

    public E3DRenderable(E3DEngine engine, E3DRenderMode renderMode)
    {
        this(engine, renderMode, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
    }
    
    public E3DRenderable(E3DEngine engine, E3DRenderMode renderMode, E3DBlendMode blendMode)
    {
        super(engine);
        
        this.renderMode = renderMode;
        this.blendMode = blendMode;
        
    //    keyBuffer = new StringBuffer();
    }

    
    public void setRenderMode(E3DRenderMode renderMode)
    {
    	this.renderMode = renderMode;
    }
    
    public E3DRenderMode getRenderMode()
    {
        return renderMode;
    }
    
    public void setBlendMode(E3DBlendMode blendMode)
    {
        this.blendMode = blendMode;
    }

    public E3DBlendMode getBlendMode()
    {
        return blendMode;
    }
    
    //TODO: Fix Me
//    public void initCorrectGeometryRenderer()
//    {
//        int currentViewportRenderMode = getEngine().getCurrentViewport().getRenderMode().getRenderMode();
//        
//        int blendMode = getBlendMode().blendMode;
//        int renderMode = getRenderMode().getRenderMode();
//        
//        if(currentViewportRenderMode == E3DViewport.RENDERMODE_DEFAULT)
//        {
//            if(renderMode == E3DRenderMode.RENDERMODE_TEXTURED)
//            {
//                if(blendMode == E3DBlendMode.BLENDMODE_SOLID)
//                    getEngine().getGeometryRenderer().initSolidTexturedGeometryRenderer();
//                else if(blendMode == E3DBlendMode.BLENDMODE_BLEND)
//                    getEngine().getGeometryRenderer().initBlendedTexturedGeometryRenderer();
//            }
//            else if(renderMode == E3DRenderMode.RENDERMODE_SOLID)
//            {
//                if(blendMode == E3DBlendMode.BLENDMODE_SOLID)
//                    getEngine().getGeometryRenderer().initSolidNonTexturedGeometryRenderer();
//                else if(blendMode == E3DBlendMode.BLENDMODE_BLEND)
//                    getEngine().getGeometryRenderer().initBlendedNonTexturedGeometryRenderer();
//            }
//            else if(renderMode == E3DRenderMode.RENDERMODE_WIREFRAME)
//                getEngine().getGeometryRenderer().initLineRenderer();
//        }
//        else if(currentViewportRenderMode == E3DRenderMode.RENDERMODE_WIREFRAME)
//            getEngine().getGeometryRenderer().initLineRenderer();
//        else if(currentViewportRenderMode == E3DRenderMode.RENDERMODE_SOLID)
//        {
//            if(blendMode == E3DBlendMode.BLENDMODE_SOLID)
//                getEngine().getGeometryRenderer().initSolidNonTexturedGeometryRenderer();
//            else if(blendMode == E3DBlendMode.BLENDMODE_BLEND)
//                getEngine().getGeometryRenderer().initBlendedNonTexturedGeometryRenderer();
//        }
//        else if(currentViewportRenderMode == E3DRenderMode.RENDERMODE_TEXTURED)
//        {
//            if(blendMode == E3DBlendMode.BLENDMODE_SOLID)
//                getEngine().getGeometryRenderer().initSolidTexturedGeometryRenderer();
//            else if(blendMode == E3DBlendMode.BLENDMODE_BLEND)
//                getEngine().getGeometryRenderer().initBlendedTexturedGeometryRenderer();
//        }
//    }
}
