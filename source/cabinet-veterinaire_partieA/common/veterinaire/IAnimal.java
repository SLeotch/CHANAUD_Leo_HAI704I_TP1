package veterinaire;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
Etape A1.1 : Contrat simple distant de l'animal avec : nom, maître, race, espèce déclarée avec RemoteException
 */

public interface IAnimal extends Remote {
    String getNom() throws RemoteException;
    String getNomMaitre() throws RemoteException;
    String getRace() throws RemoteException;
    Espece getEspece() throws RemoteException;

    // Nouvel ajout pour l'étape A3 : récupération du dossier de suivi distant
    IDossierSuivi getDossierSuivi() throws RemoteException;
}