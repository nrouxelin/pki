package pki;
import java.io.Serializable;
import java.time.LocalDateTime;

import pki.annuaire.Personne;

@SuppressWarnings("serial")
public class Message implements Serializable {

	private Personne expediteur;
	private Personne destinataire;
	private LocalDateTime date;
	private String message;
	private String signature;
	
	public Message(Personne exp, Personne dest){
		expediteur = exp;
		destinataire = dest;
		date = LocalDateTime.now();
		message = "";
		signature ="";
	}
	
	public Message(Personne exp, Personne dest, String msg, String s){
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
	
	public String getMessage(){
		return message;
	}
	
	public String getSignature(){
		return signature;
	}
	
	public void setMessage(String msg){
		message = msg;
	}
	
	public void setSignature(String s){
		signature = s;
	}
	
	public String toString(){
		return date.toString()+expediteur.toString();
	}
}
