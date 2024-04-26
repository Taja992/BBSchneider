package BLL;

import BE.Team;
import DAL.TeamDAO;

import java.sql.SQLException;
import java.util.List;

public class TeamBLL {

    TeamDAO teamDAO = new TeamDAO();


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

}
