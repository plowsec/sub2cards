package sub2cards;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sub2cards.Constants.*;

/**
 * handles a collected word from a subtitle file. This class should be able to retrieve the original
 * morphological form of the word and its translation
 */
@Immutable
public class Word {

    private final String originalForm; //the form of the word as we collected it
    private final String translation; //translation to the default target language
    private final String baseForm; //base form in the native language
    private final String context; //context, in which the word was used
    private final int occurrence; //nb of occurrence of the word

    //copy constructor
    public Word(Word word)  {
        this.originalForm = word.getOriginalForm();
        this.baseForm = word.getBaseForm();
        this.translation = word.getTranslation();
        this.occurrence = word.getOccurrence();
        this.context = word.getContext();
    }

    public Word(WordBuilder wordBuilder)    {
        this.originalForm = wordBuilder.originalForm;
        this.baseForm = wordBuilder.simplifiedForm;
        this.translation = wordBuilder.translation;
        this.occurrence = wordBuilder.occurrence;
        this.context = wordBuilder.context;
    }

    public String getOriginalForm() {
        return originalForm;
    }

    public String getBaseForm()   {
        return baseForm;
    }

    public String getTranslation()  {
        return translation;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public String getContext()  {
        return context;
    }

    public Word setBaseForm(String newBaseForm)    {
        return new WordBuilder(originalForm)
                .withBaseForm(newBaseForm)
                .withTranslation(translation)
                .withOccurrence(occurrence+1)
                .withContext(context)
                .build();
    }

    public Word setTranslation(String newTranslation)    {
        return new WordBuilder(originalForm)
                .withBaseForm(baseForm)
                .withTranslation(newTranslation)
                .withOccurrence(occurrence+1)
                .withContext(context)
                .build();
    }

    public Word incrementOccurrence()  {
        return new WordBuilder(originalForm)
                .withBaseForm(baseForm)
                .withTranslation(translation)
                .withOccurrence(occurrence+1)
                .withContext(context)
                .build();
    }

    public static class WordBuilder {
        private final String originalForm; //the form of the word as we collected it
        private String translation; //translation to the default target language
        private String simplifiedForm; //base form in the native language
        private String context; //context, in which the word was used
        private int occurrence; //nb of occurrence of the word

        public WordBuilder(String originalForm) {
            this.originalForm = originalForm;
            this.occurrence = 1;
        }

        public WordBuilder withBaseForm(String baseForm)    {
            this.simplifiedForm = baseForm;
            return this;
        }

        public WordBuilder withTranslation(String translation)  {
            this.translation = translation;
            return this;
        }

        public WordBuilder withOccurrence(int occurrence)   {
            this.occurrence = occurrence;
            return this;
        }

        public WordBuilder withContext(String context)  {
            this.context = context;
            return this;
        }

        public Word build() {
            return new Word(this);
        }
    }

    /**
     * for example, using this function on the word стали, we expect to find сталь
     * which is the "base form" of the word steel in russian.
     *
     * @param originalWord     the word to simplify
     * @param encoding if a special encoding is needed
     * @return the simplified form of the word or the word itself
     * @throws Exception in case the service used didn't know the word
     */
    public static String simplify(String originalWord, String encoding) throws Exception {

        String url;
        try {
            url = String.format(STARLING_URL_FMT, URLEncoder.encode(originalWord, encoding));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return originalWord;
        }
        String content = Word.getRemoteContent(url, encoding);
        List<String> links = extractResultsFromData(content);
        if (links.isEmpty()) {
            throw new Exception(originalWord);
        }
        return links.get(0).replaceAll(TONIC_ACCENT, WITH_NOTHING);
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
    public static String simplify(String word) throws Exception {
        return simplify(word, DEFAULT_ENCODING);
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
        return extractResultsFromData(string, STARLING_REGEX);
    }

    /**
     * @param uri      location of the resource
     * @param encoding encoding to be used
     * @return the raw content retrieved at the given location or null in case of problems
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
     * <p>
     * Default encoding used is UTF8
     */
    public static String getRemoteContent(String uri) {
        return getRemoteContent(uri, DEFAULT_ENCODING);
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
        final String request = String.format(REQ_FMT, yandexAPIKey, text, lang);
        final String content = getRemoteContent(request);
        List<String> translation = extractResultsFromData(content, YANDEX_REGEX);
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

        return getTranslation(text, DEFAULT_LANG);
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
                Word word = new WordBuilder(w)
                        .withBaseForm(w)
                        .withTranslation(translation)
                        .build();
                translatedWords.add(word);
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

        final ExecutorService pool = Executors.newFixedThreadPool(NB_PROCS * FACTOR);
        final ExecutorCompletionService<Word> completionService = new ExecutorCompletionService<>(pool);
        for (final Word w : words) {
            completionService.submit(() -> w.setTranslation(getTranslation(w.getBaseForm(), lang)));
        }

        for (int i = 0; i < words.size(); i++) {
            try {
                final Future<Word> future = completionService.take();
                final Word translation = future.get();
                translatedWords.putIfAbsent(translation.getBaseForm(), translation);

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
    public static List<Word> simplifySeq(List<Word> words) {
        List<Word> simplifiedWords = new ArrayList<>(words.size());

        for (Word word : words) {
            try {
                String simplified = Word.simplify(word.getOriginalForm(), WIN_ENC);
                Word baseForm =word.setBaseForm(simplified);
                if (!simplifiedWords.contains(baseForm)) {
                    simplifiedWords.add(baseForm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return simplifiedWords;
    }

    /**
     *
     * @param words a given collection of words to simplify
     * @param factor multiple used on the number of available processors. Used to compute the number of threads.
     * @return a new list of Word instances, with translation field updated
     */
    public static List<Word> simplifyParallel(List<Word> words, int factor) {
        ConcurrentHashMap<String, Word> simplifiedWords = new ConcurrentHashMap<>(words.size());
        final ExecutorService pool = Executors.newFixedThreadPool(NB_PROCS * factor);
        final ExecutorCompletionService<Word> completionService = new ExecutorCompletionService<>(pool);
        for (final Word w : words) {
            completionService.submit(() -> w.setBaseForm(Word.simplify(w.getOriginalForm(), WIN_ENC)));
        }

        for (int i = 0; i < words.size(); i++) {
            try {
                final Future<Word> future = completionService.take();
                final Word simplified = future.get();
                simplifiedWords.putIfAbsent(simplified.getBaseForm(), simplified);

            } catch (InterruptedException | ExecutionException e) {
            }
        }

        pool.shutdown();
        return new ArrayList<>(simplifiedWords.values());
    }

    @Override
    public String toString()    {
        return this.originalForm + " -> " + this.baseForm + " -> " + this.translation + " : " + this.context;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(!(o instanceof Word))
            return false;
        Word word = (Word) o;
        return occurrence == word.occurrence &&
                Objects.equals(originalForm, word.originalForm) &&
                Objects.equals(baseForm, word.baseForm) &&
                Objects.equals(translation, word.translation) &&
                Objects.equals(context, word.context);
    }

    @Override
    public int hashCode()   {
        return Objects.hash(originalForm, baseForm, translation, occurrence, context);
    }
}
