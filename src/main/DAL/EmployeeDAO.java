package DAL;

import BE.Employee;
import Exceptions.BBExceptions;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeeDAO {
    private final ConnectionManager connectionManager;

    public EmployeeDAO(){
        connectionManager = new ConnectionManager();
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
            if (employee.getTeamId() != null) {
                ps.setInt(6, employee.getTeamId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setInt(7, employee.getWorkingHours());
            ps.setBigDecimal(8, employee.getUtilization());
            ps.setBoolean(9, employee.getIsOverheadCost());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new BBExceptions("Error inserting new employee", ex);
        }
    }

}
