package pki.messagerie;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import pki.Message;
import pki.annuaire.Personne;

public interface ServeurStockage extends Remote {
	public void enregistrerMessage(Message m)
			throws RemoteException, IOException;
	public ArrayList<Message> getMessages(Personne p)
			throws RemoteException;
	public int getNbMessages(Personne p)
			throws RemoteException;
}
