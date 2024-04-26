package GUI.model;

import BE.Employee;
import BLL.EmployeeBLL;
import Exceptions.BBExceptions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmployeeModel {
    private final EmployeeBLL employeeBLL;
    private final ObservableList<Employee> employees;

    public EmployeeModel(){
        employeeBLL = new EmployeeBLL();
        employees = FXCollections.observableArrayList();
    }

    public ObservableList<Employee> getEmployees() throws BBExceptions {
        if(employees.isEmpty()) {
            //populate our list from database
            employees.addAll(employeeBLL.getAllEmployees());
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

    //Added a method to repopulate the observable list from the database if needed
    public void refreshingEmployees() throws BBExceptions {
        employees.clear();
        employees.addAll(employeeBLL.getAllEmployees());
    }

    public Double calculateHourlyRate(Employee selectedEmployee) {
        return employeeBLL.calculateHourlyRate(selectedEmployee);
    }

    public Double calculateDailyRate(Employee selectedEmployee) {
        return employeeBLL.calculateDailyRate(selectedEmployee);
    }
}
