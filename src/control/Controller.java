package control;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
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

    private final Image loadingImage = new Image(Controller.class.getResource("loading6.gif").toExternalForm(),
            true);

    @FXML
    private ImageView gifPreview;
    @FXML
    private ComboBox<Integer> gifFrameRate;
    @FXML
    private ComboBox<Double> gifScale;
    @FXML
    private ComboBox<Integer> gifTime;
    @FXML
    private TextField gifStartTime;

    private File videoNeedConvert;

    @FXML
    private NotificationPane notificationPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gifFrameRate.getItems().addAll(MediaConverter.SUPPORT_GIF_FRAME_RATE);
        gifFrameRate.setValue(MediaConverter.DEFAULT_GIF_FRAME_RATE);

        gifScale.getItems().addAll(MediaConverter.SUPPORT_GIF_SCALE);
        gifScale.setValue(MediaConverter.DEFAULT_GIF_SCALE);

        gifTime.getItems().addAll(MediaConverter.SUPPORT_GIF_TIME);
        gifTime.setValue(MediaConverter.DEFAULT_GIF_TIME);

        gifStartTime.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                reloadMediaConvert(3000);
            }

        });
    }

    @FXML
    private void onChooseScale(ActionEvent event) {
        reloadMediaConvert();
    }

    @FXML
    private void onChooseVideo(ActionEvent event) {
        SmartFileChooser fileChooser = new SmartFileChooser();
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("视频文件", MediaConverter
                .SUPPORT_VIDEO_FORMAT));
        fileChooser.addExtensionFilters(new FileChooser.ExtensionFilter("所有文件", "*.*"));
        videoNeedConvert = fileChooser.showOpenDialog(gifPreview.getScene().getWindow());

        reloadMediaConvert();
    }

    @FXML
    private void onChooseTime(ActionEvent event) {
        reloadMediaConvert();
    }

    private void reloadMediaConvert(long delay) {
        if (videoNeedConvert == null) {
            return;
        }

        Looper.removeMessage(MSG_CONVERT);
        Looper.postMessage(new Message(new Runnable() {

            @Override
            public void run() {
                showLoadingImage();
                MediaConvertResult result = MediaConverter.convert(
                        new MediaConvertParameters(videoNeedConvert,
                                gifFrameRate.getValue(),
                                gifScale.getValue(),
                                gifStartTime.getText(),
                                gifTime.getValue()));
                showLoadingFinish(result);
            }

        }, MSG_CONVERT, delay));
    }

    private void reloadMediaConvert() {
        reloadMediaConvert(0);
    }

    private void showLoadingImage() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                gifPreview.setImage(loadingImage);
            }

        });
    }

    private void showLoadingFinish(MediaConvertResult result) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    gifPreview.setImage(new Image(result.getOutFile().toURI().toURL().toExternalForm(), true));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                if (result.isConvertSuccess()) {
                    notificationPane.show("转换时间：" + result.getCostTime() + "，转换后大小：" + result.getFileSize());
                } else {
                    notificationPane.show("转换失败！！是否选择了有效的视频文件？");
                }

                Looper.removeMessage(MSG_HIDE_NOTIFICATION);
                Looper.postMessage(new Message(new Runnable() {

                    @Override
                    public void run() {
                        notificationPane.hide();
                    }

                }, MSG_HIDE_NOTIFICATION, 4000));
            }

        });
    }

    @FXML
    private void onAbout(ActionEvent event) throws IOException {
        new HelpWizard(gifFrameRate.getScene().getWindow()).show();
    }

    @FXML
    private void onChooseFrame(ActionEvent event) {
        reloadMediaConvert();
    }

    private static final Object MSG_CONVERT = new Object();
    private static final Object MSG_HIDE_NOTIFICATION = new Object();

}
