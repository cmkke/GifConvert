package getting.control;

import com.sun.istack.internal.NotNull;
import getting.media.MediaConvertParameters;
import getting.media.MediaConvertResult;
import getting.media.MediaConverter;
import getting.ui.HelpWizard;
import getting.ui.SmartFileChooser;
import getting.util.Looper;
import getting.util.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.control.NotificationPane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final Image loadingImage = new Image(Controller.class.getResource("loading5.gif").toExternalForm(),
            true);

    @FXML
    private ImageView gifPreview;
    @FXML
    private ComboBox<Integer> gifFrameRate;
    @FXML
    private ComboBox<Double> gifScale;
    @FXML
    private ComboBox<Integer> gifTime;

    private File videoNeedConvert;

    private final Callback<MediaConvertResult, Void> chooseVideoCallback = new Callback<MediaConvertResult, Void>() {

        @Override
        public Void call(@NotNull MediaConvertResult result) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    showLoadFinish(result);
                }

            });
            return null;
        }

    };

    @FXML
    private NotificationPane notificationPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Looper.prepare();

        gifFrameRate.getItems().addAll(MediaConverter.SUPPORT_GIF_FRAME_RATE);
        gifFrameRate.setValue(MediaConverter.DEFAULT_GIF_FRAME_RATE);

        gifScale.getItems().addAll(MediaConverter.SUPPORT_GIF_SCALE);
        gifScale.setValue(MediaConverter.DEFAULT_GIF_SCALE);

        gifTime.getItems().addAll(MediaConverter.SUPPORT_GIF_TIME);
        gifTime.setValue(MediaConverter.DEFAULT_GIF_TIME);
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
        videoNeedConvert = fileChooser.showOpenDialog(gifPreview.getScene().getWindow());

        reloadMediaConvert();
    }

    @FXML
    private void onChooseTime(ActionEvent event) {
        reloadMediaConvert();
    }

    private void reloadMediaConvert() {
        if (videoNeedConvert == null) {
            return;
        }

        gifPreview.setImage(loadingImage);
        Looper.postMessage(new Message(new Runnable() {

            @Override
            public void run() {
                MediaConverter.convert(new MediaConvertParameters(videoNeedConvert, gifFrameRate
                                .getValue(), gifScale
                                .getValue(), gifTime.getValue()),
                        chooseVideoCallback);
            }

        }, MSG_CONVERT, 0));

    }

    @FXML
    private void onAbout(ActionEvent event) throws IOException {
        new HelpWizard(gifFrameRate.getScene().getWindow()).show();
    }

    @FXML
    private void onChooseFrame(ActionEvent event) {
        reloadMediaConvert();
    }

    private void showLoadFinish(MediaConvertResult result) {
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

    private static final Object MSG_CONVERT = new Object();
    private static final Object MSG_HIDE_NOTIFICATION = new Object();

}
