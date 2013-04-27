// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   AnimatedFloat.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

class AnimatedFloat
{

    static float getValue(AnimatedFloat anims[], int anim, int time)
    {
        if(anims.length == 0)
            return 0.0F;
        int thisAnim = anim;
        if(thisAnim >= anims.length)
            thisAnim = 0;
        return anims[thisAnim].getValue(time);
    }

    AnimatedFloat()
    {
        type = 0;
        times = null;
        data = null;
        dataIn = dataOut = null;
    }

    float getValue(int time)
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
                    return data[idx] + (data[idx + 1] - data[idx]) * r;
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
                return 0.0F;
        }
        if(data.length == 0)
            return 0.0F;
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
            dataIn = new float[count];
            dataOut = new float[count];
            for(int i = 0; i < count; i++)
            {
                dataIn[i] = buf.getFloat();
                dataOut[i] = buf.getFloat();
            }

        } else
        {
            data = new float[count];
            for(int i = 0; i < count; i++)
                data[i] = buf.getFloat();

        }
    }

    int type;
    int times[];
    float data[];
    float dataIn[];
    float dataOut[];
    boolean used;
}