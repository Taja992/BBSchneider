package DAL;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SnapshotDAO {

    private ConnectionManager connectionManager;

    private String folderPath = "jdbc:sqlite:src/resources/";


    public SnapshotDAO(){
        ConnectionManager databaseConnectionManager;
        try {
            databaseConnectionManager = new ConnectionManager(true);
            databaseConnectionManager.getConnection().close(); // Need this to test connection and force the SQLException and swap to false
            connectionManager = databaseConnectionManager;
        } catch (SQLException e) {
            connectionManager = new ConnectionManager(false);
        }
        createNewSnapshotFile("TestDB.db");
    }

    public void createNewSnapshotFile(String fileName){
        String filepath = folderPath + fileName;

        try {
            Connection con = DriverManager.getConnection(filepath);
            //this establishes connection and creates the database at the same time

            if(con != null){
                //DatabaseMetaData meta = con.getMetaData();
                System.out.println("it worked?");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
