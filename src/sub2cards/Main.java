package sub2cards;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for sub2cards project
 */
public class Main {

    public static void main(String[] args) {
        String filePath = "tests/got-simple.srt";
        if (args.length > 0)
            filePath = args[1];
        SubParse subParse = new SubParse(filePath);
        subParse.parse();
        List<String> words = subParse.getSortedWords();
        List<String> simplifiedWords = new ArrayList<>(words.size());
        List<String> errors = new ArrayList<>();

        for (int i = 0 ; i < words.size() ; i++) {
            if(i == words.size() / 4)
                System.out.println("[*] Lemmatizer : 25% done");
            else if(i == words.size() / 2)
                System.out.println("[*] Lemmatizer : 50% done");
            else if(i == words.size() * 3 / 4)
                System.out.println("[*] Lemmatizer : 75% done");

            try {
                String simplified = Word.simplify(words.get(i), Constants.WIN_ENC);
                if (!simplifiedWords.contains(simplified)) {
                    simplifiedWords.add(simplified);
                    //System.out.println(words.get(i) + " is actually : " + simplified);
                }
            } catch (Exception e) {
                errors.add(e.getMessage());
                //System.out.println("[!] Error while fetching : " + e.getMessage());
            }
        }

        System.out.println("[*] Lemmatizer : 100% done");
        System.out.println("[!] " + errors.size() + " errors encountered");

        List<Word> translatedWords = Word.translateCollection(simplifiedWords, Constants.DEFAULT_LANG);
        System.out.println("[*] Translation done");
        for(Word w : translatedWords) {
            System.out.println(w);
        }
    }
}
