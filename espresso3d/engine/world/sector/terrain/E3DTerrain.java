/*
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
 * 
 */
package espresso3d.engine.world.sector.terrain;

import java.util.ArrayList;
import java.util.HashMap;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.texture.E3DTexture;
import espresso3d.engine.window.viewport.frustum.E3DViewFrustum2D;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.base.IE3DPortalEnabledItem;
import espresso3d.engine.world.sector.light.base.IE3DLightableObject;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;
import espresso3d.engine.world.sector.portal.E3DFastPortal;
import espresso3d.engine.world.sector.portal.E3DPortalCollision;

public class E3DTerrain extends E3DSector implements IE3DLightableObject
{
	private static final int MAX_PATCH_WIDTH = 32;
	private static final int MAX_PATCH_HEIGHT = 32;
	private static final int MAX_TERRAIN_PORTAL_HEIGHT = 18000; //9000 up and down
    
	private E3DTerrainPatch currentTerrainPatch; //path the camera is located or the "main" patch
    private E3DTerrainPatch terrainPatches[][];
	private boolean lit = true;
	private boolean needingLightRecalc = false;
	
	/**
	 * Create a terrain
	 * 
	 * @param engine
	 * @param terrainID
	 * @param min starting point of the terrain (bottom left corner)
	 * @param max ending point of terrain (top right corner)
	 * @param heights[x][y] - specify height coords for each vertex in the patch. x and y must be at least length 2 (i.e.: [0][1] filled in)
	 */
	public E3DTerrain(E3DEngine engine, String terrainID, E3DVector2F min, E3DVector2F max, double[][] heights, double heightScalar)
	{
		super(engine, terrainID);
		
		buildTerrain(engine, min, max, heights, heightScalar);
		
//		engine.addExternalRenderable(this, 100000000); //temp
		
		getMesh().setAlpha(1.0);
	}
	
	private void buildTerrain(E3DEngine engine, E3DVector2F min, E3DVector2F max, double[][] heights, double heightScalar)
	{
		if(heights.length < 1)
			return;
		if(heights[0].length < 1)
			return;
		
		this.terrainPatches = buildTrianglePatch(engine, min, max, heights.length, heights[0].length, heights, heightScalar);

//		this.getMesh().addTriangles(triangles);
	}
	
	/**
	 * Builds a list of E3DTerrainTriangle's in 2D.  These are created in the min/max plane.
	 * Initially 1 vertex per pixel of terrain image.  Rotation of this can happen later
	 * @param min
	 * @param max
	 */
	private E3DTerrainPatch[][] buildTrianglePatch(E3DEngine engine, E3DVector2F min, E3DVector2F max, int width, int height, double[][] heights, double heightScalar)
	{
		E3DTerrainTriangle triangle, neighbor1, neighbor2, neighbor3;
		
		ArrayList triangleList = new ArrayList(width*height*2);

		//Position changes from left to right.  Multiply this by the index of x/y in the loop
		double movementX = (max.getX() - min.getX()) / width;
		double movementY = (max.getY() - min.getY()) / height;
		double tMovementX = 1.0 / width;
		double tMovementY = 1.0 / height;
		
		System.out.println("Min: " + min + ", Max: " + max);
		System.out.println("MoveX: " + movementX + ", MoveY: " + movementY);
		
		double mx, mx1, my, my1;
		double tx=0, tx1=0, ty=0, ty1=0;
		
		E3DTerrainPatch terrainPatches[][] = buildTerrainPatches(width, height);

		E3DTexture texture = getEngine().getTexture("TERRAIN");
		
		for(int y=0; y < height - 1; y++)
		{
			for(int x = 0; x < width - 1; x++)
			{
				triangleList.clear();
				
				triangle = new E3DTerrainTriangle(engine);
				
				mx = movementX * x;
				mx1 = movementX * (x+1);
				my = movementY * y;
				my1 = movementY * (y+1);
				if(x > 0)
					tx = tx1;
				else
					tx = 0;
				if(y > 0)
					ty = ty1;
				else
					ty = 1;
				
				tx1 = tMovementX * (x + 1);
				ty1 = tMovementY * ((height - y - 1) + 1);

				if(x == width - 2)
					tx1 = 1;
				if(y == height - 2)
					ty1 = 0;
				
				tx = E3DConstants.clampIfLess(tx, 0);
				tx = E3DConstants.clampIfGreater(tx, 1);
				tx1 = E3DConstants.clampIfLess(tx1, 0);
				tx1 = E3DConstants.clampIfGreater(tx1, 1);
				ty = E3DConstants.clampIfLess(ty, 0);
				ty = E3DConstants.clampIfGreater(ty, 1);
				ty1 = E3DConstants.clampIfLess(ty1, 0);
				ty1 = E3DConstants.clampIfGreater(ty1, 1);
				
				triangle = buildTriangle(texture, mx, my, mx1, my1, mx, my1, 
										tx, ty, tx1, ty1, tx, ty1, 
										heights[x][y], heights[x+1][y+1], heights[x][y+1], heightScalar);
				triangleList.add(triangle);

				triangle = buildTriangle(texture, mx1, my, mx1, my1, mx, my, 
						tx1, ty, tx1, ty1, tx, ty,
						heights[x+1][y], heights[x+1][y+1], heights[x][y], heightScalar);
				
				
				triangleList.add(triangle);

				terrainPatches[(int)((int)((width-1)/(double)MAX_PATCH_WIDTH) * (x / (double)(width-1)))][(int)((int)((height-1)/(double)MAX_PATCH_HEIGHT) * (y / (double)(height-1)))].getMesh().addTriangles(triangleList);
			}
		}
		
		connectPatches(terrainPatches);

        if(terrainPatches != null && terrainPatches.length > 0 && terrainPatches[0].length > 0)
            setCurrentTerrainPatch(terrainPatches[0][0]);
        
		return terrainPatches;
	}
		
	private E3DTerrainTriangle buildTriangle(E3DTexture texture, double xa, double ya, double xb, double yb, double xc, double yc,
										double ua, double va, double ub, double vb, double uc, double vc,
										double ha, double hb, double hc, double heightScalar)
	{
		E3DTerrainTriangle triangle = new E3DTerrainTriangle(getEngine());
		triangle.setVertexColor(new E3DVector4F(1, 1, 1, 1), 
				new E3DVector4F(1, 1, 1, 1), 
				new E3DVector4F(1, 1, 1, 1));

		triangle.setTexture(texture);
		triangle.setTextureCoord(new E3DVector2F(ua, va), 
				new E3DVector2F(ub, vb), 
				new E3DVector2F(uc, vc));
		
		ha *= heightScalar;
		hb *= heightScalar;
		hc *= heightScalar;
		
		triangle.setVertexPos(new E3DVector3F(xa, ya, ha), 
				  new E3DVector3F(xb, yb, hb),
				  new E3DVector3F(xc, yc, hc));
		
		return triangle;
	}
	
	private E3DTerrainPatch[][] buildTerrainPatches(int width, int height)
	{
		E3DTerrainPatch terrainPatches[][] = new E3DTerrainPatch[(width-1)/MAX_PATCH_WIDTH][(height-1)/MAX_PATCH_HEIGHT];
		
		for(int x=0; x < (int)((width - 1.0) / MAX_PATCH_WIDTH); x++)
		{
			for(int y=0; y < (int)((height - 1.0) / MAX_PATCH_HEIGHT); y++)
			{
				terrainPatches[x][y] = new E3DTerrainPatch(getEngine(), getWorld(), getSectorID() + "X:" + x + "Y:" + y);
			}
		}
		
		System.out.println("Patches: X: "  + terrainPatches.length + " Y : " + terrainPatches[0].length);
		
		return terrainPatches;
	}
	
	/**
	 * Should build up patch relationships.  TODO: THis needs to add portals between patches
	 * @param terrainPatches
	 */
	private void connectPatches(E3DTerrainPatch terrainPatches[][])
	{
		if(terrainPatches.length == 0)
			return;
		
        double maxHeight = MAX_TERRAIN_PORTAL_HEIGHT / 2;
		E3DFastPortal portal;
		E3DVector3F a, b, c, d;
		for(int x=0; x < terrainPatches.length - 1; x++)
		{
			for(int y=0; y < terrainPatches[0].length - 1; y++)
			{
				Double minX = terrainPatches[x][y].getMinX();
				Double maxX = terrainPatches[x][y].getMaxX();
				Double minY = terrainPatches[x][y].getMinY();
				Double maxY = terrainPatches[x][y].getMaxY();

				if(x < terrainPatches.length - 2)
				{
					if(terrainPatches[x][y].addConnectedPatch(terrainPatches[x+1][y]))
					{
						a = new E3DVector3F(maxX.doubleValue(), minY.doubleValue(), -maxHeight);
						b = new E3DVector3F(maxX.doubleValue(), maxY.doubleValue(), -maxHeight);
						c = new E3DVector3F(maxX.doubleValue(), maxY.doubleValue(), maxHeight);
						d = new E3DVector3F(maxX.doubleValue(), minY.doubleValue(), maxHeight);
						
						portal = new E3DFastPortal(getEngine(), terrainPatches[x][y].getSectorID() + "pX: " + ((int)(x+1)) + "pY: " + y, 
								terrainPatches[x+1][y].getSectorID(), terrainPatches[x+1][y].getSectorID() + "pX: " + x + "pY: " + y,
								a, b, c, d);

						terrainPatches[x][y].addPortal(portal);
						
						portal = new E3DFastPortal(getEngine(), terrainPatches[x+1][y].getSectorID() + "pX: " + x + "pY: " + y, 
								terrainPatches[x][y].getSectorID(), terrainPatches[x+1][y].getSectorID() + "pX: " + ((int)(x+1)) + "pY: " + y,
								a, b, c, d);
						
						terrainPatches[x+1][y].addPortal(portal);
					}
				}
				if(y < terrainPatches[x].length - 2)
				{
					if(terrainPatches[x][y].addConnectedPatch(terrainPatches[x][y+1]))
					{
						a = new E3DVector3F(maxX.doubleValue(), maxY.doubleValue(),-maxHeight);
						b = new E3DVector3F(minX.doubleValue(), maxY.doubleValue(), -maxHeight);
						c = new E3DVector3F(minX.doubleValue(), maxY.doubleValue(), maxHeight);
						d = new E3DVector3F(maxX.doubleValue(), maxY.doubleValue(), maxHeight);

						portal = new E3DFastPortal(getEngine(), terrainPatches[x][y].getSectorID() + "pX: " + x + "pY: " + ((int)(y+1)),
								terrainPatches[x][y+1].getSectorID(), terrainPatches[x][y+1].getSectorID() + "pX: " + x + "pY: " + y,
								a, b, c, d);

						terrainPatches[x][y].addPortal(portal);
						
						portal = new E3DFastPortal(getEngine(), terrainPatches[x][y+1].getSectorID() + "pX: " + x + "pY: " + y, 
								terrainPatches[x][y].getSectorID(), terrainPatches[x][y+1].getSectorID() + "pX: " + x + "pY: " + ((int)(y+1)),
								a, b, c, d);
						
						terrainPatches[x][y+1].addPortal(portal);

					}
				}
				if(x > 0)
				{
					if(terrainPatches[x][y].addConnectedPatch(terrainPatches[x-1][y]))
					{
						a = new E3DVector3F(minX.doubleValue(), minY.doubleValue(), -maxHeight);
						b = new E3DVector3F(minX.doubleValue(), maxY.doubleValue(), -maxHeight);
						c = new E3DVector3F(minX.doubleValue(), maxY.doubleValue(), maxHeight);
						d = new E3DVector3F(minX.doubleValue(), minY.doubleValue(), maxHeight);

						portal = new E3DFastPortal(getEngine(), terrainPatches[x][y].getSectorID() + "pX: " + ((int)(x-1)) + "pY: " + y, 
								terrainPatches[x-1][y].getSectorID(), terrainPatches[x-1][y].getSectorID() + "pX: " + x + "pY: " + y,
								a, b, c, d);

						terrainPatches[x][y].addPortal(portal);
						
						portal = new E3DFastPortal(getEngine(), terrainPatches[x-1][y].getSectorID() + "pX: " + x + "pY: " + y, 
								terrainPatches[x][y].getSectorID(), terrainPatches[x-1][y].getSectorID() + "pX: " + ((int)(x-1)) + "pY: " + y,
								a, b, c, d);
						
						terrainPatches[x-1][y].addPortal(portal);
					
					}
					
				}
				if(y > 0)
				{
					if(terrainPatches[x][y].addConnectedPatch(terrainPatches[x][y-1]))
					{
						a = new E3DVector3F(maxX.doubleValue(), minY.doubleValue(), -maxHeight);
						b = new E3DVector3F(minX.doubleValue(), minY.doubleValue(), -maxHeight);
						c = new E3DVector3F(minX.doubleValue(), minY.doubleValue(), maxHeight);
						d = new E3DVector3F(maxX.doubleValue(), minY.doubleValue(), maxHeight);

						portal = new E3DFastPortal(getEngine(), terrainPatches[x][y].getSectorID() + "pX: " + x + "pY: " + ((int)(y-1)),
								terrainPatches[x][y+1].getSectorID(), terrainPatches[x][y+1].getSectorID() + "pX: " + x + "pY: " + y,
								a, b, c, d);

						terrainPatches[x][y].addPortal(portal);
						
						portal = new E3DFastPortal(getEngine(), terrainPatches[x][y-1].getSectorID() + "pX: " + x + "pY: " + y, 
								terrainPatches[x][y].getSectorID(), terrainPatches[x][y-1].getSectorID() + "pX: " + x + "pY: " + ((int)(y-1)),
								a, b, c, d);
						
						terrainPatches[x][y-1].addPortal(portal);

					}
					
				}
			}
		}
	}
	
    public void render(E3DViewFrustum2D frustum, E3DActor curActor, HashMap alreadyRenderedSectors)
    {
    	//Not really necessary yet, but when we add portals to connect terrain to other things it will be
    	super.render(frustum, curActor, alreadyRenderedSectors);
    	
    	for(int x = 0; x < terrainPatches.length - 1; x++)
    	{
        	for(int y = 0; y < terrainPatches[x].length - 1; y++)
        		terrainPatches[x][y].render(frustum, curActor, alreadyRenderedSectors);
    	}
    	
    }
    
    ////////////Methods having to do with specific meshes need to be here so they apply to the entire terrain

    public void setLit(boolean lit)
    {
    	if(terrainPatches != null)
    	{
        	for(int x = 0; x < terrainPatches.length - 1; x++)
        	{
            	for(int y = 0; y < terrainPatches[x].length - 1; y++)
            		terrainPatches[x][y].getMesh().setLit(lit);
        	}
    	}
    	
    	this.lit = lit;
    }

	public boolean isNeedingLightRecalc()
	{
		return needingLightRecalc;
	}

	public void setNeedingLightRecalc(boolean needingLightRecalc)
	{
    	if(terrainPatches != null)
    	{
        	for(int x = 0; x < terrainPatches.length - 1; x++)
        	{
            	for(int y = 0; y < terrainPatches[x].length - 1; y++)
            		terrainPatches[x][y].getMesh().setNeedingLightRecalc(needingLightRecalc);
        	}
    	}
		this.needingLightRecalc = needingLightRecalc;
	}

	public boolean isLit()
	{
		return lit;
	}
	
	public void setWorld(E3DWorld world)
	{
		super.setWorld(world);
		
    	if(terrainPatches != null)
    	{
        	for(int x = 0; x < terrainPatches.length - 1; x++)
        	{
            	for(int y = 0; y < terrainPatches[x].length - 1; y++)
            		terrainPatches[x][y].setWorld(world);
        	}
    	}
		
	}
	
    /**
     * When this sector is asked to check for portal collisions, it needs to look at onlyi
     * the "current" sector, or the sector the camera is in if any
     */
    public E3DPortalCollision checkPortalCollision(E3DVector3F startPos, E3DVector3F endPos) 
    {
        E3DTerrainPatch currentPatch = getCurrentTerrainPatch();
        if(currentPatch == null)
            return null;
  
//TODO: optimize similar to 1 line
//        E3DPortalCollision collision = currentPatch.checkPortalCollision(startPos, endPos);
        E3DPortalCollision collision = null;
        E3DTerrainPatch patch = null;
        for(int x=0; x < terrainPatches.length; x++)
        {
            for(int y=0; y < terrainPatches[x].length; y++)
            {
                patch = terrainPatches[x][y];
                collision = patch.checkPortalCollision(startPos, endPos);
                if(collision != null)
                    return collision;
            }
        }

        return null;
    }

    public E3DTerrainPatch getCurrentTerrainPatch() {
        return currentTerrainPatch;
    }

    public void setCurrentTerrainPatch(E3DTerrainPatch currentTerrainPatch) {
        this.currentTerrainPatch = currentTerrainPatch;
    }	
    
    /**
     * Checks all the patches in the terrain try to find the patch that contains
     * @param pos
     * @return
     */
    public E3DTerrainPatch findContainingTerrainPatch(E3DVector3F pos)
    {
        System.out.println("Here");
        E3DTerrainPatch patch = null;
        for(int x=0; x < terrainPatches.length; x++)
        {
            for(int y=0; y < terrainPatches[x].length; y++)
            {
                patch = terrainPatches[x][y];
                if(patch.isPointContainedInPatchPortals(pos))
                    return patch;
            }
        }
        
        return null;
    }
    
    /**
     * Something moves here - we have to figure out which patch to put it in
     */
    protected void moveToSector(IE3DPortalEnabledItem renderableMovingHere, E3DVector3F position)
    {
        super.moveToSector(renderableMovingHere, position);
        
        E3DTerrainPatch containing = findContainingTerrainPatch(position);
        if(containing == null)
            System.out.println("No containing patch for " + renderableMovingHere);
        else
            System.out.println("Containing patch: " + containing.getSectorID() + " for " + renderableMovingHere);
                   
    }
    
}

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    