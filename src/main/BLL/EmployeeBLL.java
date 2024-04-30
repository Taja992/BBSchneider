package BLL;

import BE.Employee;
import DAL.EmployeeDAO;
import Exceptions.BBExceptions;

import java.sql.SQLException;
import java.util.List;

public class EmployeeBLL {
    private final EmployeeDAO employeeDAO;

    public EmployeeBLL() {
        employeeDAO = new EmployeeDAO();
    }


    public void addNewEmployee(Employee employee) throws BBExceptions{
        employeeDAO.newEmployee(employee);
    }

    public List<Employee> getAllEmployees() throws BBExceptions {
        return employeeDAO.getAllEmployees();
    }


    private double calculateRate(Employee selectedEmployee) {
        double annualSalary = selectedEmployee.getAnnualSalary().doubleValue();
        double overheadMultiplier = selectedEmployee.getOverheadMultiPercent().doubleValue() / 100; // convert to decimal
        double fixedAnnualAmount = selectedEmployee.getAnnualAmount().doubleValue();
        double utilizationPercentage = selectedEmployee.getUtilization().doubleValue() / 100; // convert to decimal
        double annualEffectiveWorkingHours = selectedEmployee.getWorkingHours();
        return ((annualSalary + fixedAnnualAmount) * (1 + overheadMultiplier)) / (annualEffectiveWorkingHours * utilizationPercentage);
    }

    public Double calculateHourlyRate(Employee selectedEmployee) {
        double rate = calculateRate(selectedEmployee);
        double hourlyRate = rate / 8; // Assuming 8 working hours in a day, have to ask in sprintreview
        return Double.valueOf(String.format("%.2f", hourlyRate));
    }

    public Double calculateDailyRate(Employee selectedEmployee) {
        double dailyRate = calculateRate(selectedEmployee); // The rate calculated is already a daily rate
        return Double.valueOf(String.format("%.2f", dailyRate));
    }

    public List<Employee> getAllEmployeesFromTeam(int TeamId) {
        try {
            return employeeDAO.getAllEmployeesFromTeam(TeamId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateEmployee(Employee employee) throws BBExceptions{
        employeeDAO.updateEmployee(employee);
    }

}
