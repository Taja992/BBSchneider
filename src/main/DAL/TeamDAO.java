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


}
