package sub2cards;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a sentence in a subtitle file
 */
public class Line {

    private final String text;

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

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

    public Line setTranslation(String translation)    {
        this.translation = translation;
        return new Line(this);
    }

    public String getText() {
        return text;
    }

    public long getDuration()    {
        LocalTime dateTime1 = LocalTime.parse(timeStart);
        LocalTime dateTime2 = LocalTime.parse(timeEnd);
        return Duration.between(dateTime1, dateTime2).toMillis();
    }

    public Line append(String text) {
       return new Line(this.text +" " + text, timeStart, timeEnd, translation);
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
