package DAL;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class
EmployeeDAO {
    private ConnectionManager connectionManager;

    public EmployeeDAO(){
        connectionManager = new ConnectionManager();
    }

    //Our New employee method returns the generated key by our database so we are able to edit the newly created employees
    public int newEmployee(Employee employee) throws BBExceptions {
        String sql = "INSERT INTO Employee (Name, AnnualSalary, OverheadMultiPercent, AnnualAmount, Country, WorkingHours, Utilization) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, employee.getName());
            ps.setBigDecimal(2, employee.getAnnualSalary());
            ps.setBigDecimal(3, employee.getOverheadMultiPercent());
            ps.setBigDecimal(4, employee.getAnnualAmount());
            ps.setString(5, employee.getCountry());
            ps.setInt(6, employee.getWorkingHours());
            ps.setBigDecimal(7, employee.getUtilization());


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
        //Using a map instead of a list to store Employees because
        //They will be on teams so we do this to avoid duplicates
        Map<Integer, Employee> hashMapEmployees = new HashMap<>();

        //Sql query connecting our tables
        String sql = "SELECT employee.*, team.*, connection.Team_Util, connection.TeamIsOverhead FROM Employee employee " +
                "LEFT JOIN Connection connection ON employee.Employee_Id = connection.Emp_Id " +
                "LEFT JOIN Team team ON connection.Team_Id = team.Team_Id";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // I put the result set in a different method for organization
                Employee employee = getEmployeeFromResultSet(rs, hashMapEmployees);
                // add the team from the current row to the employee
                addTeamToEmployee(rs, employee);
            }
        } catch (SQLException e) {
            throw new BBExceptions("Error retrieving all employees", e);
        }
        //use our hashmap to return an arraylist without duplicates
        return new ArrayList<>(hashMapEmployees.values());
    }

    private Employee getEmployeeFromResultSet(ResultSet rs, Map<Integer, Employee> hashMapEmployees) throws SQLException {
        //get the employee ID to check if its on our hashmap already or not
        //it would already be in the hashmap if its on multiple teams
        int employeeId = rs.getInt("Employee_Id");
        Employee employee = hashMapEmployees.get(employeeId);
        //if its not on hashmap, add it
        if (employee == null) {
            employee = new Employee();
            employee.setId(employeeId);
            employee.setName(rs.getString("Name"));
            employee.setAnnualSalary(rs.getBigDecimal("AnnualSalary"));
            employee.setOverheadMultiPercent(rs.getBigDecimal("OverheadMultiPercent"));
            employee.setAnnualAmount(rs.getBigDecimal("AnnualAmount"));
            employee.setCountry(rs.getString("Country"));
            employee.setWorkingHours(rs.getInt("WorkingHours"));
            employee.setUtilization(rs.getBigDecimal("Utilization"));
            employee.setTeamUtil(rs.getBigDecimal("Team_Util"));
            employee.setTeamOverhead(rs.getBoolean("TeamIsOverhead"));


            hashMapEmployees.put(employeeId, employee);
        }

        return employee;
    }

    private void addTeamToEmployee(ResultSet rs, Employee employee) throws SQLException {
        int teamId = rs.getInt("Team_Id");
        if (teamId > 0) {
            Team team = new Team();
            team.setId(teamId);
            team.setName(rs.getString("Team_Name"));
            //add the team to the employee's team list
            employee.getTeams().add(team);
        }
    }


    public void addEmployeeToTeam(int employeeId, int teamId) throws BBExceptions {
        String sql = "INSERT INTO Connection (Emp_Id, Team_Id, Team_Util, TeamIsOverhead) VALUES (?, ?, ?, ?)";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            pstmt.setInt(2, teamId);
            pstmt.setBigDecimal(3, new BigDecimal("0.00")); // Set Team_Util to 0.00 initially
            pstmt.setBoolean(4, false); // Set isOverhead to false initially

            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e instanceof SQLServerException && e.getMessage().contains("Violation of UNIQUE KEY constraint")) {
                throw new BBExceptions("The employee is already on the team.", e);
            } else {
                throw new BBExceptions("Error adding employee to team", e);
            }
        }
    }


    public void removeEmployeeFromTeam(int employeeId, int teamId) throws BBExceptions {
        String sql = "DELETE FROM Connection WHERE Emp_Id = ? AND Team_Id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, teamId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BBExceptions("Error removing employee from team", e);
        }
    }





    public void updateTeamUtilForEmployee(int teamId, int employeeId, BigDecimal newUtil) throws BBExceptions {
        String sql = "UPDATE Connection SET Team_Util = ? WHERE Emp_Id = ? AND Team_Id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBigDecimal(1, newUtil);
            ps.setInt(2, employeeId);
            ps.setInt(3, teamId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BBExceptions("Error updating team utilization for employee with ID " + employeeId + " in team with ID " + teamId, e);
        }
    }

    public void updateTeamIsOverheadForEmployee(int teamId, int employeeId, boolean isOverhead) throws BBExceptions {
        String sql = "UPDATE Connection SET TeamIsOverhead = ? WHERE Emp_Id = ? AND Team_Id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, isOverhead);
            ps.setInt(2, employeeId);
            ps.setInt(3, teamId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BBExceptions("Error updating TeamIsOverhead for employee with ID " + employeeId + " in team with ID " + teamId, e);
        }
    }


    public void updateEmployee(Employee employee) throws BBExceptions {
        String sql = "UPDATE Employee SET Name = ?, AnnualSalary = ?, OverheadMultiPercent = ?, AnnualAmount = ?, Country = ?, WorkingHours = ?, Utilization = ? WHERE Employee_Id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setBigDecimal(2, employee.getAnnualSalary());
            ps.setBigDecimal(3, employee.getOverheadMultiPercent());
            ps.setBigDecimal(4, employee.getAnnualAmount());
            ps.setString(5, employee.getCountry());
            ps.setInt(6, employee.getWorkingHours());
            ps.setBigDecimal(7, employee.getUtilization());
            ps.setInt(8, employee.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BBExceptions("Error updating employee", e);
        }
    }

    public BigDecimal getUtilizationForTeam(int employeeId, int teamId) throws BBExceptions {
        String sql = "SELECT Team_Util FROM Connection WHERE Emp_Id = ? AND Team_Id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setInt(2, teamId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("Team_Util");
            } else {
                return BigDecimal.ZERO;
//                throw new BBExceptions("No utilization found for employee with ID " + employeeId + " in team with ID " + teamId);
            }
        } catch (SQLException e) {
            throw new BBExceptions("Error retrieving utilization for employee in team", e);
        }
    }

    public List<Employee> getEmployeesWithOverheadStatus(int teamId) throws BBExceptions {
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT E.*, C.TeamIsOverhead FROM Employee E " +
                "INNER JOIN Connection C ON E.Employee_Id = C.Emp_Id " +
                "WHERE C.Team_Id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, teamId);

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
                employee.setTeamOverhead(rs.getBoolean("TeamIsOverhead"));

                employees.add(employee);
            }
        } catch (SQLException e) {
            throw new BBExceptions("Error retrieving employees for team with ID " + teamId, e);
        }

        return employees;
    }
}
