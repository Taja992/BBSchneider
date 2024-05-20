package GUI.model;

import BE.Employee;
import BE.Team;
import BLL.SnapshotBLL;
import Exceptions.BBExceptions;

import java.util.List;
import java.util.Map;

public class SnapshotModel {

    private SnapshotBLL snapBLL = new SnapshotBLL();

    public String createSnapshotFile(String fileName){
        return snapBLL.createSnapshotFile(fileName);
    }
    public Map<String, String> getAllSnapshotNames(){
        return snapBLL.getAllSnapshotNames();
    }

    public List<Team> getAllTeamsInSnapshot(String fileName) throws BBExceptions {
        return snapBLL.getAllTeamsInSnapshot(fileName);
    }

    public List<Employee> getAllEmployeesFromTeam(int teamId, String filename) throws BBExceptions {
        return snapBLL.getAllEmployeesFromTeam(teamId, filename);
    }

}
