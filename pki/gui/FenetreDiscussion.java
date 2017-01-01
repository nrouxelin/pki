package pki.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pki.Message;
import pki.annuaire.Personne;
import pki.client.Client;
import pki.exceptions.CertificatNonTrouveException;
import pki.exceptions.CertificatNonValideException;

/**
 * Fenetre contennant une zone de text pour écrire, une pour visualiser les messages reçus
 * et des menus déroulants pour le choix des destinataires ou des messages
 *
 */
@SuppressWarnings("serial")
public class FenetreDiscussion extends JFrame {

	//Pour voir les messages
	private JTextArea messageVisualise = new JTextArea();
	private JPanel panneauVisualise = new JPanel();
	private JPanel panneauMenuVisualise = new JPanel();
	private JComboBox<Message> menuVisualise = new JComboBox<Message>();

	//Pour envoyer un message
	private JTextArea messageSaisie = new JTextArea();
	private JComboBox<Personne> menuDestinataire = new JComboBox<Personne>();
	private JPanel panneauDestinataire = new JPanel();
	private JPanel panneauEnvoie = new JPanel();
	private JButton boutonEnvoie = new JButton("Envoie");	
	
	//Le client qui a lancé la fenêtre
	Client client;

	/**
	 * Constructeur de la fenêtre de discussion
	 * @param client le client qui s'est connecté ou inscrit
	 */
	public FenetreDiscussion(Client client){
		this.client = client;

	    this.setSize(1200, 500);
		this.setTitle("Discussion - " + client.getUtilisateur());
	    this.setLocationRelativeTo(null);               
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    //Envoie le message et nettoie la zone de saisi
	    boutonEnvoie.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Message m = new Message(client.getUtilisateur(), (Personne) menuDestinataire.getSelectedItem());
	    		try {
					client.envoyerMessage(m, messageSaisie.getText());
		    		messageSaisie.setText("");
				} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
						| CertificatNonTrouveException | IOException | CertificatNonValideException e1) {

					JOptionPane.showMessageDialog(null, "Problème lors de l'envoie du message");
					e1.printStackTrace();
				}
	    	}
	    });
	    
	    //Change le message affiché
	    menuVisualise.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if(menuVisualise.getSelectedItem() != null){
		    		try {
						messageVisualise.setText(client.dechiffrerMessage((Message) menuVisualise.getSelectedItem()));
					} catch (ClassNotFoundException | CertificatNonTrouveException | IOException
							| CertificatNonValideException e1) {
						
						JOptionPane.showMessageDialog(null, "Problème au déchifrage");
						e1.printStackTrace();
					}
	    		}
	    	}
		});
	    
	    //panneau pour voir les messages
	    panneauMenuVisualise.setLayout(new FlowLayout());
	    panneauMenuVisualise.add(new JLabel("Choix du message"));
	    panneauMenuVisualise.add(menuVisualise);

	    panneauVisualise.setLayout(new BorderLayout());
	    panneauVisualise.add(panneauMenuVisualise, BorderLayout.NORTH);
	    panneauVisualise.add(new JScrollPane(messageVisualise), BorderLayout.CENTER);

	    //Fix le texte du message visualisé
	    messageVisualise.setEditable(false);
	    
	    //panneau pour envoyer les messages
	    panneauDestinataire.setLayout(new FlowLayout());
	    panneauDestinataire.add(new JLabel("Choix du destinataire"));
	    panneauDestinataire.add(menuDestinataire);
	    
	    panneauEnvoie.setLayout(new BorderLayout());
	    panneauEnvoie.add(panneauDestinataire, BorderLayout.NORTH);
	    panneauEnvoie.add(new JScrollPane(messageSaisie), BorderLayout.CENTER);
	    panneauEnvoie.add(boutonEnvoie, BorderLayout.SOUTH);
	    
	    this.getContentPane().setLayout(new GridLayout(1, 2));
	    this.getContentPane().add(panneauEnvoie);
	    this.getContentPane().add(panneauVisualise);
	    
	    //lancement du thread permettant de relever les messages régulièremenr
	    Thread t = new Thread(new ThreadRelever());
	    t.start();
	}
	
	/**
	 * Met à jours les menus déroulants pour pouvoir choisir les derniers messages envoyés et destinataires créés
	 */
	private void relever(){
	    //Rafraîchissement des menus déroulants
	    try {
	    	if(menuDestinataire.getItemCount() != client.getAnnuaire().getNbPersonnes()){
				for(Personne pers : client.getAnnuaire().getPersonnes()){
					if(((DefaultComboBoxModel<Personne>)menuDestinataire.getModel()).getIndexOf(pers) == -1)
						menuDestinataire.addItem(pers);
				}
	    	}
	    	
	    	if(menuVisualise.getItemCount() != client.getNbMessages()){
				if(menuVisualise.getItemCount()>0) menuVisualise.removeAllItems();
				for(Message recu : client.getMessages()){
					menuVisualise.addItem(recu);
				}
	    	}
		} catch (RemoteException e1) {
			JOptionPane.showMessageDialog(null, "Problème de récupération des données");
			e1.printStackTrace();
		}
	}
	
	/**
	 * thread pour le chargement des messages à interval régulier
	 */
	class ThreadRelever implements Runnable{

		public void run() {
			while(true){
				relever();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
