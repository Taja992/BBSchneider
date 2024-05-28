package GUI.model;

import BE.Employee;
import BE.Team;
import BLL.EmployeeBLL;
import Exceptions.BBExceptions;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EmployeeModel {
    private final EmployeeBLL employeeBLL;
    private final BooleanProperty countryAdded = new SimpleBooleanProperty(false);
    private final List<String> allCountries = FXCollections.observableArrayList();
    private final ObservableList<Employee> allEmployees;
    private final FilteredList<Employee> filteredEmployees;
    private Map<Integer, BigDecimal> teamUtilSumCache = new HashMap<>();
    private Map<String, BigDecimal> teamUtilCache = new HashMap<>();

    public EmployeeModel(){
        employeeBLL = new EmployeeBLL();
        allEmployees = FXCollections.observableArrayList();
        filteredEmployees = new FilteredList<>(allEmployees);
        teamViewListener();
    }


    public static int setWorkingHours(int newWorkingHours) {
        return EmployeeBLL.setWorkingHours(newWorkingHours);
    }

    public static int getWorkingHours() {
        return EmployeeBLL.getWorkingHours();
    }


    public ObservableList<Employee> getEmployees() throws BBExceptions {
        if(allEmployees.isEmpty()) {
            //populate our list from database
            List<Employee> fetchedEmployees = employeeBLL.getAllEmployees();
            allEmployees.addAll(fetchedEmployees);
        }
        //return our observable list
        return allEmployees;
    }



    public ObservableList<Employee> getAllEmployeesFromTeam(int TeamId) {
        // Create a filtered view of the allEmployees list
        //the lambda below works like this: Predicate: employee -> employee.getTeams().contains(TeamId) return
        FilteredList<Employee> teamEmployees = new FilteredList<>(allEmployees, employee -> {
            for (Team team : employee.getTeams()) {
                //returns true if the employee has the given teamID
                if (team.getId() == TeamId) {
                    return true;
                }
            }
            return false;
        });
        return teamEmployees;
    }

    //this listener lets the team coloumn on Overview Table reflect if someone has been removed from a team
    public void teamViewListener() {
        filteredEmployees.addListener((ListChangeListener<Employee>) listChange -> {
            while (listChange.next()) {
                if (listChange.wasUpdated()) {
                    // For each updated employee in the filtered list, update the same employee in the allEmployees list
                    for (int i = listChange.getFrom(); i < listChange.getTo(); ++i) {
                        Employee updatedEmployee = filteredEmployees.get(i);
                        int indexInAllEmployees = allEmployees.indexOf(updatedEmployee);
                        if (indexInAllEmployees != -1) {
                            allEmployees.set(indexInAllEmployees, updatedEmployee);
                        }
                    }
                }
            }
        });
    }

    public void updateEmployee(Employee employee) throws BBExceptions{
        // Update the employee in the database
        employeeBLL.updateEmployee(employee);

        // Update the employee in the allEmployees list
        int index = allEmployees.indexOf(employee);
        //we check the index to make sure the employee actually exists
        if (index != -1) {
            allEmployees.set(index, employee);
        }
    }

    public void addEmployeeToTeam(Employee employee, Team team) throws BBExceptions {
        // Add the team to the employee's list of teams
        employee.getTeams().add(team);

        // Update the employee in the database
        employeeBLL.addEmployeeToTeam(employee.getId(), team.getId());

        // Update the employee in the allEmployees list
        int index = allEmployees.indexOf(employee);
        if (index != -1) {
            allEmployees.set(index, employee);
        }
    }

    public void removeEmployeeFromTeam(int employeeId, int teamId) throws BBExceptions {
        //find the employee to be removed
        Employee employee = null;
        for(Employee e : allEmployees){
            if(e.getId() == employeeId){
                employee = e;
                break;
            }
        }
        if (employee == null) {
            throw new BBExceptions("Employee not found");
        }
        //loop through all teams of the employee to be removed
        Team teamToRemove = null;
        for(Team t : employee.getTeams()){
            if(t.getId() == teamId){
                //assign the team that matches the teamId to teamToRemove
                teamToRemove = t;
                break;
            }
        }
        if (teamToRemove != null) {
            //remove the employee from the team on our employee object
            employee.getTeams().remove(teamToRemove);
        } else {
            throw new BBExceptions("Team not found");
        }
        //remove employee from team on database
        employeeBLL.removeEmployeeFromTeam(employeeId, teamId);
        //index is the place where the employee is in the obsvlist
        int index = allEmployees.indexOf(employee);
        if (index != -1) {
            //update the employee at that index on our ObservableList so the changes will reflect on UI/FilteredList
            allEmployees.set(index, employee);
        }

    }


    public void addNewEmployee(Employee employee) throws BBExceptions{
            // Add employee to database and get the generated ID
            int newEmployeeId = employeeBLL.addNewEmployee(employee);
            // Set the ID of the employee
            employee.setId(newEmployeeId);
            // Add employees to the observable list
            allEmployees.add(employee);
            // This needs to be done this way to get the generated employee ID from the database so we are able
            // to edit new employees
            if(allCountries != null){
                boolean countryExists = false;
                // If the employee added has a country that has not been used before, add it to "allCountries"
                for(String country : allCountries){ // Checking through allCountries to see if one of them is the same as the new employee's country
                    if (country.equals(employee.getCountry())) {
                        countryExists = true;
                        break;
                    }
                }
                // If the country does not exist in allCountries, add it
                if (!countryExists) {
                    allCountries.add(employee.getCountry());
                }
            }
    }


    public ObservableList<Employee> searchEmployees(String keyword, String country) throws BBExceptions {
        ObservableList<Employee> allEmployees;

        if(country == null || country.isEmpty()){
            allEmployees = getEmployees();
        } else {
            allEmployees = filterEmployeesByCountry(country);
        }

        ObservableList<Employee> filteredEmployees = FXCollections.observableArrayList();

        for (Employee employee : allEmployees) {
            if (employee.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredEmployees.add(employee);
            }
        }

        return filteredEmployees;
    }

    /////////////////////////////////////////////////////////////
    ////////////////Filtering Countries//////////////////////////
    /////////////////////////////////////////////////////////////

    //doing the same thing for the country combobox
    public BooleanProperty countryAddedProperty(){
        return countryAdded;
    }

    public ObservableList<Employee> filterEmployeesByCountry(String country) {
        if(country.equals("All Countries")){
            try {
                return getEmployees();
            } catch (BBExceptions e) {
                throw new RuntimeException(e);
            }
        }

        ObservableList<Employee> filteredEmployees = FXCollections.observableArrayList();

        for(Employee employee: allEmployees){
            if(employee.getCountry().equals(country)){
                filteredEmployees.add(employee);
            }
        }
        return filteredEmployees;
    }

    //getting all the countries that have employees in them, for the search function
    public List<String> getAllCountriesUsed(){
        if(allCountries.isEmpty()){ //if the list is empty then populate it
            allCountries.add("All Countries");
            List<Employee> allEmployees = null;
            try {
                allEmployees = getEmployees(); //getting all the employees so we can get all the countries they're in
            } catch (BBExceptions e) {
                throw new RuntimeException(e);
            }
            for(Employee employee : allEmployees){ //adding list of all countries
                if(employee.getCountry() != null && !allCountries.contains(employee.getCountry())){
                    allCountries.add(employee.getCountry());
                }
            }
        }
        return allCountries;
    }

    /////////////////////////////////////////////////////////////
    /////////////////////////Rates///////////////////////////////
    /////////////////////////////////////////////////////////////

    public double calculateModifier(double markupValue){
        return employeeBLL.calculateModifier(markupValue);
    }

    public Double calculateHourlyRate(Employee selectedEmployee) throws BBExceptions {
        return employeeBLL.calculateHourlyRate(selectedEmployee);
    }

    public Double calculateTotalHourlyRateForCountry(String country) throws BBExceptions{
        return employeeBLL.calculateTotalHourlyRateForCountry(country);
    }

    public Double calculateDailyRate(Employee selectedEmployee, int hoursPerDay) throws BBExceptions{
        return employeeBLL.calculateDailyRate(selectedEmployee, hoursPerDay);
    }

    public Double calculateTotalDailyRateForCountry(String country, int hoursPerDay) throws BBExceptions{
        return employeeBLL.calculateTotalDailyRateForCountry(country, hoursPerDay);
    }

    public void updateTeamUtilForEmployee(int teamId, int employeeId, BigDecimal newUtil) throws BBExceptions {
        employeeBLL.updateTeamUtilForEmployee(teamId, employeeId, newUtil);
    }

    public BigDecimal getUtilizationForTeam(int employeeId, int teamId) throws BBExceptions {
        return employeeBLL.getUtilizationForTeam(employeeId, teamId);
    }

    public void invalidateTeamUtilSumCache(int employeeId) {
        teamUtilSumCache.remove(employeeId);
    }

    public BigDecimal getTeamUtilSumCache(int employeeId) {
        return teamUtilSumCache.get(employeeId);
    }

    public void setTeamUtilSumCache(int employeeId, BigDecimal value) {
        teamUtilSumCache.put(employeeId, value);
    }

    public void updateTeamIsOverheadForEmployee(int teamId, int employeeId, boolean isOverhead) throws BBExceptions {
        employeeBLL.updateTeamIsOverheadForEmployee(teamId, employeeId, isOverhead);
    }

    public BigDecimal getTeamUtilForEmployee(int employeeId, int teamId) throws BBExceptions {
        String key = employeeId + "-" + teamId;
        if (teamUtilCache.containsKey(key)) {
            return teamUtilCache.get(key);
        } else {
            BigDecimal teamUtil = employeeBLL.getUtilizationForTeam(employeeId, teamId);
            teamUtilCache.put(key, teamUtil);
            return teamUtil;
        }
    }

    public void invalidateCacheForEmployeeAndTeam(int employeeId, int teamId) {
        String key = employeeId + "-" + teamId;
        teamUtilCache.remove(key);
    }

//    public double calculateTeamHourlyRate(Employee employee, BigDecimal teamUtil) throws BBExceptions {
//        return employeeBLL.calculateTeamHourlyRate(employee, teamUtil);
//    }
//
//    public double calculateTeamDailyRate(Employee employee, BigDecimal teamUtil, int hoursPerDay) throws BBExceptions {
//        return employeeBLL.calculateTeamDailyRate(employee, teamUtil, hoursPerDay);
//    }
}
