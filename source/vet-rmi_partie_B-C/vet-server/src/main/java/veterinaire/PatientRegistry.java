package veterinaire;

import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class PatientRegistry {
    private static final int[] SEUILS = {100, 500, 1000};

    private final List<IAnimal> patients = new CopyOnWriteArrayList<>();
    private final List<IObservateur> observateurs = new CopyOnWriteArrayList<>();

    // Méthodes recopiées de la partie A
    public void ajouter(IAnimal animal) throws RemoteException {
        int avant = patients.size();
        patients.add(animal);
        verifierSeuils(avant, patients.size());
    }

    public IAnimal rechercherParNom(String nom) throws RemoteException {
        for (IAnimal a : patients) {
            if (a.getNom().equals(nom)) return a;
        }
        return null;
    }

    public List<IAnimal> tous() {
        return List.copyOf(patients);
    }

    public int nombre(){
        return patients.size();
    }

    public void sAbonner(IObservateur observateur) {
        observateurs.add(observateur);
    }

    public void seDesabonner(IObservateur observateur) {
        observateurs.remove(observateur);
    }

    private void verifierSeuils(int avant, int apres) throws RemoteException {
        for (int seuil : SEUILS) {
            boolean hausse = avant < seuil && apres >= seuil;
            boolean baisse = avant >= seuil && apres < seuil;
            if (hausse || baisse) notifier(seuil, hausse, apres);
        }
    }

    private void notifier(int seuil, boolean hausse, int nombre) {
        for (IObservateur obs : observateurs) {
            try {
                obs.alerte(nombre, seuil, hausse);
            } catch (RemoteException e) {
                observateurs.remove(obs); // client injoignable
            }
        }
    }

    public boolean retirer(String nom) throws RemoteException{
        int avant = patients.size();
        IAnimal patient = rechercherParNom(nom);
        if(patient == null){
            return false;
        }
        patients.remove(patient);
        int apres = patients.size();
        verifierSeuils(avant, apres);
        return true;
    }
}
