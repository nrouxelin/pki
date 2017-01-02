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
	 * @param exp l'exp�diteur
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
	 * @param exp l'exp�diteur
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
	
	/**
	 * @return
	 */
	public Personne getExpediteur(){
		return expediteur;
	}
	
	/**
	 * @return
	 */
	public Personne getDestinataire(){
		return destinataire;
	}
	
	/**
	 * @return
	 */
	public LocalDateTime getDate(){
		return date;
	}
	
	/**
	 * @return
	 */
	public byte[] getMessage(){
		return message;
	}
	
	/**
	 * @return
	 */
	public byte[] getSignature(){
		return signature;
	}
	
	/**
	 * @param msg
	 */
	public void setMessage(byte[] msg){
		message = msg;
	}
	
	/**
	 * @param s
	 */
	public void setSignature(byte[] s){
		signature = s;
	}
	
	/**
	 * @param k
	 */
	public void setCle(byte[] k){
		cle = k;
	}
	
	/**
	 * @return
	 */
	public byte[] getCle(){
		return cle;
	}
	
	public String toString(){
		return date.toString()+expediteur.toString();
	}
}
