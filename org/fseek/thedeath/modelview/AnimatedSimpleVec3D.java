// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   AnimatedSimpleVec3D.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.vecmath.Point3f;

class AnimatedSimpleVec3D
{

    AnimatedSimpleVec3D()
    {
        times = null;
        data = null;
    }

    Point3f getValue(int time)
    {
        if(data.length > 1 && times.length > 1)
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
            Point3f v = new Point3f();
            v.interpolate(data[idx], data[idx + 1], r);
            return v;
        }
        if(data.length > 0)
            return data[0];
        else
            return new Point3f();
    }

    void read(ByteBuffer buf)
        throws IOException
    {
        int count = buf.getInt();
        times = new int[count];
        for(int i = 0; i < count; i++)
            times[i] = buf.getInt();

        count = buf.getInt();
        data = new Point3f[count];
        for(int i = 0; i < count; i++)
            data[i] = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());

    }

    int times[];
    Point3f data[];
}