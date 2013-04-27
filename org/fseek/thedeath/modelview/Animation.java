// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Animation.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

// Referenced classes of package modelview:
//            AnimatedVec3D, AnimatedQuat, Model

public class Animation
{

    Animation(int num)
    {
        index = 0;
        numAnimData = num;
        name = null;
        duration = elapsed = 0;
        trans = scale = null;
        rot = null;
        loop = true;
    }

    void read(ByteBuffer buf)
        throws IOException
    {
        duration = buf.getInt();
        trans = new AnimatedVec3D[numAnimData];
        rot = new AnimatedQuat[numAnimData];
        scale = new AnimatedVec3D[numAnimData];
        if(numAnimData > 0)
        {
            for(int i = 0; i < numAnimData; i++)
            {
                trans[i] = new AnimatedVec3D();
                rot[i] = new AnimatedQuat();
                scale[i] = new AnimatedVec3D();
                trans[i].read(buf);
                rot[i].read(buf);
                scale[i].read(buf);
            }

        }
    }

    void readMo2(ByteBuffer buf, int idx)
        throws IOException
    {
        index = idx;
        name = Model.readString(buf);
        flags = buf.getInt();
        duration = buf.getInt();
        if(numAnimData > 0)
        {
            trans = new AnimatedVec3D[numAnimData];
            rot = new AnimatedQuat[numAnimData];
            scale = new AnimatedVec3D[numAnimData];
            for(int i = 0; i < numAnimData; i++)
            {
                trans[i] = new AnimatedVec3D();
                rot[i] = new AnimatedQuat();
                scale[i] = new AnimatedVec3D();
                trans[i].read(buf);
                rot[i].read(buf);
                scale[i].read(buf);
            }

        }
    }

    @Override
    public String toString()
    {
        return name;
    }

    String name;
    int duration;
    int elapsed;
    int numAnimData;
    int flags;
    int index;
    AnimatedVec3D trans[];
    AnimatedVec3D scale[];
    AnimatedQuat rot[];
    boolean loop;
}