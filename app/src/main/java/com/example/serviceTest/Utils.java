package com.example.serviceTest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static String getSha1(String str) {
            if (str == null) {
                return null;
            }
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                messageDigest.update(str.getBytes());
                byte[] digest = messageDigest.digest();
                StringBuilder sb = new StringBuilder();
                int len = digest.length;
                for (int j = 0; j < len; j++) {
                    sb.append(CHARS[(digest[j] >> 4) & 15]);
                    sb.append(CHARS[digest[j] & 15]);
                }
                return sb.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

}




