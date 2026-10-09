package veterinaire;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CabinetVeterinaire extends UnicastRemoteObject implements ICabinetVeterinaire {
    public ArrayList<IAnimal> patients = new ArrayList<>();
    // Observateurs
    private final List<IObservateur> observateurs = new CopyOnWriteArrayList<>();

    public CabinetVeterinaire() throws RemoteException {
        super();

        patients = new ArrayList<>();

        patients.add(new Animal("Spoony", "Manon", "Spitz nain", new Espece("Chien", 18)));
        patients.add(new Animal("Rex", "Axel", "Berger allemand", new Espece("Chien", 14)));
        patients.add(new Animal("Bob", "Eric", "Bouldogue français", new Espece("Chien", 16)));
    }   

    public List<IAnimal> getPatients() throws RemoteException{
        return new ArrayList<>(patients);
    }

    public int getNombrePatients() throws RemoteException {
        return patients.size();
    }

    public IAnimal rechercherParNom(String nom) throws RemoteException {
        for (IAnimal a : patients){
            if(a.getNom().equals(nom)){
                return a;
            }
        }
        System.out.println("Ce nom n'existe pas dans la base de données du cabinet vétérinaire.\n");
        return null;
    }

    // Etape A5.2 : L'animal doit être créé dans la JVM server, parce que c'est un objet distant
    @Override
    public void ajouterPatient(String nom, String nomMaitre, String race, Espece espece) throws RemoteException {
        int avant = patients.size();
        // Point de contrôle B et E : identityHashCode de l'espèce CÔTÉ SERVEUR,
        // à comparer avec celui affiché côté client.
        System.out.println("[Serveur] Espèce '" + espece.getNom() + "' reçue — identityHashCode=" + System.identityHashCode(espece));

        Animal animal = new Animal(nom, nomMaitre, race, espece);
        patients.add(animal);
        verifierSeuils(avant, patients.size());
    }

    // Etape A6.3 : Mis en place des seuilds et des abonnements / désabonnements selon ces seuils

    private static final int[] SEUILS = {100, 500, 1000};

    @Override
    public void sAbonner(IObservateur observateur) throws RemoteException {
        observateurs.add(observateur);
        System.out.println("[Serveur] Nouvel abonné aux alertes (total : " + observateurs.size() + ").");
    }

    @Override
    public void seDesabonner(IObservateur observateur) throws RemoteException {
        observateurs.remove(observateur);
        System.out.println("[Serveur] Désabonnement (total : " + observateurs.size() + ").");
    }

    // Suggéré par IA pour optimisation : vérifier les seuils
    public void verifierSeuils(int avant, int apres){
        for(int seuil : SEUILS){
            boolean franchiHausse = avant < seuil && apres >= seuil;
            boolean franchiBaisse = avant >= seuil && apres < seuil;
            if(franchiBaisse || franchiHausse){
                notification(seuil, franchiHausse, apres);
            }
        }
    }

    private void notification(int seuil, boolean hausse, int nombre) {
        // Robustesse (Point de contrôle F) : un client fermé brutalement ne doit pas empêcher
        // les autres de recevoir leurs alertes -> on capture et on retire.
        for (IObservateur obs : observateurs) {
            try {
                obs.alerte(nombre, seuil, hausse);
            } catch (RemoteException e) {
                System.err.println("[Serveur] Observateur injoignable, retrait : " + e.getMessage());
                observateurs.remove(obs);
            }
        }
    }
}