package pki;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import pki.annuaire.Personne;
import pki.certification.Certificat;

public final class Chiffrement {

	private static Key genererCleBlowfish(){
		try {
			KeyGenerator kg = KeyGenerator.getInstance("Blowfish");
			kg.init(128);
			return kg.generateKey();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static byte[] chiffrerRSA(byte[] input, Key cleRSA) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException{
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, cleRSA);
			
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (NoSuchPaddingException e) {
			return null;
		}
		
	}
	
	public static void chiffrerMessage(Message m, String texte, Key cleRSA) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher;
		try {
			Key cleBlowfish = genererCleBlowfish();
			cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE,cleBlowfish);
			byte[] texteBytes = texte.getBytes("UTF-8");
			m.setMessage(cipher.doFinal(texteBytes));
			m.setCle(chiffrerRSA(cleBlowfish.getEncoded(),cleRSA));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static Key dechiffrerCleBlowfish(byte[] cleChiffree, Key cleRSA) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, cleRSA);

			byte[] cleDechifreeBytes = cipher.doFinal(cleChiffree);
			return new SecretKeySpec(cleDechifreeBytes, "Blowfish");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			return null;
		}
	}
	
	private static String dechiffrerBlowfish(byte[] texte, Key cle) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, cle);
		
		byte[] texteDechiffre = cipher.doFinal(texte);
		return new String(texteDechiffre);
	}
	
	public static byte[] dechiffrerRSA(byte[] input, Key cleRSA) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, cleRSA);
		
		return cipher.doFinal(input);
	}
	
	public static String dechiffrerMessage(Message m, Key cleRSA){
		Key cleBlowfish;
		
		try {
			cleBlowfish = dechiffrerCleBlowfish(m.getCle(),cleRSA);
			return dechiffrerBlowfish(m.getMessage(),cleBlowfish);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			return "Impossible de déchiffrer le message.";
		}
	}
	
	public static void signerMessage(Message m, Key cleRSA){
		try {
			byte[] digest = MessageDigest.getInstance("SHA1").digest(m.getMessage());
			m.setSignature(chiffrerRSA(digest,cleRSA));
			
		} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
		}
	}
	
	
	public static Boolean verifierSignature(Message m, Key cleRSA){
		try {
			byte[] signature = dechiffrerRSA(m.getSignature(),cleRSA);
			byte[] digest = MessageDigest.getInstance("SHA1").digest(m.getMessage());
			return MessageDigest.isEqual(signature, digest);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			return false;
		}
	}
	
	public static KeyPair genererClesRSA(){
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			return kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static void main(String[] args) throws Exception{
		Personne destinataire = new Personne("Connard","Jean-Michel");
		Personne expediteur = new Personne("Gérard","Norbert");
		
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	    keyPairGenerator.initialize(1024);
	    KeyPair keyPair = keyPairGenerator.genKeyPair();
	    KeyPair cleSignature = keyPairGenerator.generateKeyPair();
	    
	    Certificat certificat = new Certificat(destinataire,keyPair.getPublic(),keyPair.getPublic());
	    
		Message m = new Message(expediteur,destinataire);
		Chiffrement.chiffrerMessage(m, "Je m'appelle Jean-Babar",certificat.getCleEcriture());
		Chiffrement.signerMessage(m, cleSignature.getPublic());
		
		System.out.println(new String(m.getMessage()));
		System.out.println(Chiffrement.dechiffrerMessage(m,keyPair.getPrivate()));
		System.out.println(Chiffrement.verifierSignature(m, cleSignature.getPrivate()));
		
	}
}
