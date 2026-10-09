package veterinaire;

public class InfoAnimal {
    private final String nom;
    private final String nomMaitre;
    private final String race;
    private final Espece espece;   // copie locale, pas de java.rmi

    public InfoAnimal(String nom, String nomMaitre, String race, Espece espece) {
        this.nom = nom; this.nomMaitre = nomMaitre; this.race = race; this.espece = espece;
    }
    public String getNom() { return nom; }
    public String getNomMaitre() { return nomMaitre; }
    public String getRace() { return race; }
    public Espece getEspece() { return espece; }

    @Override
    public String toString() {
        return nom + " (maître : " + nomMaitre + ", race : " + race
             + ", espèce : " + espece.getNom() + ", " + espece.getEsperanceDeVie() + " ans)";
    }
}