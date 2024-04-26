package GUI.model;

import BE.Team;
import BLL.TeamBLL;
import DAL.TeamDAO;

import java.sql.SQLException;
import java.util.List;

public class TeamModel {

    TeamBLL teamBLL = new TeamBLL();


    public List<Team> getAllTeams() throws SQLException {
        return teamBLL.getAllTeams();
    }

    public void newTeam(Team team){
        teamBLL.newTeam(team);
    }

    public int getLastTeamId(){
        return teamBLL.getLastTeamId();
    }

}
