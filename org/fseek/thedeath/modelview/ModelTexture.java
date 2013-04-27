// Decompiled by Thedeath
// Decompiler options: packimports(3) 
// Source File Name:   ModelTexture.java

package org.fseek.thedeath.modelview;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.media.opengl.GL;

public class ModelTexture
{

    public ModelTexture()
    {
    }

    private static int createTextureID(GL gl)
    {
        int tmp[] = new int[1];
        gl.glGenTextures(1, tmp, 0);
        return tmp[0];
    }

    public static int getTexture(GL gl, BufferedImage image)
        throws IOException
    {
        int tex = getTexture(gl, image, 3553, 6408, 9729, 9729);
        return tex;
    }

    public static int getTexture(GL gl, BufferedImage image, int target, int dstPixelFormat, int minFilter, int magFilter)
        throws IOException
    {
        int srcPixelFormat = 0;
        int textureID = createTextureID(gl);
        gl.glBindTexture(target, textureID);
        if(image.getColorModel().hasAlpha())
            srcPixelFormat = 6408;
        else
            srcPixelFormat = 6407;
        ByteBuffer textureBuffer = convertImageData(image);
        if(textureBuffer == null)
            return 0;
        if(target == 3553)
        {
            gl.glTexParameteri(target, 10241, minFilter);
            gl.glTexParameteri(target, 10240, magFilter);
        }
        gl.glTexImage2D(target, 0, dstPixelFormat, image.getWidth(), image.getHeight(), 0, srcPixelFormat, 5121, textureBuffer);
        return textureID;
    }

    private static ByteBuffer convertImageData(BufferedImage bufferedImage)
    {
        ByteBuffer imageBuffer = null;
        int byteSize = bufferedImage.getWidth() * bufferedImage.getHeight() * 4;
        imageBuffer = ByteBuffer.allocateDirect(byteSize);
        imageBuffer.order(ByteOrder.nativeOrder());
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        for(int y = 0; y < h; y++)
        {
            for(int x = 0; x < w; x++)
            {
                int pixel = bufferedImage.getRGB(x, y);
                byte a = (byte)((pixel & 0xff000000) >> 24);
                byte r = (byte)((pixel & 0xff0000) >> 16);
                byte g = (byte)((pixel & 0xff00) >> 8);
                byte b = (byte)(pixel & 0xff);
                imageBuffer.put(r);
                imageBuffer.put(g);
                imageBuffer.put(b);
                if(bufferedImage.getColorModel().hasAlpha())
                    imageBuffer.put(a);
            }

        }

        imageBuffer.flip();
        return imageBuffer;
    }
}