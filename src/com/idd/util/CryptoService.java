package com.idd.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public final class CryptoService {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKey secretKey;

    public CryptoService(byte[] keyBytes) {
        if (keyBytes.length != 16 && keyBytes.length != 32) {
            throw new IllegalArgumentException("AES-128 requires 16 or 32 bytes key");
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }
    
    public CryptoService(String secretKey) {
    	if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException(
                "Missing AES key"
            );
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secretKey.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                "Invalid AES key: must be Base64 encoded", e
            );
        }

        if (keyBytes.length != 16 && keyBytes.length != 32) {
            throw new IllegalArgumentException("AES-128 requires 16 or 32 bytes key");
        }
        
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
	}

	public static void generateAESKey() throws NoSuchAlgorithmException, IOException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		SecretKey secretKey = keyGenerator.generateKey();

		String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

		System.out.println("Key (Base64): " + encodedKey);
		System.out.println();
	}


    public String encrypt(String plaintext) {
        if (plaintext == null) return null;

        try {
            byte[] iv = randomBytes(IV_LENGTH);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH, iv));

            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] out = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(cipherText, 0, out, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("AES encrypt failed", e);
        }
    }

    public String decrypt(String encrypted) {
        try {
            byte[] all = Base64.getDecoder().decode(encrypted);
            if (all.length <= IV_LENGTH) {
                throw new IllegalArgumentException("Invalid encrypted payload");
            }
            byte[] iv = Arrays.copyOfRange(all, 0, IV_LENGTH);
            
            byte[] cipherText = Arrays.copyOfRange(all, IV_LENGTH, all.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH, iv));

            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("AES decrypt failed", e);
        }
    }

    public String decryptIfEncrypted(String value) {
        if (!looksLikeEncrypted(value)) return value;
        return decrypt(value);
    }

    private boolean looksLikeEncrypted(String value) {
        try {
            byte[] decoded = Base64.getDecoder().decode(value);
            return decoded.length > IV_LENGTH + 16;
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] randomBytes(int len) {
        byte[] b = new byte[len];
        new SecureRandom().nextBytes(b);
        return b;
    }
    
    public static void main(String[] args) throws Exception {
//    	generateAESKey();
    	CryptoService util = new CryptoService(Base64.getDecoder().decode( "tYtPIDwQBTuevzK8NhOXQw==" ));
    	String sample = "Exim@123";
    	String enc = util.encrypt(sample);
    	String dec = util.decryptIfEncrypted(enc);
    	System.out.println("Encrypted: " + enc); System.out.println("Decrypted: " + dec);
    }
}