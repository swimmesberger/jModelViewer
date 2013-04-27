package org.fseek.thedeath.modelview.interfaces;

import org.fseek.thedeath.modelview.Camera;
import org.fseek.thedeath.modelview.Model;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public interface IGraphicContext
{   
    public Model getModel();
    public Camera getCamera();
    public int getCanvasWidth();
    public int getCanvasHeight();
    public int getHairType();
    public int getHairColor();
    public int getFaceType();
    public int getSkinColor();
    public int getFacialHairType();
    public int getFacialHairColor();
    public int getGlobalTime();
    public int getMesh();
    public long getDelta();
}
