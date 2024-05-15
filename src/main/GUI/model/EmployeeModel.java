package GUI.model;

import BE.Employee;
import BE.Team;
import BLL.EmployeeBLL;
import Exceptions.BBExceptions;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Tab;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;


public class EmployeeModel {
    private final EmployeeBLL employeeBLL;

    private final TeamModel teamModel = new TeamModel();
    private final ObservableList<Employee> employees;
    private final BooleanProperty countryAdded = new SimpleBooleanProperty(false);
    private final List<String> allCountries = FXCollections.observableArrayList();
    private final ObservableList<Employee> allEmployees;


    public EmployeeModel(){
        employeeBLL = new EmployeeBLL();
        employees = FXCollections.observableArrayList();
        allEmployees = FXCollections.observableArrayList();
    }

    public void removeEmployee(Employee employee, int teamId) throws BBExceptions {
        // Find the team to be removed
        Team teamToRemove = null;
        for (Team team : employee.getTeams()) {
            if (team.getId() == teamId) {
                teamToRemove = team;
                break;
            }
        }

        // Remove the team from the employee's team list
        if (teamToRemove != null) {
            employee.getTeams().remove(teamToRemove);
        }

        // Update the database
        employeeBLL.removeEmployeeFromTeam(employee.employeeId, teamId);
    }

//    public void removeEmployeeFromTeam(int employeeId, int teamId) throws BBExceptions {
//        employeeBLL.removeEmployeeFromTeam(employeeId, teamId);
//    }


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


    public void addNewEmployee(Employee employee) {
        try {
            // Add employee to database and get the generated ID
            int newEmployeeId = employeeBLL.addNewEmployee(employee);
            // Set the ID of the employee
            employee.setId(newEmployeeId);
            // Add employees to the observable list
            employees.add(employee);
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
        } catch (BBExceptions e) {
            e.printStackTrace();
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

        for(Employee employee: employees){
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

    public double calculateMarkUp(double markupValue){
        return employeeBLL.calculateMarkUp(markupValue);
    }

    public Double calculateHourlyRate(Employee selectedEmployee) {
        return employeeBLL.calculateHourlyRate(selectedEmployee);
    }

    public Double calculateTotalHourlyRateForCountry(String country){
        return employeeBLL.calculateTotalHourlyRateForCountry(country);
    }

    public Double calculateDailyRate(Employee selectedEmployee) {
        return employeeBLL.calculateDailyRate(selectedEmployee);
    }

    public Double calculateTotalDailyRateForCountry(String country) {
        return employeeBLL.calculateTotalDailyRateForCountry(country);
    }

    public BigDecimal calculateTotalTeamUtil(int employeeId) throws BBExceptions {
        return employeeBLL.calculateTotalTeamUtil(employeeId);
    }

    public void updateTeamUtilForEmployee(int teamId, int employeeId, BigDecimal newUtil) throws BBExceptions {
        employeeBLL.updateTeamUtilForEmployee(teamId, employeeId, newUtil);
    }

    public BigDecimal getUtilizationForTeam(Employee employee, Team team) throws BBExceptions {
        return employeeBLL.getUtilizationForTeam(employee, team);
    }


}
