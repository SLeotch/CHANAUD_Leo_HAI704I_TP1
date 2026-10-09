package veterinaire; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry; 
import java.util.Scanner; 

/* * Etape A6.4 : 
* Test avec deux clients abonnés aux alertes. * 
* Le premier argument correspond au nom du vétérinaire. 
* Le deuxième argument "test" permet de lancer le test du seuil 100. */ 

public class Client_A6 { 
    public static void main(String[] args) { 
        String nomVeterinaire = (args.length > 0) ? args[0] : "Vétérinaire"; 
        String mode = (args.length > 1) ? args[1] : "attente"; 
        try { 
            // Connexion au registre RMI 
            Registry registry = LocateRegistry.getRegistry(); 
            // Récupération du cabinet 
            ICabinetVeterinaire cabinet = (ICabinetVeterinaire) registry.lookup("cabinet"); 
            
            System.out.println(); 
            System.out.println("================================"); 
            System.out.println(" Vétérinaire : " + nomVeterinaire); 
            System.out.println("================================"); 
            
            // Création de l'observateur côté client 
            Observateur observateur = new Observateur(nomVeterinaire); 
            
            // Abonnement aux alertes 
            cabinet.sAbonner(observateur); 
            System.out.println("✓ Abonné aux alertes du cabinet."); 
            
            /* * Seul le client lancé avec "test" effectue * le franchissement du seuil 100. */ 
            if (mode.equals("test")) { 
                System.out.println(); 
                System.out.println("--- Test du seuil 100 ---"); 
                int nombreActuel = cabinet.getNombrePatients(); 
                System.out.println( "Patients actuellement : " + nombreActuel ); 
                // On atteint 99 patients. 
                while (nombreActuel < 99) { 
                    cabinet.ajouterPatient( "PatientTest" + nombreActuel, nomVeterinaire, "RaceTest", new Espece("Chien", 10) ); 
                    nombreActuel = cabinet.getNombrePatients(); 
                } 
                System.out.println( "→ 99 patients atteints." ); 
                // Ajout du 100e patient. 
                cabinet.ajouterPatient( "Patient100", nomVeterinaire, "RaceTest", new Espece("Chien", 10) ); 
                System.out.println( "→ 100 patients atteints." ); 
            } 
            
            System.out.println(); 
            System.out.println("En attente des alertes..."); 
            System.out.println( "Appuyez sur Entrée pour quitter." ); 
            // Le client reste vivant pour recevoir les callbacks. 
            Scanner scanner = new Scanner(System.in); 
            scanner.nextLine(); 
            
            // Désabonnement 
            cabinet.seDesabonner(observateur); 
            System.out.println( "✓ " + nomVeterinaire + " désabonné." ); 
            scanner.close(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
}