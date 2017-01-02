package pki.annuaire;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Personne implements Serializable {
	private String prenom;
	private String nom;
	
	/**
	 * Constructeur
	 * 
	 * @param n le nom
	 * @param p le prénom
	 */
	public Personne(String n, String p){
		prenom = p;
		nom = n;
	}
	
	/**
	 * @return
	 */
	public String getNom(){
		return nom;
	}
	
	/**
	 * @return
	 */
	public String getPrenom(){
		return prenom;
	}
	
	@Override
	public String toString(){
		return nom+" "+prenom;
	}
	
	@Override
	public boolean equals(Object obj){
		if(getClass() == obj.getClass()){
			return (nom.equals(((Personne) obj).getNom())) &&
				   (prenom.equals(((Personne) obj).getPrenom()));
		}
		return false;
	}
}
