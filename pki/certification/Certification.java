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
import java.security.Key;
import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.ArrayList;

import pki.Chiffrement;
import pki.annuaire.Personne;
import pki.exceptions.CertificatNonTrouveException;
import pki.exceptions.UtilisateurExistantException;

/**
 * @author nat
 *
 */
/**
 * @author nat
 *
 */
@SuppressWarnings("serial")
public class Certification implements Serializable{
	
	
	private KeyPair clesRSA;
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
	 * 
	 */
	public Certification(File fichierCles){
		//Lecture des cles RSA
		try {
			clesRSA = lireCles(fichierCles);
		} catch (ClassNotFoundException | IOException e1) {
			System.out.println("Génération de nouvelles clés");
			clesRSA = Chiffrement.genererClesRSA();
			try {
				ecrireCles(clesRSA,"certification.keypair");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		certificats = new ArrayList<Certificat>();
		certificatsRevoques = new ArrayList<Certificat>();
		nbCertificats = 0;
		initialisation = true;
		
		//Lecture des certificats actifs
				try{
				File repertoire = new File("certificats/actifs/");
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
	
	public Certification(String nomFichierCles){
		this(new File(nomFichierCles));
	}
	
	private KeyPair lireCles(File fichier) throws IOException, ClassNotFoundException{
		ObjectInputStream flux = new ObjectInputStream(new FileInputStream(fichier));
		KeyPair cle = (KeyPair) flux.readObject();
		flux.close();
		return cle;
	}
	
	private void ecrireCles(KeyPair cles, String nomFichier) throws FileNotFoundException, IOException{
		File fichier = new File("certificats/"+nomFichier);
		ObjectOutputStream flux = new ObjectOutputStream(new FileOutputStream(fichier));
		flux.writeObject(cles);
		flux.close();
	}
	
	/**
	 * @param nom
	 * @return
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
	 * @param id
	 * @return
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
	 * @param c
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
			Chiffrement.signerCertificat(c, clesRSA.getPublic());
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
	 * @param c
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
	 * @param id
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
	 * @param id
	 * @param c
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
	 * @param c
	 * @param actif
	 * @throws IOException
	 */
	public void enregistrerCertificat(Certificat c, boolean actif) throws IOException{
		String dossier;
		if(actif){
			dossier = "certificats/actifs/";
		}else{
			dossier = "certificats/revoques/";
		}
		File fichier = new File(dossier+c.getId()+".dat");
		ObjectOutputStream flux = new ObjectOutputStream(new FileOutputStream(fichier));
		flux.writeObject(c);
		flux.close();
	}
	
	public void enregistrerCertificat(Certificat c) throws IOException{
		enregistrerCertificat(c,true);
	}
	
	/**
	 * @param nomFichier
	 * @return
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
	 * @param fichier
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Certificat lireCertificat(File fichier) throws IOException, ClassNotFoundException{
		ObjectInputStream flux = new ObjectInputStream(new FileInputStream(fichier));
		Certificat c = (Certificat) flux.readObject();
		flux.close();
		return c;
	}
	
	public int getNbCertificats(){
		return nbCertificats;
	}
	
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
	
	public Key getClePublique(){
		return clesRSA.getPrivate();
	}
}
