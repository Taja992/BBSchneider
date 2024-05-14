package DAL;

import java.sql.*;

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
        createNewSnapshotFile("TestDB");
    }

    public void createNewSnapshotFile(String fileName){
        String filepath = folderPath + fileName + ".db";

        try {
            Connection SQLiteCon = DriverManager.getConnection(filepath);
            //this creates SQLite daabase and establishes connection at the same time

            Connection DBCon = connectionManager.getConnection();
            //this gets the connection to our regular database (the one we're copying)

            
            String sql = "CREATE TABLE IF NOT EXISTS Employee\n" +
                    "(\n" +
                    "    Employee_Id          INTEGER PRIMARY KEY,\n" +
                    "    Name                 nvarchar(50) not null,\n" +
                    "    AnnualSalary         decimal(10, 2),\n" +
                    "    OverheadMultiPercent decimal(5, 2),\n" +
                    "    AnnualAmount         decimal(10, 2),\n" +
                    "    Country              nvarchar(100),\n" +
                    "    WorkingHours         int,\n" +
                    "    Utilization          decimal(5, 2),\n" +
                    "    isOverheadCost       bit          not null,\n" +
                    "    isActive             bit\n" +
                    ")";

            String sql2 = "SELECT * FROM Employee";

            String sql3 = "INSERT INTO Employee (Employee_Id, Name, AnnualSalary, OverheadMultiPercent, AnnualAmount, Country, WorkingHours, Utilization, isOverheadCost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            if(SQLiteCon != null){
                //DatabaseMetaData meta = con.getMetaData();
                System.out.println("it worked?");
                Statement stmnt = SQLiteCon.createStatement();

                stmnt.execute(sql);

                PreparedStatement ps = DBCon.prepareStatement(sql2);
                ResultSet rs = ps.executeQuery();


                PreparedStatement ps2 = SQLiteCon.prepareStatement(sql3);
                while (rs.next()){
                    ps2.setInt(1, rs.getInt(1));
                    ps2.setString(2, rs.getString(2));
                    ps2.setBigDecimal(3, rs.getBigDecimal(3));
                    ps2.setBigDecimal(4, rs.getBigDecimal(4));
                    ps2.setBigDecimal(5, rs.getBigDecimal(5));
                    ps2.setString(6, rs.getString(6));
                    ps2.setInt(7, rs.getInt(7));
                    ps2.setBigDecimal(8, rs.getBigDecimal(8));
                    ps2.setBoolean(9, rs.getBoolean(9));

                    ps2.executeUpdate();

                }


            }

            SQLiteCon.close();
            DBCon.close();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
