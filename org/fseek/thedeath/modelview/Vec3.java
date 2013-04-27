// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Vec3.java


package org.fseek.thedeath.modelview;

import javax.vecmath.*;

class Vec3
{

    public Vec3()
    {
        this(0.0F, 0.0F, 0.0F);
    }

    public Vec3(float x, float y, float z)
    {
        set(x, y, z);
    }

    public Vec3(Vec3 v)
    {
        set(v);
    }

    public Vec3(Tuple3f p)
    {
        set(p);
    }

    public Vec3 set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vec3 set(Vec3 v)
    {
        return set(v.x, v.y, v.z);
    }

    public Vec3 set(Tuple3f p)
    {
        return set(p.x, p.y, p.z);
    }

    public Point3f toPoint()
    {
        return new Point3f(x, y, z);
    }

    public Vector3f toVector()
    {
        return new Vector3f(x, y, z);
    }

    public Tuple3f toTuple(Tuple3f p)
    {
        p.set(x, y, z);
        return p;
    }

    public Vec3 add(Vec3 v)
    {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    public Vec3 sub(Vec3 v)
    {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    public Vec3 scale(float s)
    {
        x *= s;
        y *= s;
        z *= s;
        return this;
    }

    public float dot(Vec3 v)
    {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3 cross(Vec3 v)
    {
        return new Vec3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public float lengthSquared()
    {
        return x * x + y * y + z * z;
    }

    public float length()
    {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3 normalize()
    {
        return scale(1.0F / length());
    }

    public Vec3 transform(Matrix4f m)
    {
        Point3f c = toPoint();
        m.transform(c);
        set(c);
        return this;
    }

    static Vec3 add(Vec3 v1, Vec3 v2)
    {
        return new Vec3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    static Vec3 sub(Vec3 v1, Vec3 v2)
    {
        return new Vec3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    static Vec3 scale(Vec3 v, float s)
    {
        return new Vec3(v.x * s, v.y * s, v.z * s);
    }

    static float dot(Vec3 v1, Vec3 v2)
    {
        return v1.dot(v2);
    }

    static Vec3 cross(Vec3 v1, Vec3 v2)
    {
        return v1.cross(v2);
    }

    static Vec3 add(Vec3 v1, Vec3 v2, Vec3 d)
    {
        d.set(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
        return d;
    }

    static Vec3 sub(Vec3 v1, Vec3 v2, Vec3 d)
    {
        d.set(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        return d;
    }

    static Vec3 scale(Vec3 v, float s, Vec3 d)
    {
        d.set(v.x * s, v.y * s, v.z * s);
        return d;
    }

    static Vec3 cross(Vec3 v1, Vec3 v2, Vec3 d)
    {
        d.set(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
        return d;
    }

    public float x;
    public float y;
    public float z;
}