package pki.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import pki.messagerie.ServeurStockage;

@SuppressWarnings("serial")
public class Client implements Serializable{

	private ServeurAnnuaire annuaire;
	private ServeurStockage messagerie;
	private ServeurCertification certification;
	private Personne utilisateur;
	private Key cleLecture;
	private Key cleSignature;
	

	private class TrousseauCles implements Serializable{
		public Key cleLecture;
		public Key cleSignature;
		
		public TrousseauCles(Key l, Key s){
			cleLecture = l;
			cleSignature = s;
		}
	}
	
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u){
		annuaire = a;
		messagerie = m;
		certification = c;
		utilisateur = u;
	}
	
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u, File fichierCles)
			throws ClassNotFoundException, IOException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, CertificatNonTrouveException, UtilisateurExistantException,
			UtilisateurExistantException, CertificatNonValideException{
		this(a,m,c,u);
		TrousseauCles cles = lireCles(fichierCles);
		cleLecture = cles.cleLecture;
		cleSignature = cles.cleSignature;
		
		//Teste l'identité
		Certificat certificat = certification.getCertificatByPersonne(utilisateur);
		if(!Chiffrement.verifierSignature(certificat, certification.getClePublique())){
			//throw new CertificatNonValideException();
		}
		if(verifierIdentite(certificat)){
			LocalDateTime aujourdhui = LocalDateTime.now();
			if(aujourdhui.isAfter(certificat.getDateFin())){
				mettreAJourCertificat(certificat.getId());
			}
		}else{
		//	throw new CertificatNonValideException();
		}
	}
	
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u, String nomFichierCles)
			throws ClassNotFoundException, IOException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, CertificatNonTrouveException, UtilisateurExistantException, UtilisateurExistantException,
			CertificatNonValideException{
		this(a,m,c,u, new File(nomFichierCles));
	}
	
	private TrousseauCles lireCles(File fichier) throws IOException, ClassNotFoundException{
		ObjectInputStream flux = new ObjectInputStream(new FileInputStream(fichier));
		TrousseauCles trousseau = (TrousseauCles) flux.readObject();
		flux.close();
		return trousseau;
	}
	
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
	
	public void envoyerMessage(Message m, String texte) throws CertificatNonTrouveException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, CertificatNonValideException{
		if(utilisateur.equals(m.getExpediteur()) && annuaire.estInscrit(m.getDestinataire())){

			Certificat certificatDestinataire = certification.getCertificatByPersonne(m.getDestinataire());
			if(!Chiffrement.verifierSignature(certificatDestinataire, certification.getClePublique())){
				//throw new CertificatNonValideException();
			}
			
			Chiffrement.chiffrerMessage(m, texte, certificatDestinataire.getCleEcriture());
			Chiffrement.signerMessage(m, cleSignature);
			
			messagerie.enregistrerMessage(m);
		}
	}
	
	private void mettreAJourCertificat(int id) throws RemoteException, IOException{
		try {
			
			
			//on enregistre les anciennes clés
			String prefixe = "client/"+utilisateur+"/";
			prefixe = prefixe.replaceAll("\\s", "_");
			String nomRepertoire = id+"/";
			File repertoire = new File(prefixe+nomRepertoire);
			repertoire.mkdirs();
			
			File source = new File(prefixe+"trousseau.key");
			File destination = new File(prefixe+nomRepertoire+"trousseau.key");
			if(!source.renameTo(destination)){
				throw new IOException();
			}
			
			certification.revoquerCertificat(id);
			//On génère la clé d'écriture
			KeyPair clesEcriture = Chiffrement.genererClesRSA();
			cleLecture = clesEcriture.getPrivate();
					
			//On génère la clé de signature
			KeyPair clesSignature = Chiffrement.genererClesRSA();
			cleSignature = clesSignature.getPublic();
			
			//Écriture des clés
			TrousseauCles trousseau = new TrousseauCles(cleLecture,cleSignature);
			ecrireCles(trousseau,"trousseau.key");
					
			//On génère le certificat et on l'ajoute
			Certificat c = new Certificat(utilisateur, clesSignature.getPrivate(), clesEcriture.getPublic());
			certification.ajouterCertificat(c);
		} catch (CertificatNonTrouveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UtilisateurExistantException e) {
		}
		
		
	}
	
	public void inscrireUtilisateur(String nomFichier) throws UtilisateurExistantException, IOException, UtilisateurExistantException{
		annuaire.ajouterPersonne(utilisateur);
		
		//On génère la clé d'écriture
		KeyPair clesEcriture = Chiffrement.genererClesRSA();
		cleLecture = clesEcriture.getPrivate();
		
		//On génère la clé de signature
		KeyPair clesSignature = Chiffrement.genererClesRSA();
		cleSignature = clesSignature.getPublic();
		
		//Écriture des clés
		TrousseauCles trousseau = new TrousseauCles(clesEcriture.getPrivate(),clesSignature.getPublic());
		ecrireCles(trousseau, nomFichier);
		
		//On génère le certificat et on l'ajoute
		Certificat c = new Certificat(utilisateur, clesSignature.getPrivate(), clesEcriture.getPublic());
		certification.ajouterCertificat(c);
	}
	
	private void ecrireCles(TrousseauCles trousseau, String nomFichier) throws IOException{
		/*String nomRepertoire = "client/"+utilisateur+"/";
		nomRepertoire = nomRepertoire.replaceAll("\\s", "_");
		File repertoire = new File(nomRepertoire);
		if(!(repertoire.exists() && repertoire.isDirectory())){
			repertoire.mkdirs();
		}*/
		
		File fichier = new File(nomFichier);
		
		ObjectOutputStream flux = new ObjectOutputStream(new FileOutputStream(fichier));
		flux.writeObject(trousseau);
		flux.close();
	}
	
	public ArrayList<Message> getMessages() throws RemoteException{
		return messagerie.getMessages(utilisateur);
	}

	public int getNbMessages() throws RemoteException{
		return messagerie.getNbMessages(utilisateur);
	}

	public String dechiffrerMessage(Message m) throws CertificatNonTrouveException, ClassNotFoundException, IOException, CertificatNonValideException{
		Certificat certificatExpediteur = certification.getCertificatByPersonneAndDate(m.getExpediteur(),m.getDate());
		Certificat certificatDestinnataire = certification.getCertificatByPersonneAndDate(utilisateur, m.getDate());
		if(!(Chiffrement.verifierSignature(certificatExpediteur, certification.getClePublique()) && 
				Chiffrement.verifierSignature(certificatDestinnataire, certification.getClePublique()))){
			//throw new CertificatNonValideException();
		}
		Key cle;
		if(certificatDestinnataire.getId()!=certification.getCertificatByPersonne(utilisateur).getId()){
			String nomFichier = "client/"+utilisateur+"/"+certificatDestinnataire.getId()+"/cleLecture.key";
			nomFichier = nomFichier.replaceAll("\\s", "_");
			cle = lireCles(new File(nomFichier)).cleLecture;
		}else{
			cle = cleLecture;
		}
		if(Chiffrement.verifierSignature(m, certificatExpediteur.getCleSignature())){
			return Chiffrement.dechiffrerMessage(m, cle);
		}else{
			return "Erreur signature";
		}
	}
	
	//getters
	public Personne getUtilisateur(){
		return utilisateur;
	}
	
	public ServeurAnnuaire getAnnuaire(){
		return annuaire;
	}

	/*public static void main(String[] args) throws Exception{
		Personne jeanmichel = new Personne("Connard","Jean-Michel");
		Personne norbert = new Personne("Gérard","Norbert");
		
		ServeurCertification certification = (ServeurCertification)LocateRegistry.getRegistry().lookup("certification");
		ServeurAnnuaire annuaire = (ServeurAnnuaire)LocateRegistry.getRegistry().lookup("annuaire");
		ServeurStockage messagerie = (ServeurStockage)LocateRegistry.getRegistry().lookup("stockage");

		
		
		Client client1;
		Client client2=null;
		int mode = 12;
		if(mode==1){
			Certificat c = certification.getCertificatByPersonne(jeanmichel);
			KeyPair kp = Chiffrement.genererClesRSA();
			Chiffrement.signerCertificat(c,kp.getPublic());
			System.out.println(Chiffrement.verifierSignature(c, kp.getPrivate()));
			

			System.out.println("norbert : "+Chiffrement.verifierSignature(certification.getCertificatByPersonne(norbert), certification.getClePublique()));
			System.out.println("jm : "+Chiffrement.verifierSignature(certification.getCertificatByPersonne(jeanmichel), certification.getClePublique()));

			client2 = new Client(annuaire, messagerie, certification, jeanmichel, "client/Connard_Jean-Michel/trousseau.key");
			client1 = new Client(annuaire, messagerie, certification, norbert, "client/Gérard_Norbert/trousseau.key");
		}else{
			client1 = new Client(annuaire, messagerie, certification, norbert);
			client2 = new Client(annuaire, messagerie, certification, jeanmichel);
			client1.inscrireUtilisateur();
			client2.inscrireUtilisateur();
		}
	
		Message m = new Message(norbert, jeanmichel);
		client1.envoyerMessage(m, "Yolo");
		
		for(Message recu : client2.getMessages()){
			System.out.println(recu+" : "+client2.dechiffrerMessage(recu));
		}
	}*/
}
