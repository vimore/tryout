package com.securityx.modelfeature.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Class responsible for:
 * 1. Encryption using AES
 * 2. Decryption using AES
 *
 * High Level Algorithm :
 * 1. Generate a AES key (specify the Key size during this phase)
 * 2. Create the Cipher
 * 3. To Encrypt : Initialize the Cipher for Encryption
 * 4. To Decrypt : Initialize the Cipher for Decryption
 *
 *
 */

public class EncryptionUtil {

    private  final static Logger LOGGER = LoggerFactory.getLogger(EncryptionUtil.class);

    /**
     * Algorithm used by the KeyGenerator to generate key
     * ref: http://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyGenerator
     */
    private static String ALGORITHM_NAME = "AES";
    /**
     *  The name of the transformation
     *      a. Algorithm name - here it is AES
     * 		b. Mode - here it is CBC mode
     * 		c. Padding - e.g. PKCS7 or PKCS5
     *
     * 	ref: http://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher
     */
    private static String transformation = "AES/CBC/PKCS5PADDING";


    /**
     * @param plainText String to be encrypted
     * @return Returns encrypted version of the input plainText
     */
    public static Optional<String> encrypt(String plainText) {
        if(plainText == null){
            return Optional.empty();
        }
        try {
            SecretKeySpec secretKeySpec = getSecretKey();
            byte[] iv = getIVKey();

            Cipher aesCipherForEncryption = Cipher.getInstance(transformation);
            aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            byte[] byteDataToEncrypt = plainText.getBytes();
            byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
            String strCipherText = Base64.encodeBase64String(byteCipherText);
            return Optional.of(strCipherText);
        } catch (Exception e) {
            LOGGER.error(" Exception during Encryption " + e);
        }
        return Optional.empty();
    }

    /**
     * @param encryptedText String to be decrypted
     * @return Returns decrypted version of the input encryptedText
     */
    public static Optional<String> decrypt(String encryptedText) {
        try{
            SecretKeySpec secretKeySpec = getSecretKey();
            byte[] iv = getIVKey();

            //For decryption, follow the same steps as encrypt(), in reverse order
            byte [] byteCipherText = Base64.decodeBase64(encryptedText);
            Cipher aesCipherForDecryption = Cipher.getInstance(transformation);
            aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            byte[] byteDecryptedText = aesCipherForDecryption.doFinal(byteCipherText);
            return Optional.of(new String(byteDecryptedText));
        } catch (Exception e) {
            LOGGER.error(" Exception during Decryption " + e);
        }
        return Optional.empty();
    }

    private static SecretKeySpec getSecretKey() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String secretKey = "2117966FA8114AFD415B728CEE5CE";
        byte[] key = secretKey.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        return new SecretKeySpec(key, ALGORITHM_NAME);
    }

    private static byte [] getIVKey() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String ivSecretKey = "D8726647F6B385343318BB21A718C";
        byte[] iv = ivSecretKey.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte [] ivKey = sha.digest(iv);;
        ivKey = Arrays.copyOf(ivKey, 16); // use only first 128 bit
        return ivKey;
    }
}