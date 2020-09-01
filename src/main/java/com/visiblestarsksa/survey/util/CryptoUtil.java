package com.visiblestarsksa.survey.util;

import lombok.extern.slf4j.Slf4j;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
public class CryptoUtil {

    private static final String SECRET_KEY = "secret!!";
    private static final String SALT = "salt!";
    private static IvParameterSpec ivParameterSpec = null;
    private static SecretKeySpec secretKeySpec = null;

    static {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            ivParameterSpec = new IvParameterSpec(iv);
            SecretKeyFactory secretKeyFactory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch (Exception e) {
            log.error("error at CryptoUtil", e);
            throw new RuntimeException("Error at CryptoUtil init");
        }
    }

    public static String encrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes("UTF-8")));
        } catch (Exception ex) {
            log.error("Error while encrypt", ex);
        }
        return "-1";
    }

    public static String decrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(input)));
        } catch (Exception ex) {
            log.error("Error while decrypt", ex);
        }
        return "-1";
    }

    public static void main(String[] args) {
        System.out.println("encrypted: " + encrypt("1"));
        System.out.println("decrypted: " + decrypt(encrypt("1")));
    }
}
