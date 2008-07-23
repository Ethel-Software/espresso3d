/*
 * Created on Nov 12, 2004
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
package espresso3d.engine.lowlevel.geometry;

import java.nio.FloatBuffer;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.tree.base.IE3DHashableNode;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.base.E3DTexturedRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;

/**
 * @author Curt
 *
 * A quad is a renderable, texturable rectangle.  This is normally only
 * used for particle geometry.  But could be used elsewhere on the external renderable list
 * if desired.
 * 
 * Be sure to ONLY modify vertex position information through the quad object itself -- IE: do NOT get the vertex and modify the vertex object itself.  
 * It will cause normals to NOT be recalculated if you do so and will cause lots of problems.
 * 
 */
public class E3DQuad extends E3DTexturedRenderable implements IE3DHashableNode
{
    private E3DTexturedVertex[] vertices;
	private E3DVector3F normal;
	private E3DVector4F planeEquationCoords;
	private boolean needNormalRecalc = false;
	private boolean needPlaneEquationRecalc = false;
    private double alpha = 1.0;
    
	public E3DQuad(E3DEngine engine)
	{
		super(engine, null);
        vertices = new E3DTexturedVertex[4];
		vertices[0] = new E3DTexturedVertex(engine, 
                                        new E3DVector3F(0.0, 0.0, 0.0),
                                        new E3DVector4F(1.0, 1.0, 1.0, 1.0),
                                        new E3DVector2F(0.0, 0.0));
        vertices[1] = new E3DTexturedVertex(engine, 
                                        new E3DVector3F(0.0, 0.0, 0.0),
                                        new E3DVector4F(1.0, 1.0, 1.0, 1.0),
                                        new E3DVector2F(0.0, 0.0));
        vertices[2] = new E3DTexturedVertex(engine, 
                                        new E3DVector3F(0.0, 0.0, 0.0),
                                        new E3DVector4F(1.0, 1.0, 1.0, 1.0),
                                        new E3DVector2F(0.0, 0.0));
        vertices[3] = new E3DTexturedVertex(engine, 
                                        new E3DVector3F(0.0, 0.0, 0.0),
                                        new E3DVector4F(1.0, 1.0, 1.0, 1.0),
                                        new E3DVector2F(0.0, 0.0));
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
	}

	
	
	public E3DQuad(E3DQuad toCopy)
	{
		super(toCopy.getEngine(), new E3DRenderMode(toCopy.getRenderMode()), 
			  new E3DBlendMode(toCopy.getBlendMode()), 
			  toCopy.getTexture().getTextureName(), 
			  (toCopy.getTextureDetail0() == null ? null : toCopy.getTextureDetail0().getTextureName()), 
			  (toCopy.getTextureDetail1() == null ? null : toCopy.getTextureDetail1().getTextureName()));
		vertices = new E3DTexturedVertex[4];
        vertices[0] = new E3DTexturedVertex(toCopy.getEngine(), new E3DVector3F(toCopy.getVertexPosA()),
                                                                new E3DVector4F(toCopy.getVertexColorA()),
                                                                new E3DVector2F(toCopy.getTextureCoordA()));
        vertices[1] = new E3DTexturedVertex(toCopy.getEngine(), new E3DVector3F(toCopy.getVertexPosB()),
                                                                new E3DVector4F(toCopy.getVertexColorB()),
                                                                new E3DVector2F(toCopy.getTextureCoordB()));
        vertices[2] = new E3DTexturedVertex(toCopy.getEngine(), new E3DVector3F(toCopy.getVertexPosC()),
                                                                new E3DVector4F(toCopy.getVertexColorC()),
                                                                new E3DVector2F(toCopy.getTextureCoordC()));
        vertices[3] = new E3DTexturedVertex(toCopy.getEngine(), new E3DVector3F(toCopy.getVertexPosD()),
                                                                new E3DVector4F(toCopy.getVertexColorD()),
                                                                new E3DVector2F(toCopy.getTextureCoordD()));

        needNormalRecalc = true;
		needPlaneEquationRecalc = true;
	}

	public E3DQuad(E3DEngine engine, 
                E3DVector3F vertexPosA, E3DVector3F vertexPosB, E3DVector3F vertexPosC, E3DVector3F vertexPosD,
                E3DVector2F texCoordA, E3DVector2F texCoordB, E3DVector2F texCoordC, E3DVector2F texCoordD,
			  String textureName)
	{
		super(engine, textureName);
        vertices = new E3DTexturedVertex[4];
        vertices[0] = new E3DTexturedVertex(engine, vertexPosA,
                                                    new E3DVector4F(1.0, 1.0, 1.0, 1.0),
                                                    texCoordA);
        vertices[1] = new E3DTexturedVertex(engine, vertexPosB,
                                                    new E3DVector4F(1.0, 1.0, 1.0, 1.0),
                                                    texCoordB);
        vertices[2] = new E3DTexturedVertex(engine, vertexPosC,
                                                    new E3DVector4F(1.0, 1.0, 1.0, 1.0),
                                                    texCoordC);
        vertices[3] = new E3DTexturedVertex(engine, vertexPosD,
                                                    new E3DVector4F(1.0, 1.0, 1.0, 1.0),
                                                    texCoordD);
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
	}	
	
    public E3DQuad(E3DEngine engine, 
            E3DVector3F vertexPosA, E3DVector3F vertexPosB, E3DVector3F vertexPosC, E3DVector3F vertexPosD,
            E3DVector2F texCoordA, E3DVector2F texCoordB, E3DVector2F texCoordC, E3DVector2F texCoordD,
            E3DVector4F vertexColorA, E3DVector4F vertexColorB, E3DVector4F vertexColorC, E3DVector4F vertexColorD, 
          String textureName)
    {
        super(engine, textureName);
        vertices = new E3DTexturedVertex[4];
        vertices[0] = new E3DTexturedVertex(engine, vertexPosA,
                                                    vertexColorA,
                                                    texCoordA);
        vertices[1] = new E3DTexturedVertex(engine, vertexPosB,
                                                    vertexColorB,
                                                    texCoordB);
        vertices[2] = new E3DTexturedVertex(engine, vertexPosC,
                                                    vertexColorC,
                                                    texCoordC);
        vertices[3] = new E3DTexturedVertex(engine, vertexPosD,
                                                    vertexColorD,
                                                    texCoordD);
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
    }
    
    /**
     * Instantiate a quad that can only be used for accessing is various functions on vertexPos's.  Do NOT
     * pass to the rendering pipeline!!
     * @param engine
     * @param vertexPosA
     * @param vertexPosB
     * @param vertexPosC
     * @param vertexPosD
     */
    public E3DQuad(E3DEngine engine, 
            E3DVector3F vertexPosA, E3DVector3F vertexPosB, E3DVector3F vertexPosC, E3DVector3F vertexPosD)
    {
        super(engine, null);
        vertices = new E3DTexturedVertex[4];
        vertices[0] = new E3DTexturedVertex(engine, vertexPosA, null, null);
        vertices[1] = new E3DTexturedVertex(engine, vertexPosB, null, null);
        vertices[2] = new E3DTexturedVertex(engine, vertexPosC, null, null);
        vertices[3] = new E3DTexturedVertex(engine, vertexPosD, null, null);
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
    }

    /**
	 * To render outside of the normal rendering loop, this can be added
	 * to the external renderable list of the engine's because
	 * it implements render.
	 */
    public void render() {
    	E3DRenderTree renderTree = new E3DRenderTree(getEngine());
    	renderTree.getQuadHandler().add(this);
    	renderTree.render();
    }

//    private boolean isNeedNormalRecalc() {
//        return needNormalRecalc;
//    }
//    private void setNeedNormalRecalc(boolean needNormalRecalc) {
//        this.needNormalRecalc = needNormalRecalc;
//    }
//    private boolean isNeedPlaneEquationRecalc() {
//        return needPlaneEquationRecalc;
//    }
//    private void setNeedPlaneEquationRecalc(boolean needPlaneEquationRecalc) {
//        this.needPlaneEquationRecalc = needPlaneEquationRecalc;
//    }
    
    /**
     * Get array of texture coords for the quad.  There will be 4
     * @return
     */
    public E3DVector2F[] getTextureCoord() {
        return new E3DVector2F[]{vertices[0].getTextureCoord(), 
                                 vertices[1].getTextureCoord(), 
                                 vertices[2].getTextureCoord(), 
                                 vertices[3].getTextureCoord()};
    }
    /**
     * Get the first texture coordinate
     * @return
     */
    public E3DVector2F getTextureCoordA(){
        return vertices[0].getTextureCoord();
    }
	
    /**
     * Get the second texture coordinate
	 * @return
	 */    
    public E3DVector2F getTextureCoordB(){
        return vertices[1].getTextureCoord();
    }

    /**
     * Get the third texture coordinate
	 * @return
	 */    
    public E3DVector2F getTextureCoordC(){
        return vertices[2].getTextureCoord();
    }
    
    /**
     * Get the fourth texture coordinate
	 * @return
	 */    
    public E3DVector2F getTextureCoordD(){
        return vertices[3].getTextureCoord();
    }

    /**
     * Set the texture coordinates of the quad
     * @param textureCoord An array of 4 E3DVector3F's
     */
    public void setTextureCoord(E3DVector2F[] textureCoord) {
        vertices[0].setTextureCoord(textureCoord[0]);
        vertices[1].setTextureCoord(textureCoord[1]);
        vertices[2].setTextureCoord(textureCoord[2]);
        vertices[3].setTextureCoord(textureCoord[3]);
    }
    
    /**
     * Set the texture coordinates of the quad 
     * @param texCoordA
     * @param textCoordB
     * @param texCoordC
     * @param texCoordD
     */
    public void setTextureCoord(E3DVector2F texCoordA, E3DVector2F texCoordB, E3DVector2F texCoordC, E3DVector2F texCoordD)
    {
        vertices[0].setTextureCoord(texCoordA);
        vertices[1].setTextureCoord(texCoordB);
        vertices[2].setTextureCoord(texCoordC);
        vertices[3].setTextureCoord(texCoordD);
    }

    //Detail0
    /**
     * Get array of texture coords for the quad.  There will be 4
     * @return
     */
    public E3DVector2F[] getTextureCoordDetail0() {
        return new E3DVector2F[]{vertices[0].getTextureCoordDetail0(), 
                                 vertices[1].getTextureCoordDetail0(), 
                                 vertices[2].getTextureCoordDetail0(), 
                                 vertices[3].getTextureCoordDetail0()};
    }
    /**
     * Get the first texture coordinate
     * @return
     */
    public E3DVector2F getTextureCoordDetail0A(){
        return vertices[0].getTextureCoordDetail0();
    }
    
    /**
     * Get the second texture coordinate
     * @return
     */    
    public E3DVector2F getTextureCoordDetail0B(){
        return vertices[1].getTextureCoordDetail0();
    }

    /**
     * Get the third texture coordinate
     * @return
     */    
    public E3DVector2F getTextureCoordDetail0C(){
        return vertices[2].getTextureCoordDetail0();
    }
    
    /**
     * Get the fourth texture coordinate
     * @return
     */    
    public E3DVector2F getTextureCoordDetail0D(){
        return vertices[3].getTextureCoordDetail0();
    }

    /**
     * Set the texture coordinates of the quad
     * @param TextureCoordDetail0 An array of 4 E3DVector3F's
     */
    public void setTextureCoordDetail0(E3DVector2F[] TextureCoordDetail0) {
        vertices[0].setTextureCoordDetail0(TextureCoordDetail0[0]);
        vertices[1].setTextureCoordDetail0(TextureCoordDetail0[1]);
        vertices[2].setTextureCoordDetail0(TextureCoordDetail0[2]);
        vertices[3].setTextureCoordDetail0(TextureCoordDetail0[3]);
    }
    
    /**
     * Set the texture coordinates of the quad 
     * @param texCoordA
     * @param textCoordB
     * @param texCoordC
     * @param texCoordD
     */
    public void setTextureCoordDetail0(E3DVector2F texCoordA, E3DVector2F texCoordB, E3DVector2F texCoordC, E3DVector2F texCoordD)
    {
        vertices[0].setTextureCoordDetail0(texCoordA);
        vertices[1].setTextureCoordDetail0(texCoordB);
        vertices[2].setTextureCoordDetail0(texCoordC);
        vertices[3].setTextureCoordDetail0(texCoordD);
    }
 
    //Detail1
    /**
     * Get array of texture coords for the quad.  There will be 4
     * @return
     */
    public E3DVector2F[] getTextureCoordDetail1() {
        return new E3DVector2F[]{vertices[0].getTextureCoordDetail1(), 
                                 vertices[1].getTextureCoordDetail1(), 
                                 vertices[2].getTextureCoordDetail1(), 
                                 vertices[3].getTextureCoordDetail1()};
    }
    /**
     * Get the first texture coordinate
     * @return
     */
    public E3DVector2F getTextureCoordDetail1A(){
        return vertices[0].getTextureCoordDetail1();
    }
    
    /**
     * Get the second texture coordinate
     * @return
     */    
    public E3DVector2F getTextureCoordDetail1B(){
        return vertices[1].getTextureCoordDetail1();
    }

    /**
     * Get the third texture coordinate
     * @return
     */    
    public E3DVector2F getTextureCoordDetail1C(){
        return vertices[2].getTextureCoordDetail1();
    }
    
    /**
     * Get the fourth texture coordinate
     * @return
     */    
    public E3DVector2F getTextureCoordDetail1D(){
        return vertices[3].getTextureCoordDetail1();
    }

    /**
     * Set the texture coordinates of the quad
     * @param TextureCoordDetail1 An array of 4 E3DVector3F's
     */
    public void setTextureCoordDetail1(E3DVector2F[] TextureCoordDetail1) {
        vertices[0].setTextureCoordDetail1(TextureCoordDetail1[0]);
        vertices[1].setTextureCoordDetail1(TextureCoordDetail1[1]);
        vertices[2].setTextureCoordDetail1(TextureCoordDetail1[2]);
        vertices[3].setTextureCoordDetail1(TextureCoordDetail1[3]);
    }
    
    /**
     * Set the texture coordinates of the quad 
     * @param texCoordA
     * @param textCoordB
     * @param texCoordC
     * @param texCoordD
     */
    public void setTextureCoordDetail1(E3DVector2F texCoordA, E3DVector2F texCoordB, E3DVector2F texCoordC, E3DVector2F texCoordD)
    {
        vertices[0].setTextureCoordDetail1(texCoordA);
        vertices[1].setTextureCoordDetail1(texCoordB);
        vertices[2].setTextureCoordDetail1(texCoordC);
        vertices[3].setTextureCoordDetail1(texCoordD);
    }
    
    
    /**
     * Get an array of vertices that make up the quad.  There will be 4
     * @return
     */
    private E3DTexturedVertex[] getVertices() {
        return vertices;
    }

    public E3DVector3F getVertexPos(int index){
        if(index >= 0 && index < 4)
            return vertices[index].getVertexPos();
        else
            return null;
    }
    
    /**
     * Set the vertices of the quad.
     * @param vertex  An array of 4 E3DVector3F's
     */
    public void setVertices(E3DTexturedVertex[] vertices) {
        this.vertices = vertices;
		needNormalRecalc = true;
		needPlaneEquationRecalc = true;
    }
    
    /**
     * Set the vertices of the quad
     * @param vertexA
     * @param vertexB
     * @param vertexC
     * @param vertexD
     */
    public void setVertices(E3DTexturedVertex vertexA, E3DTexturedVertex vertexB, E3DTexturedVertex vertexC, E3DTexturedVertex vertexD)
    {
        vertices[0] = vertexA;
        vertices[1] = vertexB;
        vertices[2] = vertexC;
        vertices[3] = vertexD;
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
    }

    /**
     * Get the position of the first vertex of the quad
     * @return
     */
    public E3DVector3F getVertexPosA(){
        return vertices[0].getVertexPos();
    }
    
    /**
     * Set the position of the first vertex of the quad
     * @param vertexPos
     */
    public void setVertexPosA(E3DVector3F vertexPos){
        vertices[0].setVertexPos(vertexPos);
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
    }
    
    /**
     * Get the position of the second vertex of the quad
     * @return
     */
    public E3DVector3F getVertexPosB(){
        return vertices[1].getVertexPos();
    }
    
    /**
     * Set the position of the second vertex of the quad
     * @param vertexPos
     */
    public void setVertexPosB(E3DVector3F vertexPos){
        vertices[1].setVertexPos(vertexPos);
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
    }

    /**
     * Get the position of the third vertex of the quad
     * @return
     */
    public E3DVector3F getVertexPosC(){
        return vertices[2].getVertexPos();
    }
    
    /**
     * Set the position of the third vertex of the quad
     * @param vertexPos
     */
    public void setVertexPosC(E3DVector3F vertexPos){
        vertices[2].setVertexPos(vertexPos);
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
    }
    
    /**
     * Get the position of the fourth vertex of the quad
     * @return
     */
    public E3DVector3F getVertexPosD(){
        return vertices[3].getVertexPos();
    }
    
    /**
     * Set the position of the fourth vertex of the quad
     * @param vertexPos
     */
    public void setVertexPosD(E3DVector3F vertexPos){
        vertices[3].setVertexPos(vertexPos);
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
    }
    
    /**
     * Set the positions of all the vertices
     * @param vertexPosA
     * @param vertexPosB
     * @param vertexPosC
     * @param vertexPosD
     */
    public void setVertexPos(E3DVector3F vertexPosA, E3DVector3F vertexPosB, E3DVector3F vertexPosC, E3DVector3F vertexPosD)
    {
        vertices[0].setVertexPos(vertexPosA);
        vertices[1].setVertexPos(vertexPosB);
        vertices[2].setVertexPos(vertexPosC);
        vertices[3].setVertexPos(vertexPosD);
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
    }
    
    /**
     * Set the positiosn of all the vertices
     * @param vertexPositions Array of 4 position elements.  Must be length 4.
     */
    public void setVertexPos(E3DVector3F[] vertexPos)
    {
        vertices[0].setVertexPos(vertexPos[0]);
        vertices[1].setVertexPos(vertexPos[1]);
        vertices[2].setVertexPos(vertexPos[2]);
        vertices[3].setVertexPos(vertexPos[3]);
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
    }
    
    /**
     * Get the color of the vertices.
     * @return
     *  Array containing 4 E3DVector4F's
     */
    public E3DVector4F[] getVertexColor() {
        return new E3DVector4F[]{vertices[0].getVertexColor(),
                                 vertices[1].getVertexColor(),
                                 vertices[2].getVertexColor(),
                                 vertices[3].getVertexColor()};
    }
    
    /**
     * Get the color of the first vertex of the quad
     * @return
     */
    public E3DVector4F getVertexColorA(){
        return vertices[0].getVertexColor();
    }

    /**
     * Get the color of the second vertex of the quad
     * @return
     */
    public E3DVector4F getVertexColorB(){
        return vertices[1].getVertexColor();
    }

    /**
     * Get the color of the third vertex of the quad
     * @return
     */
    public E3DVector4F getVertexColorC(){
        return vertices[2].getVertexColor();
    }

    /**
     * Get the color of the fourth vertex of the quad
     * @return
     */
    public E3DVector4F getVertexColorD(){
        return vertices[3].getVertexColor();
    }
    
    /**
     * Set the color of the vertices of the quad
     * @param vertexColor An array of 4 E3DVector3F's
     */
    public void setVertexColor(E3DVector4F[] vertexColor) {
        vertices[0].setVertexColor(vertexColor[0]);
        vertices[1].setVertexColor(vertexColor[1]);
        vertices[2].setVertexColor(vertexColor[2]);
        vertices[3].setVertexColor(vertexColor[3]);
    }
    
    /**
     * Set the color of the vertices of the quad
     * @param vertexColorA
     * @param vertexColorB
     * @param vertexColorC
     * @param vertexColorD
     */
    public void setVertexColor(E3DVector4F vertexColorA, E3DVector4F vertexColorB, 
            				E3DVector4F vertexColorC, E3DVector4F vertexColorD)
    {
        vertices[0].setVertexColor(vertexColorA);
        vertices[1].setVertexColor(vertexColorB);
        vertices[2].setVertexColor(vertexColorC);
        vertices[3].setVertexColor(vertexColorD);
    }
    
	/**
	 * Resets all the vertex colors to all be color.  Value between 0 and 1 should be used
	 * @param color Value between 0 and 1 to set all R,G,B values to for all vertices
	 */
	public void resetVertexColor(double color){
	    E3DVector4F vertexColor;
        for(int i=0; i<4; i++)
		{
            vertexColor = vertices[i].getVertexColor();
            
		    vertexColor.setA(color);
		    vertexColor.setB(color);
		    vertexColor.setC(color);
            vertexColor.setD(color);
        }
	}
	
	/**
	 * Ensures all vertex colors are <= 1.0
	 *
	 */
	public void normaliseVertexColors(){
		for(int i=0; i<4; i++)
            vertices[i].normaliseVertexColor();
	}	
	
    
    public void appendVertexBuffer(FloatBuffer vertexBuffer)
    {
        E3DVector3F vertexPosA = vertices[0].getVertexPos(),
                    vertexPosB = vertices[1].getVertexPos(),
                    vertexPosC = vertices[2].getVertexPos(),
                    vertexPosD = vertices[3].getVertexPos();
        vertexBuffer.put((float)vertexPosA.getX());
        vertexBuffer.put((float)vertexPosA.getY());
        vertexBuffer.put((float)vertexPosA.getZ());
        vertexBuffer.put((float)vertexPosB.getX());
        vertexBuffer.put((float)vertexPosB.getY());
        vertexBuffer.put((float)vertexPosB.getZ());
        vertexBuffer.put((float)vertexPosC.getX());
        vertexBuffer.put((float)vertexPosC.getY());
        vertexBuffer.put((float)vertexPosC.getZ());
        vertexBuffer.put((float)vertexPosD.getX());
        vertexBuffer.put((float)vertexPosD.getY());
        vertexBuffer.put((float)vertexPosD.getZ());
    }
    
    public void appendVertexColorBuffer(FloatBuffer vertexColorBuffer)
    {
        E3DVector4F vertexColorA = vertices[0].getVertexColor(),
                    vertexColorB = vertices[1].getVertexColor(),
                    vertexColorC = vertices[2].getVertexColor(),
                    vertexColorD = vertices[3].getVertexColor();
        vertexColorBuffer.put((float)vertexColorA.getA());
        vertexColorBuffer.put((float)vertexColorA.getB());
        vertexColorBuffer.put((float)vertexColorA.getC());
        vertexColorBuffer.put((float)vertexColorA.getD());
        vertexColorBuffer.put((float)vertexColorB.getA());
        vertexColorBuffer.put((float)vertexColorB.getB());
        vertexColorBuffer.put((float)vertexColorB.getC());
        vertexColorBuffer.put((float)vertexColorB.getD());
        vertexColorBuffer.put((float)vertexColorC.getA());
        vertexColorBuffer.put((float)vertexColorC.getB());
        vertexColorBuffer.put((float)vertexColorC.getC());
        vertexColorBuffer.put((float)vertexColorC.getD());
        vertexColorBuffer.put((float)vertexColorD.getA());
        vertexColorBuffer.put((float)vertexColorD.getB());
        vertexColorBuffer.put((float)vertexColorD.getC());
        vertexColorBuffer.put((float)vertexColorD.getD());
    }    

    public void appendTexCoordBuffer(FloatBuffer texCoordBuffer)
    {
        E3DVector2F textureCoordA = vertices[0].getTextureCoord(),
                    textureCoordB = vertices[1].getTextureCoord(),
                    textureCoordC = vertices[2].getTextureCoord(),
                    textureCoordD = vertices[3].getTextureCoord();
        texCoordBuffer.put((float)textureCoordA.getX());
        texCoordBuffer.put((float)textureCoordA.getY());
        texCoordBuffer.put((float)textureCoordB.getX());
        texCoordBuffer.put((float)textureCoordB.getY());
        texCoordBuffer.put((float)textureCoordC.getX());
        texCoordBuffer.put((float)textureCoordC.getY());
        texCoordBuffer.put((float)textureCoordD.getX());
        texCoordBuffer.put((float)textureCoordD.getY());
    }
    
    public void appendTexCoordDetail0Buffer(FloatBuffer texCoordDetail0Buffer)
    {
        if(!isTextureDetail0Available())
            return;

        E3DVector2F textureCoordA = vertices[0].getTextureCoordDetail0(),
                    textureCoordB = vertices[1].getTextureCoordDetail0(),
                    textureCoordC = vertices[2].getTextureCoordDetail0(),
                    textureCoordD = vertices[3].getTextureCoordDetail0();
        texCoordDetail0Buffer.put((float)textureCoordA.getX());
        texCoordDetail0Buffer.put((float)textureCoordA.getY());
        texCoordDetail0Buffer.put((float)textureCoordB.getX());
        texCoordDetail0Buffer.put((float)textureCoordB.getY());
        texCoordDetail0Buffer.put((float)textureCoordC.getX());
        texCoordDetail0Buffer.put((float)textureCoordC.getY());
        texCoordDetail0Buffer.put((float)textureCoordD.getX());
        texCoordDetail0Buffer.put((float)textureCoordD.getY());
    }  
    
    public void appendTexCoordDetail1Buffer(FloatBuffer texCoordDetail1Buffer)
    {
        if(!isTextureDetail1Available())
            return;

        E3DVector2F textureCoordA = vertices[0].getTextureCoordDetail1(),
                    textureCoordB = vertices[1].getTextureCoordDetail1(),
                    textureCoordC = vertices[2].getTextureCoordDetail1(),
                    textureCoordD = vertices[3].getTextureCoordDetail1();
        texCoordDetail1Buffer.put((float)textureCoordA.getX());
        texCoordDetail1Buffer.put((float)textureCoordA.getY());
        texCoordDetail1Buffer.put((float)textureCoordB.getX());
        texCoordDetail1Buffer.put((float)textureCoordB.getY());
        texCoordDetail1Buffer.put((float)textureCoordC.getX());
        texCoordDetail1Buffer.put((float)textureCoordC.getY());
        texCoordDetail1Buffer.put((float)textureCoordD.getX());
        texCoordDetail1Buffer.put((float)textureCoordD.getY());
    }  
    
    /*
    private float[] floatTexCoordDetail0Array = new float[8];
    public float[] getFloatTexCoordDetail0Array()
    {
        if(!isTextureDetail0Available())
            return floatTexCoordDetail0Array;
        
        E3DVector2F textureCoordA = vertices[0].getTextureCoordDetail0(),
                   textureCoordB = vertices[1].getTextureCoordDetail0(),
                   textureCoordC = vertices[2].getTextureCoordDetail0(),
                   textureCoordD = vertices[3].getTextureCoordDetail0();
                
        floatTexCoordDetail0Array[0] = (float)textureCoordA.getX();
        floatTexCoordDetail0Array[1] = (float)textureCoordA.getY();
        floatTexCoordDetail0Array[2] = (float)textureCoordB.getX();
        floatTexCoordDetail0Array[3] = (float)textureCoordB.getY();
        floatTexCoordDetail0Array[4] = (float)textureCoordC.getX();
        floatTexCoordDetail0Array[5] = (float)textureCoordC.getY();
        floatTexCoordDetail0Array[6] = (float)textureCoordD.getX();
        floatTexCoordDetail0Array[7] = (float)textureCoordD.getY();
        return floatTexCoordDetail0Array;
    }    

    private float[] floatTexCoordDetail1Array = new float[8];
    public float[] getFloatTexCoordDetail1Array()
    {
        if(!isTextureDetail1Available())
            return floatTexCoordDetail1Array;
        
        E3DVector2F textureCoordA = vertices[0].getTextureCoordDetail1(),
                   textureCoordB = vertices[1].getTextureCoordDetail1(),
                   textureCoordC = vertices[2].getTextureCoordDetail1(),
                   textureCoordD = vertices[3].getTextureCoordDetail1();
                
        floatTexCoordDetail1Array[0] = (float)textureCoordA.getX();
        floatTexCoordDetail1Array[1] = (float)textureCoordA.getY();
        floatTexCoordDetail1Array[2] = (float)textureCoordB.getX();
        floatTexCoordDetail1Array[3] = (float)textureCoordB.getY();
        floatTexCoordDetail1Array[4] = (float)textureCoordC.getX();
        floatTexCoordDetail1Array[5] = (float)textureCoordC.getY();
        floatTexCoordDetail1Array[6] = (float)textureCoordD.getX();
        floatTexCoordDetail1Array[7] = (float)textureCoordD.getY();
        return floatTexCoordDetail1Array;
    }    
    */
    
    
	private void recalculateNormal()
	{
        E3DVector3F vertexPosA = vertices[0].getVertexPos(),
        vertexPosB = vertices[1].getVertexPos(),
        vertexPosC = vertices[2].getVertexPos();
        
		if(vertexPosA == null || vertexPosB == null || vertexPosC == null)
			return;
		
		E3DVector3F vec1 = vertexPosB.subtract(vertexPosA);
		E3DVector3F vec2 = vertexPosC.subtract(vertexPosA);
	
		normal = vec1.crossProduct(vec2);
		normal.normaliseEqual();
	}	
	
	/**
	 * @return Returns the normal of the quad
	 */
	public E3DVector3F getNormal() {
		if(needNormalRecalc) //only recalculate this right before its needed
		{
			recalculateNormal();
			needNormalRecalc = false;
		}

		return normal;
	}

	private void recalculatePlaneEquationCoords()
	{
		E3DVector3F normal = getNormal();

		setPlaneEquationCoords(new E3DVector4F(normal.getX(), 
							  normal.getY(), 
							  normal.getZ(), 
							  -normal.dotProduct(getVertexPosA())));
	}
	
	/**
	 * Returns a vector with the 4 parameters of the quad's plane
	 * @return
	 */
	public E3DVector4F getPlaneEquationCoords()
	{
		if(needPlaneEquationRecalc)
		{
			recalculatePlaneEquationCoords();
			needPlaneEquationRecalc = false;
		}
		
		return planeEquationCoords;
	
	}

	private void setPlaneEquationCoords(E3DVector4F planeEq) {
		this.planeEquationCoords = planeEq;
	}
	
	/**
	 * Translate the quad vertices
	 *
	 */
	public void translate(E3DVector3F translationAmt)
	{
		getVertexPosA().addEqual(translationAmt);
		getVertexPosB().addEqual(translationAmt);
		getVertexPosC().addEqual(translationAmt);
		getVertexPosD().addEqual(translationAmt);
        needPlaneEquationRecalc = true;
	}
	
	/**
	 * Scale the quad
	 * @param scaleAmt
	 */
	public void scale(double scaleAmt)
	{
        getVertexPosA().scaleEqual(scaleAmt);
        getVertexPosB().scaleEqual(scaleAmt);
        getVertexPosC().scaleEqual(scaleAmt);
        getVertexPosD().scaleEqual(scaleAmt);
        needPlaneEquationRecalc = true;
	}

	/**
	 * Get a string with the quads vertice values listed
	 */
	public String toString()
	{
		return "(" + getVertexPosA() + ", " + getVertexPosB() + ", " + getVertexPosC()+ ", " + getVertexPosD() + ")";
	}

	/**
	 * Rotate the quad around aroundVec.  Translated amt is how far the quad has been translated from the origin (it needsd this so it can be centered around the up vec correctly and then rotate and moved back)
	 * @param angle
	 * @param upVec
	 * @param translatedAmt
	 */
	public void rotate(double angle, E3DVector3F upVec, E3DVector3F translatedAmt)
	{
		E3DVector3F vec = null;

		//Normal first in case it was null
		vec = getNormal();
		vec.rotateEqual(angle, upVec);
		vec.normaliseEqual();

		//Go through each vertex, get the vector from it to the position, rotate it, add position back to it
		for(int a = 0; a<4; a++)
		{
			vec = vertices[a].getVertexPos();
			//These operations should modify the vec itself, so it doesn't need to be re-set
			vec.subtractEqual(translatedAmt);
			vec.rotateEqual(angle, upVec);
			vec.addEqual(translatedAmt);
		}
		
        needNormalRecalc = true;
        needPlaneEquationRecalc = true;
	}
    /**
     * This checks if a point is in a triangle by seeing if the interior angles add up to 360Deg.
     * This is slightly slower than isPointInTriangle, but left for testing sake.
     * @param point
     * @return
     */
    public boolean isPointInQuad(E3DVector3F point)
    {
        if(point == null)
            return false;
        
        E3DVector3F vertexPosA = vertices[0].getVertexPos(),
                    vertexPosB = vertices[1].getVertexPos(),
                    vertexPosC = vertices[2].getVertexPos(),
                    vertexPosD = vertices[3].getVertexPos();

        E3DVector3F a, b, c, d;
        a = vertexPosA.subtract(point);
        b = vertexPosB.subtract(point);
        c = vertexPosC.subtract(point);
        d = vertexPosD.subtract(point);

        //Point is a vertex point, so that should count
        if(a.equals(0.0, 0.0, 0.0) || b.equals(0.0, 0.0, 0.0) || c.equals(0.0, 0.0, 0.0) || d.equals(0.0, 0.0, 0.0))
            return true;
        
        a.normaliseEqual();
        b.normaliseEqual();
        c.normaliseEqual();
        d.normaliseEqual();
        
        double totalAngle = a.angleBetweenRads(b) + b.angleBetweenRads(c) + c.angleBetweenRads(d) + d.angleBetweenRads(a);

        if( totalAngle < E3DConstants.TWOPI + E3DConstants.DBL_PRECISION_ERROR && 
            totalAngle > E3DConstants.TWOPI - E3DConstants.DBL_PRECISION_ERROR)
            return true;
        else
            return false;
    }
    
    public double getDistanceToPoint(E3DVector3F point)
    {
        double planeD = getPlaneEquationCoords().getD();
        
        return getNormal().dotProduct(point) + planeD; //DotProduct( plane->normal, destPt ) + plane->distance;
    }
    
    public boolean doesSegmentCrossPlane(E3DVector3F startPos, E3DVector3F endPos)
    {
        double distanceBefore = getDistanceToPoint(startPos);
        double distanceAfter = getDistanceToPoint(endPos);
        
        if((distanceBefore < 0 && distanceAfter > 0) || (distanceBefore > 0 && distanceAfter < 0))
            return true;
        else
            return false;
    }
    
    /**
     * Gets the intersection point of the line segment formed between startPos and endPos and the triangle if it intersects
     * @param startPos
     * @param endPos
     * @param triangle
     * @return
     *  returns intersection point
     */
    /**
     * Gets the intersection point of the line segment formed between startPos and endPos and the triangle if it intersects
     * @param startPos
     * @param endPos
     * @param triangle
     * @return
     *  returns intersection point
     */
    public E3DVector3F getPlaneIntersectionPoint(E3DVector3F startPos, E3DVector3F endPos)
    {
        double a, b;
        
        E3DVector3F normal = getNormal();
        E3DVector3F direction = endPos.subtract(startPos);

        a = -normal.dotProduct(startPos.subtract(vertices[0].getVertexPos()));
        b = normal.dotProduct(direction);

        double r = a / b;
        if (r < 0.0)                   // ray goes away from triangle
            return null;
        
        direction.scaleEqual(r);
        direction = startPos.add(direction);
        
        return direction;
        
/*        
       //old
        E3DVector3F normal = getNormal();
        
        E3DVector3F ray = endPos.subtract(startPos);
        
        double t =  -(normal.dotProduct(startPos) -
                      getPlaneEquationCoords().getD() +
                      normal.dotProduct(ray));
        //NO
        ray.normaliseEqual(); //NO

        ray.scaleEqual(t);
        ray.addEqual(startPos); //should be marginally faster (one less new E3DVector3F)
        return ray; //startPos.add(ray);*/
    }
 
    public double getAlpha() {
        return alpha;
    }
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    	this.vertices[0].getVertexColor().setD(alpha);
    	this.vertices[1].getVertexColor().setD(alpha);
    	this.vertices[2].getVertexColor().setD(alpha);
    	this.vertices[3].getVertexColor().setD(alpha);
    }
    
    /**
     * Checks for full fledged collision between the line segment formed between startPos and endPos and the
     *  triangle and returns the intersection point (or null if no collision occurs)
     * @return
     *  Returns the intersection point vector if there is 
     *  a collision between the line segment (startPos, endPos) and triangle.
     *  Otherwise, returns null;
     */
    public E3DVector3F checkSegmentCollision(E3DVector3F startPos, E3DVector3F endPos)
    {
        if(doesSegmentCrossPlane(startPos, endPos))
        {
            E3DVector3F intersectionPoint = getPlaneIntersectionPoint(startPos, endPos);
            if(isPointInQuad(intersectionPoint))// || triangle.pointInTriangle(intersectionPoint2))
                return intersectionPoint;
        }
        return null;
    }
    
    public boolean equals(Object arg0) 
    {
    	if(arg0 == null || !(arg0 instanceof E3DQuad))
    		return false;
    	return arg0 == this;
//    	E3DTriangle compareQuad = (E3DTriangle)arg0;
//    	
//    	return vertices[0].equals(compareQuad.vertices[0]) &&
//    		   vertices[1].equals(compareQuad.vertices[1]) &&
//    		   vertices[2].equals(compareQuad.vertices[2]);
    }
    
    public int hashCode() {
    	return super.hashCode();
//    	return vertices[0].hashCode() + vertices[1].hashCode() + vertices[2].hashCode();
    }
    
}
