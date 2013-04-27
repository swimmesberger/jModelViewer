// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ParticleEmitter.java

package org.fseek.thedeath.modelview;

import javax.vecmath.Matrix4f;

// Referenced classes of package modelview:
//            Vec3, Vec4

class Particle
{

    Particle()
    {
    }

    public static Matrix4f CalcSpread(float spread1, float spread2, float w, float l)
    {
        float a[] = new float[2];
        float c[] = new float[2];
        float s[] = new float[2];
        Matrix4f temp = new Matrix4f();
        Matrix4f spread = new Matrix4f();
        spread.setIdentity();
        a[0] = ((float)Math.random() * (spread1 * 2.0F) - spread1) / 2.0F;
        a[1] = ((float)Math.random() * (spread2 * 2.0F) - spread2) / 2.0F;
        for(int i = 0; i < 2; i++)
        {
            c[i] = (float)Math.cos(a[i]);
            s[i] = (float)Math.sin(a[i]);
        }

        temp.setIdentity();
        temp.m11 = c[0];
        temp.m21 = s[0];
        temp.m22 = c[0];
        temp.m12 = -s[0];
        spread.mul(temp);
        temp.setIdentity();
        temp.m00 = c[1];
        temp.m10 = s[1];
        temp.m11 = c[1];
        temp.m01 = -s[1];
        spread.mul(temp);
        float size = Math.abs(c[0]) * l + Math.abs(s[0]) * w;
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
                spread.setElement(i, j, spread.getElement(i, j) * size);

        }

        return spread;
    }

    Vec3 pos;
    Vec3 origin;
    Vec3 corners[];
    Vec3 speed;
    Vec3 dir;
    Vec3 down;
    Vec4 color;
    float size;
    float life;
    float maxLife;
    int tile;
}