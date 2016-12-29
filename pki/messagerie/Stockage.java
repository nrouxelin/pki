package pki.messagerie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.util.ArrayList;

import pki.Message;
import pki.annuaire.Personne;

@SuppressWarnings("serial")
public class Stockage implements Serializable {

	private String nomRepertoire;
	
	
	private static FilenameFilter filtreExtension = new FilenameFilter(){
		public boolean accept(File dir, String name){
			return name.endsWith(".msg");
		}
	};
	
	
	public Stockage(String nomRepertoire){
		this.nomRepertoire = nomRepertoire;
	}
	
	public void enregistrerMessage(Message m) throws FileNotFoundException, IOException{
		String folderName = nomRepertoire+m.getDestinataire()+"/";
		folderName = folderName.replaceAll("\\s","_");
		File repertoire = new File(folderName);
		if(!(repertoire.exists() && repertoire.isDirectory())){
			repertoire.mkdirs();
		}
		String fileName = nomRepertoire+m.getDestinataire().toString()+"/"+m.toString()+".msg";
		fileName = fileName.replaceAll("\\s", "_");
		File fichier = new File(fileName);
		ObjectOutputStream flux = new ObjectOutputStream(new FileOutputStream(fichier));
		flux.writeObject(m);
		flux.close();
	}
	
	public Message lireMessage(File fichier) throws ClassNotFoundException, IOException{
		ObjectInputStream flux = new ObjectInputStream(new FileInputStream(fichier));
		Message m = (Message) flux.readObject();
		flux.close();
		return m;
	}
	
	public ArrayList<Message> getMessages(Personne destinataire) throws ClassNotFoundException, IOException{
		ArrayList<Message> messages = new ArrayList<Message>();
		String nomDossier = nomRepertoire+destinataire+"/";
		nomDossier = nomDossier.replaceAll("\\s", "_");
		File repertoire = new File(nomDossier);
		
		if(repertoire.exists() && repertoire.isDirectory()){
			File[] fichiers = repertoire.listFiles(filtreExtension);
			for(File f : fichiers){
				messages.add(lireMessage(f));
			}
		}
		
		return messages;
	}
	
	
}
