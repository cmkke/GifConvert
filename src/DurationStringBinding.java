import com.sun.javafx.binding.StringFormatter;
import javafx.beans.property.DoubleProperty;

public class DurationStringBinding extends StringFormatter {

    private DoubleProperty durationProperty;

    public DurationStringBinding(DoubleProperty durationProperty) {
        this.durationProperty = durationProperty;
        super.bind(durationProperty);
    }

    public static String formatMediaDuration(int duration) {
        return String.format("%02d:%02d", duration / 60, duration % 60);
    }

    @Override
    public void dispose() {
        super.unbind(durationProperty);
    }

    @Override
    protected String computeValue() {
        return formatMediaDuration(durationProperty.intValue());
    }

}
