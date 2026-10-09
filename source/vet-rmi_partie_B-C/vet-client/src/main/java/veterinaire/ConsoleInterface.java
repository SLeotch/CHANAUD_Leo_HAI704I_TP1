package veterinaire;

import java.util.List;
import java.util.Scanner;

/**
 * Étape A8, responsabilité "interface console" : menu, lecture/validation
 * des saisies, affichage des résultats. Volontairement AUCUN import
 * java.rmi ici — toute communication distante passe par ClientLogic, dont
 * les seules exceptions visibles ici sont RuntimeException (ErreurServiceDistant
 * ou une erreur de saisie). Affichage géré par IA, logique application donnée par ClientLogic.java
 * retranscrite ici.
 */
public class ConsoleInterface {

    private final ClientLogic logique;
    private final Scanner saisie = new Scanner(System.in);
    private volatile boolean enCours = true;

    public ConsoleInterface(ClientLogic logique) {
        this.logique = logique;
    }

    public void lancer() {
        System.out.println("=== Cabinet vétérinaire — client ===");
        while (enCours) {
            afficherMenu();
            String choix = saisie.nextLine().trim();
            traiterChoix(choix);
        }
        logique.quitter();
        System.out.println("Au revoir.");
    }

    private void afficherMenu() {
        System.out.println();
        System.out.println("1. Lister les patients");
        System.out.println("2. Rechercher un patient par nom");
        System.out.println("3. Enregistrer un nouveau patient");
        System.out.println("4. Consulter le dossier de suivi d'un patient");
        System.out.println("5. Mettre à jour l'état de santé d'un patient");
        System.out.println("6. Ajouter une observation au dossier d'un patient");
        System.out.println("7. S'abonner aux alertes de seuil"
                + (logique.estAbonne() ? " (déjà abonné)" : ""));
        System.out.println("8. Se désabonner des alertes");
        System.out.println("9. Quitter");
        System.out.print("Votre choix : ");
    }

    private void traiterChoix(String choix) {
        try {
            switch (choix) {
                case "1": lister(); break;
                case "2": rechercher(); break;
                case "3": enregistrer(); break;
                case "4": consulterDossier(); break;
                case "5": mettreAJourEtat(); break;
                case "6": ajouterObservation(); break;
                case "7": abonner(); break;
                case "8": desabonner(); break;
                case "9": enCours = false; break;
                default: System.out.println("Choix invalide : entrez un nombre entre 1 et 9.");
            }
        } catch (ErreurServiceDistant e) {
            // Panne distante (serveur arrêté, animal disparu...) : message
            // compréhensible, on ne quitte pas le client pour autant.
            System.out.println("Échec de l'opération : " + e.getMessage());
        } catch (RuntimeException e) {
            // Saisie invalide (ex. nombre mal formé) : idem.
            System.out.println("Saisie invalide : " + e.getMessage());
        }
    }

    private void lister() {
        List<InfoAnimal> patients = logique.listerPatients();
        if (patients.isEmpty()) {
            System.out.println("Aucun patient enregistré.");
            return;
        }
        patients.forEach(a -> System.out.println(" - " + a));
    }

    private void rechercher() {
        String nom = demander("Nom du patient : ");
        InfoAnimal a = logique.rechercherParNom(nom);
        System.out.println(a == null ? "Aucun patient nommé '" + nom + "'." : a.toString());
    }

    private void enregistrer() {
        String nom = demander("Nom du patient : ");
        String maitre = demander("Nom du maître : ");
        String race = demander("Race : ");
        Espece espece = demanderEspece("Espèce : ");
        InfoAnimal a = logique.enregistrerUnPatient(nom, maitre, race, espece);
        System.out.println("Patient enregistré : " + a);
    }

    private void consulterDossier() {
        String nom = demander("Nom du patient : ");
        InfoDossier dossier = logique.consulterDossier(nom);
        System.out.println("État de santé : " + dossier.getEtatSante());
        System.out.println("Historique :");
        if (dossier.getHistorique().isEmpty()) {
            System.out.println("  (aucune observation)");
        } else {
            dossier.getHistorique().forEach(obs -> System.out.println("  - " + obs));
        }
    }

    private void mettreAJourEtat() {
        String nom = demander("Nom du patient : ");
        String etat = demander("Nouvel état de santé : ");
        logique.mettreAJourEtatSante(nom, etat);
        System.out.println("État de santé mis à jour.");
    }

    private void ajouterObservation() {
        String nom = demander("Nom du patient : ");
        String observation = demander("Observation à ajouter : ");
        logique.ajouterObservation(nom, observation);
        System.out.println("Observation ajoutée.");
    }

    private void abonner() {
        logique.sAbonner();
        System.out.println("Abonné aux alertes : elles s'afficheront ici pendant l'utilisation.");
    }

    private void desabonner() {
        logique.seDesabonner();
        System.out.println("Désabonné des alertes.");
    }

    // Internes

    private String demander(String invite) {
        System.out.print(invite);
        return saisie.nextLine().trim();
    }

    private Espece demanderEspece(String invite){
        String nom = demander(invite);
        int duree = demanderEntier("Durée de vie moyenne de l'espèce (années) : ");
        return new Espece(nom, duree);
    }

    private int demanderEntier(String invite) {
        System.out.print(invite);
        String ligne = saisie.nextLine().trim();
        try {
            return Integer.parseInt(ligne);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + ligne + "' n'est pas un nombre entier.");
        }
    }
}
