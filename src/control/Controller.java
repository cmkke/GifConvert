package control;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import media.MediaConvertParameters;
import media.MediaConvertResult;
import media.MediaConverter;
import org.controlsfx.control.NotificationPane;
import ui.HelpWizard;
import ui.SmartFileChooser;
import util.Looper;
import util.Message;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ImageView gifPreviewView;
    @FXML
    private ComboBox<Integer> gifFrameRateView;
    @FXML
    private ComboBox<Double> gifScaleView;
    @FXML
    private ComboBox<Integer> gifDurationView;
    @FXML
    private TextField gifStartTimeView;
    @FXML
    private Label mediaInfoView;
    @FXML
    private NotificationPane notificationPane;

    private File mediaHasChoosed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gifFrameRateView.getItems().addAll(MediaConverter.SUPPORT_GIF_FRAME_RATE);
        gifFrameRateView.setValue(MediaConverter.DEFAULT_GIF_FRAME_RATE);

        gifScaleView.getItems().addAll(MediaConverter.SUPPORT_GIF_SCALE);
        gifScaleView.setValue(MediaConverter.DEFAULT_GIF_SCALE);

        gifDurationView.getItems().addAll(MediaConverter.SUPPORT_GIF_TIME);
        gifDurationView.setValue(MediaConverter.DEFAULT_GIF_TIME);

        gifStartTimeView.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                reloadMediaConvert(3000);
            }

        });
    }

    @FXML
    private void onChooseScale(ActionEvent event) {
        reloadMediaConvert(0);
    }

    @FXML
    private void onChooseVideo(ActionEvent event) {
        SmartFileChooser fileChooser = new SmartFileChooser();
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("视频文件", MediaConverter.SUPPORT_VIDEO_FORMAT));
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("所有文件", "*.*"));
        mediaHasChoosed = fileChooser.showOpenDialog(gifPreviewView.getScene().getWindow());

        reloadMediaConvert(0);
    }

    @FXML
    private void onChooseTime(ActionEvent event) {
        reloadMediaConvert(0);
    }

    private void reloadMediaConvert(long delay) {
        if (mediaHasChoosed == null) {
            return;
        }

        hideNotificationPanel();
        Looper.removeMessage(MSG_CONVERT_MEDIA);
        Looper.postMessage(new Message(new Runnable() {

            @Override
            public void run() {
                showLoadingImage();
                MediaConvertResult result = MediaConverter.convert(
                        new MediaConvertParameters(mediaHasChoosed,
                                gifFrameRateView.getValue(),
                                gifScaleView.getValue(),
                                gifStartTimeView.getText(),
                                gifDurationView.getValue()));
                showLoadingFinish(result);
            }

        }, MSG_CONVERT_MEDIA, delay));
    }

    private void showLoadingImage() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                gifPreviewView.setImage(new Image(Controller.class.getResource("loading6.gif").toExternalForm(), true));
            }

        });
    }

    private void hideNotificationPanel() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (notificationPane.isShowing()) {
                    notificationPane.hide();
                }
            }

        });
    }

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

                if (result.isConvertSuccess()) {
                    notificationPane.show("转换时间：" + result.getCostTime() + "，转换后大小：" + result.getFileSize());
                } else {
                    notificationPane.show("转换失败！！是否选择了有效的视频文件？");
                }

                Looper.removeMessage(MSG_HIDE_NOTIFICATION);
                Looper.postMessage(new Message(new Runnable() {

                    @Override
                    public void run() {
                        hideNotificationPanel();
                    }

                }, MSG_HIDE_NOTIFICATION, 3000));
            }

        });
    }

    @FXML
    private void onAbout(ActionEvent event) throws IOException {
        new HelpWizard(gifFrameRateView.getScene().getWindow()).show();
    }

    @FXML
    private void onChooseFrame(ActionEvent event) {
        reloadMediaConvert(3000);
    }

    private static final Object MSG_CONVERT_MEDIA = new Object();
    private static final Object MSG_HIDE_NOTIFICATION = new Object();

}
