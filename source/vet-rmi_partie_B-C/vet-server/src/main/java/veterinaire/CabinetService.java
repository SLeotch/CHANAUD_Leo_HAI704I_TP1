package veterinaire;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CabinetService extends UnicastRemoteObject implements ICabinetVeterinaire {
    private final PatientRegistry registry;

    public CabinetService(PatientRegistry registry) throws RemoteException{
        super();
        this.registry = registry;
    }

    // Méthodes copiées
    @Override
    public void ajouterPatient(String nom, String nomMaitre, String race, Espece espece) throws RemoteException {
        Animal animal = new Animal(nom, nomMaitre, race, espece);
        registry.ajouter(animal);
    }

    @Override
    public boolean retirerPatient(String nom) throws RemoteException {
        return registry.retirer(nom);
    }

    @Override
    public IAnimal rechercherParNom(String nom) throws RemoteException {
        return registry.rechercherParNom(nom);
    }

    @Override
    public List<IAnimal> getPatients() throws RemoteException {
        return registry.tous();
    }

    @Override 
    public int getNombrePatients() throws RemoteException {
        return registry.nombre();
    }

    @Override
    public void sAbonner(IObservateur observateur) throws RemoteException {
        registry.sAbonner(observateur);
    }

    @Override
    public void seDesabonner(IObservateur observateur) throws RemoteException {
        registry.seDesabonner(observateur);
    }
}
