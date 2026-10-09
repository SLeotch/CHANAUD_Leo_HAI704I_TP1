package veterinaire;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class VeterinaireGUI extends JFrame {

    private final ClientLogic logique;

    // Liste des patients
    private final DefaultListModel<String> modelePatients;
    private final JList<String> listePatients;

    // Informations du patient sélectionné
    private final JTextArea informationsPatient;

    // Zone des alertes
    private final JTextArea zoneAlertes;

    public VeterinaireGUI(ClientLogic logique) {
        this.logique = logique;

        setTitle("Cabinet vétérinaire");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // =========================
        // TITRE
        // =========================

        JLabel titre = new JLabel(
                "CABINET VÉTÉRINAIRE",
                SwingConstants.CENTER
        );

        titre.setBorder(BorderFactory.createEmptyBorder(
                15, 10, 15, 10
        ));

        add(titre, BorderLayout.NORTH);

        // =========================
        // PARTIE CENTRALE
        // =========================

        JPanel panneauCentral = new JPanel(
                new GridLayout(1, 2, 10, 10)
        );

        panneauCentral.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        // -------------------------
        // GAUCHE : patients
        // -------------------------

        JPanel panneauPatients = new JPanel(new BorderLayout(5, 5));

        panneauPatients.setBorder(
                BorderFactory.createTitledBorder("Patients")
        );

        modelePatients = new DefaultListModel<>();
        listePatients = new JList<>(modelePatients);

        JScrollPane defilementPatients =
                new JScrollPane(listePatients);

        panneauPatients.add(
                defilementPatients,
                BorderLayout.CENTER
        );

        JPanel boutonsPatients = new JPanel();

        JButton boutonActualiser =
                new JButton("Actualiser");

        JButton boutonRechercher =
                new JButton("Rechercher");

        JButton boutonAjouter =
                new JButton("Ajouter patient");

        JButton boutonRetirer = new JButton("Retirer patient");

        boutonsPatients.add(boutonActualiser);
        boutonsPatients.add(boutonRechercher);
        boutonsPatients.add(boutonAjouter);
        boutonsPatients.add(boutonRetirer);

        panneauPatients.add(
                boutonsPatients,
                BorderLayout.SOUTH
        );

        // -------------------------
        // DROITE : informations
        // -------------------------

        JPanel panneauInformations =
                new JPanel(new BorderLayout(5, 5));

        panneauInformations.setBorder(
                BorderFactory.createTitledBorder(
                        "Informations du patient"
                )
        );

        informationsPatient = new JTextArea();

        informationsPatient.setEditable(false);
        informationsPatient.setLineWrap(true);
        informationsPatient.setWrapStyleWord(true);

        panneauInformations.add(
                new JScrollPane(informationsPatient),
                BorderLayout.CENTER
        );

        JPanel boutonsInformations = new JPanel();

        JButton boutonDossier =
                new JButton("Consulter dossier");

        JButton boutonEtat =
                new JButton("Modifier état");

        JButton boutonObservation =
                new JButton("Ajouter observation");

        boutonsInformations.add(boutonDossier);
        boutonsInformations.add(boutonEtat);
        boutonsInformations.add(boutonObservation);

        panneauInformations.add(
                boutonsInformations,
                BorderLayout.SOUTH
        );

        panneauCentral.add(panneauPatients);
        panneauCentral.add(panneauInformations);

        add(panneauCentral, BorderLayout.CENTER);

        // =========================
        // ALERTES
        // =========================

        JPanel panneauBas = new JPanel(new BorderLayout(5, 5));

        panneauBas.setBorder(
                BorderFactory.createTitledBorder("Alertes")
        );

        zoneAlertes = new JTextArea(5, 50);

        zoneAlertes.setEditable(false);
        zoneAlertes.setLineWrap(true);
        zoneAlertes.setWrapStyleWord(true);

        panneauBas.add(
                new JScrollPane(zoneAlertes),
                BorderLayout.CENTER
        );

        JPanel boutonsAlertes = new JPanel();

        JButton boutonAbonner =
                new JButton("S'abonner");

        JButton boutonDesabonner =
                new JButton("Se désabonner");

        JButton boutonTest = new JButton("Test : +99 patients");

        boutonsAlertes.add(boutonAbonner);
        boutonsAlertes.add(boutonDesabonner);
        boutonsAlertes.add(boutonTest);

        panneauBas.add(
                boutonsAlertes,
                BorderLayout.SOUTH
        );

        add(panneauBas, BorderLayout.SOUTH);

        // =========================
        // ÉVÉNEMENTS
        // =========================

        boutonActualiser.addActionListener(
                e -> actualiserPatients()
        );

        boutonRechercher.addActionListener(
                e -> rechercherPatient()
        );

        boutonAjouter.addActionListener(
                e -> ajouterPatient()
        );

        boutonRetirer.addActionListener(
                e -> retirerPatient()
        );

        boutonDossier.addActionListener(
                e -> consulterDossier()
        );

        boutonEtat.addActionListener(
                e -> modifierEtat()
        );

        boutonObservation.addActionListener(
                e -> ajouterObservation()
        );

        boutonAbonner.addActionListener(
                e -> abonner()
        );

        boutonDesabonner.addActionListener(
                e -> desabonner()
        );

        boutonTest.addActionListener(
                e -> testerAlerte()
        );

        listePatients.addListSelectionListener(
                e -> afficherPatientSelectionne()
        );

        // =========================
        // ALERTES RMI
        // =========================

        logique.setEcouteurAlerte(message -> {

            // Le callback RMI n'est pas forcément
            // exécuté sur le thread graphique.
            SwingUtilities.invokeLater(() -> {
                zoneAlertes.append(message + "\n");
            });
        });

        // Première récupération
        actualiserPatients();
    }

    // =========================================================
    // ACTUALISER LES PATIENTS
    // =========================================================

    private void actualiserPatients() {

        lancerTache(
                () -> logique.listerPatients(),
                patients -> {

                    modelePatients.clear();

                    for (InfoAnimal animal : patients) {
                        modelePatients.addElement(
                                animal.getNom()
                        );
                    }
                }
        );
    }

    // =========================================================
    // RECHERCHER
    // =========================================================

    private void rechercherPatient() {
        String nom = JOptionPane.showInputDialog(
                this,
                "Nom du patient :"
        );
        if (nom == null || nom.isBlank()) {
            return;
        }
        lancerTache(
                () -> logique.rechercherParNom(nom),
                animal -> {
                    if (animal == null) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Aucun patient nommé '" + nom + "'."
                        );
                        return;
                    }
                    afficherInformations(animal);
                    listePatients.setSelectedValue(animal.getNom(), true);
                }
        );
    }

    // =========================================================
    // AJOUTER PATIENT
    // =========================================================

    private void ajouterPatient() {

        JTextField nom =
                new JTextField();

        JTextField maitre =
                new JTextField();

        JTextField race =
                new JTextField();

        JTextField espece =
                new JTextField();

        JTextField duree =
                new JTextField();

        JPanel panneau =
                new JPanel(new GridLayout(0, 2, 5, 5));

        panneau.add(new JLabel("Nom :"));
        panneau.add(nom);

        panneau.add(new JLabel("Maître :"));
        panneau.add(maitre);

        panneau.add(new JLabel("Race :"));
        panneau.add(race);

        panneau.add(new JLabel("Espèce :"));
        panneau.add(espece);

        panneau.add(new JLabel("Espérance de vie :"));
        panneau.add(duree);

        int resultat = JOptionPane.showConfirmDialog(
                this,
                panneau,
                "Ajouter un patient",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (resultat != JOptionPane.OK_OPTION) {
            return;
        }

        // ajout supplémentaire non demandé : pas de champs vides
        if (nom.getText().isBlank() || maitre.getText().isBlank() || race.getText().isBlank() || espece.getText().isBlank() || duree.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Champ manquant", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {

            int dureeVie =
                    Integer.parseInt(duree.getText());

            Espece nouvelleEspece =
                    new Espece(
                            espece.getText(),
                            dureeVie
                    );

            lancerTache(
                    () -> logique.enregistrerUnPatient(
                            nom.getText(),
                            maitre.getText(),
                            race.getText(),
                            nouvelleEspece
                    ),
                    animal -> {

                        JOptionPane.showMessageDialog(
                                this,
                                "Patient enregistré."
                        );

                        actualiserPatients();
                    }
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "L'espérance de vie doit être un nombre."
            );
        }
    }

    private void retirerPatient() {
        String nom = listePatients.getSelectedValue();
        if(nom == null){
            afficherAucunPatientSelectionne();
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this, "Retirer définitivement '" + nom + "' ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(confirmation !=  JOptionPane.YES_NO_OPTION) return;
        lancerTache(
                () -> logique.retirerPatient(nom),
                supprime -> {
                        if (Boolean.TRUE.equals(supprime)) {
                                informationsPatient.setText("");
                                actualiserPatients();
                        } else {
                                JOptionPane.showMessageDialog(this, "Patient introuvable.");
                        }
                }
        );
    }

    // =========================================================
    // AFFICHER LE PATIENT SÉLECTIONNÉ
    // =========================================================

    private void afficherPatientSelectionne() {

        String nom =
                listePatients.getSelectedValue();

        if (nom == null) {
            return;
        }

        lancerTache(
                () -> logique.rechercherParNom(nom),
                this::afficherInformations
        );
    }

    private void afficherInformations(InfoAnimal animal) {

        if (animal == null) {
            return;
        }

        String texte =
                "Nom : " + animal.getNom() + "\n"
                + "Maître : " + animal.getNomMaitre() + "\n"
                + "Race : " + animal.getRace() + "\n"
                + "Espèce : "
                + animal.getEspece().getNom() + "\n"
                + "Espérance de vie : "
                + animal.getEspece().getEsperanceDeVie()
                + " ans";

        informationsPatient.setText(texte);
    }

    // =========================================================
    // DOSSIER
    // =========================================================

    private void consulterDossier() {

        String nom =
                listePatients.getSelectedValue();

        if (nom == null) {
            afficherAucunPatientSelectionne();
            return;
        }

        lancerTache(
                () -> logique.consulterDossier(nom),
                dossier -> {

                    StringBuilder texte =
                            new StringBuilder();

                    texte.append(
                            "État de santé :\n"
                    );

                    texte.append(
                            dossier.getEtatSante()
                    );

                    texte.append(
                            "\n\nHistorique :\n"
                    );

                    for (String observation :
                            dossier.getHistorique()) {

                        texte.append(
                                "- "
                        );

                        texte.append(
                                observation
                        );

                        texte.append("\n");
                    }

                    informationsPatient.setText(
                            texte.toString()
                    );
                }
        );
    }

    // =========================================================
    // MODIFIER ÉTAT
    // =========================================================

    private void modifierEtat() {

        String nom =
                listePatients.getSelectedValue();

        if (nom == null) {
            afficherAucunPatientSelectionne();
            return;
        }

        String nouvelEtat =
                JOptionPane.showInputDialog(
                        this,
                        "Nouvel état de santé :"
                );

        if (nouvelEtat == null
                || nouvelEtat.isBlank()) {

            return;
        }

        lancerTache(
                () -> {
                    logique.mettreAJourEtatSante(
                            nom,
                            nouvelEtat
                    );
                    return logique.consulterDossier(nom);
                },
                resultat -> {
                        afficherDossier(resultat);

                    JOptionPane.showMessageDialog(
                            this,
                            "État de santé mis à jour."
                    );
                }
        );
    }

    // =========================================================
    // AJOUTER OBSERVATION
    // =========================================================

    private void ajouterObservation() {

        String nom =
                listePatients.getSelectedValue();

        if (nom == null) {
            afficherAucunPatientSelectionne();
            return;
        }

        String observation =
                JOptionPane.showInputDialog(
                        this,
                        "Observation :"
                );

        if (observation == null
                || observation.isBlank()) {

            return;
        }

        lancerTache(
                () -> {
                    logique.ajouterObservation(
                            nom,
                            observation
                    );
                    return logique.consulterDossier(nom);
                },
                resultat -> {
                    afficherDossier(resultat);

                    JOptionPane.showMessageDialog(
                            this,
                            "Observation ajoutée."
                    );
                }
        );
    }

    // =========================================================
    // ABONNEMENT
    // =========================================================

    private void abonner() {
        if (logique.estAbonne()) {
                JOptionPane.showMessageDialog(this, "Abonnement déjà effectué.");
                return;
        }
        lancerTache(() -> { logique.sAbonner(); return null; },
                resultat -> JOptionPane.showMessageDialog(this, "Abonnement aux alertes effectué."));
        }

    private void desabonner() {
        if (!logique.estAbonne()) {
                JOptionPane.showMessageDialog(this, "Désabonnement déjà effectué.");
                return;
        }
        lancerTache(() -> { logique.seDesabonner(); return null; },
                resultat -> JOptionPane.showMessageDialog(this, "Désabonnement effectué."));
        }

    // =========================================================
    // OUTILS
    // =========================================================

    private void afficherAucunPatientSelectionne() {

        JOptionPane.showMessageDialog(
                this,
                "Sélectionnez d'abord un patient."
        );
    }

    private <T> void lancerTache(
            Tache<T> tache,
            ActionResultat<T> action) {

        SwingWorker<T, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected T doInBackground()
                            throws Exception {

                        return tache.executer();
                    }

                    @Override
                    protected void done() {

                        try {

                            T resultat = get();

                            action.executer(
                                    resultat
                            );

                        } catch (Exception e) {

                            Throwable cause =
                                    e.getCause();

                            String message =
                                    cause != null
                                    ? cause.getMessage()
                                    : e.getMessage();

                            JOptionPane.showMessageDialog(
                                    VeterinaireGUI.this,
                                    "Échec de l'opération : "
                                            + message,
                                    "Erreur",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };

        worker.execute();
    }

    private void testerAlerte() {
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Créer 99 patients de test ? (utile pour déclencher les seuils d'alerte)",
                "Test", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) return;

        lancerTache(
                () -> { logique.ajouterPatientdeTest(99); return null; },
                resultat -> {
                JOptionPane.showMessageDialog(this, "99 patients de test créés.");
                actualiserPatients();
                }
        );
    }

    private void afficherDossier(InfoDossier dossier) {

        StringBuilder texte =
                new StringBuilder();

        texte.append("État de santé :\n");
        texte.append(dossier.getEtatSante());

        texte.append("\n\nHistorique :\n");

        for (String observation : dossier.getHistorique()) {
                texte.append("- ");
                texte.append(observation);
                texte.append("\n");
        }

        informationsPatient.setText(
                texte.toString()
        );
        }

    @FunctionalInterface
    private interface Tache<T> {
        T executer() throws Exception;
    }

    @FunctionalInterface
    private interface ActionResultat<T> {
        void executer(T resultat);
    }
}