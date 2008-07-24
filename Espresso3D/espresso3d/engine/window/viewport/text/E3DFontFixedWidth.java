/*
 * Created on Jan 25, 2005
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
package espresso3d.engine.window.viewport.text;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.texture.E3DTexture;

/**
 * @author cmoxley
 *
 * A fixed with font is a font that has even spacing between all characters
 * 
 * A texture is divided into a grid of (numColumns x numRows) and each portion of the 
 * grid will be 1 symbol.  Symbols are then bound to a column/row.
 * 
 */
public class E3DFontFixedWidth extends E3DFont
{
    
    /**
     * Instantiate a fixed width font.
     * 
     * @param engine Engine the font will reside in
     * @param fontID Unique ID for the font
     * @param textureName Name of the texture (loaded from a textureset) that contains all the symbols for the font
     * @param numColumns Number of columns in the texture.  This will divide the texture up evenly into numColumns and use the space in-between the columns as the place the symbols will reside
     * @param numRows Number of rows in the texture.  This will divide the texture up evenly into numRows and use the space in-between the rows as the place the symbols will reside
     */
    public E3DFontFixedWidth(E3DEngine engine, String fontID, String textureName, int numColumns, int numRows)
    {
    	super(engine, fontID, textureName, numColumns, numRows);
    }

    /**
     * Instantiate a fixed width font.
     * 
     * @param engine Engine the font will reside in
     * @param fontID Unique ID for the font
     * @param textureName Name of the texture (loaded from a textureset) that contains all the symbols for the font
     * @param numColumns Number of columns in the texture.  This will divide the texture up evenly into numColumns and use the space in-between the columns as the place the symbols will reside
     * @param numRows Number of rows in the texture.  This will divide the texture up evenly into numRows and use the space in-between the rows as the place the symbols will reside
     * @param blendMode Type of blending to use for the font
     */
    public E3DFontFixedWidth(E3DEngine engine, String fontID, String textureName, int numColumns, int numRows, E3DBlendMode blendMode)
    {
    	super(engine, fontID, textureName, numColumns, numRows, blendMode);
    }
	
	
    /**
     * This binds a symbol (a single letter, number, etc) to
     * a certain position on this fonts texture.  That position will be where
     * the engine looks on the texture when the symbol is passed into a viewport print statement.
     * 
     * Row/Column starts in the upper left hand corner of the texture image with row=1, column=1.
     * 
     * For example, row=1 col=1 would give you the portion of the texture starting in the upper
     * left hand corner and extending right symbolWidths pixels and extending down symbolHeights pixels.
     * 
     * row=1, col=2 would have an upper left corner starting at symbolWidth + 1 and extending to symbolWidth * 2 and symbolHeight
     * 
     */    
    public void bindSymbolToTexturePosition(char symbol, int row, int column)
    {
    	E3DTexture texture = getTexture();
    	
        E3DSymbol e3dsymbol = new E3DSymbol(getEngine(), this, texture.getTextureName(), new E3DBlendMode(getBlendMode()), symbol, row, column, -1);

        getCharSymbolMapping().put(new Character(symbol), e3dsymbol);
    }

}
