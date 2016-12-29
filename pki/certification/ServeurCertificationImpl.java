package pki.certification;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Key;
import java.time.LocalDateTime;

import pki.annuaire.Personne;
import pki.exceptions.CertificatNonTrouveException;
import pki.exceptions.ErreurStockageException;
import pki.exceptions.UtilisateurExistantException;
@SuppressWarnings("serial")
public class ServeurCertificationImpl extends UnicastRemoteObject implements ServeurCertification {
	
	private Certification certification;

	public ServeurCertificationImpl() throws RemoteException{
		super();
		certification = new Certification("certificats/certification.keypair");
	}

	@Override
	public void ajouterCertificat(Certificat c) throws UtilisateurExistantException, ErreurStockageException, IOException{
		certification.ajouterCertificat(c);
		certification.enregistrerCertificat(c, true);
	}
	@Override
	public Certificat getCertificatByPersonne(Personne p) throws RemoteException, CertificatNonTrouveException{
		return certification.getCertificatByPersonne(p);
	}
	@Override
	public Certification getCertification() throws RemoteException{
		return certification;
	}
	
	@Override
	public void revoquerCertificat(int id)
			throws CertificatNonTrouveException, ErreurStockageException, IOException{
		certification.revoquerCertificat(id);
	}
	
	@Override
	public void mettreAJourCertificat(int id, Certificat c)
			throws CertificatNonTrouveException, UtilisateurExistantException, ErreurStockageException, IOException{
		certification.mettreAJourCertificat(id, c);
		certification.enregistrerCertificat(c, true);
	}
	
	@Override
	public Certificat getCertificatByPersonneAndDate(Personne p, LocalDateTime date) throws RemoteException,CertificatNonTrouveException{
		return certification.getCertificatByPersonneAndDate(p, date);
	}
	
	@Override
	public Key getClePublique() throws RemoteException{
		return certification.getClePublique();
	}
	
	public static void main(String[] args){
		try {
			ServeurCertificationImpl serveur = new ServeurCertificationImpl();
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("certification", serveur);
			System.out.println("Serveur enregistré");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/**private void enregistrerCertificat(Certificat c, boolean actif) throws IOException {
		certification.enregistrerCertificat(c, actif);
	}**/

	@Override
	public int getNbCertificats() throws RemoteException {
		return certification.getNbCertificats();
	}

}
