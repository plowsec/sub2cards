package sub2cards;

import java.io.*;
import java.util.*;

/**
 * takes care of parsing a subtitle file and collecting words
 */
public class SubParse {

    private String subtitleFile; //path to the subtitle file
    private HashMap<String, Integer> words; //to remember words and their occurrences

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
            String line = br.readLine();
            while (line != null) {
                List<String> w = Arrays.asList(line
                        .trim()
                        .replaceAll("[\\[()\\]{}+\\\\/-]", "")
                        .replaceAll("[.,?!]", "")
                        .toLowerCase()
                        .split("\\s+"));
                //w.stream().sequential().forEach(i -> words.merge(i, 1, (key, oldCount) -> oldCount+1));
                for (String word : w) {
                    if (!Utils.isAlphabetic(word))
                        continue;
                    if (words.containsKey(word)) {
                        words.compute(word, (k, v) -> v + 1);
                    } else {
                        words.put(word, 1);
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the collected words, sorted by occurences
     */
    public List<String> getSortedWords() {
        List<String> res = new ArrayList<>(words.entrySet().size());
        words.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed())
                .forEach(i -> res.add(i.getKey()));
        return res;
    }

    /**
     * @return the internal dictionnary. todo : make a copy instead of leaking the internal collection
     */
    public Map<String, Integer> getDictionnary() {
        return words;
    }
}
