package GUI.model;

import BE.Team;
import BLL.TeamBLL;
import DAL.TeamDAO;
import Exceptions.BBExceptions;

import java.sql.SQLException;
import java.util.List;

public class TeamModel {

    TeamBLL teamBLL = new TeamBLL();


    public List<Team> getAllTeams() throws BBExceptions {
        return teamBLL.getAllTeams();
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

}
