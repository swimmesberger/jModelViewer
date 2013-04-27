// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   AnimatedVec3D.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import javax.vecmath.Point3f;

class AnimatedVec3D
{

    static Point3f getValue(AnimatedVec3D anims[], int anim, int time)
    {
        if(anims.length == 0)
            return new Point3f();
        int thisAnim = anim;
        if(thisAnim >= anims.length)
            thisAnim = 0;
        return anims[thisAnim].getValue(time);
    }

    AnimatedVec3D()
    {
        type = 0;
        times = null;
        data = null;
        dataIn = dataOut = null;
    }

    Point3f getValue(int time)
    {
        if(type != 0 || data.length > 1)
        {
            if(times.length > 1)
            {
                int maxTime = times[times.length - 1];
                if(maxTime > 0 && time > maxTime)
                    time %= maxTime;
                int idx = 0;
                for(int i = 0; i < times.length - 1; i++)
                {
                    if(time < times[i] || time >= times[i + 1])
                        continue;
                    idx = i;
                    break;
                }

                int t1 = times[idx];
                int t2 = times[idx + 1];
                float r = 0.0F;
                if(t2 - t1 != 0)
                    r = (float)(time - t1) / (float)(t2 - t1);
                if(type == 1)
                {
                    Point3f v = new Point3f();
                    v.interpolate(data[idx], data[idx + 1], r);
                    return v;
                }
                if(type == 0)
                    return data[idx];
                else
                    return dataOut[idx];
            }
            if(times.length > 0)
                return data[0];
            if(data.length > 0)
                return data[0];
            else
                return new Point3f();
        }
        if(data.length == 0)
            return new Point3f();
        else
            return data[0];
    }

    void read(ByteBuffer buf)
        throws IOException
    {
        byte inByte = 0;
        inByte = buf.get();
        used = inByte != 0;
        int count = buf.getInt();
        times = new int[count];
        for(int i = 0; i < count; i++)
            times[i] = buf.getInt();

        type = buf.getInt();
        if(type == 2)
            System.out.println("rot hermite");
        count = buf.getInt();
        if(type == 2)
        {
            dataIn = new Point3f[count];
            dataOut = new Point3f[count];
            for(int i = 0; i < count; i++)
            {
                dataIn[i] = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
                dataOut[i] = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
            }

        } else
        {
            data = new Point3f[count];
            for(int i = 0; i < count; i++)
                data[i] = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());

        }
    }

    int type;
    int times[];
    Point3f data[];
    Point3f dataIn[];
    Point3f dataOut[];
    boolean used;
}