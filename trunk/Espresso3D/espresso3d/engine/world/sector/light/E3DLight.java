/*
 * Created on Oct 18, 2004
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
package espresso3d.engine.world.sector.light;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DPoint;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.base.E3DPortalEnabledRenderable;
/**
 * @author Curt
 *
 * A light class for lighting the world/actors.  A light belongs to a single sector,
 * but will light any sectors that its sector is connected to with a portal as well.
 * 
 * TODO: This is temporary and basic light support for E3D.  This will very likely change and be
 * extended upon to provide support for all the light types and models planned.  This is essentially
 * available now in a basic format to provide you with some sort of lighting facility.
 */
public class E3DLight extends E3DPortalEnabledRenderable
{
	public static final int LIGHT_TYPE_OMNIDIRECTIONAL = 0;
	public static final int LIGHT_TYPE_GLOBAL = 1;
	
	private String lightID;
	
//	private E3DWorld world;
//	private E3DSector sector;
	
	private E3DVector3F position = new E3DVector3F(0.0, 0.0, 0.0);
	private E3DVector4F color = new E3DVector4F(1.0, 1.0, 1.0, 1.0);
	private int type = 0; //default omnidirectional
	private double brightness = 1;
	private double falloff = 0; //TODO: Fix the falloff, it can't be right, a higher falloff makes it be brighter in the center.. get a real formula
	
	public E3DLight(E3DEngine engine, String lightID)
	{
		super(engine);
		
		setLightID(lightID);
	}

	/**
	 * Returns the RGB values in a E3DVector3F with the correct lighting information for this light 
	 *  based on the distance the vertex passed in is away and the lights color, brightness, falloff, etc.
	 * @param vertex
	 * 	The vertex that we want to get the lit values of from this light
	 * @return
	 *  Returns the lit values for this vertex from this light or null if it has no effect.  Other lights will have to be accumulated
	 */
	public E3DVector4F getLitVertex(E3DVector3F vertex)
	{
		E3DVector4F rgba = new E3DVector4F(color);
		
		E3DVector3F directionVec = vertex.subtract(position);
	
		double distanceSquared = directionVec.getLengthSquared(); //keep it squared for speed.
		
		/**
		 * 
		 * d = 10
		 * b = 0.1
		 * f = 2
		 * 
		 * 
		 */
		if(falloff > 0 && (distanceSquared > (falloff * falloff))) //If its past the falloff, it doesn't get any light
		{	
			rgba.set(0, 0, 0, 0);
			return null;
		}
		
		double vertBrightness = (brightness / distanceSquared); //linear model
		
		
		if(vertBrightness > 1)
			vertBrightness = 1;

        rgba.scaleEqual(vertBrightness);
        
		return rgba;
	}
	
	//Make this render some sort of debug information to show where the light is
	public void render() {
		E3DPoint point = new E3DPoint(getEngine(), getPosition(), getColor());
		getEngine().addExternalRenderable(point, 10);
	}
	/**
	 * @return Returns the brightness of the light
	 */
	public double getBrightness() {
		return brightness;
	}

	/**
	 * Brightness is how bright the light is. 
	 * @param brightness The brightness to set.
	 */
	public void setBrightness(double brightness) {
		this.brightness = brightness;
		if(getSector() != null)
		    getSector().getMesh().setNeedingLightRecalc(true);
	}
	/**
	 * @return Returns the color in a vector with RGB = XYZ as a 0.0 to 1.0 value
	 */
	public E3DVector4F getColor() {
		return color;
	}
	/**
	 * @param color The color to set.
	 */
	public void setColor(E3DVector4F color) {
		this.color = color;
		if(getSector() != null)
		    getSector().getMesh().setNeedingLightRecalc(true);

	}
	/**
	 * @return Returns the falloff distance
	 */
	public double getFalloff() {
		return falloff;
	}
	/**
	 * @param falloff The falloff distance is the distance the light stops working.  0 disables falloff and the light goes until it no longer has any strength left
	 */
	public void setFalloff(double falloff) {
		this.falloff = falloff;
	}
	/**
	 * @return Returns the position.
	 */
	public E3DVector3F getPosition() {
		return position;
	}
	/**
	 * @param position The position to set.
	 */
	public void setPosition(E3DVector3F position) {
		this.position = position;
		if(getSector() != null)
		    getSector().getMesh().setNeedingLightRecalc(true);
	}
	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}
	public String getLightID() {
		return lightID;
	}
	public void setLightID(String lightID) {
		this.lightID = lightID;
	}

	/**
	 * Override setSector to provide ability to tell the sector it needs
	 * to be relit when this light is set to point to it.
	 */
	public void setSector(E3DSector sector) {
		super.setSector(sector);
		if(getSector() != null)
		    getSector().getMesh().setNeedingLightRecalc(true);
	}
	/**
	 * Translate the light
	 * @param translationAmt
	 */
	public void translate(E3DVector3F translationAmt)
	{
		checkSectorChangeDuringMovement(position, position.add(translationAmt));
		
	    this.position.addEqual(translationAmt);
	    if(getSector() != null)
	        getSector().getMesh().setNeedingLightRecalc(true);
	}

}
