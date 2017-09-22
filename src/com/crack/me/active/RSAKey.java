package com.crack.me.active;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

/**
 * 密钥工具,提供加密解密，并把密钥对存储到文件
 * 
 * @author macbookpro
 */
public class RSAKey {

    public static String PRIVATE_KEY_FILENAME = "privateKey.bytes";
    public static String PUBLIC_KEY_FILENAME = "publicKey.bytes";

    /**
     * 生成密钥对，并存储到文件中<br>
     * 默认文件为
     * <p>
     * <li>privateKey.bytes(加密key)
     * <li>publicKey.bytes(解密key)
     */
    public void generateKeyFile() {
	try {
	    /* 生成密钥对 */
	    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
	    keyPairGen.initialize(1024, new SecureRandom());
	    KeyPair keyPair = keyPairGen.generateKeyPair();

	    /* 解密key */
	    PublicKey pubkey = keyPair.getPublic();
	    /* 加密key */
	    PrivateKey prikey = keyPair.getPrivate();

	    saveFile(new File(PUBLIC_KEY_FILENAME), pubkey.getEncoded());
	    saveFile(new File(PRIVATE_KEY_FILENAME), prikey.getEncoded());
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}
    }

    /**
     * 存储成文件
     * 
     * @param file
     * @param data
     */
    public void saveFile(File file, byte[] data) {
	try {
	    FileOutputStream localFileOutputStream = new FileOutputStream(file);
	    localFileOutputStream.write(data);
	    localFileOutputStream.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * 加密<br>
     * 使用默认加密Key
     * 
     * @param text
     *            明文
     * @return 密文
     */
    public String encryption(String text) {
	return encryption((File) null, text);
    }

    public String encryption(String privateKeyFilename, String text) {
	if (privateKeyFilename == null || "".equals(privateKeyFilename)) {
	    privateKeyFilename = PRIVATE_KEY_FILENAME;
	}
	return encryption(new File(privateKeyFilename), text);
    }

    /**
     * 加密
     * 
     * @param privateKeyFile
     *            加密Key文件
     * @param text
     *            明文
     * @return 密文
     */
    public String encryption(File privateKeyFile, String text) {
	String enText = null;
	try {
	    if (privateKeyFile == null || !privateKeyFile.exists()) {
		privateKeyFile = new File(PRIVATE_KEY_FILENAME);
	    }
	    InputStream in = new FileInputStream(privateKeyFile);
	    byte[] data = new byte[in.available()];
	    in.read(data);
	    in.close();

	    Key key = RSAPrivateCrtKeyImpl.newKey(data);

	    enText = encryption(key, text);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return enText;
    }

    /**
     * 加密 <br>
     * 明文转换成byte[],然后通过rsa加密，之后结果再通过Hex转换加密16进制
     * 
     * @param privateKey
     *            加密key
     * @param text
     *            明文
     * @return 密文
     */
    public String encryption(Key privateKey, String text) {
	String enText = null;
	if (privateKey != null) {
	    try {
		Cipher localCipher = Cipher.getInstance("rsa");
		localCipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] arrayOfByte = localCipher.doFinal(text.getBytes());
		enText = new String(Hex.encodeHex(arrayOfByte));
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return enText;
    }

    /**
     * 解密
     * 
     * @param text
     * @return
     */
    public String decryption(String text) {
	return decryption((File) null, text);
    }

    /**
     * 解密
     * 
     * @param publicKeyFilename
     * @param text
     * @return
     */
    public String decryption(String publicKeyFilename, String text) {
	if (publicKeyFilename == null || "".equals(publicKeyFilename)) {
	    publicKeyFilename = PUBLIC_KEY_FILENAME;
	}
	return decryption(new File(publicKeyFilename), text);
    }

    /**
     * 解密
     * 
     * @param publicKeyFile
     *            解密key文件
     * @param text
     *            密文
     * @return 明文
     */
    public String decryption(File publicKeyFile, String text) {
	String deText = null;
	try {
	    if (publicKeyFile == null || !publicKeyFile.exists()) {
		publicKeyFile = new File(PUBLIC_KEY_FILENAME);
	    }
	    InputStream in = new FileInputStream(publicKeyFile);
	    byte[] data = new byte[in.available()];
	    in.read(data);
	    in.close();

	    Key key = new RSAPublicKeyImpl(data);
	    deText = decryption(key, text);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return deText;
    }

    /**
     * 解密<br>
     * 首先要把密文（16进制）恢复到普通字符，然后通过rsa解密，结果直接就是明文
     * 
     * @param publicKey
     *            解密key
     * @param text
     *            密文
     * @return 明文
     */
    public String decryption(Key publicKey, String text) {
	String deText = null;
	if (publicKey != null) {
	    try {
		Cipher rsaCipher = Cipher.getInstance("rsa");
		rsaCipher.init(Cipher.DECRYPT_MODE, publicKey);
		byte[] handleData = Hex.decodeHex(text.toCharArray());
		byte[] bytes = rsaCipher.doFinal(handleData);
		deText = new String(bytes);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return deText;
    }

    

}
