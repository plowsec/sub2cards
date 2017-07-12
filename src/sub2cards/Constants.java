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

    //bad chars to remove from subtitle line
    public static final String BAD_CHARS = "[.,?!\\[()\\]{}+\\\\/-]";

    //whitespace modifier
    public static final String ON_WHITESPACES = "\\s+";

    //empty string
    public static final String WITH_NOTHING = "";

    //empty accent
    public static final String TONIC_ACCENT = "'";

    //time separator symbol in a subtitle file
    public static final String TIME_SEPARATOR = " --> ";

    //ffmpeg cmd
    public static final String FFMPEG_CMD_FMT = "ffmpeg -i %s -ss %s -to %s -f mp3 %s";

    //base html
    public static final String BASE_HTML = "<html>\n" +
            "  <head>\n" +
            "    <title>Flash Cards</title>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "     <!-- Latest compiled and minified CSS -->\n" +
            "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\">\n" +
            "    <!-- Optional theme -->\n" +
            "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css\" integrity=\"sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp\" crossorigin=\"anonymous\">\n" +
            "    <!-- Latest compiled and minified JavaScript -->\n" +
            "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\" integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\" crossorigin=\"anonymous\"></script>\n" +
            "    <script src=\"https://code.jquery.com/jquery-3.2.1.min.js\"></script>\n" +
            "    <style>\n" +
            "\n" +
            "\n" +
            "    .thumb {\n" +
            "        margin-bottom: 30px;\n" +
            "        min-height: 300px;\n" +
            "    }\n" +
            "\n" +
            "    footer {\n" +
            "        margin: 50px 0;\n" +
            "    }\n" +
            "    \n" +
            "    .subtitle .play-button {\n" +
            "        position: absolute;\n" +
            "        left: 20;\n" +
            "        top: 0;\n" +
            "    }\n" +
            "\n" +
            "    .subtitle .play-button {\n" +
            "        z-index: 1;\n" +
            "    }\n" +
            "    .subtitle .text {\n" +
            "        margin-left: 10px;\n" +
            "    }\n" +
            "\n" +
            "    .subtitle .text p {\n" +
            "        margin: 0 0 5px 0;\n" +
            "        max-width:70%;\n" +
            "    }\n" +
            "    </style>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "      \n" +
            "      <!-- Page Content -->\n" +
            "    <div class=\"container\">\n" +
            "\n" +
            "        <div class=\"row\">\n" +
            "\n" +
            "            <div class=\"col-lg-12\">\n" +
            "                <h1 class=\"page-header\">Flash Cards Gallery</h1>\n" +
            "            </div>";

    //html code for a thumbnail, to format with thumbnailPath, #audio, #audio, audioPath, text1, text2
    public static final String THUMBNAIL_HTML_FMT = "<div class=\"col-lg-4 col-md-4 col-xs-4 thumb\">\n" +
            "                <div class=\"subtitle\">\n" +
            "                    <img class=\"thumbnail\" src=\"%s\">\n" +
            "                    <img class=\"play-button\" src=\"play.svg\"\n" +
            "                      onclick=\"document.getElementById('audio-%d').play()\">\n" +
            "                   <audio id=\"audio-%d\" src=\"%s\"></audio>\n" +
            "                   <div class=\"text caption\">\n" +
            "                      <p class=\"foreign\" lang=\"en\">%s</p>\n" +
            "                      <p class=\"native\" lang=\"ru\">%s</p>\n" +
            "                   </div>\n" +
            "                </div>\n" +
            "            </div>";

    //html footer
    public static final String BASE_HTML_END = "\n" +
            "        <hr>\n" +
            "\n" +
            "        <!-- Footer -->\n" +
            "        <footer>\n" +
            "            <div class=\"row\">\n" +
            "                <div class=\"col-lg-12\">\n" +
            "                    <p>Copyright &copy; Your Website 2014</p>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </footer>\n" +
            "\n" +
            "    </div>\n" +
            "    <!-- /.container -->\n" +
            "\n" +
            "    <!-- jQuery -->\n" +
            "<script\n" +
            "\t\t\t  src=\"https://code.jquery.com/jquery-3.2.1.min.js\"\n" +
            "\t\t\t  integrity=\"sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4=\"\n" +
            "\t\t\t  crossorigin=\"anonymous\"></script>\n" +
            "  </body>\n" +
            "</html>";

}
