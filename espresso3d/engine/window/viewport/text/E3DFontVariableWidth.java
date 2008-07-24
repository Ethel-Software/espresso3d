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
 * A variable width font is a font that will scale as the window scales (NOT the viewport)
 * 
 * That way, no matter what size the window is, the font will appear to take the same amount of space
 */
public class E3DFontVariableWidth extends E3DFont
{
    /**
     * Construct a variable width font.
     * 
     * @param engine Engine the font will reside in
     * @param fontID Unique ID for the font
     * @param textureName Name of the texture (loaded from a textureset) that contains all the symbols for the font
     * @param numColumns Number of columns in the texture.  This will divide the texture up evenly into numColumns and use the space in-between the columns as the place the symbols will reside
     * @param numRows Number of rows in the texture.  This will divide the texture up evenly into numRows and use the space in-between the rows as the place the symbols will reside
     */
    public E3DFontVariableWidth(E3DEngine engine, String fontID, String textureName, int numColumns, int numRows)
    {
    	super(engine, fontID, textureName, numColumns, numRows);
    }

    /**
     * Construct a variable width font.
     * 
     * @param engine Engine the font will reside in
     * @param fontID Unique ID for the font
     * @param textureName Name of the texture (loaded from a textureset) that contains all the symbols for the font
     * @param numColumns Number of columns in the texture.  This will divide the texture up evenly into numColumns and use the space in-between the columns as the place the symbols will reside
     * @param numRows Number of rows in the texture.  This will divide the texture up evenly into numRows and use the space in-between the rows as the place the symbols will reside
     * @param blendMode Blending mode of the font
     */
    public E3DFontVariableWidth(E3DEngine engine, String fontID, String textureName, int numColumns, int numRows, E3DBlendMode blendMode)
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

     * @param symbol A single character
     * @param row Row in the texture the character resides
     * @param column Column in the texture the character resides
     * @param symbolWidthPixels True number of pixels the symbol is within the row/column box.  
     *   This will then be centered in the row/column to read the symbol and will then contain
     *   an exact width of the symbol so each symbol can be different widths (ie: variable width font).
     *   This symbol width can be calculated by hand, but I recommend using hte program BitmapFontBuilder (Search for it)
     *   to do this as it does it all from creating the texture to exporting the symbol widths.
     */
    public void bindSymbolToTexturePosition(char symbol, int row, int column, int symbolWidthPixels)
    {
    	E3DTexture texture = getTexture();
    	
        E3DSymbol e3dsymbol = new E3DSymbol(getEngine(), this, texture.getTextureName(), new E3DBlendMode(getBlendMode()), symbol, row, column, symbolWidthPixels);

        getCharSymbolMapping().put(new Character(symbol), e3dsymbol);
    }
	
}
