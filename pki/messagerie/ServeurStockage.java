package pki.messagerie;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import pki.Message;
import pki.annuaire.Personne;

public interface ServeurStockage extends Remote {
	
	/**
	 * Enregistre un message
	 * 
	 * @param m le message à enregistrer
	 * @throws IOException
	 */
	public void enregistrerMessage(Message m)
			throws RemoteException, IOException;
	
	/**
	 * Récupère les messages à destination d'une personne
	 * @param destinataire le destinataire des messages
	 * @return les messages vers ce destinataire
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public ArrayList<Message> getMessages(Personne p)
			throws RemoteException;
	
	/**
	 * Récupère le nombre de message à destination d'une personne 
	 * @param destinataire le destinataire des messages
	 * @return le nombre de messages vers ce destinataire
	 */
	public int getNbMessages(Personne p)
			throws RemoteException;
}
