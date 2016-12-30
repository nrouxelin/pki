package pki.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.security.InvalidKeyException;
import java.security.KeyPair;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pki.Chiffrement;
import pki.annuaire.Personne;
import pki.annuaire.ServeurAnnuaire;
import pki.certification.Certificat;
import pki.certification.ServeurCertification;
import pki.client.Client;
import pki.exceptions.CertificatNonTrouveException;
import pki.exceptions.CertificatNonValideException;
import pki.exceptions.UtilisateurExistantException;
import pki.messagerie.ServeurStockage;

/**
 * Fenêtre avec permettant de s'identifier ou de créer un nouvel utilisateur.
 * Lance automatiquement la fenêtre de discussion une fois cette étape effectuée.
 */
@SuppressWarnings("serial")
public class FenetreConnexion extends JFrame {

	//Permet de choisir le fichier contenant la clef de connexion
	private JFileChooser selectFichier = new JFileChooser();

	//affiche le nom du fichier sélectionné
	private JLabel nomFichier = new JLabel();
	private JPanel panneauFichier = new JPanel();
	
	//JPanel contenant les boutons
	private JPanel panneauBoutons = new JPanel();
	
	//Permet d'entrer les noms et prénoms
	private JTextField nom = new JTextField();
	private JTextField prenom = new JTextField();
	private JPanel panneauNom = new JPanel();
	private JPanel panneauPrenom = new JPanel();

	private JButton boutonFichier = new JButton("Choix du fichier");
	private JButton boutonConnexion = new JButton("Connexion");
	private JButton boutonInscription = new JButton("Inscription et enregistrement la clef de connexion");
	
	//Le client qui a lancé la fenêtre
	Client client;
	
	/**
	 * Constructeur de la fenêtre de connexion
	 * @param client le client qui devra se connecter ou inscrire
	 */
	public FenetreConnexion(Client client){
		this.client = client;
		
	    this.setSize(600, 250);
		this.setTitle("Connexion");
	    this.setLocationRelativeTo(null);               
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    //Liaison des actions aux boutons
	    boutonFichier.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		selectFichier.showOpenDialog(null);
	    		
	    		if(selectFichier.getSelectedFile() != null){
		    		nomFichier.setText(selectFichier.getSelectedFile().toString());
	    		}
	    	}
	    });

	    boutonConnexion.addActionListener(new ConnexionListener(this));
	    
	    boutonInscription.addActionListener(new InscriptionListener(this));
	      
	    //Création des JPanel pour la fenêtre
	    panneauFichier.setLayout(new FlowLayout());
	    panneauFichier.add(nomFichier);
	    
	    nom.setPreferredSize(new Dimension(300, 30));
	    prenom.setPreferredSize(new Dimension(300, 30));

	    panneauNom.setLayout(new FlowLayout());
	    panneauNom.add(new JLabel("Nom"));
	    panneauNom.add(nom);

	    panneauPrenom.setLayout(new FlowLayout());
	    panneauPrenom.add(new JLabel("Prenom"));
	    panneauPrenom.add(prenom);

	    panneauBoutons.setLayout(new FlowLayout());
	    panneauBoutons.add(boutonFichier);
	    panneauBoutons.add(boutonConnexion);
	    panneauBoutons.add(boutonInscription);

	    //Ajout des boutons et le nom du fichier choisis
	    this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
	    this.getContentPane().add(panneauFichier);
	    this.getContentPane().add(panneauNom);
	    this.getContentPane().add(panneauPrenom);
	    this.getContentPane().add(panneauBoutons);
	}
	
	//getters
	public JFileChooser getSelectFichier(){
		return selectFichier;
	}

	public JTextField getNom(){
		return nom;
	}

	public JTextField getPrenom(){
		return prenom;
	}
	
	public Client getClient(){
		return client;
	}
	
	//setters
	public void setClient(Client client){
		this.client = client;
	}
	
}

/**
 * ActionListener pour le bouton de connexion
 * Contrôle de la fenêtre de connexion et du client
 */
class ConnexionListener implements ActionListener{
	private FenetreConnexion fenConnex;
	
	/**
	 * Constructeur de l'ActionListener
	 * @param fenConnex Fenêtre dans lequel le bouton est placé
	 */
	public ConnexionListener(FenetreConnexion fenConnex){
		this.fenConnex = fenConnex;
	}
	
	/**
	 * Connecte et lance la fenêtre de discussion si le fichier est valide
	 */
	public void actionPerformed(ActionEvent e){
		if(fenConnex.getSelectFichier().getSelectedFile() != null){
			try {
				ServeurCertification certification = (ServeurCertification)LocateRegistry.getRegistry().lookup("certification");
				ServeurAnnuaire annuaire = (ServeurAnnuaire)LocateRegistry.getRegistry().lookup("annuaire");
				ServeurStockage messagerie = (ServeurStockage)LocateRegistry.getRegistry().lookup("stockage");
				
				Personne utlilisateur = new Personne(fenConnex.getNom().getText(), fenConnex.getPrenom().getText());
				
				Certificat c = certification.getCertificatByPersonne(utlilisateur);
				KeyPair kp = Chiffrement.genererClesRSA();
				Chiffrement.signerCertificat(c,kp.getPublic());
				if(Chiffrement.verifierSignature(c, kp.getPrivate()) && 
						Chiffrement.verifierSignature(certification.getCertificatByPersonne(utlilisateur), certification.getClePublique()))
				{
					fenConnex.setClient(new Client(annuaire, messagerie, certification, utlilisateur, 
							fenConnex.getSelectFichier().getSelectedFile()));
									
					FenetreDiscussion fenDisc = new FenetreDiscussion(fenConnex.getClient());
					fenDisc.setVisible(true);
					fenConnex.setVisible(false);

				}
				else{
					JOptionPane.showMessageDialog(null, "Clef ou noms invalides");
				}
			}catch (NotBoundException | IOException | InvalidKeyException | ClassNotFoundException | 
					IllegalBlockSizeException | BadPaddingException | CertificatNonTrouveException | 
					UtilisateurExistantException | CertificatNonValideException e1) {
				JOptionPane.showMessageDialog(null, "Problème de connexion");
				e1.printStackTrace();
			}
		}
	}
}

/**
 * ActionListener pour le bouton d'inscription
 * Contrôle de la fenêtre de connexion et du client
 */
class InscriptionListener implements ActionListener{
	private FenetreConnexion fenConnex;
	
	/**
	 * Constructeur de l'ActionListener
	 * @param fenConnex Fenêtre dans lequel le bouton est placé
	 */
	public InscriptionListener(FenetreConnexion fenConnex){
		this.fenConnex = fenConnex;
	}
	
	/**
	 * Crée l'utilisateur et enregistre une clef si l'utilisateur n'existe pas encore.
	 */
	public void actionPerformed(ActionEvent e) {
		fenConnex.getSelectFichier().showSaveDialog(null);
		if(fenConnex.getSelectFichier().getSelectedFile() != null){
			Personne utlilisateur = new Personne(fenConnex.getNom().getText(), fenConnex.getPrenom().getText());

			try {
			ServeurCertification certification = (ServeurCertification)LocateRegistry.getRegistry().lookup("certification");
			ServeurAnnuaire annuaire = (ServeurAnnuaire)LocateRegistry.getRegistry().lookup("annuaire");
			ServeurStockage messagerie = (ServeurStockage)LocateRegistry.getRegistry().lookup("stockage");

			
			fenConnex.setClient(new Client(annuaire, messagerie, certification, utlilisateur));
			
			fenConnex.getClient().inscrireUtilisateur(fenConnex.getSelectFichier().getSelectedFile().getAbsolutePath());

			
			FenetreDiscussion fenDisc = new FenetreDiscussion(fenConnex.getClient());
			fenDisc.setVisible(true);
			fenConnex.setVisible(false);

			} catch (IOException | NotBoundException e1) {
				JOptionPane.showMessageDialog(null, "Problème d'inscription");
				e1.printStackTrace();
			} catch (UtilisateurExistantException e1) {
				JOptionPane.showMessageDialog(null, "L'utilisateur " + utlilisateur + " existe déjà");
				e1.printStackTrace();
			}
		}
	}
}

