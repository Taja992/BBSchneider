package DAL;

import BE.Employee;
import Exceptions.BBExceptions;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class
EmployeeDAO {
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

    //Our New employee method returns the generated key by our database so we are able to edit the newly created employees
    public int newEmployee(Employee employee) throws BBExceptions {
        String sql = "INSERT INTO Employee (Name, AnnualSalary, OverheadMultiPercent, AnnualAmount, Country, WorkingHours, Utilization, isOverheadCost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, employee.getName());
            ps.setBigDecimal(2, employee.getAnnualSalary());
            ps.setBigDecimal(3, employee.getOverheadMultiPercent());
            ps.setBigDecimal(4, employee.getAnnualAmount());
            ps.setString(5, employee.getCountry());
            ps.setInt(6, employee.getWorkingHours());
            ps.setBigDecimal(7, employee.getUtilization());
            ps.setBoolean(8, employee.getIsOverheadCost());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new BBExceptions("Creating employee failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new BBExceptions("Error inserting new employee", e);
        }
    }

    public List<Employee> getAllEmployees() throws BBExceptions {
        List<Employee> employees = new ArrayList<>();


        String sql = "SELECT * FROM Employee";

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


    public List<Employee> getAllEmployeesFromTeam(int TeamId) throws BBExceptions {
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM Employee" +
                "         INNER JOIN Connection ON Employee.Employee_Id = Connection.Emp_Id" +
                " WHERE Team_Id = ?";

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

    public void updateEmployee(Employee employee) throws BBExceptions {
        String sql = "UPDATE Employee SET Name = ?, AnnualSalary = ?, OverheadMultiPercent = ?, AnnualAmount = ?, Country = ?, WorkingHours = ?, Utilization = ?, isOverheadCost = ? WHERE Employee_Id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setBigDecimal(2, employee.getAnnualSalary());
            ps.setBigDecimal(3, employee.getOverheadMultiPercent());
            ps.setBigDecimal(4, employee.getAnnualAmount());
            ps.setString(5, employee.getCountry());
            ps.setInt(6, employee.getWorkingHours());
            ps.setBigDecimal(7, employee.getUtilization());
            ps.setBoolean(8, employee.getIsOverheadCost());
            ps.setInt(9, employee.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BBExceptions("Error updating employee", e);
        }
    }

}
