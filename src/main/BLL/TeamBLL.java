package BLL;

import BE.Employee;
import BE.Team;
import DAL.TeamDAO;

import java.sql.SQLException;
import java.util.List;

public class TeamBLL {

    TeamDAO teamDAO = new TeamDAO();
    EmployeeBLL employeeBLL = new EmployeeBLL();

    public List<Team> getAllTeams() throws SQLException {
        return teamDAO.getAllTeams();
    }

    public void newTeam(Team team) {
        try {
            teamDAO.newTeam(team);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getLastTeamId(){
        return teamDAO.getLastTeamId();
    }

    public Double calculateTotalHourlyRate(int teamId){
        List<Employee> employees = employeeBLL.getAllEmployeesFromTeam(teamId);
        double totalHourlyRate = 0;
        for(Employee employee : employees){
            totalHourlyRate += employeeBLL.calculateHourlyRate(employee);
        }
        return totalHourlyRate;
    }

    public Double calculateTotalDailyRate(int teamId){
        List<Employee> employees = employeeBLL.getAllEmployeesFromTeam(teamId);
        double totalDailyRate = 0;
        for(Employee employee : employees){
            totalDailyRate += employeeBLL.calculateDailyRate(employee);
        }
        return totalDailyRate;
    }

}
