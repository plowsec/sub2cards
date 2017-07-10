package sub2cards;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * handles a collected word from a subtitle file. This class should be able to retrieve the original
 * morphological form of the word and its translation
 */
public class Word {

    private final String originalForm; //the form of the word as we collected it
    private final String translation; //translation to the default target language
    private final String simplifiedForm; //base form in the native language

    public Word(String originalForm, String simplifiedForm) {
        this.originalForm = originalForm;
        this.simplifiedForm = simplifiedForm;
        this.translation = "";
    }

    public Word(String originalForm,  String simplifiedForm, String translation) {
        this.originalForm = originalForm;
        this.translation = translation;
        this.simplifiedForm = simplifiedForm;
    }

    public String getOriginalForm() {
        return originalForm;
    }

    public String getSimplifiedForm()   {
        return simplifiedForm;
    }

    /**
     * for example, using this function on the word стали, we expect to find сталь
     * which is the "base form" of the word steel in russian.
     *
     * @param word     the word to simplify
     * @param encoding if a special encoding is needed
     * @return the simplified form of the word or the word itself
     * @throws Exception in case the service used didn't know the word
     */
    public static Word simplify(String word, String encoding) throws Exception {

        String url;
        try {
            url = String.format(Constants.STARLING_URL_FMT, URLEncoder.encode(word, encoding));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Word(word, word);
        }
        String content = Word.getRemoteContent(url, encoding);
        List<String> links = extractResultsFromData(content);
        if (links.isEmpty()) {
            throw new Exception(word);
        }
        return new Word(word, links.get(0).replaceAll("'", ""));
    }

    /**
     * for example, using this function on the word стали, we expect to find сталь
     * which is the "base form" of the word steel in russian.
     * <p>
     * Default encoding is UTF-8
     *
     * @param word the word to simplify
     * @return the simplified form of the word or the word itself
     * @throws Exception in case the service used didn't know the word
     */
    public static Word simplify(String word) throws Exception {
        return simplify(word, Constants.DEFAULT_ENCODING);
    }


    /**
     * used to retrieve results matching the pattern from a web page
     *
     * @param string raw data to be parsed
     * @param regex  pattern to match
     * @return a list of matches
     */
    public static List<String> extractResultsFromData(String string, String regex) {
        List<String> links = new ArrayList<>();

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                links.add(matcher.group(i));
            }
        }

        return links;
    }

    /**
     * used to retrieve results matching the pattern from a web page
     *
     * @param string raw data to be parsed
     * @return a list of matches
     */
    public static List<String> extractResultsFromData(String string) {
        return extractResultsFromData(string, Constants.STARLING_REGEX);
    }

    /**
     * @param uri      location of the resource
     * @param encoding encoding to be used
     * @return the raw content retrieved at the given location or null in case of problems
     * @todo this should be asynchronous
     */
    public static String getRemoteContent(String uri, String encoding) {

        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input, encoding));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param uri location of the resource
     * @return the raw content retrieved at the given location or null in case of problems
     * @todo this should be asynchronous
     * <p>
     * Default encoding used is UTF8
     */
    public static String getRemoteContent(String uri) {
        return getRemoteContent(uri, Constants.DEFAULT_ENCODING);
    }

    /**
     *
     * @param text text to translate
     * @param lang src-dest languages
     * @return the translated word
     * @throws Exception in case there was a problem with the service
     */
    public static String getTranslation(String text, String lang) throws Exception   {

        final String yandexAPIKey = Config.getInstance().getYandexAPIKey();

        if(yandexAPIKey.isEmpty())
            throw new RuntimeException("[!] Problem fetching Yandex API key. Do you have one ?");
        final String request = String.format(Constants.REQ_FMT, yandexAPIKey, text, lang);
        final String content = getRemoteContent(request);
        List<String> translation = extractResultsFromData(content, Constants.YANDEX_REGEX);
        if(translation.isEmpty())
            throw new Exception(text);

        return translation.get(0).toLowerCase();
    }

    /**
     *
     * @param text text to translate
     * @return the translated word
     * @throws Exception in case there was a problem with the service
     */
    public static String getTranslation(String text) throws Exception   {

        return getTranslation(text, Constants.DEFAULT_LANG);
    }

    /**
     *
     * @param words words to be translated
     * @param lang src-dest languages
     * @return a new collection containing the original words and their translation
     */
    public static List<Word> translateCollectionSeq(List<String> words, String lang)  {
        List<Word> translatedWords = new ArrayList<>(words.size());
        List<String> errors = new ArrayList<>();
        for(String w : words)   {
            try {
                String translation = getTranslation(w, lang);
                translatedWords.add(new Word(w, translation, w));
            } catch (Exception e) {
                errors.add(w);
                e.printStackTrace();
            }
        }

        return translatedWords;
    }

    /**
     *
     * @param words words to be translated
     * @param lang src-dest languages
     * @return a new collection containing the original words and their translation
     */
    public static List<Word> translateCollectionParallel(List<Word> words, String lang)  {
        ConcurrentHashMap<String, Word> translatedWords = new ConcurrentHashMap<>(words.size());

        final ExecutorService pool = Executors.newFixedThreadPool(Constants.NB_PROCS * Constants.FACTOR);
        final ExecutorCompletionService<Word> completionService = new ExecutorCompletionService<>(pool);
        for (final Word w : words) {
            completionService.submit(() -> new Word(w.getOriginalForm(), w.getSimplifiedForm(),
                    getTranslation(w.getSimplifiedForm(), lang)));
        }

        for (int i = 0; i < words.size(); i++) {
            try {
                final Future<Word> future = completionService.take();
                final Word translation = future.get();
                translatedWords.putIfAbsent(translation.getSimplifiedForm(), translation);

            } catch (InterruptedException | ExecutionException e) {
            }
        }

        pool.shutdown();
        return new ArrayList<>(translatedWords.values());
    }

    /**
     *
     * @param words a given collection of words
     * @return a collection of unique base forms of the given words
     */
    public static List<Word> simplifySeq(List<String> words) {
        List<Word> simplifiedWords = new ArrayList<>(words.size());

        for (String word : words) {
            try {
                Word simplified = Word.simplify(word, Constants.WIN_ENC);
                if (!simplifiedWords.contains(simplified)) {
                    simplifiedWords.add(simplified);
                }
            } catch (Exception e) {
            }
        }

        return simplifiedWords;
    }

    /**
     *
     * @param words a given collection of words to simplify
     * @param factor multiple used on the number of available processors. Used to compute the number of threads.
     */
    public static List<Word> simplifyParallel(List<String> words, int factor) {
        ConcurrentHashMap<String, Word> simplifiedWords = new ConcurrentHashMap<>(words.size());
        final ExecutorService pool = Executors.newFixedThreadPool(Constants.NB_PROCS * factor);
        final ExecutorCompletionService<Word> completionService = new ExecutorCompletionService<>(pool);
        for (final String w : words) {
            completionService.submit(() -> Word.simplify(w, Constants.WIN_ENC));
        }

        for (int i = 0; i < words.size(); i++) {
            try {
                final Future<Word> future = completionService.take();
                final Word simplified = future.get();
                simplifiedWords.putIfAbsent(simplified.getSimplifiedForm(), simplified);

            } catch (InterruptedException | ExecutionException e) {
            }
        }

        pool.shutdown();
        return new ArrayList<>(simplifiedWords.values());
    }

    @Override
    public String toString()    {
        return this.originalForm + " -> " + this.simplifiedForm + " -> " + this.translation;
    }
}
