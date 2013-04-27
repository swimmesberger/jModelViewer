package org.fseek.thedeath.modelview;

import java.io.IOException;
import java.nio.ByteBuffer;

class BakedTexture
  implements Comparable<BakedTexture>
{
  byte mRegion;
  byte mLayer;
  String mTextureName;
  Material mMaterial;

  public BakedTexture()
  {
    this.mRegion = (this.mLayer = 0);
    this.mTextureName = null;
    this.mMaterial = null;
  }
  public BakedTexture(Model model, int region, int layer, String texture) {
    this.mRegion = (byte)region;
    this.mLayer = (byte)layer;
    this.mTextureName = texture;
    this.mMaterial = new Material(this.mTextureName);

    load(model);
  }

  public void read(ByteBuffer buf) throws IOException {
    this.mRegion = buf.get();
    this.mLayer = buf.get();
    this.mTextureName = Model.readString(buf);
    if (this.mTextureName.length() > 0)
      this.mMaterial = new Material(this.mTextureName);
  }

  public void load(Model model) {
        if (this.mMaterial == null) {
            return;
        }
        FileLoader fl = new FileLoader(2, model, this.mMaterial, ("textures/" + this.mMaterial.mFilename).toLowerCase(), model.getViewer());
        fl.start();
  }

  public int compareTo(BakedTexture t) {
    if (this.mLayer < t.mLayer) return -1;
    if (this.mLayer == t.mLayer) return 0;
    return 1;
  }
}