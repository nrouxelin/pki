package pki.messagerie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import pki.Message;
import pki.annuaire.Personne;

@SuppressWarnings("serial")
public class ServeurStockageImpl extends UnicastRemoteObject implements ServeurStockage {
	
	private Stockage stockage;

	protected ServeurStockageImpl() throws RemoteException {
		super();
		stockage = new Stockage("msg");
	}

	@Override
	public void enregistrerMessage(Message m)
			throws RemoteException, FileNotFoundException, IOException, PersonneInconnueException {
		stockage.enregistrerMessage(m);

	}

	@Override
	public ArrayList<Message> getMessages(Personne p) throws RemoteException, ClassNotFoundException, IOException {
		return stockage.getMessages(p);

	}
	
	public static void main(String[] args){
		try {
			ServeurStockage serveur = new ServeurStockageImpl();
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("stockage", serveur);
			System.out.println("Serveur enregistré");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
