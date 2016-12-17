package pki.annuaire;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServeurAnnuaire extends Remote {

	public void ajouterPersonne(Personne p) throws PersonneExistanteException, IOException;
	public Personne getPersonne(String nom, String prenom) throws PersonneNonTrouveeException, RemoteException;
	public void supprimerPersonne(Personne p) throws PersonneNonTrouveeException, IOException;
	public void supprimerPersonne(String nom, String prenom) throws PersonneNonTrouveeException, IOException;
	public boolean estInscrit(Personne p) throws RemoteException;
	public boolean estInscrit(String nom, String prenom) throws RemoteException;
}
