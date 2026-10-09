package veterinaire;

import java.io.Serializable;

/* 
Classe représentant une espèce d'animaux dans le cabinet vétérinaire. Cette classe est sérialisable pour permettre la transmission 
de ses instances entre le client et le serveur via RMI. Elle se situe dans common pour être accessible à la fois par le client et 
le serveur.
 */

public class Espece implements Serializable {
    private String nom;
    private int esperanceDeVie;

    public Espece(String nom, int esperanceDeVie) {
        this.nom = nom;
        this.esperanceDeVie = esperanceDeVie;
    }

    public String getNom() {
        return nom;
    }

    public int getEsperanceDeVie() {
        return esperanceDeVie;
    }
    
    /* Grosse aparté que me conseille une IA quand je lui passe mon code pour le point de contrôle B : muter la copie reçue
    par le client pour qu'il puisse modifier l'espérance de vie de l'espèce parce que Espece est Serializable et non Remote. 
    Je ne sais pas si c'est ce que vous attendez mais je le fais quand même.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDureeDeVie(int esperanceDeVie) {
        this.esperanceDeVie = esperanceDeVie;
    }
}
