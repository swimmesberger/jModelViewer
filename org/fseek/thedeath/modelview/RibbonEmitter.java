// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ParticleEmitter.java

package org.fseek.thedeath.modelview;

import org.fseek.thedeath.modelview.interfaces.IGraphicContext;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.media.opengl.GL2;

// Referenced classes of package modelview:
//            Model, Vec3, Vec4, RibbonSegment, 
//            Bone, ModelViewerApplet, AnimatedVec3D, AnimatedUShort, 
//            AnimatedFloat, Material

class RibbonEmitter
{
    private IGraphicContext viewer;
    RibbonEmitter(IGraphicContext viewer)
    {
        this.viewer = viewer;
        spawnRemainder = 0.0F;
    }

    public void init()
    {
        material = model.mMaterials[textures[0]];
        parent = model.mBones[bone];
        curPos = new Vec3(position);
        curColor = new Vec4(1.0F, 1.0F, 1.0F, 1.0F);
        totalLength = resolution / length;
        RibbonSegment seg = new RibbonSegment();
        seg.pos.set(curPos);
        seg.len = 0.0F;
        segments = new LinkedList();
        segments.add(seg);
    }

    public void update(int anim, int time)
    {
        Vec3 npos = new Vec3(position);
        npos.transform(parent.matrix);
        Vec3 nup = new Vec3(position.x, position.y, position.z + 1.0F);
        nup.transform(parent.matrix);
        nup.sub(npos).normalize();
        float len = Vec3.sub(npos, curPos).length();
        RibbonSegment f = (RibbonSegment)segments.peekFirst();
        if(f.len > length)
        {
            Vec3.sub(curPos, npos, f.back).normalize();
            f.len2 = f.len;
            RibbonSegment n = new RibbonSegment();
            n.pos.set(npos);
            n.up.set(nup);
            n.len = len;
            segments.addFirst(n);
        } else
        {
            f.pos.set(npos);
            f.up.set(nup);
            f.len += len;
            float speed = len * (1000F / (float)viewer.getDelta());
            if(segments.size() > 1 && speed < 3F)
            {
                RibbonSegment l = (RibbonSegment)segments.peekLast();
                l.len -= totalLength * 3F * ((float)viewer.getDelta() * 0.001F);
                if(l.len < 0.0F)
                    segments.removeLast();
            }
        }
        float l = 0.0F;
        for(ListIterator it = segments.listIterator(); it.hasNext();)
        {
            RibbonSegment s = (RibbonSegment)it.next();
            l += s.len;
        }

        if(l > totalLength)
            segments.removeLast();
        curPos.set(npos);
        Vec3 c = new Vec3(AnimatedVec3D.getValue(color, anim, time));
        float a = (float)AnimatedUShort.getValue(transparency, anim, time) / 32767F;
        curColor.set(c.x, c.y, c.z, a);
        curAbove = AnimatedFloat.getValue(above, anim, time);
        curBelow = AnimatedFloat.getValue(below, anim, time);
    }

    public void draw(GL2 gl)
    {
        gl.glBindTexture(3553, material.mTextureId);
        gl.glEnable(3042);
        gl.glDisable(2896);
        gl.glDisable(3008);
        gl.glDisable(2884);
        gl.glDepthMask(false);
        gl.glBlendFunc(770, 1);
        gl.glColor4f(curColor.x, curColor.y, curColor.z, curColor.w);
        Vec3 v = new Vec3();
        Vec3 t = new Vec3();
        gl.glBegin(8);
        ListIterator it = segments.listIterator();
        RibbonSegment s;
        for(float l = 0.0F; it.hasNext(); l += s.len)
        {
            s = (RibbonSegment)it.next();
            float u = l / totalLength;
            Vec3.add(s.pos, Vec3.scale(s.up, curAbove, t), v);
            gl.glTexCoord2f(u, 0.0F);
            gl.glVertex3f(v.x, v.y, v.z);
            Vec3.sub(s.pos, Vec3.scale(s.up, curBelow, t), v);
            gl.glTexCoord2f(u, 1.0F);
            gl.glVertex3f(v.x, v.y, v.z);
        }

        if(segments.size() > 1)
        {
            Vec3 t2 = new Vec3();
            s = (RibbonSegment)segments.peekLast();
            Vec3.add(s.pos, Vec3.add(Vec3.scale(s.up, curAbove, t), Vec3.scale(s.back, s.len, t2)), v);
            gl.glTexCoord2f(1.0F, 0.0F);
            gl.glVertex3f(v.x, v.y, v.z);
            Vec3.sub(s.pos, Vec3.add(Vec3.scale(s.up, curAbove, t), Vec3.scale(s.back, s.len, t2)), v);
            gl.glTexCoord2f(1.0F, 1.0F);
            gl.glVertex3f(v.x, v.y, v.z);
        }
        gl.glEnd();
        gl.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        gl.glEnable(2896);
        gl.glBlendFunc(770, 771);
        gl.glDepthMask(true);
    }

    public void read(ByteBuffer buf, Model m)
        throws IOException
    {
        model = m;
        id = buf.getInt();
        bone = buf.getInt();
        position = new Vec3(buf.getFloat(), buf.getFloat(), buf.getFloat());
        int count = buf.getInt();
        if(count > 0)
        {
            textures = new int[count];
            for(int i = 0; i < count; i++)
                textures[i] = buf.getInt();

        }
        color = Model.readAnimVec3(buf);
        transparency = Model.readAnimUShort(buf);
        above = Model.readAnimFloat(buf);
        below = Model.readAnimFloat(buf);
        resolution = buf.getFloat();
        length = buf.getFloat();
        emissionAngle = buf.getFloat();
        s1 = buf.getShort();
        s2 = buf.getShort();
        init();
    }

    int id;
    int bone;
    Vec3 position;
    int textures[];
    AnimatedVec3D color[];
    AnimatedUShort transparency[];
    AnimatedFloat above[];
    AnimatedFloat below[];
    float resolution;
    float length;
    float totalLength;
    float emissionAngle;
    short s1;
    short s2;
    Model model;
    Material material;
    Bone parent;
    LinkedList segments;
    Vec3 curPos;
    Vec4 curColor;
    float curAbove;
    float curBelow;
    float spawnRemainder;
}