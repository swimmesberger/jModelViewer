// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   Material.java

package org.fseek.thedeath.modelview;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

// Referenced classes of package modelview:
//            FileRequester, Model

class Material
    implements FileRequester
{

    public void RequestComplete(Object origin, int id, InputStream stream, String path)
    {
        try
        {
            mBufferedImage = ImageIO.read(stream);
            Model model = (Model)origin;
            if(id == 1)
                model.mLoadedTextures.add(this);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void RequestFailed(Object origin, int id, String path)
    {
        mSkip = true;
    }

    public Material()
    {
        mFilename = null;
        mTextureId = 0;
        mBufferedImage = null;
        mSkip = false;
    }

    public Material(String tex)
    {
        mFilename = tex;
        mSpecialTexture = -1;
    }

    public void read(ByteBuffer buf)
        throws IOException
    {
        mFilename = Model.readString(buf);
        mSpecialTexture = buf.getInt();
    }

    public void readMo2(ByteBuffer buf)
        throws IOException
    {
        mFilename = Model.readString(buf);
        byte inByte = buf.get();
        mSpecialTexture = inByte & 0xff;
    }

    protected String mFilename;
    protected int mTextureId;
    protected BufferedImage mBufferedImage;
    protected int mSpecialTexture;
    protected boolean mSkip;
}