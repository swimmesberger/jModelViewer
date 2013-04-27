package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

class FacialHair
{
  int geoset1 = 0; int geoset2 = 0; int geoset3 = 0;
  FacialHair.FacialHairrTexture[] hair = null;

  public void read(ByteBuffer buf, int numColors)
    throws IOException
  {
    this.geoset1 = buf.getInt();
    this.geoset2 = buf.getInt();
    this.geoset3 = buf.getInt();

    this.hair = new FacialHair.FacialHairrTexture[numColors];
    for (int i = 0; i < numColors; i++) {
      this.hair[i] = new FacialHair.FacialHairrTexture();
      this.hair[i].lowerTexture = Model.readString(buf);
      this.hair[i].upperTexture = Model.readString(buf);
    }
  }

  public void readMo2(ByteBuffer buf) throws IOException {
    this.geoset1 = buf.getInt();
    this.geoset2 = buf.getInt();
    this.geoset3 = buf.getInt();
  }
  public void readColorsMo2(ByteBuffer buf) throws IOException {
    int count = buf.getInt();
    this.hair = new FacialHair.FacialHairrTexture[count];
    for (int i = 0; i < count; i++) {
      this.hair[i] = new FacialHair.FacialHairrTexture();
      this.hair[i].lowerTexture = Model.readString(buf);
      this.hair[i].upperTexture = Model.readString(buf);
    }
  }

  class FacialHairrTexture
  {
    String lowerTexture = null;
    String upperTexture = null;

    public FacialHairrTexture()
    {
    }
  }
}