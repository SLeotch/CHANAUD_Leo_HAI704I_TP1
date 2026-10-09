package veterinaire;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/* Etape A4.3 : On fait un nouveau client, dans un fichier différent pour éviter de confondre et montrer tous
les affichages puvant être obtenus au cours du projet, fait avec IA pour plus de rapidité ici */

public class Client_A4A5A7 {

    public static void afficherAnimal(IAnimal animal) throws Exception {

        System.out.println("Nom : " + animal.getNom());
        System.out.println("Maître : " + animal.getNomMaitre());
        System.out.println("Race : " + animal.getRace());

        Espece espece = animal.getEspece();

        System.out.println(
            "Espèce : " + espece.getNom()
            + ", Espérance de vie : " + espece.getEsperanceDeVie()
        );

        System.out.println();
    }

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];

        try {
            Registry registry = LocateRegistry.getRegistry(host);

            ICabinetVeterinaire cabinet =
                (ICabinetVeterinaire) registry.lookup("cabinet");

            System.out.println("=== NOMBRE DE PATIENTS ===");
            System.out.println(
                "Nombre de patients : " + cabinet.getNombrePatients()
            );

            System.out.println("\n=== LISTE DES PATIENTS ===");

            List<IAnimal> patients = cabinet.getPatients();

            for (IAnimal animal : patients) {
                afficherAnimal(animal);
            }

            System.out.println("=== RECHERCHE DE SPOONY ===");

            IAnimal animal = cabinet.rechercherParNom("Spoony");

            if (animal != null) {
                afficherAnimal(animal);
            } else {
                System.out.println("Patient introuvable.");
            }

            System.out.println("=== RECHERCHE D'UN PATIENT INEXISTANT ===");

            IAnimal inconnu = cabinet.rechercherParNom("Inconnu");

            if (inconnu == null) {
                System.out.println("Patient introuvable.");
            }

            /*Etape A5.3 : On va tester si l'ajout d'un patient fonctionne bien sur la taille et sur la liste, côté
            client ici mais aussi côté serveur pour prouver l'interaction distante.
            */

            System.out.println("\n=== AJOUT D'UN NOUVEAU PATIENT ===");
            int nombreAvant = cabinet.getNombrePatients();
            System.out.println("Nombre de patients avant : " + nombreAvant);

            cabinet.ajouterPatient("Médor", "Vladimir", "T-Rex", new Espece("Dinosaure", 35));

            int nombreApres = cabinet.getNombrePatients();
            System.out.println("Nombre de patients après : " + nombreApres);

            System.out.println("\n=== RECHERCHE DU NOUVEAU PATIENT ===");

            IAnimal nouveauPatient = cabinet.rechercherParNom("Médor");

            if (nouveauPatient != null) {
                System.out.println("Patient trouvé !");
                afficherAnimal(nouveauPatient);
            } else {
                System.out.println("Patient introuvable.");
            }

            /* Etape A7.1 : Test du fail d'ajout EspeceChien */

            System.out.println("\n=== AJOUT D'UN NOUVEAU PATIENT CHIEN ===");
            cabinet.ajouterPatient("BugChien", "Missingno", "Waffif", new EspeceChien(99, "no"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}