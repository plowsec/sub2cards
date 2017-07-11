package sub2cards;

import java.io.*;
import java.util.*;

/**
 * takes care of parsing a subtitle file and collecting words
 */
public class SubParse {

    private final String subtitleFile; //path to the subtitle file
    private HashMap<String, Word> words; //to remember words and their occurrences
    //private Set<Word> words; //to remember the collected words

    public SubParse(String path) {
        subtitleFile = path;
        words = new HashMap<>();
    }

    /**
     * reads the subtitle file and retains the words it encounters. Junk data is ignored.
     * The occurrences of the words are computed as well, in order to be able to sort
     * the collection by the most used words for example.
     */
    public void parse() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(subtitleFile), Constants.DEFAULT_ENCODING))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(!Utils.isAlphabetic(line))
                    continue;

                List<String> w = Arrays.asList(line
                        .trim()
                        .replaceAll(Constants.BAD_CHARS, Constants.WITH_NOTHING)
                        .toLowerCase()
                        .split(Constants.ON_WHITESPACES));
                //w.stream().sequential().forEach(i -> words.merge(i, 1, (key, oldCount) -> oldCount+1));
                for (String word : w) {
                    if (!Utils.isAlphabetic(word))
                        continue;
                    if (words.containsKey(word)) {

                        words.compute(word, (k, v) -> v.incrementOccurrence());
                    } else {
                        words.put(word, new Word.WordBuilder(word).withContext(line).build());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the collected words, sorted by occurences
     */
    public List<Word> getSortedWords() {
        List<Word> res = new ArrayList<>(words.entrySet().size());
        words.values()
                .stream()
                .sorted(Comparator.comparing(Word::getOccurrence).reversed())
                .forEach(res::add);
        return res;
    }

    /**
     * @return the internal dictionnary. todo : make a copy instead of leaking the internal collection
     */
    public Map<String, Word> getDictionnary() {
        return words;
    }
}
