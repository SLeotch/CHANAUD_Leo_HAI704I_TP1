package veterinaire;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/* Etape A3.1 : décision de conception centrale du TP, choix fait par moi, explication de l'IA pour l'alternative :

Choix retenu ICI : le dossier de suivi est un objet DISTANT à part entière (Remote), exporté séparément de l'animal
qui le porte. Tous les clients qui appelent IAnimal recoivent un stub pointant vers le même servant côté serveur : toute
modification du dossier de suivi est répercutée sur tous les clients. 

L'alternative aurait été de rendre le dossier Serializable, chaque client aurait alors reçu sa PROPRE copie et les modifs seraient
restées locales, un choix qui peut être bien si le suivi n'a pas besoin d'être partagé*/

public interface IDossierSuivi extends Remote {
    String getEtatSante() throws RemoteException;
    void setEtatSante(String etatSante) throws RemoteException;
    // Liste de toutes les observations du vétérinaire sur l'animal, chaque observation est un String
    List<String> getHistorique() throws RemoteException;
    void ajouterObservation(String observation) throws RemoteException;
}