package GUI.model;

import BE.Team;
import BLL.TeamBLL;
import DAL.TeamDAO;
import Exceptions.BBExceptions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class TeamModel {

    TeamBLL teamBLL = new TeamBLL();
    TeamDAO teamDAO = new TeamDAO();
    private ObservableList<Team> allTeams;


// change into oberverable list
public ObservableList<Team> getAllTeams() throws BBExceptions {
    if (allTeams == null) {
        List<Team> teamList = teamBLL.getAllTeams();
        allTeams = FXCollections.observableArrayList(teamList);
    }
    return allTeams;
}

    public void newTeam(Team team) throws BBExceptions {
        teamBLL.newTeam(team);
    }

    public int getLastTeamId() throws BBExceptions {
        return teamBLL.getLastTeamId();
    }

    public Double calculateTotalHourlyRate(int teamId){
        return teamBLL.calculateTotalHourlyRate(teamId);
    }

    public Double calculateTotalDailyRate(int teamId){
        return teamBLL.calculateTotalDailyRate(teamId);
    }


    public void updateTeamName(Team team) throws BBExceptions {
        teamBLL.updateTeamName(team.getEmployeeId(), team.getEmployeeName());
        for (Team t : allTeams) {
            if (t.getEmployeeId() == team.getEmployeeId()) {
                t.setEmployeeName(team.getEmployeeName());
                break;
            }
        }
    }
}
