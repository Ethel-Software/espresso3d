/*
 * Created on Mar 9, 2005
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
package espresso3d.engine.sound;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.exceptions.E3DInvalidFileFormatException;
import espresso3d.engine.exceptions.E3DInvalidSoundFormatException;
import espresso3d.engine.exceptions.E3DMissingEngineException;
import espresso3d.engine.fileloaders.E3DSoundLoader;
import espresso3d.engine.logger.E3DEngineLogger;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.sound.exceptions.E3DSoundOutOfSourcesException;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DSoundHandler extends E3DEngineItem 
{
    boolean soundEnabled = true;
    //Map of soundName's to their E3DSound object
    private HashMap soundMap;
    private HashMap playingSoundMap;
//    private HashMap loopingSoundMap;
    private IntBuffer scratchIntBuffer;
    private FloatBuffer orientationBuffer;
    
    //This is for the pool of sources
    private ArrayList freeSourceMap; //a queue of sources that are free essentially (Integer's)
    private HashMap usedSourceMap; //a map to the sources being used for easy removal/addition (Integer, Integer)
    
    public E3DSoundHandler(E3DEngine engine)
    {
        super(engine);

        engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Initializing sound handler and OpenAL audio");

        soundMap = new HashMap();
        playingSoundMap = new HashMap();
//        loopingSoundMap = new HashMap();
        
        freeSourceMap = new ArrayList();
        usedSourceMap = new HashMap();
        
        scratchIntBuffer = BufferUtils.createIntBuffer(1);
        orientationBuffer = BufferUtils.createFloatBuffer(6);
        
        try{
            AL.create();
        }catch(LWJGLException e){
            soundEnabled = false;
		    engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_WARNING, "Unable to initialize sound handler - audio is disabled. OpenAL doesn't appear to be supported.  Try upgrading the sound card drivers.");
        }
    }
    
    protected void finalize() throws Throwable {
        super.finalize();
        removeAllSounds();
    }
    
    public E3DSound getSound(String soundName)
    {
        if(soundMap.containsKey(soundName))
            return (E3DSound)soundMap.get(soundName);
        return null;
    }
    
    /**
     * Adds a sound to the preloaded sound list.  If the sound is already loaded, the existing sound will
     * be unloaded from memory and this new sound will be loaded in its place.
     * 
     * @param sound
     */
    public void addSound(E3DSound sound)
    {
        if(sound == null)
            return;
        if(sound.getSoundName() == null || "".equals(sound.getSoundName()))
            return;
        if(soundMap.containsKey(sound.getSoundName()))
        {
            discardAndRemoveSoundSourceAndBuffer(sound);
        }

        soundMap.put(sound.getSoundName(), sound);
    }
    
    public void removeSound(String soundName)
    {
        if(soundMap.containsKey(soundName))
        {
            E3DSound sound = (E3DSound)soundMap.get(soundName);
            IntBuffer soundBuffer = BufferUtils.createIntBuffer(sound.getAlSoundBufferID());
            freeSoundBufferData(soundBuffer);
            soundMap.remove(soundName);
        }
    }
    
    public void removeSound(E3DSound sound)
    {
        if(sound != null)
            removeSound(sound.getSoundName());
    }
    
    /**
     * Called if the sound needs to be completely unloaded
     * @param soundBuffer
     */
    private void freeSoundBufferData(IntBuffer soundBuffer)
    {
        if(soundEnabled)
            AL10.alDeleteBuffers(soundBuffer);        
    }
    
    /**
     * Called when a sound finishes playing
     * @param sourceBuffer
     */
    private void freeSourceData(IntBuffer sourceBuffer)
    {
        if(soundEnabled)
            AL10.alDeleteSources(sourceBuffer);
    }
    
    public void removeAllSounds()
    {
        IntBuffer soundBuffer = BufferUtils.createIntBuffer(soundMap.size());
        Iterator it = soundMap.entrySet().iterator();
        Map.Entry entry = null;
        E3DSound sound = null;
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            sound = (E3DSound)entry.getValue();
            
            soundBuffer.put(sound.getAlSoundBufferID());
        }
        freeSoundBufferData(soundBuffer);
        soundMap.clear();
    }
    
    /**
     * Loads the soundSet into memory.  Current sounds are not removed.
     * @param soundSetName
     * @param fromJar
     */
    public void loadSoundSet(String soundSetFileName) throws E3DMissingEngineException, E3DInvalidFileFormatException, E3DInvalidSoundFormatException, IOException
    {
        if(getEngine() == null)
            throw new E3DMissingEngineException();
        
        E3DSoundLoader.loadSoundSet(getEngine(), soundSetFileName, soundMap);
    }

    /**
     * Plays the specified sound 1 time and binds it to the sourceActor so as the sourceActor moves, the sound will also move.
     * 
     * @param soundName The name given to the sound that was loaded from the soundset (or manually)
     * @param sourceActor The actor this sound will be bound to (so when the actor moves, this sound moves with it)
     * @param pitch The pitch of the sound when it is played
     * @param volume The volume of the sound when it is played
     * @param referenceDistance This is the distance away you can be from the object and hear the sound at full volume (volume variable)
     * @param maxDistance The maximum distance away you can hear the sound
     * @param falloffFactor A falloff scalar.  The higher this is, the faster the sound will falloff.  Usually use 1
     * 
     * @return  Returns null if the sound is not found or a unique E3DSound3D object representing this sound or
     *  if the sound handler is out of free sources (sources are pooled and free up as other sounds end).  
     *  This will be used for checking if the sound is playing and stopping
     * the sound.  The sound will be removed from the system after the sound has stopped (either the end of the sound or
     * manual intervention).
     * @throws E3DSoundOutOfSourcesException - This is a completely recoverable exception and is used to indicate
     * that the engine is temporarily out of sources for sounds (ie: too many playing at one time).  As a sound stops,
     * the engine will free up that source for later use so sources will free up (unless they are all looping and never stop, in which case
     * if you really need a sound you should handle this and stop one to free it up)
     */
    public E3DSound3D play3DSound(String soundName, E3DActor sourceActor, double pitch, double volume, double referenceDistance, double maxDistance, double falloffFactor) throws E3DSoundOutOfSourcesException
    {
        E3DSound3D sound3D = setup3DSound(soundName, sourceActor, pitch, volume, referenceDistance, maxDistance, falloffFactor);
        if(sound3D == null)
            return null;
        
        if(soundEnabled)
            AL10.alSourcePlay(sound3D.getAlSourceID());
        playingSoundMap.put(sound3D.getKey(), sound3D);
        
        return sound3D;
    }
    
    /**
     * Loops the specified until manually stoppped and binds it to the sourceActor so as the sourceActor moves, the sound will also move.
     * 
     * @param soundName The name given to the sound that was loaded from the soundset (or manually)
     * @param sourceActor The actor this sound will be bound to (so when the actor moves, this sound moves with it)
     * @param pitch The pitch of the sound when it is played
     * @param volume The volume of the sound when it is played
     * @param referenceDistance This is the distance away you can be from the object and hear the sound at full volume (volume variable)
     * @param maxDistance The maximum distance away you can hear the sound
     * @param falloffFactor A falloff scalar.  The higher this is, the faster the sound will falloff.  Usually use 1
     * 
     * @return  Returns null if the sound is not found or a unique E3DSound3D object representing this sound.  This will be used for checking if the sound is playing and stopping
     * the sound.  The sound will be removed from the system after the sound has stopped (either the end of the sound or
     * manual intervention).
     * @throws E3DSoundOutOfSourcesException - This is a completely recoverable exception and is used to indicate
     * that the engine is temporarily out of sources for sounds (ie: too many playing at one time).  As a sound stops,
     * the engine will free up that source for later use so sources will free up (unless they are all looping and never stop, in which case
     * if you really need a sound you should handle this and stop one to free it up)
     */
    public E3DSound3D loop3DSound(String soundName, E3DActor sourceActor, double pitch, double volume, double referenceDistance, double maxDistance, double falloffFactor) throws E3DSoundOutOfSourcesException
    {
        E3DSound3D sound3D = setup3DSound(soundName, sourceActor, pitch, volume, referenceDistance, maxDistance, falloffFactor);
        if(sound3D == null)
            return null;
        
        if(soundEnabled)
        {
	        AL10.alSourcei(sound3D.getAlSourceID(), AL10.AL_LOOPING, AL10.AL_TRUE);
	        AL10.alSourcePlay(sound3D.getAlSourceID());
        }
        playingSoundMap.put(sound3D.getKey(), sound3D);
        
        return sound3D;
    }
    
    
    /**
     * This grabs a source out of the pool if there is free one, otherwise allocates it
     * @return An Integer sourceID or null if it couldn't allocate/get one from the pool
     */
    private Integer getSourceFromPool() throws E3DSoundOutOfSourcesException
    {
        if(freeSourceMap.size() > 0)
        {
            Integer ret = (Integer)freeSourceMap.get(0);
            freeSourceMap.remove(0);
            return ret;
        }
        else
        {
            try 
            {
                scratchIntBuffer.clear();
                if(soundEnabled)
                    AL10.alGenSources(scratchIntBuffer);
                Integer source = new Integer(scratchIntBuffer.get(0));
                usedSourceMap.put(source, source);
                return source;
            }catch(OpenALException e) //at maximum sources... can't do it
            {
                throw new E3DSoundOutOfSourcesException();
            }
        }
    }
    
    /**
     * Removes a source from the used pool and adds it back to the free pool
     * @param sourceID
     */
    private void returnSourceToPool(int sourceID)
    {
        Integer iSourceID = new Integer(sourceID);
        
        usedSourceMap.remove(iSourceID);
        freeSourceMap.add(iSourceID);
    }
    /**
     * Attempts to setup a 3d sound.  If an error occurs, it returns null and its not played (ie: if max buffers hit)
     * @param soundName
     * @param sourceActor
     * @param pitch
     * @param volume
     * @param referenceDistance
     * @param maxDistance
     * @param falloffFactor
     * @return
     */
    private E3DSound3D setup3DSound(String soundName, E3DActor sourceActor, double pitch, double volume, double referenceDistance, double maxDistance, double falloffFactor) throws E3DSoundOutOfSourcesException
    {
        E3DSound sound = getSound(soundName);
        if(sound == null)
            return null;

        Integer source = getSourceFromPool();
        if(source == null)
            return null;
        
        int sourceID = source.intValue();
        
        E3DSound3D sound3D = new E3DSound3D(sound); 
        sound3D.sourceActor = sourceActor;
        sound3D.alSourceID = sourceID;
        sound3D.pitch = pitch;
        sound3D.volume = volume;
        sound3D.referenceDistance = referenceDistance;
        sound3D.maxDistance = maxDistance;
        sound3D.falloffFactor = falloffFactor;
        
        sourceActor.addSound3DToAssociatedList(sound3D);
        
        try{
            if(soundEnabled)
            {
	            AL10.alSourcei(sourceID, AL10.AL_BUFFER,   sound.getAlSoundBufferID());
	            AL10.alSourcef(sourceID, AL10.AL_PITCH,    (float)pitch);
	            AL10.alSourcef(sourceID, AL10.AL_GAIN,     (float)volume);
	            AL10.alSource (sourceID, AL10.AL_POSITION, sourceActor.getOrientation().getPosition().getFloatBuffer());
	            AL10.alSourcef(sourceID, AL10.AL_MAX_DISTANCE, (float)maxDistance) ;        
	            AL10.alSourcef(sourceID, AL10.AL_REFERENCE_DISTANCE, (float)referenceDistance) ;
	            AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, (float)falloffFactor) ;
	            AL10.alSourcei(sourceID, AL10.AL_SOURCE_RELATIVE, AL10.AL_FALSE);
            }

        }catch(OpenALException e)
        {
            return null;
        }

        return sound3D;
    }

    /**
     * Plays a 2D sound, or a sound that has no position, 1 time.
     * 
     * @param soundName  The name given to the sound that was loaded from the soundset (or manually)
     * @param pitch The pitch of the sound when it is played
     * @param volume The volume of the sound when it is played
     * @return  Returns null if the sound is not found or a unique E3DSound2D object representing this sound.  This will be used for checking if the sound is playing and stopping
     * the sound.  The sound will be removed from the system after the sound has stopped (either the end of the sound or
     * manual intervention).  When the sound has been removed, the discarded flag will be set to true in the sound.
     * @throws E3DSoundOutOfSourcesException - This is a completely recoverable exception and is used to indicate
     * that the engine is temporarily out of sources for sounds (ie: too many playing at one time).  As a sound stops,
     * the engine will free up that source for later use so sources will free up (unless they are all looping and never stop, in which case
     * if you really need a sound you should handle this and stop one to free it up)
     */
    public E3DSound2D play2DSound(String soundName, double pitch, double volume) throws E3DSoundOutOfSourcesException
    {
        E3DSound2D sound2D = setup2DSound(soundName, pitch, volume);
        if(sound2D == null)
            return null;
        
        if(soundEnabled)
            AL10.alSourcePlay(sound2D.getAlSourceID());
        playingSoundMap.put(sound2D.getKey(), sound2D);
        
        return sound2D;
    }
    
    /**
     * Loops a 2D sound, or a sound that has no position, until it is manually stopped.
     * 
     * @param soundName  The name given to the sound that was loaded from the soundset (or manually)
     * @param pitch The pitch of the sound when it is played
     * @param volume The volume of the sound when it is played
     * @return  Returns null if the sound is not found or a unique E3DSound2D object representing this sound.  This will be used for checking if the sound is playing and stopping
     * the sound.  The sound will be removed from the system after the sound has stopped (either the end of the sound or
     * manual intervention).  When the sound has been removed, the discarded flag will be set to true in the sound.
     * @throws E3DSoundOutOfSourcesException - This is a completely recoverable exception and is used to indicate
     * that the engine is temporarily out of sources for sounds (ie: too many playing at one time).  As a sound stops,
     * the engine will free up that source for later use so sources will free up (unless they are all looping and never stop, in which case
     * if you really need a sound you should handle this and stop one to free it up)
     */
    public E3DSound2D loop2DSound(String soundName, double pitch, double volume) throws E3DSoundOutOfSourcesException
    {
        E3DSound2D sound2D = setup2DSound(soundName, pitch, volume);
        if(sound2D == null)
            return null;
        
        if(soundEnabled)
        {
	        AL10.alSourcei(sound2D.getAlSourceID(), AL10.AL_LOOPING, AL10.AL_TRUE);
	        AL10.alSourcePlay(sound2D.getAlSourceID());
        }
        playingSoundMap.put(sound2D.getKey(), sound2D);
        
        return sound2D;
    }
    
    /**
     * Attempts ot setup a 2d sound, if it can't, it returns null (ie: if sources is full)
     * @param soundName
     * @param pitch
     * @param volume
     * @return
     * @throws E3DSoundOutOfSourcesException - This is a completely recoverable exception and is used to indicate
     * that the engine is temporarily out of sources for sounds (ie: too many playing at one time).  As a sound stops,
     * the engine will free up that source for later use so sources will free up (unless they are all looping and never stop, in which case
     * if you really need a sound you should handle this and stop one to free it up)
     */
    private E3DSound2D setup2DSound(String soundName, double pitch, double volume) throws E3DSoundOutOfSourcesException
    {
        E3DSound sound = getSound(soundName);
        if(sound == null)
            return null;
        
        Integer source = getSourceFromPool();
        if(source == null)
            return null;
        
        int sourceID = source.intValue();
        
        E3DSound2D sound2D = new E3DSound2D(sound); 
        sound2D.alSourceID = sourceID;
        sound2D.pitch = pitch;
        sound2D.volume = volume;
        
        try
        {
            if(soundEnabled)
            {
	            AL10.alSourcei(sourceID, AL10.AL_BUFFER,   sound.getAlSoundBufferID());
	            
	            AL10.alSourcei(sourceID, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE) ;
	            AL10.alSourcef(sourceID, AL10.AL_REFERENCE_DISTANCE, 0) ;
	            AL10.alSourcef(sourceID, AL10.AL_MAX_DISTANCE, 0) ;
	            
	            AL10.alSourcef(sourceID, AL10.AL_PITCH,    (float)pitch);
	            AL10.alSourcef(sourceID, AL10.AL_GAIN,     (float)volume);
            }
        }catch(OpenALException e)
        {
            return null;
        }
        
        return sound2D;
    }
    
    /**
     * This is called by the engine to iterate through all playing sounds that have been set to play and ensure they are still playing.
     * If they have stopped, their al source is discarded.
     *
     */
    public void discardOldSounds()
    {
        Iterator it = playingSoundMap.entrySet().iterator();
        Map.Entry entry = null;
        E3DSound sound = null;
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            sound = (E3DSound)entry.getValue();
            
            if(isSoundStopped(sound))
            {
                discardSoundSource(sound);
                it.remove();
            }
        }
    }
    
    
    /**
     * This can be used to stop any sound, 2D or 3D by passing the sound object returned when the sound was set
     * to play or loop.  After the sound is stopped, its alsourceid is freed and its discarded flag is set to true.
     * @param sound  An instance of a sound - not a preloaded sound.
     */
    public void stopSound(E3DSound sound)
    {
        if(sound.discarded)
            return;

        if(soundEnabled)
            AL10.alSourceStop(sound.getAlSourceID());
        discardAndRemoveSoundSource(sound);
    }
    
    /**
     * Pauses a sound.  To resume its play, unpauseSound must be called.  When paused,
     * the sounds paused flag is set to true
     * @param sound
     */
    public void pauseSound(E3DSound sound)
    {
        if(sound.discarded)
            return;
        if(soundEnabled)
            AL10.alSourcePause(sound.getAlSourceID());
        sound.paused = true;
    }

    /**
     * Unpauses a paused sound.  This will do nothing if the sound hasn't already been paused
     * @param sound  An instance of a sound - not a preloaded sound.
     */
    public void unpauseSound(E3DSound sound)
    {
        if(!sound.discarded && sound.paused)
        {
            if(soundEnabled)
                AL10.alSourcePlay(sound.getAlSourceID());
            sound.paused = false;
        }
    }
    
    /**
     * Rewinds a sound to the beginning.  The sound keeps playing if it is playing, or stays paused if it is paused.
     * @param sound  An instance of a sound - not a preloaded sound.
     */
    public void rewindSound(E3DSound sound)
    {
        if(sound.discarded)
            return;
        
        if(soundEnabled)
        {
	        AL10.alSourceRewind(sound.getAlSourceID());
	        if(!sound.isPaused())
	            AL10.alSourcePlay(sound.getAlSourceID());
        }
    }

    private void discardAndRemoveSoundSourceAndBuffer(E3DSound sound)
    {
        discardAndRemoveSoundSource(sound);
        scratchIntBuffer.clear();
        scratchIntBuffer.put(sound.getAlSoundBufferID());
        freeSoundBufferData(scratchIntBuffer);
    }

    private void discardAndRemoveSoundSource(E3DSound sound)
    {
        playingSoundMap.remove(sound.getKey());
        discardSoundSource(sound);
    }
    
    private void discardSoundSource(E3DSound sound)
    {
//        scratchIntBuffer.clear();
//        scratchIntBuffer.put(sound.getAlSourceID());
        if(soundEnabled)
            AL10.alSourcei(sound.getAlSourceID(), AL10.AL_BUFFER, AL10.AL_NONE);

//        freeSourceData(scratchIntBuffer);
        sound.discarded = true;
        returnSourceToPool(sound.getAlSourceID());
    }
    
    /**
     * Checks if a sound is playing or not.  Paused does not count as playing
     * 
     * @param sound The sound to check
     * @return Returns true if it is currently playing, otherwise false.  After a sound has been stopped, it will
     * quickly be discarded so you may also want to check the sound's discarded flag.
     */
    public boolean isSoundPlaying(E3DSound sound)
    {
        if(!soundEnabled)
            return false;
        
        int state = AL10.alGetSourcei(sound.getAlSourceID(), AL10.AL_SOURCE_STATE);
        return (state == AL10.AL_PLAYING);
    }

    /**
     * Checks if a sound is paused or not using the AL state of the sound.  This should normally be the same value
     * that is retrieved from teh sounds paused flag (and it would be much faster to check that).
     * @param sound
     * @return
     */
    public boolean isSoundPaused(E3DSound sound)
    {
        if(!soundEnabled)
            return false;
        
        int state = AL10.alGetSourcei(sound.getAlSourceID(), AL10.AL_SOURCE_STATE);
        return (state == AL10.AL_PAUSED);
    }

    /**
     * Checks if a sound is stopped or not.  After a sound is stopped, its source will
     * quickly be discarded by the engine and its flag will be set to discarded meaning play must be called again.
     * @param sound
     * @return
     */
    public boolean isSoundStopped(E3DSound sound)
    {
        if(!soundEnabled)
            return false;
        int state = AL10.alGetSourcei(sound.getAlSourceID(), AL10.AL_SOURCE_STATE);
        return (state == AL10.AL_STOPPED);
    }
    /**
     * Updates the sounds position and velocity to be that of its source actor. Velocity can be null
     * if the actor isn't moving (just rotating)
     * @param velocity
     */
    public void update3DSoundOrientation(E3DSound3D sound3D, E3DVector3F velocity)
    {
        if(!soundEnabled)
            return;
        
        if(velocity != null)
            AL10.alSource (sound3D.getAlSourceID(), AL10.AL_VELOCITY, velocity.getFloatBuffer());
        AL10.alSource (sound3D.getAlSourceID(), AL10.AL_POSITION, sound3D.getSourceActor().getOrientation().getPosition().getFloatBuffer());
    }
    
    
    /**
     * Usually automatically called by the engine to be the current viewports camera actor
     * @param actor
     */
    public void setListener(E3DActor listenerActor)
    {
    	if(listenerActor == null)
    		return;
    	
        if(!soundEnabled)
            return;
        
        AL10.alListener(AL10.AL_POSITION,    listenerActor.getOrientation().getPosition().getFloatBuffer());
        AL10.alListener(AL10.AL_VELOCITY,    listenerActor.getOrientation().getForward().getFloatBuffer());
        orientationBuffer.clear();
        orientationBuffer.put(listenerActor.getOrientation().getForward().getFloatBuffer());
        orientationBuffer.put(listenerActor.getOrientation().getUp().getFloatBuffer());
        orientationBuffer.flip();
        AL10.alListener(AL10.AL_ORIENTATION, orientationBuffer);
    }
}
