/*
 * Created on Jul 26, 2005
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
package espresso3d.engine.lowlevel.list;

import java.util.List;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DListHelper 
{
    /**
     * Get pieceNum of a list split into multiple pieces
     * @param pieceNum
     * @param maxPieces
     * @param listToSplit
     * @return
     *  The correct list piece or null if the list cannot be split
     */
    public static List getListPiece(int pieceNum, int maxPieces, List listToSplit)
    {
        if(listToSplit.size() < maxPieces)
            return null;
        
        int sizeOfPiece = (int)(listToSplit.size() / (double)maxPieces);
        if(pieceNum == maxPieces - 1)
            return listToSplit.subList(pieceNum * sizeOfPiece, listToSplit.size());
        else
            return listToSplit.subList(pieceNum * sizeOfPiece, (pieceNum+1) * sizeOfPiece); //inclusive, exclusive so no need for -1 on last piece
        
    }

}
