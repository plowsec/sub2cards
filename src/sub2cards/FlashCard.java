package sub2cards;

import java.io.*;

/**
 * Created by vladimir on 12.07.17.
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
     *
     * The sound asset will be extracted from inputPath, between timeStart and timeEnd
     * PRE: timeStart and timeEnd are valid, no error checking below
     */
    public static void extractSound(String inputPath, String outputPath, String timeStart, String timeEnd) {
        File file = new File(inputPath);
        if(!file.isFile())
            throw new RuntimeException("Invalid file : " + inputPath);

        runCmd(String.format(Constants.FFMPEG_CMD_FMT, inputPath, timeStart, timeEnd, outputPath));

        //simple ugly check, use error stream please
        File output = new File(outputPath);
        if(!output.isFile())
            throw new RuntimeException("[!] Sound asset could not be extracted : " + inputPath);
        else if(!(output.length()>0))
            throw new RuntimeException("[!] Sound asset could not be extracted : " + inputPath);
    }
}
