package DAL;

import BE.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {

    private ConnectionManager connectionManager;
    public TeamDAO(){
        ConnectionManager dbConManager;
        try{
            dbConManager = new ConnectionManager(true);
            dbConManager.getConnection().close();
            connectionManager = dbConManager;
        } catch (SQLException e) {
            connectionManager = new ConnectionManager(false);
        }
    }

    public List<Team> getAllTeams() throws SQLException {
        List<Team> allTeams = new ArrayList<Team>();

        try(Connection con = connectionManager.getConnection()){
            String sql = "SELECT * FROM Team";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int id = rs.getInt("Team_Id");
                String name = rs.getString("Team_Name");

                Team team = new Team(id, name);
                allTeams.add(team);
            }
        }

        return allTeams;
    }

    public void newTeam(Team team) throws SQLException {

        try(Connection con = connectionManager.getConnection()){
            String sql = "INSERT INTO Team (Team_Name) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, team.getName());
            ps.executeUpdate();
        }

    }

    // gets last team Id created
    // we need this because when a new team object is made, we need an id for it in java
    // so we can get the last Id + 1
    public int getLastTeamId(){
        int lastId = -1;

        try(Connection con = connectionManager.getConnection()){
            String sql = "SELECT * FROM Team WHERE Team_Id = (SELECT IDENT_CURRENT('Team'))";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                lastId = rs.getInt("Team_Id");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return lastId;
    }


}
