/*
 * Created on Dec 22, 2004
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
package espresso3d.engine.window.viewport.text;

import java.util.ArrayList;
import java.util.HashMap;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.window.viewport.E3DViewport;

/**
 * @author Curt
 *
 * The container for all text that needs to be displayed, regardless of
 * viewport.  This handles managing fonts (global to all viewports)
 * and adding messages (text) to specific viewports.
 * 
 */

public class E3DViewportPrinter extends E3DRenderable
{
    private ArrayList messageList;
    private E3DRenderTree renderTree;

//    private E3DSortedRenderableMap sortedMessageList;
    
    private E3DViewport viewport;
    
    /**
     *Construct a viewport printer.  This will be handled automatically by a viewport
     */
    public E3DViewportPrinter(E3DEngine engine, E3DViewport viewport)
    {
        super(engine);

        this.viewport = viewport;

        messageList = new ArrayList();
        renderTree = new E3DRenderTree(engine);
//        sortedMessageList = new E3DSortedRenderableMap();
    }

    
    /**
     * 
     * @param text The text to print to the viewport
     * @param fontName Name of a font that has been loaded already
     * @param fontSize The size the font should be displayed as.  The font size scales as the main window scales, NOT the viewport
     * @param position Position starting from x=0,y=0 at the bottom left corner of the viewport (NOT window). The font grows up and right from here
     * @param life How long (Seconds) the text should be printed.  -1 is infinity
     * @return
     *  returns the E3DMessage object that is printed in the viewport.  
     */
    public E3DMessage printToViewport(String text, String fontName, int fontSize, E3DVector2I position,  double life)
    {
    	return printToViewport(text, fontName, fontSize, new E3DVector4F(1.0, 1.0, 1.0, 1.0), position, life, new E3DBlendMode(getEngine(), E3DBlendMode.BLENDMODE_BLEND));
    }

    /**
     * 
     * @param text The text to print to the viewport
     * @param fontName Name of a font that has been loaded already
     * @param fontSize The size the font should be displayed as.  The font size scales as the main window scales, NOT the viewport
     * @param position Position starting from x=0,y=0 at the bottom left corner of the viewport (NOT window). The font grows up and right from here
     * @param life How long (Seconds) the text should be printed.  -1 is infinity
     * @param color A color overlay for the font. X=R, Y=G, Z=B. If the font is white, this will change the font to whatever color you specify Valid range is [0.0, 1.0]
     * @return
     *  returns the E3DMessage object that is printed in the viewport.  
     */
    public E3DMessage printToViewport(String text, String fontName, int fontSize, E3DVector4F color, E3DVector2I position, double life)
    {
    	return printToViewport(text, fontName, fontSize, color, position, life, new E3DBlendMode(getEngine(), E3DBlendMode.BLENDMODE_BLEND));
    }

    /**
     * 
     * @param text The text to print to the viewport
     * @param fontName Name of a font that has been loaded already
     * @param fontSize The size the font should be displayed as.  The font size scales as the main window scales, NOT the viewport
     * @param position Position starting from x=0,y=0 at the bottom left corner of the viewport (NOT window). The font grows up and right from here
     * @param life How long (Seconds) the text should be printed.  -1 is infinity
     * @param color A color overlay for the font. A=R, B=G, C=B, D=A. If the font is white, this will change the font to whatever color you specify Valid range is [0.0, 1.0]
     * @param blendMode The type of blending to use for the font if other than default BLENDMODE_BLEND.  The blendmodes are enumerated in E3DBlendedRenderable
     * @return
     *  returns the E3DMessage object that is printed in the viewport.  
     */
    public E3DMessage printToViewport(String text, String fontName, int fontSize, E3DVector4F color, E3DVector2I position, double life, E3DBlendMode blendMode)
    {
    	HashMap fontMap = viewport.getWindow().getFontMap();
        if(!fontMap.containsKey(fontName))
            return null;
        
        //Modify X and Y to be in viewport space, not window space
        position.addEqual(new E3DVector2I(viewport.getX(), viewport.getY()));

        E3DFont font = (E3DFont)fontMap.get(fontName);
        
        E3DMessage message = new E3DMessage(getEngine(), viewport, text, font, fontSize, position, life, color, blendMode);
        messageList.add(message);
        
//        sortedMessageList.addObject(message);
        return message;
    }
    
    /**
     * Remove a message from the viewport even if its life hasn't expired yet.
     * @param message
     */
    public void removeMessage(E3DMessage message)
    {
//    	sortedMessageList.removeObject(message);
    	messageList.remove(message);
    }
    /**
     * Rendering all messages
     * 
     */
    public void render() 
    {
        if(messageList.isEmpty())
        	return;

        double lastFrameTime = getEngine().getFpsTimer().getLastUpdateTimeSeconds();
        ArrayList quadList = null;
        E3DMessage message = null;
        renderTree.clear();
        for(int i=0; i < messageList.size(); i++)
        {
            message = (E3DMessage)messageList.get(i);

            quadList = message.getPositionedQuadList();
            if(quadList == null)
            	continue;

            //Update the message, if its time (seconds) has expired, remove it
            if(!message.update(lastFrameTime))
            {
            	removeMessage(message);
                i--;
            }
            else
	            renderTree.getQuadHandler().addAll(quadList);
    	}
        renderTree.render();
    }

    /**
     * Get the viewport the viewportPrinter is within
     */
	public E3DViewport getViewport()
	{
		return viewport;
	}
	
	/**
	 * Set the viewport the viewportPrinter belongs to.  This should not be changed.
	 * @param viewport
	 */
	public void setViewport(E3DViewport viewport)
	{
		this.viewport = viewport;
	}
}
