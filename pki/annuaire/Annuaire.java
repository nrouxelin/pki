package pki.annuaire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import pki.exceptions.UtilisateurExistantException;
import pki.exceptions.UtilisateurNonTrouveException;

@SuppressWarnings("serial")
public class Annuaire implements Serializable {
	
	private ArrayList<Personne> annuaire;
	private String nomFichier;
	private boolean initialisation;
	
	/**
	 * Constructeur
	 * 
	 * @param nomFichier nom du fichier d'annuaire
	 */
	public Annuaire(String nomFichier){
		this.nomFichier = nomFichier;
		annuaire = new ArrayList<Personne>();
		initialisation = true;
		
		File fichier = new File(nomFichier);
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(fichier);
			while(fileScanner.hasNextLine()){
				String l = fileScanner.nextLine();
				if(!l.isEmpty()){//Éviter le bug lors de la lecture d'une ligne vide
					String[] ligne = l.split(" ");
					Personne p = new Personne(ligne[0],ligne[1]);
					try {
						ajouterPersonne(p);
					} catch (UtilisateurExistantException e) {
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Fichier non-trouvé -- Création d'un annuaire vide.");
		}finally{
			if(fileScanner!=null){
				fileScanner.close();
			}
		}
		initialisation = false;
	}
	
	/**
	 * Ajoute une personne au fichier d'annuaire
	 * @param p la personne à ajouter
	 * @throws IOException
	 */
	private void ecrire(Personne p) throws IOException{
		FileWriter fw = new FileWriter(nomFichier,true);
		fw.write("\n"+p);
		fw.close();
	}
	
	/**
	 * Ajoute une personne à l'annuaire
	 * 
	 * @param p la personne à ajouter
	 * @throws UtilisateurExistantException
	 * @throws IOException
	 */
	public void ajouterPersonne(Personne p) throws UtilisateurExistantException, IOException{
		try{
			Personne p1 = getPersonne(p.getNom(),p.getPrenom());
			if(p1!=null){
				throw new UtilisateurExistantException();
			}
		}catch(UtilisateurNonTrouveException e){
			annuaire.add(p);
			if(!initialisation){
				ecrire(p);
			}
		}
	}
	
	/**
	 * récupère une personne par son nom et prénom
	 * @param nom nom de la personne
	 * @param prenom prénoom de la personne
	 * @return la personne correspondante
	 * @throws UtilisateurNonTrouveException
	 */
	public Personne getPersonne(String nom, String prenom) throws UtilisateurNonTrouveException{
		for(Personne p : annuaire){
			if(p.getNom().equals(nom) && p.getPrenom().equals(prenom)){
				return p;
			}
		}
		throw new UtilisateurNonTrouveException();
	}

	/**
	 * Supprime une personne de l'annuaire
	 * 
	 * @param p la personne à supprimer
	 * @throws UtilisateurNonTrouveException
	 * @throws IOException
	 */
	public void supprimerPersonne(Personne p) throws UtilisateurNonTrouveException, IOException{
		if(!annuaire.remove(p)){
			throw new UtilisateurNonTrouveException();
		}else{
			File f= new File(nomFichier);
			f.delete();
			for(Personne pers : annuaire){
				ecrire(pers);
			}
		}
	}
	
	/**
	 * Supprime une personne de l'annuaire
	 * 
	 * @param nom de la personne à supprimer
	 * @param prenom de la personne à supprimer
	 * @throws UtilisateurNonTrouveException
	 * @throws IOException
	 */
	public void supprimerPersonne(String nom, String prenom) throws UtilisateurNonTrouveException, IOException{
		Personne p = getPersonne(nom,prenom);
		supprimerPersonne(p);
	}
	
	/**
	 * Test l'inscription d'une personne
	 * @param pers la personne dont on test l'inscription
	 * @return vrai si la personne est inscrite, faux sinon
	 */
	public boolean estInscrit(Personne pers){
		for(Personne p : annuaire){
			if(p.equals(pers)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Test l'inscription d'une personne
	 * @param nom le nom de la personne à tester
	 * @param prenom le prénom de la personne à tester
	 * @return vrai si la personne est inscrite, faux sinon
	 */
	public boolean estInscrit(String nom, String prenom){
		return estInscrit(new Personne(nom,prenom));
	}
	
	/**
	 * @return
	 */
	public ArrayList<Personne> getPersonnes(){
		return annuaire;
	}
	
	/**
	 * @return
	 */
	public int getNbPersonnes(){
		return annuaire.size();
	}
		
	@Override
	public String toString(){
		String res="";
		for(Personne p : annuaire){
			res = res+"\n"+p;
		}
		return res;
	}
}
