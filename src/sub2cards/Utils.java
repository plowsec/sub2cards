package sub2cards;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

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
}
