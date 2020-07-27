package com.epopcon.advatar.common.util;

import android.util.Base64;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class AESEncrypter implements Encrypter {

	private static final byte[] SALT = {
		 (byte) 0x45, (byte) 0x50, (byte) 0x4F, (byte) 0x50,
	     (byte) 0x43, (byte) 0x4F, (byte) 0x4E, (byte) 0x21, (byte) 0x21
    };
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 128;
    private Cipher ecipher;
    private Cipher dcipher;

    public AESEncrypter(String passPhrase) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1And8bit");
        KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        ecipher = Cipher.getInstance("AES");
        ecipher.init(Cipher.ENCRYPT_MODE, secret);
        dcipher = Cipher.getInstance("AES");
        dcipher.init(Cipher.DECRYPT_MODE, secret);
    }

    private byte[] decrypt(byte[] encrypt) throws Exception {
        return dcipher.doFinal(encrypt);
    }

    private byte[] encrypt(byte[] plain) throws Exception {
        return ecipher.doFinal(plain);
    }
 
    public String encrypt(String encrypt) throws Exception {
        byte[] bytes = encrypt.getBytes("UTF8");
        byte[] encrypted = encrypt(bytes);

        // \n 이 왜 붙는지 모르겠음.
        return Base64.encodeToString(encrypted, Base64.DEFAULT).replaceAll("\n", "");
    }
 
    public String decrypt(String encrypt) throws Exception {
        byte[] bytes = Base64.decode(encrypt, Base64.DEFAULT);
        byte[] decrypted = decrypt(bytes);
        return new String(decrypted, "UTF8");
    }
}
