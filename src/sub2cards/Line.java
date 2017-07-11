package sub2cards;

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

    public Line setTranslation(String translation)    {
        this.translation = translation;
        return new Line(this);
    }

    public String getText() {
        return text;
    }

    public Line append(String text) {
       return new Line(this.text +" " + text, timeStart, timeEnd, translation);
    }

    @Override
    public String toString()    {
        return "[" + timeStart + "-->" + timeEnd +"] " + text;
    }
}
