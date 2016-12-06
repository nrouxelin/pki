/**
 * 
 */
package pki.certification;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author nat
 *
 */
public interface ServeurCertification extends Remote {
	public void ajouterCertificat(Certificat c)
			throws RemoteException, UtilisateurExistantException, ErreurStockageException;
	public Certificat getCertificatByNom(String nom)
			throws RemoteException, CertificatNonTrouveException;
	public Certification getCertification()
			throws RemoteException;
	public void revoquerCertificat(int id)
			throws RemoteException, CertificatNonTrouveException, ErreurStockageException;
	public void mettreAJourCertificat(int id, Certificat c)
			throws RemoteException, CertificatNonTrouveException, UtilisateurExistantException, ErreurStockageException;
	public void enregistrerCertificat(Certificat c, boolean actif) throws IOException;
}
