import java.awt.*;

class Bonus {
    private int x, y; // position actuelle du bonus
    private int type; // quel effet il donnera si on l'attrape
    private int vitesse; // vitesse de chute
    private Color couleur;

    public Bonus(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        vitesse = 2;
    }

    public void tombe() {
        y += vitesse;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public Bonus(int x, int y, int type, Color couleur) {
        this.x = x;
        this.y = y;
        this.type = type;
        vitesse = 2;
        this.couleur = couleur;
    }

    public void dessine(Graphics2D motif) {
        motif.setColor(couleur);
        motif.fillRect(x, y, 10, 10);
    }
}