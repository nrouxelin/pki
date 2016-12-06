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
	public void ajouterCertificat(Certificat c) throws RemoteException, UtilisateurExistantException, ErreurStockageException{
		certification.ajouterCertificat(c);
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
			throws RemoteException, CertificatNonTrouveException, ErreurStockageException{
		certification.revoquerCertificat(id);
	}
	
	@Override
	public void mettreAJourCertificat(int id, Certificat c)
			throws RemoteException,CertificatNonTrouveException, UtilisateurExistantException, ErreurStockageException{
		certification.mettreAJourCertificat(id, c);
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

	@Override
	public void enregistrerCertificat(Certificat c, boolean actif) throws IOException {
		certification.enregistrerCertificat(c, actif);
	}

}
