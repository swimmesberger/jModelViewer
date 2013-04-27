package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

class SkinColor
{
  String baseTexture = null;
  String furTexture = null;
  String pantiesTexture = null;
  String braTexture = null;
  FaceTexture[] face = null;

  public void read(ByteBuffer buf, int numFaces)
    throws IOException
  {
    this.baseTexture = Model.readString(buf);
    this.furTexture = Model.readString(buf);
    this.pantiesTexture = Model.readString(buf);
    this.braTexture = Model.readString(buf);

    this.face = new FaceTexture[numFaces];
    for (int i = 0; i < numFaces; i++) {
      this.face[i] = new FaceTexture();
      this.face[i].lowerTexture = Model.readString(buf);
      this.face[i].upperTexture = Model.readString(buf);
    }
  }

  public void readMo2(ByteBuffer buf) throws IOException {
    this.baseTexture = Model.readString(buf);
    this.furTexture = Model.readString(buf);
    this.pantiesTexture = Model.readString(buf);
    this.braTexture = Model.readString(buf);
  }
  public void readFacesMo2(ByteBuffer buf) throws IOException {
    int count = buf.getInt();
    this.face = new FaceTexture[count];
    for (int i = 0; i < count; i++) {
      this.face[i] = new FaceTexture();
      this.face[i].lowerTexture = Model.readString(buf);
      this.face[i].upperTexture = Model.readString(buf);
    }
  }

  class FaceTexture
  {
    String lowerTexture = null;
    String upperTexture = null;

    public FaceTexture()
    {
    }
  }
}