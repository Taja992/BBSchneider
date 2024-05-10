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

    private final BooleanProperty employeeAdded = new SimpleBooleanProperty(false);
    private final BooleanProperty countryAdded = new SimpleBooleanProperty(false);

    private List<String> allCountries = FXCollections.observableArrayList();


    public EmployeeModel(){
        employeeBLL = new EmployeeBLL();
        employees = FXCollections.observableArrayList();
    }

    //Boolean property added as a switch to tell the employeeTableview to update
    public BooleanProperty employeeAddedProperty() {
        return employeeAdded;
    }
    //doing the same thing for the country combobox
    public BooleanProperty countryAddedProperty(){
        return countryAdded;
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

        if(allCountries != null){
            boolean countryExists = false;
            //if the employee added has a country that has not been used before, add it to "allCountries"
            for(String country : allCountries){ //checking through allCountries to see if one of them is the same as the new employees country
                if (country.equals(employee.getCountry())) {
                    countryExists = true;
                }
            }

            if(!countryExists){
                allCountries.add(employee.getCountry());
                countryAdded.set(true);
            }
        }


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

    public Double calculateTotalHourlyRateForCountry(String country){
        return employeeBLL.calculateTotalHourlyRateForCountry(country);
    }

    public Double calculateTotalDailyRateForCountry(String country){
        return employeeBLL.calculateTotalDailyRateForCountry(country);
    }

    public ObservableList<Employee> getAllEmployeesFromTeam(int TeamId) {
        // Set up Observable list for tables
        ObservableList<Employee> empFromTeam = FXCollections.observableArrayList();
        empFromTeam.addAll(employeeBLL.getAllEmployeesFromTeam(TeamId));

        // Return the list of employees
        return empFromTeam;
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



    public void updateEmployee(Employee employee) throws BBExceptions{
        employeeBLL.updateEmployee(employee);
    }

    //refresh employees in team if we need this
    public ObservableList<Employee> refreshEmployeesInTeam(int teamId) {

        // Return the list of employees
        return FXCollections.observableArrayList(employeeBLL.getAllEmployeesFromTeam(teamId));
    }

    public double calculateMarkUp(double markupValue){
        return employeeBLL.calculateMarkUp(markupValue);
    }

}
