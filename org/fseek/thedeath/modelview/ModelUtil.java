package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class ModelUtil
{
    public static ByteBuffer bufferData(InputStream is)
        throws IOException
    {
        int curMax = 16384;
        byte mainBuf[] = new byte[curMax];
        int offset = 0;
        for(int count = is.read(mainBuf, 0, curMax); count != -1; count = is.read(mainBuf, offset, curMax - offset))
        {
            if(offset + count == curMax)
            {
                curMax *= 3;
                byte tmp[] = new byte[curMax];
                System.arraycopy(mainBuf, 0, tmp, 0, offset + count);
                mainBuf = tmp;
            }
            offset += count;
        }

        return ByteBuffer.wrap(mainBuf, 0, offset).order(ByteOrder.LITTLE_ENDIAN);
    }
}
