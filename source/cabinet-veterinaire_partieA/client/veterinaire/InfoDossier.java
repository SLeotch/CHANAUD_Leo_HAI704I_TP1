package veterinaire;

import java.util.List;

/** Donnée purement locale, retournée par ClientLogic.consulterDossier(). */
public class InfoDossier {

    private String etatSante;
    private final List<String> historique;

    public InfoDossier(String etatSante, List<String> historique) {
        this.etatSante = etatSante;
        this.historique = historique;
    }

    public String getEtatSante() { return etatSante; }
    public List<String> getHistorique() { return historique; }

    public void setEtatSante(String etatSante) {this.etatSante = etatSante;} 
}
