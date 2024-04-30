package DAL;

import BE.Employee;
import Exceptions.BBExceptions;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private ConnectionManager connectionManager;

    public EmployeeDAO(){
        ConnectionManager databaseConnectionManager;
        try {
            databaseConnectionManager = new ConnectionManager(true);
            databaseConnectionManager.getConnection().close(); // Need this to test connection and force the SQLException and swap to false
            connectionManager = databaseConnectionManager;
        } catch (SQLException e) {
            connectionManager = new ConnectionManager(false);
        }
    }

    public void newEmployee(Employee employee) throws BBExceptions {
        String sql = "INSERT INTO Employee (Name, AnnualSalary, OverheadMultiPercent, AnnualAmount, Country, Team_Id, WorkingHours, Utilization, isOverheadCost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setBigDecimal(2, employee.getAnnualSalary());
            ps.setBigDecimal(3, employee.getOverheadMultiPercent());
            ps.setBigDecimal(4, employee.getAnnualAmount());
            ps.setString(5, employee.getCountry());
            //Check to see if teams are null or not
            if (employee.getTeamIdEmployee() != null) {
                ps.setInt(6, employee.getTeamIdEmployee());
            } else {
                //We use java.sql.types class INTEGER so SQL knows what type of NULL(Integer) it is
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, employee.getWorkingHours());
            ps.setBigDecimal(8, employee.getUtilization());
            ps.setBoolean(9, employee.getIsOverheadCost());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BBExceptions("Error inserting new employee", e);
        }
    }

    public List<Employee> getAllEmployees() throws BBExceptions {
        List<Employee> employees = new ArrayList<>();

        //I joined the Team and Employee table using a LEFT JOIN(So it would show employees even if team was null)
        //also we use AS to change the Employee.Team_Id name to Employee_Team_Id so I am able to hold both Ids in this list
        //I did it this way to make assigning teams on a dropdown menu on Tableview more simple
        String sql = "SELECT Employee.*, Employee.Team_Id AS Employee_Team_Id, Team.Team_Id, Team.Team_Name FROM Employee LEFT JOIN Team ON Employee.Team_Id = Team.Team_Id";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("Employee_Id"));
                employee.setName(rs.getString("Name"));
                employee.setAnnualSalary(rs.getBigDecimal("AnnualSalary"));
                employee.setOverheadMultiPercent(rs.getBigDecimal("OverheadMultiPercent"));
                employee.setAnnualAmount(rs.getBigDecimal("AnnualAmount"));
                employee.setCountry(rs.getString("Country"));
                employee.setTeamIdEmployee(rs.getInt("Employee_Team_Id"));
                employee.setTeamId(rs.getInt("Team_Id"));
                employee.setTeamName(rs.getString("Team_Name"));
                employee.setWorkingHours(rs.getInt("WorkingHours"));
                employee.setUtilization(rs.getBigDecimal("Utilization"));
                employee.setIsOverheadCost(rs.getBoolean("isOverheadCost"));

                employees.add(employee);
            }
        } catch (SQLException e) {
            throw new BBExceptions("Error retrieving all employees", e);
        }

        return employees;
    }


    public List<Employee> getAllEmployeesFromTeam(int TeamId) throws SQLException {
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM Employee WHERE Team_Id = ?";

        try(Connection con = connectionManager.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, TeamId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Employee employee = new Employee();
                employee.setId(rs.getInt("Employee_Id"));
                employee.setName(rs.getString("Name"));
                employee.setAnnualSalary(rs.getBigDecimal("AnnualSalary"));
                employee.setOverheadMultiPercent(rs.getBigDecimal("OverheadMultiPercent"));
                employee.setAnnualAmount(rs.getBigDecimal("AnnualAmount"));
                employee.setCountry(rs.getString("Country"));
                employee.setTeamIdEmployee(rs.getInt("Team_Id"));
                employee.setWorkingHours(rs.getInt("WorkingHours"));
                employee.setUtilization(rs.getBigDecimal("Utilization"));
                employee.setIsOverheadCost(rs.getBoolean("isOverheadCost"));

                employees.add(employee);
            }

        } catch (SQLException e){
            throw new BBExceptions("Error retrieving all employees from team with ID " + TeamId, e);
        }


        return employees;
    }

    public List<Employee> getAllEmployeesFromLocation(String Location) throws SQLException {
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM Employee WHERE Country = ?";

        try(Connection con = connectionManager.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, Location);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Employee employee = new Employee();
                employee.setId(rs.getInt("Employee_Id"));
                employee.setName(rs.getString("Name"));
                employee.setAnnualSalary(rs.getBigDecimal("AnnualSalary"));
                employee.setOverheadMultiPercent(rs.getBigDecimal("OverheadMultiPercent"));
                employee.setAnnualAmount(rs.getBigDecimal("AnnualAmount"));
                employee.setCountry(rs.getString("Country"));
                employee.setTeamIdEmployee(rs.getInt("Team_Id"));
                employee.setWorkingHours(rs.getInt("WorkingHours"));
                employee.setUtilization(rs.getBigDecimal("Utilization"));
                employee.setIsOverheadCost(rs.getBoolean("isOverheadCost"));

                employees.add(employee);
            }

        } catch (SQLException e){
            throw new BBExceptions("Error retrieving all employees from team from location " + Location, e);
        }


        return employees;
    }

    public void updateEmployee(Employee employee) throws BBExceptions {
        String sql = "UPDATE Employee SET Name = ?, AnnualSalary = ?, OverheadMultiPercent = ?, AnnualAmount = ?, Country = ?, Team_Id = ?, WorkingHours = ?, Utilization = ?, isOverheadCost = ? WHERE Employee_Id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setBigDecimal(2, employee.getAnnualSalary());
            ps.setBigDecimal(3, employee.getOverheadMultiPercent());
            ps.setBigDecimal(4, employee.getAnnualAmount());
            ps.setString(5, employee.getCountry());
            //Check to see if teams are null or not
            if (employee.getTeamIdEmployee() != null) {
                ps.setInt(6, employee.getTeamIdEmployee());
            } else {
                //We use java.sql.types class INTEGER so SQL knows what type of NULL(Integer) it is
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, employee.getWorkingHours());
            ps.setBigDecimal(8, employee.getUtilization());
            ps.setBoolean(9, employee.getIsOverheadCost());
            ps.setInt(10, employee.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BBExceptions("Error updating employee", e);
        }
    }

}
