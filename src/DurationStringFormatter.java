import com.sun.javafx.binding.StringFormatter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DurationStringFormatter extends StringFormatter {

    private DoubleProperty durationProperty;

    public DurationStringFormatter(DoubleProperty durationProperty) {
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

    @Override
    public ObservableList<ObservableValue<?>> getDependencies() {
        return FXCollections.unmodifiableObservableList(FXCollections
                .observableArrayList(durationProperty));
    }

}
