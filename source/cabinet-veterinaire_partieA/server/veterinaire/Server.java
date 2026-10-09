package veterinaire;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/*Etape A1.3 : Le but est de créer le servant dans un main propre à lui, d'obtenir un registre et de publier : "animal" désigne le stub
de notre objet
 */

/*public class Server {
    public static void main(String[] args){
        try{
            Animal animal = new Animal("Spoony", "Manon", "Spitz nain", new Espece("Chien", 18));

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("animal", animal);

            System.out.println("Serveur lancé.");
            System.out.println("Animal publié sous le nom : animal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}*/

/*Etape A4.2 : On change notre serveur, il n'attends plus que de simples animaux mais maintenant un cabinet qui4
contient plusieurs animaux pour le côté plus réel du TP */

public class Server {
    public static void main(String[] args){
        try{
            // On peut le rendre customizable avec le port en paramètre d'éxécution
            int port = (args.length) < 1 ? 1099 : Integer.parseInt(args[1]); 

            CabinetVeterinaire cabinet = new CabinetVeterinaire();

            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("cabinet", cabinet);

            System.out.println("Serveur lancé.");
            System.out.println("Cabinet vétérinaire publié sous le nom : cabinet.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}