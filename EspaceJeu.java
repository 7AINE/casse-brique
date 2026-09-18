//package cassebriques;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class EspaceJeu extends JPanel implements Runnable, MouseListener,
    MouseMotionListener {

  // Delai entre 2 déplacements
  private final int DELAI = 16;

  // Constantes rattachées aux phases de jeu
  private final int ATTEND = 1;
  private final int ROULE = 2;
  private final int SORT = 3;
  private final int GAGNE = 4;

  // Constantes rattachées aux types de briques
  private final int SIMPLE = 0;
  private final int NORME = 1;
  private final int RAPIDE = 2;
  private final int PetiteBarre = 3;
  private final int DoubleBoule = 4;

  // Champs d'instance
  private Thread action;
  private boolean fini;
  private int phase;
  private int delai;
  private Barre barre;

  // Liste contenant toutes les boules
  private ArrayList<Boule> boules;

  // Liste contenant tous les bonus
  private ArrayList<Bonus> bonus;

  private Mur mur;

  public EspaceJeu() {

    // Création de la barre
    barre = new Barre();

    // Création de la liste de boules
    boules = new ArrayList<Boule>();

    // Création de la première boule
    boules.add(new Boule());

    // Création de la liste de bonus
    bonus = new ArrayList<Bonus>();

    // Délai entre 2 déplacements
    delai = DELAI;

    // phase d'attente
    phase = ATTEND;

    // Gestion des événements liés à la souris
    addMouseMotionListener(this);
    addMouseListener(this);
  }

  public void initialiseNiveau() {

    // Arrêt du thread action s'il est en cours d'exécution.
    fini = true;

    if (action != null) {
      while (action.isAlive())
        ;
    }

    // Création du mur de brique
    if (mur == null) {
      mur = new Mur();
    }

    mur.construit();

    // On remet une seule boule au début du niveau
    boules.clear();
    boules.add(new Boule());

    // On supprime tous les bonus
    bonus.clear();

    // Première phase du jeu
    phase = ATTEND;
    delai = DELAI;

    // Remise à la taille normale de la barre
    barre.resetMiLargeur();

    // Lancement de l'exécution du jeu dans un thread
    action = new Thread(this);
    action.start();
  }

  // Traitement central exécuté avec une périodicité précise
  public void run() {

    fini = false;

    while (!fini) {

      // Selon la phase du jeu ...
      switch (phase) {

        // Attente de lancement de la boule
        case ATTEND:

          // Placement de la première boule au milieu de la barre
          Boule bouleDepart = boules.get(0);

          bouleDepart.place(
              barre.getX(),
              barre.getY() - bouleDepart.getRayon());

          break;

        // Les boules roulent
        case ROULE:

          // On parcourt toutes les boules
          for (int i = 0; i < boules.size(); i++) {

            Boule boule = boules.get(i);

            // Déplacement de la boule
            boule.deplace();

            // Rebond sur le bord gauche ?
            if (boule.getX() < boule.getRayon()) {

              boule.chocH();

              boule.place(
                  boule.getRayon(),
                  boule.getY());
            }

            else {

              // Rebond sur le bord droit ?
              if (boule.getX() > getSize().width - boule.getRayon()) {

                boule.chocH();

                boule.place(
                    getSize().width - boule.getRayon(),
                    boule.getY());
              }
            }

            // Rebond sur le haut ?
            if (boule.getY() < boule.getRayon()) {

              boule.chocV();

              boule.place(
                  boule.getX(),
                  boule.getRayon());
            }

            else {

              // Rebond (ou non) sur la barre ?
              if (boule.getY() > 310 - boule.getRayon()) {

                if ((boule.getX() - boule.getRayon() < barre.getX() + barre.getMiLargeur())
                    &&
                    (boule.getX() + boule.getRayon() > barre.getX() - barre.getMiLargeur())) {

                  // Rebond sur la barre
                  rebondSurBarre(
                      boule,
                      boule.getX() - barre.getX());

                  boule.place(
                      boule.getX(),
                      310 - boule.getRayon());
                }

                else {

                  // Si la boule touche le fond
                  if (boule.getY() > 310 + barre.getHauteur() - boule.getRayon()) {

                    // On supprime uniquement cette boule
                    boules.remove(i);

                    // Comme on a supprimé un élément,
                    // on recule l'index
                    i--;

                    // On passe à la boule suivante
                    continue;
                  }
                }
              }
            }

            // Gestion du choc avec une brique

            // Récupération de la hauteur d'une brique
            int hauteur = mur.getHauteurBrique();

            // Récupération de la largeur d'une brique
            int largeur = mur.getLargeurBrique();

            // Si la boule se trouve dans la zone du mur de briques
            if (boule.getY() - boule.getRayon() < 10 * (hauteur + 1)) {

              // Coordonnées des coins de la boule
              int l1, l2, c1, c2;

              l1 = (int) ((boule.getY() - boule.getRayon())
                  / (hauteur + 1));

              l2 = (int) ((boule.getY() + boule.getRayon())
                  / (hauteur + 1));

              c1 = (int) ((boule.getX() - boule.getRayon())
                  / (largeur + 1));

              c2 = (int) ((boule.getX() + boule.getRayon())
                  / (largeur + 1));

              // Le rebond dépend des coins

              // Coin supérieur gauche
              if (mur.percute(l1, c1)) {

                // Coin supérieur droit
                if (mur.percute(l1, c2)) {

                  // Choc vertical
                  boule.chocV();
                }

                else {

                  // Coin inférieur gauche
                  if (mur.percute(l2, c1)) {

                    // Choc horizontal
                    boule.chocH();
                  }

                  else {

                    // Double choc
                    boule.chocV();
                    boule.chocH();
                  }
                }
              }

              else {

                // Coin supérieur droit
                if (mur.percute(l1, c2)) {

                  // Coin inférieur droit
                  if (mur.percute(l2, c2)) {

                    // Choc horizontal
                    boule.chocH();
                  }

                  else {

                    // Double choc
                    boule.chocV();
                    boule.chocH();
                  }
                }

                else {

                  // Coin inférieur gauche
                  if (mur.percute(l2, c1)) {

                    // Coin inférieur droit
                    if (mur.percute(l2, c2)) {

                      // Choc vertical
                      boule.chocV();
                    }

                    else {

                      // Double choc
                      boule.chocV();
                      boule.chocH();
                    }
                  }

                  else {

                    // Coin inférieur droit
                    if (mur.percute(l2, c2)) {

                      // Double choc
                      boule.chocV();
                      boule.chocH();
                    }
                  }
                }
              }

              // Casse effective des briques
              // et mise en place des conséquences

              creeBonusSiEffet(mur.casse(l1, c1), l1, c1);
              creeBonusSiEffet(mur.casse(l1, c2), l1, c2);
              creeBonusSiEffet(mur.casse(l2, c1), l2, c1);
              creeBonusSiEffet(mur.casse(l2, c2), l2, c2);

              // Si toutes les briques sont cassées
              if (mur.getNbBriques() == 0) {

                // Le joueur a gagné
                phase = GAGNE;
              }
            }
          } // <-- fin de la boucle "for (Boule boule : boules)"

          // Gestion des bonus qui tombent
          for (int i = 0; i < bonus.size(); i++) {

            Bonus b = bonus.get(i);
            b.tombe();

            // Le bonus est-il arrivé au niveau de la barre ?
            if (b.getY() + 10 >= barre.getY()) {

              // La barre est-elle en dessous, au bon endroit ?
              if (b.getX() + 10 > barre.getX() - barre.getMiLargeur()
                  && b.getX() < barre.getX() + barre.getMiLargeur()) {
                // Attrapé ! On applique enfin l'effet
                modifJeu(b.getType(), boules.get(0));
              }

              // Attrapé ou pas, le bonus disparaît une fois arrivé en bas
              bonus.remove(i);
              i--;
            }
          }

          // Si toutes les boules sont perdues
          if (boules.size() == 0) {

            phase = SORT;
          }

          break;

        case SORT:

          JOptionPane.showMessageDialog(
              this,
              "C'est perdu !",
              "Casse briques",
              JOptionPane.INFORMATION_MESSAGE);

          fini = true;

          break;

        case GAGNE:

          JOptionPane.showMessageDialog(
              this,
              "Bravo, vous avez gagné !",
              "Casse briques",
              JOptionPane.INFORMATION_MESSAGE);

          fini = true;

          break;
      }

      // On redessine l'espace de jeu
      repaint();

      try {

        Thread.sleep(delai);

      } catch (InterruptedException e) {
      }
    }
  }

  // Rebond sur la barre
  void rebondSurBarre(Boule boule, int impact) {

    // Rebond sur la barre
    boule.chocV();

    // La barre est divisée en 5 parties.
    // Chaque partie provoque un rebond différent

    // Partie extrême gauche :
    // augmentation de l'angle de 30 degrés
    if (impact < -(barre.getMiLargeur() * 0.6)) {

      boule.modifAngle(30);
    }

    else {

      // Partie suivante :
      // augmentation de l'angle de 15 degrés
      if (impact < -(barre.getMiLargeur() * 0.2)) {

        boule.modifAngle(15);
      }
    }

    // Partie extrême droite :
    // diminution de l'angle de 30 degrés
    if (impact > (barre.getMiLargeur() * 0.6)) {

      boule.modifAngle(-30);
    }

    else {

      // Partie précédente :
      // diminution de l'angle de 15 degrés
      if (impact > (barre.getMiLargeur() * 0.2)) {

        boule.modifAngle(-15);
      }
    }

    // La partie centrale de la barre
    // provoque un rebond normal
  }

  public void modifJeu(int action, Boule boule) {

    switch (action) {

      case NORME:

        // Retour aux valeurs de base
        delai = DELAI;
        barre.resetMiLargeur();

        break;

      case RAPIDE:

        // Accélération du traitement
        delai = (int) (DELAI / 2);

        break;

      case PetiteBarre:

        // Rétrécissement de la barre
        barre.setMiLargeur(
            (int) (barre.getMiLargeur() * 0.85));

        break;

      case DoubleBoule:

        // Création d'une nouvelle boule
        // à partir de la boule qui a cassé la brique
        boules.add(new Boule(boule));

        break;
    }
  }

  private void creeBonusSiEffet(int effet, int ligne, int colonne) {
    if (effet != SIMPLE) {
      int x = colonne * (mur.getLargeurBrique() + 1);
      int y = ligne * (mur.getHauteurBrique() + 1);
      Color couleurBrique = mur.getCouleurBrique(ligne, colonne);
      bonus.add(new Bonus(x, y, effet, couleurBrique));
    }
  }

  void lanceBoule(int angle) {

    if (phase == ATTEND) {

      phase = ROULE;

      // On lance la première boule
      boules.get(0).angleDep(angle);
    }
  }

  public void paintComponent(Graphics comp) {

    Graphics2D comp2D = (Graphics2D) comp;

    // Effacement de l'espace de jeu
    comp2D.setColor(getBackground());

    comp2D.fillRect(
        0,
        0,
        getSize().width,
        getSize().height);

    // Dessin de la barre
    barre.dessine(comp2D);

    // Dessin de toutes les boules
    for (Boule boule : boules) {

      boule.dessine(comp2D);
    }

    // Dessin des bonus qui tombent
    for (Bonus b : bonus) {

      b.dessine(comp2D);
    }

    // Dessin du mur de briques
    // Au tout départ le mur n'existe pas
    if (mur != null) {

      mur.dessine(comp2D);
    }
  }

  // Méthodes de l'interface MouseMotionListener

  public void mouseMoved(MouseEvent evt) {

    // Si le pointeur est trop à gauche
    if (evt.getX() < barre.getMiLargeur()) {

      // Barre contre le bord gauche
      barre.setX(barre.getMiLargeur());
    }

    else {

      // Si le pointeur est trop à droite
      if (evt.getX() > getSize().width - barre.getMiLargeur()) {

        // Barre contre le bord droit
        barre.setX(
            getSize().width - barre.getMiLargeur());
      }

      else {

        // Barre centrée sur le pointeur
        barre.setX(evt.getX());
      }
    }
  }

  public void mouseDragged(MouseEvent evt) {
  }

  // Méthodes de l'interface MouseListener

  public void mouseClicked(MouseEvent evt) {

    lanceBoule(
        (int) (Math.random() * 120) + 30);
  }

  public void mouseEntered(MouseEvent evt) {
  }

  public void mouseExited(MouseEvent evt) {
  }

  public void mousePressed(MouseEvent evt) {
  }

  public void mouseReleased(MouseEvent evt) {
  }
}