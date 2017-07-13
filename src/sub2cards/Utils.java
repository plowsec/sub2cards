package sub2cards;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.Random;

/**
 * simple utility class for generic useful functions
 */
public class Utils {

    private Utils() {
    }

    /**
     * @param input any string
     * @return true if the input is in fact a number
     */
    static boolean isNumeric(String input) {
        return !(input == null || input.isEmpty()) && input.chars().allMatch(Character::isDigit);
    }

    /**
     * @param input any string
     * @return true if there is at least a letter in the input
     */
    static boolean isAlphabetic(String input) {
        return !(input == null || input.isEmpty()) && input.chars().anyMatch(Character::isAlphabetic);
    }


    public static String trimBefore(String text)   {
        StringBuilder res = new StringBuilder();
        for(int i = 0 ; i < text.length() ; i++)    {
            if(Character.isAlphabetic(text.charAt(i)))  {
                res.append(text.substring(i));
                break;
            }
        }

        return res.toString();
    }

    /**
     * @param string a escaped unicode string
     * @return the unescaped form, more readable for humans.
     */
    public static String unescape(String string) {
        try {
            //only way I found to convert unicode escaped string, sorry
            Properties p = new Properties();
            p.load(new StringReader("key=" + string));
            return p.getProperty("key");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * taken as is from https://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java
     * @param base string to be hashed
     * @return the sha256 hash of base
     */
    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(Constants.DEFAULT_ENCODING));
            StringBuffer hexString = new StringBuffer();

            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * used to generate a random string of arbitrary length for non-security purpose.
     * @param length length of the random string
     * @return a random string of length 'length'
     */
    public static String randomString(int length) {
        final String charset = Constants.ASCII_CHARSET;
        return randomString(charset, length);
    }

    /**
     * used to generate a random string of arbitrary length for non-security purpose.
     * @param charset charset to use
     * @param length length of the random string
     * @return a random string of length 'length'
     */
    public static String randomString(String charset, int length) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) {
            int index = (int)(rnd.nextFloat() * charset.length());
            salt.append(charset.charAt(index));
        }
        return salt.toString();
    }
}
