package sub2cards;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for sub2cards project
 */
public class Main {

    public static void main(String[] args) {
        String filePath = "tests/got.srt";
        if (args.length > 0)
            filePath = args[1];
        SubParse subParse = new SubParse(filePath);
        subParse.parse();
        List<String> words = subParse.getSortedWords();
        List<String> simplifiedWords = new ArrayList<>(words.size());
        List<String> errors = new ArrayList<>();

        for (String w : subParse.getSortedWords()) {
            try {
                String simplified = Word.simplify(w, Constants.WIN_ENC);
                if (!simplifiedWords.contains(simplified)) {
                    simplifiedWords.add(simplified);
                    System.out.println(w + " is actually : " + simplified);
                }
            } catch (Exception e) {
                errors.add(e.getMessage());
                System.out.println("[!] Error while fetching : " + e.getMessage());
            }
        }
    }
}
