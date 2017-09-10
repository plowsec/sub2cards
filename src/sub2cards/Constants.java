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
    public static final String FFMPEG_SOUND_EXTRACT_FMT = "ffmpeg -i %s -ss %s -to %s -f mp3 %s";

    //fmpeg cmd to generate a thumbnail
    public static final String FFMPEG_THUMBNAIL_EXTRACT_FMT = "ffmpeg -ss %s -i %s  -vframes:v 1 -s 480x300 -f image2 %s";

    //assets folder path
    public static final String ASSETS_FOLDER = "assets/";

    //anki guid is 10 chars long
    public static final int ANKI_GUID_LENGTH = 10;

    //an anki note / card id is Epoch + 3 random digits
    public static final int ANKI_FLOAT_ID_LENGTH = 3;

    public static final int HEX_BASE = 16;

    //used to generate random integers of 3 digits
    public static final String INT_CHARSET = "123456789";

    //used when creating anki flashcards
    public static final int ANKI_HASH_LENGTH = 8;

    //basic ascii charset (probably not exhaustive)
    public static final String ASCII_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvxyz!$.:;?%&";

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
            "    .thumbnail {\n" +
            "        max-width: 240px;\n" +
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
            "                    <p>Generated by sub2cards</p>\n" +
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

    public static final String ANKI_HEAD = "BEGIN TRANSACTION;\n" +
            "CREATE TABLE revlog (\n" +
            "    id              integer primary key,\n" +
            "    cid             integer not null,\n" +
            "    usn             integer not null,\n" +
            "    ease            integer not null,\n" +
            "    ivl             integer not null,\n" +
            "    lastIvl         integer not null,\n" +
            "    factor          integer not null,\n" +
            "    time            integer not null,\n" +
            "    type            integer not null\n" +
            ");\n" +
            "CREATE TABLE notes (\n" +
            "    id              integer primary key,   /* 0 */\n" +
            "    guid            text not null,         /* 1 */\n" +
            "    mid             integer not null,      /* 2 */\n" +
            "    mod             integer not null,      /* 3 */\n" +
            "    usn             integer not null,      /* 4 */\n" +
            "    tags            text not null,         /* 5 */\n" +
            "    flds            text not null,         /* 6 */\n" +
            "    sfld            integer not null,      /* 7 */\n" +
            "    csum            integer not null,      /* 8 */\n" +
            "    flags           integer not null,      /* 9 */\n" +
            "    data            text not null          /* 10 */\n" +
            ");\n";

    public static final String ANKI_MID = "CREATE TABLE graves (\n" +
            "    usn             integer not null,\n" +
            "    oid             integer not null,\n" +
            "    type            integer not null\n" +
            ");\n" +
            "CREATE TABLE col (\n" +
            "    id              integer primary key,\n" +
            "    crt             integer not null,\n" +
            "    mod             integer not null,\n" +
            "    scm             integer not null,\n" +
            "    ver             integer not null,\n" +
            "    dty             integer not null,\n" +
            "    usn             integer not null,\n" +
            "    ls              integer not null,\n" +
            "    conf            text not null,\n" +
            "    models          text not null,\n" +
            "    decks           text not null,\n" +
            "    dconf           text not null,\n" +
            "    tags            text not null\n" +
            ");\n" +
            "INSERT INTO `col` (id,crt,mod,scm,ver,dty,usn,ls,conf,models,decks,dconf,tags) VALUES (1,1499911200,1499951145302,1499951145265,11,0,0,0,'{\"nextPos\": 1, \"estTimes\": true, \"activeDecks\": [1], \"sortType\": \"noteFld\", \"timeLim\": 0, \"sortBackwards\": false, \"addToCur\": true, \"curDeck\": 1, \"newBury\": true, \"newSpread\": 0, \"dueCounts\": true, \"curModel\": \"1499951145266\", \"collapseTime\": 1200}','{\"1499937466127\": {\"vers\": [], \"name\": \"Basique\", \"tags\": [], \"did\": 1499951104878, \"usn\": -1, \"req\": [[0, \"any\", [2, 3]]], \"flds\": [{\"name\": \"English\", \"media\": [], \"sticky\": false, \"rtl\": false, \"ord\": 0, \"font\": \"Nimbus Sans\", \"size\": 20}, {\"name\": \"Russian\", \"media\": [], \"sticky\": false, \"rtl\": false, \"ord\": 1, \"font\": \"Nimbus Sans\", \"size\": 20}, {\"name\": \"Picture\", \"media\": [], \"sticky\": false, \"rtl\": false, \"ord\": 2, \"font\": \"Nimbus Sans\", \"size\": 20}, {\"name\": \"Sound\", \"media\": [], \"sticky\": false, \"rtl\": false, \"ord\": 3, \"font\": \"Nimbus Sans\", \"size\": 20}], \"sortf\": 0, \"tmpls\": [{\"name\": \"Carte 1\", \"qfmt\": \"{{Picture}}\\n</br>\\n{{Sound}}\", \"did\": null, \"bafmt\": \"\", \"afmt\": \"{{FrontSide}}\\n\\n<hr id=answer>\\n{{Russian}}\\n</br>\\n{{English}}\\n\", \"ord\": 0, \"bqfmt\": \"\"}], \"mod\": 1499951115, \"latexPost\": \"\\\\end{document}\", \"type\": 0, \"id\": \"1499937466127\", \"css\": \".card {\\n font-family: arial;\\n font-size: 20px;\\n text-align: center;\\n color: black;\\n background-color: white;\\n}\\n\", \"latexPre\": \"\\\\documentclass[12pt]{article}\\n\\\\special{papersize=3in,5in}\\n\\\\usepackage[utf8]{inputenc}\\n\\\\usepackage{amssymb,amsmath}\\n\\\\pagestyle{empty}\\n\\\\setlength{\\\\parindent}{0in}\\n\\\\begin{document}\\n\"}}','{\"1\": {\"desc\": \"\", \"name\": \"Default\", \"extendRev\": 50, \"usn\": 0, \"collapsed\": false, \"newToday\": [0, 0], \"timeToday\": [0, 0], \"dyn\": 0, \"extendNew\": 10, \"conf\": 1, \"revToday\": [0, 0], \"lrnToday\": [0, 0], \"id\": 1, \"mod\": 1499951145}, \"1499951104878\": {\"desc\": \"\", \"name\": \"test\", \"extendRev\": 50, \"usn\": -1, \"collapsed\": false, \"newToday\": [0, 0], \"timeToday\": [0, 0], \"dyn\": 0, \"extendNew\": 10, \"conf\": 1, \"revToday\": [0, 0], \"lrnToday\": [0, 0], \"id\": 1499951104878, \"mod\": 1499951104}}','{\"1\": {\"name\": \"Default\", \"replayq\": true, \"lapse\": {\"leechFails\": 8, \"minInt\": 1, \"delays\": [10], \"leechAction\": 0, \"mult\": 0}, \"rev\": {\"perDay\": 100, \"fuzz\": 0.05, \"ivlFct\": 1, \"maxIvl\": 36500, \"ease4\": 1.3, \"bury\": true, \"minSpace\": 1}, \"timer\": 0, \"maxTaken\": 60, \"usn\": 0, \"new\": {\"perDay\": 20, \"delays\": [1, 10], \"separate\": true, \"ints\": [1, 4, 7], \"initialFactor\": 2500, \"bury\": true, \"order\": 1}, \"mod\": 0, \"id\": 1, \"autoplay\": true}}','{}');\n" +
            "CREATE TABLE cards (\n" +
            "    id              integer primary key,   /* 0 */\n" +
            "    nid             integer not null,      /* 1 */\n" +
            "    did             integer not null,      /* 2 */\n" +
            "    ord             integer not null,      /* 3 */\n" +
            "    mod             integer not null,      /* 4 */\n" +
            "    usn             integer not null,      /* 5 */\n" +
            "    type            integer not null,      /* 6 */\n" +
            "    queue           integer not null,      /* 7 */\n" +
            "    due             integer not null,      /* 8 */\n" +
            "    ivl             integer not null,      /* 9 */\n" +
            "    factor          integer not null,      /* 10 */\n" +
            "    reps            integer not null,      /* 11 */\n" +
            "    lapses          integer not null,      /* 12 */\n" +
            "    left            integer not null,      /* 13 */\n" +
            "    odue            integer not null,      /* 14 */\n" +
            "    odid            integer not null,      /* 15 */\n" +
            "    flags           integer not null,      /* 16 */\n" +
            "    data            text not null          /* 17 */\n" +
            ");\n";

    public static final String ANKI_FOOT = "CREATE INDEX ix_revlog_usn on revlog (usn);\n"+
            "CREATE INDEX ix_revlog_cid on revlog (cid);\n"+
            "CREATE INDEX ix_notes_usn on notes (usn);\n"+
            "CREATE INDEX ix_notes_csum on notes (csum);\n"+
            "CREATE INDEX ix_cards_usn on cards (usn);\n"+
            "CREATE INDEX ix_cards_sched on cards (did, queue, due);\n"+
            "CREATE INDEX ix_cards_nid on cards (nid);\n"+
            "COMMIT;";

    /*
     * Fields:
     * id: 1398130088495 - The note id, generate it randomly.
     * guid: 'Ot0!xywPWG' - a GUID identifier, generate it randomly.
     * mid: 1342697561419 - Identifier of the model, use the one found in the models JSON section.
     * mod: 1398130110 - Replace with current time (seconds since 1970).
     * usn: -1 - We can leave it untouched.
     * tags: - Tags, visible to the user, which can be used to filter cards (e.g. "verb"). We can leave it untouched (empty string).
     * flds: 'Bonjour�Hello' - Card content, front and back, separated by \x1f char.
     * sfld: 'Bonjour' - Card front content without html (first part of flds, filtered).
     * csum: 4077833205 - A string SHA1 checksum of sfld, limited to 8 digits. PHP: (int)(hexdec(getFirstNchars(sha1($sfld), 8)));
     * flags: 0 - We can leave it untouched.
     * data: - We can leave it untouched.
     * example :
     * INSERT INTO `notes` VALUES (1499951115381,'Aj0+!:.D5M',1499937466127,1499951115,-1,'','JustТолько<img src="00_03_51_160.jpg" />[sound:00_03_51_160.mp3]','Just',1131897823,0,'');
     * INSERT INTO `notes` VALUES (1499951115382,'N{J*g@p@Al',1499937466127,1499951115,-1,'','helloпривет<img src="00_04_08_930.jpg" />[sound:00_04_08_930.mp3]','hello',773817065,0,'');
     * how to format : id, guid, mod, flds, sfld, csum
     */
    public static final String ANKI_NOTE_FMT = "INSERT INTO `notes` (id,guid,mid,mod,usn,tags,flds,sfld,csum,flags,data) VALUES(%s,'%s',1499937466127,%d,-1,'','%s','%s',%s,0,'');\n";

    /* Fields:
     * id: 1398130110964 - The card id, generate it randomly.
     * nid: 1398130088495 - The note id this card is associated with.
     * did: 1398130078204 - The deck id this card is associated with.
     * the rest doesn't really matter for us
     * INSERT INTO `cards` VALUES (1499951115384,1499951115381,1499951104878,0,1499951115,-1,0,0,1,0,0,0,0,0,0,0,0,'');
     * INSERT INTO `cards` VALUES (1499951115385,1499951115382,1499951104878,0,1499951115,-1,0,0,2,0,0,0,0,0,0,0,0,'');
     * how to format: id, nid, mod_of_note
     */
    public static final String ANKI_CARD_FMT ="INSERT INTO `cards` (id,nid,did,ord,mod,usn,type,queue,due,ivl,factor,reps,lapses,left,odue,odid,flags,data) VALUES (%s,%s,1499951104878,0,%d,-1,0,0,2,0,0,0,0,0,0,0,0,'');\n";

    //
    public static final String ANKI_FLDS_FMT = "%s\u001F%s\u001F<img src=\"%s.jpg\" />\u001F[sound:%s.mp3]";
}
