package pki.annuaire;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import pki.exceptions.UtilisateurExistantException;
import pki.exceptions.UtilisateurNonTrouveException;

public interface ServeurAnnuaire extends Remote {
	
	/**
	 * Ajoute une personne à l'annuaire
	 * 
	 * @param p la personne à ajouter
	 * @throws UtilisateurExistantException
	 * @throws IOException
	 */
	public void ajouterPersonne(Personne p) throws UtilisateurExistantException, IOException;
	
	/**
	 * récupère une personne par son nom et prénom
	 * @param nom nom de la personne
	 * @param prenom prénoom de la personne
	 * @return la personne correspondante
	 * @throws UtilisateurNonTrouveException
	 */
	public Personne getPersonne(String nom, String prenom) throws UtilisateurNonTrouveException, RemoteException;
	
	/**
	 * Supprime une personne de l'annuaire
	 * 
	 * @param p la personne à supprimer
	 * @throws UtilisateurNonTrouveException
	 * @throws IOException
	 */
	public void supprimerPersonne(Personne p) throws UtilisateurNonTrouveException, IOException;
		
	/**
	 * Supprime une personne de l'annuaire
	 * 
	 * @param nom de la personne à supprimer
	 * @param prenom de la personne à supprimer
	 * @throws UtilisateurNonTrouveException
	 * @throws IOException
	 */
	public void supprimerPersonne(String nom, String prenom) throws UtilisateurNonTrouveException, IOException;
	
	/**
	 * Test l'inscription d'une personne
	 * @param pers la personne dont on test l'inscription
	 * @return vrai si la personne est inscrite, faux sinon
	 */
	public boolean estInscrit(Personne p) throws RemoteException;
	
	/**
	 * Test l'inscription d'une personne
	 * @param nom le nom de la personne à tester
	 * @param prenom le prénom de la personne à tester
	 * @return vrai si la personne est inscrite, faux sinon
	 */
	public boolean estInscrit(String nom, String prenom) throws RemoteException;
	
	/**
	 * @return
	 * @throws RemoteException
	 */
	ArrayList<Personne> getPersonnes() throws RemoteException;
	
	/**
	 * @return
	 * @throws RemoteException
	 */
	int getNbPersonnes() throws RemoteException;
}
