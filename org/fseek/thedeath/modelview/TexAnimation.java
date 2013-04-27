// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Animation.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

// Referenced classes of package modelview:
//            AnimatedVec3D, AnimatedQuat, Model

class TexAnimation
{

    TexAnimation()
    {
        read = false;
        trans = scale = null;
        rot = null;
        transMo2 = scaleMo2 = null;
        rotMo2 = null;
    }

    void read(ByteBuffer buf)
        throws IOException
    {
        trans = new AnimatedVec3D();
        rot = new AnimatedQuat();
        scale = new AnimatedVec3D();
        trans.read(buf);
        rot.read(buf);
        scale.read(buf);
        read = true;
    }

    void readMo2(ByteBuffer buf)
        throws IOException
    {
        transMo2 = Model.readAnimVec3(buf);
        rotMo2 = Model.readAnimQuat(buf);
        scaleMo2 = Model.readAnimVec3(buf);
        read = true;
    }

    AnimatedVec3D trans;
    AnimatedVec3D scale;
    AnimatedQuat rot;
    AnimatedVec3D transMo2[];
    AnimatedVec3D scaleMo2[];
    AnimatedQuat rotMo2[];
    boolean read;
}