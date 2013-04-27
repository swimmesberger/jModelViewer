// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Camera.java

package org.fseek.thedeath.modelview;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

public class Camera
{

    public Camera()
    {
        latitude = 0.0F;
        longitude = 1.570796F;
        distance = 5F;
        translation = new Vector3f();
    }

    public void look(GL2 gl)
    {
        gl.glLoadIdentity();
        Vector3f dir = getPosition();
        GLU glu = new GLU();
        gl.glTranslated(translation.x, translation.y, translation.z);
        glu.gluLookAt(dir.x, dir.y, dir.z, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public Vector3f getPosition()
    {
        float cosTheta = (float)Math.cos(latitude);
        float sinTheta = (float)Math.sin(latitude);
        float cosPhi = (float)Math.cos(longitude);
        float sinPhi = (float)Math.sin(longitude);
        Vector3f dir = new Vector3f(distance * sinPhi * cosTheta, distance * sinPhi * sinTheta, distance * cosPhi);
        return dir;
    }

    public void translate(float x, float y, float z)
    {
        double scaleFactor = ((double)distance / 5D) * 0.02D;
        translation.x += (double)x * scaleFactor;
        translation.y += (double)y * scaleFactor;
        translation.z += (double)z * scaleFactor;
    }

    public void rotate(float lat, float lon)
    {
        latitude += lat;
        longitude += lon;
        for(; latitude < 0.0F; latitude += 6.2831853071795862D);
        for(; (double)latitude > 6.2831853071795862D; latitude -= 6.2831853071795862D);
        if((double)longitude > 3.1415926535897931D)
            longitude = 3.141593F;
        else
        if(longitude <= 0.0F)
            longitude = 0.01F;
    }

    public void zoom(float change)
    {
        distance += (double)change * ((double)distance / 15D);
        if(distance <= 0.0F)
            distance = 0.05F;
        else
        if(distance > 100F)
            distance = 100F;
    }

    public void setDistance(float dist)
    {
        distance = dist;
    }

    private float latitude;
    private float longitude;
    private float distance;
    Vector3f translation;
}