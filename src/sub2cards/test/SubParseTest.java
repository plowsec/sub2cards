package sub2cards.test;

import org.junit.Test;

import static org.junit.Assert.*;

import sub2cards.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


/**
 * Unit tests for the project.
 */
public class SubParseTest {

    @Test
    public void parseFileTest() {
        SubParse subParseTest = new SubParse("tests/test.srt");
        subParseTest.parse();
        List<Word> words = subParseTest.getSortedWords();
        Map<String, Word> dict = subParseTest.getDictionnary();
        assertTrue("right count of words", words.size() == 7);
        assertTrue("most occurring word", words.get(0).getOriginalForm().equals("est"));
        assertTrue("occurences count for most occurring word",
                dict.get(words.get(0).getOriginalForm()).getOccurrence() == 4);
    }

    @Test
    public void parseSubtitleTest() {
        SubParse subParseTest = new SubParse("tests/got-simple.srt");
        subParseTest.parse();
        List<Word> words = subParseTest.getSortedWords();
        Map<String, Word> dict = subParseTest.getDictionnary();
        assertTrue("most occurring word", words.get(0).getOriginalForm().equals("в"));
        assertTrue("occurences count for most occurring word",
                dict.get(words.get(0).getOriginalForm()).getOccurrence() == 2);
    }

    @Test
    public void jsonParseTest() {
        String json = "{\"batchcomplete\":\"\",\"query\":{\"pages\":{\"5137669\":{\"pageid\":5137669,\"ns\":0," +
                "\"title\":\"\\u0441\\u0442\\u0430\\u043b\\u0438\"," +
                "\"links\":[{\"ns\":0,\"title\":\"\\u0441\\u0442\\u0430\\u043b\\u044c\"}," +
                "{\"ns\":0,\"title\":\"\\u0441\\u0442\\u0430\\u0442\\u044c\"}," +
                "{\"ns\":4,\"title\":\"Wiktionary:International Phonetic Alphabet\"}," +
                "{\"ns\":4,\"title\":\"Wiktionary:Russian transliteration\"}," +
                "{\"ns\":100,\"title\":\"Appendix:Glossary\"}]}}}," +
                "\"limits\":{\"links\":500}}\n";
        List<String> links = Word.extractResultsFromData(json, Constants.WIKI_REGEX);
        assertTrue(Utils.unescape(links.get(1)).equals("сталь"));
        assertTrue(Utils.unescape(links.get(3)).equals("стать"));
    }

    @Test
    public void simplifyTest() {
        String declensed = "жил";

        try {
            String url = String.format(Constants.STARLING_URL_FMT, URLEncoder.encode(declensed, Constants.WIN_ENC));
            String content = Word.getRemoteContent(url, Constants.WIN_ENC);
            List<String> links = Word.extractResultsFromData(content);
            assertTrue(links.get(0).equals("жить"));
            assertTrue(links.get(1).equals("жила"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void simplifyTest2() {
        try {
            assertTrue("infinitive", Word.simplify("жил", Constants.WIN_ENC).equals("жить"));

            assertTrue("стали should give сталь or стать",
                    Word.simplify("стали", Constants.WIN_ENC).equals("сталь") ||
                            Word.simplify("стали", Constants.WIN_ENC).equals("стать")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void simplifyTest3() {
        String declensed = "жил";
        try {

            String content = Word.getRemoteContent("http://starling.rinet.ru/cgi-bin/morph.cgi?" +
                    "flags=endnnnnp&root=config&word=" + URLEncoder.encode(declensed, "windows-1251"));

            byte[] e = content.getBytes("windows-1251");
            String m = new String(new String(e, "windows-1251").getBytes(), "UTF8");
            System.out.println("[SubParseTest:simplifyTest3] " + m);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void translateTest() {
        try {
            assertTrue("сталь -> steel", Word.getTranslation("сталь").equals("steel"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEqualsAndHash() {
        Word word1 = new Word.WordBuilder("сталь")
                .withBaseForm("сталь")
                .withContext("help")
                .withOccurrence(4)
                .withTranslation("test")
                .build();
        Word word2 = new Word.WordBuilder("сталь")
                .withBaseForm("сталь")
                .withContext("help")
                .withOccurrence(4)
                .withTranslation("test")
                .build();
        assertTrue("same words", word1.equals(word2));
        assertTrue("same words", word2.equals(word1));

        word2 = new Word.WordBuilder("сталь")
                .withBaseForm("сталь")
                .withContext("help")
                .withTranslation("test")
                .build();

        assertFalse("same words, not same occurrence", word1.equals(word2));
        assertFalse("same words, not same occurrence", word2.equals(word1));
    }

    @Test
    public void testArgumentsCollecting() {


        try {
            String[] args = {"hello"};
            Main.collectArgs(args);
            assertTrue(false);

        }
        catch (Exception e) {
            assertTrue(e.getMessage().equals("Illegal parameter usage"));
        }

        try {
            String[] args = {"-"};
            Main.collectArgs(args);
            assertTrue(false);

        }
        catch (Exception e) {
            assertTrue(e.getMessage().equals("Error at argument -"));
        }

        try {
            //should not crash
            String[] args = {"-s", "hello.txt"};
            Main.collectArgs(args);
        }
        catch (Exception e) {
            assertTrue(false);
        }

        try {
            String[] args = {"-s", "-t"};
            Main.collectArgs(args);
        }
        catch (Exception e) {
            assertTrue(false);
        }

        try {
            String[] args = {"-s", "file1", "file2"};
            Main.collectArgs(args);
        }
        catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void testCheckInput() {
        try {
            String[] args = {};
            Map<String, List<String>> collectArgs = Main.collectArgs(args);
            Main main = new Main();
            Main.parseInput(collectArgs, main);

        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Not enough arguments"));
        }

        try {
            String[] args = {"-h"};
            Main.collectArgs(args);

        }
        catch (Exception e) {
            assertTrue(false);
        }

        try {
            String[] args = {"-z"};
            Main.collectArgs(args);
            Map<String, List<String>> collectArgs = Main.collectArgs(args);
            Main main = new Main();
            Main.parseInput(collectArgs, main);
            assertTrue(false);
        }
        catch (Exception e) {
            assertTrue(e.getMessage().matches(".*Unknown argument.*"));
        }

        try {
            String[] args = {"-h"};
            Main.collectArgs(args);
            Map<String, List<String>> collectArgs = Main.collectArgs(args);
            Main main = new Main();
            Main.parseInput(collectArgs, main);
        }
        catch (Exception e) {
            assertTrue(false);
        }

        try {
            String[] args = {"-e", "test"};
            Main.collectArgs(args);
            Map<String, List<String>> collectArgs = Main.collectArgs(args);
            Main main = new Main();
            Main.parseInput(collectArgs, main);
            assertTrue(false);
        }
        catch (Exception e) {
            assertTrue(e.getMessage().matches(".*Invalid parameter used with option.*"));
        }

        try {
            String[] args = {"-l", "en-ru", "-s", "tests/got.srt", "-e", "html"};
            Main.collectArgs(args);
            Map<String, List<String>> collectArgs = Main.collectArgs(args);
            Main main = new Main();
            Main.parseInput(collectArgs, main);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }

        try {
            String[] args = {"-e", "html", "-m", "tests/got.srt", "tests/yihaa"};
            Main.collectArgs(args);
            Map<String, List<String>> collectArgs = Main.collectArgs(args);
            Main main = new Main();
            Main.parseInput(collectArgs, main);
            assertTrue(false);
        }
        catch (Exception e) {
            assertTrue(e.getMessage().matches(".*File doesn't exist.*"));
        }
    }
}
