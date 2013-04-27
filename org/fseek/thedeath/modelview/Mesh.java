// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Mesh.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.vecmath.Point3f;

class Mesh
{

    public Mesh()
    {
        geoset = 0;
        mIndexStart = mIndexCount = '\0';
        mMaterial = 0;
        transparent = swrap = twrap = noZWrite = envmap = unlit = billboard = cull = show = false;
        blendmode = 1;
        color = null;
        opacity = 1.0F;
        colorIndex = opacityIndex = texAnim = -1;
    }

    public void read(ByteBuffer buf)
        throws IOException
    {
        byte inByte = 0;
        inByte = buf.get();
        show = inByte != 0;
        geoset = buf.getInt();
        geosetId = buf.getShort();
        mMaterial = buf.getShort();
        mIndexStart = buf.getChar();
        mIndexCount = buf.getChar();
        inByte = buf.get();
        transparent = inByte != 0;
        blendmode = buf.getShort();
        inByte = buf.get();
        swrap = inByte != 0;
        inByte = buf.get();
        twrap = inByte != 0;
        inByte = buf.get();
        noZWrite = inByte != 0;
        inByte = buf.get();
        envmap = inByte != 0;
        inByte = buf.get();
        unlit = inByte != 0;
        inByte = buf.get();
        billboard = inByte != 0;
        color = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
        opacity = buf.getFloat();
    }

    public void readMo2(ByteBuffer buf)
        throws IOException
    {
        show = true;
        geoset = buf.getInt();
        geosetId = buf.getShort();
        mMaterial = buf.getShort();
        texAnim = buf.getShort();
        mIndexStart = buf.getChar();
        mIndexCount = buf.getChar();
        blendmode = buf.getShort();
        texUnit = buf.getShort();
        renderFlags = buf.getShort() & 0xffff;
        textureFlags = buf.getShort() & 0xffff;
        transparent = false;
        swrap = (textureFlags & 1) > 0;
        twrap = (textureFlags & 2) > 0;
        unlit = (renderFlags & 1) > 0;
        cull = (renderFlags & 4) == 0;
        billboard = (renderFlags & 8) > 0;
        noZWrite = (renderFlags & 0x10) > 0;
        envmap = texUnit == -1 && billboard && blendmode > 2;
        colorIndex = buf.getShort();
        opacityIndex = buf.getShort();
    }

    public void readExt(ByteBuffer buf)
        throws IOException
    {
        texAnim = buf.getShort();
    }

    protected boolean show;
    protected int geoset;
    protected short geosetId;
    protected char mIndexStart;
    protected char mIndexCount;
    protected short mMaterial;
    protected boolean transparent;
    protected boolean swrap;
    protected boolean twrap;
    protected boolean noZWrite;
    protected boolean envmap;
    protected boolean unlit;
    protected boolean billboard;
    protected boolean cull;
    protected short blendmode;
    protected short texUnit;
    protected int renderFlags;
    protected int textureFlags;
    protected Point3f color;
    protected float opacity;
    protected short colorIndex;
    protected short opacityIndex;
    protected short texAnim;
}