package veterinaire;

/**
 * Étape A7 — expérience de sérialisation.
 *
 * Cette classe est volontairement placée dans "common" : c'est la correction
 * la plus simple possible au problème posé par le sujet.
 *
 * Pour REPRODUIRE l'échec demandé par l'énoncé :
 *   1. déplacez temporairement ce fichier vers client/veterinaire/,
 *   2. recompilez SEULEMENT le client (pas le serveur, qui ne doit plus la
 *      connaître),
 *   3. envoyez une instance de cette classe via
 *      ClientLogic.enregistrerPatient(...) (adaptez temporairement la
 *      signature, ou appelez cabinet.ajouterPatient(...) directement).
 *
 * Résultat observé (à consigner dans le compte-rendu) : le serveur échoue à
 * la désérialisation avec une ClassNotFoundException("veterinaire.EspeceChien"),
 * encapsulée dans une UnmarshalException, elle-même encapsulée dans une
 * ServerException reçue côté client.
 *
 * Explication : un flux sérialisé transporte le NOM de la classe et l'ÉTAT de
 * l'objet, jamais son bytecode (CM 02, section 5). Le correctif le plus
 * simple est donc de garantir que toute classe transmise par valeur est
 * connue des deux côtés — c'est-à-dire rangée dans "common", ce qui est déjà
 * le cas de ce fichier dans son emplacement final ci-dessous.
 */
public class EspeceChien extends Espece {

    private final String raceParticuliere;

    public EspeceChien(int dureeVieMoyenne, String raceParticuliere) {
        super("Chien", dureeVieMoyenne);
        this.raceParticuliere = raceParticuliere;
    }

    public String getRaceParticuliere() {
        return raceParticuliere;
    }
}