package BLL;

import BE.Employee;
import BE.Team;
import DAL.TeamDAO;
import Exceptions.BBExceptions;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class TeamBLL {

    TeamDAO teamDAO = new TeamDAO();
    EmployeeBLL employeeBLL = new EmployeeBLL();

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
}
