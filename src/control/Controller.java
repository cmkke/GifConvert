package control;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import media.GifConvertParameters;
import media.MediaConvertResult;
import media.MediaConverter;
import media.MediaInfo;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.spreadsheet.StringConverterWithFormat;
import ui.SmartFileChooser;
import util.Looper;
import util.Message;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final Object MSG_CONVERT_MEDIA = new Object();

    private static final Object MSG_HIDE_NOTIFICATION = new Object();

    @FXML
    private ImageView gifPreviewView;

    @FXML
    private Slider gifFrameRateView;

    @FXML
    private Slider gifScaleView;

    @FXML
    private RangeSlider gifConvertRange;
    @FXML
    private Label mediaInfoView;

    @FXML
    private NotificationPane notificationPane;

    @FXML
    private StatusBar statusBar;

    private File mediaToBeConverted;

    private final MediaConverter mediaConverter = new MediaConverter();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusBar.progressProperty().bind(mediaConverter.convertProgressProperty());

        gifConvertRange.lowValueProperty().addListener(changeListener);
        gifConvertRange.highValueProperty().addListener(changeListener);
        gifConvertRange.setLabelFormatter(new StringConverterWithFormat<Number>() {

            @Override
            public String toString(Number object) {
                return String.format("%02d:%02d", object.intValue() / 60, object.intValue() % 60);
            }

            @Override
            public Number fromString(String string) {
                return Double.valueOf(string);
            }

        });
        gifFrameRateView.valueProperty().addListener(changeListener);
        gifScaleView.valueProperty().addListener(changeListener);

        showLoadingImage();
    }

    private final ChangeListener<Number> changeListener = new ChangeListener<Number>() {

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            reloadMediaConvert(3000);
        }

    };

    @FXML
    private void onChooseVideo(ActionEvent event) {
        SmartFileChooser fileChooser = new SmartFileChooser();
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("视频文件", GifConvertParameters.SUPPORT_VIDEO_FORMAT));
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("所有文件", "*.*"));
        mediaToBeConverted = fileChooser.showOpenDialog(gifPreviewView.getScene().getWindow());

        reloadMediaConvert(0);
    }

    private void reloadRangeSlide(MediaInfo info) {
        gifConvertRange.setVisible(true);
        if (info.getDuration() < 60) {
            gifConvertRange.setMajorTickUnit(5);
        } else {
            gifConvertRange.setMajorTickUnit(info.getDuration() / 10);
        }
        gifConvertRange.setMax(info.getDuration());
    }

    private void reloadMediaConvert(long delay) {
        Looper.removeMessage(MSG_CONVERT_MEDIA);

        if (notificationPane.isShowing()) {
            notificationPane.hide();
        }

        if (mediaToBeConverted == null) {
            return;
        }

        if (!mediaToBeConverted.exists() || !mediaToBeConverted.isFile()) {
            notificationPane.show("所选择的文件已被删除，请重新选择文件");
            return;
        }

        if (gifConvertRange.getHighValue() - gifConvertRange.getLowValue() > 30) {
            notificationPane.show("转换时间长度过长");
            return;
        }

        Looper.postMessage(new Message(new Runnable() {

            @Override
            public void run() {
                showLoadingImage();
                MediaConvertResult result = mediaConverter.convert(new GifConvertParameters(mediaToBeConverted,
                        gifFrameRateView.getValue(),
                        gifScaleView.getValue(),
                        gifConvertRange.getLowValue(),
                        gifConvertRange.getHighValue() - gifConvertRange.getLowValue()));
                showLoadingFinish(result);
            }

        }, MSG_CONVERT_MEDIA, delay));
    }

    private void showLoadingImage() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                gifPreviewView.setImage(loadingImage);
            }

        });
    }

    private final Image loadingImage = new Image(Controller.class.getResource("loading.gif").toExternalForm(), true);

    private void showLoadingFinish(MediaConvertResult result) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    gifPreviewView.setImage(new Image(result.getOutputFile().toURI().toURL().toExternalForm(), true));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                mediaInfoView.setText(result.getMediaInfo().toString());
                reloadRangeSlide(result.getMediaInfo());

                if (result.isSuccess()) {
                    showNotificationForAWhile("转换时间：" + result.getCostTimeString() + "，转换后大小：" + result.getFileSize(), 3000);
                } else {
                    showNotificationForAWhile("转换失败！！是否选择了有效的视频文件？", 3000);
                }
            }

        });
    }

    private void showNotificationForAWhile(String message, long duration) {
        notificationPane.show(message);

        Looper.removeMessage(MSG_HIDE_NOTIFICATION);
        Looper.postMessage(new Message(new Runnable() {

            @Override
            public void run() {
                if (notificationPane.isShowing()) {
                    notificationPane.hide();
                }
            }

        }, MSG_HIDE_NOTIFICATION, 3000, true));
    }

}
