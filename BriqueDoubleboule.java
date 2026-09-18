//package cassebriques;

import java.awt.Color;

class BriqueDoubleboule extends Brique {
  private final int DoubleBoule = 4;

  public BriqueDoubleboule() {
    super();
    couleur = Color.red;
  }

  public int choc() {
    super.choc();
    return DoubleBoule;
  }

}
