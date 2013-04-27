// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   AnimatedSimpleUShort.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

class AnimatedSimpleUShort
{

    AnimatedSimpleUShort()
    {
        times = null;
        data = null;
    }

    int getValue(int time)
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
            return (int)((float)data[idx] + (float)(data[idx + 1] - data[idx]) * r);
        }
        if(data.length > 0)
            return data[0];
        else
            return 0;
    }

    void read(ByteBuffer buf)
        throws IOException
    {
        int count = buf.getInt();
        times = new int[count];
        for(int i = 0; i < count; i++)
            times[i] = buf.getInt();

        count = buf.getInt();
        data = new int[count];
        for(int i = 0; i < count; i++)
            data[i] = buf.getShort() & 0xffff;

    }

    int times[];
    int data[];
}