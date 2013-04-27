// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Vec3.java

package org.fseek.thedeath.modelview;

import javax.vecmath.*;

class Vec2
{

    public Vec2()
    {
        this(0.0F, 0.0F);
    }

    public Vec2(float x, float y)
    {
        set(x, y);
    }

    public Vec2(Vec2 v)
    {
        set(v);
    }

    public Vec2(Tuple2f p)
    {
        set(p);
    }

    public Vec2 set(float x, float y)
    {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vec2 set(Vec2 v)
    {
        return set(v.x, v.y);
    }

    public Vec2 set(Tuple2f p)
    {
        return set(p.x, p.y);
    }

    public Point2f toPoint()
    {
        return new Point2f(x, y);
    }

    public Vector2f toVector()
    {
        return new Vector2f(x, y);
    }

    public Tuple2f toTuple(Tuple2f p)
    {
        p.set(x, y);
        return p;
    }

    public Vec2 add(Vec2 v)
    {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vec2 sub(Vec2 v)
    {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public Vec2 scale(float s)
    {
        x *= s;
        y *= s;
        return this;
    }

    public float dot(Vec2 v)
    {
        return x * v.x + y * v.y;
    }

    public float lengthSquared()
    {
        return x * x + y * y;
    }

    public float length()
    {
        return (float)Math.sqrt(x * x + y * y);
    }

    public Vec2 normalize()
    {
        return scale(1.0F / length());
    }

    static Vec2 add(Vec2 v1, Vec2 v2)
    {
        return new Vec2(v1.x + v2.x, v1.y + v2.y);
    }

    static Vec2 sub(Vec2 v1, Vec2 v2)
    {
        return new Vec2(v1.x - v2.x, v1.y - v2.y);
    }

    static Vec2 scale(Vec2 v, float s)
    {
        return new Vec2(v.x * s, v.y * s);
    }

    static float dot(Vec2 v1, Vec2 v2)
    {
        return v1.dot(v2);
    }

    static Vec2 add(Vec2 v1, Vec2 v2, Vec2 d)
    {
        d.set(v1.x + v2.x, v1.y + v2.y);
        return d;
    }

    static Vec2 sub(Vec2 v1, Vec2 v2, Vec2 d)
    {
        d.set(v1.x - v2.x, v1.y - v2.y);
        return d;
    }

    static Vec2 scale(Vec2 v, float s, Vec2 d)
    {
        d.set(v.x * s, v.y * s);
        return d;
    }

    public float x;
    public float y;
}