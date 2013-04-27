package org.fseek.thedeath.modelview;

class Armor
  implements Comparable<Armor>
{
  protected ArmorGeoset[] geo;
  protected ArmorTexture[] tex;
  protected ArmorModel[] mod;
  protected int slot = 0;
  protected int uniqueSlot = 0;
  protected int geoA = 0; protected int geoB = 0; protected int geoC = 0;
  protected String modelFile;
  protected Model model;

  public Armor(Model m, int s)
  {
    this.model = m;
    this.slot = s;
    this.uniqueSlot = Model.uniqueSlots[this.slot];
    this.modelFile = null;

    this.geo = null;
    this.tex = null;
    this.mod = null;
  }
  public Armor(Model m, int s, String modelStr, int race, int gender) {
    this(m, s);

    this.modelFile = modelStr;

    if (this.slot != 3)
      this.mod = new ArmorModel[1];
    else {
      this.mod = new ArmorModel[2];
    }
    int fauxSlot = this.slot;
    if (this.slot == 26)
      fauxSlot = 21;
    for (int i = 0; i < this.mod.length; i++) 
    {
        int bone = -1;
        if ((this.model.mSlotBones != null) && (this.model.mSlotBones.length > 0)) {
            int found = 0;
            for (int j = 0; j < this.model.mSlotBones.length; j++) {
            SlotBone sb = this.model.mSlotBones[j];
            if ((sb.slot == fauxSlot) && (found == i)) {
                bone = sb.bone;
                break;
            }
            if (sb.slot == fauxSlot) {
                found++;
            }
            }
        }

        this.mod[i] = new ArmorModel(new Model(m.getViewer()), race, gender, bone);

        String fn = "models/" + this.modelFile;
        if (this.slot == 1)
            fn = fn + "_" + race + "_" + gender;
        else if (this.slot == 3)
            fn = fn + "_" + (i + 1);
        fn = fn + ".mum";
        FileLoader loader = new FileLoader(1004, this.mod[i].model, fn.toLowerCase(), m.getViewer());
        loader.start();
    }
  }

  public int compareTo(Armor a) {
    if (Model.slotSort[this.slot] < Model.slotSort[a.slot]) return -1;
    if (Model.slotSort[this.slot] == Model.slotSort[a.slot]) return 0;
    return 1;
  }

  class ArmorGeoset
  {
    int index = -1;
    int value = 0;

    public ArmorGeoset()
    {
    }
  }

  class ArmorModel
  {
    int race;
    int gender;
    int bone;
    Model model;

    public ArmorModel(Model m, int r, int g, int b) {
      this.race = r;
      this.gender = b;
      this.bone = b;
      this.model = m;
    }
  }

  class ArmorTexture
  {
    int slot = -1;
    int gender = 0;
    String name = null;
    Material mat = null;

    public ArmorTexture()
    {
    }
  }
}