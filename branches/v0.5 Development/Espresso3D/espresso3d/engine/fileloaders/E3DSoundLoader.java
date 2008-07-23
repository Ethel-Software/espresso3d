/*
 * Created on Apr 13, 2005
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
package espresso3d.engine.fileloaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.sun.media.sound.WaveFileReader;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.exceptions.E3DInvalidFileFormatException;
import espresso3d.engine.exceptions.E3DInvalidSoundFormatException;
import espresso3d.engine.lowlevel.sound.E3DWaveFile;
import espresso3d.engine.sound.E3DSound;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DSoundLoader extends E3DFileLoader
{
    public static void loadSoundSet(E3DEngine engine, String soundSetFileName, HashMap retSoundSet) throws E3DInvalidSoundFormatException, E3DInvalidFileFormatException, IOException
    {
        InputStream is = null;
        BufferedReader soundSetFile = null;
        
        try{
            is = openFile(soundSetFileName);
            soundSetFile = new BufferedReader(new InputStreamReader(is));

            readSoundSet(engine, soundSetFile, retSoundSet);
            
        }finally{
            if(soundSetFile != null)
                soundSetFile.close();
            closeFile(is);
        }
    }
    private static void readSoundSet(E3DEngine engine, BufferedReader soundSetFile, HashMap retSoundSet)  throws E3DInvalidFileFormatException, E3DInvalidSoundFormatException, IOException
    {
        StringTokenizer tokenizer = null;
        String line = "";
        String path = "", soundName = "";
        
        int i=0;
        while((line = soundSetFile.readLine()) != null)
        {
            i=0;
            tokenizer = new StringTokenizer(line, " ,;");
            while(tokenizer.hasMoreTokens())
            {
                if(i == 0)
                    soundName = tokenizer.nextToken();
                else if(i == 1)
                {
                    path = tokenizer.nextToken();
                    if(!retSoundSet.containsKey(soundName)) //dont' load it twice
                    {
                            retSoundSet.put(soundName, loadSoundIntoAL(engine, soundName, loadSound(path)));
                    }
                }
                else if(i >= 2) //too many tokens
                    throw new E3DInvalidFileFormatException();
                
                i++;
            }
            if(i == 0)
                continue;
            else if(i < 2) //too few tokens
                throw new E3DInvalidFileFormatException();
        
        }
    }
    
    public static E3DWaveFile loadSound(String fileName) throws IOException, E3DInvalidSoundFormatException
    {
        InputStream stream = null;
        
        try
        {
            stream = openFile(fileName);

            WaveFileReader reader = new WaveFileReader();
            AudioInputStream audioStream = reader.getAudioInputStream(stream);
            return new E3DWaveFile(audioStream);
        }catch(UnsupportedAudioFileException e){
            throw new E3DInvalidSoundFormatException(e.getMessage());
        }
        
        //Make sure NOT to close the stream in a finally block or else the whole audioStream thinks it gets closed
    }
    
    public static E3DSound loadSoundIntoAL(E3DEngine engine, String soundName, E3DWaveFile waveFile) throws E3DInvalidSoundFormatException, IOException
    {
        E3DSound sound = null;
        
        IntBuffer soundBuffer = BufferUtils.createIntBuffer(1);

        //Create the soundBuffer
        AL10.alGenBuffers(soundBuffer);

        if(AL10.alGetError() != AL10.AL_NO_ERROR)
            return null;
        
        //Generate the AL buffer for it
        AL10.alBufferData(soundBuffer.get(0), waveFile.getALFormat(), waveFile.getDataBuffer(), (int)waveFile.getSampleRate());
        
        sound = new E3DSound(engine, soundName, soundBuffer.get(0));
        
        return sound;
    }
}
