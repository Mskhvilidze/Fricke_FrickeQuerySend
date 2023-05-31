package fricke.model;

import fricke.service.Service;
import fricke.util.WorkBookClass;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;

public class DataBaseUserStore {
    private final DataBaseConnection connection;

    public DataBaseUserStore() {
        connection = new DataBaseConnection();
    }

    // Resultset wird erstellt und PreparedStatement wird zurückgegeben
    public ResultSet executeQuery(String from, String to, List<String> country, List<String> articles) {
        StringJoiner joiner = getJoiner(articles);
        StringJoiner countries = getJoiner(country);
        String query = getQuery(joiner, countries);
        try {
            PreparedStatement statement = connection.getDataSource().prepareStatement(query);
            statement.setString(1, from);
            statement.setString(2, to);
            int i = 3;
            for (String count : country) {
                statement.setNString(i, count.replaceAll("'", "").trim());
                i++;
            }
            int index = country.size() + 3;
            for (String article : articles) {
                statement.setNString(index, article.replaceAll("'", "").trim());
                index++;
            }
            return statement.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    //Progressbar einrichten und Datei erstellen
    public void createFile(String from, String to, List<String> country, List<String> articles, ProgressIndicator progressBar, String filename) {
        WorkBookClass bookClass = new WorkBookClass();
        bookClass.createXLSXFile(filename);
        BasketOfList basketOfList = new BasketOfList();
        int count = getRow(from, to, country, articles);
        int counterpoint = 0;
        int row = 0;
        ResultSet result = executeQuery(from, to, country, articles);
        try {
            while (result.next()) {
                counterpoint++;
                row = result.getRow();
                basketOfList.add(result.getString("OLORDT"), result.getString("OLORDS"),
                        result.getString("OLCUNO"), result.getString("OLORNO"),
                        result.getString("OLPRDC"), result.getString("OLDESC"), result.getString("OLOQTY"));
                basketOfList.add(result.getString("OLSALP"), result.getString("OLSCPR"),
                        result.getString("OLITET"), result.getString("OLCOSP"),
                        result.getString("OLFOCC"), result.getString("NACOUN"));
                basketOfList.add(result.getString("NANAME"), result.getString("OHEXR3"), result.getString("OHPCUR"),
                        result.getString("OHODAT"), result.getString("PGPGRP"));
            }
            if (row <= 0) {
                Service.alert("Bitte überprüfen die eingegebenen Daten!", "Error");
            }else {
                bookClass.writeXLSXFile(filename, basketOfList);
            }
        } catch (SQLException throwables) {
            Service.alert("Bitte überprüfen die eingegebenen Daten!", "Error");
            throwables.printStackTrace();
        }
        Task<Void> task = getTask(counterpoint, count);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());
        executeThread(task).start();
    }

    //Tabelle im Gui füllen
    public BasketOfList fillTableView(String from, String to, List<String> country, List<String> articles) {
        ResultSet result = executeQuery(from, to, country, articles);
        BasketOfList basketOfList = new BasketOfList();
        try {
            while (result.next()) {
                basketOfList.add(result.getString("OLORDT"), result.getString("OLORDS"),
                        result.getString("OLCUNO"), result.getString("OLORNO"),
                        result.getString("OLPRDC"), result.getString("OLDESC"), result.getString("OLOQTY"));
                basketOfList.add(result.getString("OLSALP"), result.getString("OLSCPR"),
                        result.getString("OLITET"), result.getString("OLCOSP"),
                        result.getString("OLFOCC"), result.getString("NACOUN"));
                basketOfList.add(result.getString("NANAME"), result.getString("OHEXR3"), result.getString("OHPCUR"),
                        result.getString("OHODAT"), result.getString("PGPGRP"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return basketOfList;
    }

    //
    private int getRow(String from, String to, List<String> country, List<String> articles) {
        int count = 0;
        StringJoiner joiner = getJoiner(articles);
        StringJoiner countries = getJoiner(country);
        String query = getRowCountQuery(joiner, countries);
        try (PreparedStatement statement = connection.getDataSource().prepareStatement(query)) {
            statement.setString(1, from);
            statement.setString(2, to);
            int i = 3;
            for (String c : country) {
                statement.setNString(i, c.replaceAll("'", "").trim());
                i++;
            }
            int index = country.size() + 3;
            for (String article : articles) {
                statement.setNString(index, article.replaceAll("'", "").trim());
                index++;
            }
            ResultSet set = statement.executeQuery();
            set.next();
            count = set.getInt("recordCount");
            set.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    private String getQuery(StringJoiner joiner, StringJoiner countries) {
        return "SELECT\n" +
                "\tT01.OLORDT,\n" +
                "\tT01.OLORDS,\n" +
                "\tT01.OLCUNO,\n" +
                "\tT01.OLORNO,\n" +
                "\tT01.OLPRDC,\n" +
                "\tT01.OLDESC,\n" +
                "\tT01.OLOQTY,\n" +
                "\tT01.OLSALP,\n" +
                "\tT01.OLSCPR,\n" +
                "\tT01.OLITET,\n" +
                "\tT01.OLCOSP,\n" +
                "\tT01.OLFOCC,\n" +
                "\tT02.NACOUN,\n" +
                "\tT02.NANAME,\n" +
                "\tT04.OHEXR3,\n" +
                "\tT04.OHPCUR,\n" +
                "\tT04.OHODAT,\n" +
                "\tT06.PGPGRP,\n" +
                "\tT03.CTOTIC        \n" +
                "FROM\n" +
                "\tFRI510AFWF.SROORSPL T01 JOIN FRI510AFWF.SRONAM T02 ON T01.OLCUNO = T02.NANUM \n" +
                "\tJOIN FRI510AFWF.SRBSOH T04 ON T01.OLORNO = T04.OHORNO \n" +
                "\tJOIN FRI510AF.SROPRG T06 ON T01.OLPRDC = T06.PGPRDC \n" +
                "\tJOIN FRI510AF.SRBCTLD1 T03 ON  T01.OLORDT = T03.CTOTYP \n" +
                "WHERE\n" +
                "\tT04.OHODAT between ? AND ?   AND\n" +
                "\tT01.OLSTAT <> 'D' AND\n" +
                "\tT01.OLIORD = 'N'  AND\n" +
                "\tT01.OLORDT NOT IN ('IV', 'IS', 'BL', 'IG', 'V4', 'IU') AND\n" +
                "\tT02.NACOUN IN " + countries + " AND\n" +
                "\tT04.OHHAND <> 'PSHOP'   AND\n" +
                "\tT01.OLPRDC IN " + joiner;
    }

    private String getRowCountQuery(StringJoiner joiner, StringJoiner countries) {
        return "SELECT\n" +
                "count(*) AS recordCount " +
                "FROM\n" +
                "\tFRI510AFWF.SROORSPL T01 JOIN FRI510AFWF.SRONAM T02 ON T01.OLCUNO = T02.NANUM \n" +
                "\tJOIN FRI510AFWF.SRBSOH T04 ON T01.OLORNO = T04.OHORNO \n" +
                "\tJOIN FRI510AF.SROPRG T06 ON T01.OLPRDC = T06.PGPRDC \n" +
                "\tJOIN FRI510AF.SRBCTLD1 T03 ON  T01.OLORDT = T03.CTOTYP \n" +
                "WHERE\n" +
                "\tT04.OHODAT between ? AND ?   AND\n" +
                "\tT01.OLSTAT <> 'D' AND\n" +
                "\tT01.OLIORD = 'N'  AND\n" +
                "\tT01.OLORDT NOT IN ('IV', 'IS', 'BL', 'IG', 'V4', 'IU') AND\n" +
                "\tT02.NACOUN IN " + countries + " AND\n" +
                "\tT04.OHHAND <> 'PSHOP'   AND\n" +
                "\tT01.OLPRDC IN " + joiner;
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
}
