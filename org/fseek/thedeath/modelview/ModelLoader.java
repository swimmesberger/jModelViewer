// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ModelLoader.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

// Referenced classes of package modelview:
//            Model, Vertex, Material, FileLoader, 
//            Mesh, SpecialTexture, BakedTexture, Bone, 
//            SlotBone, SkinColor, FacialHair, HairStyle, 
//            TexAnimation, Animation

class ModelLoader
{

    ModelLoader()
    {
    }

    static boolean load(Model model, ByteBuffer buf, boolean loadTextures, String path)
    {
        try
        {
            int VersionNumber = buf.getInt();
            model.VersionNumber = VersionNumber;
            short numVertices = 0;
            short numMaterials = 0;
            short numMeshes = 0;
            short numTexGroups = 0;
            short numBakedTextures = 0;
            short numBones = 0;
            char numIndices = '\0';
            int ofsVertices = 0;
            int ofsIndices = 0;
            int ofsMaterials = 0;
            int ofsMeshes = 0;
            int ofsTexGroups = 0;
            int ofsBakedTextures = 0;
            int ofsBones = 0;
            int ofsSlotBones = 0;
            short numSkinColors = 0;
            short numFacialHairs = 0;
            short numHairStyles = 0;
            short numTexAnimations = 0;
            int ofsSkinColors = 0;
            int ofsFacialHairs = 0;
            int ofsHairStyles = 0;
            int ofsTexAnimations = 0;
            short numPassesExt = 0;
            int ofsPassesExt = 0;
            short numOrigVertices = 0;
            short numBoneLookup = 0;
            short numAnimData = 0;
            int ofsOrigVertices = 0;
            int ofsBoneLookup = 0;
            int ofsAnimData = 0;
            short numLoopAnims = 0;
            short numActiveAnims = 0;
            int ofsLoopAnims = 0;
            int ofsActiveAnims = 0;
            int headerLen = 0;
            headerLen = buf.getInt();
            numVertices = buf.getShort();
            ofsVertices = buf.getInt();
            numIndices = buf.getChar();
            ofsIndices = buf.getInt();
            numMaterials = buf.getShort();
            ofsMaterials = buf.getInt();
            numMeshes = buf.getShort();
            ofsMeshes = buf.getInt();
            numTexGroups = buf.getShort();
            ofsTexGroups = buf.getInt();
            numBakedTextures = buf.getShort();
            ofsBakedTextures = buf.getInt();
            numBones = buf.getShort();
            ofsBones = buf.getInt();
            short numSlotBones = buf.getShort();
            ofsSlotBones = buf.getInt();
            numSkinColors = buf.getShort();
            ofsSkinColors = buf.getInt();
            numFacialHairs = buf.getShort();
            ofsFacialHairs = buf.getInt();
            numHairStyles = buf.getShort();
            ofsHairStyles = buf.getInt();
            if(VersionNumber >= 490)
            {
                numPassesExt = buf.getShort();
                ofsPassesExt = buf.getInt();
                numTexAnimations = buf.getShort();
                ofsTexAnimations = buf.getInt();
                if(ofsPassesExt == 0);
            }
            if(VersionNumber >= 500)
            {
                numOrigVertices = buf.getShort();
                ofsOrigVertices = buf.getInt();
                numBoneLookup = buf.getShort();
                ofsBoneLookup = buf.getInt();
                numAnimData = buf.getShort();
                ofsAnimData = buf.getInt();
                if(ofsOrigVertices != 0)
                    if(ofsBoneLookup == 0);
            }
            if(VersionNumber >= 510)
            {
                numLoopAnims = buf.getShort();
                ofsLoopAnims = buf.getInt();
                numActiveAnims = buf.getShort();
                ofsActiveAnims = buf.getInt();
                if(ofsLoopAnims == 0);
            }
            if(ofsVertices != 0 && ofsIndices != 0 && ofsMaterials != 0 && ofsMeshes != 0 && ofsTexGroups != 0 && ofsBakedTextures != 0 && ofsBones != 0 && ofsSlotBones != 0 && ofsSkinColors != 0 && ofsFacialHairs != 0)
                if(ofsHairStyles == 0);
            model.mOpacity = buf.getFloat();
            model.mGender = buf.get();
            model.mRace = buf.get();
            if(numVertices > 0)
            {
                Vertex vertices[] = new Vertex[numVertices];
                for(int i = 0; i < numVertices; i++)
                {
                    Vertex vert = new Vertex();
                    vert.read(buf);
                    vertices[i] = vert;
                }

                model.mVertices = vertices;
            }
            if(numIndices > 0)
            {
                short indices[] = new short[numIndices];
                for(int i = 0; i < numIndices; i++)
                    indices[i] = buf.getShort();

                model.mIndices = indices;
            }
            if(numMaterials > 0)
            {
                Material materials[] = new Material[numMaterials];
                for(int i = 0; i < numMaterials; i++)
                {
                    Material mat = new Material();
                    mat.read(buf);
                    if(loadTextures && mat.mFilename.length() > 0)
                    {
                        FileLoader fl = new FileLoader(1, model, mat, (new StringBuilder("textures/")).append(mat.mFilename).toString().toLowerCase(), model.getViewer());
                        fl.start();
                    }
                    materials[i] = mat;
                }

                model.mMaterials = materials;
            }
            if(numMeshes > 0)
            {
                Mesh meshes[] = new Mesh[numMeshes];
                for(int i = 0; i < numMeshes; i++)
                {
                    Mesh mesh = new Mesh();
                    mesh.read(buf);
                    meshes[i] = mesh;
                }

                model.mMeshes = meshes;
            }
            for(int i = 0; i < numTexGroups; i++)
            {
                SpecialTexture tex = new SpecialTexture();
                tex.read(buf);
                if(loadTextures)
                    tex.load(model);
                model.mSpecialTextures.add(tex);
            }

            for(int i = 0; i < numBakedTextures; i++)
            {
                BakedTexture tex = new BakedTexture();
                tex.read(buf);
                if(loadTextures)
                    tex.load(model);
                model.mBakedTextures.add(tex);
            }

            if(numBones > 0)
            {
                Bone bones[] = new Bone[numBones];
                for(int i = 0; i < numBones; i++)
                {
                    Bone bone = new Bone(model, i);
                    bone.read(buf);
                    bones[i] = bone;
                }

                model.mBones = bones;
            }
            if(numSlotBones > 0)
            {
                SlotBone slotBones[] = new SlotBone[numSlotBones];
                for(int i = 0; i < numSlotBones; i++)
                {
                    SlotBone slot = new SlotBone();
                    slot.read(buf);
                    slotBones[i] = slot;
                }

                model.mSlotBones = slotBones;
            }
            model.mNumFaceTypes = buf.getInt();
            if(numSkinColors > 0)
            {
                SkinColor skinColors[] = new SkinColor[numSkinColors];
                for(int i = 0; i < numSkinColors; i++)
                {
                    SkinColor skin = new SkinColor();
                    skin.read(buf, model.mNumFaceTypes);
                    skinColors[i] = skin;
                }

                model.mSkinColors = skinColors;
            }
            model.mNumFacialHairColors = buf.getInt();
            if(numFacialHairs > 0)
            {
                FacialHair facialHairs[] = new FacialHair[numFacialHairs];
                for(int i = 0; i < numFacialHairs; i++)
                {
                    FacialHair hair = new FacialHair();
                    hair.read(buf, model.mNumFacialHairColors);
                    facialHairs[i] = hair;
                }

                model.mFacialHairs = facialHairs;
            }
            model.mNumHairColors = buf.getInt();
            if(numHairStyles > 0)
            {
                HairStyle hairStyles[] = new HairStyle[numHairStyles];
                for(int i = 0; i < numHairStyles; i++)
                {
                    HairStyle hair = new HairStyle();
                    hair.read(buf, model.mNumHairColors);
                    hairStyles[i] = hair;
                }

                model.mHairStyles = hairStyles;
            }
            if(VersionNumber >= 490)
            {
                for(int i = 0; i < numPassesExt; i++)
                    model.mMeshes[i].readExt(buf);

                if(numTexAnimations > 0)
                {
                    TexAnimation texAnimations[] = new TexAnimation[numTexAnimations];
                    for(int i = 0; i < numTexAnimations; i++)
                    {
                        TexAnimation anim = new TexAnimation();
                        anim.read(buf);
                        texAnimations[i] = anim;
                    }

                    model.mTexAnimations = texAnimations;
                }
            }
            if(VersionNumber >= 500)
            {
                model.mNumBoneLookup = numBoneLookup;
                model.mNumOrigVertices = numOrigVertices;
                model.mNumAnimData = numAnimData;
                if(VersionNumber == 500)
                {
                    model.mNumLoopAnims = 1;
                    model.mNumActiveAnims = 0;
                    loadAnims(model, buf);
                } else
                if(VersionNumber >= 510)
                {
                    model.mNumLoopAnims = numLoopAnims;
                    model.mNumActiveAnims = numActiveAnims;
                    FileLoader fl = new FileLoader(1005, model, (new StringBuilder(String.valueOf(path.substring(0, path.length() - 3)))).append("anm").toString().toLowerCase(), model.getViewer());
                    fl.start();
                }
            }
            model.setup();
            model.mLoaded = true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return true;
    }

    static boolean loadAnims(Model model, ByteBuffer buf)
        throws IOException
    {
        int numOrigVertices = model.mNumOrigVertices;
        int numLoopAnims = model.mNumLoopAnims;
        int numActiveAnims = model.mNumActiveAnims;
        int numAnimData = model.mNumAnimData;
        int numBoneLookup = model.mNumBoneLookup;
        if(numOrigVertices > 0)
        {
            Vertex origVertices[] = new Vertex[numOrigVertices];
            for(int i = 0; i < numOrigVertices; i++)
            {
                Vertex vert = new Vertex();
                vert.readExt(buf);
                origVertices[i] = vert;
            }

            model.mOrigVertices = origVertices;
        }
        if(numBoneLookup > 0)
        {
            int boneLookup[] = new int[numBoneLookup];
            for(int i = 0; i < numBoneLookup; i++)
                boneLookup[i] = buf.getInt();

            model.mBoneLookup = boneLookup;
        }
        if(numLoopAnims > 0)
        {
            Animation loopAnims[] = new Animation[numLoopAnims];
            for(int i = 0; i < numLoopAnims; i++)
                loopAnims[i] = new Animation(numAnimData);

            model.mLoopAnims = loopAnims;
        }
        if(numActiveAnims > 0)
        {
            Animation activeAnims[] = new Animation[numActiveAnims];
            for(int i = 0; i < numActiveAnims; i++)
            {
                activeAnims[i] = new Animation(numAnimData);
                activeAnims[i].name = Model.readString(buf);
                byte tmpbool = buf.get();
                activeAnims[i].loop = tmpbool == 1;
            }

            model.mActiveAnims = activeAnims;
        }
        for(int i = 0; i < numLoopAnims; i++)
            model.mLoopAnims[i].read(buf);

        for(int i = 0; i < numActiveAnims; i++)
            model.mActiveAnims[i].read(buf);

        model.mCurrentAnims = new ArrayList();
        model.mCurrentAnims.add(model.mLoopAnims[0]);
        model.mCurrentAnimIndex = 0;
        return true;
    }
}