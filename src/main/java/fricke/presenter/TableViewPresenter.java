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
    private TableColumn<BasketOfColumns, String> pgpgrp;
    @FXML
    private TableColumn<BasketOfColumns, String> ohodat;
    @FXML
    private TableColumn<BasketOfColumns, String> ohpcur;
    @FXML
    private TableColumn<BasketOfColumns, String> ohexr3;
    @FXML
    private TableColumn<BasketOfColumns, String> naname;
    @FXML
    private TableColumn<BasketOfColumns, String> nacoun;
    @FXML
    private TableColumn<BasketOfColumns, String> olfocc;
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
    private TableColumn<BasketOfColumns, String> olorno;
    @FXML
    private TableColumn<BasketOfColumns, String> olprdc;
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

    public void setTest(BasketOfList table) {
        tableView.getItems().clear();
        this.basketOfList = table;
        reSize();
        initTableColumn();
        writeDataInTable();

    }

    public void filter(String input) {
        System.out.println(input);
        tableView.getItems().clear();
        for (int i = 0; i < basketOfList.getOLCUNO().size(); i++) {
            if (input.equals(basketOfList.getOLCUNO().get(i).replaceAll("'", "").trim())
                    || input.equals(basketOfList.getOLORNO().get(i).replaceAll("'", "").trim())
                    || input.equals(basketOfList.getOLPRDC().get(i).replaceAll("'", "").trim())) {
                BasketOfColumns basketOfColumns = getBasketOfColumns(basketOfList, i);
                tableView.getItems().addAll(basketOfColumns);
            }
        }
    }

    public void writeDataInTable() {
        for (int i = 0; i < basketOfList.getOLORDS().size(); i++) {
            BasketOfColumns basketOfColumns = getBasketOfColumns(basketOfList, i);
            tableView.getItems().addAll(basketOfColumns);
        }
    }

    private BasketOfColumns getBasketOfColumns(BasketOfList basketOfList, int i) {
        BasketOfColumns basketOfColumns = new BasketOfColumns(basketOfList.getOLORDT().get(i),
                basketOfList.getOLORDS().get(i),
                basketOfList.getOLCUNO().get(i),
                basketOfList.getOLORNO().get(i),
                basketOfList.getOLPRDC().get(i),
                basketOfList.getOLDESC().get(i),
                basketOfList.getOLOQTY().get(i));
        basketOfColumns.setBasketOfColumns(basketOfList.getOLSALP().get(i),
                basketOfList.getOLSCPR().get(i), basketOfList.getOLCOSP().get(i), basketOfList.getOLITET().get(i),
                basketOfList.getOLFOCC().get(i), basketOfList.getNACOUN().get(i));
        basketOfColumns.setBasketOfColumns(basketOfList.getNANAME().get(i), basketOfList.getOHEXR3().get(i),
                basketOfList.getOHPCUR().get(i), basketOfList.getOHODAT().get(i), basketOfList.getPGPGRP().get(i));
        return basketOfColumns;
    }

    private void initTableColumn() {
        olordt.setCellValueFactory(new PropertyValueFactory<>("OLORDT"));
        olords.setCellValueFactory(new PropertyValueFactory<>("OLORDS"));
        olcuno.setCellValueFactory(new PropertyValueFactory<>("OLCUNO"));
        olorno.setCellValueFactory(new PropertyValueFactory<>("OLORNO"));
        olprdc.setCellValueFactory(new PropertyValueFactory<>("OLPRDC"));
        oldesc.setCellValueFactory(new PropertyValueFactory<>("OLDESC"));
        oloqty.setCellValueFactory(new PropertyValueFactory<>("OLOQTY"));
        olsalp.setCellValueFactory(new PropertyValueFactory<>("OLSALP"));
        olscpr.setCellValueFactory(new PropertyValueFactory<>("OLSCPR"));
        olcosp.setCellValueFactory(new PropertyValueFactory<>("OLCOSP"));
        olitet.setCellValueFactory(new PropertyValueFactory<>("OLITET"));
        olfocc.setCellValueFactory(new PropertyValueFactory<>("OLFOCC"));
        nacoun.setCellValueFactory(new PropertyValueFactory<>("NACOUN"));
        naname.setCellValueFactory(new PropertyValueFactory<>("NANAME"));
        ohexr3.setCellValueFactory(new PropertyValueFactory<>("OHEXR3"));
        ohpcur.setCellValueFactory(new PropertyValueFactory<>("OHPCUR"));
        ohodat.setCellValueFactory(new PropertyValueFactory<>("OHODAT"));
        pgpgrp.setCellValueFactory(new PropertyValueFactory<>("PGPGRP"));
    }

    private void reSize() {
        this.anchor.setPrefWidth(1500);
        this.pane.setPrefWidth(1500);
        this.scrollPane.setPrefWidth(1500);
        this.tableView.setPrefWidth(1500);
    }
}
