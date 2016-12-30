/**
 * 
 */
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
 * @author Meuleman
 *
 */
@SuppressWarnings("serial")
public class FenetreDiscussion extends JFrame {

	//Pour voir les messages
	private JTextArea messageVisualise = new JTextArea();
	private JPanel panneauVisualise = new JPanel();
	private JPanel panneauMenuVisualise = new JPanel();
	private JComboBox<Message> menuVisualise = new JComboBox<Message>();
	private JButton boutonRefresh = new JButton("Relever");	

	//Pour envoyer un message
	private JTextArea messageEnvoie = new JTextArea();
	private JComboBox<Personne> menuDestinataire = new JComboBox<Personne>();
	private JPanel panneauDestinataire = new JPanel();
	private JPanel panneauEnvoie = new JPanel();
	private JButton boutonEnvoie = new JButton("Envoie");	
	
	//Le client qui a lancé la fenêtre
	Client client;

	public FenetreDiscussion(Client client){
		this.client = client;

	    this.setSize(1200, 500);
		this.setTitle("Discussion");
	    this.setLocationRelativeTo(null);               
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    
	    boutonEnvoie.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Message m = new Message(client.getUtilisateur(), (Personne) menuDestinataire.getSelectedItem());
	    		try {
					client.envoyerMessage(m, messageEnvoie.getText());
				} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
						| CertificatNonTrouveException | IOException | CertificatNonValideException e1) {

					JOptionPane.showMessageDialog(null, "Problème lors de l'envoie du message");
					e1.printStackTrace();
				}
	    		messageEnvoie.setText("");
	    	}
	    });
	    
	    boutonRefresh.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		relever();
	    	}
	    });
	    
	    menuVisualise.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		try {
					messageVisualise.setText(client.dechiffrerMessage((Message) menuVisualise.getSelectedItem()));
				} catch (ClassNotFoundException | CertificatNonTrouveException | IOException
						| CertificatNonValideException e1) {
					
					JOptionPane.showMessageDialog(null, "Problème au déchifrage");
					e1.printStackTrace();
				}
	    	}
		});

	    relever();
	    
	    //panneau pour voir les messages
	    panneauMenuVisualise.setLayout(new FlowLayout());
	    panneauMenuVisualise.add(new JLabel("Choix du message à visualiser"));
	    panneauMenuVisualise.add(menuVisualise);

	    panneauVisualise.setLayout(new BorderLayout());
	    panneauVisualise.add(panneauMenuVisualise, BorderLayout.NORTH);
	    panneauVisualise.add(new JScrollPane(messageVisualise), BorderLayout.CENTER);
	    panneauVisualise.add(boutonRefresh, BorderLayout.SOUTH);

	    //panneau pour envoyer les messages
	    panneauDestinataire.setLayout(new FlowLayout());
	    panneauDestinataire.add(new JLabel("Choix du destinataire"));
	    panneauDestinataire.add(menuDestinataire);
	    
	    panneauEnvoie.setLayout(new BorderLayout());
	    panneauEnvoie.add(panneauDestinataire, BorderLayout.NORTH);
	    panneauEnvoie.add(new JScrollPane(messageEnvoie), BorderLayout.CENTER);
	    panneauEnvoie.add(boutonEnvoie, BorderLayout.SOUTH);
	    
	    this.getContentPane().setLayout(new GridLayout(1, 2));
	    this.getContentPane().add(panneauEnvoie);
	    this.getContentPane().add(panneauVisualise);
	}
	
	private void relever(){
	    //Rafraîchissement des menus déroulants
	    try {
			for(Personne pers : client.getAnnuaire().getPersonnes()){
				if(((DefaultComboBoxModel<Personne>)menuDestinataire.getModel()).getIndexOf(pers) == -1)
					menuDestinataire.addItem(pers);
			}
			for(Message recu : client.getMessages()){
				if(((DefaultComboBoxModel<Message>)menuVisualise.getModel()).getIndexOf(recu) == -1)
					menuVisualise.addItem(recu);
			}
		} catch (RemoteException e1) {
			JOptionPane.showMessageDialog(null, "Problème de connexion");
			e1.printStackTrace();
		}
	}
}
