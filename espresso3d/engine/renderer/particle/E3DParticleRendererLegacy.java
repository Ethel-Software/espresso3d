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

import java.util.List;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.renderer.particle.base.IE3DParticleRenderer;
import espresso3d.engine.world.sector.particle.E3DParticle;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;
import espresso3d.engine.world.sector.particle.E3DSprite;
/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DParticleRendererLegacy extends E3DEngineItem implements IE3DParticleRenderer 
{
    public E3DParticleRendererLegacy(E3DEngine engine)
    {
        super(engine);
    }
    
    public void renderParticleList(List particleList, int renderMode)
    {
        if(particleList != null)
        	getEngine().getGeometryRenderer().renderQuadList(getEngine(), E3DParticleSystem.getQuadList(particleList), renderMode);
    }

    /* (non-Javadoc)
     * @see espresso3d.engine.renderer.particle.base.IE3DParticleRenderer#renderParticle(espresso3d.engine.world.sector.particle.E3DParticle)
     */
    public void renderParticle(E3DParticle particle) {
        ((E3DSprite)particle).render(); //render as a normal quad sprite
    }
}
