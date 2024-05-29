package BLL;

import BE.Employee;
import BE.Team;
import DAL.SnapshotDAO;
import Exceptions.BBExceptions;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SnapshotBLL {

    private SnapshotDAO snapDAO = new SnapshotDAO();


    public String createSnapshotFile(String fileName){
        int copyNum = 2;
        String finalFileName = fileName;

        while(doesFileExist(finalFileName + ".db")){
            finalFileName = fileName + " (" + copyNum + ")";
            copyNum++;
        }

        snapDAO.createNewSnapshotFile(finalFileName);
        return finalFileName;
        //since the name can have a number at the end,
        //return the final name so that the tab in the UI can have a proper name
    }

    public Map<String, String> getAllSnapshotNames(){

        //this Map is so that we can have a clean display name
        //on the tab but can still reference the original file
        Map<String, String> snapshotMap = new HashMap<>();
        List<String> snapNames = snapDAO.getAllSnapshotNames();
        for(String name : snapNames){
            //creating the display name (which will just be the date)
            String displayName = name.substring(12, name.lastIndexOf('.'));
            displayName = displayName.replace("-", "/");

            snapshotMap.put(displayName, name);
            //putting in the display name gets you the original file, which will be used to retrieve data
        }

        return snapshotMap;
    }

    public List<Employee> getAllEmployeesFromTeam(int teamId, String filename) throws BBExceptions {
        return snapDAO.getAllEmployeesFromTeam(teamId, filename);
    }

    public List<Team> getAllTeamsInSnapshot(String fileName) throws BBExceptions {
        return snapDAO.getAllTeamsInSnapshot(fileName);
    }


    public boolean doesFileExist(String fileName){
        File file = new File("src/resources/Snapshots/" + fileName);

        return file.getAbsoluteFile().exists();
    }

}
