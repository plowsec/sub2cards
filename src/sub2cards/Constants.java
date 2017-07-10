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
}
