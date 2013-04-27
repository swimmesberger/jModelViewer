// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   AnimatedQuat.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.vecmath.Quat4f;

class AnimatedQuat
{

    static Quat4f getValue(AnimatedQuat anims[], int anim, int time)
    {
        if(anims.length == 0)
            return new Quat4f();
        int thisAnim = anim;
        if(thisAnim >= anims.length)
            thisAnim = 0;
        return anims[thisAnim].getValue(time);
    }

    AnimatedQuat()
    {
        type = 0;
        times = null;
        data = null;
        dataIn = dataOut = null;
    }

    Quat4f getValue(int time)
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
                    Quat4f v = new Quat4f();
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
                return new Quat4f();
        }
        if(data.length == 0)
            return new Quat4f();
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
            dataIn = new Quat4f[count];
            dataOut = new Quat4f[count];
            for(int i = 0; i < count; i++)
            {
                dataIn[i] = new Quat4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
                dataOut[i] = new Quat4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
            }

        } else
        {
            data = new Quat4f[count];
            for(int i = 0; i < count; i++)
                data[i] = new Quat4f(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());

        }
    }

    int type;
    int times[];
    Quat4f data[];
    Quat4f dataIn[];
    Quat4f dataOut[];
    boolean used;
}