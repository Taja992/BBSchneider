package GUI.model;

import BE.Employee;
import BLL.EmployeeBLL;
import Exceptions.BBExceptions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeModel {
    private final EmployeeBLL employeeBLL;
    private final ObservableList<Employee> employees;

    //Added 2 Hashmaps in order to track if an employees Team Id has changed and reflect changes on the tableviews
    private final Map<Employee, Integer> previousTeamIds = new HashMap<>();
    private final Map<Integer, ObservableList<Employee>> teamEmployees = new HashMap<>();


    public EmployeeModel(){
        employeeBLL = new EmployeeBLL();
        employees = FXCollections.observableArrayList();
    }

    public ObservableList<Employee> getEmployees() throws BBExceptions {
        if(employees.isEmpty()) {
            //populate our list from database
            List<Employee> allEmployees = employeeBLL.getAllEmployees();
            employees.addAll(allEmployees);

            for (Employee employee : allEmployees) {
                previousTeamIds.put(employee, employee.getTeamIdEmployee());
            }
        }
        //return our observable list
        return employees;
    }


    public ObservableList<Employee> searchEmployees(String keyword) throws BBExceptions {
        ObservableList<Employee> allEmployees = getEmployees();
        ObservableList<Employee> filteredEmployees = FXCollections.observableArrayList();

        for (Employee employee : allEmployees) {
            if (employee.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredEmployees.add(employee);
            }
        }

        return filteredEmployees;
    }

    public void addNewEmployee(Employee employee) throws BBExceptions {
        //add employee to database
        employeeBLL.addNewEmployee(employee);
        //add employees to the observable list
        employees.add(employee);

    }

//    public Employee addNewEmployee(Employee employee) throws BBExceptions {
//        // Add the employee to the database and get the updated employee
//        Employee updatedEmployee = employeeBLL.addNewEmployee(employee);
//
//        // Add the updated employee to the observable list
//        employees.add(updatedEmployee);
//
//        // Add the updated employee to the team's list in teamEmployees
//        Integer teamId = updatedEmployee.getTeamIdEmployee();
//        if (teamId != null && teamId != -1) {
//            ObservableList<Employee> teamList = teamEmployees.get(teamId);
//            if (teamList == null) {
//                // If the team doesn't exist in the map yet, create a new list
//                teamList = FXCollections.observableArrayList();
//                teamEmployees.put(teamId, teamList);
//            }
//            teamList.add(updatedEmployee);
//        }
//        previousTeamIds.put(updatedEmployee, teamId);
//
//        // Return the updated employee
//        return updatedEmployee;
//    }

    //Added a method to repopulate the observable list from the database if needed
    public void refreshingEmployees() throws BBExceptions {
        List<Employee> allEmployees = employeeBLL.getAllEmployees();
        employees.setAll(allEmployees);
    }

//    public void refreshingEmployees() throws BBExceptions {
//        employees.clear();
//        employees.addAll(employeeBLL.getAllEmployees());
//    }

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

    public void updateEmployee(Employee employee) throws BBExceptions{
        System.out.println("Employee details:");
        System.out.println("ID: " + employee.getId());


        Integer previousTeamId = previousTeamIds.get(employee);
        Integer currentTeamId = employee.getTeamIdEmployee();

        //I make these -1 because the hashmap cannot handle null
        if(previousTeamId == null){
            previousTeamId = -1;
        }

        if(currentTeamId == null){
            currentTeamId = -1;
        }

        employeeBLL.updateEmployee(employee);
        //if the previous Id(hashmap) does not match the current Id, we call the refresh method
        if (previousTeamId != null && !previousTeamId.equals(currentTeamId)) {
            //we call this method twice so both lists get updated
            refreshEmployeesInTeam(previousTeamId);
            refreshEmployeesInTeam(currentTeamId);
        }
        //then we update our hashmap to set the employee Key to the the Id
        previousTeamIds.put(employee, currentTeamId);
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

}
