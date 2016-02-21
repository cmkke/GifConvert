import javafx.util.StringConverter;

public class DurationStringConverter extends StringConverter<Number> {

    @Override
    public String toString(Number object) {
        return String.format("%02d:%02d", object.intValue() / 60, object.intValue() % 60);
    }

    @Override
    public Number fromString(String string) {
        return Double.parseDouble(string);
    }

}
