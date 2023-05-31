import fricke.ScannerManager;
import fricke.util.FilePath;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        new ScannerManager(stage);
    }

    public static void main(String[] args) throws IOException {
        FilePath.removeAllFile();
        File dir = new File(System.getProperty("/"),
                ".credentials/drive-java-quickstart");
        if (!dir.exists()){
            dir.mkdirs();
        }
        if (dir.listFiles().length > 0) {
            dir.listFiles()[0].delete();
        }
        File file = new File("files");
        if (!file.exists()) {
            file.mkdirs();
        }
        launch();
    }
}
