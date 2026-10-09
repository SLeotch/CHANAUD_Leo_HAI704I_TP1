package veterinaire;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class DossierSuivi extends UnicastRemoteObject implements IDossierSuivi {
    private String etatSante;
    private final List<String> historique = new ArrayList<>();

    public DossierSuivi(String etatSante) throws RemoteException {
        super();
        this.etatSante = etatSante;
    }

    @Override
    public String getEtatSante() throws RemoteException {
        return etatSante;
    }

    @Override
    public void setEtatSante(String etatSante) throws RemoteException {
        this.etatSante = etatSante;
    }

    @Override
    public List<String> getHistorique() throws RemoteException {
        return new ArrayList<>(historique);
    }

    @Override
    public void ajouterObservation(String observation) throws RemoteException {
        historique.add(observation);
    }
}
