package fruit.banana.crypto;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecureMessage {
	
	static String fileName="data/keystore.secure";
	static String seperator="|-|";
	static String ALGORITHM = "AES";
	static String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	static int keysize=128;
	static byte[] salt= new byte[] {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
        };
	String password="password";
	static int countLimit=3;
	byte[] keyCheckSum=null;
	byte[] iv=null;
	SecretKey secret=null;
	Scanner scnr=null;

	public static void main(String[] args) {
		/* initialize password */
		
		/* password verification */
		
		/* functions 0 change password, 1 read, 2 update, 3 add record, 4 delete record, 5 exit */
		RecordsControl rc=new RecordsControl();
		Scanner sc=new Scanner(System.in);
		int selection=-1;
		do{
			System.out.println("Please selection function: 1. read record; 2. update/add record; 3. delete record; 4. exit; 0. change password.");
			selection=sc.nextInt();
			switch(selection){
			case 1:
				System.out.println("Please type the keyword or 0 to show all headers");
				String keyword=sc.next();
				Record record=rc.readRecord(keyword);
				if(record==null){
					System.out.println("Record not found");
				}else{
					System.out.println("Record found");
					System.out.println("Data: "+record.getData());
				}
				break;
			case 2:
				System.out.println("Please key in the header.");
				String header=sc.next();
				System.out.println("Please key in other information.");
				String data=sc.next();
				record=new Record();
				record.setData(data);
				record.setHeader(header);
				rc.addRecord(record);
				break;
			}
		}while(selection!=0);
		
	}
	
	public void updateRecord() throws IOException{
		Path path = Paths.get("test.txt");
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll("foo", "bar");
		Files.write(path, content.getBytes(charset));
	}

	public void addRecord(){
		System.out.println("Confirm to add this record? Y/N: ");
		String confirmation=null;
		if(scnr.hasNext()) confirmation=scnr.nextLine();
		if(confirmation.equalsIgnoreCase("y")){
			//TODO
		}
	}
	
	public String findRecord(String recordHeader){
		String result=null;
		result=getEncryptedRecord(recordHeader);
		String encryptedBody=trimHeader(result);
		String decryptedBody=decrypt(encryptedBody);
		return recordHeader+":"+decryptedBody;
	}
	
	public String getEncryptedRecord(String key){
		Scanner sc=null;
		String encryptedRrd=null;
		try {
			sc=new Scanner(new FileReader(fileName));
			while(sc.hasNext()){
				encryptedRrd=sc.nextLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("<-----Warning: File Not Found------>");
			e.printStackTrace();
		} finally{
			if(sc!=null)
				sc.close();
		}
		return encryptedRrd;
	}

	public String decryptRecord(String str){
		
		return null;
	}
	
	private String trimHeader(String line){
		String []ss=line.split(seperator);
		if(ss.length!=2){
			System.err.println("<----Warning: parse line fail. Number of elements dismatch.----->");
			return null;
		}else{
			return ss[1];
		}
	}
	
	public static String bytesToHex(byte[] in) {
	    final StringBuilder builder = new StringBuilder();
	    for(byte b : in) {
	        builder.append(String.format("%02x", b));
	    }
	    return builder.toString();
	}
	
	public static byte[] hexToBytes(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	public String createNewRecord(){
		System.out.println("Please tell me what account is this: ");
		String accountName=null;
		if(scnr.hasNext()) accountName=scnr.nextLine();
		System.out.println("Please tell me the information you want to save: ");
		String accountInfo=null;
		if(scnr.hasNext()) accountName=scnr.nextLine();
		accountInfo=encrypt(accountInfo);
		return accountName+seperator+accountInfo;
	}
	
	public String encrypt(String s){
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
			byte[] ciphertext = cipher.doFinal(s.getBytes("UTF-8"));
			return bytesToHex(ciphertext);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String decrypt(String hexString){
		// reinit cypher using param spec
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			return new String(cipher.doFinal(hexToBytes(hexString)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void createKey(){
		/* Derive the key, given password and salt. */
	    try{
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, keysize);
			SecretKey tmp = factory.generateSecret(spec);
			secret = new SecretKeySpec(tmp.getEncoded(),ALGORITHM);
			/* Encrypt the message. */
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }

	}
	
	public boolean verifyPassword(int count){
		if(count==0){
			System.out.println("Exceed password trial limit, exit...");
			System.exit(1);
		}
		System.out.println("Please type in the password: ");
		String password=null;
		if(scnr.hasNext()) password=scnr.next();
		System.out.println("The password typed is: "+password);
		byte[] key=calcCheckSum(password);
		boolean flag= Arrays.equals(key, keyCheckSum);
		if(!flag){
			System.out.println("The password does not match.");
			flag=verifyPassword(count-1);
		}
		return flag;
	}
	
	public byte[] calcCheckSum(String password){
		byte[] key=null;
		try {
			key = password.getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
//			key = Arrays.copyOf(key, 16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}
		
	public void deleteRecord(String recordHeader){
		System.out.println("Record deleted");
	}
	
	public void setPassword(){
		System.out.println("Please type in new password: ");
		String password=null;
		if(scnr.hasNext()) password=scnr.next();
		System.out.println("new password will be: "+password);
		
		keyCheckSum=calcCheckSum(password);
	}
}
