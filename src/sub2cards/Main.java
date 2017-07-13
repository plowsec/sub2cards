package sub2cards;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Entry point for sub2cards project
 */
public class Main {

    static void benchmarkSimplification(List<Word> words) {

        long startTime = System.nanoTime();
        Word.simplifySeq(words);
        long endTime = System.nanoTime();

        long seqTime = endTime - startTime;
        seqTime = TimeUnit.MILLISECONDS.convert(seqTime, TimeUnit.NANOSECONDS);


        System.out.printf("[*] Sequentially : %d [ms]\n", seqTime);
        int availableProcs = Runtime.getRuntime().availableProcessors();
        int factor = 2;
        startTime = System.nanoTime();
        Word.simplifyParallel(words, factor);
        endTime = System.nanoTime();

        long parallelTime = endTime - startTime;
        parallelTime = TimeUnit.MILLISECONDS.convert(parallelTime, TimeUnit.NANOSECONDS);
        System.out.printf("[*] Parallel, with %d threads : %d [ms]\n", availableProcs * factor, parallelTime);

        startTime = System.nanoTime();
        factor = 4;
        Word.simplifyParallel(words, factor);
        endTime = System.nanoTime();

        parallelTime = endTime - startTime;
        parallelTime = TimeUnit.MILLISECONDS.convert(parallelTime, TimeUnit.NANOSECONDS);

        System.out.printf("[*] Parallel, with %d threads : %d [ms]\n", availableProcs * factor, parallelTime);
    }

    public static void main(String[] args) {
        String filePath = "tests/got-simple.srt";
        if (args.length > 0)
            filePath = args[1];
        SubParse subParse = new SubParse(filePath);
        subParse.parse();
        List<Word> words = subParse.getSortedWords();
        List<Line> lines = subParse.getLines();

        /*List<Word> simplifiedWords = Word.simplifyParallel(words, Constants.FACTOR);
        System.out.println("[*] Simplification done");

        //benchmarkSimplification(words);

        List<Word> translatedWords = Word.translateCollectionParallel(simplifiedWords, Constants.DEFAULT_LANG);
        System.out.println("[*] Translation done");
        for(Word w : translatedWords) {
            System.out.println(w);
        }*/

        List<Line> withTranslation = Line.translateLinesParallel(lines, Constants.DEFAULT_LANG);
        /*FlashCard.exportHTML(withTranslation, "tests/video.avi", "tests");*/
        FlashCard.exportAnki(withTranslation, "got-simple", "tests/video.avi", "tests/new-deck");
    }
}
