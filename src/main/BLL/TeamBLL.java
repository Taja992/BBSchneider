package BLL;

import BE.Team;
import DAL.TeamDAO;

import java.sql.SQLException;
import java.util.List;

public class TeamBLL {

    TeamDAO DAO = new TeamDAO();


    public List<Team> getAllTeams() throws SQLException {
        return DAO.getAllTeams();
    }

    public void newTeam(Team team) throws SQLException {
        DAO.newTeam(team);
    }

}
