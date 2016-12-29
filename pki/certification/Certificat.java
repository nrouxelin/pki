package pki.certification;

import java.io.Serializable;
import java.security.Key;
import java.time.LocalDateTime;

import pki.annuaire.Personne;

@SuppressWarnings("serial")
public class Certificat implements Serializable{
	
	//Attribtus
	private int id;
	private Personne personne;//nom de la personne
	private Key cleSignature;//clé publique pour déchiffrer la signature
	private Key cleEcriture;//clé publique pour chiffrer les messages
	private LocalDateTime dateDebut;
	private LocalDateTime dateFin; //date de péremption
	private boolean idModifie;
	private byte[] signature;
	
	
	//Constructeur
	/**
	 * @param nom Nom de la personne
	 * @param cleSignature clé publique pour déchiffrer la signature
	 * @param cleEcriture clé publique pour chiffrer les messages
	 */
	public Certificat(Personne p, Key cleSignature, Key cleEcriture){
		this.personne		  = p;
		this.cleEcriture  = cleEcriture;
		this.cleSignature = cleSignature;
		this.id = 0;
		idModifie = false;
		
		//Date de fin
		dateDebut = LocalDateTime.now();//La validité du certificat commence aujourd'hui
		this.dateFin = dateDebut.plusYears(1);//le certificat est valable un an
	}
	
	//Getters
	public Personne getPersonne(){
		return personne;
	}
	
	public Key getCleEcriture(){
		return cleEcriture;
	}
	
	public Key getCleSignature(){
		return cleSignature;
	}
	public LocalDateTime getDateFin(){
		return dateFin;
	}
	public LocalDateTime getDateDebut(){
		return dateDebut;
	}
	public int getId(){
		return id;
	}
	
	public String toString(){
		return "Id:"+id+" : "+personne;
	}
	
	public void setId(int id){
		if(!idModifie){
			this.id = id;
			idModifie = true;
		}
	}
	
	public void setDateFin(LocalDateTime date){
		dateFin = date;
	}
	
	public void setSignature(byte[] s){
		signature = s;
	}
	
	public byte[] getSignature(){
		return signature;
	}
	
}
