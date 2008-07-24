/*
 * Created on Jan 29, 2005
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
package espresso3d.engine.logo.font;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.window.viewport.text.E3DFontVariableWidth;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DLogoVerdanaFont extends E3DFontVariableWidth
{
    public E3DLogoVerdanaFont(E3DEngine engine)
    {
    	super(engine, "VerdanaBold", "VERDANABOLD", 16, 16);
		 
	    bindSymbolToTexturePosition(' ', 1, 1, 6);
	    bindSymbolToTexturePosition('!', 1, 2, 7);
	    bindSymbolToTexturePosition('"', 1, 3, 11);
	    bindSymbolToTexturePosition('#', 1, 4, 16);
	    bindSymbolToTexturePosition('$', 1, 5, 13);
	    bindSymbolToTexturePosition('%', 1, 6, 23);
	    bindSymbolToTexturePosition('&', 1, 7, 16);
	    bindSymbolToTexturePosition('\'', 1, 8, 6);
	    bindSymbolToTexturePosition('(', 1, 9, 10);
	    bindSymbolToTexturePosition(')', 1, 10, 10);
	    bindSymbolToTexturePosition('*', 1, 11, 13);
	    bindSymbolToTexturePosition('+', 1, 12, 16);
	    bindSymbolToTexturePosition(',', 1, 13, 7);
	    bindSymbolToTexturePosition('-', 1, 14, 8);
	    bindSymbolToTexturePosition('.', 1, 15, 7);
	    bindSymbolToTexturePosition('/', 1, 16, 12);

	    bindSymbolToTexturePosition('0', 2, 1, 13);
	    bindSymbolToTexturePosition('1', 2, 2, 13);
	    bindSymbolToTexturePosition('2', 2, 3, 13);
	    bindSymbolToTexturePosition('3', 2, 4, 13);
	    bindSymbolToTexturePosition('4', 2, 5, 13);
	    bindSymbolToTexturePosition('5', 2, 6, 13);
	    bindSymbolToTexturePosition('6', 2, 7, 13);
	    bindSymbolToTexturePosition('7', 2, 8, 13);
	    bindSymbolToTexturePosition('8', 2, 9, 13);
	    bindSymbolToTexturePosition('9', 2, 10, 13);
	    bindSymbolToTexturePosition(':', 2, 11, 7);
	    bindSymbolToTexturePosition(';', 2, 12, 7);
	    bindSymbolToTexturePosition('<', 2, 13, 16);
	    bindSymbolToTexturePosition('=', 2, 14, 16);
	    bindSymbolToTexturePosition('>', 2, 15, 16);
	    bindSymbolToTexturePosition('?', 2, 16, 11);
	    
	    bindSymbolToTexturePosition('@', 3, 1, 17);
	    bindSymbolToTexturePosition('A', 3, 2, 14);
	    bindSymbolToTexturePosition('B', 3, 3, 14);
	    bindSymbolToTexturePosition('C', 3, 4, 13);
	    bindSymbolToTexturePosition('D', 3, 5, 15);
	    bindSymbolToTexturePosition('E', 3, 6, 12);
	    bindSymbolToTexturePosition('F', 3, 7, 12);
	    bindSymbolToTexturePosition('G', 3, 8, 15);
	    bindSymbolToTexturePosition('H', 3, 9, 15);
	    bindSymbolToTexturePosition('I', 3, 10, 10);
	    bindSymbolToTexturePosition('J', 3, 11, 10);
	    bindSymbolToTexturePosition('K', 3, 12, 14);
	    bindSymbolToTexturePosition('L', 3, 13, 11);
	    bindSymbolToTexturePosition('M', 3, 14, 17);
	    bindSymbolToTexturePosition('N', 3, 15, 15);
	    bindSymbolToTexturePosition('O', 3, 16, 15);

	    bindSymbolToTexturePosition('P', 4, 1, 13);
	    bindSymbolToTexturePosition('Q', 4, 2, 15);
	    bindSymbolToTexturePosition('R', 4, 3, 14);
	    bindSymbolToTexturePosition('S', 4, 4, 13);
	    bindSymbolToTexturePosition('T', 4, 5, 12);
	    bindSymbolToTexturePosition('U', 4, 6, 15);
	    bindSymbolToTexturePosition('V', 4, 7, 14);
	    bindSymbolToTexturePosition('W', 4, 8, 20);
	    bindSymbolToTexturePosition('X', 4, 9, 14);
	    bindSymbolToTexturePosition('Y', 4, 10, 13);
	    bindSymbolToTexturePosition('Z', 4, 11, 12);
	    bindSymbolToTexturePosition('[', 4, 12, 10);
	    bindSymbolToTexturePosition('\\', 4, 13, 12);
	    bindSymbolToTexturePosition(']', 4, 14, 10);
	    bindSymbolToTexturePosition('^', 4, 15, 16);
	    bindSymbolToTexturePosition('_', 4, 16, 13);

	    bindSymbolToTexturePosition('`', 5, 1, 13);
	    bindSymbolToTexturePosition('a', 5, 2, 12);
	    bindSymbolToTexturePosition('b', 5, 3, 13);
	    bindSymbolToTexturePosition('c', 5, 4, 11);
	    bindSymbolToTexturePosition('d', 5, 5, 13);
	    bindSymbolToTexturePosition('e', 5, 6, 12);
	    bindSymbolToTexturePosition('f', 5, 7, 8);
	    bindSymbolToTexturePosition('g', 5, 8, 13);
	    bindSymbolToTexturePosition('h', 5, 9, 13);
	    bindSymbolToTexturePosition('i', 5, 10, 6);
	    bindSymbolToTexturePosition('j', 5, 11, 7);
	    bindSymbolToTexturePosition('k', 5, 12, 12);
	    bindSymbolToTexturePosition('l', 5, 13, 6);
	    bindSymbolToTexturePosition('m', 5, 14, 19);
	    bindSymbolToTexturePosition('n', 5, 15, 13);
	    bindSymbolToTexturePosition('o', 5, 16, 12);
	    
	    bindSymbolToTexturePosition('p', 6, 1, 13);
	    bindSymbolToTexturePosition('q', 6, 2, 13);
	    bindSymbolToTexturePosition('r', 6, 3, 9);
	    bindSymbolToTexturePosition('s', 6, 4, 11);
	    bindSymbolToTexturePosition('t', 6, 5, 8);
	    bindSymbolToTexturePosition('u', 6, 6, 13);
	    bindSymbolToTexturePosition('v', 6, 7, 12);
	    bindSymbolToTexturePosition('w', 6, 8, 18);
	    bindSymbolToTexturePosition('x', 6, 9, 12);
	    bindSymbolToTexturePosition('y', 6, 10, 12);
	    bindSymbolToTexturePosition('z', 6, 11, 11);
	    bindSymbolToTexturePosition('{', 6, 12, 13);
	    bindSymbolToTexturePosition('|', 6, 13, 10);
	    bindSymbolToTexturePosition('}', 6, 14, 13);
	    bindSymbolToTexturePosition('~', 6, 15, 16);
    }
}
