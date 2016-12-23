package pki.client;

import java.rmi.RemoteException;

import pki.Message;
import pki.annuaire.Personne;
import pki.annuaire.ServeurAnnuaire;
import pki.certification.Certificat;
import pki.certification.CertificatNonTrouveException;
import pki.certification.ServeurCertification;
import pki.messagerie.ServeurStockage;

public class Client {

	private ServeurAnnuaire annuaire;
	private ServeurStockage messagerie;
	private ServeurCertification certification;
	private Personne utilisateur;
	
	public Client(ServeurAnnuaire a, ServeurStockage m, ServeurCertification c, Personne u){
		annuaire = a;
		messagerie = m;
		certification = c;
		utilisateur = u;
	}
	
	public void envoyerMessage(Message m) throws RemoteException, CertificatNonTrouveException{
		if(utilisateur.equals(m.getExpediteur()) && annuaire.estInscrit(m.getDestinataire())){
			String nom = m.getDestinataire().getPrenom()+" "+m.getDestinataire().getNom();
			Certificat certificatDestinataire = certification.getCertificatByNom(nom);
		}
	}
}
