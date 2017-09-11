package sub2cards;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

/**
 * Entry point for sub2cards project
 */
public class Main {
    private static final ArrayList<String> supportedExportModes = new ArrayList<>(
            Arrays.asList("words", "lines", "mixed"));
    private static final ArrayList<String> supportedExportFormats = new ArrayList<>(
            Arrays.asList("html", "anki", "quizlet", "text"));
    private String languages = "en-ru";
    private String exportMode = "lines";
    private String exportFormat = "html";
    private List<String> sourceSubtitles;
    private List<String> targetSubtitles;
    private List<String> mediaFiles;

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

    private static List checkPaths(List<String> args, String arg) {

        if (args.size() < 1)
            throw new RuntimeException("Invalid number of arguments to option " + arg);

        for (String f : args)
            if (!Files.exists(Paths.get(f)))
                throw new RuntimeException("File doesn't exist : " + f);
        return args;
    }

    /**
     * options usages examples
     * sub2cards -e html|anki|quizlet
     * sub2cards -h
     * sub2cards -m file1 file2 file3
     * sub2cards -s file1 file2 file3 //mandatory
     * sub2cards -t file1 file2 file3
     * sub2cards -l ru //mandatory
     * sub2cards -w words|lines|mixed
     * sub2cards
     *
     * @param args collected arguments
     */
    public static void parseInput(Map<String, List<String>> args, Main main) {

        List<String> options = Arrays.asList("-e", "-h", "-m", "-s", "-t", "-l", "-w");
        if (args.keySet().size() < 1)
            throw new RuntimeException("Not enough arguments");

        for (String arg : args.keySet()) {
            if (!options.contains(arg))
                throw new RuntimeException("Unknown argument : " + arg);
        }

        for (String arg : args.keySet()) {

            switch (arg) {
                case "-h":
                    printHelp();
                    return;
                case "-e":
                    if (args.get(arg).size() != 1)
                        throw new RuntimeException("Invalid number of arguments to option -e");
                    if (!supportedExportFormats.contains(args.get(arg).get(0)))
                        throw new RuntimeException("Invalid parameter used with option -e : "
                                + args.get(arg).get(0));
                    main.exportFormat = args.get(arg).get(0);
                    break;
                case "-m":
                    main.mediaFiles = checkPaths(args.get(arg), arg);
                    break;
                case "-s":
                    main.sourceSubtitles = checkPaths(args.get(arg), arg);
                    break;
                case "-t":
                    main.targetSubtitles = checkPaths(args.get(arg), arg);
                    break;
                case "-w":
                    if (args.get(arg).size() != 1)
                        throw new RuntimeException("Invalid number of arguments to option -w");
                    if (!supportedExportModes.contains(args.get(arg).get(0)))
                        throw new RuntimeException("Invalid parameter used with option -w : "
                                + args.get(arg).get(0));
                    main.exportMode = args.get(arg).get(0);
                    break;
                case "-l":
                    if (args.get(arg).size() != 1)
                        throw new RuntimeException("Invalid number of arguments to option -w");
                    String a = args.get(arg).get(0);
                    if (a.length() != 5 || a.charAt(2) != '-')
                        throw new RuntimeException("Invalid language : " + a);
                    main.languages = args.get(arg).get(0);
                    break;
            }
        }


        if ((!args.containsKey("-s") || !args.containsKey("-l")) && !args.containsKey("-h"))
            throw new RuntimeException("-s and -l options are mandatory");

    }

    /**
     * simply prints a usage help for the tool
     */
    private static void printHelp() {
        System.out.println("Usage: sub2cards -e [html|anki|quizlet|text] -s path -l [languages]");
        System.out.println("-h\t print this [h]elp");
        System.out.println("-e\t [html|anki|quizlet|text] [e]xport to selected format");
        System.out.println("-m\t path(s) to video [m]edia files, used to extract " +
                "sound and thumbnails for a subtitle line");
        System.out.println("-s\t path(s) to subtitle file(s) of '[s]ource language'");
        System.out.println("-t\t path(s) to subtitle file(s) of '[t]arget language'");
        System.out.println("-w\t export mode (only makes sense to use with -e) : words|lines|mixed. See Note 3");
        System.out.println("-l\t source [l]anguage/target [l]anguage, for example 'ru-en'\n");
        System.out.println("Note 1: if -t is not specified, the tool will use Yandex Translate to translate" +
                "the subtitle files to the target language\n" +
                "Note 2 : if -m is specified, thumbnails and audio extracts will be taken\n\t\tfrom the media files" +
                "to illustrate the related subtitle line / word.\n" +
                "Note 3 : for option -w, if 'words' is used, the subtitle files will be parsed,\n\t\twords collected " +
                "and sorted by occurrences, and the flashcards will consist of theses words and their " +
                "Yandex translation\n\t\tIf 'lines'" +
                "is used, then the flashcards will hold the lines as they appear in the subtitles.\n\t\tIf 'mixed'" +
                "is used, then the flashcards will hold the lines, and below the words of the lines and their meaning");
    }

    /**
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
        Main main = new Main();

        main.sourceSubtitles= Arrays.asList("tests/got-simple.srt");
        main.mediaFiles = Arrays.asList("tests/video.avi");

        if (args.length > 0)    {
            try {
                parseInput(collectArgs(args), main);
            }
            catch(Exception e)  {
                printHelp();
                e.printStackTrace();
                System.exit(-1);
            }
        }

        //todo : handle multiple files
        SubParse subParse = new SubParse(main.sourceSubtitles.get(0));
        subParse.parse();


        if(!main.exportMode.equals("words") && !main.targetSubtitles.isEmpty()) {
            List<Line> lines = subParse.getLines();
            SubParse targetSubParse = new SubParse(main.targetSubtitles.get(0));
            targetSubParse.parse();
            List<Line> targetLines = targetSubParse.getLines();

            List<Line> withTranslation = Line.translateLinesParallel(lines, Constants.DEFAULT_LANG);
        /*FlashCard.exportHTML(withTranslation, "tests/video.avi", "tests");*/
            FlashCard.exportAnki(withTranslation, "got-simple2", main.mediaFiles.get(0), "tests/new-deck");
        }
        else if(main.exportMode.equals("words"))    {
            List<Word> words = subParse.getSortedWords();

            //todo : only works for russian language
            if(main.languages.split("-")[0].equals("ru"))   {
                //this is really slow with many words
                List<Word> simplifiedWords = Word.simplifyParallel(words, Constants.FACTOR);
                System.out.println("[*] Simplification done");
                //benchmarkSimplification(words);

                List<Word> translatedWords = Word.translateCollectionParallel(simplifiedWords, Constants.DEFAULT_LANG);
                System.out.println("[*] Translation done");
                List<Word> res = new ArrayList<>(translatedWords.size());
                translatedWords
                        .stream()
                        .sorted(Comparator
                                .comparingInt(Word::getOccurrence)
                                .reversed())
                        .forEach(res::add);

                for(Word w : res) {
                    System.out.println(w.getBaseForm() + " : " + w.getTranslation());
                }

                System.out.println("-----------------------------------\n\n\n");
                for(Word w : res) {
                    System.out.println(w.getBaseForm() + " : " + w.getTranslation() + "#"+w.getOccurrence());
                }
                System.out.println("\n\nCollection of " + res.size() + " words, before : " + words.size());
            }
        }
    }
}
