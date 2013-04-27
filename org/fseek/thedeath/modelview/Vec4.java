// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Vec3.java

package org.fseek.thedeath.modelview;

import javax.vecmath.*;

class Vec4
{

    public Vec4()
    {
        this(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public Vec4(float x, float y, float z, float w)
    {
        set(x, y, z, w);
    }

    public Vec4(Vec4 v)
    {
        set(v);
    }

    public Vec4(Tuple4f p)
    {
        set(p);
    }

    public Vec4 set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vec4 set(Vec4 v)
    {
        return set(v.x, v.y, v.z, v.w);
    }

    public Vec4 set(Tuple4f p)
    {
        return set(p.x, p.y, p.z, p.w);
    }

    public Point4f toPoint()
    {
        return new Point4f(x, y, z, w);
    }

    public Vector4f toVector()
    {
        return new Vector4f(x, y, z, w);
    }

    public Tuple4f toTuple(Tuple4f p)
    {
        p.set(x, y, z, w);
        return p;
    }

    public Vec4 add(Vec4 v)
    {
        x += v.x;
        y += v.y;
        z += v.z;
        w += v.w;
        return this;
    }

    public Vec4 sub(Vec4 v)
    {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        w -= v.w;
        return this;
    }

    public Vec4 scale(float s)
    {
        x *= s;
        y *= s;
        z *= s;
        w *= s;
        return this;
    }

    public float x;
    public float y;
    public float z;
    public float w;
}