package fricke.model;

import fricke.service.Service;
import fricke.util.Client;
import fricke.util.SQLColumns;
import fricke.util.WorkBookClass;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class DataBaseUserStore {
    private final DataBaseConnection connection;
    private final Client client = new Client();

    public DataBaseUserStore() {
        connection = new DataBaseConnection();
    }

    // Resultset wird erstellt und PreparedStatement wird zurückgegeben
    public ResultSet executeQuery(String from, String to, List<String> country, List<String> articles,
                                  List<String> comparisonDate) {
        StringJoiner joiner = getJoiner(articles);
        StringJoiner countries = getJoiner(country);
        String query = getQuery(joiner, countries);
        int index = 0;
        try {
            PreparedStatement statement = connection.getDataSource().prepareStatement(query);
            //Select Bediengungen
            index = setStatementDate(statement, from, to, comparisonDate, index);
            //Where Bediengungen
            index = setStatementQueryDate(statement, from, to, comparisonDate, index);
            setStatementCountryAndArticle(statement, country, articles, index);
            return statement.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    //Progressbar einrichten und Datei erstellen
    public void createFile(String from, String to, List<String> country, List<String> articles,
                           ProgressIndicator progressBar, String filename, List<String> basketForDateAndTabPane) {
        WorkBookClass bookClass = new WorkBookClass();
        bookClass.createXLSXFile(filename);
        BasketOfList basketOfList = new BasketOfList();
        int count = getRow(from, to, country, articles, basketForDateAndTabPane);
        int counterpoint = 0;
        int row = 0;
        ResultSet result = executeQuery(from, to, country, articles, basketForDateAndTabPane);
        try {
            while (result.next()) {
                counterpoint++;
                row = result.getRow();
                String newsletter = Service.getBasketForNewsletter().get(basketForDateAndTabPane.get(2))
                        .get(result.getString(SQLColumns.COUNTRY.value()).trim());
                basketOfList.add(result.getString(SQLColumns.ARTICLE.value()),
                        formatSales(result.getString(SQLColumns.SALES_ACTION.value())),
                        formatQty(result.getString(SQLColumns.AMOUNT_ACTION.value())),
                        formatSales(result.getString(SQLColumns.SALES_COMPARISON.value())),
                        formatQty(result.getString(SQLColumns.AMOUNT_COMPARISON.value())));
                String nacoun = this.client.getClient(result.getString(SQLColumns.COUNTRY.value())
                        .toUpperCase(Locale.ROOT).trim());
                basketOfList.add(result.getString(SQLColumns.COUNTRY.value()), nacoun, to, nacoun + "_" + to + "_" +
                        newsletter);
                Task<Void> task = getTask(counterpoint, count);
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(task.progressProperty());
                executeThread(task).start();
            }
            if (row <= 0) {
                Service.alert("Bitte überprüfen die eingegebenen Daten!", "Error");
            } else {
                bookClass.writeXLSXFile(filename, basketOfList);
            }
        } catch (SQLException throwables) {
            Service.alert("Bitte überprüfen die eingegebenen Daten!", "Error");
            throwables.printStackTrace();
        }
    }

    //Tabelle im Gui füllen
    public BasketOfList fillTableView(String from, String to, List<String> country, List<String> articles,
                                      List<String> basketForDateAndTabPane) {
        ResultSet result = executeQuery(from, to, country, articles, basketForDateAndTabPane);
        BasketOfList basketOfList = new BasketOfList();
        try {
            while (result.next()) {
                String newsletter = Service.getBasketForNewsletter().get(basketForDateAndTabPane.get(2))
                        .get(result.getString(SQLColumns.COUNTRY.value()).trim());
                basketOfList.add(result.getString(SQLColumns.ARTICLE.value()),
                        formatSales(result.getString(SQLColumns.SALES_ACTION.value())),
                        formatQty(result.getString(SQLColumns.AMOUNT_ACTION.value())),
                        formatSales(result.getString(SQLColumns.SALES_COMPARISON.value())),
                        formatQty(result.getString(SQLColumns.AMOUNT_COMPARISON.value())));
                String client = this.client.getClient(
                        result.getString(SQLColumns.COUNTRY.value()).toUpperCase(Locale.ROOT).trim());
                basketOfList.add(result.getString(SQLColumns.COUNTRY.value()), client, to, client + "_" + to + "_" +
                        newsletter);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return basketOfList;
    }

    //
    private int getRow(String from, String to, List<String> country, List<String> articles,
                       List<String> comparisonDate) {
        int count = 0;
        int index = 0;
        StringJoiner joiner = getJoiner(articles);
        StringJoiner countries = getJoiner(country);
        String query = getRowCountQuery(joiner, countries);
        try (PreparedStatement statement = connection.getDataSource().prepareStatement(query)) {
            //Where Bediengungen
            index = setStatementQueryDate(statement, from, to, comparisonDate, index);
            setStatementCountryAndArticle(statement, country, articles, index);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                count++;
            }
            set.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }


    private String getQuery(StringJoiner joiner, StringJoiner countries) {
        return "SELECT\n" +
                "\tH01.OLPRDC,\n" +
                "\tsum(CASE WHEN H04.OHODAT BETWEEN ? AND ? THEN H01.OLITET ELSE 0 END) AS \"Umsatz Aktion\",\n" +
                "\tsum(CASE WHEN H04.OHODAT BETWEEN ? AND ? THEN H01.OLOQTY ELSE 0 END) AS \"Menge Aktion\",\n" +
                "\tsum(CASE WHEN H04.OHODAT BETWEEN ? AND ? THEN H01.OLITET ELSE 0 END) AS \"Umsatz Vergleich\",\n" +
                "\tsum(CASE WHEN H04.OHODAT BETWEEN ? AND ? THEN H01.OLOQTY ELSE 0 END) AS \"Menge Vergleich\",\n" +
                "\tH02.NACOUN\n" +
                "FROM\n" +
                "\tFRI510AFWF.SROORSPL H01\n" +
                "JOIN FRI510AFWF.SRONAM H02 ON\n" +
                "\tH01.OLCUNO = H02.NANUM\n" +
                "JOIN FRI510AFWF.SRBSOH H04 ON\n" +
                "\tH01.OLORNO = H04.OHORNO\n" +
                "JOIN FRI510AF.SROPRG H06 ON\n" +
                "\tH01.OLPRDC = H06.PGPRDC\n" +
                "JOIN FRI510AF.SRBCTLD1 H03 ON\n" +
                "\tH01.OLORDT = H03.CTOTYP\n" +
                "WHERE\n" +
                "\t( H04.OHODAT BETWEEN ? AND ? \n" +
                "\t--Aktionszeitraum\n" +
                "\t\tOR H04.OHODAT BETWEEN ? AND ? ) \n" +
                "\t--Vergleichzeitraum\n" +
                "\tAND H01.OLSTAT <> 'D'\n" +
                "\tAND H01.OLIORD = 'N'\n" +
                "\tAND H01.OLORDT NOT IN ('IV', 'IS', 'BL', 'IG', 'V4', 'IU')\n" +
                "\tAND H02.NACOUN IN " + countries + "\n" +
                "\tAND H04.OHHAND <> 'PSHOP'\n" +
                "\tAND H01.OLPRDC IN " + joiner + "\n" +
                "GROUP BY\n" +
                "\tH01.OLPRDC, H02.NACOUN";
    }

    private String getRowCountQuery(StringJoiner joiner, StringJoiner countries) {
        return "SELECT\n" +
                "\tcount(*) AS recordCount \n" +
                "FROM\n" +
                "\tFRI510AFWF.SROORSPL H01\n" +
                "JOIN FRI510AFWF.SRONAM H02 ON\n" +
                "\tH01.OLCUNO = H02.NANUM\n" +
                "JOIN FRI510AFWF.SRBSOH H04 ON\n" +
                "\tH01.OLORNO = H04.OHORNO\n" +
                "JOIN FRI510AF.SROPRG H06 ON\n" +
                "\tH01.OLPRDC = H06.PGPRDC\n" +
                "JOIN FRI510AF.SRBCTLD1 H03 ON\n" +
                "\tH01.OLORDT = H03.CTOTYP\n" +
                "WHERE\n" +
                "\t( H04.OHODAT BETWEEN ? AND ? \n" +
                "\t--Aktionszeitraum\n" +
                "\t\tOR H04.OHODAT BETWEEN ? AND ? ) \n" +
                "\t--Vergleichzeitraum\n" +
                "\tAND H01.OLSTAT <> 'D'\n" +
                "\tAND H01.OLIORD = 'N'\n" +
                "\tAND H01.OLORDT NOT IN ('IV', 'IS', 'BL', 'IG', 'V4', 'IU')\n" +
                "\tAND H02.NACOUN IN " + countries + "\n" +
                "\tAND H04.OHHAND <> 'PSHOP'\n" +
                "\tAND H01.OLPRDC IN " + joiner + "\n" +
                "GROUP BY\n" +
                "\tH01.OLPRDC, H02.NACOUN";
    }

    private StringJoiner getJoiner(List<String> articles) {
        StringJoiner joiner = new StringJoiner(",", "(", ")");
        for (int i = 0; i < articles.size(); i++) {
            joiner.add("?");
        }
        return joiner;
    }

    private Task<Void> getTask(int bar, int count) {
        return new Task<Void>() {
            @Override
            protected Void call() {
                updateProgress(bar, count);
                return null;
            }
        };
    }

    private Thread executeThread(Task<Void> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        return thread;
    }

    private int setStatementDate(PreparedStatement statement, String from, String to, List<String> comparisonDate,
                                 int index) throws SQLException {
        //Aktionszeitraum
        statement.setString(index = index + 1, from);
        statement.setString(index = index + 1, to);
        statement.setString(index = index + 1, from);
        statement.setString(index = index + 1, to);
        //Vergleichzeitraum
        statement.setString(index = index + 1, comparisonDate.get(0).trim());
        statement.setString(index = index + 1, comparisonDate.get(1).trim());
        statement.setString(index = index + 1, comparisonDate.get(0).trim());
        statement.setString(index = index + 1, comparisonDate.get(1).trim());
        return index;
    }

    private int setStatementQueryDate(PreparedStatement statement, String from, String to, List<String> comparisonDate,
                                      int index) throws SQLException {
        //Where Aktionszeitraum
        statement.setString(index = index + 1, from);
        statement.setString(index = index + 1, to);

        //Where Vergleichzeitraum
        statement.setString(index = index + 1, comparisonDate.get(0).trim());
        statement.setString(index = index + 1, comparisonDate.get(1).trim());
        index = index + 1;
        return index;
    }

    private int setStatementCountryAndArticle(PreparedStatement statement, List<String> country, List<String> articles,
                                              int index) throws SQLException {
        //Country
        for (String count : country) {
            statement.setNString(index, count.replaceAll("'", "").trim());
            index++;
        }
        //article
        for (String article : articles) {
            statement.setNString(index, article.replaceAll("'", "").trim());
            index++;
        }
        return index;
    }

    private String formatQty(String qty) {
        if (qty.isEmpty()) {
            qty = "0";
        }
        Double formatted = Double.parseDouble(qty);
        return String.valueOf(formatted.intValue());
    }

    private String formatSales(String sales) {
        if (sales.isEmpty()) {
            sales = "0";
        }
        Double formatted = Double.parseDouble(sales);
        return String.valueOf(formatted).replace(".", ",");
    }
}
