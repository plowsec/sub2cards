package sub2cards;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;

import static sub2cards.Constants.FACTOR;
import static sub2cards.Constants.NB_PROCS;

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
     * @param cmd the cmd to execute on the local system
     */
    public static void runCmd(String cmd)   {
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
     *
     * @param inputPath path to the video file to extract the sound from
     * @param outputPath output path for the generated file
     * @param timeStart start time offset for extraction
     * @param timeEnd end time offset for extraction
     * @return dummy boolean, to be improved of course
     *
     * The sound asset will be extracted from inputPath, between timeStart and timeEnd
     * PRE: timeStart and timeEnd are valid, no error checking below
     */
    public static boolean extractSound(String inputPath, String outputPath, String timeStart, String timeEnd) {
        File file = new File(inputPath);
        if(!file.isFile())
            throw new RuntimeException("Invalid file : " + inputPath);

        runCmd(String.format(Constants.FFMPEG_SOUND_EXTRACT_FMT, inputPath, timeStart, timeEnd, outputPath));

        //simple ugly check, use error stream please
        File output = new File(outputPath);
        if(!output.isFile())
            throw new RuntimeException("[!] Sound asset could not be extracted : " + inputPath);
        else if(!(output.length()>0))
            throw new RuntimeException("[!] Sound asset could not be extracted : " + inputPath);

        return true;
    }

    /**
     *
     * @param inputPath path to the video file to extract the sound from
     * @param outputPath output path for the generated file
     * @param timeStart start time offset for extraction
     * @return dummy boolean, to be improved of course
     *
     * The image asset will be extracted from inputPath, between timeStart and timeEnd
     * PRE: timeStart and timeEnd are valid, no error checking below
     */
    public static boolean extractThumbnail(String inputPath, String outputPath, String timeStart) {
        File file = new File(inputPath);
        if(!file.isFile())
            throw new RuntimeException("Invalid file : " + inputPath);

        runCmd(String.format(Constants.FFMPEG_THUMBNAIL_EXTRACT_FMT, inputPath, timeStart, outputPath));

        //simple ugly check, use error stream please
        File output = new File(outputPath);
        if(!output.isFile())
            throw new RuntimeException("[!] Image asset could not be extracted : " + inputPath);
        else if(!(output.length()>0))
            throw new RuntimeException("[!] Image asset could not be extracted : " + inputPath);

        return true;
    }

    /**
     * will generate the media assets for a set of Lines. Probably not pretty.
     * @param lines the subtitle lines
     * @param inputPath path to the video media file
     * @param outputPath output directory
     */
    public static void generateAssets(List<Line> lines, String inputPath, String outputPath)    {

        //create assets directory
        //todo : make a function that check these slashes of maybe it already exists
        String assetsPath = outputPath+"/"+Constants.ASSETS_FOLDER;
        File assetsDir = new File(assetsPath);
        boolean ok = assetsDir.mkdir();
        if(!ok)
            throw new RuntimeException("[!] Couldn't create assets directory. Abort");

        final ExecutorService pool = Executors.newFixedThreadPool(NB_PROCS * FACTOR);
        final ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<>(pool);
        for (final Line l : lines) {
            final String output = assetsPath + "/" + l.getTimeStart()
                    .replaceAll("\\.", "_")
                    .replaceAll(":", "_");
            completionService.submit(() -> extractSound(inputPath, output + ".mp3",
                    l.getTimeStart(),l.getTimeEnd()));
            completionService.submit(() -> extractThumbnail(inputPath, output+".jpg",
                    l.getTimeStart()));
        }

        for (int i = 0; i < lines.size()*2; i++) {
            try {
                final Future<Boolean> future = completionService.take();
                final Boolean res = future.get();

            } catch (InterruptedException | ExecutionException e) {
            }
        }

        pool.shutdown();
    }

    /**
     * will export everything to an html document : generate the media assets and the html doc
     * @param lines lines to be exported, with their translation
     * @param inputPath path to the video media file
     * @param exportPath output directory in which to export everything
     */
    public static void exportHTML(List<Line> lines, String inputPath, String exportPath) {

         StringBuilder output = new StringBuilder();
        output.append(Constants.BASE_HTML);
        for(int i = 0 ; i < lines.size() ; i++) {
            final String outputPath = Constants.ASSETS_FOLDER + lines.get(i)
                    .getTimeStart()
                    .replaceAll("\\.", "_")
                    .replaceAll(":", "_");

            output.append(String.format(Constants.THUMBNAIL_HTML_FMT,
                    outputPath+".jpg", i, i, outputPath+".mp3",
                    lines.get(i).getTranslation(),
                    lines.get(i).getText()));
        }

        output.append(Constants.BASE_HTML_END);

        //dump to disk
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(exportPath+"/flashcards.html"), Constants.DEFAULT_ENCODING))) {
            writer.write(output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //parallely generate thumbnails and sound assets
        generateAssets(lines, inputPath, exportPath);
    }
}
