/*
 * Created on Jul 18, 2004

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
package espresso3d.engine.fileloaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import espresso3d.engine.fileloaders.base.E3DGeometryLoader;
import espresso3d.engine.logger.E3DEngineLogger;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;
import espresso3d.engine.world.sector.portal.E3DAccuratePortal;
import espresso3d.engine.world.sector.portal.E3DFastPortal;
import espresso3d.engine.world.sector.portal.E3DPortal;

/**
 * @author espresso3d
 * 
 * 
 * Loads a map file's geometry, actors, particles, etc for a world
 */
public class E3DMapLoader extends E3DGeometryLoader
{
	private static final String MAPTAG_TEXTURESET = "TEXTURESET";
	private static final String MAPTAG_SECTOR = "SECTOR";
	private static final String MAPTAG_TERRAIN = "TERRAIN";
	private static final String MAPTAG_TRIANGLE = "TRIANGLE";
	private static final String MAPTAG_ACTOR = "ACTOR";
	private static final String MAPTAG_LIGHT = "LIGHT";
	private static final String MAPTAG_PORTAL = "PORTAL";
	private static final String MAPTAG_PORTAL_PORTALLINK = "PORTALLINK";
    private static final String MAPTAG_PARTICLESYSTEM = "PARTICLESYSTEM";
    private static final String PORTALTYPE_FAST = "FAST";
    private static final String PORTALTYPE_ACCURATE = "FAST";
    
    
    

	/**
	 * Load a map into the world from an external location
	 * 
	 * @param mapFileNameAndPath
	 * @param world
	 * @throws Exception
	 */
	public static void loadMap(String mapFileName, E3DWorld world) throws Exception
	{
        InputStream is = null;
        BufferedReader mapFile = null;
        
        try{
            is = openFile(mapFileName);
            mapFile = new BufferedReader(new InputStreamReader(is));
    		
    		readMap(mapFile, world);
        }finally{
            if(mapFile != null)
                mapFile.close();
            closeFile(is);
        }
	}

	private static void readMap(BufferedReader mapFile, E3DWorld world) throws Exception
	{
		StringTokenizer tokenizer = null;
		String line = "";
		String str;
		E3DSector sector;

		while((line = mapFile.readLine()) != null)
		{
			tokenizer = new StringTokenizer(line, TOKENIZER_CHARS);
			while(tokenizer.hasMoreTokens())
			{
				str = readNextValue(tokenizer);
				if(MAPTAG_TEXTURESET.equalsIgnoreCase(str))
				{
					String path = readNextValue(tokenizer);
					String jar = readNextValue(tokenizer);
					world.loadTextureSet(path);
				}
				else if(MAPTAG_SECTOR.equalsIgnoreCase(str))
				{
                    String sectorID = readNextValue(tokenizer);
					sector = readSector(mapFile, world, sectorID);
					if(sector != null)
						world.addSector(sector);
				}
				else if(MAPTAG_TERRAIN.equalsIgnoreCase(str))
				{
	
				}
                else if(COMMENT.equalsIgnoreCase(str))
                {
                    break; //skip whole line
                }
                else
					world.getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Unknown tag in mapfile: " + str);
				//else ERROR
			}
		}
	}
	
//	==========================================
//	Function: ReadSector
//	  Loads a sector (Which we are currently in)
//	  Called from LoadWorld after a sector
//	  is encountered
//	==========================================
	private static E3DSector readSector(BufferedReader mapFile, E3DWorld world, String sectorID) throws IOException
	{
		StringTokenizer tokenizer = null;
		E3DSector sector;
		E3DTriangle triangle;
		E3DPortal portal;
		E3DActor actor;
		E3DParticleSystem particleSystem;
		String str;
		String line;
		boolean startBlock = false;

        sector = new E3DSector(world.getEngine(), sectorID);
        
		//Read sector
        while((line = mapFile.readLine()) != null)
		{
			tokenizer = new StringTokenizer(line, TOKENIZER_CHARS);
			while(tokenizer.hasMoreTokens())
			{
				str = readNextValue(tokenizer);
		
				//Now read from { to } loading the body between
				//grab the opening brace
				if(START_BLOCK.equalsIgnoreCase(str))
				{
					startBlock = true;
				}
				else if(MAPTAG_TRIANGLE.equalsIgnoreCase(str))
				{
					if(!startBlock)
						return null; //TODO: error

                    TempTriangle tempTriangle = readTriangle(sector.getEngine(), mapFile);
                    if(tempTriangle != null)
                    {
                        triangle = tempTriangle.triangle;
                        if(triangle != null)
                            sector.getMesh().addTriangle(triangle);
                    }
				}
				else if(MAPTAG_ACTOR.equalsIgnoreCase(str))
				{
					if(!startBlock)
						return null; 
					
                    String preloadedActorName, actorID;
                    preloadedActorName = readNextValue(tokenizer);
                    actorID = readNextValue(tokenizer);

					actor = readActor(mapFile, world, preloadedActorName, actorID);
					if(actor != null)
						sector.addActor(actor);
				}
				else if(MAPTAG_PORTAL.equalsIgnoreCase(str))
				{
				    if(!startBlock)
				        return null;
                    
                    String portalType, portalID;
                    portalType = readNextValue(tokenizer);
                    portalID = readNextValue(tokenizer);
                    
				    portal = readPortal(mapFile, sector, portalType, portalID);
				    if(portal != null)
				        sector.addPortal(portal);
				}
				else if(MAPTAG_LIGHT.equalsIgnoreCase(str))
				{
				}
				else if(MAPTAG_PARTICLESYSTEM.equalsIgnoreCase(str))
				{
				    if(!startBlock)
				        return null;
                    
                    String particleSystemName = readNextValue(tokenizer);
                    
				    particleSystem = readParticleSystem(mapFile, world, particleSystemName);
				    if(particleSystem != null)
				        sector.addParticleSystem(particleSystem);
				}
                else if(COMMENT.equalsIgnoreCase(str))
                {
                    break; //skip whole line
                }
				else if(END_BLOCK.equalsIgnoreCase(str))
				{
					return sector;
				}
				else
					world.getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Unknown tag in sector: " + str);

			}
		}
		return null; //never hit an end block, invalid format, return null
	}

	/**
 	 *  returns a triangle that it parses
 	 *  out of the mapFile from our current position
	 *  (it assumes we have just encountered MAPTAG_TRIANGLE
	 * @param mapFile
	 * @param world
	 * @return
	 * @throws IOException
	 */
	private static E3DPortal readPortal(BufferedReader mapFile, E3DSector sector, String portalType, String portalID) throws IOException
	{
	    StringTokenizer tokenizer;
	    String str, line;
	    boolean startBlockFound = false;
        
        E3DVector3F[] vertices = new E3DVector3F[4];
        String linkedSectorID = null, linkedPortalID = null;
        
        while((line = mapFile.readLine()) != null)
        {
            tokenizer = new StringTokenizer(line, TOKENIZER_CHARS);
            while(tokenizer.hasMoreTokens())
            {
                str = tokenizer.nextToken();

                if(START_BLOCK.equals(str))
                {
                    startBlockFound = true;
                }
                else if(VERTICES.equals(str))
                {
                    if(!startBlockFound)
                        continue;
                    String subLine = mapFile.readLine();
                    
                    vertices = readPortalVertexInformation(new StringTokenizer(subLine, TOKENIZER_CHARS));
                }
                else if(MAPTAG_PORTAL_PORTALLINK.equals(str))
                {
                    if(!startBlockFound)
                        continue;
                    String subLine = mapFile.readLine();
                    StringTokenizer subTokenizer = new StringTokenizer(subLine, TOKENIZER_CHARS);
                    
                    linkedSectorID = readNextValue(subTokenizer);
                    linkedPortalID = readNextValue(subTokenizer);
                }
                else if(END_BLOCK.equals(str))
                {
                    if(portalID == null)
                    {
                        System.out.println("Portal does not have an ID.  Portal will be ignore.");
                        return null;
                    }
                    if(linkedSectorID == null || linkedPortalID == null)
                    {
                        System.out.println("Portal does not have a link definition for either sector or portal.  Portal will be ignore.");
                        return null;
                    }

                    E3DPortal portal = null;
                    if(PORTALTYPE_FAST.equals(portalType))
                    {
                        portal = new E3DFastPortal(sector.getEngine(), portalID, linkedSectorID, linkedPortalID, 
                                                            vertices[0], vertices[1], vertices[2], vertices[3]);
                    }
                    else if(PORTALTYPE_ACCURATE.equals(portalType))
                    {
                        portal = new E3DAccuratePortal(sector.getEngine(), portalID, linkedSectorID, linkedPortalID, 
                                                            vertices[0], vertices[1], vertices[2], vertices[3]);
                    }
                    else
    					sector.getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_WARNING, "Unknown portal type in definition: " + portalType + ". Portal will be ignored.");
                       
                    return portal;
                }
                else if(COMMENT.equalsIgnoreCase(str))
                {
                    break; //skip whole line
                }
                else
					sector.getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Unknown tag in portal definition: " + str);
            }
        }
		
        return null;
	}
	
    private static E3DVector3F[] readPortalVertexInformation(StringTokenizer vertexInfo)
    {
        double ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz;

        //Get vertices
        ax = Double.parseDouble(readNextValue(vertexInfo));
        ay = Double.parseDouble(readNextValue(vertexInfo));
        az = Double.parseDouble(readNextValue(vertexInfo));
        bx = Double.parseDouble(readNextValue(vertexInfo));
        by = Double.parseDouble(readNextValue(vertexInfo));
        bz = Double.parseDouble(readNextValue(vertexInfo));
        cx = Double.parseDouble(readNextValue(vertexInfo));
        cy = Double.parseDouble(readNextValue(vertexInfo));
        cz = Double.parseDouble(readNextValue(vertexInfo));
        dx = Double.parseDouble(readNextValue(vertexInfo));
        dy = Double.parseDouble(readNextValue(vertexInfo));
        dz = Double.parseDouble(readNextValue(vertexInfo));

        E3DVector3F[] vertices = new E3DVector3F[4];
        vertices[0] = new E3DVector3F(ax, ay, az);
        vertices[1] = new E3DVector3F(bx, by, bz);
        vertices[2] = new E3DVector3F(cx, cy, cz);
        vertices[3] = new E3DVector3F(dx, dy, dz);
     
        return vertices;
    }
        
	private static E3DActor readActor(BufferedReader mapFile, E3DWorld world, String preloadedActorName, String actorID) throws IOException
	{
        String line, str;
        boolean startBlockFound = false;
        StringTokenizer tokenizer;

        E3DVector3F position = null, forward = null, up = null;
        
        while((line = mapFile.readLine()) != null)
        {
            tokenizer = new StringTokenizer(line, TOKENIZER_CHARS);
            while(tokenizer.hasMoreTokens())
            {
                str = readNextValue(tokenizer);
                
                if(START_BLOCK.equalsIgnoreCase(str))
                    startBlockFound = true;
                else if(POSITION.equalsIgnoreCase(str))
                {
                    if(!startBlockFound)
                        continue;
                    

                    String subLine = mapFile.readLine();
                    position = readVector3F(subLine);
                }
                else if(FORWARD.equalsIgnoreCase(str))
                {
                    if(!startBlockFound)
                        continue;
                    
                    String subLine = mapFile.readLine();
                    forward = readVector3F(subLine);
                }
                else if(UP.equalsIgnoreCase(str))
                {
                    if(!startBlockFound)
                        continue;

                    String subLine = mapFile.readLine();
                    up = readVector3F(subLine);
                }
                else if(COMMENT.equalsIgnoreCase(str))
                {
                    break;
                }
                else if(END_BLOCK.equalsIgnoreCase(str))
                {
                    if(actorID == null)
                    {
                        System.out.println("ActorID can not be null.  Actor ignored.");
                        return null;
                    }
                    
                    if(!world.getPreloadedActorMap().containsKey(preloadedActorName))
                    {
                        System.out.println(preloadedActorName + " is not a recognized preloaded actor");
                        return null;
                    }
                    
                    E3DActor preloadedActor = (E3DActor)world.getPreloadedActorMap().get(preloadedActorName);

                    E3DActor newActor = null;       
                    try{
                        newActor = preloadedActor.onGetClone();
                        newActor.setActorID(actorID);
                        newActor.setPosition(position);
                        
                        //Get the up vec by crossing the default forward with the forward, and then rotate the whole model to the forward
                        E3DVector3F newForwardVec = forward;
                        E3DVector3F newUpVec = up;
                        newForwardVec.normaliseEqual(); //ensure its normalised
                        newUpVec.normaliseEqual(); //ensure its normalised
                        
                        E3DVector3F crossP = newActor.getOrientation().getForward().crossProduct(newForwardVec);
                        if(crossP.closeTo(0.0, 0.0, 0.0))     
                            crossP = newActor.getOrientation().getUp();
                        
                        newActor.rotate(newActor.getOrientation().getForward().angleBetweenRads(newForwardVec), crossP);

                        crossP = newActor.getOrientation().getUp().crossProduct(newUpVec);
                        if(crossP.closeTo(0.0, 0.0, 0.0))
                            crossP = newActor.getOrientation().getForward();
                        
                        newActor.rotate(newActor.getOrientation().getUp().angleBetweenRads(newUpVec), crossP);
                    }catch(Exception e){
                        e.printStackTrace();
                        return null;
                    }
                    
                    return newActor;
                }
                else
					world.getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Unknown tag in particle system def: " + str);
            }
        }
        return null;
	}

	private static E3DParticleSystem readParticleSystem(BufferedReader mapFile, E3DWorld world, String particleSystemName) throws IOException
	{
	    String line, str;
        boolean startBlockFound = false;
        StringTokenizer tokenizer;

        E3DVector3F position = null, forward = null, up = null;
        
        while((line = mapFile.readLine()) != null)
        {
            tokenizer = new StringTokenizer(line, TOKENIZER_CHARS);
            while(tokenizer.hasMoreTokens())
            {
                str = readNextValue(tokenizer);
                
                if(START_BLOCK.equalsIgnoreCase(str))
                    startBlockFound = true;
                else if(POSITION.equalsIgnoreCase(str))
                {
                    if(!startBlockFound)
                        continue;
                    
                    String subLine = mapFile.readLine();
                    position = readVector3F(subLine);
                }
                else if(FORWARD.equalsIgnoreCase(str))
                {
                    if(!startBlockFound)
                        continue;
                    
                    String subLine = mapFile.readLine();
                    forward = readVector3F(subLine);
                }
                else if(UP.equalsIgnoreCase(str))
                {
                    if(!startBlockFound)
                        continue;

                    String subLine = mapFile.readLine();
                    up = readVector3F(subLine);
                }
                else if(COMMENT.equalsIgnoreCase(str))
                {
                    break;
                }
                else if(END_BLOCK.equalsIgnoreCase(str))
                {
                    if(!world.getPreloadedParticleSystemMap().containsKey(particleSystemName))
                    {
    					world.getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_WARNING, particleSystemName + " is not a recognized preloaded particle system and will not be included in the map.");
                        return null;
                    }
                    
                    E3DParticleSystem preloadedParticleSystem = (E3DParticleSystem)world.getPreloadedParticleSystemMap().get(particleSystemName);

                    E3DParticleSystem newParticleSystem = null;     
                    try{
                        newParticleSystem = preloadedParticleSystem.onGetClone();
                        newParticleSystem.setPosition(position);
                        
                        //Get the up vec by crossing the default forward with the forward, and then rotate the whole model to the forward
                        E3DVector3F newForwardVec = forward;
                        E3DVector3F newUpVec = up;
                        newForwardVec.normaliseEqual(); //ensure its normalised
                        newUpVec.normaliseEqual(); //ensure its normalised
                        
                        E3DVector3F crossP = newParticleSystem.getOrientation().getForward().crossProduct(newForwardVec);
                        if(crossP.closeTo(0.0, 0.0, 0.0))
                            crossP = newParticleSystem.getOrientation().getUp();
                        
                        newParticleSystem.rotate(newParticleSystem.getOrientation().getForward().angleBetweenRads(newForwardVec), crossP);

                        crossP = newParticleSystem.getOrientation().getUp().crossProduct(newUpVec);
                        if(crossP.closeTo(0.0, 0.0, 0.0))
                            crossP = newParticleSystem.getOrientation().getForward();
                        
                        newParticleSystem.rotate(newParticleSystem.getOrientation().getUp().angleBetweenRads(newUpVec), crossP);
                    }catch(Exception e){
                        e.printStackTrace();
                        return null;
                    }
                    
                    return newParticleSystem;
                }
                else
					world.getEngine().getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Unknown tag in particle system def: " + str);
            }
        }
        return null;
	}	
}
