package pki.certification;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class TestConsoleDistante {

	public static void main(String[] args){
		try {
			ServeurCertification serveur = (ServeurCertification)LocateRegistry.getRegistry().lookup("certification");
			Certificat c = new Certificat("nathan","abc","def");
			Certificat c2 = new Certificat("nathan","jui","hit");
			serveur.ajouterCertificat(c);
			System.out.println("Certificat ajouté");
			Certificat nathan = serveur.getCertificatByNom("nathan");
			System.out.println(nathan.getCleEcriture());
			serveur.mettreAJourCertificat(nathan.getId(), c2);
			System.out.println(serveur.getCertificatByNom("nathan").getCleEcriture());
			System.out.println(System.getProperty("user.dir"));
			serveur.enregistrerCertificat(c2,true);
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificatNonTrouveException e) {
			System.out.println("Impossible de trouver le certificat demandé");
			e.printStackTrace();
		} catch (UtilisateurExistantException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErreurStockageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
//