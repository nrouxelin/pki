/**
 * 
 */
package pki.certification;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Key;
import java.time.LocalDateTime;

import pki.annuaire.Personne;
import pki.exceptions.CertificatNonTrouveException;
import pki.exceptions.ErreurStockageException;
import pki.exceptions.UtilisateurExistantException;

/**
 * @author nat
 *
 */
public interface ServeurCertification extends Remote {
	public void ajouterCertificat(Certificat c)
			throws RemoteException, UtilisateurExistantException, ErreurStockageException,IOException;
	public Certification getCertification()
			throws RemoteException;
	public void revoquerCertificat(int id)
			throws RemoteException, CertificatNonTrouveException, ErreurStockageException, IOException;
	public void mettreAJourCertificat(int id, Certificat c)
			throws RemoteException, CertificatNonTrouveException, UtilisateurExistantException,
			ErreurStockageException, IOException;
	//public void enregistrerCertificat(Certificat c, boolean actif) throws IOException;
	public int getNbCertificats() throws RemoteException;
	public Certificat getCertificatByPersonne(Personne p) throws RemoteException, CertificatNonTrouveException;
	public Certificat getCertificatByPersonneAndDate(Personne p, LocalDateTime date) throws RemoteException, CertificatNonTrouveException;
	public Key getClePublique() throws RemoteException;
}
