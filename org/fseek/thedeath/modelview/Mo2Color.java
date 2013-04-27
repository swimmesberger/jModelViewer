// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Material.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;

// Referenced classes of package modelview:
//            Model, AnimatedVec3D, AnimatedUShort

class Mo2Color
{

    Mo2Color()
    {
    }

    public void read(ByteBuffer buf)
        throws IOException
    {
        rgb = Model.readAnimVec3(buf);
        alpha = Model.readAnimUShort(buf);
    }

    public Point4f get(int anim, int time)
    {
        Point4f ret = new Point4f();
        int thisAnim = 0;
        if(anim > rgb.length)
            thisAnim = 0;
        else
            thisAnim = anim;
        Point3f color = rgb[thisAnim].getValue(time);
        if(anim > alpha.length)
            thisAnim = 0;
        else
            thisAnim = anim;
        int opacity = alpha[thisAnim].getValue(time);
        ret.set(color.x, color.y, color.z, (float)opacity / 32767F);
        return ret;
    }

    AnimatedVec3D rgb[];
    AnimatedUShort alpha[];
}