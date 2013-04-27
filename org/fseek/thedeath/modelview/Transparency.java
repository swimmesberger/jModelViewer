// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Material.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

// Referenced classes of package modelview:
//            Model, AnimatedUShort

class Transparency
{

    Transparency()
    {
    }

    public void read(ByteBuffer buf)
        throws IOException
    {
        alpha = Model.readAnimUShort(buf);
    }

    public float get(int anim, int time)
    {
        float ret = 1.0F;
        int thisAnim = 0;
        if(anim > alpha.length)
            thisAnim = 0;
        else
            thisAnim = anim;
        int opacity = alpha[thisAnim].getValue(time);
        ret = (float)opacity / 32767F;
        if(ret > 1.0001F)
            ret = 0.0F;
        return ret;
    }

    AnimatedUShort alpha[];
}