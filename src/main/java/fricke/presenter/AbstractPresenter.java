package fricke.presenter;

import fricke.ScannerManager;
import fricke.model.BasketOfColumns;
import fricke.model.DataBaseUserStore;
import fricke.model.BasketOfList;
import fricke.service.Service;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AbstractPresenter implements Initializable {
    public static final String FXML = "/fxml/view.fxml";
    @FXML
    private Button reset;
    @FXML
    private Pane pane;
    @FXML
    private AnchorPane tableView;
    @FXML
    private Button search;
    @FXML
    private TextField input;
    @FXML
    private AnchorPane anchor;
    @FXML
    private TableViewPresenter tableViewController;
    @FXML
    private ImageView imageView;
    @FXML
    private Tab tab;
    @FXML
    private TabPane tabPane;
    private final ScannerManager manager = new ScannerManager();
    private Image image;
    private final DataBaseUserStore store = new DataBaseUserStore();
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        image = new Image(String.valueOf(getClass().getResource("/image/fricke.png")), 910, 367, false, false);
        new Service();
        try {
            tab.setContent(manager.showTabs());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Service.onRequestTab(tab);
        Service.onRequestTabPane(tabPane);
    }

    @FXML
    private void onCreateTabPane() throws IOException {
        Tab newTab = Service.createTab();
        Service.counter();
        newTab.setText("SendFile " + Service.getCount());
        newTab.setUserData(tabPane.getTabs().size() == 1 ? tabPane.getTabs().size() : tabPane.getTabs().size() + 1);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        newTab.setContent(manager.showTabs());
        tabPane.getTabs().add(newTab);
        Service.onRequestTab(newTab);
        tabPane.getSelectionModel().getSelectedItem().setOnSelectionChanged((event -> Service.onRequestTabPane(tabPane)));
    }

    public void viewHomeImage() {
        Platform.runLater(() -> imageView.setImage(image));
    }

    @FXML
    private void onClearDirectory() {
        System.out.println(Service.getMap().get(this.tabPane.getSelectionModel().getSelectedItem().getUserData()).get(3));
    }

    @FXML
    private void onOpenTableView() {
        if (Service.getMap().get(this.tabPane.getSelectionModel().getSelectedItem().getUserData()) == null) {
            Service.alert("Daten wurde nicht geladen", "Empty");
            return;
        }
        toggleNodeVisible(pane,tableView , false, true);
        String from = Service.getMap().get(this.tabPane.getSelectionModel().getSelectedItem().getUserData()).get(0);
        String to = Service.getMap().get(this.tabPane.getSelectionModel().getSelectedItem().getUserData()).get(1);
        List<String> country = Arrays.asList(Service.getMap().get(this.tabPane.getSelectionModel().getSelectedItem()
                .getUserData()).get(2).split(","));
        List<String> articles = Arrays.asList(Service.getMap().get(this.tabPane.getSelectionModel().getSelectedItem()
                .getUserData()).get(3).split(","));
        BasketOfList table1 = store.fillTableView(from.trim(), to.trim(), country, articles);
        tableViewController.setTest(table1);
        reSize(1700.0);
        toggleNodeVisible(search, input, reset, true);
    }

    @FXML
    private void onCloseTableView() {
        toggleNodeVisible(pane,tableView , true, false);
        toggleNodeDisable(reset, true);
        toggleNodeVisible(search, input, reset, false);
        reSize(720.0);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onFilter() {
        tableViewController.filter(input.getText().trim());
        toggleNodeDisable(reset, false);
    }

    @FXML
    private void onReset() {
        tableViewController.writeDataInTable();
        toggleNodeDisable(reset, true);
    }

    private void toggleNodeVisible(Node search, Node input, Node reset, boolean visible) {
        search.setVisible(visible);
        input.setVisible(visible);
        reset.setVisible(visible);
    }

    private void toggleNodeVisible(Pane pane, AnchorPane tableView, boolean visible, boolean visible1) {
        pane.setVisible(visible);
        tableView.setVisible(visible1);
    }

    private void toggleNodeDisable(Node reset, boolean visible) {
        reset.setDisable(visible);
    }

    private void reSize(double width) {
        this.stage.setWidth(width);
        this.anchor.setPrefWidth(width);
    }
}
