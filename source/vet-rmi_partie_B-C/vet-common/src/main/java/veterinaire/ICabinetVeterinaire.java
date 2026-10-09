package veterinaire;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/* Etape A4.1 : le cabinet devient le seul objet publié dans le registre à cet étape, il connait sa collection de patients. Comme mes Animal sont distants
le client ne reçoit pas des copies complètes des animaux mais des stubs, mais des moyens de les joindre. Ainsi on va faire une List<IAnimal> */

public interface ICabinetVeterinaire extends Remote {
    List<IAnimal> getPatients() throws RemoteException;
    // On sait jamais si on en besoin pour des tests, on peut aussi demander le nombre de patients
    int getNombrePatients() throws RemoteException;
    IAnimal rechercherParNom(String nom) throws RemoteException;

    // Etape A5.1 : Opération d'ajout de patient, appelable à distance
    void ajouterPatient(String nom, String nomMaitre, String race, Espece espece) throws RemoteException;

    // Etape A6.3 : Abonnement + Désabonnement
    void sAbonner(IObservateur observateur) throws RemoteException;
    void seDesabonner(IObservateur observateur) throws RemoteException;

    // Suggestion de moi-même : retirer un patient (erreurs)
    boolean retirerPatient(String nom) throws RemoteException;
}
