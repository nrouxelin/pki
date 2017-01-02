package pki.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pki.Chiffrement;
import pki.Message;
import pki.annuaire.Personne;
import pki.annuaire.ServeurAnnuaire;
import pki.certification.Certificat;
import pki.certification.ServeurCertification;
import pki.exceptions.CertificatNonTrouveException;
import pki.exceptions.CertificatNonValideException;
import pki.exceptions.UtilisateurExistantException;
import pki.gui.FenetreConnexion;
import pki.messagerie.ServeurStockage;

/**
 * logiciel lanc�  par  l'utilisateur final, permet de communiquer avec les diff�rents serveurs
 *
 */
@SuppressWarnings("serial")
public class Client implements Serializable{

	private ServeurAnnuaire annuaire;
	private ServeurStockage messagerie;
	private ServeurCertification certification;
	private Personne utilisateur;
	private Key cleLecture;
	private Key cleSignature;
	private Hashtable<Integer, Key> anciennesClesLecture;
	private String nomFichierTrousseau;
	

	private class TrousseauCles implements Serializable{
		public Key cleLecture;
		public Key cleSignature;
		public Hashtable<Integer, Key> anciennesClesLecture;
		
		public TrousseauCles(Key l, Key s, Hashtable<Integer,Key> anciennesCles){
			cleLecture = l;
			cleSignature = s;
			
			anciennesClesLecture = anciennesCles;
		}
	}
	
	/**
	 * Constructeur par dÃ©faut. 
	 * Initialise les serveurs d'annuaire, de messagerie et de certification Ã  partir des serveurs.
	 * 
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public Client() throws AccessException, RemoteException, NotBoundException{
		ServeurCertification c = (ServeurCertification)LocateRegistry.getRegistry().lookup("certification");
		ServeurAnnuaire a = (ServeurAnnuaire)LocateRegistry.getRegistry().lookup("annuaire");
		ServeurStockage m = (ServeurStockage)LocateRegistry.getRegistry().lookup("stockage");
		
		annuaire = a;
		messagerie = m;
		certification = c;
	}

	/**
	 * Initialise annuaire, messagerie, certification et utilisateur Ã  partir des arguments.
	 * 
	 * @param a le serveur d'annuaire
	 * @param m le serveur de stockage des messages 
	 * @param c le serveur de certification
	 * @param u l'utilisateur
	 */
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u){
		this.annuaire = a;
		this.messagerie = m;
		this.certification = c;
		this.utilisateur = u;
	}
	
	/**
	 * Initialise tous les attributs Ã  partir des arguments.
	 * Utilise le fichier contenant les clefs
	 * 
	 * @param a le serveur d'annuaire
	 * @param m le serveur de stockage des messages 
	 * @param c le serveur de certification
	 * @param u l'utilisateur
	 * @param fichierCles le fichier contenant les clefs
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws CertificatNonTrouveException
	 * @throws UtilisateurExistantException
	 * @throws UtilisateurExistantException
	 * @throws CertificatNonValideException
	 */
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u, File fichierCles)
			throws ClassNotFoundException, IOException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, CertificatNonTrouveException, UtilisateurExistantException,
			UtilisateurExistantException, CertificatNonValideException{
		this(a,m,c,u);
		nomFichierTrousseau = fichierCles.getName();
		TrousseauCles cles = lireCles(fichierCles);
		cleLecture = cles.cleLecture;
		cleSignature = cles.cleSignature;
		if(cles.anciennesClesLecture != null){
			anciennesClesLecture = cles.anciennesClesLecture;
		}else{
			anciennesClesLecture = new Hashtable<Integer, Key>();
		}
		
		
		//Teste l'identitÃ©
		Certificat certificat = certification.getCertificatByPersonne(utilisateur);
		if(verifierIdentite(certificat)){
			LocalDateTime aujourdhui = LocalDateTime.now();
			if(aujourdhui.isAfter(certificat.getDateFin())){
				mettreAJourCertificat(certificat.getId());
			}
		}else{
		//	throw new CertificatNonValideException();
		}
	}
	
	/**
	 * initialise tous les attributs Ã  partir des arguments.
	 * Utilise le nom du fichier contenant les clefs
	 * 
	 * @param a le serveur d'annuaire
	 * @param m le serveur de stockage des messages 
	 * @param c le serveur de certification
	 * @param u l'utilisateur
	 * @param nomFichierCles le nom du fichier contenant les clefs
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws CertificatNonTrouveException
	 * @throws UtilisateurExistantException
	 * @throws UtilisateurExistantException
	 * @throws CertificatNonValideException
	 */
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u, String nomFichierCles)
			throws ClassNotFoundException, IOException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, CertificatNonTrouveException, UtilisateurExistantException, UtilisateurExistantException,
			CertificatNonValideException{
		this(a,m,c,u, new File(nomFichierCles));
	}
	
	/**
	 * Lis la clef contenu dans le fichier
	 * 
	 * @param fichier le fichier contenant les clefs
	 * 
	 * @return le trousseau de clefs
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private TrousseauCles lireCles(File fichier) throws IOException, ClassNotFoundException{
		ObjectInputStream flux = new ObjectInputStream(new FileInputStream(fichier));
		TrousseauCles trousseau = (TrousseauCles) flux.readObject();
		flux.close();
		return trousseau;
	}
	
	/**
	 * V�rifie la correspondance entre l'utilisateur et le certificat
	 * 
	 * @param c le certificat
	 * @return vrai si l'identidé correspond à l'utilisateur
	 * 
	 * @throws RemoteException
	 * @throws CertificatNonTrouveException
	 */
	private boolean verifierIdentite(Certificat c) throws RemoteException, CertificatNonTrouveException{
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		byte[] signature;
		try {
			signature = Chiffrement.chiffrerRSA(uuid.getBytes("UTF-8"),cleSignature);
			signature = Chiffrement.dechiffrerRSA(signature,c.getCleSignature());
			return uuid.equals(new String(signature,"UTF-8"));
		} catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			return false;
		}
		
	}
	
	/**
	 * Transmet un message au serveur
	 * 
	 * @param m le message à envoyer
	 * @param texte le texte du message à envoyer
	 * 
	 * @throws CertificatNonTrouveException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 * @throws CertificatNonValideException
	 */
	public void envoyerMessage(Message m, String texte) throws CertificatNonTrouveException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, CertificatNonValideException{
		if(utilisateur.equals(m.getExpediteur()) && annuaire.estInscrit(m.getDestinataire())){

			Certificat certificatDestinataire = certification.getCertificatByPersonne(m.getDestinataire());
			Chiffrement.chiffrerMessage(m, texte, certificatDestinataire.getCleEcriture());
			Chiffrement.signerMessage(m, cleSignature);
			
			messagerie.enregistrerMessage(m);
		}
	}
	
	/**
	 * Met à jour le certificat
	 * 
	 * @param id l'id du certificat
	 * @throws RemoteException
	 * @throws IOException
	 */
	private void mettreAJourCertificat(int id) throws RemoteException, IOException{
		try {
			
			
			//on enregistre les anciennes clÃ©s
			anciennesClesLecture.put(id,cleLecture);
			
			certification.revoquerCertificat(id);
			//On gÃ©nÃ¨re la clÃ© d'Ã©criture
			KeyPair clesEcriture = Chiffrement.genererClesRSA();
			cleLecture = clesEcriture.getPrivate();
					
			//On gÃ©nÃ¨re la clÃ© de signature
			KeyPair clesSignature = Chiffrement.genererClesRSA();
			cleSignature = clesSignature.getPublic();
			
			//Ã‰criture des clÃ©s
			TrousseauCles trousseau = new TrousseauCles(cleLecture,cleSignature,anciennesClesLecture);
			ecrireCles(trousseau,nomFichierTrousseau);
					
			//On gÃ©nÃ¨re le certificat et on l'ajoute
			Certificat c = new Certificat(utilisateur, clesSignature.getPrivate(), clesEcriture.getPublic());
			certification.ajouterCertificat(c);
		} catch (CertificatNonTrouveException e) {
			e.printStackTrace();
		} catch (UtilisateurExistantException e) {
		}
		
		
	}
	
	/**
	 * inscrit l'utilisateur
	 * 
	 * @param u l'utilisateur à ajouter
	 * @param nomFichier le nom du fichier contenant les clefs
	 * @throws UtilisateurExistantException
	 * @throws IOException
	 * @throws UtilisateurExistantException
	 */
	public void inscrireUtilisateur(Personne u, String nomFichier) throws UtilisateurExistantException, IOException, UtilisateurExistantException{
		utilisateur = u;
		
		annuaire.ajouterPersonne(utilisateur);
		
		//On gÃ©nÃ¨re la clÃ© d'Ã©criture
		KeyPair clesEcriture = Chiffrement.genererClesRSA();
		cleLecture = clesEcriture.getPrivate();
		
		//On gÃ©nÃ¨re la clÃ© de signature
		KeyPair clesSignature = Chiffrement.genererClesRSA();
		cleSignature = clesSignature.getPublic();
		
		//Ã‰criture des clÃ©s
		TrousseauCles trousseau = new TrousseauCles(clesEcriture.getPrivate(),clesSignature.getPublic(),anciennesClesLecture);
		ecrireCles(trousseau, nomFichier);
		
		//On gÃ©nÃ¨re le certificat et on l'ajoute
		Certificat c = new Certificat(utilisateur, clesSignature.getPrivate(), clesEcriture.getPublic());
		certification.ajouterCertificat(c);
	}
	
	/**
	 * Enregistre les clefs dans un fichier
	 * 
	 * @param trousseau le troussseau de clefs
	 * @param nomFichier nom du fichier dans lequel les clefs doivent être enregistrées
	 * @throws IOException
	 */
	private void ecrireCles(TrousseauCles trousseau, String nomFichier) throws IOException{		
		File fichier = new File(nomFichier);
		
		ObjectOutputStream flux = new ObjectOutputStream(new FileOutputStream(fichier));
		flux.writeObject(trousseau);
		flux.close();
	}
	
	/**
	 * D�chiffre un message à l'aide des clefs
	 * 
	 * @param m le message à d�chiffrer
	 * @return le text du message
	 * @throws CertificatNonTrouveException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws CertificatNonValideException
	 */
	public String dechiffrerMessage(Message m) throws CertificatNonTrouveException, ClassNotFoundException, IOException, CertificatNonValideException{
		Certificat certificatExpediteur = certification.getCertificatByPersonneAndDate(m.getExpediteur(),m.getDate());
		Certificat certificatDestinnataire = certification.getCertificatByPersonneAndDate(utilisateur, m.getDate());
		Key cle;
		if(certificatDestinnataire.getId()!=certification.getCertificatByPersonne(utilisateur).getId()){
			cle = anciennesClesLecture.get(certificatDestinnataire.getId());
		}else{
			cle = cleLecture;
		}
		if(Chiffrement.verifierSignature(m, certificatExpediteur.getCleSignature())){
			return Chiffrement.dechiffrerMessage(m, cle);
		}else{
			return "Erreur signature";
		}
	}
	
	/**
	 * Connecte l'utilisateur : assigne une personne et un fichier de clef au client
	 * 
	 * @param utilisateur personne qui sera assignÃ© au client
	 * @param fichierCles Fichier contennant les clefs 
	 * @throws NotBoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws CertificatNonTrouveException
	 * @throws CertificatNonValideException 
	 */
	public void connexion(Personne utilisateur, File fichierCles) 
			throws NotBoundException, ClassNotFoundException, IOException, CertificatNonTrouveException, CertificatNonValideException{
		
		this.utilisateur = utilisateur;
		
		nomFichierTrousseau = fichierCles.getName();
		TrousseauCles cles = lireCles(fichierCles);
		cleLecture = cles.cleLecture;
		cleSignature = cles.cleSignature;
		if(cles.anciennesClesLecture != null){
			anciennesClesLecture = cles.anciennesClesLecture;
		}else{
			anciennesClesLecture = new Hashtable<Integer, Key>();
		}
		
		
		//Teste l'identitÃ©
		Certificat certificat = certification.getCertificatByPersonne(utilisateur);
		if(verifierIdentite(certificat)){
			LocalDateTime aujourdhui = LocalDateTime.now();
			if(aujourdhui.isAfter(certificat.getDateFin())){
				mettreAJourCertificat(certificat.getId());
			}
		}else{
			throw new CertificatNonValideException();
		}
	}
		
	//getters
	
	/**
	 * @return
	 * @throws RemoteException
	 */
	public ArrayList<Message> getMessages() throws RemoteException{
		return messagerie.getMessages(utilisateur);
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public int getNbMessages() throws RemoteException{
		return messagerie.getNbMessages(utilisateur);
	}
	
	/**
	 * @return l'utilisateur associ�    l'instance de Client
	 */
	public Personne getUtilisateur(){
		return utilisateur;
	}
	
	/**
	 * @return le serveur d'annuaire
	 */
	public ServeurAnnuaire getAnnuaire(){
		return annuaire;
	}
	
	/**
	 * CrÃ©e un client, lance une fenÃªtre de connexion et l'affiche.
	 * 
	 * @param args
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws AccessException 
	 */
	public static void main(String[] args) throws AccessException, RemoteException, NotBoundException{
		Client client = new Client();
		
		FenetreConnexion fenConnex = new FenetreConnexion(client);
		fenConnex.setVisible(true);
	}
}
