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
import java.util.StringTokenizer;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.vector.E3DVector2I;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.base.E3DTexturedRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.window.viewport.E3DViewport;

/**
 * @author Curt
 *
 * A message is a single piece of text that was added to the viewport for printing
 * via the viewport's viewportPrinter.  This is constructing out of the String message to be displayed,
 * font, etc.  It is constructed by the viewport printer and passed back to the user after it is
 * added to the viewport for rendering.
 */
public class E3DMessage extends E3DTexturedRenderable 
{
    private E3DViewport viewport;
    private E3DFont font;
    private String text;
    private ArrayList textPieces; //holds parsed text (line delimitted)
    private double life;
    private double age;
    private E3DVector2I position;
    private double fontSize;
    private E3DVector4F color;
    
    /**
     * Create a message to be printed by the viewport printer.
     * 
     * This defaults to be rendered blended
     * 
     * @param engine
     * @param viewport
     * @param text
     * @param font
     * @param fontSize
     * @param position
     * @param life
     * @param color
     */
    public E3DMessage(E3DEngine engine, E3DViewport viewport, String text, E3DFont font, double fontSize, E3DVector2I position, double life, E3DVector4F color)
    {
    	this(engine, viewport, text, font, fontSize, position, life, color, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_BLEND));
    }
    /**
     * Create a message to be printed by the viewport printer.
     * 
     * @param engine
     * @param viewport
     * @param text
     * @param font
     * @param fontSize
     * @param x
     * @param y
     * @param life
     * @param color
     * @param blendMode Type of blending to use when rendering
     */
    public E3DMessage(E3DEngine engine, E3DViewport viewport, String text, E3DFont font, double fontSize, E3DVector2I position, double life, E3DVector4F color, E3DBlendMode blendMode)
    {
        super(engine, new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED), blendMode, font.getTexture().getTextureName());
        
        this.textPieces = new ArrayList();
        
        this.viewport = viewport;
        setText(text); //call the setter so it will actually build the text pieces
        this.font = font;
        this.fontSize = fontSize;
        this.position = position;
        this.life = life;
        this.age = 0;
        this.color = color;
    }
    
    /* (non-Javadoc)
     * @see espresso3d.engine.renderer.base.E3DRenderable#renderAtPosition(espresso3d.engine.lowlevel.E3DVector3F)
     */
    public void render() 
    {
        ArrayList quadList = getPositionedQuadList();
        
        if(quadList.size() > 0)
        {
        	E3DRenderTree renderTree = new E3DRenderTree(getEngine());
        	renderTree.getQuadHandler().addAll(quadList);
        	renderTree.render();
        }
    }

    /**
     * This will return an arraylist of positioned, textured quads in 3D space for the message.  This is
     *  usually used internally by the font renderer only.
     * @return
     */
    public ArrayList getPositionedQuadList()
    {
        ArrayList quadList = new ArrayList();
        E3DQuad quad;
        E3DQuad quadCopy;
        char symbol;
        E3DVector3F unprojA, unprojB, unprojC, unprojD;
        E3DSymbol e3dsymbol;
        
        double x = position.getX();
        double y = position.getY();

        double curXPosition = 0.0;
        double scaledFontSize = fontSize * 0.1;
    	double aspectRatio = getEngine().getWindow().getWidth() / ((double)getEngine().getWindow().getHeight());
    	double widthScalar = getEngine().getWindow().getWidth() * 0.001;
    	double heightScalar = (getEngine().getWindow().getHeight() * 0.001) * aspectRatio;

        String line;
        int curRow = 0, curColumn = 0;

        for(int i=0; i<textPieces.size(); i++)
        {
            curXPosition = 0.0;

            line = (String)textPieces.get(i);
            for(curColumn = 0; line != null && curColumn < line.length(); curColumn++)
	        {
	            symbol = line.charAt(curColumn);
	
	            e3dsymbol = font.getFontSymbol(symbol);
	            if(e3dsymbol == null)
	            	continue;
	            
	            quad = e3dsymbol.getQuad();
	            if(quad == null)
	                continue;
	            
	            quadCopy = new E3DQuad(quad);
	            quadCopy.setBlendMode(new E3DBlendMode(getBlendMode()));
	            quadCopy.setRenderMode(new E3DRenderMode(getRenderMode()));
	            
	            //Set the color
	            quadCopy.setVertexColor(color, color, color, color);
	            
            	double symbolHeight = scaledFontSize * heightScalar * e3dsymbol.getSymbolHeightPixels();
            	double symbolWidth = scaledFontSize * widthScalar * e3dsymbol.getSymbolWidthPixels();
            	
            	//bottom left
 	            unprojA = viewport.unprojectPoint(new E3DVector3F(x + curXPosition, y + (-curRow * symbolHeight), 0.1));
	            //top left
	            unprojB = viewport.unprojectPoint(new E3DVector3F(x + curXPosition,  y + (-curRow * symbolHeight) + symbolHeight, 0.1));
	            //top right
	            unprojC = viewport.unprojectPoint(new E3DVector3F(x + curXPosition + symbolWidth, y + (-curRow * symbolHeight) + symbolHeight, 0.1));
	            //bottom right
	            unprojD = viewport.unprojectPoint(new E3DVector3F(x + curXPosition + symbolWidth, y + (-curRow * symbolHeight), 0.1));

	            curXPosition += (e3dsymbol.getSymbolWidthPixels()+1) * scaledFontSize * widthScalar;// + (.001 * widthOverHeight); //put a little space after each letter
	            
//	            quadCopy.setVertexPos(unprojA, unprojB, unprojC, unprojD);
	            quadCopy.setVertexPos(unprojD, unprojC, unprojB, unprojA);
	            quadList.add(quadCopy);
	            
	        }
            curRow++;
        }
        
        return quadList;
    }
    
    /**
     * This returns the X, Y coords of the cursor after the last character in the message.
     * This is useful for creating another message that will start directly after the current message
     *  for things like a change in colors.
     * @return
     */
    public E3DVector2I getCursorPositionAfterMessage()
    {
        char symbol;
        E3DSymbol e3dsymbol;
        
        double x = position.getX();
        double y = position.getY();

        double curXPosition = x;
        double curYPosition = y;
        
        String line;
        int curColumn = 0;
        
    	double scaledFontSize = fontSize * 0.1;
    	double aspectRatio = getEngine().getWindow().getWidth() / ((double)getEngine().getWindow().getHeight());
    	double widthScalar = getEngine().getWindow().getWidth() * .001;
    	double heightScalar = (getEngine().getWindow().getHeight() * .001) * aspectRatio;
    	double lastSymbolHeight = 0.0;
    	
        for(int i=0; i<textPieces.size(); i++)
        {
        	curXPosition = x;

            line = (String)textPieces.get(i);
            for(curColumn = 0; line != null && curColumn < line.length(); curColumn++)
	        {
	            symbol = line.charAt(curColumn);
	
	            e3dsymbol = font.getFontSymbol(symbol);
	            if(e3dsymbol == null)
	            	continue;
	            lastSymbolHeight = e3dsymbol.getSymbolHeightPixels();
	                        	
	            curXPosition += (e3dsymbol.getSymbolWidthPixels()+1) * scaledFontSize * widthScalar;// + (.001 * widthOverHeight); //put a little space after each letter
	        }
            if(i + 1 < textPieces.size()) //if it has more
            	curYPosition -= lastSymbolHeight * scaledFontSize * heightScalar;
        }
    	
        return new E3DVector2I(((int)(curXPosition - viewport.getX())), ((int)(curYPosition - viewport.getY())));
    }

    /**
     * Return the font object the message is bound to
     */
    public E3DFont getFont() {
        return font;
    }

    /**
     * Change the font object the message should use
     * @param font
     */
    public void setFont(E3DFont font) {
        this.font = font;
        if(font != null)
        	setTexture(font.getTexture());
    }
    
    /**
     * Called by the printer to update its life
     *@return
     * returns false if this message's life has expired and it should be removed from the viewport printer
     */
    protected boolean update(double lastFrameTimeSeconds)
    {
        if(life > 0) //-1 lives forever
        {
	        this.age += lastFrameTimeSeconds;
	        if(age > life)
	            return false;
	        else
	            return true;
        }
        else return true;
    }
    
    /**
     * Get the life of the message, or, how many seconds it will be rendered in the engine.
     * -1 is infinity.
     * @return
     */
    public double getLife() {
        return life;
    }
    
    /**
     * Get the life of the message, or, how many seconds it will be rendered in the engine.
     * -1 is infinity.
	 * @param life Number of seconds the message should be rendered
	 */    
    protected void setLife(double life) {
        this.life = life;
    }
    
    /**
     * Get the text of the message that will be rendered in the viewport.
     * @return
     */
    public String getText() {
        return text;
    }
    
    /**
     * Change the messages text
     * @param text
     */
    public void setText(String text) {
        this.text = text;
        buildTextPieces(); //build the pieces
    }

    /**
     * Since string tokenizer is slow, we only want to tokenize text when it changes
     *
     */
    private void buildTextPieces()
    {
    	textPieces.clear();
    	StringTokenizer tokenizer = new StringTokenizer(text, "\n");
    	while(tokenizer.hasMoreTokens())
    		textPieces.add(tokenizer.nextToken());
    }
    
    /**
     * Return the age, or, how long the message has been rendered
     * @return
     */
    public double getAge()
	{
		return age;
	}

    protected void setAge(double age)
	{
		this.age = age;
	}

    /**
     * Return the color of the message
     * @return
     */
    public E3DVector4F getColor()
	{
		return color;
	}
    
    /**
     * Set the color of the message.  
     * @param color
     */
	public void setColor(E3DVector4F color)
	{
		this.color = color;
	}
	
	/**
	 * Return the size of the font for the message
	 * @return
	 */
	public double getFontSize()
	{
		return fontSize;
	}
	
	/**
	 * Change the size of the font the message is using.  Will increase or decrease its size
	 * @param fontSize Size of the font (or scalar for the message)
	 */
	public void setFontSize(double fontSize)
	{
		this.fontSize = fontSize;
	}
	
	/**
	 * Get the 2D (x,y) position the message starts at.  
	 * The position is the bottom left corner of the first character of the message
	 * and is in viewport space.
	 * 
	 * @return
	 */
	public E3DVector2I getPosition()
	{
		return position;
	}
	
	/**
	 * Set the 2D (x,y) position the message starts at.  
	 * The position is the bottom left corner of the first character of the message
	 * and is in viewport space.
	 * @param position
	 */
	public void setPosition(E3DVector2I position)
	{
		this.position = position;
	}
}
