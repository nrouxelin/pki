package pki.certification;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
public class Certificat implements Serializable{
	
	//Attribtus
	private int id;
	private String nom;//nom de la personne
	private String cleSignature;//clé publique pour déchiffrer la signature
	private String cleEcriture;//clé publique pour chiffrer les messages
	private LocalDate dateFin; //date de péremption
	private boolean idModifie;
	
	
	//Constructeur
	/**
	 * @param nom Nom de la personne
	 * @param cleSignature clé publique pour déchiffrer la signature
	 * @param cleEcriture clé publique pour chiffrer les messages
	 */
	public Certificat(String nom, String cleSignature, String cleEcriture){
		this.nom 		  = nom;
		this.cleEcriture  = cleEcriture;
		this.cleSignature = cleSignature;
		this.id = 0;
		idModifie = false;
		
		//Date de fin
		LocalDate aujourdhui = LocalDate.now();
		this.dateFin = aujourdhui.plusYears(1);//le certificat est valable un an
	}
	
	//Getters
	public String getNom(){
		return nom;
	}
	
	public String getCleEcriture(){
		return cleEcriture;
	}
	
	public String getCleSignature(){
		return cleSignature;
	}
	public LocalDate getDateFin(){
		return dateFin;
	}
	public int getId(){
		return id;
	}
	
	public String toString(){
		return "#"+id+" : "+nom;
	}
	
	public void setId(int id){
		if(!idModifie){
			this.id = id;
			idModifie = true;
		}
	}
}
