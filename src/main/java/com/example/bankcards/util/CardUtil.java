package com.example.bankcards.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CardUtil {

    private static final String MASK = "**** **** **** ";
    
    /**
     * Возвращает последние 4 цифры карты, например 1234
     */
    public static String getLast4(String pan) {
    	return pan.substring(pan.length() - 4);
    }	
    

    /**
     * Возвращает маскированный номер карты, например **** **** **** 1234
     */	
    public static String maskCardNumber(String pan) {
        if (pan == null || pan.length() < 4) {
            return "**** **** **** ****";
        }
        return MASK + getLast4(pan);
    }
    
    /**
     *  Метод для хеширования PAN (SHA-256)
     */
    public static String hashPan(String pan) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(pan.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found", e);
        }
    }
    
    
}