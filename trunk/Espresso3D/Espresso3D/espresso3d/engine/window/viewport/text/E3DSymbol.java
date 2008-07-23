/*
 * Created on Jan 24, 2005
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
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.base.E3DTexturedRenderable;

/**
 * @author cmoxley
 *
 * A symbol is an individual character of a message bound to a portion of a font
 */
class E3DSymbol extends E3DTexturedRenderable
{
	private E3DFont font; //the font the symbol is in
	private char symbol;
	private int row;
	private int column;
	private int symbolWidthPixels;
	private int symbolHeightPixels;
	private E3DQuad quad;

	public E3DSymbol(E3DEngine engine, E3DFont font, String texture, E3DBlendMode blendMode, char symbol, int row, int column)
	{
		this(engine, font, texture, blendMode, symbol, row, column, -1, -1);
	}
	
	public E3DSymbol(E3DEngine engine, E3DFont font, String texture, E3DBlendMode blendMode, char symbol, int row, int column, int symbolWidthPixels)
	{
		this(engine, font, texture, blendMode, symbol, row, column, symbolWidthPixels, -1);
	}
	
	public E3DSymbol(E3DEngine engine, E3DFont font, String texture, E3DBlendMode blendMode, char symbol, int row, int column, int symbolWidthPixels, int symbolHeightPixels)
	{
		super(engine, new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED), blendMode, texture);

		this.font = font;
		this.symbol = symbol;
		this.row = row;
		this.column = column;

		if(symbolWidthPixels == -1)
			this.symbolWidthPixels = ((int)(getTexture().getWidth() / ((double)font.getNumColumns())));
		else
			this.symbolWidthPixels = symbolWidthPixels;

		if(symbolHeightPixels == -1)
			this.symbolHeightPixels = ((int)(getTexture().getHeight() / ((double)font.getNumRows())));
		else
			this.symbolHeightPixels = symbolHeightPixels;
		
		calculateAndCreateTexturePositionedQuad();
	}
	private void calculateAndCreateTexturePositionedQuad()
	{
        double minTexY = 1.0 - ((1.0 / font.getNumRows()) * row);
        double maxTexY = 1.0 - ((1.0 / font.getNumRows()) * (row - 1));

        if(symbolWidthPixels < 0) //didn't specify a size, used the size of the cell for the row/column
		{
	        double minTexX = (1.0 / font.getNumColumns()) * (column - 1);
			double maxTexX = (1.0 / font.getNumColumns()) * column;

	        quad = new E3DQuad(getEngine(),
                       new E3DVector3F(0.5, -0.5, 0), 
                       new E3DVector3F(0.5, 0.5, 0), 
                       new E3DVector3F(-0.5, 0.5, 0), 
                       new E3DVector3F(-0.5, -0.5, 0), 
                       new E3DVector2F(maxTexX, minTexY), 
                       new E3DVector2F(maxTexX, maxTexY), 
                       new E3DVector2F(minTexX, maxTexY), 
                       new E3DVector2F(minTexX, minTexY), 
                       getTexture().getTextureName());
		}
		else //A specific size within the cell was given, so determine the middle of the cell, and create tex coords for 1/2 the width on either side of the middle
		{
			int texWidth = getTexture().getWidth();
			double pixelsPerColumn = texWidth * (1.0 / font.getNumColumns());
			double halfWidth = (pixelsPerColumn - getSymbolWidthPixels()) / 2.0;
			halfWidth = (1.0/texWidth) * halfWidth; //convert from halfWidthPixels to halfWidth normalised 0->1
	        double minTexX = ((1.0 / font.getNumColumns()) * (column - 1)) + halfWidth;
			double maxTexX = ((1.0 / font.getNumColumns()) * column) - halfWidth;

	        quad = new E3DQuad(getEngine(),
                    new E3DVector3F(0.5, -0.5, 0), 
                    new E3DVector3F(0.5, 0.5, 0), 
                    new E3DVector3F(-0.5, 0.5, 0), 
                    new E3DVector3F(-0.5, -0.5, 0), 
                    new E3DVector2F(maxTexX, minTexY), 
                    new E3DVector2F(maxTexX, maxTexY), 
                    new E3DVector2F(minTexX, maxTexY), 
                    new E3DVector2F(minTexX, minTexY), 
                    getTexture().getTextureName());
		}
	}

	public void render()
	{
		// TODO Auto-generated method stub
	}
	public int getColumn()
	{
		return column;
	}
	public int getRow()
	{
		return row;
	}
	public char getSymbol()
	{
		return symbol;
	}
	public int getSymbolWidthPixels()
	{
		return symbolWidthPixels;
	}
	public E3DFont getFont()
	{
		return font;
	}
	public void setFont(E3DFont font)
	{
		this.font = font;
	}
	public E3DQuad getQuad()
	{
		return quad;
	}
	public int getSymbolHeightPixels()
	{
		return symbolHeightPixels;
	}
}
