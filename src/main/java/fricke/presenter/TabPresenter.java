package fricke.presenter;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import fricke.message.RequestTab;
import fricke.message.RequestTabPane;
import fricke.model.Country;
import fricke.model.DataBaseUserStore;
import fricke.service.Service;
import fricke.util.DocsQuickstart;
import fricke.util.WorkBookClass;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class TabPresenter implements Initializable {
    public static final String FXML = "/fxml/tab.fxml";
    @FXML
    private MenuButton menuButton;
    @FXML
    private TextField menuItems;
    @FXML
    private Label validator;
    @FXML
    private ProgressIndicator progressBar;
    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private ComboBox<String> country;
    @FXML
    private TextField articles;
    private TabPane tabPane;
    private final List<String> inf = new ArrayList<>();
    private final List<Tab> tabs = new ArrayList<>();
    private final List<String> countryItems = new ArrayList<>();
    private String error = "Bitte überprüfen die eingegebenen Daten!";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Service();
        EventBus bus = Service.getBus();
        bus.register(this);
        this.country.getItems().addAll(FXCollections.observableArrayList(Country.getInstance().getCountries()));
        List<CheckMenuItem> list = getItems();
        menuButton.getItems().addAll(list);
        for (CheckMenuItem checkMenuItem : list) {
            checkMenuItem.setOnAction(event -> {
                CheckMenuItem item = (CheckMenuItem) event.getSource();
                if (item.isSelected()) {
                    countryItems.add(item.getText());
                } else {
                    countryItems.remove(item.getText());
                }
                menuItems.setText(String.join(",", countryItems));
            });
        }
        setDateFormatter(from);
        setDateFormatter(to);
    }

    @FXML
    private void onExecuteQuery() {
        inf.clear();
        progressBarReset();
        if (Integer.parseInt(from.getText()) > Integer.parseInt(to.getText())) {
            Service.alert("Bitte überprüfen eingegebenes Datum! \n" + from.getText() +
                    " darf nicht größer sein als " + to.getText(), "Datum");
            return;
        }
        if (countryItems.isEmpty()) {
            Service.alert("MenuButton: Länder wurden nicht ausgewählt", "Länder");
            return;
        }
        DataBaseUserStore store = new DataBaseUserStore();
        List<String> list = Arrays.asList(articles.getText().split(","));
        store.createFile(from.getText().trim(), to.getText().trim(), countryItems, list, progressBar, tabPane.getSelectionModel().getSelectedItem().getText());
        if (isYearValid(from.getText()) && isYearValid(to.getText())) {
            inf.add(from.getText());
            inf.add(to.getText());
            inf.add(String.join(",", countryItems));
            inf.add(articles.getText());
            for (Tab tab : tabs) {
                if (tab.getUserData() == tabPane.getSelectionModel().getSelectedItem().getUserData()) {
                    if (Service.getMap().containsKey(tab.getUserData())) {
                        Service.getMap().remove(tab.getUserData());
                    }
                    Service.getMap().put(tab.getUserData(), inf);
                }
            }
            validator.setText("");
        } else {
            validator.setText(error);
        }
    }

    @FXML
    private void onSendFile() throws IOException {
        Drive service = DocsQuickstart.getDriveService();
        WorkBookClass workBookClass = new WorkBookClass();
        boolean isExist = false;
        List<File> excelFiles = service.files().list()
                .setQ("mimeType='application/vnd.ms-excel'")
                .execute().getFiles();
        for (File excelFile : excelFiles) {
            if (excelFile.getName().startsWith("SendFile")) {
                isExist = true;
                java.io.File tempFile = new java.io.File("files/temp.xlsx");
                FileOutputStream fileOut = new FileOutputStream(tempFile);
                service.files().get(excelFile.getId())
                        .executeMediaAndDownloadTo(fileOut);
                if (!tempFile.exists() || !new java.io.File("files/" + tabPane.getSelectionModel().getSelectedItem().getText() + ".xlsx").exists()) {
                    Service.alert("Datei wurde nicht erstellt!", "");
                    return;
                }
                boolean isReadXSLSFile = workBookClass.readXLSXFile(tempFile, tabPane.getSelectionModel().getSelectedItem().getText());
                if (isReadXSLSFile) {
                    service.files().delete(excelFile.getId()).execute();
                    send(service, "mergedFile");
                }
            }
        }
        if (!isExist) {
            send(service, tabPane.getSelectionModel().getSelectedItem().getText());
        }
    }

    private void send(Drive service, String filename) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName("SendFile.xlsx");
        // File's content.
        java.io.File filePath = new java.io.File("files/" +
                filename + ".xlsx");
        if (!filePath.exists()) {
            Service.alert("Datei wurde nicht erstellt!", "");
            return;
        }
        // Specify media type and file-path for file.
        FileContent mediaContent = new FileContent("application/vnd.ms-excel", filePath);
        try {
            service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        } catch (GoogleJsonResponseException e) {
            Service.alert("Unable to upload file: " + e.getDetails().toString(), "GoogleJsonResponseException");
            throw e;
        }
        progressBarReset();
        Service.alert("Datei wurde erfolgreich hochgeladen", "");
    }

    private void progressBarReset() {
        progressBar.progressProperty().bind(DoubleBinding.doubleExpression(new DoubleBinding() {
            @Override
            protected double computeValue() {
                return 0;
            }
        }));
    }

    private void setDateFormatter(TextField input) {
        input.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("([1-9][0-9]*)?")) {
                return change;
            }
            validator.setText("Buchstaben sind nicht erlaubt!");
            validator.setTextFill(Color.web("red"));
            return null;
        }));
    }

    private boolean isYearValid(String text) {
        int length = text.length();
        int year = Integer.parseInt(text.substring(0, 4));
        Calendar cal = new GregorianCalendar();
        if (length == 8 && year <= cal.get(Calendar.YEAR)) {
            return true;
        }
        error += "\n" + "* Datum ist nicht korrekt!";
        validator.setTextFill(Color.web("red"));
        return false;
    }

    private List<CheckMenuItem> getItems() {
        List<CheckMenuItem> items = new ArrayList<>();
        for (int i = 0; i < Country.getInstance().getCountries().length; i++) {
            items.add(new CheckMenuItem(Country.getInstance().getCountries()[i]));
        }
        return items;
    }

    @Subscribe
    public void onSetTab(RequestTab request) {
        tabs.add(request.getTab());
    }

    @Subscribe
    public void onSetTabPane(RequestTabPane requestTabPane) {
        this.tabPane = requestTabPane.getTabPane();
    }
}