package fricke;

import fricke.presenter.AbstractPresenter;
import fricke.presenter.TabPresenter;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class ScannerManager {
    private Stage primaryStage;
    private Scene scene;

    public ScannerManager(Stage stage) throws IOException {
        this.primaryStage = stage;
        initView();
    }

    public ScannerManager() {

    }

    private void initView() throws IOException {
        showScene();
        setMinAndMaxSizeOfStage();
    }


    private void showScene() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(AbstractPresenter.FXML));
        Parent rootParent = loader.load();
        AbstractPresenter presenter = loader.getController();
        presenter.setStage(primaryStage);
        Platform.runLater(() -> {
            presenter.viewHomeImage();
            primaryStage.initStyle(StageStyle.UTILITY);
            primaryStage.setTitle("Platform");
            scene = new Scene(rootParent, 700, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    public Parent showTabs() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(TabPresenter.FXML));
        Parent tab = loader.load();
        return tab;
    }

    private void setMinAndMaxSizeOfStage() {
        this.primaryStage.setMinHeight(600);
        this.primaryStage.setMinWidth(600);
        this.primaryStage.setMaxHeight(630);
    }
}
