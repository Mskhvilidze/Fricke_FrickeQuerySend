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
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class TabPresenter implements Initializable {
    public static final String FXML = "/fxml/tab.fxml";
    @FXML
    private AnchorPane paneForNewsletter;
    @FXML
    private ListView listView;
    @FXML
    private Button fileSend;
    @FXML
    private TextField newsletter;
    @FXML
    private TextField comparisonPeriodFrom;
    @FXML
    private TextField comparisonPeriodTo;
    @FXML
    private MenuButton menuButton;
    @FXML
    private TextField menuItems;
    @FXML
    private Label validator;
    @FXML
    private ProgressIndicator progressBar;
    @FXML
    private TextField actionPeriodFrom;
    @FXML
    private TextField actionPeriodTo;
    @FXML
    private TextField articles;
    private TabPane tabPane;
    private final List<String> recordList = new ArrayList<>();
    private final List<Tab> tabs = new ArrayList<>();
    private final List<String> countryItems = new ArrayList<>();
    private String error = "Bitte überprüfen die eingegebenen Daten!";
    private List<TextField> textFields;
    private Map<String, String> newsletterIdByCountryMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Service();
        EventBus bus = Service.getBus();
        bus.register(this);
        listView.setEditable(true);
        List<CheckMenuItem> list = getItems();
        menuButton.getItems().addAll(list);
        for (CheckMenuItem checkMenuItem : list) {
            handleCheckMenuItemAction(checkMenuItem);
        }
        setDateFormatter(comparisonPeriodFrom);
        setDateFormatter(comparisonPeriodTo);
        setDateFormatter(actionPeriodFrom);
        setDateFormatter(actionPeriodTo);
        textFields = Arrays.asList(actionPeriodFrom, actionPeriodTo, comparisonPeriodFrom, comparisonPeriodTo);
        clearValidatorLabel();
    }

    @FXML
    private void onExecuteQuery() {
        recordList.clear();
        progressBarReset();
        Service.deleteAllFiles();
        boolean isCheckField = checkFields();
        if (!isCheckField) {
            return;
        }
        boolean isValid = required(actionPeriodFrom, actionPeriodTo, countryItems);
        if (!isValid) {
            return;
        }
        for (Object item : listView.getItems()) {
            String temp = item.toString();
            String[] splitTemp = temp.split(":");
            if (splitTemp.length < 2 || splitTemp[1].trim().isEmpty()) {
                Service.alert(splitTemp[0].trim() + " fehlt Newsletter_ID", "Error");
                return;
            }
            newsletterIdByCountryMap.put(splitTemp[0].trim(), splitTemp[1].trim());
            Service.getBasketForNewsletter().put(tabPane.getSelectionModel().getSelectedItem().getUserData(),
                    newsletterIdByCountryMap);
        }
        DataBaseUserStore store = new DataBaseUserStore();
        List<String> list = Arrays.asList(articles.getText().split(","));
        List<String> basketForDateAndTabPane = new ArrayList<>();
        basketForDateAndTabPane.add(comparisonPeriodFrom.getText());
        basketForDateAndTabPane.add(comparisonPeriodTo.getText());
        //Hier wird auch tabpane gespeichert, damit NewsletterId aus Map geholt werden kann in DataBaseUserStore
        basketForDateAndTabPane.add(String.valueOf(tabPane.getSelectionModel().getSelectedItem().getUserData()));
        store.createFile(actionPeriodFrom.getText().trim(), actionPeriodTo.getText().trim(), countryItems, list,
                progressBar,
                tabPane.getSelectionModel().getSelectedItem().getText(), basketForDateAndTabPane);
        if (isYearValid(actionPeriodFrom.getText()) && isYearValid(actionPeriodTo.getText()) &&
                isYearValid(comparisonPeriodFrom.getText()) &&
                isYearValid(comparisonPeriodTo.getText())) {
            recordList.add(actionPeriodFrom.getText());
            recordList.add(actionPeriodTo.getText());
            recordList.add(String.join(",", countryItems));
            recordList.add(articles.getText());
            recordList.add(comparisonPeriodFrom.getText());
            recordList.add(comparisonPeriodTo.getText());
            recordList.add(newsletter.getText());
            for (Tab tab : tabs) {
                if (tab.getUserData() == tabPane.getSelectionModel().getSelectedItem().getUserData()) {
                    if (Service.getMap().containsKey(tab.getUserData())) {
                        Service.getMap().remove(tab.getUserData());
                    }
                    Service.getMap().put(tab.getUserData(), recordList);
                }
            }
            validator.setText("");
            fileSend.setDisable(false);
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
                .setQ("mimeType='application/vnd.ms-excel' or " +
                        "mimeType='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' or mimeType='text/csv'")
                .execute().getFiles();
        for (File excelFile : excelFiles) {
            if (excelFile.getName().startsWith("SendFile")) {
                isExist = true;
                java.io.File tempFile = new java.io.File("files/temp.xlsx");
                FileOutputStream fileOut = new FileOutputStream(tempFile);
                service.files().get(excelFile.getId())
                        .executeMediaAndDownloadTo(fileOut);
                if (!tempFile.exists() || !new java.io.File("files/" + tabPane.getSelectionModel()
                        .getSelectedItem().getText() + ".xlsx").exists()) {
                    Service.alert("Datei wurde nicht erstellt!", "");
                    return;
                }
                if (!workBookClass.checkFile(tempFile)){
                    isExist = false;
                    service.files().delete(excelFile.getId()).execute();
                }else {
                    boolean isReadXSLSFile = workBookClass.readXLSXFile(tempFile, tabPane.getSelectionModel()
                            .getSelectedItem().getText());
                    if (isReadXSLSFile) {
                        service.files().delete(excelFile.getId()).execute();
                        fileSend.setDisable(true);
                        send(service, "mergedFile");
                    }
                }
            }
        }
        if (!isExist) {
            fileSend.setDisable(true);
            send(service, tabPane.getSelectionModel().getSelectedItem().getText());
        }
    }

    @FXML
    private void onCloseWindow() {
        this.paneForNewsletter.setVisible(false);
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

    private boolean required(TextField from, TextField to, List<String> countryItems) {
        //Wenn anfangszeit größer ist als endzeit
        if (Integer.parseInt(from.getText()) > Integer.parseInt(to.getText())) {
            Service.alert("Bitte überprüfen eingegebenes Datum! \n" + from.getText() +
                    " darf nicht größer sein als " + to.getText(), "Datum");
            return false;
        }

        //NewsletterID darf nicht leer sein
        BooleanBinding binding = newsletter.textProperty().isEmpty();
        if (binding.get()) {
            Service.alert("Newsletter_ID: Id muss eingegeben werden!", "Newsletter_ID");
            return false;
        }

        //Wenn der Benutzer vergessen, die Länder auszuwählen
        if (countryItems.isEmpty()) {
            Service.alert("MenuButton: Länder wurden nicht ausgewählt", "Länder");
            return false;
        }

        return true;
    }

    private boolean checkFields() {
        for (TextField textField : textFields) {
            BooleanBinding binding = textField.textProperty().isEmpty();
            if (binding.get()) {
                error += "\n" + "Felder müssen gefüllt werden";
                validator.setText(error);
                validator.setTextFill(Color.web("red"));
                return false;
            }
        }
        return true;
    }

    private void clearValidatorLabel() {
        for (TextField textField : textFields) {
            textField.focusedProperty().addListener(observable -> {
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> validator.setText(""));
                    }
                }, 15000);
            });
        }
    }

    public void handleCheckMenuItemAction(CheckMenuItem checkMenuItem) {
        checkMenuItem.setOnAction(event -> {
            this.paneForNewsletter.setVisible(true);
            CheckMenuItem item = (CheckMenuItem) event.getSource();
            if (item.isSelected()) {
                countryItems.add(item.getText());
                //Newsletter_ID Editierbar
                listView.setCellFactory(TextFieldListCell.forListView(new StringConverter<Object>() {
                    String temp = "";

                    @Override
                    public String toString(Object object) {
                        temp = object.toString();
                        return object.toString();
                    }

                    @Override
                    public Object fromString(String string) {
                        boolean isValid = string.contains(":");
                        if (!isValid) {
                            String prefix = temp.substring(0, temp.substring(0, 3).endsWith("-") ? 3 : 2);
                            return prefix + " : " + string;
                        } else {
                            return string;
                        }
                    }
                }));
                listView.getItems().add(item.getText() + ":");
            } else {
                countryItems.remove(item.getText());
                Iterator<String> iterator = listView.getItems().iterator();
                while (iterator.hasNext()) {
                    String temp = iterator.next();
                    // Unterschied zwischen Mandanten, z.B FR und FR-
                    if (temp.substring(0, 3).endsWith("-")) {
                        if (item.getText().equals(temp.substring(0, 3))) {
                            iterator.remove();
                        }
                    } else {
                        if (item.getText().equals(temp.substring(0, 2))) {
                            iterator.remove();
                        }
                    }
                }
            }
            menuItems.setText(String.join(",", countryItems));
        });
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