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
	
	
	/**
	 * Constructeur
	 * 
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
	/**
	 * @return
	 */
	public Personne getPersonne(){
		return personne;
	}
	
	/**
	 * @return
	 */
	public Key getCleEcriture(){
		return cleEcriture;
	}
	
	/**
	 * @return
	 */
	public Key getCleSignature(){
		return cleSignature;
	}
	
	/**
	 * @return
	 */
	public LocalDateTime getDateFin(){
		return dateFin;
	}
	
	/**
	 * @return
	 */
	public LocalDateTime getDateDebut(){
		return dateDebut;
	}
	
	/**
	 * @return
	 */
	public int getId(){
		return id;
	}
	
	public String toString(){
		return "Id:"+id+" : "+personne;
	}
	
	/**
	 * @param id
	 */
	public void setId(int id){
		if(!idModifie){
			this.id = id;
			idModifie = true;
		}
	}
	
	/**
	 * @param date
	 */
	public void setDateFin(LocalDateTime date){
		dateFin = date;
	}
	
}
