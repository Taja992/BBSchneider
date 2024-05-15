package BLL;

import BE.Employee;
import BE.Team;
import DAL.EmployeeDAO;
import DAL.TeamDAO;
import Exceptions.BBExceptions;
import javafx.scene.control.Tab;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import static BE.Employee.employeeId;

public class TeamBLL {

    TeamDAO teamDAO = new TeamDAO();
    EmployeeBLL employeeBLL = new EmployeeBLL();

    public List<Team> getAllTeams() throws BBExceptions {
        return teamDAO.getAllTeams();
    }

    public void newTeam(Team team) {
        try {
            teamDAO.newTeam(team);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getLastTeamId() throws BBExceptions {
        return teamDAO.getLastTeamId();
    }

    public Double calculateTotalHourlyRate(int teamId){
        List<Employee> employees = employeeBLL.getAllEmployeesFromTeam(teamId);
        double totalHourlyRate = 0;
        for(Employee employee : employees){
            totalHourlyRate += employeeBLL.calculateHourlyRate(employee);
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(totalHourlyRate)).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Double calculateTotalDailyRate(int teamId){
        List<Employee> employees = employeeBLL.getAllEmployeesFromTeam(teamId);
        double totalDailyRate = 0;
        for(Employee employee : employees){
            totalDailyRate += employeeBLL.calculateDailyRate(employee);
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(totalDailyRate)).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTeamName(int teamId, String newTeamName) throws BBExceptions {
        teamDAO.updateTeamName(teamId, newTeamName);
    }

    public List<Team> getTeamsForEmployee(int employeeId) {
        try {
            return teamDAO.getTeamsForEmployee(employeeId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeEmployeeFromTeam(int employeeId, int teamId) throws SQLException {
        EmployeeDAO.removeEmployeeFromTeam(employeeId, teamId);

    }
}
