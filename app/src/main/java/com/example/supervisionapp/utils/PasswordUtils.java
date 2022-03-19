package com.example.supervisionapp.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {
    public static String createSha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(text.getBytes());
            byte[] digest = md.digest();

            return Base64.encodeToString(digest, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
