package veterinaire;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

/*Etape A1.4 : Le client doit récupérer le registre, rechercher "animal", convertir le résultat en IAnimal et appeler les méthodes */
/*Etape A2.3 : récupération, modification de la copie locale, puis nouvelle récupération depuis le serveur. */
/*Etape A3.2 : Consultation et modification du dossier de suivi distant (logique de code suggérée par moi retransmise par IA 
avec donc meilleure présentation, on utilise des fonctions pour les différents tests) */

public class Client_A1A2A3 {
    // Test d'affichage des informations de l'animal
    public static void afficherAnimal(IAnimal stub) throws RemoteException {
        System.out.println("=== ANIMAL ===");
        System.out.println("Nom : " + stub.getNom());
        System.out.println("Maître : " + stub.getNomMaitre());
        System.out.println("Race : " + stub.getRace());

        Espece espece = stub.getEspece();

        System.out.println("Espèce : " + espece.getNom() + ", Espérance de vie : " + espece.getEsperanceDeVie() + 
            ", identityHashCode : " + System.identityHashCode(espece));
    }

    // Test pour le dossier de suivi distant
    public static void afficherDossier(IDossierSuivi dossier) throws RemoteException {
        System.out.println("=== DOSSIER DE SUIVI ===");
        System.out.println("État de santé : " + dossier.getEtatSante());
        System.out.println("Historique des observations : " + dossier.getHistorique());
    }

    public static void main(String[] args){
        String host = (args.length < 1) ? null : args[0];
        try{
            // Connexion au registre RMI
            Registry registry = LocateRegistry.getRegistry(host);
            // Récupération objet distant
            IAnimal stub = (IAnimal) registry.lookup("animal");
            
            // =========================
            // A1 / A2
            // =========================

            afficherAnimal(stub);

            // Première récupération de l'espèce
            Espece espece = stub.getEspece();

            // Modification de l'espérance de vie de l'espèce
            espece.setNom("Chien modifié");
            espece.setDureeDeVie(20);
            System.out.println("\n=== Après modification de la copie locale : ===");
            System.out.println("Espèce modifiée : " + espece.getNom() + ", Espérance de vie modifiée : " + espece.getEsperanceDeVie() + 
                ", identityHashCode : " + System.identityHashCode(espece));

            // Nouvelle récupération de l'espèce depuis le serveur
            Espece espece2 = stub.getEspece();
            System.out.println("\n=== Nouvelle récupération depuis le serveur : ===");
            System.out.println("Espèce : " + espece2.getNom() + ", Espérance de vie : " + espece2.getEsperanceDeVie() + 
                ", identityHashCode : " + System.identityHashCode(espece2));

            // =========================
            // A3 : DOSSIER DE SUIVI
            // =========================

            IDossierSuivi dossier = stub.getDossierSuivi();

            System.out.println("\n=== Consultation du dossier de suivi distant : ===");
            afficherDossier(dossier);

            // Modification du dossier à distance
            dossier.setEtatSante("État de santé modifié");
            dossier.ajouterObservation("Nouvelle observation ajoutée par le client.");

            System.out.println("\n=== Après modification du dossier de suivi distant : ===");
            afficherDossier(dossier);

            // On redemande l'animal au serveur, point de contrôle C : une seconde lecture du dossier de suivi distant pour vérifier que les modifications sont bien persistées côté serveur
            IAnimal stub2 = (IAnimal) registry.lookup("animal");
            IDossierSuivi dossier2 = stub2.getDossierSuivi();
            System.out.println("\n=== Nouvelle récupération du dossier de suivi depuis le serveur : ===");
            afficherDossier(dossier2);

            // =========================
            // Informations RMI
            // =========================

            System.out.println("\nstub.getClass().getName() = " + stub.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}