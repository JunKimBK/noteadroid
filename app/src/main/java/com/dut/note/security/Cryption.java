package com.dut.note.security;

import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by HoVanLy on 22-Sep-15.
 */
public class Cryption {
    // AES ( Advanced Encryption Standard) ALGORITHM
    private static final String ALGORITHM = "AES";
    private static final int ITERATION_COUNT = 96;
    private static final int KEY_LENGTH = 128;
    //Create Hash table for different key
    private static final byte[] SALT = {
            (byte) 0xA2, (byte) 0xB7, (byte) 0xC8, (byte) 0xD1,
            (byte) 0x69, (byte) 0x96, (byte) 0xE9, (byte) 0x03
    };
    /*AES: Working with input data block 128 bits and  128, 196, 256 bits key*/

    /*Encrypt*/
    public String enCrypt(String planText, String pass) {
        try {
            // Create key for encrypt
            Key key = createKey(pass);
            // Cipher object for encrypt use AES algorithm
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Start Cipher object with encrypt mode
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // Handler Encrypt by call  doFinal method
            byte[] encrypted = cipher.doFinal(planText.getBytes());
            // Return encrypt text
            return new String(encrypted);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*Decrypt*/
    public String deCrypt(String encryptText, String pass) {
        try {
            // Create key for decrypt
            Key key = createKey(pass);
            // Cipher object for decrypt use AES algorithm
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Start Cipher object with decrypt mode
            cipher.init(Cipher.DECRYPT_MODE, key);
            // Handler decrypt by call  doFinal method
            byte[] decrypted = cipher.doFinal(encryptText.getBytes());
            // Return decrypt text
            return new String(decrypted);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // Encrypt by MD5 algorithm
    public String enCryptMD5(String Password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(Password.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashText = number.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    //Create key for Crypt
    private Key createKey(String keyText) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(keyText.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH);
            SecretKey tmp = keyFactory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");
            return key;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
