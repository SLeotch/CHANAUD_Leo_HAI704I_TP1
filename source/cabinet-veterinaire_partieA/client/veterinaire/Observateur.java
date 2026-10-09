package veterinaire;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Étape A6.2 : le client exporte lui-même cet objet et en passe le stub au
 * cabinet via sAbonner(). Le serveur peut ensuite le "rappeler" — patron
 * Observer réalisé à travers le réseau (CM 02, section 5).
 */

public class Observateur extends UnicastRemoteObject implements IObservateur {
    // On désigne un vétérinaire par un nom uniquement
    private final String nomVeterinaire;

    public Observateur(String nomVeterinaire) throws RemoteException{
        super();
        this.nomVeterinaire = nomVeterinaire;
    }

    // On appelera cette méthode aux seuils 100, 500, 1000
    @Override
    public void alerte(int nombrePatients, int seuil, boolean hausse){
        // Comment on est niveau hausse ? baisse ?
        String sens = hausse ? "franchi À LA HAUSSE" : "repassé EN DESSOUS";
        // Cet affichage arrive sur un thread du runtime RMI : il peut donc
        // s'intercaler avec le prompt de la console au moment où il survient
        System.out.println();
        System.out.println("[" + nomVeterinaire + "] ALERTE : le seuil de " + seuil
                + " patients a été " + sens + " (nombre actuel : " + nombrePatients + ")");
    }
}
