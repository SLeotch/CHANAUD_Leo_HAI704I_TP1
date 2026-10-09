package veterinaire;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/*
Etape A1.2 : Le servant : reste dans la JVM du serveur (CM 02, section 2).
 */
public class Animal extends UnicastRemoteObject implements IAnimal {
    private final String nom;
    private final String nomMaitre;
    private final String race;
    /*Etape A2.2 : L'animal ne stocke plus un String mais une Espece, on modifiera la client et le serveur en conséquence */
    private final Espece espece;
    /*A3 : Dossier de suivi associé à l'animal */
    private final IDossierSuivi dossierSuivi;

    public Animal(String nom, String nomMaitre, String race, Espece espece) throws RemoteException {
        super(); // export implicite
        this.nom = nom; 
        this.nomMaitre = nomMaitre;
        this.race = race;
        this.espece = espece;
        this.dossierSuivi = new DossierSuivi("Bon état de santé"); // Création du dossier de suivi associé à l'animal
    }

    @Override public String getNom() { return nom; }
    @Override public String getNomMaitre() { return nomMaitre; }
    @Override public String getRace() { return race; }
    @Override public Espece getEspece() { return espece; }
    public IDossierSuivi getDossierSuivi() { return dossierSuivi;}
}
