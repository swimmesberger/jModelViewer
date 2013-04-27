// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ModelLoader2.java

package org.fseek.thedeath.modelview;

import java.nio.ByteBuffer;
import javax.vecmath.*;

// Referenced classes of package modelview:
//            Model, Vertex, Material, FileLoader, 
//            Mesh, Bone, SlotBone, Animation, 
//            TexAnimation, Mo2Color, Transparency, ParticleEmitter, 
//            RibbonEmitter, SkinColor, FacialHair, HairStyle

class ModelLoader2
{

    ModelLoader2()
    {
    }

    static boolean load(Model model, ByteBuffer buf, boolean loadTextures, String path)
    {
        try
        {
            model.VersionNumber = buf.getInt();
            int headerLen = 0;
            int numVertices = 0;
            int numIndices = 0;
            int numTextures = 0;
            int numMeshes = 0;
            int numBones = 0;
            int numAttachments = 0;
            int numBoneLookup = 0;
            int numAnimations = 0;
            int numTexAnimations = 0;
            int numColors = 0;
            int numTransparency = 0;
            int numParticleEmitters = 0;
            int numRibbonEmitters = 0;
            int numSkinColors = 0;
            int numFaceTypes = 0;
            int numFacialStyles = 0;
            int numFacialColors = 0;
            int numHairStyles = 0;
            int numHairColors = 0;
            int ofsVertices = 0;
            int ofsIndices = 0;
            int ofsTextures = 0;
            int ofsMeshes = 0;
            int ofsBones = 0;
            int ofsAttachments = 0;
            int ofsBoneLookup = 0;
            int ofsAnimations = 0;
            int ofsTexAnimations = 0;
            int ofsColors = 0;
            int ofsTransparency = 0;
            int ofsParticleEmitters = 0;
            int ofsRibbonEmitters = 0;
            int ofsSkinColors = 0;
            int ofsFaceTypes = 0;
            int ofsFacialStyles = 0;
            int ofsFacialColors = 0;
            int ofsHairStyles = 0;
            int ofsHairColors = 0;
            headerLen = buf.getInt();
            numVertices = buf.getInt();
            ofsVertices = buf.getInt();
            numIndices = buf.getInt();
            ofsIndices = buf.getInt();
            numTextures = buf.getInt();
            ofsTextures = buf.getInt();
            numMeshes = buf.getInt();
            ofsMeshes = buf.getInt();
            numBones = buf.getInt();
            ofsBones = buf.getInt();
            numAttachments = buf.getInt();
            ofsAttachments = buf.getInt();
            numBoneLookup = buf.getInt();
            ofsBoneLookup = buf.getInt();
            numAnimations = buf.getInt();
            ofsAnimations = buf.getInt();
            numTexAnimations = buf.getInt();
            ofsTexAnimations = buf.getInt();
            numColors = buf.getInt();
            ofsColors = buf.getInt();
            numTransparency = buf.getInt();
            ofsTransparency = buf.getInt();
            numParticleEmitters = buf.getInt();
            ofsParticleEmitters = buf.getInt();
            numRibbonEmitters = buf.getInt();
            ofsRibbonEmitters = buf.getInt();
            numSkinColors = buf.getInt();
            ofsSkinColors = buf.getInt();
            numFaceTypes = buf.getInt();
            ofsFaceTypes = buf.getInt();
            numFacialStyles = buf.getInt();
            ofsFacialStyles = buf.getInt();
            numFacialColors = buf.getInt();
            ofsFacialColors = buf.getInt();
            numHairStyles = buf.getInt();
            ofsHairStyles = buf.getInt();
            numHairColors = buf.getInt();
            ofsHairColors = buf.getInt();
            buf.position(headerLen);
            model.mRace = buf.get();
            model.mGender = buf.get();
            if(numVertices > 0)
            {
                buf.position(ofsVertices);
                Vertex verts[] = new Vertex[numVertices];
                Vertex tVerts[] = new Vertex[numVertices];
                for(int i = 0; i < numVertices; i++)
                {
                    Vertex v = new Vertex();
                    v.readMo2(buf);
                    verts[i] = v;
                    tVerts[i] = new Vertex();
                    tVerts[i].mPosition = new Point3f(v.mPosition);
                    tVerts[i].mNormal = new Vector3f(v.mNormal);
                    tVerts[i].mTexCoord = new Point2f(v.mTexCoord);
                }

                model.mOrigVertices = verts;
                model.mVertices = tVerts;
            }
            if(numIndices > 0)
            {
                buf.position(ofsIndices);
                short indices[] = new short[numIndices];
                for(int i = 0; i < numIndices; i++)
                    indices[i] = buf.getShort();

                model.mIndices = indices;
            }
            if(numTextures > 0)
            {
                buf.position(ofsTextures);
                Material mats[] = new Material[numTextures];
                for(int i = 0; i < numTextures; i++)
                {
                    Material m = new Material();
                    m.readMo2(buf);
                    if(loadTextures && m.mFilename.length() > 0)
                    {
                        FileLoader fl = new FileLoader(1, model, m, (new StringBuilder("textures/")).append(m.mFilename).toString().toLowerCase(), model.getViewer());
                        fl.start();
                    }
                    mats[i] = m;
                }

                model.mMaterials = mats;
            }
            if(numMeshes > 0)
            {
                buf.position(ofsMeshes);
                Mesh meshes[] = new Mesh[numMeshes];
                for(int i = 0; i < numMeshes; i++)
                {
                    Mesh m = new Mesh();
                    m.readMo2(buf);
                    meshes[i] = m;
                }

                model.mMeshes = meshes;
            }
            if(numBones > 0)
            {
                buf.position(ofsBones);
                Bone bones[] = new Bone[numBones];
                for(int i = 0; i < numBones; i++)
                {
                    Bone b = new Bone(model, i);
                    b.readMo2(buf);
                    bones[i] = b;
                }

                model.mBones = bones;
            }
            if(numAttachments > 0)
            {
                buf.position(ofsAttachments);
                SlotBone slotBones[] = new SlotBone[numAttachments];
                for(int i = 0; i < numAttachments; i++)
                {
                    SlotBone slot = new SlotBone();
                    slot.read(buf);
                    slotBones[i] = slot;
                }

                model.mSlotBones = slotBones;
            }
            if(numBoneLookup > 0)
            {
                buf.position(ofsBoneLookup);
                model.mBoneLookup = new int[numBoneLookup];
                for(int i = 0; i < numBoneLookup; i++)
                    model.mBoneLookup[i] = buf.getInt();

            }
            if(numAnimations > 0)
            {
                buf.position(ofsAnimations);
                Animation anims[] = new Animation[numAnimations];
                for(int i = 0; i < numAnimations; i++)
                {
                    Animation a = new Animation(model.mBones.length);
                    a.readMo2(buf, i);
                    anims[i] = a;
                }
                model.setModelAnimations(anims);
            }
            if(numTexAnimations > 0)
            {
                buf.position(ofsTexAnimations);
                TexAnimation anims[] = new TexAnimation[numTexAnimations];
                for(int i = 0; i < numTexAnimations; i++)
                {
                    TexAnimation a = new TexAnimation();
                    a.readMo2(buf);
                    anims[i] = a;
                }

                model.mTexAnimations = anims;
            }
            if(numColors > 0)
            {
                buf.position(ofsColors);
                Mo2Color colors[] = new Mo2Color[numColors];
                for(int i = 0; i < numColors; i++)
                {
                    Mo2Color c = new Mo2Color();
                    c.read(buf);
                    colors[i] = c;
                }

                model.mColors = colors;
            }
            if(numTransparency > 0)
            {
                buf.position(ofsTransparency);
                Transparency trans[] = new Transparency[numTransparency];
                for(int i = 0; i < numTransparency; i++)
                {
                    Transparency t = new Transparency();
                    t.read(buf);
                    trans[i] = t;
                }

                model.mTransparency = trans;
            }
            if(numParticleEmitters > 0)
            {
                buf.position(ofsParticleEmitters);
                ParticleEmitter emitters[] = new ParticleEmitter[numParticleEmitters];
                for(int i = 0; i < numParticleEmitters; i++)
                {
                    ParticleEmitter p = new ParticleEmitter(model.getViewer());
                    p.read(buf, model);
                    emitters[i] = p;
                }

                model.mParticleEmitters = emitters;
            }
            if(numRibbonEmitters > 0)
            {
                buf.position(ofsRibbonEmitters);
                RibbonEmitter emitters[] = new RibbonEmitter[numRibbonEmitters];
                for(int i = 0; i < numRibbonEmitters; i++)
                {
                    RibbonEmitter r = new RibbonEmitter(model.getViewer());
                    r.read(buf, model);
                    emitters[i] = r;
                }

                model.mRibbonEmitters = emitters;
            }
            if(numSkinColors > 0)
            {
                buf.position(ofsSkinColors);
                SkinColor skins[] = new SkinColor[numSkinColors];
                for(int i = 0; i < numSkinColors; i++)
                {
                    SkinColor s = new SkinColor();
                    s.readMo2(buf);
                    skins[i] = s;
                }

                if(numFaceTypes > 0)
                {
                    buf.position(ofsFaceTypes);
                    for(int i = 0; i < numSkinColors; i++)
                        skins[i].readFacesMo2(buf);

                }
                model.mSkinColors = skins;
            }
            if(numFacialStyles > 0)
            {
                buf.position(ofsFacialStyles);
                FacialHair styles[] = new FacialHair[numFacialStyles];
                for(int i = 0; i < numFacialStyles; i++)
                {
                    FacialHair h = new FacialHair();
                    h.readMo2(buf);
                    styles[i] = h;
                }

                if(numFacialColors > 0)
                {
                    buf.position(ofsFacialColors);
                    for(int i = 0; i < numFacialStyles; i++)
                        styles[i].readColorsMo2(buf);

                }
                model.mFacialHairs = styles;
            }
            if(numHairStyles > 0)
            {
                buf.position(ofsHairStyles);
                HairStyle styles[] = new HairStyle[numHairStyles];
                for(int i = 0; i < numHairStyles; i++)
                {
                    HairStyle s = new HairStyle();
                    s.readMo2(buf);
                    styles[i] = s;
                }

                if(numHairColors > 0)
                {
                    buf.position(ofsHairColors);
                    for(int i = 0; i < numHairStyles; i++)
                        styles[i].readColorsMo2(buf);

                }
                model.mHairStyles = styles;
            }
            model.setup();
            model.mLoaded = true;
            model.SetAnimation("Stand");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return true;
    }
}