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
package espresso3d.engine.logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DEngineLogger extends E3DEngineItem
{
    public static final String SEVERITY_INFO = "INFO:";
    public static final String SEVERITY_DEBUG = "DEBUG:";
    public static final String SEVERITY_WARNING = "WARNING:";
    public static final String SEVERITY_ERROR = "ERROR:";
    
    BufferedOutputStream out;
    
    boolean debug = false;
    
    public E3DEngineLogger(E3DEngine engine)
    {
        this(engine, engine.getWorkingDirectory() + "\\espresso3d.log");
    }
    
    public E3DEngineLogger(E3DEngine engine, String logFileName)
    {
        super(engine);

        try{
            out = new BufferedOutputStream(new FileOutputStream(logFileName));
            writeLine("", "=== Espresso3D v"+E3DEngine.ENGINE_VERSION + " Log === www.espresso3d.com === " + new Date());
        }catch(IOException e){
            System.out.println("Unable to initalize engine log file: " + logFileName);
            out = null;
        }
    }
    
    public void writeMessage(String severity, String message)
    {
    	if(debug)
    	{
    		if(SEVERITY_ERROR.equals(severity))
    			System.err.println(severity + message);
    		else
    			System.out.println(severity + message);
    	}
        if(out != null)
        {
            try{
                out.write((severity + message).getBytes());
                out.flush();
            }catch(IOException e){
                System.out.println("Unable to write message to log file");
                System.out.println("E3DLogMessage: " + severity + message);
            }
        }
        else
            System.out.println("E3DLogMessage: " + message);
    }
    
    public void writeLine(String severity,String message)
    {
        writeMessage(severity, message + "\n");
    }
    protected void finalize() throws Throwable 
    {
        super.finalize();

        if(out != null)
        {
            out.flush();
            out.close();
        }
    }

    /**
     * If true, all log messages are also printed to the console.
     * Severity_error's will be printed as sys errs
     * @return
     */
	public boolean isDebug() {
		return debug;
	}

	/**
     * If set to true, all log messages are also printed to the console
     * Severity_error's will be printed as sys errs
	 * @param debug
	 */
	public void setDebug(boolean debug) 
	{
		this.debug = debug;
	}
    
}
