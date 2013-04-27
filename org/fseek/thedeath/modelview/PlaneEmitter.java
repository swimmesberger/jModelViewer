// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ParticleEmitter.java

package org.fseek.thedeath.modelview;

import javax.vecmath.Matrix4f;

// Referenced classes of package modelview:
//            Particle, ParticleEmitter, Model, Bone, 
//            Vec3, AnimatedFloat, Vec4

class PlaneEmitter
{

    PlaneEmitter()
    {
    }

    public static Particle newParticle(ParticleEmitter emitter, int anim, int time, float w, float l, float speed, float var, float spread, 
            float spread2)
    {
        Particle p = new Particle();
        Matrix4f mat = new Matrix4f(emitter.model.mBones[emitter.bone].rotMatrix);
        Matrix4f spreadMat = Particle.CalcSpread(spread, spread, 1.0F, 1.0F);
        mat.mul(spreadMat);
        p.pos = new Vec3(emitter.position);
        p.pos.add(new Vec3((float)Math.random() * l * 2.0F - l, (float)Math.random() * w * 2.0F - w, 0.0F));
        p.pos.transform(emitter.parent.matrix);
        p.dir = new Vec3(0.0F, 0.0F, 1.0F);
        p.dir.transform(emitter.parent.rotMatrix);
        p.speed = new Vec3(p.dir);
        p.speed.normalize();
        p.speed.scale(speed * (1.0F + ((float)Math.random() * var * 2.0F - var)));
        p.down = new Vec3(0.0F, 0.0F, -1F);
        if((emitter.flags & 0x1000) > 0)
        {
            p.corners = new Vec3[4];
            p.corners[0] = new Vec3(-1F, 1.0F, 0.0F);
            p.corners[0].transform(mat);
            p.corners[1] = new Vec3(1.0F, 1.0F, 0.0F);
            p.corners[1].transform(mat);
            p.corners[2] = new Vec3(1.0F, -1F, 0.0F);
            p.corners[2].transform(mat);
            p.corners[3] = new Vec3(-1F, -1F, 0.0F);
            p.corners[3].transform(mat);
        }
        p.life = 0.0F;
        p.maxLife = AnimatedFloat.getValue(emitter.lifespan, anim, time);
        if(p.maxLife == 0.0F)
            p.maxLife = 1.0F;
        p.origin = new Vec3(p.pos);
        p.tile = (int)(Math.random() * (double)emitter.tileRows * (double)emitter.tileCols);
        p.color = new Vec4(1.0F, 1.0F, 1.0F, 1.0F);
        return p;
    }
}