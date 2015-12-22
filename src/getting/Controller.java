package getting;

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
import javafx.util.Callback;
import org.controlsfx.control.NotificationPane;
import res.Resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final Image loadingImage = new Image(Resource.class.getResource("loading5.gif").toExternalForm(),
            true);

    @FXML
    private ImageView gifPreview;
    @FXML
    private ComboBox<Integer> gifFrameRate;
    @FXML
    private ComboBox<Double> gifScale;

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
    }

    @FXML
    private void onChooseScale(ActionEvent event) {
        reloadConvert();
    }

    @FXML
    private void onChooseVideo(ActionEvent event) {
        videoNeedConvert = new SmartFileChooser().showOpenDialog(gifPreview.getScene().getWindow());

        reloadConvert();
    }

    private void reloadConvert() {
        if (videoNeedConvert == null) {
            return;
        }

        gifPreview.setImage(loadingImage);
        Looper.postMessage(new Message(new Runnable() {

            @Override
            public void run() {
                MediaConverter.convert(new MediaConvertParameters(videoNeedConvert, gifFrameRate
                                .getValue(), gifScale
                                .getValue()),
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
        reloadConvert();
    }

    private void showLoadFinish(MediaConvertResult result) {
        try {
            gifPreview.setImage(new Image(result.getOutFile().toURI().toURL().toExternalForm(), true));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        notificationPane.show("转换时间：" + result.getCostTime() + "，转换后大小：" + result.getFileSize());
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
