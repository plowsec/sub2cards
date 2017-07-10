package sub2cards;

/**
 * Utility class to centralize all the constants of the project
 */
public class Constants {

    private Constants() {
    }

    //url used to query words for wiktionary
    public static final String WIKI_URL = "https://en.wiktionary.org/w/api.php?action=query&titles=%s" +
            "&prop=links&pllimit=max&format=json";

    //regex used to parse the json of wiktionary and get the links
    public static final String WIKI_REGEX = "\\{\\\"ns\\\":(\\d+),\\\"title\\\":\\\"([^\"]*)\\\"\\}";

    //url of the starling service, formatted
    public static final String STARLING_URL_FMT = "http://starling.rinet.ru/cgi-bin/morph.cgi?" +
            "flags=endnnnnp&root=config&word=%s";

    //regex used to match the simplified form of a word on a starling result
    public static final String STARLING_REGEX = "Source form: ([^<]*)<p>";

    //encoding used by starling, sadly
    public static final String WIN_ENC = "windows-1251";

    //default encoding otherwise
    public static final String DEFAULT_ENCODING = "UTF8";

    //path to config file for api keys
    public static final String CONFIG_FILE = "config.properties";

    //api key of yandex translate. get here https://tech.yandex.com/key/form.xml?service=trnsl
    //Update : you must place this key inside the config.properties file, field is "yandex".
    public static String YANDEX_API_KEY = "";

    //default langs used to translate the text (source-dest)
    public static final String DEFAULT_LANG = "ru-en";

    //regex used to extract translation from yandex json response
    public static final String YANDEX_REGEX = "\\[\\\"([^\"]*)\\\"\\]";

    //Yandex Translate GET request
    public static final String REQ_FMT = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&text=%s&lang=%s";

    //number of processors
    public static final int NB_PROCS = Runtime.getRuntime().availableProcessors();

    //multiple used on the number of processsors
    public static final int FACTOR = 4;
}
