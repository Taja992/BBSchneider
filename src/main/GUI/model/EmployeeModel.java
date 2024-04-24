package GUI.model;

import BE.Employee;
import BLL.EmployeeBLL;
import Exceptions.BBExceptions;

public class EmployeeModel {
    private final EmployeeBLL employeeBLL;

    public EmployeeModel(){
        employeeBLL = new EmployeeBLL();
    }

    public void addNewEmployee(Employee employee) throws BBExceptions {
        employeeBLL.addNewEmployee(employee);
    }
}
