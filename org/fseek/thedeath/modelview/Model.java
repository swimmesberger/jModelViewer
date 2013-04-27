// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Model.java

package org.fseek.thedeath.modelview;

import com.jogamp.opengl.util.awt.TextureRenderer;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.*;

// Referenced classes of package modelview:
//            FileRequester, AnimatedVec3D, AnimatedUShort, AnimatedFloat, 
//            AnimatedQuat, ModelViewer, ModelLoader2, ModelLoader, 
//            FileLoader, Animation, Mesh, Mo2Color, 
//            Transparency, Vertex, Camera, Armor, 
//            Material, SpecialTexture, Bone, HairStyle, 
//            FacialHair, BakedTexture, SlotBone, ParticleEmitter, 
//            RibbonEmitter, ModelTexture, TexAnimation, SkinColor

public class Model
    implements FileRequester
{
    private ModelViewer viewer;
    
    public ModelViewer getViewer(){
        return this.viewer;
    }
    
    boolean modelDownloadFailed = false;
    
    static String readString(ByteBuffer buf)
        throws IOException
    {
        int len = buf.getShort();
        if(len == 0)
        {
            return new String("");
        } else
        {
            StringBuffer str = new StringBuffer(len);
            byte strbuf[] = new byte[len];
            buf.get(strbuf);
            str.append(new String(strbuf, "US-ASCII"));
            return str.toString();
        }
    }

    static AnimatedVec3D[] readAnimVec3(ByteBuffer buf)
        throws IOException
    {
        int count = buf.getInt();
        AnimatedVec3D anims[] = new AnimatedVec3D[count];
        for(int i = 0; i < count; i++)
        {
            anims[i] = new AnimatedVec3D();
            anims[i].read(buf);
        }

        return anims;
    }

    static AnimatedUShort[] readAnimUShort(ByteBuffer buf)
        throws IOException
    {
        return readAnimUShort(buf, false);
    }

    static AnimatedUShort[] readAnimUShort(ByteBuffer buf, boolean dump)
        throws IOException
    {
        int count = buf.getInt();
        AnimatedUShort anims[] = new AnimatedUShort[count];
        for(int i = 0; i < count; i++)
        {
            anims[i] = new AnimatedUShort();
            anims[i].read(buf, dump);
        }

        return anims;
    }

    static AnimatedFloat[] readAnimFloat(ByteBuffer buf)
        throws IOException
    {
        int count = buf.getInt();
        AnimatedFloat anims[] = new AnimatedFloat[count];
        for(int i = 0; i < count; i++)
        {
            anims[i] = new AnimatedFloat();
            anims[i].read(buf);
        }

        return anims;
    }

    static AnimatedQuat[] readAnimQuat(ByteBuffer buf)
        throws IOException
    {
        int count = buf.getInt();
        AnimatedQuat anims[] = new AnimatedQuat[count];
        for(int i = 0; i < count; i++)
        {
            anims[i] = new AnimatedQuat();
            anims[i].read(buf);
        }

        return anims;
    }

    public void RequestComplete(Object origin, int id, InputStream stream, String path)
    {
        mPath = path;
        if(id == 10000)
        {
            int race = -1;
            int gender = -1;
            boolean reloading = false;
            if(mLoaded)
            {
                reloading = true;
                race = mRace;
                gender = mGender;
                ResetModel();
            }
            mNewArmor = true;
            try
            {
                ModelLoader2.load(this, ModelUtil.bufferData(stream), true, path);
                if(reloading)
                    ReloadModels(race, gender);
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        } else
        if(id == 1000)
        {
            int race = -1;
            int gender = -1;
            boolean reloading = false;
            if(mLoaded)
            {
                reloading = true;
                race = mRace;
                gender = mGender;
                ResetModel();
            }
            mNewArmor = true;
            try
            {
                ModelLoader.load(this, ModelUtil.bufferData(stream), true, path);
                if(reloading)
                    ReloadModels(race, gender);
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        } else
        if(id == 1001)
            AddArmor(stream, true);
        else
        if(id == 1002)
            SetCharacter(stream);
        else
        if(id == 1003)
            SetHelmHairVis(stream);
        else
        if(id == 1004)
            SetModel(stream);
        else
        if(id == 1005)
            try
            {
                ModelLoader.loadAnims(this, ModelUtil.bufferData(stream));
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
    }

    public void RequestFailed(Object origin, int id, String path)
    {
        if(id == 10000)
        {
            path = path.replace(".mo2", ".mom");
            FileLoader fl = new FileLoader(1000, (Model)origin, path, viewer);
            fl.start();
        }
        else if(id == 1004)
        {
            path = path.replace(".mum", ".mo2");
            path = path.replace("npc/", "char/");
            FileLoader fl = new FileLoader(10000, (Model)origin, path, viewer);
            fl.start();
        }
        else if(id == 1000)
        {
            path = path.replace(".mom", ".sis");
            path = path.replace("char/", "npc/");
            FileLoader fl = new FileLoader(1002, (Model)origin, path, viewer);
            fl.start();
        }
        else
        {
            modelDownloadFailed = true;
        }
    }

    public int GetNumAnimations()
    {
        if(VersionNumber >= 1000)
            return mAnimations.length;
        else
            return mActiveAnims.length;
    }

    public String GetAnimation(int index)
    {
        if(VersionNumber >= 1000)
        {
            if(index < mAnimations.length && index >= 0)
                return mAnimations[index].name;
        } else
        if(index < mActiveAnims.length && index >= 0)
            return mActiveAnims[index].name;
        return "";
    }

    public int GetNumAnimationFrames()
    {
        if(mCurrentAnims != null && mCurrentAnims.size() > 0)
            return ((Animation)mCurrentAnims.get(mCurrentAnimIndex)).numAnimData;
        else
            return 0;
    }

    public void SetAnimationFrame(int frame)
    {
        if(mCurrentAnims == null || mCurrentAnims.size() == 0 || frame >= ((Animation)mCurrentAnims.get(mCurrentAnimIndex)).numAnimData || frame < 0)
        {
            return;
        } else
        {
            int timePerFrame = ((Animation)mCurrentAnims.get(mCurrentAnimIndex)).duration / ((Animation)mCurrentAnims.get(mCurrentAnimIndex)).numAnimData;
            mStartTime = mTime - timePerFrame * frame;
            return;
        }
    }

    public Model(ModelViewer main)
    {
        this.viewer = main;
        VersionNumber = 450;
        boundsSet = false;
        mFreeze = false;
        ResetModel();
        mArmorList = new ArrayList(6);
        mRand = new Random();
        mSkinColor = mFaceType = mHairColor = mHairStyle = mFacialHair = mFacialColor = 0;
    }

    public void ResetModel()
    {
        mMaterials = null;
        mMeshes = null;
        mVertices = null;
        mIndices = null;
        mSpecialTextures = new ArrayList(4);
        mBakedTextures = new ArrayList(10);
        mBones = null;
        mBoneLookup = null;
        mSlotBones = null;
        mSkinColors = null;
        mFacialHairs = null;
        mHairStyles = null;
        mTexAnimations = null;
        mOrigVertices = null;
        mTime = mStartTime = 0;
        mNumFaceTypes = mNumFacialHairColors = mNumHairColors = 0;
        mNewArmor = false;
        mNewBase = false;
        mLoaded = false;
        mBoundsMin = mBoundsMax = mBoundsCenter = mSize = null;
        mOpacity = 1.0F;
        mBaseTexture = mBraTexture = mPantiesTexture = null;
        mBakedNpcTexture = null;
        mLoadedTextures = new ArrayList(3);
        geosets = new int[20];
        for(int i = 0; i < 20; i++)
            geosets[i] = 1;

        geosets[7] = 2;
    }

    public void calcBounds()
    {
        mBoundsMin = new Point3f(1000F, 1000F, 1000F);
        mBoundsMax = new Point3f(-1000F, -1000F, -1000F);
        if(mVertices != null && mMeshes != null)
        {
            for(int i = 0; i < mMeshes.length; i++)
            {
                Mesh mesh = mMeshes[i];
                if(mesh.show)
                {
                    Point4f color = new Point4f(1.0F, 1.0F, 1.0F, mOpacity);
                    Point4f emissive = new Point4f(0.0F, 0.0F, 0.0F, 0.0F);
                    if(mesh.colorIndex != -1 && mColors[mesh.colorIndex].rgb[0].used)
                    {
                        Point3f c = mColors[mesh.colorIndex].rgb[0].getValue(mTime);
                        if(mColors[mesh.colorIndex].alpha.length > mCurrentAnim && mColors[mesh.colorIndex].alpha[mCurrentAnim].used)
                            color.w = (float)mColors[mesh.colorIndex].alpha[mCurrentAnim].getValue(mCurrentAnim) / 32767F;
                        if(mesh.unlit)
                            color.set(c.x, c.y, c.z, color.w);
                        else
                            color.set(0.0F, 0.0F, 0.0F, color.w);
                        emissive.set(c.x, c.y, c.z, color.w);
                    } else
                    if(VersionNumber < 1000)
                        color.set(mesh.color.x, mesh.color.y, mesh.color.z, mOpacity);
                    else
                        color.set(1.0F, 1.0F, 1.0F, mOpacity);
                    if(mesh.opacityIndex != -1 && mTransparency[mesh.opacityIndex].alpha[0].used)
                        color.w *= mTransparency[mesh.opacityIndex].alpha[0].getValue(mTime);
                    if(color.w > 0.0F && (mesh.colorIndex == -1 || emissive.w > 0.0F))
                    {
                        int c = mMeshes[i].mIndexCount;
                        int s = mMeshes[i].mIndexStart;
                        for(int j = 0; j < c; j++)
                        {
                            Point3f v = mVertices[mIndices[j + s]].mPosition;
                            mBoundsMin.x = Math.min(v.x, mBoundsMin.x);
                            mBoundsMin.y = Math.min(v.y, mBoundsMin.y);
                            mBoundsMin.z = Math.min(v.z, mBoundsMin.z);
                            mBoundsMax.x = Math.max(v.x, mBoundsMax.x);
                            mBoundsMax.y = Math.max(v.y, mBoundsMax.y);
                            mBoundsMax.z = Math.max(v.z, mBoundsMax.z);
                        }

                    }
                }
            }

            mSize = new Point3f(mBoundsMax.x - mBoundsMin.x, mBoundsMax.y - mBoundsMin.y, mBoundsMax.z - mBoundsMin.z);
        } else
        {
            mSize = new Point3f(1.0F, 1.0F, 1.0F);
        }
        mBoundsCenter = new Point3f(mSize);
        mBoundsCenter.scale(0.5F);
        mBoundsCenter.add(mBoundsMin);
        if(viewer.getModel() == this)
        {
            double ratio = (double)viewer.getCanvasWidth() / (double)viewer.getCanvasHeight();
            double tan2 = 2D * Math.tan(22.5D);
            double hNear = tan2 * 0.10000000000000001D;
            double wNear = hNear * ratio;
            double hFar = tan2 * 150D;
            double wFar = hFar * ratio;
            double hRate = (hFar - hNear) / 150D;
            double wRate = (wFar - wNear) / 150D;
            double hDist = (((double)mSize.z * 1.5D - hNear) + (double)(mSize.x / 2.0F)) / hRate;
            double wDist = (((double)Math.max(mSize.x, mSize.y) * 1.5D + (double)(mSize.x / 2.0F)) - wNear) / wRate;
            double dist = Math.max(hDist, wDist);
            viewer.getCamera().setDistance((float)dist);
        }
        boundsSet = true;
    }

    public void ClearSlot(int slot)
    {
        int uniq = uniqueSlots[slot];
        for(int i = 0; i < mArmorList.size(); i++)
        {
            Armor arm = (Armor)mArmorList.get(i);
            if(arm.uniqueSlot != uniq && arm.slot != slot)
                continue;
            mArmorList.remove(i);
            mNewArmor = true;
            PaintArmor();
            SetMeshes();
            break;
        }

    }

    public void ClearAllSlots()
    {
        mArmorList.clear();
        mNewArmor = true;
        PaintArmor();
        SetMeshes();
    }

    public void ReloadModels(int prevRace, int prevGender)
    {
        while(!mLoaded) 
            Thread.yield();
        for(int i = 0; i < mArmorList.size(); i++)
        {
            Armor arm = (Armor)mArmorList.get(i);
            if(arm.slot == 1 && (mRace != prevRace || mGender != prevGender))
            {
                String fn = (new StringBuilder("models/")).append(arm.modelFile).append("_").append(mRace).append("_").append(mGender).append(".mom").toString();
                arm.mod[0].model = new Model(viewer);
                FileLoader loader = new FileLoader(1000, arm.mod[0].model, fn.toLowerCase(), viewer);
                loader.start();
                loader = new FileLoader(1003, this, (new StringBuilder("models/")).append(arm.modelFile).append(".oma").toString().toLowerCase(), viewer);
                loader.start();
            } else
            if(arm.tex != null)
            {
                for(int j = 0; j < arm.tex.length; j++)
                    if(arm.tex[j].gender == mGender && mGender != prevGender)
                    {
                        arm.tex[j].mat = new Material(arm.tex[j].name);
                        if(arm.tex[j].slot > 0)
                        {
                            FileLoader fl = new FileLoader(2, this, arm.tex[j].mat, (new StringBuilder("textures/")).append(arm.tex[j].mat.mFilename).toString().toLowerCase(), viewer);
                            fl.start();
                        } else
                        {
                            SpecialTexture spec = new SpecialTexture(this, 2, arm.tex[j].mat);
                            addSpecialTexture(spec);
                        }
                    }

            }
        }

    }

    public void AttachModel(String modelfn, int slot)
    {
        while(!mLoaded) 
            try
            {
                Thread.sleep(10L);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        Armor armor = new Armor(this, slot, modelfn, mRace, mGender);
        addArmor(armor);
        if(slot == 1)
        {
            FileLoader loader = new FileLoader(1003, this, (new StringBuilder("models/")).append(modelfn).append(".oma").toString().toLowerCase(), viewer);
            loader.start();
        }
    }

    public void AddArmor(InputStream inStream, boolean loadTextures)
    {
        while(!mLoaded) 
            try
            {
                Thread.sleep(10L);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            int slot = Integer.parseInt(in.readLine());
            Armor armor = new Armor(this, slot);
            int numGeosets = Integer.parseInt(in.readLine());
            armor.geo = new Armor.ArmorGeoset[numGeosets];
            String line;
            for(int i = 0; i < numGeosets; i++)
            {
                armor.geo[i] = armor.new ArmorGeoset();
                line = in.readLine();
                String vals[] = line.split(" ");
                armor.geo[i].index = Integer.parseInt(vals[0]);
                armor.geo[i].value = Integer.parseInt(vals[1]);
            }

            int numTextures = Integer.parseInt(in.readLine());
            armor.tex = new Armor.ArmorTexture[numTextures];
            for(int i = 0; i < numTextures; i++)
            {
                line = in.readLine();
                String vals[] = line.split(" ", 3);
                if(vals == null || vals.length <= 0)continue;
                armor.tex[i] = armor.new ArmorTexture();
                armor.tex[i].slot = Integer.parseInt(vals[0]);
                armor.tex[i].gender = Integer.parseInt(vals[1]);
                armor.tex[i].name = vals[2];
                armor.tex[i].mat = new Material(armor.tex[i].name);
                if(loadTextures && armor.tex[i].gender == mGender && mBakedNpcTexture == null)
                {
                    if(armor.tex[i].slot > 0)
                    {
                        FileLoader fl = new FileLoader(2, this, armor.tex[i].mat, (new StringBuilder("textures/")).append(armor.tex[i].mat.mFilename).toString().toLowerCase(), viewer);
                        fl.start();
                    } else
                    {
                        SpecialTexture spec = new SpecialTexture(this, 2, armor.tex[i].mat);
                        addSpecialTexture(spec);
                    }
                } else
                if(mBakedNpcTexture != null)
                    armor.tex[i].mat.mSkip = true;
            }

            line = in.readLine();
            if(line != null)
            {
                String vals[] = line.split(" ");
                if(vals.length == 3)
                {
                    armor.geoA = Integer.parseInt(vals[0]);
                    armor.geoB = Integer.parseInt(vals[1]);
                    armor.geoC = Integer.parseInt(vals[2]);
                }
            }
            addArmor(armor);
            mNewArmor = true;
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void RefreshAppearance()
    {
        mHairStyle = viewer.getHairType();
        mHairColor = viewer.getHairColor();
        mFaceType = viewer.getFaceType();
        mSkinColor = viewer.getSkinColor();
        mFacialHair = viewer.getFacialHairType();
        mFacialColor = viewer.getFacialHairColor();
        setup();
        SetMeshes();
    }

    public void dumpTranslation()
    {
        for(int i = 0; i < mBones.length; i++)
            mBones[i].dumpTranslation();

    }

    public void dumpRotation()
    {
        for(int i = 0; i < mBones.length; i++)
            mBones[i].dumpRotation();

    }

    public void dumpScale()
    {
        for(int i = 0; i < mBones.length; i++)
            mBones[i].dumpScale();

    }

    private void SetModel(InputStream inStream)
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            String modelfn = in.readLine();
            modelfn = modelfn.replace(".mom", ".mo2");
            FileLoader fl = new FileLoader(10000, this, (new StringBuilder("models/")).append(modelfn).toString(), viewer);
            fl.start();
            while(!mLoaded) 
                try
                {
                    Thread.sleep(10L);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            int numTextures = Integer.parseInt(in.readLine());
            for(int i = 0; i < numTextures; i++)
            {
                String line = in.readLine();
                String vals[] = line.split(" ", 2);
                if(vals.length == 2)
                {
                    int mat = Integer.parseInt(vals[0]);
                    mMaterials[mat].mFilename = vals[1];
                    fl = new FileLoader(1, this, mMaterials[mat], (new StringBuilder("textures/")).append(vals[1]).toString().toLowerCase(), viewer);
                    fl.start();
                }
            }

        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void SetHelmHairVis(InputStream inStream)
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            String line = in.readLine();
            String vals[] = line.split(" ");
            int hairVis = 0;
            try
            {
                hairVis = Integer.parseInt(vals[0]);
            }
            catch(NumberFormatException ex)
            {
                hairVis = 1;
            }
            int facialHairVis = 0;
            try
            {
                facialHairVis = Integer.parseInt(vals[1]);
            }
            catch(NumberFormatException ex)
            {
                facialHairVis = 1;
            }
            for(int i = 0; i < mHairStyles.length; i++)
            {
                HairStyle style = mHairStyles[i];
                for(int j = 0; j < mMeshes.length; j++)
                {
                    Mesh mesh = mMeshes[j];
                    if(mesh.geosetId != 0 && mesh.geosetId == style.geoset)
                    {
                        mesh.show = mHairStyle == style.index && hairVis == 0;
                        if(mesh.show)
                            mCurrentHair = mesh;
                    }
                    if(mesh.geosetId == 1)
                        mesh.show = (mRace == 1 || mRace == 7 || mRace == 8) && mGender == 0 && mHairStyle == 0 || hairVis != 0;
                }

            }

            if(facialHairVis == 0)
            {
                FacialHair facialHair = mFacialHairs[mFacialHair];
                geosets[1] = facialHair.geoset1;
                geosets[2] = facialHair.geoset2;
                geosets[3] = facialHair.geoset3;
            } else
            {
                geosets[1] = geosets[2] = geosets[3] = 0;
            }
            SetMeshes();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void SetCharacter(InputStream inStream)
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            String line = in.readLine();
            String vals[] = line.split(" ");
            mRace = Integer.parseInt(vals[0]);
            mGender = Integer.parseInt(vals[1]);
            line = in.readLine();
            FileLoader fl = null;
            if(line.length() > 0)
            {
                mBakedNpcTexture = new Material(line);
                fl = new FileLoader(2, this, mBakedNpcTexture, (new StringBuilder("textures/")).append(line).toString(), viewer);
                fl.start();
            }
            line = in.readLine();
            vals = line.split(" ");
            mSkinColor = Integer.parseInt(vals[0]);
            mHairStyle = Integer.parseInt(vals[1]);
            mHairColor = Integer.parseInt(vals[2]);
            mFacialHair = Integer.parseInt(vals[3]);
            mFaceType = Integer.parseInt(vals[4]);
            line = in.readLine();
            vals = line.split(" ");
            int helmId = Integer.parseInt(vals[0]);
            int shoulderId = Integer.parseInt(vals[1]);
            int shirtId = Integer.parseInt(vals[2]);
            int chestId = Integer.parseInt(vals[3]);
            int beltId = Integer.parseInt(vals[4]);
            int pantsId = Integer.parseInt(vals[5]);
            int bootsId = Integer.parseInt(vals[6]);
            int bracersId = Integer.parseInt(vals[7]);
            int glovesId = Integer.parseInt(vals[8]);
            int tabardId = Integer.parseInt(vals[9]);
            String charModelFn = (new StringBuilder("models/char/")).append(races[mRace]).append(genders[mGender]).append(".mo2").toString().toLowerCase();
            fl = new FileLoader(10000, this, charModelFn, viewer);
            fl.start();
            if(helmId != 0)
                AttachModel((new StringBuilder("armor/1/")).append(helmId).toString(), 1);
            if(shoulderId != 0)
                AttachModel((new StringBuilder("armor/3/")).append(shoulderId).toString(), 3);
            if(shirtId != 0)
            {
                fl = new FileLoader(1001, this, (new StringBuilder("models/armor/4/")).append(shirtId).append(".sis").toString(), viewer);
                fl.start();
            }
            if(chestId != 0)
            {
                fl = new FileLoader(1001, this, (new StringBuilder("models/armor/5/")).append(chestId).append(".sis").toString(), viewer);
                fl.start();
            }
            if(beltId != 0)
            {
                fl = new FileLoader(1001, this, (new StringBuilder("models/armor/6/")).append(beltId).append(".sis").toString(), viewer);
                fl.start();
            }
            if(pantsId != 0)
            {
                fl = new FileLoader(1001, this, (new StringBuilder("models/armor/7/")).append(pantsId).append(".sis").toString(), viewer);
                fl.start();
            }
            if(bootsId != 0)
            {
                fl = new FileLoader(1001, this, (new StringBuilder("models/armor/8/")).append(bootsId).append(".sis").toString(), viewer);
                fl.start();
            }
            if(bracersId != 0)
            {
                fl = new FileLoader(1001, this, (new StringBuilder("models/armor/9/")).append(bracersId).append(".sis").toString(), viewer);
                fl.start();
            }
            if(glovesId != 0)
            {
                fl = new FileLoader(1001, this, (new StringBuilder("models/armor/10/")).append(glovesId).append(".sis").toString(), viewer);
                fl.start();
            }
            if(tabardId != 0)
            {
                fl = new FileLoader(1001, this, (new StringBuilder("models/armor/19/")).append(tabardId).append(".sis").toString(), viewer);
                fl.start();
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void PaintArmor()
    {
        int width = mBaseTexture.getWidth(null);
        int height = mBaseTexture.getHeight(null);
        TextureRenderer renderer = new TextureRenderer(width, height, true);
        Graphics2D g = renderer.createGraphics();
        g.drawImage(mBaseTexture, 0, 0, width, height, 0, 0, width, height, null);
        Collections.sort(mArmorList);
        if(mBraTexture != null)
        {
            boolean skip = false;
            for(int i = 0; i < mArmorList.size(); i++)
            {
                Armor arm = (Armor)mArmorList.get(i);
                if(arm.uniqueSlot != 5 && arm.uniqueSlot != 4 && arm.uniqueSlot != 19)
                    continue;
                skip = true;
                break;
            }

            if(!skip)
            {
                float dst[] = regions[6];
                int srcWidth = mBraTexture.getWidth(null);
                int srcHeight = mBraTexture.getHeight(null);
                g.drawImage(mBraTexture, (int)(dst[0] * (float)width), (int)(dst[1] * (float)height), (int)(dst[0] * (float)width + dst[2] * (float)width), (int)(dst[1] * (float)height + dst[3] * (float)height), 0, 0, srcWidth, srcHeight, null);
            }
        }
        if(mPantiesTexture != null)
        {
            boolean skip = false;
            for(int i = 0; i < mArmorList.size(); i++)
            {
                Armor arm = (Armor)mArmorList.get(i);
                if(arm.slot != 20 && arm.uniqueSlot != 7)
                    continue;
                skip = true;
                break;
            }

            if(!skip)
            {
                float dst[] = regions[8];
                int srcWidth = mPantiesTexture.getWidth(null);
                int srcHeight = mPantiesTexture.getHeight(null);
                g.drawImage(mPantiesTexture, (int)(dst[0] * (float)width), (int)(dst[1] * (float)height), (int)(dst[0] * (float)width + dst[2] * (float)width), (int)(dst[1] * (float)height + dst[3] * (float)height), 0, 0, srcWidth, srcHeight, null);
            }
        }
        for(int i = 0; i < mArmorList.size(); i++)
        {
            Armor arm = (Armor)mArmorList.get(i);
            if(arm.tex != null)
            {
                for(int j = 0; j < arm.tex.length; j++)
                    if(arm.tex[j].gender == mGender && !arm.tex[j].mat.mSkip && arm.tex[j].slot > 0 && (mRace != 6 && mRace != 8 && mRace != 11 && mRace != 13 && mRace != 14 || arm.tex[j].slot != 10))
                    {
                        float dst[] = regions[arm.tex[j].slot];
                        int srcWidth = arm.tex[j].mat.mBufferedImage.getWidth();
                        int srcHeight = arm.tex[j].mat.mBufferedImage.getHeight();
                        g.drawImage(arm.tex[j].mat.mBufferedImage, (int)(dst[0] * (float)width), (int)(dst[1] * (float)height), (int)(dst[0] * (float)width + dst[2] * (float)width), (int)(dst[1] * (float)height + dst[3] * (float)height), 0, 0, srcWidth, srcHeight, null);
                    }

            }
        }

        g.dispose();
        for(int i = 0; i < mSpecialTextures.size(); i++)
        {
            SpecialTexture text = (SpecialTexture)mSpecialTextures.get(i);
            if(text.mId != 1)
                continue;
            if(text.mMaterial != null)
            {
                text.mMaterial.mBufferedImage = (BufferedImage)renderer.getImage();
                mLoadedTextures.add(text.mMaterial);
            }
            break;
        }

    }

    private void PaintBase()
    {
        Material baseTex = null;
        for(int i = 0; i < mSpecialTextures.size(); i++)
        {
            SpecialTexture text = (SpecialTexture)mSpecialTextures.get(i);
            if(text.mId != 1)
                continue;
            if(text.mMaterial != null && text.mMaterial.mBufferedImage != null)
                baseTex = text.mMaterial;
            else
                return;
            break;
        }

        int width = baseTex.mBufferedImage.getWidth();
        int height = baseTex.mBufferedImage.getHeight();
        TextureRenderer renderer = new TextureRenderer(width, height, true);
        Graphics2D g = renderer.createGraphics();
        g.drawImage(baseTex.mBufferedImage, 0, 0, width, height, 0, 0, width, height, null);
        for(int i = 0; i < mBakedTextures.size(); i++)
        {
            BakedTexture t = (BakedTexture)mBakedTextures.get(i);
            if(t.mMaterial.mBufferedImage != null)
                if(t.mRegion == 6)
                    mBraTexture = t.mMaterial.mBufferedImage;
                else
                if(t.mRegion == 8)
                {
                    mPantiesTexture = t.mMaterial.mBufferedImage;
                } else
                {
                    float dst[] = regions[t.mRegion];
                    int srcWidth = t.mMaterial.mBufferedImage.getWidth();
                    int srcHeight = t.mMaterial.mBufferedImage.getHeight();
                    g.drawImage(t.mMaterial.mBufferedImage, (int)(dst[0] * (float)width), (int)(dst[1] * (float)height), (int)(dst[0] * (float)width + dst[2] * (float)width), (int)(dst[1] * (float)height + dst[3] * (float)height), 0, 0, srcWidth, srcHeight, null);
                }
        }

        g.dispose();
        mBaseTexture = renderer.getImage();
        mNewBase = true;
    }

    void SetMeshes()
    {
        if(mMeshes == null)
            return;
        boolean hairStatus = true;
        if(mCurrentHair != null)
            hairStatus = mCurrentHair.show;
        for(int i = 0; i < mMeshes.length; i++)
        {
            Mesh mesh = mMeshes[i];
            mesh.show = mesh.geosetId == 0;
        }

        for(int i = 0; i < 20; i++)
            geosets[i] = 1;

        geosets[7] = 2;
        FacialHair facialHair = null;
        if(mFacialHairs != null && mFacialHair < mFacialHairs.length)
            facialHair = mFacialHairs[mFacialHair];
        if(facialHair != null)
        {
            geosets[1] = facialHair.geoset1;
            geosets[2] = facialHair.geoset2;
            geosets[3] = facialHair.geoset3;
        }
        if(mRace == 9)
        {
            if(geosets[1] == 1)
                geosets[1]++;
            if(geosets[2] == 1)
                geosets[2]++;
            if(geosets[3] == 1)
                geosets[3]++;
        }
        boolean skipHair = false;
        for(int j = 0; j < mArmorList.size(); j++)
        {
            Armor arm = (Armor)mArmorList.get(j);
            if(arm.slot == 1 && mRace != 6)
                skipHair = true;
            if(arm.geo != null)
            {
                for(int k = 0; k < arm.geo.length; k++)
                    geosets[arm.geo[k].index] = arm.geo[k].value;

                if(geosets[13] == 1)
                    geosets[13] = 1 + arm.geoC;
                if(arm.slot == 6)
                    geosets[18] = 1 + arm.geoA;
            }
        }

        if(geosets[13] == 2)
        {
            geosets[5] = 0;
            geosets[12] = 0;
        }
        if(geosets[4] > 1)
            geosets[8] = 0;
        if(!skipHair && mHairStyles != null)
        {
            for(int i = 0; i < mHairStyles.length; i++)
            {
                HairStyle style = mHairStyles[i];
                for(int j = 0; j < mMeshes.length; j++)
                {
                    Mesh mesh = mMeshes[j];
                    if(mesh.geosetId != 0 && mesh.geosetId == style.geoset)
                        mesh.show = mHairStyle == style.index;
                }

            }

        }
        for(int i = 0; i < mMeshes.length; i++)
        {
            Mesh m = mMeshes[i];
            if(m.geosetId == 1 && mGender == 0 && mHairStyle == 0 && (mRace == 1 || mRace == 7 || mRace == 8 || mRace == 18))
                m.show = true;
            for(int j = 1; j < 20; j++)
            {
                int a = j * 100;
                int b = (j + 1) * 100;
                if(m.geosetId > a && m.geosetId < b)
                    m.show = m.geosetId == a + geosets[j];
            }

            if(mRace == 9)
            {
                if(mGender == 1 && m.geoset == 0)
                    m.show = false;
                else
                if(mGender == 0 && m.geoset == 3)
                    m.show = false;
            } else
            if(mRace == 22)
                if(mGender == 0)
                {
                    if(m.geoset == 2 || m.geoset == 3 || m.geoset >= 36 && m.geoset <= 47)
                        m.show = false;
                } else
                if(m.geoset == 2 || m.geoset == 3 || m.geoset >= 58 && m.geoset <= 69)
                    m.show = false;
        }

        for(int a = 0; a < mArmorList.size(); a++)
        {
            Armor arm = (Armor)mArmorList.get(a);
            if(arm.mod != null)
            {
                for(int m = 0; m < arm.mod.length; m++)
                {
                    int found = 0;
                    for(int i = 0; i < mSlotBones.length; i++)
                    {
                        SlotBone sb = mSlotBones[i];
                        if(sb.slot == arm.slot && found == m)
                        {
                            arm.mod[m].bone = sb.bone;
                            break;
                        }
                        if(sb.slot == arm.slot)
                            found++;
                    }

                }

            }
        }

        if(mCurrentHair != null)
            mCurrentHair.show = hairStatus;
    }

    public void SetAnimation(String anim)
    {
        SetAnimation(anim, false);
    }

    public void SetAnimation(String anim, boolean isRecursive)
    {
        if(VersionNumber < 1000 && (anim.equals("Stand") || anim.equals("idle")))
        {
            mStartTime = 0;
            mDoingActiveAnim = false;
            mCurrentAnims = new ArrayList();
            for(int i = 0; i < mLoopAnims.length; i++)
                mCurrentAnims.add(mLoopAnims[i]);

            mCurrentAnimIndex = 0;
            return;
        }
        mCurrentAnims = new ArrayList();
        for(int i = 0; i < mAnimations.length; i++)
            if(anim == "Stand")
            {
                if(mAnimations[i].name.equals(anim))
                    mCurrentAnims.add(mAnimations[i]);
            } else
            if(mAnimations[i].name.startsWith(anim))
                mCurrentAnims.add(mAnimations[i]);

        mStartTime = 0;
        mCurrentAnimIndex = 0;
        mCurrentAnim = mCurrentAnims.size() <= 0 ? 0 : ((Animation)mCurrentAnims.get(0)).index;
        if(!isRecursive && mCurrentAnims.size() == 0)
            SetAnimation("Stand", true);
    }

    private void updateAnimation(GL gl)
    {
        if(!mFreeze && mCurrentAnims != null && mCurrentAnims.size() > 0)
        {
            if(mStartTime == 0)
                mTime = mStartTime = viewer.getGlobalTime();
            else
                mTime = viewer.getGlobalTime();
            if(mTime - mStartTime >= ((Animation)mCurrentAnims.get(mCurrentAnimIndex)).duration)
            {
                mCurrentAnimIndex = mRand.nextInt(mCurrentAnims.size());
                mCurrentAnim = mCurrentAnims.size() <= 0 ? 0 : ((Animation)mCurrentAnims.get(mCurrentAnimIndex)).index;
                mStartTime = mTime;
            }
        }
        if(mBones.length > 0 && mAnimations != null)
        {
            for(int i = 0; i < mBones.length; i++)
                mBones[i].calc = false;

            for(int i = 0; i < mBones.length; i++)
                mBones[i].calcMatrix(gl, mTime - mStartTime);

            if(mVertices != null)
            {
                Point3f p = new Point3f();
                Vector3f n = new Vector3f();
                for(int i = 0; i < mVertices.length; i++)
                {
                    Vertex v = mVertices[i];
                    Vertex ov = mOrigVertices[i];
                    v.mPosition.set(0.0F, 0.0F, 0.0F);
                    v.mNormal.set(0.0F, 0.0F, 0.0F);
                    for(int j = 0; j < 4; j++)
                        if(ov.weights[j] > 0)
                        {
                            p.set(ov.mPosition);
                            n.set(ov.mNormal);
                            mBones[ov.bones[j]].matrix.transform(p);
                            mBones[ov.bones[j]].rotMatrix.transform(n);
                            v.mPosition.scaleAdd((float)ov.weights[j] / 255F, p, v.mPosition);
                            v.mNormal.scaleAdd((float)ov.weights[j] / 255F, n, v.mNormal);
                        }

                }

            }
        }
        if(mParticleEmitters != null && mParticleEmitters.length > 0)
        {
            for(int i = 0; i < mParticleEmitters.length; i++)
                mParticleEmitters[i].update(mCurrentAnim, mTime);

        }
        if(mRibbonEmitters != null && mRibbonEmitters.length > 0)
        {
            for(int i = 0; i < mRibbonEmitters.length; i++)
                mRibbonEmitters[i].update(mCurrentAnim, mTime);

        }
    }

    private void createdBakedNpcTexture(GL gl)
    {
        boolean found = false;
        for(int i = 0; i < mSpecialTextures.size(); i++)
        {
            SpecialTexture spec = (SpecialTexture)mSpecialTextures.get(i);
            if(spec.mId != 1)
                continue;
            spec.mMaterial = mBakedNpcTexture;
            spec.mTextureName = mBakedNpcTexture.mFilename;
            found = true;
            break;
        }

        if(!found)
        {
            SpecialTexture spec = new SpecialTexture(this, 1, mBakedNpcTexture);
            addSpecialTexture(spec);
        }
        mBaseTexture = mBakedNpcTexture.mBufferedImage;
        try
        {
            mBakedNpcTexture.mTextureId = ModelTexture.getTexture(gl, (BufferedImage)mBaseTexture);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        mNewArmor = true;
    }

    public void render(GL2 gl, Camera cam, boolean translate)
    {
        for(int i = 0; i < mLoadedTextures.size(); i++)
        {
            Material m = (Material)mLoadedTextures.get(i);
            try
            {
                if(m != null && m.mBufferedImage != null)
                    m.mTextureId = ModelTexture.getTexture(gl, m.mBufferedImage);
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }

        mLoadedTextures.clear();
        if(!mLoaded)
            return;
        if(VersionNumber >= 500 && mCurrentAnims != null && mCurrentAnims.size() > 0)
            updateAnimation(gl);
        Mesh mesh = null;
        Material mat = null;
        int tex = 0;
        if(mSize == null)
            calcBounds();
        gl.glPushMatrix();
        if(translate)
            gl.glTranslatef(-mBoundsCenter.x, -mBoundsCenter.y, -mBoundsCenter.z);
        gl.glDisable(3042);
        gl.glDisable(3008);
        gl.glColor4f(1.0F, 1.0F, 1.0F, mOpacity);
        if(mBakedNpcTexture != null && mBakedNpcTexture.mBufferedImage != null && mBaseTexture == null)
            createdBakedNpcTexture(gl);
        else
        if(mBakedTextures.size() > 0 && mBaseTexture == null)
        {
            boolean readyToComp = true;
            for(int i = 0; i < mBakedTextures.size(); i++)
            {
                BakedTexture text = (BakedTexture)mBakedTextures.get(i);
                if((text.mMaterial == null || text.mMaterial.mBufferedImage == null) && !text.mMaterial.mSkip)
                    readyToComp = false;
            }

            if(readyToComp)
                PaintBase();
        }
        if(mNewBase && mBaseTexture != null)
        {
            boolean textureReady = true;
            Material baseTex = null;
            for(int i = 0; i < mSpecialTextures.size(); i++)
            {
                SpecialTexture text = (SpecialTexture)mSpecialTextures.get(i);
                if(text.mId != 1)
                    continue;
                if(text.mMaterial != null && text.mMaterial.mBufferedImage != null)
                    baseTex = text.mMaterial;
                else
                    textureReady = false;
                break;
            }

            if(textureReady)
            {
                try
                {
                    baseTex.mTextureId = ModelTexture.getTexture(gl, (BufferedImage)mBaseTexture);
                }
                catch(IOException ex)
                {
                    ex.printStackTrace();
                }
                mNewBase = false;
            }
            mNewArmor = true;
        }
        if(mNewArmor && mBaseTexture != null)
        {
            boolean readyToComp = true;
            for(int i = 0; i < mArmorList.size(); i++)
            {
                Armor arm = (Armor)mArmorList.get(i);
                if(arm.tex != null)
                {
                    for(int j = 0; j < arm.tex.length; j++)
                        if(arm.tex[j].gender == mGender)
                            if(arm.tex[j].mat == null)
                                readyToComp = false;
                            else
                            if(arm.tex[j].mat.mBufferedImage == null && arm.tex[j].mat.mTextureId == 0 && !arm.tex[j].mat.mSkip)
                                readyToComp = false;

                }
            }

            if(readyToComp)
            {
                PaintArmor();
                SetMeshes();
                mNewArmor = false;
            }
        }
        if(mMeshes != null)
        {
            for(int i = 0; i < mMeshes.length; i++)
                if(viewer.getMesh() % mMeshes.length == i || viewer.getMesh() == 0)
                {
                    mesh = mMeshes[i];
                    if(mesh.show)
                    {
                        Point4f color = new Point4f(1.0F, 1.0F, 1.0F, mOpacity);
                        Point4f emissive = new Point4f(0.0F, 0.0F, 0.0F, 0.0F);
                        if(mesh.colorIndex != -1 && mColors[mesh.colorIndex].rgb[0].used)
                        {
                            Point3f c = mColors[mesh.colorIndex].rgb[0].getValue(mTime);
                            if(mColors[mesh.colorIndex].alpha.length > mCurrentAnim && mColors[mesh.colorIndex].alpha[mCurrentAnim].used)
                                color.w = (float)mColors[mesh.colorIndex].alpha[mCurrentAnim].getValue(mCurrentAnim) / 32767F;
                            if(mesh.unlit)
                                color.set(c.x, c.y, c.z, color.w);
                            else
                                color.set(0.0F, 0.0F, 0.0F, color.w);
                            emissive.set(c.x, c.y, c.z, color.w);
                        } else
                        if(VersionNumber < 1000)
                            color.set(mesh.color.x, mesh.color.y, mesh.color.z, mOpacity);
                        else
                            color.set(1.0F, 1.0F, 1.0F, mOpacity);
                        if(mesh.opacityIndex != -1 && mTransparency[mesh.opacityIndex].alpha[0].used)
                            color.w *= mTransparency[mesh.opacityIndex].alpha[0].getValue(mTime);
                        if(color.w > 0.0F && (mesh.colorIndex == -1 || emissive.w > 0.0F))
                        {
                            gl.glColor4f(color.x, color.y, color.z, color.w);
                            switch(mesh.blendmode)
                            {
                            case 0: // '\0'
                                gl.glDisable(3042);
                                gl.glDisable(3008);
                                break;

                            case 1: // '\001'
                                gl.glDisable(3042);
                                gl.glEnable(3008);
                                gl.glAlphaFunc(518, 0.7F);
                                break;

                            case 2: // '\002'
                                gl.glEnable(3042);
                                gl.glBlendFunc(1, 771);
                                break;

                            case 3: // '\003'
                                gl.glEnable(3042);
                                gl.glBlendFunc(768, 1);
                                break;

                            case 4: // '\004'
                                gl.glEnable(3042);
                                gl.glBlendFunc(770, 1);
                                break;

                            case 5: // '\005'
                                gl.glEnable(3042);
                                gl.glBlendFunc(774, 768);
                                break;

                            case 6: // '\006'
                                gl.glEnable(3042);
                                gl.glBlendFunc(774, 768);
                                break;

                            default:
                                gl.glEnable(3042);
                                gl.glBlendFunc(1, 771);
                                break;
                            }
                            if(mesh.cull)
                                gl.glEnable(2884);
                            else
                                gl.glDisable(2884);
                            if(mesh.swrap)
                                gl.glTexParameteri(3553, 10242, 10497);
                            if(mesh.twrap)
                                gl.glTexParameteri(3553, 10243, 10497);
                            if(mesh.noZWrite)
                                gl.glDepthMask(false);
                            else
                                gl.glDepthMask(true);
                            if(mesh.envmap)
                            {
                                gl.glMaterialf(1032, 5633, 18F);
                                gl.glEnable(3168);
                                gl.glEnable(3169);
                                int maptype = 9218;
                                gl.glTexGeni(8192, 9472, maptype);
                                gl.glTexGeni(8193, 9472, maptype);
                            }
                            if(mesh.unlit)
                                gl.glDisable(2896);
                            if(mesh.blendmode <= 1 && color.w < 1.0F)
                                gl.glEnable(3042);
                            mat = null;
                            tex = 0;
                            if(mesh.mMaterial < mMaterials.length)
                            {
                                mat = mMaterials[mesh.mMaterial];
                                if(mat.mTextureId != 0)
                                    tex = mat.mTextureId;
                                else
                                if(mat.mSpecialTexture != -1 && mSpecialTextures.size() > 0)
                                {
                                    for(int j = 0; j < mSpecialTextures.size(); j++)
                                    {
                                        SpecialTexture specTex = (SpecialTexture)mSpecialTextures.get(j);
                                        if(mat.mSpecialTexture != specTex.mId)
                                            continue;
                                        if(specTex.mMaterial != null && specTex.mMaterial.mTextureId != 0)
                                            tex = specTex.mMaterial.mTextureId;
                                        break;
                                    }

                                }
                            }
                            if(tex != 0)
                            {
                                gl.glEnable(3553);
                                gl.glBindTexture(3553, tex);
                            }
                            if(mTexAnimations != null && mTexAnimations.length > 0 && mesh.texAnim > -1)
                            {
                                TexAnimation ta = mTexAnimations[mesh.texAnim];
                                gl.glMatrixMode(5890);
                                gl.glPushMatrix();
                                if(ta.trans != null)
                                {
                                    if(ta.trans.used)
                                    {
                                        Point3f tr = ta.trans.getValue(mTime);
                                        gl.glTranslatef(tr.x, tr.y, tr.z);
                                    }
                                    if(ta.rot.used)
                                    {
                                        AxisAngle4f rot = new AxisAngle4f();
                                        rot.set(ta.rot.getValue(mTime));
                                        gl.glRotatef(rot.angle, rot.x, rot.y, rot.z);
                                    }
                                    if(ta.scale.used)
                                    {
                                        Point3f sc = ta.scale.getValue(mTime);
                                        gl.glScalef(sc.x, sc.y, sc.z);
                                    }
                                } else
                                if(ta.transMo2 != null)
                                {
                                    int thisAnim = 0;
                                    if(mCurrentAnim >= ta.transMo2.length)
                                        thisAnim = 0;
                                    else
                                        thisAnim = mCurrentAnim;
                                    if(ta.transMo2.length > 0 && ta.transMo2[thisAnim].used)
                                    {
                                        Point3f tr = ta.transMo2[thisAnim].getValue(mTime);
                                        gl.glTranslatef(tr.x, tr.y, tr.z);
                                    }
                                    if(mCurrentAnim >= ta.rotMo2.length)
                                        thisAnim = 0;
                                    else
                                        thisAnim = mCurrentAnim;
                                    if(ta.rotMo2.length > 0 && ta.rotMo2[thisAnim].used)
                                    {
                                        AxisAngle4f rot = new AxisAngle4f();
                                        rot.set(ta.rotMo2[thisAnim].getValue(mTime));
                                        gl.glRotatef(rot.angle, rot.x, rot.y, rot.z);
                                    }
                                    if(mCurrentAnim >= ta.scaleMo2.length)
                                        thisAnim = 0;
                                    else
                                        thisAnim = mCurrentAnim;
                                    if(ta.scaleMo2.length > 0 && ta.scaleMo2[thisAnim].used)
                                    {
                                        Point3f sc = ta.scaleMo2[thisAnim].getValue(mTime);
                                        gl.glScalef(sc.x, sc.y, sc.z);
                                    }
                                }
                                gl.glMatrixMode(5888);
                            }
                            char start = mesh.mIndexStart;
                            char count = mesh.mIndexCount;
                            gl.glBegin(4);
                            int j = start;
                            for(int k = 0; k < count;)
                            {
                                Vertex v = mVertices[mIndices[j]];
                                gl.glTexCoord2f(v.mTexCoord.x, 1.0F - v.mTexCoord.y);
                                gl.glNormal3f(v.mNormal.x, v.mNormal.y, v.mNormal.z);
                                gl.glVertex3f(v.mPosition.x, v.mPosition.y, v.mPosition.z);
                                k++;
                                j++;
                            }

                            gl.glEnd();
                            if(mTexAnimations != null && mTexAnimations.length > 0 && mesh.texAnim > -1)
                            {
                                gl.glMatrixMode(5890);
                                gl.glPopMatrix();
                                gl.glMatrixMode(5888);
                            }
                            if(tex != 0)
                                gl.glDisable(3553);
                            switch(mesh.blendmode)
                            {
                            case 1: // '\001'
                                gl.glDisable(3008);
                                break;

                            case 2: // '\002'
                            case 3: // '\003'
                            case 4: // '\004'
                            case 5: // '\005'
                            case 6: // '\006'
                                gl.glDisable(3042);
                                break;

                            default:
                                gl.glDisable(3042);
                                gl.glBlendFunc(1, 771);
                                break;

                            case 0: // '\0'
                                break;
                            }
                            if(mesh.noZWrite)
                                gl.glDepthMask(true);
                            if(mesh.unlit)
                                gl.glEnable(2896);
                            if(mesh.cull)
                                gl.glDisable(2884);
                            if(mesh.envmap)
                            {
                                gl.glDisable(3168);
                                gl.glDisable(3169);
                            }
                            if(mesh.swrap)
                                gl.glTexParameteri(3553, 10242, 10496);
                            if(mesh.twrap)
                                gl.glTexParameteri(3553, 10243, 10496);
                        }
                    }
                }

        }
        gl.glBlendFunc(1, 771);
        gl.glEnable(3042);
        gl.glEnable(3008);
        for(int i = 0; i < mArmorList.size(); i++)
        {
            Armor arm = (Armor)mArmorList.get(i);
            if(arm.mod != null)
            {
                for(int j = 0; j < arm.mod.length; j++)
                    if(arm.mod[j].model != null && arm.mod[j].bone >= 0 && mBones.length > arm.mod[j].bone)
                    {
                        Bone attBone = mBones[arm.mod[j].bone];
                        gl.glPushMatrix();
                        if(attBone.matrix != null)
                        {
                            float boneMat[] = new float[16];
                            boneMat[0] = attBone.matrix.m00;
                            boneMat[1] = attBone.matrix.m10;
                            boneMat[2] = attBone.matrix.m20;
                            boneMat[3] = attBone.matrix.m30;
                            boneMat[4] = attBone.matrix.m01;
                            boneMat[5] = attBone.matrix.m11;
                            boneMat[6] = attBone.matrix.m21;
                            boneMat[7] = attBone.matrix.m31;
                            boneMat[8] = attBone.matrix.m02;
                            boneMat[9] = attBone.matrix.m12;
                            boneMat[10] = attBone.matrix.m22;
                            boneMat[11] = attBone.matrix.m32;
                            boneMat[12] = attBone.matrix.m03;
                            boneMat[13] = attBone.matrix.m13;
                            boneMat[14] = attBone.matrix.m23;
                            boneMat[15] = attBone.matrix.m33;
                            gl.glMultMatrixf(boneMat, 0);
                        } else
                        {
                            gl.glMultMatrixf(attBone.mat, 0);
                        }
                        SlotBone sb = null;
                        int found = 0;
                        for(int k = 0; k < mSlotBones.length; k++)
                        {
                            sb = mSlotBones[k];
                            if(sb.slot == arm.slot && found == j)
                                break;
                            if(sb.slot == arm.slot)
                                found++;
                            sb = null;
                        }

                        if(sb != null)
                            gl.glTranslatef(sb.pos.x, sb.pos.y, sb.pos.z);
                        arm.mod[j].model.render(gl, cam, false);
                        gl.glPopMatrix();
                    }

            }
        }

        gl.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(mRibbonEmitters != null && mRibbonEmitters.length > 0)
        {
            gl.glEnable(3553);
            for(int i = 0; i < mRibbonEmitters.length; i++)
                mRibbonEmitters[i].draw(gl);

            gl.glDisable(3553);
        }
        if(mParticleEmitters != null && mParticleEmitters.length > 0)
        {
            gl.glDisable(2896);
            gl.glDepthMask(false);
            gl.glEnable(3553);
            for(int i = 0; i < mParticleEmitters.length; i++)
                mParticleEmitters[i].draw(gl);

            gl.glDisable(3553);
            gl.glDepthMask(true);
            gl.glEnable(2896);
        }
        gl.glPopMatrix();
    }

    private int hasSpecialTexture(int id, String texture)
    {
        for(int i = 0; i < mSpecialTextures.size(); i++)
        {
            SpecialTexture st = (SpecialTexture)mSpecialTextures.get(i);
            if(st.mId == id && st.mTextureName == texture)
                return i;
        }

        return -1;
    }

    private int hasBakedTexture(int region, int layer, String texture)
    {
        for(int i = 0; i < mBakedTextures.size(); i++)
        {
            BakedTexture bt = (BakedTexture)mBakedTextures.get(i);
            if(bt.mRegion == region && bt.mLayer == layer && bt.mTextureName == texture)
                return i;
        }

        return -1;
    }

    private void addArmor(Armor armor)
    {
        for(int i = 0; i < mArmorList.size(); i++)
        {
            Armor arm = (Armor)mArmorList.get(i);
            if(arm.uniqueSlot == armor.uniqueSlot)
            {
                mArmorList.set(i, armor);
                return;
            }
        }

        mArmorList.add(armor);
    }

    private void addSpecialTexture(SpecialTexture tex)
    {
        mBaseTexture = null;
        for(int i = 0; i < mSpecialTextures.size(); i++)
        {
            SpecialTexture st = (SpecialTexture)mSpecialTextures.get(i);
            if(st.mId == tex.mId)
            {
                mSpecialTextures.set(i, tex);
                return;
            }
        }

        mSpecialTextures.add(tex);
    }

    private void addBakedTexture(int region, int layer, BakedTexture tex)
    {
        mBaseTexture = null;
        for(int i = 0; i < mBakedTextures.size(); i++)
        {
            BakedTexture bt = (BakedTexture)mBakedTextures.get(i);
            if(bt.mRegion == region && bt.mLayer == layer)
            {
                mBakedTextures.set(i, tex);
                return;
            }
        }

        mBakedTextures.add(tex);
    }

    public void setup()
    {
        if(mRace == 0)
        {
            if(mMeshes != null)
            {
                for(int i = 0; i < mMeshes.length; i++)
                {
                    Mesh mesh = mMeshes[i];
                    mesh.show = true;
                }

            }
            if(mPath.equals("models/creature/arthaslichking/arthaslichking_unarmed.mom"))
            {
                for(int i = 0; i < mMeshes.length; i++)
                    if(mMeshes[i].geoset == 2 || mMeshes[i].geoset == 9 || mMeshes[i].geoset == 10)
                        mMeshes[i].show = false;

            } else
            if(mPath.equals("models/creature/dragonsinestra/dragonsinestra.mom"))
            {
                for(int i = 0; i < mMeshes.length; i++)
                    if(mMeshes[i].geoset > 3)
                        mMeshes[i].show = false;

            }
            return;
        }
        if((mRace == 14 || mRace == 17) && mHairStyle == 0)
            mHairStyle = 1;
        SkinColor skin = null;
        SkinColor.FaceTexture face = null;
        FacialHair facialHair = null;
        FacialHair.FacialHairrTexture facialColor = null;
        HairStyle hair = null;
        HairStyle.HairTexture hairColor = null;
        if(mSkinColors != null)
        {
            if(mSkinColor >= mSkinColors.length)
                mSkinColor = 0;
            if(mSkinColor < mSkinColors.length)
            {
                skin = mSkinColors[mSkinColor];
                if(mFaceType >= skin.face.length)
                    mFaceType = 0;
                if(skin.face.length > 0)
                    face = skin.face[mFaceType];
            }
        }
        if(mFacialHairs != null)
        {
            if(mFacialHair >= mFacialHairs.length)
                mFacialHair = 0;
            if(mFacialHair < mFacialHairs.length)
            {
                facialHair = mFacialHairs[mFacialHair];
                if(mFacialColor >= facialHair.hair.length)
                    mFacialColor = 0;
                if(facialHair.hair.length > 0)
                    facialColor = facialHair.hair[mFacialColor];
            }
        }
        if(mHairStyles != null)
        {
            if(mHairStyle >= mHairStyles.length)
                mHairStyle = 0;
            if(mHairStyle < mHairStyles.length)
            {
                hair = mHairStyles[mHairStyle];
                if(mHairColor >= hair.hair.length)
                    mHairColor = 0;
                if(hair.hair.length > 0)
                    hairColor = hair.hair[mHairColor];
            }
        }
        int idx = 0;
        if(mBakedNpcTexture == null)
        {
            SpecialTexture tex = null;
            if(skin != null && skin.baseTexture.length() > 0)
            {
                idx = hasSpecialTexture(1, skin.baseTexture);
                if(idx < 0)
                {
                    tex = new SpecialTexture(this, 1, skin.baseTexture);
                    addSpecialTexture(tex);
                }
            }
            BakedTexture baked = null;
            if(skin != null && skin.pantiesTexture.length() > 0)
            {
                idx = hasBakedTexture(8, 1, skin.pantiesTexture);
                if(idx < 0)
                {
                    baked = new BakedTexture(this, 8, 1, skin.pantiesTexture);
                    addBakedTexture(8, 1, baked);
                }
            }
            if(skin != null && skin.braTexture.length() > 0)
            {
                idx = hasBakedTexture(6, 1, skin.braTexture);
                if(idx < 0)
                {
                    baked = new BakedTexture(this, 6, 1, skin.braTexture);
                    addBakedTexture(6, 1, baked);
                }
            }
            if(face != null && face.lowerTexture.length() > 0)
            {
                idx = hasBakedTexture(5, 1, face.lowerTexture);
                if(idx < 0)
                {
                    baked = new BakedTexture(this, 5, 1, face.lowerTexture);
                    addBakedTexture(5, 1, baked);
                }
            }
            if(face != null && face.upperTexture.length() > 0)
            {
                idx = hasBakedTexture(4, 1, face.upperTexture);
                if(idx < 0)
                {
                    baked = new BakedTexture(this, 4, 1, face.upperTexture);
                    addBakedTexture(4, 1, baked);
                }
            }
            if(facialColor != null && facialColor.lowerTexture.length() > 0)
            {
                idx = hasBakedTexture(5, 2, facialColor.lowerTexture);
                if(idx < 0)
                {
                    baked = new BakedTexture(this, 5, 2, facialColor.lowerTexture);
                    addBakedTexture(5, 2, baked);
                }
            }
            if(facialColor != null && facialColor.upperTexture.length() > 0)
            {
                idx = hasBakedTexture(4, 2, facialColor.upperTexture);
                if(idx < 0)
                {
                    baked = new BakedTexture(this, 4, 2, facialColor.upperTexture);
                    addBakedTexture(4, 2, baked);
                }
            }
            if(hairColor != null && hairColor.lowerTexture.length() > 0)
            {
                idx = hasBakedTexture(5, 3, hairColor.lowerTexture);
                if(idx < 0)
                {
                    baked = new BakedTexture(this, 5, 3, hairColor.lowerTexture);
                    addBakedTexture(5, 3, baked);
                }
            }
            if(hairColor != null && hairColor.upperTexture.length() > 0)
            {
                idx = hasBakedTexture(4, 3, hairColor.upperTexture);
                if(idx < 0)
                {
                    baked = new BakedTexture(this, 4, 3, hairColor.upperTexture);
                    addBakedTexture(4, 3, baked);
                }
            }
        }
        if(skin != null && skin.furTexture.length() > 0)
        {
            idx = hasSpecialTexture(8, skin.furTexture);
            if(idx < 0)
            {
                SpecialTexture tex = new SpecialTexture(this, 8, skin.furTexture);
                addSpecialTexture(tex);
            }
        }
        if(hairColor != null && hairColor.texture.length() > 0)
        {
            idx = hasSpecialTexture(6, hairColor.texture);
            if(idx < 0)
            {
                SpecialTexture tex = new SpecialTexture(this, 6, hairColor.texture);
                addSpecialTexture(tex);
            }
        }
        SetMeshes();
    }
    
    public Animation[] getModelAnimations(){
        return mAnimations;
    }
    
    public void setModelAnimations(Animation[] anims){
        this.mAnimations = anims;
        viewer.animationsChanged();
    }
    
    int VersionNumber;
    Material mMaterials[];
    Mesh mMeshes[];
    Vertex mVertices[];
    short mIndices[];
    ArrayList mSpecialTextures;
    ArrayList mBakedTextures;
    ArrayList mArmorList;
    Bone mBones[];
    SlotBone mSlotBones[];
    public SkinColor mSkinColors[];
    public FacialHair mFacialHairs[];
    public HairStyle mHairStyles[];
    TexAnimation mTexAnimations[];
    Mo2Color mColors[];
    Transparency mTransparency[];
    ParticleEmitter mParticleEmitters[];
    RibbonEmitter mRibbonEmitters[];
    Vertex mOrigVertices[];
    int mBoneLookup[];
    Random mRand;
    private Animation mAnimations[];
    Animation mLoopAnims[];
    Animation mActiveAnims[];
    ArrayList mCurrentAnims;
    int mCurrentAnimIndex;
    int mCurrentAnim;
    boolean mDoingActiveAnim;
    public int mNumHairColors;
    public int mNumFaceTypes;
    public int mNumFacialHairColors;
    int mSkinColor;
    int mFaceType;
    int mHairColor;
    int mHairStyle;
    int mFacialHair;
    int mFacialColor;
    int mNumLoopAnims;
    int mNumActiveAnims;
    int mNumOrigVertices;
    int mNumBoneLookup;
    int mNumAnimData;
    ArrayList mLoadedTextures;
    Point3f mBoundsMin;
    Point3f mBoundsMax;
    Point3f mBoundsCenter;
    Point3f mSize;
    boolean boundsSet;
    float mOpacity;
    int mGender;
    int mRace;
    TextureRenderer mRenderer;
    Image mBaseTexture;
    Image mBraTexture;
    Image mPantiesTexture;
    Material mBakedNpcTexture;
    Mesh mCurrentHair;
    boolean mNewArmor;
    boolean mNewBase;
    public boolean mLoaded;
    public boolean mFreeze;
    int geosets[];
    String mPath;
    int mTime;
    int mStartTime;
    public static final String genders[] = {
        "Male", "Female"
    };
    public static final String races[] = {
        "", "Human", "Orc", "Dwarf", "NightElf", "Scourge", "Tauren", "Gnome", "Troll", "Goblin", 
        "BloodElf", "Draenei", "FelOrc", "Naga_", "Broken", "Skeleton", "Vrykul", "Tuskarr", "ForestTroll", "Taunka", 
        "NorthrendSkeleton", "IceTroll", "Worgen"
    };
    public static final int IT_HEAD = 1;
    public static final int IT_SHOULDER = 3;
    public static final int IT_SHIRT = 4;
    public static final int IT_CHEST = 5;
    public static final int IT_BELT = 6;
    public static final int IT_PANTS = 7;
    public static final int IT_BOOTS = 8;
    public static final int IT_BRACERS = 9;
    public static final int IT_GLOVES = 10;
    public static final int IT_ONEHAND = 13;
    public static final int IT_SHIELD = 14;
    public static final int IT_BOW = 15;
    public static final int IT_CAPE = 16;
    public static final int IT_TWOHAND = 17;
    public static final int IT_TABARD = 19;
    public static final int IT_ROBE = 20;
    public static final int IT_RIGHTHAND = 21;
    public static final int IT_LEFTHAND = 22;
    public static final int IT_OFFHAND = 23;
    public static final int IT_THROWN = 25;
    public static final int IT_RANGED = 26;
    public static final int IT_MAX = 26;
    public static final int BONE_LARM = 0;
    public static final int BONE_RARM = 1;
    public static final int BONE_LSHOULDER = 2;
    public static final int BONE_RSHOULDER = 3;
    public static final int BONE_STOMACH = 4;
    public static final int BONE_WAIST = 5;
    public static final int BONE_HEAD = 6;
    public static final int BONE_JAW = 7;
    public static final int BONE_RFINGER1 = 8;
    public static final int BONE_RFINGER2 = 9;
    public static final int BONE_RFINGER3 = 10;
    public static final int BONE_RFINGERS = 11;
    public static final int BONE_RTHUMB = 12;
    public static final int BONE_LFINGER1 = 13;
    public static final int BONE_LFINGER2 = 14;
    public static final int BONE_LFINGER3 = 15;
    public static final int BONE_LFINGERS = 16;
    public static final int BONE_LTHUMB = 17;
    public static final int BONE_ROOT = 26;
    public static final int uniqueSlots[] = {
        0, 1, 0, 3, 4, 5, 6, 7, 8, 9, 
        10, 0, 0, 21, 22, 22, 16, 21, 0, 19, 
        5, 21, 22, 22, 0, 21, 21
    };
    public static final int slotSort[] = {
        0, 16, 0, 15, 1, 7, 9, 5, 6, 10, 
        11, 0, 0, 17, 18, 19, 14, 20, 0, 8, 
        7, 21, 22, 23, 0, 24, 25
    };
    public static final int CR_BASE = 0;
    public static final int CR_ARM_UPPER = 1;
    public static final int CR_ARM_LOWER = 2;
    public static final int CR_HAND = 3;
    public static final int CR_FACE_UPPER = 4;
    public static final int CR_FACE_LOWER = 5;
    public static final int CR_TORSO_UPPER = 6;
    public static final int CR_TORSO_LOWER = 7;
    public static final int CR_PELVIS_UPPER = 8;
    public static final int CR_PELVIS_LOWER = 9;
    public static final int CR_FOOT = 10;
    public static final int CR_LEG_UPPER = 8;
    public static final int CR_LEG_LOWER = 9;
    public static final float regions[][] = {
        {
            0.0F, 0.0F, 1.0F, 1.0F
        }, {
            0.0F, 0.75F, 0.5F, 0.25F
        }, {
            0.0F, 0.5F, 0.5F, 0.25F
        }, {
            0.0F, 0.375F, 0.5F, 0.125F
        }, {
            0.0F, 0.25F, 0.5F, 0.125F
        }, {
            0.0F, 0.0F, 0.5F, 0.25F
        }, {
            0.5F, 0.75F, 0.5F, 0.25F
        }, {
            0.5F, 0.625F, 0.5F, 0.125F
        }, {
            0.5F, 0.375F, 0.5F, 0.25F
        }, {
            0.5F, 0.125F, 0.5F, 0.25F
        }, {
            0.5F, 0.0F, 0.5F, 0.125F
        }
    };

}