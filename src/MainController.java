import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import media.GifConvertParameters;
import media.MediaConvertResult;
import media.MediaConverter;
import media.MediaInfo;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.ToggleSwitch;
import ui.SmartFileChooser;
import ui.ValueAnimator;
import util.Looper;
import util.MessageTask;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final Object MSG_HIDE_NOTIFICATION = new Object();

    private static final Object MSG_CONVERT_MEDIA = new Object();

    private final MediaConverter mediaConverter = new MediaConverter();

    private final Image loadingImage = new Image(MainController.class.getResource("loading.gif").toExternalForm(), true);

    @FXML
    private ImageView outputPreview;

    @FXML
    private Slider outputFrameRate;

    @FXML
    private Slider outputScale;

    @FXML
    private RangeSlider inputMediaDuration;

    @FXML
    private Label inputMediaStartTimeView;

    @FXML
    private Label inputMediaEndTimeView;

    @FXML
    private Pane inputMediaDurationPane;

    @FXML
    private CheckMenuItem reverseOutput;

    @FXML
    private CheckMenuItem addLogo;

    @FXML
    private ToggleSwitch inputMediaDurationDetail;

    @FXML
    private Label mediaInfoView;

    @FXML
    private NotificationPane notificationPane;

    @FXML
    private StatusBar statusBar;

    private ObjectProperty<File> inputMedia = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusBar.progressProperty().bind(mediaConverter.convertProgressProperty());
        mediaInfoView.textProperty().bind(mediaConverter.mediaInfoPropertyProperty().asString());
        inputMediaStartTimeView.textProperty().bind(new DurationStringBinding(inputMediaDuration.lowValueProperty()));
        inputMediaEndTimeView.textProperty().bind(new DurationStringBinding(inputMediaDuration.highValueProperty()));

        inputMediaDuration.setLabelFormatter(new DurationStringConverter());

        {
            final ChangeListener<Number> convertParameterChangeListener = new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    reloadRangeSlide();
                    reloadMediaConvert(1000);
                }

            };

            inputMediaDuration.lowValueProperty().addListener(convertParameterChangeListener);
            inputMediaDuration.highValueProperty().addListener(convertParameterChangeListener);
            outputScale.valueProperty().addListener(convertParameterChangeListener);
            outputFrameRate.valueProperty().addListener(convertParameterChangeListener);
        }

        {
            final ChangeListener<Boolean> convertParameterChangeListener = new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    reloadMediaConvert(0);
                }

            };

            reverseOutput.selectedProperty().addListener(convertParameterChangeListener);
            addLogo.selectedProperty().addListener(convertParameterChangeListener);
        }

        inputMedia.addListener(new ChangeListener<File>() {

            @Override
            public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {
                initMediaConvertDuration();
                reloadMediaConvert(0);
            }

        });

        mediaConverter.mediaInfoPropertyProperty().addListener(new ChangeListener<MediaInfo>() {

            @Override
            public void changed(ObservableValue<? extends MediaInfo> observable, MediaInfo oldValue, MediaInfo newValue) {
                reloadRangeSlide();
            }

        });

        inputMediaDurationDetail.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                animateReloadRangeSlide();
            }

        });

        outputPreview.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.LINK);
            }

        });
        outputPreview.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                List<File> files = event.getDragboard().getFiles();
                if (!files.isEmpty()) {
                    inputMedia.set(files.get(0));
                }
            }

        });
    }

    @FXML
    private void onChooseVideo(ActionEvent event) {
        SmartFileChooser fileChooser = new SmartFileChooser();
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("视频文件", GifConvertParameters.SUPPORT_VIDEO_FORMAT));
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("所有文件", "*.*"));
        inputMedia.set(fileChooser.showOpenDialog(outputPreview.getScene().getWindow()));
    }

    @FXML
    private void onOpenSaveDirectory(ActionEvent event) {
        if (inputMedia.get() == null) {
            return;
        }

        if (!inputMedia.get().exists()) {
            return;
        }

        try {
            java.awt.Desktop.getDesktop().open(inputMedia.get().getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMediaConvertDuration() {
        inputMediaDurationPane.setVisible(false);
        // default convert from 00:00 to 00:10
        inputMediaDuration.setMin(0);
        inputMediaDuration.setMax(10);
        inputMediaDuration.setLowValue(0);
        inputMediaDuration.setHighValue(10);
    }

    private void animateReloadRangeSlide() {
        if (mediaConverter.mediaInfoPropertyProperty().get() == MediaInfo.INVALID) {
            return;
        }

        final double mediaDuration = mediaConverter.mediaInfoPropertyProperty().get().getDuration();

        final double minFrom;
        final double minTo;
        final double maxFrom;
        final double maxTo;
        if (inputMediaDurationDetail.isSelected()) {
            minFrom = 0;
            minTo = Math.max(0, inputMediaDuration.getLowValue() - 10);
            maxFrom = mediaDuration;
            maxTo = Math.min(mediaDuration, inputMediaDuration.getHighValue() + 10);
        } else {
            minFrom = inputMediaDuration.getMin();
            minTo = 0;
            maxFrom = inputMediaDuration.getMax();
            maxTo = mediaDuration;
        }

        if (mediaDurationAnimator != null) {
            mediaDurationAnimator.cancel();
        }
        mediaDurationAnimator = new MediaDurationAnimator(minFrom, minTo, maxFrom, maxTo);
        mediaDurationAnimator.start();

        inputMediaDurationPane.setVisible(true);
    }

    private void reloadRangeSlide() {
        if (mediaConverter.mediaInfoPropertyProperty().get() == MediaInfo.INVALID) {
            return;
        }

        final double mediaDuration = mediaConverter.mediaInfoPropertyProperty().get().getDuration();

        if (inputMediaDurationDetail.isSelected()) {
            inputMediaDuration.setMin(Math.max(0, inputMediaDuration.getLowValue() - 10));
            inputMediaDuration.setMax(Math.min(mediaDuration, inputMediaDuration.getHighValue() + 10));
        } else {
            inputMediaDuration.setMin(0);
            inputMediaDuration.setMax(mediaDuration);
        }
        inputMediaDuration.setMajorTickUnit((inputMediaDuration.getMax() - inputMediaDuration.getMin()) / 10);

        inputMediaDurationPane.setVisible(true);
    }

    private ValueAnimator mediaDurationAnimator;

    private class MediaDurationAnimator extends ValueAnimator {

        private final double minFrom;

        private final double minTo;

        private final double maxFrom;

        private final double maxTo;

        public MediaDurationAnimator(double minFrom, double minTo, double maxFrom, double maxTo) {
            super(500);
            this.minFrom = minFrom;
            this.minTo = minTo;
            this.maxFrom = maxFrom;
            this.maxTo = maxTo;
        }

        @Override
        public void onAnimate(double progress) {
            inputMediaDuration.setMin(calculate(minFrom, minTo, progress));
            inputMediaDuration.setMax(calculate(maxFrom, maxTo, progress));
            inputMediaDuration.setMajorTickUnit((inputMediaDuration.getMax() - inputMediaDuration.getMin()) / 10);
        }

    }

    public static double calculate(double from, double to, double progress) {
        return from + (to - from) * progress;
    }

    private void reloadMediaConvert(long delay) {
        Looper.removeMessage(MSG_CONVERT_MEDIA);

        notificationPane.hide();

        if (inputMedia.get() == null) {
            return;
        }

        if (!inputMedia.get().exists() || !inputMedia.get().isFile()) {
            notificationPane.show("所选择的文件已被删除，请重新选择文件");
            return;
        }

        if (inputMediaDuration.getHighValue() - inputMediaDuration.getLowValue() > 30) {
            notificationPane.show("转换时间长度过长");
            return;
        }

        Looper.postMessage(new ConvertMediaTask(delay));
    }

    private void showLoadingImage() {
        outputPreview.setImage(loadingImage);
    }

    private void showLoadingFinish(MediaConvertResult result) {
        if (result == null) {
            return;
        }

        try {
            outputPreview.setImage(new Image(result.getOutputFile().toURI().toURL().toExternalForm(), true));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (result.isCanceled()) {
            return;
        }

        showNotificationForAWhile(result.getResult());
    }

    private void showNotificationForAWhile(String message) {
        notificationPane.show(message);

        Looper.removeMessage(MSG_HIDE_NOTIFICATION);
        Looper.postMessage(new HideNotificationTask(3000));
    }

    private class HideNotificationTask extends MessageTask<Void> {

        public HideNotificationTask(long delay) {
            super(MSG_HIDE_NOTIFICATION, delay);
        }

        @Override
        public void preTaskOnUi() {
            notificationPane.hide();
        }

        @Override
        public Void runTask() {
            return null;
        }

        @Override
        public void postTaskOnUi(Void result) {
        }

        @Override
        public void cancel() {
        }

    }

    private class ConvertMediaTask extends MessageTask<MediaConvertResult> {

        public ConvertMediaTask(long delay) {
            super(MSG_CONVERT_MEDIA, delay);
        }

        @Override
        public void preTaskOnUi() {
            showLoadingImage();
        }

        @Override
        public MediaConvertResult runTask() {
            String logo = addLogo.isSelected() ? new SimpleDateFormat().format(new Date()) : " ";
            return mediaConverter.convert(
                    new GifConvertParameters(inputMedia.get(),
                            outputFrameRate.getValue(),
                            outputScale.getValue(),
                            inputMediaDuration.getLowValue(),
                            inputMediaDuration.getHighValue() - inputMediaDuration.getLowValue(),
                            reverseOutput.isSelected(),
                            logo));
        }

        @Override
        public void postTaskOnUi(MediaConvertResult result) {
            showLoadingFinish(result);
        }

        @Override
        public void cancel() {
            mediaConverter.cancel();
        }

    }

}
