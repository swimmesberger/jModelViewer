package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

class HairStyle
{
  int geoset = 0; int index = 0;
  HairTexture[] hair = null;

  public void read(ByteBuffer buf, int numColors)
    throws IOException
  {
    this.geoset = buf.getInt();
    this.index = buf.getInt();

    this.hair = new HairTexture[numColors];
    for (int i = 0; i < numColors; i++) {
      this.hair[i] = new HairTexture();
      this.hair[i].texture = Model.readString(buf);
      this.hair[i].lowerTexture = Model.readString(buf);
      this.hair[i].upperTexture = Model.readString(buf);
    }
  }

  public void readMo2(ByteBuffer buf) throws IOException {
    this.geoset = buf.getInt();
    this.index = buf.getInt();
  }
  public void readColorsMo2(ByteBuffer buf) throws IOException {
    int count = buf.getInt();
    this.hair = new HairTexture[count];
    for (int i = 0; i < count; i++) {
      this.hair[i] = new HairTexture();
      this.hair[i].texture = Model.readString(buf);
      this.hair[i].lowerTexture = Model.readString(buf);
      this.hair[i].upperTexture = Model.readString(buf);
    }
  }

  class HairTexture
  {
    String texture = null;
    String lowerTexture = null;
    String upperTexture = null;

    public HairTexture()
    {
    }
  }
}