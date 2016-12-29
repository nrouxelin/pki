package pki.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pki.Chiffrement;
import pki.Message;
import pki.annuaire.Personne;
import pki.annuaire.PersonneExistanteException;
import pki.annuaire.ServeurAnnuaire;
import pki.certification.Certificat;
import pki.certification.CertificatNonTrouveException;
import pki.certification.ErreurStockageException;
import pki.certification.ServeurCertification;
import pki.certification.UtilisateurExistantException;
import pki.messagerie.ServeurStockage;

public class Client {

	private ServeurAnnuaire annuaire;
	private ServeurStockage messagerie;
	private ServeurCertification certification;
	private Personne utilisateur;
	private Key cleLecture;
	private Key cleSignature;
	
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u){
		annuaire = a;
		messagerie = m;
		certification = c;
		utilisateur = u;
	}
	
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u, File fichierSignature, File fichierLecture) throws ClassNotFoundException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, CertificatNonTrouveException, ErreurStockageException, PersonneExistanteException, UtilisateurExistantException, CertificatNonValideException{
		this(a,m,c,u);
		cleLecture = lireCle(fichierLecture);
		cleSignature = lireCle(fichierSignature);
		
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
	
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u, String nomFichierSignature, String nomFichierLecture) throws ClassNotFoundException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, CertificatNonTrouveException, ErreurStockageException, PersonneExistanteException, UtilisateurExistantException, CertificatNonValideException{
		this(a,m,c,u, new File(nomFichierSignature), new File(nomFichierLecture));
	}
	
	private Key lireCle(File fichier) throws IOException, ClassNotFoundException{
		ObjectInputStream flux = new ObjectInputStream(new FileInputStream(fichier));
		Key cle = (Key) flux.readObject();
		flux.close();
		return cle;
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
	
	public void envoyerMessage(Message m, String texte) throws CertificatNonTrouveException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, FileNotFoundException, IOException, CertificatNonValideException{
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
	
	private void mettreAJourCertificat(int id) throws RemoteException, ErreurStockageException, IOException{
		try {
			
			
			//on enregistre les anciennes clés
			String prefixe = "client/"+utilisateur+"/";
			prefixe = prefixe.replaceAll("\\s", "_");
			String nomRepertoire = id+"/";
			File repertoire = new File(prefixe+nomRepertoire);
			repertoire.mkdirs();
			
			File source = new File(prefixe+"cleLecture.key");
			File destination = new File(prefixe+nomRepertoire+"cleLecture.key");
			if(!source.renameTo(destination)){
				throw new IOException();
			}
			
			source = new File(prefixe+"cleSignature.key");
			destination = new File(prefixe+nomRepertoire+"cleSignature.key");
			if(!source.renameTo(destination)){
				throw new IOException();
			}
			
			certification.revoquerCertificat(id);
			//On génère la clé d'écriture
			KeyPair clesEcriture = Chiffrement.genererClesRSA();
			cleLecture = clesEcriture.getPrivate();
			ecrireCle(cleLecture,"cleLecture.key");
					
			//On génère la clé de signature
			KeyPair clesSignature = Chiffrement.genererClesRSA();
			cleSignature = clesSignature.getPublic();
			ecrireCle(cleSignature,"cleSignature.key");
					
			//On génère le certificat et on l'ajoute
			Certificat c = new Certificat(utilisateur, clesSignature.getPrivate(), clesEcriture.getPublic());
			certification.ajouterCertificat(c);
		} catch (CertificatNonTrouveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UtilisateurExistantException e) {
		}
		
		
	}
	
	public void inscrireUtilisateur() throws PersonneExistanteException, IOException, UtilisateurExistantException, ErreurStockageException{
		annuaire.ajouterPersonne(utilisateur);
		
		//On génère la clé d'écriture
		KeyPair clesEcriture = Chiffrement.genererClesRSA();
		cleLecture = clesEcriture.getPrivate();
		ecrireCle(cleLecture,"cleLecture.key");
		
		//On génère la clé de signature
		KeyPair clesSignature = Chiffrement.genererClesRSA();
		cleSignature = clesSignature.getPublic();
		ecrireCle(cleSignature,"cleSignature.key");
		
		//On génère le certificat et on l'ajoute
		Certificat c = new Certificat(utilisateur, clesSignature.getPrivate(), clesEcriture.getPublic());
		certification.ajouterCertificat(c);
	}
	
	private void ecrireCle(Key cle, String nomFichier) throws FileNotFoundException, IOException{
		String nomRepertoire = "client/"+utilisateur+"/";
		nomRepertoire = nomRepertoire.replaceAll("\\s", "_");
		File repertoire = new File(nomRepertoire);
		if(!(repertoire.exists() && repertoire.isDirectory())){
			repertoire.mkdirs();
		}
		File fichier = new File(nomRepertoire+nomFichier);
		ObjectOutputStream flux = new ObjectOutputStream(new FileOutputStream(fichier));
		flux.writeObject(cle);
		flux.close();
	}
	
	public ArrayList<Message> getMessages() throws RemoteException{
		return messagerie.getMessages(utilisateur);
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
			cle = lireCle(new File(nomFichier));
		}else{
			cle = cleLecture;
		}
		if(Chiffrement.verifierSignature(m, certificatExpediteur.getCleSignature())){
			return Chiffrement.dechiffrerMessage(m, cle);
		}else{
			return "Erreur signature";
		}
	}
	
	public static void main(String[] args) throws Exception{
		Personne jeanmichel = new Personne("Connard","Jean-Michel");
		Personne norbert = new Personne("Gérard","Norbert");
		
		ServeurCertification certification = (ServeurCertification)LocateRegistry.getRegistry().lookup("certification");
		ServeurAnnuaire annuaire = (ServeurAnnuaire)LocateRegistry.getRegistry().lookup("annuaire");
		ServeurStockage messagerie = (ServeurStockage)LocateRegistry.getRegistry().lookup("stockage");

		
		
		Client client1;
		Client client2=null;
		int mode = 1;
		if(mode==1){
			Certificat c = certification.getCertificatByPersonne(jeanmichel);
			KeyPair kp = Chiffrement.genererClesRSA();
			Chiffrement.signerCertificat(c,kp.getPublic());
			System.out.println(Chiffrement.verifierSignature(c, kp.getPrivate()));
			

			System.out.println("norbert : "+Chiffrement.verifierSignature(certification.getCertificatByPersonne(norbert), certification.getClePublique()));
			System.out.println("jm : "+Chiffrement.verifierSignature(certification.getCertificatByPersonne(jeanmichel), certification.getClePublique()));

			client2 = new Client(annuaire, messagerie, certification, jeanmichel, "client/Connard_Jean-Michel/cleSignature.key","client/Connard_Jean-Michel/cleLecture.key");
			client1 = new Client(annuaire, messagerie, certification, norbert, "client/Gérard_Norbert/cleSignature.key","client/Gérard_Norbert/cleLecture.key");
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
	}
}
