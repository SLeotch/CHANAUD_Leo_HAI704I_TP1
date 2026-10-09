package veterinaire;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Étape A8, responsabilité "logique client" : toutes les opérations sur les
 * interfaces distantes sont concentrées ici. ConsoleInterface ne connaît que
 * cette classe, jamais java.rmi ni ICabinetVeterinaire/IAnimal directement.
 */

public class ClientLogic {
    private final ICabinetVeterinaire cabinet;
    private final String nomVeterinaire;
    private Observateur observateur;

    public ClientLogic(ICabinetVeterinaire cabinet, String nomVeterinaire){
        this.cabinet = cabinet;
        this.nomVeterinaire = nomVeterinaire;
    }

    /* Rappel : Depuis la CLI, l’utilisateur doit pouvoir :
        • consulter la liste des patients et rechercher un patient par son nom ;
        • enregistrer un nouveau patient, espèce comprise ;
        • consulter le dossier de suivi d’un patient et le mettre à jour ;
        • s’abonner aux alertes et s’en désabonner, les alertes s’affichant pendant l’utilisation ;
        • quitter proprement. 
    */

    // Premier point
    public List<InfoAnimal> listerPatients(){
        try{
            List<InfoAnimal> resultat = new ArrayList<>();
            for(IAnimal animal : cabinet.getPatients()){
                resultat.add(new InfoAnimal(animal.getNom(), animal.getNomMaitre(), animal.getRace(), animal.getEspece()));
            }
            return resultat;
        } catch(RemoteException e){
            // On envoie notre erreur à nous (ErreurServiceDistant) --> Suggéré par IA
            throw new ErreurServiceDistant("Impossible de lister les patients", e);
        }
    }
    public InfoAnimal rechercherParNom(String nom){
        try{
            IAnimal animal = cabinet.rechercherParNom(nom);
            return animal == null ? null : new InfoAnimal(animal.getNom(), animal.getNomMaitre(), animal.getRace(), animal.getEspece());
        } catch (RemoteException e){
            throw new ErreurServiceDistant("La recherche par nom n'a pas aboutie pour " + nom + ".", e);
        }
    }

    // Second point
    public InfoAnimal enregistrerUnPatient(String nom, String nomMaitre, String race, Espece espece){
        try{
            cabinet.ajouterPatient(nom, nomMaitre, race, espece);
            IAnimal a = cabinet.rechercherParNom(nom);
            if(a == null){
                throw new ErreurServiceDistant("Le patient a été ajouté mais introuvable");
            }
            return new InfoAnimal(a.getNom(), a.getNomMaitre(), a.getRace(), a.getEspece());
        } catch (RemoteException e){
            throw new ErreurServiceDistant("Impossible d'enregistrer '" + nom + "'.", e);
        }
    }

    // Troisième point
    public String lireEtatSante(String nomAnimal){
        try{
            IAnimal a = cabinet.rechercherParNom(nomAnimal);
            if(a == null) throw new ErreurServiceDistant("Aucun patient nommé '" + nomAnimal + "'.");
            return a.getDossierSuivi().getEtatSante();
        } catch (RemoteException e){
            throw new ErreurServiceDistant("Dossier inaccessible pour '" + nomAnimal + "'.");
        }
    }
    public List<String> lireHistorique(String nomAnimal){
        try{
            IAnimal a = cabinet.rechercherParNom(nomAnimal);
            if(a == null) throw new ErreurServiceDistant("Aucun patient nommé '" + nomAnimal + "'.");
            return a.getDossierSuivi().getHistorique();
        } catch (RemoteException e){
            throw new ErreurServiceDistant("Dossier inaccessible pour '" + nomAnimal + "'.");
        }
    }
    public void ajouterObservation(String nomAnimal, String observation){
        try{
            IAnimal a = cabinet.rechercherParNom(nomAnimal);
            if(a == null) throw new ErreurServiceDistant("Aucun patient nommé '" + nomAnimal + "'.");
            a.getDossierSuivi().ajouterObservation(observation);
        } catch (RemoteException e){
            throw new ErreurServiceDistant("Dossier inaccessible pour '" + nomAnimal + "'.");
        }
    }
    public InfoDossier consulterDossier(String nomAnimal) {
        try {
            IAnimal a = cabinet.rechercherParNom(nomAnimal);
            if (a == null) {
                throw new ErreurServiceDistant("Aucun patient nommé '" + nomAnimal + "'.");
            }
            IDossierSuivi dossier = a.getDossierSuivi();
            return new InfoDossier(dossier.getEtatSante(),dossier.getHistorique());
        } catch (RemoteException e) {
            throw new ErreurServiceDistant("Dossier inaccessible pour '" + nomAnimal + "'.", e);
        }
    }
    public void mettreAJourEtatSante(String nomAnimal, String nouvelEtat){
        try{
            IAnimal a = cabinet.rechercherParNom(nomAnimal);
            if(a == null){
                throw new ErreurServiceDistant("Aucun patient nommé '" + nomAnimal + "'.");
            }
            a.getDossierSuivi().setEtatSante(nouvelEtat);
        } catch (RemoteException e){
            throw new ErreurServiceDistant("Dossier inaccessible pour '" + nomAnimal + "'.", e);
        }
    }

    // Quatrième point
    public boolean estAbonne(){
        return observateur != null;
    }
    public void sAbonner(){
        if(observateur != null) return;
        try{
            observateur = new Observateur(nomVeterinaire);
            cabinet.sAbonner(observateur);
        } catch(RemoteException e){
            observateur = null;
            throw new ErreurServiceDistant("Abonnement impossible", e);
        }
    }
    public void seDesabonner(){
        if(observateur == null) return;
        try{
            cabinet.seDesabonner(observateur);
        } catch (RemoteException e){
            throw new ErreurServiceDistant("Désabonnement impossible");
        }
    }

    // Cinquième point, à appeler en quittant sinon les threads de l'observateur exporté gardent la JVM en vie
    public void quitter(){
        seDesabonner();
    }
}
