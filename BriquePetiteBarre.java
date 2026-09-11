//package cassebriques;

import java.awt.Color;

 class BriquePetiteBarre extends Brique{
   private final int PetiteBarre=3;

  public BriquePetiteBarre() {
    super();
    couleur=Color.blue;
  }

  public int choc() {
    super.choc();
    return PetiteBarre;
  }

}
