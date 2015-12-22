package getting.ui;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import getting.media.MediaConverter;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.prefs.Preferences;

public class SmartFileChooser {

    private final FileChooser fileChooser = new FileChooser();

    {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("视频文件", MediaConverter
                .SUPPORT_VIDEO_FORMAT));
    }

    public File showOpenDialog(final Window ownerWindow) {
        fileChooser.setInitialDirectory(getLastVisitDirectory());
        File file = fileChooser.showOpenDialog(ownerWindow);

        if (file != null) {
            setLastVisitDirectory(file.getParentFile());
        }

        return file;
    }

    @Nullable
    private File getLastVisitDirectory() {
        Preferences preferences = Preferences.userNodeForPackage(SmartFileChooser.class);
        String path = preferences.get(LAST_VISIT_DIRECTORY, null);
        if (path != null) {
            if (new File(path).isDirectory()) {
                return new File(path);
            }
        }

        return null;
    }

    private void setLastVisitDirectory(@NotNull File folder) {
        Preferences preferences = Preferences.userNodeForPackage(SmartFileChooser.class);
        preferences.put(LAST_VISIT_DIRECTORY, folder.getAbsolutePath());
    }

    private static final String LAST_VISIT_DIRECTORY = "last_visit_directory";

}
