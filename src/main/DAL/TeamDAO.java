package DAL;

import BE.Team;
import Exceptions.BBExceptions;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {

    private ConnectionManager connectionManager;
    public TeamDAO(){
        connectionManager = new ConnectionManager();
    }

    public List<Team> getAllTeams() throws BBExceptions {
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
        } catch (SQLException e) {
            throw new BBExceptions("Error getting all teams", e);
        }

        return allTeams;
    }


    public void createNewTeam(Team team) throws BBExceptions {

        try(Connection con = connectionManager.getConnection()){
            String sql = "INSERT INTO Team (Team_Name) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, team.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BBExceptions("Error inserting new team", e);
        }

    }

    // gets last team Id created
    // we need this because when a new team object is made, we need an id for it in java
    // so we can get the last Id + 1
    public int getLastTeamId() throws BBExceptions {
        int lastId = -1;

        try(Connection con = connectionManager.getConnection()){
            String sql = "SELECT IDENT_CURRENT('Team')";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                lastId = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new BBExceptions("Error getting last team ID", e);
        }

        return lastId;
    }


    public void updateTeamName(int teamId, String newTeamName) throws BBExceptions {
        try(Connection con = connectionManager.getConnection()){
            String sql = "UPDATE Team SET Team_Name = ? WHERE Team_Id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, newTeamName);
            ps.setInt(2, teamId);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e instanceof SQLServerException && e.getMessage().contains("Violation of UNIQUE KEY constraint")) {
                throw new BBExceptions("The team name '" + newTeamName + "' already exists. Please choose a different name.", e);
            } else {
                throw new BBExceptions("Error updating team name", e);
            }
        }
    }

   }
