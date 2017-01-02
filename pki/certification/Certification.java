/**
 * 
 */
package pki.certification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import pki.annuaire.Personne;
import pki.exceptions.CertificatNonTrouveException;
import pki.exceptions.UtilisateurExistantException;


@SuppressWarnings("serial")
public class Certification implements Serializable{
	
	
	private ArrayList<Certificat> certificats;
	private ArrayList<Certificat> certificatsRevoques;
	private int nbCertificats;
	private boolean initialisation;
	//Filtre pour le nom des certificats
	private static FilenameFilter filtreExtension = new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.endsWith(".dat");
			}
		};
	
	
	/**
	 * Constructeur
	 */
	public Certification(){

		certificats = new ArrayList<Certificat>();
		certificatsRevoques = new ArrayList<Certificat>();
		nbCertificats = 0;
		initialisation = true;
		
		
		//Lecture des certificats actifs
				try{
				File repertoire = new File("certificats/actifs/");
				//On vérifie que les dossiers nécessaires existent, si besoin on les créée
				if(!(repertoire.exists() && repertoire.isDirectory())){
					repertoire.mkdirs();
				}
				File[] actifs = repertoire.listFiles(filtreExtension);
				for(File f : actifs){
					Certificat c = lireCertificat(f);
					ajouterCertificat(c);
					
				}
				}catch(IOException e){
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (UtilisateurExistantException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Lecture des certificats revoqués
				File repertoire = new File("certificats/revoques/");
				//On vérifie que les dossiers nécessaires existent, si besoin on les créée
				if(!(repertoire.exists() && repertoire.isDirectory())){
					repertoire.mkdirs();
				}
				File[] revoques = repertoire.listFiles();
				for(File f : revoques){
					try {
						ajouterCertificatRevoque(lireCertificat(f));
					} catch (ClassNotFoundException | UtilisateurExistantException | IOException e) {
						System.out.println("Une erreur a eu lieu lors de l'ajout des certificats révoqués.");
					}
				}
				
				initialisation = false;
				
	}
	
	
	/**
	 * Recherche le certificat correspondant à une personne
	 * 
	 * @param p la personne
	 * @return le certificat correspondant
	 * @throws CertificatNonTrouveException
	 */
	public Certificat getCertificatByPersonne(Personne p) throws CertificatNonTrouveException{
		for(Certificat certif : certificats){
			if(certif.getPersonne().equals(p)){
				return certif;
			}
		}
		throw new CertificatNonTrouveException();
	}
	
	/**
	 * Cherche le certificat correspondant à un id
	 * 
	 * @param id l'id du certificat
	 * @return le certificat correspondant
	 * @throws CertificatNonTrouveException
	 */
	public Certificat getCertificatById(int id) throws CertificatNonTrouveException{
		for(Certificat certif : certificats){
			if(certif.getId() == id){
				return certif;
			}
		}
		throw new CertificatNonTrouveException();
	}
	
	
	/**
	 * Ajoute un certificat
	 * 
	 * @param c le certificat à ajouter
	 * @throws UtilisateurExistantException
	 * @throws IOException 
	 */
	public boolean ajouterCertificat(Certificat c)
			throws UtilisateurExistantException, IOException{
		try{
			Certificat c1 = getCertificatByPersonne(c.getPersonne());
			if(c1!=null){
				throw new UtilisateurExistantException();
			}
		}catch (CertificatNonTrouveException e){
			if(!certificats.add(c)){
				return false;
			}else{
				c.setId(nbCertificats++);
				if(!initialisation){
					enregistrerCertificat(c);
				}
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * Ajoute le certificat aux certificats révoqués
	 * 
	 * @param c le certidicat à révoquer
	 * @throws UtilisateurExistantException
	 */
	public boolean ajouterCertificatRevoque(Certificat c)
			throws UtilisateurExistantException{
		try{
			Certificat c1 = getCertificatById(c.getId());
			if(c1!=null){
				throw new UtilisateurExistantException();
			}
		}catch (CertificatNonTrouveException e){
			if(!certificatsRevoques.add(c)){
				return false;
			}else{
				nbCertificats++;
				return true;
			}
		}
		return false;
		
	}
	
	
	/**
	 * Révoque un certificat
	 * 
	 * @param id l'id du certificat à révoquer
	 * @throws CertificatNonTrouveException
	 * @throws IOException
	 */
	public boolean revoquerCertificat(int id)
			throws CertificatNonTrouveException, IOException{
		Certificat c = getCertificatById(id);
		c.setDateFin(LocalDateTime.now());
		if(certificats.remove(c)){
			if(!certificatsRevoques.add(c)){
				return false;
			}else{
				File source = new File("certificats/actifs/"+id+".dat");
				File destination = new File("certificats/revoques/"+id+".dat");
				if(!source.renameTo(destination)){
					throw new IOException();
				}
				return true;
			}
		}else{
			return false;
		}
	}
	
	
	/**
	 * met à jour le certificat
	 * 
	 * @param id l'id du certificat
	 * @param c le nouveau certificat
	 * @throws CertificatNonTrouveException
	 * @throws UtilisateurExistantException
	 * @throws IOException
	 */
	public boolean mettreAJourCertificat(int id, Certificat c)
			throws CertificatNonTrouveException, UtilisateurExistantException, IOException{
		Certificat ancienCertificat = getCertificatById(id);
		if(c.getPersonne().equals(ancienCertificat.getPersonne())){
			boolean r = revoquerCertificat(id);
			boolean a = ajouterCertificat(c);
			return a && r;
		}
		return false;
	}
	
	/**
	 * Enregistre un certificat
	 * 
	 * @param c le certificat à enregistrer
	 * @param actif pour savoir si le certificat est actif ou révoqué
	 * @throws IOException
	 */
	public void enregistrerCertificat(Certificat c, boolean actif) throws IOException{
		String dossier;
		if(actif){
			dossier = "certificats/actifs/";
		}else{
			dossier = "certificats/revoques/";
		}
		
		//On vérifie que les dossiers nécessaires existent, si besoin on les créée
		File repertoire = new File(dossier);
		if(!(repertoire.exists() && repertoire.isDirectory())){
			repertoire.mkdirs();
		}
		
		//on enregistre le certificat
		File fichier = new File(dossier+c.getId()+".dat");
		ObjectOutputStream flux = new ObjectOutputStream(new FileOutputStream(fichier));
		flux.writeObject(c);
		flux.close();
	}
	
	/**
	 * @param c le certificat à enregistrer
	 * @throws IOException
	 */
	public void enregistrerCertificat(Certificat c) throws IOException{
		enregistrerCertificat(c,true);
	}
	
	/**
	 * Lis le certificat dans le fichier dans lequel il est stocké
	 * 
	 * @param nomFichier le nom du fichier du certificat
	 * @return le certificat
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Certificat lireCertificat(String nomFichier) throws IOException, ClassNotFoundException{
		File fichier = new File(nomFichier);
		ObjectInputStream flux = new ObjectInputStream(new FileInputStream(fichier));
		Certificat c = (Certificat) flux.readObject();
		flux.close();
		return c;
	}
	
	/**
	 * Lis le certificat dans le fichier dans lequel il est stocké
	 * 
	 * @param fichier le fichier du certificat
	 * @return le certificat
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Certificat lireCertificat(File fichier) throws IOException, ClassNotFoundException{
		ObjectInputStream flux = new ObjectInputStream(new FileInputStream(fichier));
		Certificat c = (Certificat) flux.readObject();
		flux.close();
		return c;
	}
	
	/**
	 * Récupère le certificat correspondant à une personne et à une date
	 * 
	 * @param p la personne
	 * @param date la date
	 * @return le certificat
	 * @throws CertificatNonTrouveException
	 */
	public Certificat getCertificatByPersonneAndDate(Personne p, LocalDateTime date) throws CertificatNonTrouveException{
		Certificat actuel = getCertificatByPersonne(p);
		if(date.isAfter(actuel.getDateDebut()) && actuel.getDateFin().isAfter(date)){
			return actuel;
		}else{
			for(Certificat c : certificatsRevoques){
				if(c.getPersonne().equals(p) && date.isAfter(c.getDateDebut()) && c.getDateFin().isAfter(date)){
					return c;
				}
			}
		}
		throw new CertificatNonTrouveException();
	}
	
	/**
	 * @return
	 */
	public int getNbCertificats(){
		return nbCertificats;
	}
}
