package veterinaire;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
Etape A6.1 : contrat que le CLIENT implémente et exporte pour être rappelé
(callback) par le cabinet. Patron Observer réalisé à travers le réseau
(CM 01 patron 5, CM 02 section 5).
 */

// J'ai rajouté le booleen hausse pour savoir si on est en dessous des seuils ou au dessus et ainsi changer
// le message en sortie standard
public interface IObservateur extends Remote {
    void alerte(int nombrePatients, int seuil, boolean hausse) throws RemoteException;
}
