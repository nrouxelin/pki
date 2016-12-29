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
	
	public Personne getPersonne(String nom, String prenom) throws UtilisateurNonTrouveException{
		for(Personne p : annuaire){
			if(p.getNom().equals(nom) && p.getPrenom().equals(prenom)){
				return p;
			}
		}
		throw new UtilisateurNonTrouveException();
	}
	
	private void ecrire(Personne p) throws IOException{
		FileWriter fw = new FileWriter(nomFichier,true);
		fw.write("\n"+p);
		fw.close();
	}
	
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
	
	public void supprimerPersonne(String nom, String prenom) throws UtilisateurNonTrouveException, IOException{
		Personne p = getPersonne(nom,prenom);
		supprimerPersonne(p);
	}
	
	public boolean estInscrit(Personne pers){
		for(Personne p : annuaire){
			if(p.equals(pers)){
				return true;
			}
		}
		return false;
	}
	
	public boolean estInscrit(String nom, String prenom){
		return estInscrit(new Personne(nom,prenom));
	}
	
	public ArrayList<Personne> getPersonnes(){
		return annuaire;
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
