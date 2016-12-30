package pki.certification;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;

import pki.annuaire.Personne;

import pki.exceptions.CertificatNonTrouveException;
import pki.exceptions.UtilisateurExistantException;
@SuppressWarnings("serial")
public class ServeurCertificationImpl extends UnicastRemoteObject implements ServeurCertification {
	
	private Certification certification;

	public ServeurCertificationImpl() throws RemoteException{
		super();
		certification = new Certification();
	}

	@Override
	public boolean ajouterCertificat(Certificat c) throws UtilisateurExistantException, IOException{
		return certification.ajouterCertificat(c);
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
	public boolean revoquerCertificat(int id)
			throws CertificatNonTrouveException, IOException{
		return certification.revoquerCertificat(id);
	}
	
	@Override
	public boolean mettreAJourCertificat(int id, Certificat c)
			throws CertificatNonTrouveException, UtilisateurExistantException, IOException{
		return certification.mettreAJourCertificat(id, c);
	}
	
	@Override
	public Certificat getCertificatByPersonneAndDate(Personne p, LocalDateTime date) throws RemoteException,CertificatNonTrouveException{
		return certification.getCertificatByPersonneAndDate(p, date);
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
	public int getNbCertificats() throws RemoteException {
		return certification.getNbCertificats();
	}

}
