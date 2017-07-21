package sub2cards;

import java.util.*;
import java.util.concurrent.*;
/**
 * Entry point for sub2cards project
 */
public class Main {
    private String exportFormat = "html";
    private String[] sourceSubtitles;
    private String[] targetSubtitles;
    private String[] mediaFiles;
    private String languages = "en-ru";
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

    /**
     * options usages examples
     * sub2cards -e html|anki|quizlet
     * sub2cards -h
     * sub2cards -m file1 file2 file3
     * sub2cards -s file1 file2 file3 //mandatory
     * sub2cards -t file1 file2 file3
     * sub2cards -l ru //mandatory
     * sub2cards
     * @param args collected arguments
     */
    public static void parseInput(Map<String, List<String>> args) {

        List<String> options = Arrays.asList("-e", "-h", "-m", "-s", "-t", "-l", "-w");
        if(args.keySet().size() < 1)
            throw new RuntimeException("Not enough arguments");

        for(String arg : args.keySet()) {
            if(!options.contains(arg))
                throw new RuntimeException("Unknown argument : " + arg);
        }

        for(String arg : args.keySet()) {
            if(!options.contains(arg))
                throw new RuntimeException("Unknown argument : " + arg);
            switch(arg)    {
                case "-h":
                    printHelp();
                    break;

            }
        }
    }

    private static void printHelp() {
        System.out.println("Usage: sub2cards -e [html|anki|quizlet] -s path -l [languages]");
        System.out.println("-h\t print this [h]elp");
        System.out.println("-e\t [html|anki|quizlet] [e]xport to selected format");
        System.out.println("-m\t path(s) to video [m]edia files, used to extract " +
                "sound and thumbnails for a subtitle line");
        System.out.println("-s\t path(s) to subtitle file(s) of '[s]ource language'");
        System.out.println("-t\t path(s) to subtitle file(s) of '[t]arget language'");
        System.out.println("-w\t export mode (only makes sense to use with -e) : words|lines|mixed. See Note 3");
        System.out.println("-l\t source [l]anguage-target [l]anguage, for example 'en-ru'\n");
        System.out.println("Note 1: if -t is not specified, the tool will use Yandex Translate to translate" +
                "the subtitle files to the target language\n" +
                "Note 2 : if -m is specified, thumbnails and audio extracts will be taken\n\t\tfrom the media files" +
                "to illustrate the related subtitle line / word.\n" +
                "Note 3 : for option -w, if 'words' is used, the subtitle files will be parsed,\n\t\t words collected " +
                "and sorted by occurrences, and the flashcards will consist of theses words.\n\t\t If 'lines'" +
                "is used, then the flashcards will hold the lines as they appear in the subtitles.\n\t\tIf 'mixed'" +
                "is used, then the flashcards will hold the lines, and below the words of the lines and their meaning");
    }

    /**
     *
     * @param args commandline arguments
     * @return the collected arguments : each option has a list of own arguments
     */
    public static Map<String, List<String>> collectArgs(String[] args) {

        final Map<String, List<String>> params = new HashMap<>();

        List<String> options = null;
        for (final String a : args) {
            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    throw new RuntimeException("Error at argument " + a);
                }

                options = new ArrayList<>();
                params.put(a, options);
            } else if (options != null) {
                options.add(a);
            } else {
                throw new RuntimeException("Illegal parameter usage");
            }
        }

        return params;
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
