package DAL;

import org.sqlite.core.DB;

import java.io.File;
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
    }

    public void createNewSnapshotFile(String fileName){
        String filepath = folderPath + fileName + ".db";

        try {
            Connection SQLiteCon = DriverManager.getConnection(filepath);
            //this creates SQLite database and establishes connection at the same time

            Connection DBCon = connectionManager.getConnection();
            //this gets the connection to our regular database (the one we're copying)

            //duplicating all the tables and adding them onto the snapshot DB
            duplicateEmployeeTable(SQLiteCon, DBCon);
            duplicateTeamTable(SQLiteCon, DBCon);
            duplicateConnectionTable(SQLiteCon, DBCon);

            //closing connections just in case
            SQLiteCon.close();
            DBCon.close();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void duplicateEmployeeTable(Connection SQLiteCon, Connection DBCon) throws SQLException {

        String sql = "CREATE TABLE IF NOT EXISTS Employee(\n" +
                "    Employee_Id          INTEGER PRIMARY KEY,\n" +
                "    Name                 nvarchar(50) not null,\n" +
                "    AnnualSalary         decimal(10, 2),\n" +
                "    OverheadMultiPercent decimal(5, 2),\n" +
                "    AnnualAmount         decimal(10, 2),\n" +
                "    Country              nvarchar(100),\n" +
                "    WorkingHours         int,\n" +
                "    Utilization          decimal(5, 2),\n" +
                "    isOverheadCost       bit          not null,\n" +
                "    isActive             bit)"; //"identity" removed so Id's are the same

        String sql2 = "SELECT * FROM Employee";

        String sql3 = "INSERT INTO Employee (Employee_Id, Name, AnnualSalary, OverheadMultiPercent, " +
                "AnnualAmount, Country, WorkingHours, Utilization, isOverheadCost) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        if(SQLiteCon != null){
            Statement stmnt = SQLiteCon.createStatement();
            stmnt.execute(sql); //executing first sql statement on snapshot db

            PreparedStatement ps = DBCon.prepareStatement(sql2); //get all employees from original db
            ResultSet rs = ps.executeQuery();


            PreparedStatement ps2 = SQLiteCon.prepareStatement(sql3); //get ready to insert new employee into snapshot db
            while (rs.next()){
                //taking values from original db and putting them into snapshot db
                ps2.setInt(1, rs.getInt(1));
                ps2.setString(2, rs.getString(2));
                ps2.setBigDecimal(3, rs.getBigDecimal(3));
                ps2.setBigDecimal(4, rs.getBigDecimal(4));
                ps2.setBigDecimal(5, rs.getBigDecimal(5));
                ps2.setString(6, rs.getString(6));
                ps2.setInt(7, rs.getInt(7));
                ps2.setBigDecimal(8, rs.getBigDecimal(8));
                ps2.setBoolean(9, rs.getBoolean(9));

                ps2.executeUpdate(); //putting values into statement

            }


        }

    }//end of method

    private void duplicateTeamTable(Connection SQLiteCon, Connection DBCon) throws SQLException {

        String sql = "create table Team(\n" +
                "    Team_Id   int \n" +
                "        primary key,\n" +
                "    Team_Name nvarchar(150) not null \n" +
                "        constraint UC_Team\n" +
                "            unique)";

        String sql2 = "SELECT * FROM Team";

        String sql3 = "INSERT INTO Team (Team_Id, Team_Name) VALUES (?, ?)";

        if(SQLiteCon != null){
            Statement stmnt = SQLiteCon.createStatement();
            stmnt.execute(sql); //executing first sql statement on snapshot db

            PreparedStatement ps = DBCon.prepareStatement(sql2); //get all employees from original db
            ResultSet rs = ps.executeQuery();


            PreparedStatement ps2 = SQLiteCon.prepareStatement(sql3); //get ready to insert new employee into snapshot db
            while (rs.next()){
                //taking values from original db and putting them into snapshot db
                ps2.setInt(1, rs.getInt(1));
                ps2.setString(2, rs.getString(2));

                ps2.executeUpdate(); //putting values into statement

            }


        }
    }//end of method

    private void duplicateConnectionTable(Connection SQLiteCon, Connection DBCon) throws SQLException {

        String sql = "create table Connection(\n" +
                "    Con_Id     int \n" +
                "        constraint Connection_pk\n" +
                "            primary key,\n" +
                "    Emp_Id     int     not null\n" +
                "        constraint Connection_Employee_Employee_Id_fk\n" +
                "            references Employee,\n" +
                "    Team_Id    int     not null\n" +
                "        constraint Connection_Team_Team_Id_fk\n" +
                "            references Team,\n" +
                "    Team_Util  decimal not null,\n" +
                "    TeamIsOverhead bit     not null)";

        String sql2 = "SELECT * FROM Connection";

        String sql3 = "INSERT INTO Connection (Con_Id, Emp_Id, Team_Id, Team_Util, TeamIsOverhead) VALUES (?, ?, ?, ?, ?)";

        if(SQLiteCon != null){
            Statement stmnt = SQLiteCon.createStatement();
            stmnt.execute(sql); //executing first sql statement on snapshot db

            PreparedStatement ps = DBCon.prepareStatement(sql2); //get all employees from original db
            ResultSet rs = ps.executeQuery();


            PreparedStatement ps2 = SQLiteCon.prepareStatement(sql3); //get ready to insert new employee into snapshot db
            while (rs.next()){
                //taking values from original db and putting them into snapshot db
                ps2.setInt(1, rs.getInt(1));
                ps2.setInt(2, rs.getInt(2));
                ps2.setInt(3, rs.getInt(3));
                ps2.setBigDecimal(4, rs.getBigDecimal(4));
                ps2.setBoolean(5, rs.getBoolean(5));

                ps2.executeUpdate(); //putting values into statement

            }


        }
    }//end of method


    public boolean doesFileExist(String fileName){
        File file = new File(fileName);

        return file.exists();
    }

}
