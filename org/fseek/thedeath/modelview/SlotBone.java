// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Bone.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.vecmath.Point3f;

class SlotBone
{

    public SlotBone()
    {
        slot = 0;
        bone = -1;
        pos = null;
    }

    public void read(ByteBuffer buf)
        throws IOException
    {
        slot = buf.getShort();
        bone = buf.getShort();
        pos = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
    }

    short slot;
    short bone;
    Point3f pos;
}