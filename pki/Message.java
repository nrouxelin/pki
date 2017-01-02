package pki;
import java.io.Serializable;
import java.time.LocalDateTime;

import pki.annuaire.Personne;

@SuppressWarnings("serial")
public class Message implements Serializable {

	private Personne expediteur;
	private Personne destinataire;
	private LocalDateTime date;
	private byte[] message;
	private byte[] cle;
	private byte[] signature;
	
	/**
	 * Constructeur 
	 * 
	 * @param exp l'expéditeur
	 * @param dest le destinataire
	 */
	public Message(Personne exp, Personne dest){
		expediteur = exp;
		destinataire = dest;
		date = LocalDateTime.now();
	}
	
	/**
	 * Constructeur 
	 * 
	 * @param exp l'expéditeur
	 * @param dest le destinataire
	 * @param msg le text du message
	 * @param s la signature
	 */
	public Message(Personne exp, Personne dest, byte[] msg, byte[] s){
		expediteur = exp;
		destinataire = dest;
		date = LocalDateTime.now();
		message = msg;
		signature = s;
	}
	
	public Personne getExpediteur(){
		return expediteur;
	}
	
	public Personne getDestinataire(){
		return destinataire;
	}
	
	public LocalDateTime getDate(){
		return date;
	}
	
	public byte[] getMessage(){
		return message;
	}
	
	public byte[] getSignature(){
		return signature;
	}
	
	public void setMessage(byte[] msg){
		message = msg;
	}
	
	public void setSignature(byte[] s){
		signature = s;
	}
	
	public void setCle(byte[] k){
		cle = k;
	}
	
	public byte[] getCle(){
		return cle;
	}
	
	public String toString(){
		return date.toString()+expediteur.toString();
	}
}
