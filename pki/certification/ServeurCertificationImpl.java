package pki.certification;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
@SuppressWarnings("serial")
public class ServeurCertificationImpl extends UnicastRemoteObject implements ServeurCertification {
	
	private Certification certification;

	public ServeurCertificationImpl() throws RemoteException{
		super();
		certification = new Certification();
	}

	@Override
	public void ajouterCertificat(Certificat c) throws UtilisateurExistantException, ErreurStockageException, IOException{
		certification.ajouterCertificat(c);
		certification.enregistrerCertificat(c, true);
	}
	@Override
	public Certificat getCertificatByNom(String nom) throws RemoteException, CertificatNonTrouveException{
		return certification.getCertificatByNom(nom);
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
