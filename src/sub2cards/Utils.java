package sub2cards;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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


    public static String trimBefore(String text) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (Character.isAlphabetic(text.charAt(i))) {
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
     *
     * @param base string to be hashed
     * @return the sha256 hash of base
     */
    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(Constants.DEFAULT_ENCODING));
            StringBuffer hexString = new StringBuffer();

            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * used to generate a random string of arbitrary length for non-security purpose.
     *
     * @param length length of the random string
     * @return a random string of length 'length'
     */
    public static String randomString(int length) {
        final String charset = Constants.ASCII_CHARSET;
        return randomString(charset, length);
    }

    /**
     * used to generate a random string of arbitrary length for non-security purpose.
     *
     * @param charset charset to use
     * @param length  length of the random string
     * @return a random string of length 'length'
     */
    public static String randomString(String charset, int length) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) {
            int index = (int) (rnd.nextFloat() * charset.length());
            salt.append(charset.charAt(index));
        }
        return salt.toString();
    }

    /**
     * used to create directories
     * @param path where to create the directory
     * @return true if a directory was succesfully created
     */
    public static boolean mkdir(String path)    {

        File theDir = new File(path);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            return result;
        }

        return false;
    }

    /**
     * used to generate the collection.anki2 databse
     *
     * @param sql sql allowing to generate the database
     */
    public static void createDataBase(String outputPath, String sql) {
        Connection connection;
        Statement statement;
        File file = new File(outputPath);
        if(file.exists())   {
            file.delete();
            System.out.println("[!] Deleted " + outputPath + " because it was on my way.");
        }
        try {
            // connect to database - will create it if it does not exist
            connection = DriverManager.getConnection("jdbc:sqlite:" + outputPath);
            statement = connection.createStatement();

            // execute the statement string
            statement.executeUpdate(sql);
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * used to zip all the files located inside a directory.
     * taken almost as is from :
     * https://stackoverflow.com/questions/15968883/how-to-zip-a-folder-itself-using-java
     *
     * @param sourceDirPath the directory in which we want the files to zip are located
     * @param zipFilePath output location of zip
     */
    public static void pack(String sourceDirPath, String zipFilePath){
        Path p = null;
        try {
            if(Paths.get(zipFilePath).toFile().exists())
                Paths.get(zipFilePath).toFile().delete();
            p = Files.createFile(Paths.get(zipFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            zs.write(Files.readAllBytes(path));
                            zs.closeEntry();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * after having generated an anki deck, lots of files are to be deleted
     * @param directoryPath directory where the anki deck was generated
     * @param assetsCounter max index value of assets, all files named
     *                      from 0 to assetsCounter will be deleted
     */
    public static void cleanTemporaryFiles(String directoryPath, int assetsCounter) {
        List<String> files = new ArrayList<>();
        IntStream.range(0, assetsCounter).boxed().forEach(i -> files.add(directoryPath+"/"+String.valueOf(i)));
        files.add(directoryPath+"/media");
        files.add(directoryPath+"/collection.anki2");
        files.forEach(i -> {
            File f = new File(i);
            if(f.exists())  {
                boolean res = f.delete();
                System.out.println("[*] Deleted temp file : " + i + ", success="+res);
            }
        });
    }
}
