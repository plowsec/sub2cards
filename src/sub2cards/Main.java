package sub2cards;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Entry point for sub2cards project
 */
public class Main {

    public static void simplifySeq(List<String> words) {
        List<String> simplifiedWords = new ArrayList<>(words.size());
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {
            /*if (i == words.size() / 4)
                System.out.println("[*] Lemmatizer : 25% done");
            else if (i == words.size() / 2)
                System.out.println("[*] Lemmatizer : 50% done");
            else if (i == words.size() * 3 / 4)
                System.out.println("[*] Lemmatizer : 75% done");*/

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

        /*System.out.println("[*] Lemmatizer : 100% done");
        System.out.println("[!] " + errors.size() + " errors encountered");*/


    }

    public static void simplifyParallel(List<String> words, int factor) {
        ConcurrentHashMap<String, Integer> simplifiedWords = new ConcurrentHashMap<>(words.size());
        ConcurrentHashMap<String, Integer> errors = new ConcurrentHashMap<>();
        int availableProcs = Runtime.getRuntime().availableProcessors();
        final ExecutorService pool = Executors.newFixedThreadPool(availableProcs * factor);
        final ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(pool);
        for (final String w : words) {
            completionService.submit(() -> Word.simplify(w, Constants.WIN_ENC));
        }

        for (int i = 0; i < words.size(); i++) {
            /*if (i == words.size() / 4)
                System.out.println("[*] Lemmatizer : 25% done");
            else if (i == words.size() / 2)
                System.out.println("[*] Lemmatizer : 50% done");
            else if (i == words.size() * 3 / 4)
                System.out.println("[*] Lemmatizer : 75% done");*/
            try {
                final Future<String> future = completionService.take();
                final String simplified = future.get();
                simplifiedWords.putIfAbsent(simplified, 0);

            } catch (InterruptedException | ExecutionException e) {
                errors.putIfAbsent(e.getMessage(), 0);
                //e.printStackTrace();
            }
        }

        /*System.out.println("[*] Lemmatizer : 100% done");
        //simplifiedWords.forEach(System.out::println);
        System.out.println("[!] " + errors.size() + " errors encountered");*/
        //errors.forEach(System.out::println);
        pool.shutdown();
    }

    public static void benchmarkSimplification(List<String> words) {

        long startTime = System.nanoTime();
        simplifySeq(words);
        long endTime = System.nanoTime();

        long seqTime = endTime - startTime;
        seqTime = TimeUnit.MILLISECONDS.convert(seqTime, TimeUnit.NANOSECONDS);


        System.out.printf("[*] Sequentially : %d [ms]\n", seqTime);
        int availableProcs = Runtime.getRuntime().availableProcessors();
        int factor = 2;
        startTime = System.nanoTime();
        simplifyParallel(words, factor);
        endTime = System.nanoTime();

        long parallelTime = endTime - startTime;
        parallelTime = TimeUnit.MILLISECONDS.convert(parallelTime, TimeUnit.NANOSECONDS);
        System.out.printf("[*] Parallel, with %d threads : %d [ms]\n", availableProcs * factor, parallelTime);

        startTime = System.nanoTime();
        factor = 4;
        simplifyParallel(words, factor);
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
        List<String> words = subParse.getSortedWords();
        List<String> simplifiedWords = new ArrayList<>(words.size());
        List<String> errors = new ArrayList<>();

        benchmarkSimplification(words);

        /*List<Word> translatedWords = Word.translateCollection(simplifiedWords, Constants.DEFAULT_LANG);
        System.out.println("[*] Translation done");
        for(Word w : translatedWords) {
            System.out.println(w);
        }*/
    }
}
