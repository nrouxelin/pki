package pki.annuaire;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("serial")
public class ServeurAnnuaireImpl extends UnicastRemoteObject implements ServeurAnnuaire {
	
	private Annuaire annuaire;
	
	public ServeurAnnuaireImpl() throws RemoteException{
		super();
		annuaire = new Annuaire("annuaire.txt");
	}

	@Override
	public void ajouterPersonne(Personne p) throws PersonneExistanteException, IOException {
		annuaire.ajouterPersonne(p);

	}

	@Override
	public Personne getPersonne(String nom, String prenom) throws RemoteException, PersonneNonTrouveeException {
		return annuaire.getPersonne(nom, prenom);
	}

	@Override
	public void supprimerPersonne(Personne p) throws PersonneNonTrouveeException, IOException {
		annuaire.supprimerPersonne(p);

	}

	@Override
	public void supprimerPersonne(String nom, String prenom) throws PersonneNonTrouveeException, IOException {
		annuaire.supprimerPersonne(nom, prenom);
	}

	
	public static void main(String[] args){
		try {
			ServeurAnnuaireImpl serveur = new ServeurAnnuaireImpl();
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("annuaire", serveur);
			System.out.println("Serveur enregistré");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean estInscrit(Personne p) throws RemoteException {
		return annuaire.estInscrit(p);
	}

	@Override
	public boolean estInscrit(String nom, String prenom) throws RemoteException {
		return annuaire.estInscrit(nom, prenom);
	}
}
