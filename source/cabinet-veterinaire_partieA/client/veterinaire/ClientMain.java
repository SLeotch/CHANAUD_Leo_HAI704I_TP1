package veterinaire;

import java.lang.reflect.Proxy;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Étape A8, responsabilité "point d'entrée" : lit les arguments (hôte, port,
 * nom du vétérinaire), obtient le stub du cabinet, puis lance la console.
 * Avec ClientLogic, c'est le seul endroit du client à importer java.rmi.
 *
 * Usage : java veterinaire.ClientMain [hote] [port] [nomVeterinaire]
 */
public class ClientMain {

    public static void main(String[] args) throws Exception {
        String host = (args.length >= 1) ? args[0] : null;
        int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 1099;
        String nomVeterinaire = (args.length >= 3) ? args[2] : "Vétérinaire";

        Registry registry = LocateRegistry.getRegistry(host, port);
        ICabinetVeterinaire cabinet = (ICabinetVeterinaire) registry.lookup("cabinet");

        diagnostiquerStub(cabinet);      // Point de contrôle A
        demontrerCopieEspece(cabinet);   // Point de contrôle B

        ClientLogic logique = new ClientLogic(cabinet, nomVeterinaire);
        ConsoleInterface console = new ConsoleInterface(logique);
        console.lancer();
    }

    /** Point de contrôle A : le stub est un proxy dynamique, pas une classe engendrée. */
    private static void diagnostiquerStub(ICabinetVeterinaire cabinet) {
        System.out.println("Stub obtenu : " + cabinet.getClass().getName());
        System.out.println("Proxy.isProxyClass(...) = " + Proxy.isProxyClass(cabinet.getClass()));
        System.out.println();
    }

    /** Point de contrôle B : identityHashCode différents, mutation locale sans effet côté serveur. */
    private static void demontrerCopieEspece(ICabinetVeterinaire cabinet) throws Exception {
        IAnimal spoony = cabinet.rechercherParNom("Spoony");
        if (spoony == null) {
            return; // patient de démo absent (registre déjà utilisé autrement)
        }

        Espece espece = spoony.getEspece();
        System.out.println("Espèce de Spoony (côté client) : " + espece.getNom()
                + ", " + espece.getEsperanceDeVie() + " ans"
                + " — identityHashCode=" + System.identityHashCode(espece));

        espece.setDureeDeVie(999); // mutation LOCALE uniquement
        System.out.println("Après mutation locale : " + espece.getEsperanceDeVie() + " ans");

        Espece especeRedemandee = spoony.getEspece(); // nouvel appel => nouvelle copie
        System.out.println("Nouvelle demande au serveur : " + especeRedemandee.getEsperanceDeVie() + " ans"
                + " — identityHashCode=" + System.identityHashCode(especeRedemandee));
        System.out.println();
    }
}
