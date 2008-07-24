/*
 * Created on Nov 7, 2004
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
package espresso3d.engine.world.sector.portal;

import org.lwjgl.opengl.GL11;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DLine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.window.viewport.frustum.E3DViewFrustum2D;
import espresso3d.engine.world.sector.E3DSector;

/**
 * @author Curt
 *
 * Base class for a portal
 * This is default access so it cannot be accessed.  Portals must be an implemented type (Fast, Accurate, etc)
 */
public abstract class E3DPortal extends E3DRenderable 
{
    private String portalID; //id of the portal
    private E3DSector sector; //sector the portal is in
    
    private E3DVector3F a, b, c, d;
    private String linkSectorID; //String of the sector this portal links to.  Right now, this has to be an ID and not looked up already because sectors may be loaded before others they link to. Could do a post load
    private String linkPortalID; //Sector and ID of portal to link to
    
    private E3DTriangle triangleA, triangleB;

    /**
     * 
     * @param engine
     * @param portalID The ID of this portal
     * @param linkSectorID The sector this portal links to
     * @param linkPortalID The portal this portal links to
     * @param a Lower left corner
     * @param b Upper left corner
     * @param c Upper right corner
     * @param d Lower right corner
     */
    protected E3DPortal(E3DEngine engine, String portalID, String linkSectorID, String linkPortalID, E3DVector3F a, E3DVector3F b, E3DVector3F c, E3DVector3F d)
    {
        super(engine);
        setPortalID(portalID);
        setLinkSectorID(linkSectorID);
        setLinkPortalID(linkPortalID);
        setA(a);
        setB(b);
        setC(c);
        setD(d);
        triangleA = new E3DTriangle(engine, a, b, c);
        triangleB = new E3DTriangle(engine, a, c, d);
    }
    
    protected void set(E3DVector3F a, E3DVector3F b, E3DVector3F c, E3DVector3F d)
    {
        setA(a);
        setB(b);
        setC(c);
        setD(d);
    }

	public void render() {
        if(getEngine().getDebugFlags().isDebugRenderPortals())
        {
    	    GL11.glPushMatrix();
    		    getEngine().getGeometryRenderer().initSolidAndLineRendering();
    		    E3DLine lineA, lineB, lineC, lineD;
    		    lineA = new E3DLine(getEngine(), a, b);
    		    lineB = new E3DLine(getEngine(), b, c);
    		    lineC = new E3DLine(getEngine(), c, d);
    		    lineD = new E3DLine(getEngine(), a, d);
                
                E3DVector3F color = new E3DVector3F(0.0, 0.0, 1.0);
                lineA.setStartColor(color);
                lineA.setEndColor(color);
                lineB.setStartColor(color);
                lineB.setEndColor(color);
                lineC.setStartColor(color);
                lineC.setEndColor(color);
                lineD.setStartColor(color);
                lineD.setEndColor(color);
                
    		    getEngine().getGeometryRenderer().renderLine(lineA);
    		    getEngine().getGeometryRenderer().renderLine(lineB);
    		    getEngine().getGeometryRenderer().renderLine(lineC);
    		    getEngine().getGeometryRenderer().renderLine(lineD);

                getEngine().getGeometryRenderer().initBlendedRendering();
                E3DQuad quad = new E3DQuad(getEngine(), a, b, c, d);
                E3DVector4F vertexColor = new E3DVector4F(0.0, 0.0, 1.0, 0.1);
                quad.setVertexColor(vertexColor, vertexColor, vertexColor, vertexColor);
                getEngine().getGeometryRenderer().renderQuad(quad, E3DRenderMode.RENDERMODE_SOLID);
    	   GL11.glPopMatrix();
        }
	}
	
	public String getLinkSectorID() {
        return linkSectorID;
    }
	protected void setLinkSectorID(String linkSectorID) {
        this.linkSectorID = linkSectorID;
    }
	public String getLinkPortalID() {
        return linkPortalID;
    }
	protected void setLinkPortalID(String linkPortalID) {
        this.linkPortalID = linkPortalID;
    }
	public String getPortalID() {
        return portalID;
    }
	protected void setPortalID(String portalID) {
        this.portalID = portalID;
    }
	public E3DSector getSector() {
        return sector;
    }
	public void setSector(E3DSector sector) {
        this.sector = sector;
    }
	
	//Constructs a 2D view frustum using some data stored in the current frustum
	public E3DViewFrustum2D get2DFrustum(E3DViewport viewport)
	{
	    E3DVector2F projA = viewport.projectPointIgnoreZ(a);
	    E3DVector2F projB = viewport.projectPointIgnoreZ(b);
	    E3DVector2F projC = viewport.projectPointIgnoreZ(c);
	    E3DVector2F projD = viewport.projectPointIgnoreZ(d);
	    
	    return new E3DViewFrustum2D(getEngine(), viewport, projA, projB, projC, projD);
	}
    public E3DVector3F getA() {
        return a;
    }
    public void setA(E3DVector3F a) {
        this.a = a;
    }
    public E3DVector3F getB() {
        return b;
    }
    public void setB(E3DVector3F b) {
        this.b = b;
    }
    public E3DVector3F getC() {
        return c;
    }
    public void setC(E3DVector3F c) {
        this.c = c;
    }
    public E3DVector3F getD() {
        return d;
    }
    public void setD(E3DVector3F d) {
        this.d = d;
    }
    
    /**
     * One of two triangles that make up the portal rectangle
     * @return
     */
    public E3DTriangle getTriangleA() {
        return triangleA;
    }
    /**
     * One of two triangles that make up the portal rectangle
     * @return
     */
    public E3DTriangle getTriangleB() {
        return triangleB;
    }
}
