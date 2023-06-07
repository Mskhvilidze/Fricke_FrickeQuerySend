package fricke.presenter;

import fricke.model.BasketOfList;
import fricke.model.BasketOfColumns;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;


public class TableViewPresenter {
    @FXML
    private TableColumn<BasketOfColumns, String> olcosp;
    @FXML
    private TableColumn<BasketOfColumns, String> olitet;
    @FXML
    private TableColumn<BasketOfColumns, String> olscpr;
    @FXML
    private TableColumn<BasketOfColumns, String> olsalp;
    @FXML
    private TableColumn<BasketOfColumns, String> olordt;
    @FXML
    private TableColumn<BasketOfColumns, String> olords;
    @FXML
    private TableColumn<BasketOfColumns, String> olcuno;
    @FXML
    private TableColumn<BasketOfColumns, String> oldesc;
    @FXML
    private TableColumn<BasketOfColumns, String> oloqty;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane pane;
    @FXML
    private AnchorPane anchor;
    @FXML
    private TableView<BasketOfColumns> tableView;
    private BasketOfList basketOfList;

    public void setTable(BasketOfList table) {
        tableView.getItems().clear();
        this.basketOfList = table;
        reSize();
        initTableColumn();
        writeDataInTable();

    }

    public void filter(String input) {
        tableView.getItems().clear();
        for (int i = 0; i < basketOfList.getArticles().size(); i++) {
            if (input.equals(basketOfList.getArticles().get(i).replaceAll("'", "").trim())) {
                BasketOfColumns basketOfColumns = getBasketOfColumns(basketOfList, i);
                tableView.getItems().addAll(basketOfColumns);
            }
        }
    }

    public void writeDataInTable() {
        tableView.getItems().clear();
        for (int i = 0; i < basketOfList.getArticles().size(); i++) {
            BasketOfColumns basketOfColumns = getBasketOfColumns(basketOfList, i);
            tableView.getItems().addAll(basketOfColumns);
        }
    }

    private BasketOfColumns getBasketOfColumns(BasketOfList basketOfList, int i) {
        System.out.println(basketOfList.getCountries().size());
        BasketOfColumns basketOfColumns = new BasketOfColumns(basketOfList.getCountries().get(i), basketOfList.getClients().get(i),
                basketOfList.getDate().get(i), basketOfList.getIds().get(i));
        int qty_action = (int) Double.parseDouble(basketOfList.getQty_action().get(i));
        int qty_comparison = (int) Double.parseDouble(basketOfList.getQty_comparison().get(i));
        basketOfColumns.setBasketOfColumns(basketOfList.getArticles().get(i), basketOfList.getSales_volume_action().get(i),
                ""+qty_action, basketOfList.getSales_volume_comparison().get(i), "" + qty_comparison);
        return basketOfColumns;
    }

    private void initTableColumn() {
        olordt.setCellValueFactory(new PropertyValueFactory<>("Land"));
        olords.setCellValueFactory(new PropertyValueFactory<>("Mandant"));
        olcuno.setCellValueFactory(new PropertyValueFactory<>("Datum"));
        oldesc.setCellValueFactory(new PropertyValueFactory<>("Newsletter_ID"));
        oloqty.setCellValueFactory(new PropertyValueFactory<>("Artikelnummer"));
        olsalp.setCellValueFactory(new PropertyValueFactory<>("QTY_Aktion"));
        olscpr.setCellValueFactory(new PropertyValueFactory<>("SalesVolume_Aktion"));
        olitet.setCellValueFactory(new PropertyValueFactory<>("QTY_Comparison"));
        olcosp.setCellValueFactory(new PropertyValueFactory<>("SalesVolume_Comparison"));
    }

    private void reSize() {
        this.anchor.setPrefWidth(1500);
        this.pane.setPrefWidth(1500);
        this.scrollPane.setPrefWidth(1500);
        this.tableView.setPrefWidth(1500);
    }
}
