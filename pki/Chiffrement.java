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

public final class Chiffrement {

	/**
	 * @return les clefs de chiffrements
	 */
	private static Key genererCleBlowfish(){
		try {
			KeyGenerator kg = KeyGenerator.getInstance("Blowfish");
			kg.init(128);
			return kg.generateKey();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	/** 
	 * chiffre à partir d'une entrée de type byte[]
	 * 
	 * @param input entrée non chiffré
	 * @param cleRSA la clef de cryptage
	 * @return entrée cryptée
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 */
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
	
	/**
	 * chiffre un message à partir d'une entrée de type String
	 * assigne le message et la clef Blowfish cryptés à une instance de Message
	 * 
	 * @param m l'instance de message auquel sera assigné le texte et la clef chiffrés
	 * @param texte non chiffré
	 * @param cleRSA la clef RSA de cryptage
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static void chiffrerMessage(Message m, String texte, Key cleRSA) 
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
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
	
	/**
	 * Déchiffre une clef pour l'utiliser pour les messages
	 * 
	 * @param cleChiffree la clef à déchiffrer
	 * @param cleRSA la clef de décryptage
	 * @return clef la clef déchiprée poue les messages
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private static Key dechiffrerCleBlowfish(byte[] cleChiffree, Key cleRSA) 
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
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
	
	/**
	 * Déchiffre un texte avec Blowfish
	 * 
	 * @param texte le texte à déchiffrer
	 * @param cle la clef pour le ddéchiffrer
	 * @return le texte déchiffré
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private static String dechiffrerBlowfish(byte[] texte, Key cle) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, 
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, cle);
		
		byte[] texteDechiffre = cipher.doFinal(texte);
		return new String(texteDechiffre);
	}
	
	/**
	 * déchiffre avec RSA
	 * 
	 * @param input l'entrée à déchiffrer
	 * @param cleRSA la clef RSA
	 * @return l'entrée déchiffrée
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static byte[] dechiffrerRSA(byte[] input, Key cleRSA) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, cleRSA);
		
		return cipher.doFinal(input);
	}
	
	/**
	 * Déchiffre le texte d'un message
	 * 
	 * @param m le message
	 * @param cleRSA la clef RSA de déchiffrage
	 * @return le texte du message déchiffré
	 */
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
	
	/**
	 * Signe un message avec une clef RSA
	 * 
	 * @param m le message à signer
	 * @param cleRSA la clef RSA correspondante
	 */
	public static void signerMessage(Message m, Key cleRSA){
		try {
			byte[] digest = MessageDigest.getInstance("SHA1").digest(m.getMessage());
			m.setSignature(chiffrerRSA(digest,cleRSA));
			
		} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
		}
	}
	
	
	/**
	 * Vérifie la signature d'un message
	 * 
	 * @param m le message
	 * @param cleRSA la clef RSA correspondante
	 * @return vrai si la signature est correcte, faux sinon
	 */
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
	
	/**
	 * @return la paire de clef générée
	 */
	public static KeyPair genererClesRSA(){
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			return kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}
