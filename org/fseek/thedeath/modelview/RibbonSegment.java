// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ParticleEmitter.java

package org.fseek.thedeath.modelview;


// Referenced classes of package modelview:
//            Vec3

class RibbonSegment
{

    RibbonSegment()
    {
        pos = new Vec3();
        end = new Vec3();
        up = new Vec3();
        back = new Vec3();
    }

    Vec3 pos;
    Vec3 end;
    Vec3 up;
    Vec3 back;
    float len;
    float len2;
}