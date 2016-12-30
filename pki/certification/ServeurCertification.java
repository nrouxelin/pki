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
	public boolean ajouterCertificat(Certificat c)
			throws RemoteException, UtilisateurExistantException, IOException;
	public Certification getCertification()
			throws RemoteException;
	public boolean revoquerCertificat(int id)
			throws RemoteException, CertificatNonTrouveException, IOException;
	public boolean mettreAJourCertificat(int id, Certificat c)
			throws RemoteException, CertificatNonTrouveException, UtilisateurExistantException, IOException;
	public int getNbCertificats() throws RemoteException;
	public Certificat getCertificatByPersonne(Personne p) throws RemoteException, CertificatNonTrouveException;
	public Certificat getCertificatByPersonneAndDate(Personne p, LocalDateTime date) throws RemoteException, CertificatNonTrouveException;
}
