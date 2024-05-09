package GUI.model;

import BE.Employee;
import BLL.EmployeeBLL;
import Exceptions.BBExceptions;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeModel {
    private final EmployeeBLL employeeBLL;
    private final ObservableList<Employee> employees;

    private final Map<Integer, ObservableList<Employee>> teamEmployees = new HashMap<>();
    private final BooleanProperty employeeAdded = new SimpleBooleanProperty(false);


    public EmployeeModel(){
        employeeBLL = new EmployeeBLL();
        employees = FXCollections.observableArrayList();
    }

    //Boolean property added as a switch to tell the employeeTableview to update
    public BooleanProperty employeeAddedProperty() {
        return employeeAdded;
    }

    public ObservableList<Employee> getEmployees() throws BBExceptions {
        if(employees.isEmpty()) {
            //populate our list from database
            List<Employee> allEmployees = employeeBLL.getAllEmployees();
            employees.addAll(allEmployees);

        }
        //return our observable list
        return employees;
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

    public void addNewEmployee(Employee employee) throws BBExceptions {
        //add employee to database and get the generated ID
        int newEmployeeId = employeeBLL.addNewEmployee(employee);
        //set the ID of the employee
        employee.setId(newEmployeeId);
        //add employees to the observable list
        employees.add(employee);
        //Tell our boolean property that a new employee was added and to refresh Tableview
        employeeAdded.set(true);
        //this needs to be done this way to get the generated employee ID from the database so we are able
        //edit new employees
    }


    //Added a method to repopulate the observable list from the database if needed
    public void refreshingEmployees() throws BBExceptions {
        List<Employee> allEmployees = employeeBLL.getAllEmployees();
        employees.setAll(allEmployees);
    }

    public Double calculateHourlyRate(Employee selectedEmployee) {
        return employeeBLL.calculateHourlyRate(selectedEmployee);
    }

    public Double calculateDailyRate(Employee selectedEmployee) {
        return employeeBLL.calculateDailyRate(selectedEmployee);
    }
    public ObservableList<Employee> getAllEmployeesFromTeam(int TeamId) {
        //checks to see if the teamId has already been assigned to Hashmap
        if (!teamEmployees.containsKey(TeamId)) {
            //Set up Observable list for tables
            ObservableList<Employee> empFromTeam = FXCollections.observableArrayList();
            empFromTeam.addAll(employeeBLL.getAllEmployeesFromTeam(TeamId));
            //Put the TeamId as the setKey for the list of employees
            teamEmployees.put(TeamId, empFromTeam);
        }

        // Return the list from the map with .get which gives us the key (TeamId)
        return teamEmployees.get(TeamId);
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




    public void updateEmployee(Employee employee) throws BBExceptions{
        employeeBLL.updateEmployee(employee);
    }

    private void refreshEmployeesInTeam(int teamId) {
        // Get the list of employees for the team
        ObservableList<Employee> employeesInTeam = teamEmployees.get(teamId);

        // If the list is null, create a new list and put it in the map
        if (employeesInTeam == null) {
            employeesInTeam = FXCollections.observableArrayList();
            teamEmployees.put(teamId, employeesInTeam);
        }

        // Clear the list and repopulate it from the database
        employeesInTeam.clear();
        employeesInTeam.addAll(employeeBLL.getAllEmployeesFromTeam(teamId));
    }

    public double calculateMarkUp(double markupValue){
        return employeeBLL.calculateMarkUp(markupValue);
    }

}
