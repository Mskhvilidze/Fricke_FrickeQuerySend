package fricke.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private Connection dataSource;

    public DataBaseConnection() {
        try {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            final String user = "e2bis";
            final String password = "n1bbjovt";
            final String url = "jdbc:as400://10.2.1.1/;naming=system;libraries=FRI510AF,FRI510AFWF,FRI510AS,FRL510AP,FRI510AP,AMD510AP,DE510AP,A510AP,A500AP,QGPL,FRIAKO,DATQRYLIB";
            dataSource = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getDataSource(){
        return this.dataSource;
    }
}
