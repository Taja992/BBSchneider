package DAL;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private final SQLServerDataSource ds;
    private final String sqliteUrl;
    private final boolean isSQLite;

    public ConnectionManager(boolean useSqlServer) {
        if (useSqlServer) {
            ds = new SQLServerDataSource();
            ds.setDatabaseName("BinaryBuddiesSchneider");
            ds.setUser("CSe2023b_e_8");
            ds.setPassword("CSe2023bE8#23");
            ds.setServerName("10.176.111.34");
            ds.setPortNumber(1433);
            ds.setTrustServerCertificate(true);
            ds.setLoginTimeout(1); //we set the login time out and switch to local server if no database connection
            sqliteUrl = null;
            isSQLite = false;
        } else {
            ds = null;
            sqliteUrl = "jdbc:sqlite:src/resources/localschneiderdatabase.db";
            isSQLite = true;
        }
    }

    public Connection getConnection() throws SQLException {
        if(ds != null) {
            return ds.getConnection();
        } else {
            return DriverManager.getConnection(sqliteUrl);
        }
    }

    public boolean isSQLite() {
        return isSQLite;
    }


}