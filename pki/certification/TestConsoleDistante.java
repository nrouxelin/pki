package pki.certification;

import java.io.File;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class TestConsoleDistante {

	public static void main(String[] args){
		try {
			ServeurCertification serveur = (ServeurCertification)LocateRegistry.getRegistry().lookup("certification");
			System.out.println(serveur.getNbCertificats());
			Certificat c = new Certificat("nun","caf","zfg");
			Certificat c2 = new Certificat("nathan","jui","hit");
			Certificat c3 = new Certificat("nathan","faez","zggr");
			serveur.ajouterCertificat(c);
			serveur.ajouterCertificat(c2);;
			Certificat nathan = serveur.getCertificatByNom("nathan");
			System.out.println(nathan.getCleEcriture());
			serveur.mettreAJourCertificat(nathan.getId(), c3);
			System.out.println(serveur.getCertificatByNom("nathan").getCleEcriture());
			File rep = new File("certificats/actifs/");
			for(File tmp : rep.listFiles()){
				System.out.println(tmp);
			}
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
			System.out.println("Impossible d'ajouter un utilisateur existant");
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