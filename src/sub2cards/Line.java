package sub2cards;

import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static sub2cards.Constants.FACTOR;
import static sub2cards.Constants.NB_PROCS;

/**
 * Represents a sentence in a subtitle file
 */
public class Line {

    private final String text;
    private final String timeStart;
    private final String timeEnd;
    private String translation;

    public Line(String text, String timeStart, String timeEnd)  {
        this.text = text;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public Line(String text, String timeStart, String timeEnd, String translation)  {
        this.text = text;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.translation = translation;
    }
    public Line(Line line)  {
        text = line.text;
        timeStart = line.timeStart;
        timeEnd = line.timeEnd;
        translation = line.translation;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    /**
     * allows to override the translation field and force capitalization
     * @param translation the new value for translation
     * @return a new instance of Line
     */
    public Line setTranslation(String translation)    {

        this.translation = translation.substring(0,1).toUpperCase() + translation.substring(1);
        return new Line(this);
    }

    public String getText() {
        return text;
    }

    public String getTranslation()  {
        return translation;
    }

    public long getDuration()    {
        LocalTime dateTime1 = LocalTime.parse(timeStart);
        LocalTime dateTime2 = LocalTime.parse(timeEnd);
        return Duration.between(dateTime1, dateTime2).toMillis();
    }

    public Line append(String text) {
       return new Line(this.text +" " + text, timeStart, timeEnd, translation);
    }

    /**
     *
     * @param lines lines to be translated
     * @param lang src-dest languages
     * @return a new collection containing the original words and their translation
     */
    public static List<Line> translateLinesParallel(List<Line> lines, String lang)  {

        List<Line> translatedLines = new ArrayList<>(lines.size());
        final ExecutorService pool = Executors.newFixedThreadPool(NB_PROCS * FACTOR);
        final ExecutorCompletionService<Line> completionService = new ExecutorCompletionService<>(pool);
        for (final Line l : lines) {
            completionService.submit(() -> l.setTranslation(
                    Word.getTranslation(
                            URLEncoder.encode(l.getText(), Constants.DEFAULT_ENCODING),
                            lang)));
        }

        for (int i = 0; i < lines.size(); i++) {
            try {
                final Future<Line> future = completionService.take();
                final Line translation = future.get();
                translatedLines.add(translation);

            } catch (InterruptedException | ExecutionException e) {
            }
        }

        pool.shutdown();
        return translatedLines;
    }

    @Override
    public String toString()    {
        return "[" + timeStart + "-->" + timeEnd +"] " + text;
    }

    @Override
    public int hashCode()   {
        return Objects.hash(text, timeStart, timeEnd, translation);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof Line))
            return false;
        Line l = (Line) o;
        return Objects.equals(l.translation, translation) &&
                Objects.equals(l.text, text) &&
                Objects.equals(l.timeStart, timeStart) &&
                Objects.equals(l.timeEnd, timeEnd);
    }
}
