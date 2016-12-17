/**
 * 
 */
package pki.certification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

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
	
	
	private ArrayList<Certificat> certificats;
	/**
	 * 
	 */
	private ArrayList<Certificat> certificatsRevoques;
	private int nbCertificats;
	//Filtre pour le nom des certificats
	private static FilenameFilter filtreExtension = new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.endsWith(".dat");
			}
		};
	
	
	/**
	 * 
	 */
	public Certification(){
		certificats = new ArrayList<Certificat>();
		certificatsRevoques = new ArrayList<Certificat>();
		nbCertificats = 0;
		
		//Lecture des certificats actifs
				try{
				File repertoire = new File("certificats/actifs/");
				File[] actifs = repertoire.listFiles(filtreExtension);
				for(File f : actifs){
					Certificat c = lireCertificat(f);
					ajouterCertificat(c);
					
					//Vérifie si le certificat n'est pas expiré
					LocalDate aujourdhui = LocalDate.now();
					if(aujourdhui.isAfter(c.getDateFin())){
						revoquerCertificat(c.getId());
					}
				}
				}catch(IOException e){
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (UtilisateurExistantException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ErreurStockageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CertificatNonTrouveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//Lecture des certificats revoqués
				File repertoire = new File("certificats/revoques/");
				File[] revoques = repertoire.listFiles();
				for(File f : revoques){
					try {
						ajouterCertificatRevoque(lireCertificat(f));
					} catch (ClassNotFoundException | UtilisateurExistantException | ErreurStockageException | IOException e) {
						System.out.println("Une erreur a eu lieu lors de l'ajout des certificats révoqués.");
					}
				}
				
	}
	
	/**
	 * @param nom
	 * @return
	 * @throws CertificatNonTrouveException
	 */
	public Certificat getCertificatByNom(String nom) throws CertificatNonTrouveException{
		for(Certificat certif : certificats){
			if(certif.getNom().equals(nom)){
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
	 * @throws ErreurStockageException
	 */
	public void ajouterCertificat(Certificat c)
			throws UtilisateurExistantException, ErreurStockageException{
		try{
			Certificat c1 = getCertificatByNom(c.getNom());
			if(c1!=null){
				throw new UtilisateurExistantException();
			}
		}catch (CertificatNonTrouveException e){
			if(!certificats.add(c)){
				throw new ErreurStockageException();
			}else{
				c.setId(nbCertificats++);
			}
		}
		
	}
	/**
	 * @param c
	 * @throws UtilisateurExistantException
	 * @throws ErreurStockageException
	 */
	public void ajouterCertificatRevoque(Certificat c)
			throws UtilisateurExistantException, ErreurStockageException{
		try{
			Certificat c1 = getCertificatById(c.getId());
			if(c1!=null){
				throw new UtilisateurExistantException();
			}
		}catch (CertificatNonTrouveException e){
			if(!certificatsRevoques.add(c)){
				throw new ErreurStockageException();
			}else{
				nbCertificats++;
			}
		}
		
	}
	
	
	/**
	 * @param id
	 * @throws CertificatNonTrouveException
	 * @throws ErreurStockageException
	 * @throws IOException
	 */
	public void revoquerCertificat(int id)
			throws CertificatNonTrouveException,ErreurStockageException, IOException{
		Certificat c = getCertificatById(id);
		if(certificats.remove(c)){
			if(!certificatsRevoques.add(c)){
				throw new ErreurStockageException();
			}else{
				File source = new File("certificats/actifs/"+id+".dat");
				File destination = new File("certificats/revoques/"+id+".dat");
				if(!source.renameTo(destination)){
					throw new IOException();
				}
			}
		}else{
			throw new ErreurStockageException();
		}
	}
	
	
	/**
	 * @param id
	 * @param c
	 * @throws CertificatNonTrouveException
	 * @throws UtilisateurExistantException
	 * @throws ErreurStockageException
	 * @throws IOException
	 */
	public void mettreAJourCertificat(int id, Certificat c)
			throws CertificatNonTrouveException, UtilisateurExistantException,ErreurStockageException, IOException{
		Certificat ancienCertificat = getCertificatById(id);
		if(c.getNom().equals(ancienCertificat.getNom())){
			revoquerCertificat(id);
			ajouterCertificat(c);
		}
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
	
}
