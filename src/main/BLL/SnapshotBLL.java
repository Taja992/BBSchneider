package BLL;

import BE.Employee;
import BE.Team;
import DAL.SnapshotDAO;
import Exceptions.BBExceptions;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SnapshotBLL {

    private SnapshotDAO snapDAO = new SnapshotDAO();
    private EmployeeBLL employeeBLL = new EmployeeBLL();

    public void createSnapshotFile(String fileName){
        int copyNum = 2;
        String finalFileName = fileName;

        while(snapDAO.doesFileExist(finalFileName + ".db")){
            finalFileName = fileName + " (" + copyNum + ")";
            copyNum++;
        }

        snapDAO.createNewSnapshotFile(finalFileName);
    }

    public Map<String, String> getAllSnapshotNames(){

        //this Map is so that we can have a clean display name on the tab but can still reference the original file
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

    public Double calculateTotalHourlyRate(int teamId, String fileName) throws BBExceptions{
        List<Employee> employees = snapDAO.getAllEmployeesFromTeam(teamId, fileName);
        double totalDailyRate = 0;
        for(Employee employee : employees){
            totalDailyRate += employeeBLL.calculateHourlyRate(employee);
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(totalDailyRate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing total daily rate for team with ID '"
                    + teamId + "' from snapshot '" + fileName + "' ", e);
        }
    }

    public Double calculateTotalDailyRate(int teamId, String fileName) throws BBExceptions{
        List<Employee> employees = snapDAO.getAllEmployeesFromTeam(teamId, fileName);
        double totalDailyRate = 0;
        for(Employee employee : employees){
            totalDailyRate += employeeBLL.calculateDailyRate(employee);
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(totalDailyRate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing total daily rate for team with ID '"
                    + teamId + "' from snapshot '" + fileName + "' ", e);
        }
    }

}
