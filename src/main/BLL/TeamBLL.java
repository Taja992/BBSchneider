package BLL;

import BE.Employee;
import BE.Team;
import DAL.EmployeeDAO;
import DAL.TeamDAO;
import Exceptions.BBExceptions;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class TeamBLL {

    TeamDAO teamDAO = new TeamDAO();
    EmployeeBLL employeeBLL = new EmployeeBLL();
    EmployeeDAO employeeDAO = new EmployeeDAO();

    public List<Team> getAllTeams() throws BBExceptions {
        return teamDAO.getAllTeams();
    }

    public void createNewTeam(Team team) throws BBExceptions {
            teamDAO.createNewTeam(team);
    }

    public int getLastTeamId() throws BBExceptions {
        return teamDAO.getLastTeamId();
    }

    public void updateTeamName(int teamId, String newTeamName) throws BBExceptions {
        teamDAO.updateTeamName(teamId, newTeamName);
    }

    public List<Team> getTeamsForEmployee(int employeeId) throws BBExceptions {
            return teamDAO.getTeamsForEmployee(employeeId);
    }


    ////////////////////////////////////////////////////////
    /////////////////Calculation Logic//////////////////////
    ////////////////////////////////////////////////////////

    public Double calculateTotalHourlyRate(int teamId) throws BBExceptions{
        List<Employee> employees = employeeDAO.getEmployeesWithOverheadStatus(teamId);
        double totalHourlyRate = 0;
        for(Employee employee : employees){
            if (!employee.getTeamOverhead()) {  // Only calculate the hourly rate for non-overhead employees
                double hourlyRate = employeeBLL.calculateHourlyRate(employee);
                totalHourlyRate += hourlyRate;
            }
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(totalHourlyRate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing total hourly rate", e);
        }
    }
    
    public Double calculateTotalDailyRate(int teamId, int hoursPerDay) throws BBExceptions{
        List<Employee> employees = employeeBLL.getAllEmployeesFromTeam(teamId);
        double totalDailyRate = 0;
        for(Employee employee : employees){
            totalDailyRate += employeeBLL.calculateDailyRate(employee, hoursPerDay);
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(totalDailyRate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing total daily rate", e);
        }
    }

    public BigDecimal getTeamUtilForEmployee(int employeeId, int teamId) throws BBExceptions {
        return teamDAO.getTeamUtilForEmployee(employeeId, teamId);
    }
}
