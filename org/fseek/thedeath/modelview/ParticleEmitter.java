// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ParticleEmitter.java

package org.fseek.thedeath.modelview;

import org.fseek.thedeath.modelview.interfaces.IGraphicContext;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import javax.vecmath.*;

// Referenced classes of package modelview:
//            Model, ModelViewerApplet, AnimatedFloat, AnimatedUShort, 
//            PlaneEmitter, SphereEmitter, Vec3, Particle, 
//            AnimatedSimpleVec2D, AnimatedSimpleVec3D, AnimatedSimpleUShort, Vec4, 
//            Material, Camera, Bone

class ParticleEmitter
{
    private IGraphicContext viewer;
    ParticleEmitter(IGraphicContext viewer)
    {
        this.viewer = viewer;
        spawnRemainder = 0.0F;
    }

    public void init()
    {
        particles = new ArrayList(100);
        texCoords = new ArrayList(tileRows * tileCols);
        material = model.mMaterials[texture];
        parent = model.mBones[bone];
        int order = particleType <= 0 ? 0 : -1;
        if(scale.z == 519F)
            scale.z = 1.5F;
        for(int i = 0; i < tileRows * tileCols; i++)
        {
            Point2f tc[] = new Point2f[4];
            Point2f a = new Point2f();
            Point2f b = new Point2f();
            int x = i % tileCols;
            int y = i / tileCols;
            a.x = (float)x * (1.0F / (float)tileCols);
            b.x = (float)(x + 1) * (1.0F / (float)tileCols);
            a.y = (float)y * (1.0F / (float)tileRows);
            b.y = (float)(y + 1) * (1.0F / (float)tileRows);
            for(int j = 0; j < 4; j++)
            {
                int idx = ((j + 4) - order) % 4;
                if(j == 0)
                    tc[idx] = a;
                else
                if(j == 2)
                    tc[idx] = b;
                else
                if(j == 1)
                    tc[idx] = new Point2f(b.x, a.y);
                else
                if(j == 3)
                    tc[idx] = new Point2f(a.x, b.y);
            }

            texCoords.add(tc);
        }

    }

    public void update(int anim, int time)
    {
        float dt = (float)viewer.getDelta() * 0.001F;
        float grav = AnimatedFloat.getValue(gravity, anim, time);
        float deaccel = AnimatedFloat.getValue(gravity2, anim, time);
        if(emitterType == 1 || emitterType == 2)
        {
            float rate = AnimatedFloat.getValue(emissionRate, anim, time);
            float life = AnimatedFloat.getValue(lifespan, anim, time);
            float toSpawn = 0.0F;
            if(life != 0.0F)
                toSpawn = (dt * rate) / life + spawnRemainder;
            else
                toSpawn = spawnRemainder;
            if(toSpawn < 1.0F)
            {
                spawnRemainder = toSpawn;
                if(spawnRemainder < 0.0F)
                    spawnRemainder = 0.0F;
            } else
            {
                int spawnCount = (int)toSpawn;
                if(spawnCount + particles.size() > 1000)
                    spawnCount = 1000 - particles.size();
                spawnRemainder = toSpawn - (float)spawnCount;
                float w = AnimatedFloat.getValue(areaWidth, anim, time) * 0.5F;
                float l = AnimatedFloat.getValue(areaLength, anim, time) * 0.5F;
                float speed = AnimatedFloat.getValue(emissionSpeed, anim, time);
                float var = AnimatedFloat.getValue(speedVariation, anim, time);
                float spread = AnimatedFloat.getValue(verticalRange, anim, time);
                float spread2 = AnimatedFloat.getValue(horizontalRange, anim, time);
                boolean en = true;
                int thisAnim = anim;
                if(thisAnim >= enabled.length)
                    thisAnim = 0;
                if(enabled.length > 0 && enabled[thisAnim].used)
                    en = enabled[thisAnim].getValue(time) != 0;
                if(en)
                {
                    for(int i = 0; i < spawnCount; i++)
                    {
                        Particle p;
                        if(emitterType == 1)
                            p = PlaneEmitter.newParticle(this, anim, time, w, l, speed, var, spread, spread2);
                        else
                            p = SphereEmitter.newParticle(this, anim, time, w, l, speed, var, spread, spread2);
                        particles.add(p);
                    }

                }
            }
        }
        float speed = 1.0F;
        Vec3 t1 = new Vec3();
        Vec3 t2 = new Vec3();
        Vec3 t3 = new Vec3();
        Point4f d = new Point4f();
        Point4f c[] = new Point4f[3];
        for(int i = 0; i < 3; i++)
            c[i] = new Point4f();

        for(int i = 0; i < particles.size();)
        {
            Particle p = (Particle)particles.get(i);
            p.speed.add(Vec3.sub(Vec3.scale(p.down, grav * dt, t1), Vec3.scale(p.dir, deaccel * dt, t2), t3));
            if(slowdown > 0.0F)
                speed = (float)Math.exp(-1F * slowdown * p.life);
            p.pos.add(Vec3.scale(p.speed, speed * dt, t1));
            p.life += dt;
            float lifePos = p.life / p.maxLife;
            float s1 = size.data[0].x;
            float s2 = 0.0F;
            float s3 = 0.0F;
            if(size.data.length > 1)
                s2 = size.data[1].x;
            else
                s2 = s1;
            if(size.data.length > 2)
                s3 = size.data[2].x;
            else
                s3 = s2;
            p.size = lifeInterp(lifePos, 0.5F, s1 * scale.x, s2 * scale.y, s3 * scale.z);
            int limit = Math.min(3, color.data.length);
            for(int j = 0; j < limit; j++)
            {
                Point3f t = color.data[j];
                c[j].set(t.x / 255F, t.y / 255F, t.z / 255F, (float)transparency.data[j] / 32767F);
            }

            if(limit < 3)
            {
                Point3f t = color.data[limit - 1];
                for(int j = limit - 1; j < 3; j++)
                    c[j].set(t.x / 255F, t.y / 255F, t.z / 255F, (float)transparency.data[j] / 32767F);

            }
            lifeInterp(lifePos, 0.5F, c[0], c[1], c[2], d);
            p.color.set(d);
            if(lifePos >= 1.0F)
                particles.remove(i);
            else
                i++;
        }

    }

    public float lifeInterp(float life, float mid, float a, float b, float c)
    {
        if(life <= mid)
            return a + (b - a) * (life / mid);
        else
            return b + (c - b) * ((life - mid) / (1.0F - mid));
    }

    public void lifeInterp(float life, float mid, Point4f a, Point4f b, Point4f c, Point4f dest)
    {
        if(life <= mid)
            dest.interpolate(a, b, life / mid);
        else
            dest.interpolate(b, c, (life - mid) / (1.0F - mid));
    }

    public void draw(GL2 gl)
    {
        if(particles.size() == 0)
            return;
        switch(blendMode)
        {
        case 0: // '\0'
            gl.glDisable(3042);
            gl.glDisable(3008);
            break;

        case 1: // '\001'
            gl.glEnable(3042);
            gl.glDisable(3008);
            gl.glBlendFunc(768, 1);
            break;

        case 2: // '\002'
            gl.glEnable(3042);
            gl.glDisable(3008);
            gl.glBlendFunc(770, 1);
            break;

        case 3: // '\003'
            gl.glDisable(3042);
            gl.glEnable(3008);
            break;

        case 4: // '\004'
            gl.glEnable(3042);
            gl.glDisable(3008);
            gl.glBlendFunc(770, 1);
            break;
        }
        gl.glBindTexture(3553, model.mMaterials[texture].mTextureId);
        gl.glPushMatrix();
        if(particleType == 0 || particleType == 2)
        {
            if((flags & 0x1000) == 0)
            {
                Vec3 view = new Vec3(viewer.getCamera().getPosition());
                view.normalize();
                Vec3 right = Vec3.cross(view, new Vec3(0.0F, 0.0F, 1.0F)).normalize();
                Vec3 up = Vec3.cross(right, view).normalize();
                int tcStart = 0;
                if(flags == 0x40019)
                    tcStart++;
                gl.glBegin(7);
                int count = particles.size();
                Vec3 pos = new Vec3();
                Vec3 cPos = new Vec3();
                Vec3 ofs = new Vec3();
                for(int i = 0; i < count; i++)
                {
                    Particle p = (Particle)particles.get(i);
                    if(p.tile < texCoords.size())
                    {
                        gl.glColor4f(p.color.x, p.color.y, p.color.z, p.color.w);
                        Point2f tc[] = (Point2f[])texCoords.get(p.tile);
                        Vec3.add(right, up, ofs);
                        cPos.set(p.pos);
                        Vec3.sub(cPos, ofs.scale(p.size), pos);
                        gl.glTexCoord2f(tc[tcStart % 4].x, tc[tcStart % 4].y);
                        gl.glVertex3f(pos.x, pos.y, pos.z);
                        Vec3.sub(right, up, ofs);
                        Vec3.add(cPos, ofs.scale(p.size), pos);
                        gl.glTexCoord2f(tc[(tcStart + 1) % 4].x, tc[(tcStart + 1) % 4].y);
                        gl.glVertex3f(pos.x, pos.y, pos.z);
                        Vec3.add(right, up, ofs);
                        Vec3.add(cPos, ofs.scale(p.size), pos);
                        gl.glTexCoord2f(tc[(tcStart + 2) % 4].x, tc[(tcStart + 2) % 4].y);
                        gl.glVertex3f(pos.x, pos.y, pos.z);
                        Vec3.sub(right, up, ofs);
                        Vec3.sub(cPos, ofs.scale(p.size), pos);
                        gl.glTexCoord2f(tc[(tcStart + 3) % 4].x, tc[(tcStart + 3) % 4].y);
                        gl.glVertex3f(pos.x, pos.y, pos.z);
                    }
                }

                gl.glEnd();
            } else
            {
                gl.glBegin(7);
                int count = particles.size();
                Vec3 pos = new Vec3();
                Vec3 ofs = new Vec3();
                for(int i = 0; i < count; i++)
                {
                    Particle p = (Particle)particles.get(i);
                    if(p.tile < texCoords.size())
                    {
                        gl.glColor4f(p.color.x, p.color.y, p.color.z, p.color.w);
                        Point2f tc[] = (Point2f[])texCoords.get(p.tile);
                        Vec3.add(p.pos, Vec3.scale(p.corners[0], p.size, ofs), pos);
                        gl.glTexCoord2f(tc[0].x, tc[0].y);
                        gl.glVertex3f(pos.x, pos.y, pos.z);
                        Vec3.add(p.pos, Vec3.scale(p.corners[1], p.size, ofs), pos);
                        gl.glTexCoord2f(tc[1].x, tc[1].y);
                        gl.glVertex3f(pos.x, pos.y, pos.z);
                        Vec3.add(p.pos, Vec3.scale(p.corners[2], p.size, ofs), pos);
                        gl.glTexCoord2f(tc[2].x, tc[2].y);
                        gl.glVertex3f(pos.x, pos.y, pos.z);
                        Vec3.add(p.pos, Vec3.scale(p.corners[3], p.size, ofs), pos);
                        gl.glTexCoord2f(tc[3].x, tc[3].y);
                        gl.glVertex3f(pos.x, pos.y, pos.z);
                    }
                }

                gl.glEnd();
            }
        } else
        if(particleType == 1)
        {
            gl.glBegin(7);
            int count = particles.size();
            Vec3 pos = new Vec3();
            Vec3 ofs = new Vec3();
            float f = 1.0F;
            Vec3 bv0 = new Vec3(-f, f, 0.0F);
            Vec3 bv1 = new Vec3(f, f, 0.0F);
            for(int i = 0; i < count; i++)
            {
                Particle p = (Particle)particles.get(i);
                if(p.tile >= texCoords.size() - 1)
                    break;
                gl.glColor4f(p.color.x, p.color.y, p.color.z, p.color.w);
                Point2f tc[] = (Point2f[])texCoords.get(p.tile);
                Vec3.add(p.pos, Vec3.scale(bv0, p.size, ofs), pos);
                gl.glTexCoord2f(tc[0].x, tc[0].y);
                gl.glVertex3f(pos.x, pos.y, pos.z);
                Vec3.add(p.pos, Vec3.scale(bv1, p.size, ofs), pos);
                gl.glTexCoord2f(tc[1].x, tc[1].y);
                gl.glVertex3f(pos.x, pos.y, pos.z);
                Vec3.add(p.origin, Vec3.scale(bv1, p.size, ofs), pos);
                gl.glTexCoord2f(tc[2].x, tc[2].y);
                gl.glVertex3f(pos.x, pos.y, pos.z);
                Vec3.add(p.origin, Vec3.scale(bv0, p.size, ofs), pos);
                gl.glTexCoord2f(tc[3].x, tc[3].y);
                gl.glVertex3f(pos.x, pos.y, pos.z);
            }

            gl.glEnd();
        }
        gl.glPopMatrix();
    }

    public void read(ByteBuffer buf, Model m)
        throws IOException
    {
        model = m;
        id = buf.getInt();
        flags = buf.getInt();
        position = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
        bone = buf.getShort();
        texture = buf.getShort();
        blendMode = buf.get();
        emitterType = buf.get();
        particleColor = buf.getShort();
        particleType = buf.get();
        headTail = buf.get();
        tileRotation = buf.getShort();
        tileRows = buf.getShort();
        tileCols = buf.getShort();
        color = new AnimatedSimpleVec3D();
        transparency = new AnimatedSimpleUShort();
        size = new AnimatedSimpleVec2D();
        intensity = new AnimatedSimpleUShort();
        emissionSpeed = Model.readAnimFloat(buf);
        speedVariation = Model.readAnimFloat(buf);
        verticalRange = Model.readAnimFloat(buf);
        horizontalRange = Model.readAnimFloat(buf);
        gravity = Model.readAnimFloat(buf);
        lifespan = Model.readAnimFloat(buf);
        emissionRate = Model.readAnimFloat(buf);
        areaLength = Model.readAnimFloat(buf);
        areaWidth = Model.readAnimFloat(buf);
        gravity2 = Model.readAnimFloat(buf);
        color.read(buf);
        transparency.read(buf);
        size.read(buf);
        intensity.read(buf);
        scale = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
        slowdown = buf.getFloat();
        rotation = buf.getFloat();
        modelRot1 = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
        modelRot2 = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
        modelTrans = new Point3f(buf.getFloat(), buf.getFloat(), buf.getFloat());
        enabled = Model.readAnimUShort(buf);
        init();
    }

    int id;
    int flags;
    Point3f position;
    short bone;
    short texture;
    byte blendMode;
    byte emitterType;
    short particleColor;
    byte particleType;
    byte headTail;
    short tileRotation;
    short tileRows;
    short tileCols;
    AnimatedFloat emissionSpeed[];
    AnimatedFloat speedVariation[];
    AnimatedFloat verticalRange[];
    AnimatedFloat horizontalRange[];
    AnimatedFloat gravity[];
    AnimatedFloat lifespan[];
    AnimatedFloat emissionRate[];
    AnimatedFloat areaLength[];
    AnimatedFloat areaWidth[];
    AnimatedFloat gravity2[];
    AnimatedSimpleVec3D color;
    AnimatedSimpleUShort transparency;
    AnimatedSimpleVec2D size;
    AnimatedSimpleUShort intensity;
    Point3f scale;
    float slowdown;
    float rotation;
    Point3f modelRot1;
    Point3f modelRot2;
    Point3f modelTrans;
    AnimatedUShort enabled[];
    Model model;
    ArrayList particles;
    Material material;
    Bone parent;
    ArrayList texCoords;
    float spawnRemainder;
}