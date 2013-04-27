// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   AnimatedUShort.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

class AnimatedUShort
{

    static int getValue(AnimatedUShort anims[], int anim, int time)
    {
        if(anims.length == 0)
            return 0;
        int thisAnim = anim;
        if(thisAnim >= anims.length)
            thisAnim = 0;
        return anims[thisAnim].getValue(time);
    }

    AnimatedUShort()
    {
        type = 0;
        times = null;
        data = null;
        dataIn = dataOut = null;
    }

    int getValue(int time)
    {
        return getValue(time, 0);
    }

    int getValue(int time, int def)
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
                    return (int)((float)data[idx] + (float)(data[idx + 1] - data[idx]) * r);
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
                return def;
        }
        if(data.length == 0)
            return def;
        else
            return data[0];
    }

    void read(ByteBuffer buf)
        throws IOException
    {
        read(buf, false);
    }

    void read(ByteBuffer buf, boolean dump)
        throws IOException
    {
        byte inByte = 0;
        inByte = buf.get();
        used = inByte != 0;
        int count = buf.getInt();
        if(dump)
            System.out.println((new StringBuilder("count: ")).append(count).toString());
        times = new int[count];
        for(int i = 0; i < count; i++)
        {
            times[i] = buf.getInt();
            if(dump)
                System.out.print((new StringBuilder(String.valueOf(times[i]))).append(" ").toString());
        }

        if(dump)
            System.out.println();
        type = buf.getInt();
        if(type == 2)
            System.out.println("rot hermite");
        short inShort = 0;
        count = buf.getInt();
        if(type == 2)
        {
            dataIn = new int[count];
            dataOut = new int[count];
            for(int i = 0; i < count; i++)
            {
                inShort = buf.getShort();
                dataIn[i] = inShort & 0x7fff;
                inShort = buf.getShort();
                dataOut[i] = inShort & 0x7fff;
            }

        } else
        {
            data = new int[count];
            for(int i = 0; i < count; i++)
            {
                inShort = buf.getShort();
                data[i] = inShort & 0x7fff;
                if(dump)
                    System.out.println((new StringBuilder("data ")).append(i).append(": ").append(data[i]).toString());
            }

        }
    }

    int type;
    int times[];
    int data[];
    int dataIn[];
    int dataOut[];
    boolean used;
}