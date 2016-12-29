package pki.annuaire;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import pki.exceptions.UtilisateurExistantException;
import pki.exceptions.UtilisateurNonTrouveException;

public interface ServeurAnnuaire extends Remote {

	public void ajouterPersonne(Personne p) throws UtilisateurExistantException, IOException;
	public Personne getPersonne(String nom, String prenom) throws UtilisateurNonTrouveException, RemoteException;
	public void supprimerPersonne(Personne p) throws UtilisateurNonTrouveException, IOException;
	public void supprimerPersonne(String nom, String prenom) throws UtilisateurNonTrouveException, IOException;
	public boolean estInscrit(Personne p) throws RemoteException;
	public boolean estInscrit(String nom, String prenom) throws RemoteException;
}
