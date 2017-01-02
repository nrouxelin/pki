/**
 * 
 */
package pki.certification;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

import pki.annuaire.Personne;
import pki.exceptions.CertificatNonTrouveException;
import pki.exceptions.UtilisateurExistantException;

/**
 * @author nat
 *
 */
public interface ServeurCertification extends Remote {
	
	/**
	 * Ajoute un certificat
	 * 
	 * @param c le certificat à ajouter
	 * @throws UtilisateurExistantException
	 * @throws IOException 
	 */
	public boolean ajouterCertificat(Certificat c)
			throws RemoteException, UtilisateurExistantException, IOException;
	
	/**
	 * Révoque un certificat
	 * 
	 * @param id l'id du certificat à révoquer
	 * @throws CertificatNonTrouveException
	 * @throws IOException
	 */
	public boolean revoquerCertificat(int id)
			throws RemoteException, CertificatNonTrouveException, IOException;
	
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
			throws RemoteException, CertificatNonTrouveException, UtilisateurExistantException, IOException;
	
	/**
	 * Recherche le certificat correspondant à une personne
	 * 
	 * @param p la personne
	 * @return le certificat correspondant
	 * @throws CertificatNonTrouveException
	 */
	public Certificat getCertificatByPersonne(Personne p) throws RemoteException, CertificatNonTrouveException;
	
	/**
	 * Récupère le certificat correspondant à une personne et à une date
	 * 
	 * @param p la personne
	 * @param date la date
	 * @return le certificat
	 * @throws CertificatNonTrouveException
	 */
	public Certificat getCertificatByPersonneAndDate(Personne p, LocalDateTime date) throws RemoteException, CertificatNonTrouveException;
	
	/**
	 * @return
	 * @throws RemoteException
	 */
	public Certification getCertification()
			throws RemoteException;

	/**
	 * @return
	 * @throws RemoteException
	 */
	public int getNbCertificats() throws RemoteException;
}
