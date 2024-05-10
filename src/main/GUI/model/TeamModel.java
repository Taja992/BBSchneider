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


    public ObservableList<Team> getAllTeams() {
        try {
            List<Team> teamList = teamBLL.getAllTeams();
            return FXCollections.observableArrayList(teamList);
        } catch (BBExceptions e) {
            throw new RuntimeException("Error getting all teams", e);
        }
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

    public void updateTeamName(int teamId, String newTeamName) throws BBExceptions {
        teamBLL.updateTeamName(teamId, newTeamName);
    }

    public List<Team> getTeamsForEmployee(int employeeId) throws BBExceptions {
        return teamBLL.getTeamsForEmployee(employeeId);
    }

}
