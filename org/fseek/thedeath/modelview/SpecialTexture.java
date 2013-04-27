// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   SpecialTexture.java

package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

// Referenced classes of package modelview:
//            Material, Model, FileLoader

class SpecialTexture
{

    public SpecialTexture()
    {
        mId = 0;
        mTextureName = null;
        mMaterial = null;
    }

    public SpecialTexture(Model model, int id, Material mat)
    {
        mId = (byte)id;
        mMaterial = mat;
        mTextureName = mMaterial.mFilename;
        load(model);
    }

    public SpecialTexture(Model model, int id, String texture)
    {
        mId = (byte)id;
        mTextureName = texture;
        mMaterial = new Material(mTextureName);
        load(model);
    }

    public void read(ByteBuffer buf)
        throws IOException
    {
        mId = buf.get();
        mTextureName = Model.readString(buf);
        if(mTextureName.length() > 0)
            mMaterial = new Material(mTextureName);
    }

    public void load(Model model)
    {
        if(mMaterial == null)
            return;
        int type = 1;
        if(mId == 1)
            type = 2;
        FileLoader fl = new FileLoader(type, model, mMaterial, (new StringBuilder("textures/")).append(mTextureName).toString().toLowerCase(), model.getViewer());
        fl.start();
    }

    byte mId;
    String mTextureName;
    Material mMaterial;
}