package control;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import media.MediaConvertParameters;
import media.MediaConvertResult;
import media.MediaConverter;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.StatusBar;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
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

    private static final Object MSG_CONVERT_MEDIA = new Object();

    private static final Object MSG_HIDE_NOTIFICATION = new Object();

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

    @FXML
    private StatusBar statusBar;

    private File mediaHasChoosed;

    private MediaConverter mediaConverter = new MediaConverter();

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
        ValidationSupport startTimeValidationSupport = new ValidationSupport();
        startTimeValidationSupport.setValidationDecorator(new GraphicValidationDecoration());
        startTimeValidationSupport.registerValidator(gifStartTimeView, new Validator<String>() {

            @Override
            public ValidationResult apply(Control control, String s) {
                return ValidationResult.fromErrorIf(control, "时间格式不正确", !MediaConverter.validateMediaStartTime(s));
            }

        });

        statusBar.progressProperty().bind(mediaConverter.convertProgressProperty());

        showLoadingImage();
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
        if (notificationPane.isShowing()) {
            notificationPane.hide();
        }

        if (!MediaConverter.validateMediaStartTime(gifStartTimeView.getText())) {
            return;
        }

        if (mediaHasChoosed == null) {
            return;
        }

        if (!mediaHasChoosed.exists() || !mediaHasChoosed.isFile()) {
            notificationPane.show("所选择的文件已被删除，请重新选择文件");
        }

        Looper.removeMessage(MSG_CONVERT_MEDIA);
        Looper.postMessage(new Message(new Runnable() {

            @Override
            public void run() {
                showLoadingImage();
                MediaConvertResult result = mediaConverter.convert(new MediaConvertParameters(mediaHasChoosed,
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
                gifPreviewView.setImage(new Image(Controller.class.getResource("loading2.gif").toExternalForm(), true));
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

    @FXML
    private void onAbout(ActionEvent event) throws IOException {
        new HelpWizard(gifFrameRateView.getScene().getWindow()).show();
    }

    @FXML
    private void onChooseFrame(ActionEvent event) {
        reloadMediaConvert(0);
    }

}
