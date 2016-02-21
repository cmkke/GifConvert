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
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.ToggleSwitch;
import ui.SmartFileChooser;
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
    private ImageView gifPreviewView;

    @FXML
    private Slider gifFrameRateView;

    @FXML
    private Slider gifScaleView;

    @FXML
    private RangeSlider gifConvertRange;

    @FXML
    private Label gifStartTimeView;

    @FXML
    private Label gifEndTimeView;

    @FXML
    private Pane gifConvertRangePane;

    @FXML
    private CheckMenuItem reverseGifView;

    @FXML
    private CheckMenuItem addLogoView;

    @FXML
    private ToggleSwitch gifConvertRangeDetail;

    @FXML
    private Label mediaInfoView;

    @FXML
    private NotificationPane notificationPane;

    @FXML
    private StatusBar statusBar;

    private ObjectProperty<File> mediaToBeConverted = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showLoadingImage();

        statusBar.progressProperty().bind(mediaConverter.convertProgressProperty());
        mediaInfoView.textProperty().bind(mediaConverter.mediaInfoPropertyProperty().asString());
        gifStartTimeView.textProperty().bind(new DurationStringBinding(gifConvertRange.lowValueProperty()));
        gifEndTimeView.textProperty().bind(new DurationStringBinding(gifConvertRange.highValueProperty()));

        gifConvertRange.setLabelFormatter(new DurationStringConverter());

        {
            final ChangeListener<Number> convertParameterChangeListener = new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    reloadMediaConvert(1000);
                }

            };

            gifConvertRange.lowValueProperty().addListener(convertParameterChangeListener);
            gifConvertRange.highValueProperty().addListener(convertParameterChangeListener);
            gifScaleView.valueProperty().addListener(convertParameterChangeListener);
            gifFrameRateView.valueProperty().addListener(convertParameterChangeListener);
        }

        {
            final ChangeListener<Boolean> convertParameterChangeListener = new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    reloadMediaConvert(0);
                }

            };

            reverseGifView.selectedProperty().addListener(convertParameterChangeListener);
            addLogoView.selectedProperty().addListener(convertParameterChangeListener);
        }

        mediaToBeConverted.addListener(new ChangeListener<File>() {

            @Override
            public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {
                initRangeSlide();
                reloadMediaConvert(0);
            }

        });

        gifConvertRangeDetail.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                reloadRangeSlide();
            }

        });

        gifPreviewView.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.LINK);
            }

        });
        gifPreviewView.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                List<File> files = event.getDragboard().getFiles();
                if (!files.isEmpty()) {
                    mediaToBeConverted.set(files.get(0));
                }
            }

        });

    }

    @FXML
    private void onChooseVideo(ActionEvent event) {
        SmartFileChooser fileChooser = new SmartFileChooser();
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("视频文件", GifConvertParameters.SUPPORT_VIDEO_FORMAT));
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("所有文件", "*.*"));
        mediaToBeConverted.set(fileChooser.showOpenDialog(gifPreviewView.getScene().getWindow()));
    }

    @FXML
    private void onOpenSaveDirectory(ActionEvent event) {
        if (mediaToBeConverted.get() == null) {
            return;
        }

        if (!mediaToBeConverted.get().exists()) {
            return;
        }

        try {
            java.awt.Desktop.getDesktop().open(mediaToBeConverted.get().getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRangeSlide() {
        gifConvertRangePane.setVisible(false);
        // default convert from 00:00 to 00:10
        gifConvertRange.setMin(0);
        gifConvertRange.setMax(10);
        gifConvertRange.setLowValue(0);
        gifConvertRange.setHighValue(10);
    }

    private void reloadRangeSlide() {
        gifConvertRangePane.setVisible(true);

        if (gifConvertRangeDetail.isSelected()) {
            final double mediaDuration = mediaConverter.mediaInfoPropertyProperty().get().getDuration();
            gifConvertRange.setMin(Math.max(0, gifConvertRange.getLowValue() - 30));
            gifConvertRange.setMax(Math.min(mediaDuration, gifConvertRange.getHighValue() + 30));
            gifConvertRange.setMajorTickUnit((gifConvertRange.getMax() - gifConvertRange.getMin()) / 10);
        } else {
            final double mediaDuration = mediaConverter.mediaInfoPropertyProperty().get().getDuration();
            gifConvertRange.setMajorTickUnit(Math.max(10, mediaDuration / 10));
            gifConvertRange.setMin(0);
            gifConvertRange.setMax(mediaDuration);
        }
    }

    private void reloadMediaConvert(long delay) {
        Looper.removeMessage(MSG_CONVERT_MEDIA);

        notificationPane.hide();

        if (mediaToBeConverted.get() == null) {
            return;
        }

        if (!mediaToBeConverted.get().exists() || !mediaToBeConverted.get().isFile()) {
            notificationPane.show("所选择的文件已被删除，请重新选择文件");
            return;
        }

        if (gifConvertRange.getHighValue() - gifConvertRange.getLowValue() > 30) {
            notificationPane.show("转换时间长度过长");
            return;
        }

        Looper.postMessage(new ConvertMediaTask(delay));
    }

    private void showLoadingImage() {
        gifPreviewView.setImage(loadingImage);
    }

    private void showLoadingFinish(MediaConvertResult result) {
        try {
            gifPreviewView.setImage(new Image(result.getOutputFile().toURI().toURL().toExternalForm(), true));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        reloadRangeSlide();

        if (result.isCanceled()) {
        } else if (result.isSuccess()) {
            showNotificationForAWhile("转换时间：" + result.getCostTimeString() + "，转换后大小：" + result.getFileSize());
        } else {
            showNotificationForAWhile("转换失败！！是否选择了有效的视频文件？");
        }
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
            String logo = addLogoView.isSelected() ? new SimpleDateFormat().format(new Date()) : " ";
            return mediaConverter.convert(
                    new GifConvertParameters(mediaToBeConverted.get(),
                            gifFrameRateView.getValue(),
                            gifScaleView.getValue(),
                            gifConvertRange.getLowValue(),
                            gifConvertRange.getHighValue() - gifConvertRange.getLowValue(),
                            reverseGifView.isSelected(),
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
