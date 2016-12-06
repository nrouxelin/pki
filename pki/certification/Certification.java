/**
 * 
 */
package pki.certification;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author nat
 *
 */
@SuppressWarnings("serial")
public class Certification implements Serializable{
	
	
	private ArrayList<Certificat> certificats;
	private ArrayList<Certificat> certificatsRevoques;
	public static int NB_CERTIFICATS = 0;
	
	public Certification(){
		certificats = new ArrayList<Certificat>();
		certificatsRevoques = new ArrayList<Certificat>();
	}
	
	public Certificat getCertificatByNom(String nom) throws CertificatNonTrouveException{
		for(Certificat certif : certificats){
			if(certif.getNom().equals(nom)){
				return certif;
			}
		}
		throw new CertificatNonTrouveException();
	}
	
	public Certificat getCertificatById(int id) throws CertificatNonTrouveException{
		for(Certificat certif : certificats){
			if(certif.getId() == id){
				return certif;
			}
		}
		throw new CertificatNonTrouveException();
	}
	
	
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
			}
		}
		
	}
	
	public void revoquerCertificat(int id)
			throws CertificatNonTrouveException,ErreurStockageException{
		Certificat c = getCertificatById(id);
		if(certificats.remove(c)){
			if(!certificatsRevoques.add(c)){
				throw new ErreurStockageException();
			}
			}else{
				throw new ErreurStockageException();
		}
	}
	
	public void mettreAJourCertificat(int id, Certificat c)
			throws CertificatNonTrouveException, UtilisateurExistantException,ErreurStockageException{
		Certificat ancienCertificat = getCertificatById(id);
		if(c.getNom().equals(ancienCertificat.getNom())){
			revoquerCertificat(id);
			ajouterCertificat(c);
		}
	}
	
	public void enregistrerCertificat(Certificat c, boolean actif) throws IOException{
		String dossier;
		if(actif){
			dossier = "certificats/actifs/";
		}else{
			dossier = "certificats/revoques/";
		}
		FileWriter fichier = new FileWriter(dossier+c.getId()+".dat");
		ObjectOutputStream flux = new ObjectOuputStream(new FileOutputStream(fichier));
	}
	
}
