package pki.messagerie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;

import pki.Message;
import pki.annuaire.Personne;
import pki.annuaire.PersonneNonTrouveeException;
import pki.annuaire.ServeurAnnuaire;
import pki.annuaire.ServeurAnnuaireImpl;

@SuppressWarnings("serial")
public class Stockage implements Serializable {

	private String nomRepertoire;
	private ServeurAnnuaire annuaire;
	
	public Stockage(String nomRepertoire, ServeurAnnuaire serveurAnnuaire){
		this.nomRepertoire = nomRepertoire;
		annuaire = serveurAnnuaire;
	}
	
	public void enregistrerMessage(Message m) throws FileNotFoundException, IOException{
		if(annuaire.estInscrit(m.getDestinataire()) && annuaire.estInscrit(m.getExpditeur())){
			String fileName = nomRepertoire+m.getDestinataire().toString()+"/"+m.toString()+".msg";
			fileName = fileName.replaceAll("\\s", "_");
			File fichier = new File(fileName);
			ObjectOutputStream flux = new ObjectOutputStream(new FileOutputStream(fichier));
			flux.writeObject(m);
			flux.close();
		}
	}
	
	public static void main(String[] args){
		
		try {
			ServeurAnnuaire annuaire = new ServeurAnnuaireImpl();
			Stockage messagerie = new Stockage("msg/",annuaire);
			Personne exp = annuaire.getPersonne("Gerard", "Norbert");
			Personne dest = annuaire.getPersonne("Connard", "Jean-Michel");
			Message m = new Message(exp,dest,"fzfze","faefa");
			messagerie.enregistrerMessage(m);
		} catch (PersonneNonTrouveeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
