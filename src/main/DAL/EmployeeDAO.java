package DAL;

import BE.Employee;
import BE.Team;
import Exceptions.BBExceptions;

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
        String sql = "INSERT INTO Employee (Name, AnnualSalary, OverheadMultiPercent, AnnualAmount, Country, WorkingHours, Utilization, isOverheadCost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
        //use a hash map instead of a list to avoid duplicate employees
        Map<Integer, Employee> hashMapEmployees = new HashMap<>();

        String sql = "SELECT employee.*, team.*, connection.Team_Util, connection.TeamIsOverhead FROM Employee employee " +
                "LEFT JOIN Connection connection ON employee.Employee_Id = connection.Emp_Id " +
                "LEFT JOIN Team team ON connection.Team_Id = team.Team_Id";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                //first we get our employee ID from the result set
                int employeeId = rs.getInt("Employee_Id");
                //get an employee object from our hashmap to check the Id to see if that employee already exists
                Employee employee = hashMapEmployees.get(employeeId);
                //if it does not exist, we do our normal result set
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
                    employee.setTeamIsOverhead(rs.getBoolean("TeamIsOverhead"));
                    employee.setIsOverheadCost(rs.getBoolean("isOverheadCost"));
                    //we add the new employee into our hashmap
                    hashMapEmployees.put(employeeId, employee);
                }
                //Because we use LEFT JOIN if an employee doesnt have a team it returns NULL
                //So we add a check so a team isnt created if the Team_ID is null
                int teamId = rs.getInt("Team_Id");
                if (teamId > 0) {
                    Team team = new Team();
                    team.setId(teamId);
                    team.setName(rs.getString("Team_Name"));
                    //we use our employee objects .getTeams method to create or return the team an employee is on
                    //.add(team) adds the Team object to the list of teams the employee is a part of
                    employee.getTeams().add(team);
                }
            }
        } catch (SQLException e) {
            throw new BBExceptions("Error retrieving all employees", e);
        }
        //we make a new array list and populate it with our hashmap
        return new ArrayList<>(hashMapEmployees.values());
    }


    public List<Employee> getAllEmployeesFromTeam(int TeamId) throws BBExceptions {
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT Employee.*, Connection.Team_Util FROM Employee" +
                " INNER JOIN Connection ON Employee.Employee_Id = Connection.Emp_Id" +
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
                employee.setTeamUtil(rs.getBigDecimal("Team_Util")); // Set the utilization from the Connection table
                employee.setIsOverheadCost(rs.getBoolean("isOverheadCost"));
                employees.add(employee);
            }

        } catch (SQLException e){
            throw new BBExceptions("Error retrieving all employees from team with ID " + TeamId, e);
        }

        return employees;
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
            throw new BBExceptions("Error adding employee to team", e);
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

    public BigDecimal calculateTotalTeamUtilization(int employeeId) throws BBExceptions {
        BigDecimal totalUtilization = BigDecimal.ZERO;
        String sql = "SELECT Team_Util FROM Connection WHERE Emp_Id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                totalUtilization = totalUtilization.add(rs.getBigDecimal("Team_Util"));
            }
        } catch (SQLException e) {
            throw new BBExceptions("Error calculating total team utilization for employee with ID " + employeeId, e);
        }

        return totalUtilization;
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

    public BigDecimal getUtilizationForTeam(Employee employee, Team team) throws BBExceptions {
        String sql = "SELECT Team_Util FROM Connection WHERE Emp_Id = ? AND Team_Id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, employee.getId());
            ps.setInt(2, team.getId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("Team_Util");
            } else {
                throw new BBExceptions("No utilization found for employee with ID " + employee.getId() + " in team with ID " + team.getId());
            }
        } catch (SQLException e) {
            throw new BBExceptions("Error retrieving utilization for employee in team", e);
        }
    }
}
