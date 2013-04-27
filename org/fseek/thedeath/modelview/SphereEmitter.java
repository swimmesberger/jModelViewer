// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ParticleEmitter.java

package org.fseek.thedeath.modelview;

import javax.vecmath.Matrix4f;

// Referenced classes of package modelview:
//            Particle, ParticleEmitter, Model, Bone, 
//            Vec3, AnimatedFloat, Vec4

class SphereEmitter
{

    SphereEmitter()
    {
    }

    public static Particle newParticle(ParticleEmitter emitter, int anim, int time, float w, float l, float speed, float var, float spread, 
            float spread2)
    {
        Particle p = new Particle();
        float radius = (float)Math.random();
        float t = 0.0F;
        if(spread == 0.0F)
            t = (float)(Math.random() * 3.1415926535897931D * 2D - 3.1415926535897931D);
        else
            t = (float)Math.random() * spread * 2.0F - spread;
        Matrix4f mat = new Matrix4f(emitter.model.mBones[emitter.bone].rotMatrix);
        Matrix4f spreadMat = Particle.CalcSpread(spread * 2.0F, spread * 2.0F, w, l);
        mat.mul(spreadMat);
        if((emitter.flags & 0x39) == 57 || (emitter.flags & 0x139) == 313)
        {
            Vec3 dir = new Vec3(w * (float)Math.cos(t) * 1.6F, l * (float)Math.sin(t) * 1.6F, 0.0F);
            p.pos = new Vec3(emitter.position);
            p.pos.add(dir);
            p.pos.transform(emitter.parent.matrix);
            if(dir.lengthSquared() == 0.0F)
            {
                p.speed = new Vec3(0.0F, 0.0F, 0.0F);
            } else
            {
                dir.normalize();
                dir.transform(emitter.parent.rotMatrix);
                p.speed = dir.normalize();
                p.speed.scale(speed * ((1.0F + (float)Math.random() * var * 2.0F) - var));
            }
            p.dir = new Vec3(dir);
        } else
        {
            Vec3 dir = new Vec3(0.0F, 0.0F, 1.0F);
            dir.transform(mat).scale(radius);
            p.pos = new Vec3(emitter.position);
            p.pos.add(dir);
            p.pos.transform(emitter.parent.matrix);
            if(dir.lengthSquared() == 0.0F && (emitter.flags & 0x100) == 0)
            {
                p.speed = new Vec3(0.0F, 0.0F, 0.0F);
                p.dir = new Vec3(0.0F, 0.0F, 1.0F);
                p.dir.transform(emitter.model.mBones[emitter.bone].rotMatrix);
            } else
            {
                if((emitter.flags & 0x100) > 0)
                {
                    p.dir = new Vec3(0.0F, 0.0F, 1.0F);
                    p.dir.transform(emitter.model.mBones[emitter.bone].rotMatrix);
                } else
                {
                    p.dir = new Vec3(dir);
                }
                p.speed = new Vec3(p.dir);
                p.speed.scale(speed * (1.0F + ((float)Math.random() * var * 2.0F - var)));
            }
        }
        p.dir.normalize();
        p.down = new Vec3(0.0F, 0.0F, -1F);
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