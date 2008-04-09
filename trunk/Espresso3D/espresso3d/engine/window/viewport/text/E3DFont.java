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

import java.util.HashMap;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.base.E3DTexturedRenderable;

/**
 * @author Curt
 *
 * Base font class
 */
abstract public class E3DFont extends E3DTexturedRenderable {
    private String fontID;
    private int glTextureID = -1;
    
    private int numColumns;
    private int numRows;
    
    private HashMap charSymbolMapping; //Contains a mapping of a Character symbol (or letter/number) to a Quad
    
    public E3DFont(E3DEngine engine, String fontID, String textureName, int numColumns, int numRows)
    {
    	this(engine, fontID, textureName, numColumns, numRows, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
    }

    public E3DFont(E3DEngine engine, String fontID, String textureName, int numColumns, int numRows, E3DBlendMode blendMode)
    {
        super(engine, new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED), blendMode, textureName);

        this.fontID = fontID;

        this.numColumns = numColumns;
        this.numRows = numRows;
        
        charSymbolMapping = new HashMap();
    }
    

    public E3DSymbol getFontSymbol(char symbol)
    {
    	Character csymbol = new Character(symbol);
        if(charSymbolMapping.containsKey(csymbol))
            return (E3DSymbol)charSymbolMapping.get(csymbol);
        return null;
    }
    
    public void unbindSymbolFromTexture(char symbol)
    {
    	Character csymbol = new Character(symbol);
        if(charSymbolMapping.containsKey(csymbol))
        	charSymbolMapping.remove(csymbol);
    }

    public String getFontID() {
        return fontID;
    }
    public void setFontID(String fontID) {
        this.fontID = fontID;
    }
    public int getGlTextureID() {
        return glTextureID;
    }
    public void setGlTextureID(int glTextureID) {
        this.glTextureID = glTextureID;
    }
    
	public void render()
	{
		// TODO Auto-generated method stub
	}  
	public HashMap getCharSymbolMapping()
	{
		return charSymbolMapping;
	}
	public int getNumColumns()
	{
		return numColumns;
	}
	public int getNumRows()
	{
		return numRows;
	}
}
