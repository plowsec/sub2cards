package sub2cards;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static sub2cards.Constants.FACTOR;
import static sub2cards.Constants.NB_PROCS;
import static sub2cards.Utils.randomString;

/**
 * Module responsible for generating the flashcards and exporting
 * them.
 * For now, only the html doc export feature exists, but Anki
 * and Quizlet will be supported.
 */
public class FlashCard {

    /**
     * todo : there is a proper way to do this, for now
     * we are just interested in running the command and
     * we do not care about ouput/error stream
     *
     * @param cmd the cmd to execute on the local system
     */
    public static void runCmd(String cmd) {
        Runtime run = Runtime.getRuntime();

        Process pr;
        try {
            pr = run.exec(cmd);
            pr.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param inputPath  path to the video file to extract the sound from
     * @param outputPath output path for the generated file
     * @param timeStart  start time offset for extraction
     * @param timeEnd    end time offset for extraction
     * @return dummy boolean, to be improved of course
     * <p>
     * The sound asset will be extracted from inputPath, between timeStart and timeEnd
     * PRE: timeStart and timeEnd are valid, no error checking below
     */
    public static boolean extractSound(String inputPath, String outputPath,
                                       String timeStart, String timeEnd, boolean extension) {
        File file = new File(inputPath);
        if (!file.isFile())
            throw new RuntimeException("Invalid file : " + inputPath);

        String filename = extension ? outputPath + ".mp3" : outputPath;

        runCmd(String.format(Constants.FFMPEG_SOUND_EXTRACT_FMT,
                inputPath,
                timeStart,
                timeEnd,
                filename));

        //simple ugly check, use error stream please
        File output = new File(outputPath);
        if (!output.isFile())
            throw new RuntimeException("[!] Sound asset could not be extracted : " + inputPath);
        else if (!(output.length() > 0))
            throw new RuntimeException("[!] Sound asset could not be extracted : " + inputPath);

        return true;
    }

    /**
     * @param inputPath  path to the video file to extract the sound from
     * @param outputPath output path for the generated file
     * @param timeStart  start time offset for extraction
     * @return dummy boolean, to be improved of course
     * <p>
     * The image asset will be extracted from inputPath, between timeStart and timeEnd
     * PRE: timeStart and timeEnd are valid, no error checking below
     * PRE: outputPath is a name, no extension
     */
    public static boolean extractThumbnail(String inputPath, String outputPath,
                                           String timeStart, boolean extension) {
        File file = new File(inputPath);
        if (!file.isFile())
            throw new RuntimeException("Invalid file : " + inputPath);

        String filename = extension ? outputPath + ".jpg" : outputPath;
        String cmd = String.format(Constants.FFMPEG_THUMBNAIL_EXTRACT_FMT,
                inputPath, timeStart, filename);
        runCmd(cmd);

        //simple ugly check, use error stream please
        File output = new File(outputPath);
        if (!output.isFile())
            throw new RuntimeException("[!] Image asset could not be extracted : " + inputPath);
        else if (!(output.length() > 0))
            throw new RuntimeException("[!] Image asset could not be extracted : " + inputPath);

        return true;
    }

    /**
     * will generate the media assets for a set of Lines. Probably not pretty.
     *
     * @param subtitle   subtitle name
     * @param lines      the subtitle lines
     * @param inputPath  path to the video media file
     * @param outputPath output directory
     */
    public static void generateAssets(List<Line> lines, String subtitle, String inputPath, String outputPath) {

        //create assets directory
        //todo : make a function that check these slashes of maybe it already exists
        File assetsDir = new File(outputPath);
        boolean ok = assetsDir.mkdir();
        if (!ok && !assetsDir.isDirectory())
            throw new RuntimeException("[!] Couldn't create assets directory. Abort");

        final ExecutorService pool = Executors.newFixedThreadPool(NB_PROCS * FACTOR);
        final ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<>(pool);
        AtomicInteger mediaCounter = new AtomicInteger(0);
        for (final Line l : lines) {
            //final String output = outputPath + generateAssetBaseName(subtitle, l);
            final String outputSound = outputPath + "/" + mediaCounter.getAndIncrement();
            final String outputImg = outputPath + "/" + mediaCounter.getAndIncrement();
            completionService.submit(() -> extractSound(inputPath, outputSound,
                    l.getTimeStart(), l.getTimeEnd(), false));
            completionService.submit(() -> extractThumbnail(inputPath, outputImg,
                    l.getTimeStart(), false));
        }

        for (int i = 0; i < lines.size() * 2; i++) {
            try {
                final Future<Boolean> future = completionService.take();
                final Boolean res = future.get();

            } catch (InterruptedException | ExecutionException e) {
            }
        }

        pool.shutdown();
    }

    private static String generateAssetBaseName(String subtitleName, Line line) {
        StringBuilder sb = new StringBuilder();
        sb.append(subtitleName);
        sb.append(line.getTimeStart());
        return sb.toString()
                .replaceAll("\\.", "_")
                .replaceAll(":", "_");
    }

    /**
     * will export everything to an html document : generate the media assets and the html doc
     *
     * @param subtitle   subtitle name
     * @param lines      lines to be exported, with their translation
     * @param inputPath  path to the video media file
     * @param exportPath output directory in which to export everything
     */
    public static void exportHTML(List<Line> lines, String subtitle, String inputPath, String exportPath) {

        StringBuilder output = new StringBuilder();
        output.append(Constants.BASE_HTML);
        for (int i = 0; i < lines.size(); i++) {
            final String outputPath = Constants.ASSETS_FOLDER + generateAssetBaseName(subtitle, lines.get(i));

            output.append(String.format(Constants.THUMBNAIL_HTML_FMT,
                    outputPath + ".jpg", i, i, outputPath + ".mp3",
                    lines.get(i).getTranslation(),
                    lines.get(i).getText()));
        }

        output.append(Constants.BASE_HTML_END);

        //dump to disk
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(exportPath + "/flashcards.html"), Constants.DEFAULT_ENCODING))) {
            writer.write(output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //parallely generate thumbnails and sound assets
        generateAssets(lines, subtitle, inputPath, exportPath + "/" + Constants.ASSETS_FOLDER);
    }

    /**
     * will generate an .apkg file after having generated an anki and a media file
     *
     * @param subtitle   subtitle name
     * @param lines      lines to be exported, with their translation
     * @param inputPath  path to the video media file
     * @param exportPath output directory in which to export everything
     */
    public static void exportAnki(List<Line> lines, String subtitle, String inputPath, String exportPath) {

        //todo : deck id
        StringBuilder output = new StringBuilder();
        StringBuilder cardOutput = new StringBuilder();
        StringBuilder mediaOutput = new StringBuilder();
        mediaOutput.append("{");
        output.append(Constants.ANKI_HEAD);

        long mod = Instant.now().toEpochMilli();
        int partId = Integer.parseInt(randomString(Constants.INT_CHARSET, Constants.ANKI_FLOAT_ID_LENGTH));
        int mediaIDCounter = 0;
        for (Line line : lines) {
            String assetName = generateAssetBaseName(subtitle, line);
            mediaOutput.append(String.format("\"%d\":\"%s.mp3\", \"%d\":\"%s.jpg\",",
                    mediaIDCounter++, assetName, mediaIDCounter++, assetName));
            String nid = mod + String.valueOf(partId++); //mod + 3 digits, random, increment
            String guid = randomString(Constants.ANKI_GUID_LENGTH);
            String sfld = line.getTranslation().replace("'", "''");
            String flds = String.format(Constants.ANKI_FLDS_FMT,
                    sfld, line.getText().replace("''", "''"), assetName, assetName);
            String csum = Utils.sha256(sfld).substring(0, Constants.ANKI_HASH_LENGTH);
            long sum = Long.parseLong(csum, Constants.HEX_BASE);

            String noteRequest = String.format(Constants.ANKI_NOTE_FMT, nid, guid, mod, flds, sfld, sum);
            output.append(noteRequest);

            System.out.println(noteRequest);

            //generate card
            String cid = String.valueOf(mod + partId++);
            String cardRequest = String.format(Constants.ANKI_CARD_FMT, cid, nid, mod);
            System.out.println(cardRequest);
            cardOutput.append(cardRequest);
        }

        mediaOutput.setLength(mediaOutput.length()-1);
        mediaOutput.append("}");
        output.append(Constants.ANKI_MID);
        output.append(cardOutput.toString());
        output.append(Constants.ANKI_FOOT);

        //dump to disk the sql file
        /*try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(exportPath+"/collection.anki2.sql"), Constants.DEFAULT_ENCODING))) {
            writer.write(output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Utils.createDataBase(exportPath+"/collection.anki2", output.toString());
        //dump to disk the media file
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(exportPath+"/media"), Constants.DEFAULT_ENCODING))) {
            writer.write(mediaOutput.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //parallely generate thumbnails and sound assets
        generateAssets(lines, subtitle, inputPath, exportPath);

        //zip it
        Utils.pack(exportPath, exportPath+"/"+subtitle+".apkg");

        //delete temp files
        Utils.cleanTemporaryFiles(exportPath, partId);
    }

    public static void testAnki() {
        //todo : deck id
        //generate note : id, guid, mod, flds, sfld, csum
        StringBuilder output = new StringBuilder();
        long mod = Instant.now().toEpochMilli();
        String partId = randomString("123456789", 3);
        String nid = mod + partId; //mod + 3 digits, random, increment
        String guid = randomString(10); //10 chars, random
        String flds = "Just\u001FТолько\u001F<img src=\"00_03_51_160.jpg\" />\u001F[sound:00_03_51_160.mp3]";
        String sfld = "Just";
        String csum = Utils.sha256(sfld).substring(0, 8);

        long sum = Long.parseLong(csum, 16);

        String noteRequest = String.format(Constants.ANKI_NOTE_FMT, nid, guid, mod, flds, sfld, sum);
        System.out.println(noteRequest);

        //generate card
        int ccid = Integer.parseInt(partId)+1;
        String cid = String.valueOf(mod+ ccid);
        String cardRequest = String.format(Constants.ANKI_CARD_FMT, cid, nid, mod);
        System.out.println(cardRequest);
        output.append(Constants.ANKI_HEAD);
        output.append(noteRequest);
        output.append(Constants.ANKI_MID);
        output.append(cardRequest);
        output.append(Constants.ANKI_FOOT);

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("tests/test.sql"), Constants.DEFAULT_ENCODING))) {
            writer.write(output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
