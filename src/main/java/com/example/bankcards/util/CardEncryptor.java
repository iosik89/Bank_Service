package com.example.bankcards.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class CardEncryptor {

    @Value("${encryption.key}")
    private String secretKey;
    
    private SecretKeySpec secretKeySpec;

    @PostConstruct
    public void init() {
        // создаём объект ключа AES с secretKey
        secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
    }

    public String encrypt(String plainText) {
        try {
            if (plainText == null) return null;
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при шифровании номера карты", e);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            if (encryptedText == null) return null;
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при расшифровке номера карты", e);
        }
    }

}
