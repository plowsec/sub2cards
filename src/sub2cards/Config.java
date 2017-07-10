package sub2cards;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * handles config file (api keys and stuff)
 */
public class Config {

    private String yandexAPIKey = "";

    private Config()    {
        populateAPIKeys();
    }

    private static final Config INSTANCE = new Config();

    public static Config getInstance() {
        return INSTANCE;
    }

    private void populateAPIKeys() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(Constants.CONFIG_FILE);

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            String yandex = prop.getProperty("yandex");
            if (!yandex.isEmpty())
                yandexAPIKey = yandex;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getYandexAPIKey() {
        return yandexAPIKey;
    }
}
