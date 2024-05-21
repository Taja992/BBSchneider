package GUI.model;

import BE.Team;
import BLL.TeamBLL;
import Exceptions.BBExceptions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamModel {

    TeamBLL teamBLL = new TeamBLL();
    private Map<String, BigDecimal> teamUtilCache = new HashMap<>();

    public ObservableList<Team> getAllTeams() {
        try {
            List<Team> teamList = teamBLL.getAllTeams();
            return FXCollections.observableArrayList(teamList);
        } catch (BBExceptions e) {
            throw new RuntimeException("Error getting all teams", e);
        }
    }

    public void createNewTeam(Team team) throws BBExceptions {
        teamBLL.createNewTeam(team);
    }

    public int getLastTeamId() throws BBExceptions {
        return teamBLL.getLastTeamId();
    }

    public Double calculateTotalHourlyRate(int teamId) throws BBExceptions{
        return teamBLL.calculateTotalHourlyRate(teamId);
    }

    public Double calculateTotalDailyRate(int teamId, int hoursPerDay) throws BBExceptions{
        return teamBLL.calculateTotalDailyRate(teamId, hoursPerDay);
    }

    public void updateTeamName(int teamId, String newTeamName) throws BBExceptions {
        teamBLL.updateTeamName(teamId, newTeamName);
    }

    public BigDecimal getTeamUtilForEmployee(int employeeId, int teamId) throws BBExceptions {
        String key = employeeId + "-" + teamId;
        if (teamUtilCache.containsKey(key)) {
            return teamUtilCache.get(key);
        } else {
            BigDecimal teamUtil = teamBLL.getTeamUtilForEmployee(employeeId, teamId);
            teamUtilCache.put(key, teamUtil);
            return teamUtil;
        }
    }

    public void invalidateCacheForEmployeeAndTeam(int employeeId, int teamId) {
        String key = employeeId + "-" + teamId;
        teamUtilCache.remove(key);
    }
}
