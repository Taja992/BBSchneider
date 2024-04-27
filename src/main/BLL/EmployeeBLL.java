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

    public Double calculateHourlyRate(Employee selectedEmployee) {
//        double annualSalary = selectedEmployee.getAnnualSalary().doubleValue();
//        double workingHours = selectedEmployee.getWorkingHours();
//        double hourlyRate = annualSalary / workingHours;
//        // To find the hourly rate before the overhead multiplier was applied, divide the base hourly rate by 1 plus the multiplier
//        double hourlyRateBeforeOverhead = hourlyRate / (1 + selectedEmployee.getOverheadMultiPercent().doubleValue());
//        return Double.valueOf(String.format("%.2f", hourlyRateBeforeOverhead));
        double annualSalary = selectedEmployee.getAnnualSalary().doubleValue();
        double overheadMultiplier = selectedEmployee.getOverheadMultiPercent().doubleValue();
        double fixedAnnualAmount = selectedEmployee.getAnnualAmount().doubleValue();
        double workingHours = selectedEmployee.getWorkingHours();
        double hourlyRate = ((annualSalary + fixedAnnualAmount) * (1 + overheadMultiplier)) / workingHours;
        return Double.valueOf(String.format("%.2f", hourlyRate));
    }

    public Double calculateDailyRate(Employee selectedEmployee) {
//        double annualSalary = selectedEmployee.getAnnualSalary().doubleValue();
//        double workingHours = selectedEmployee.getWorkingHours();
//        double hourlyRate = annualSalary / workingHours;
//        double hourlyRateBeforeOverhead = hourlyRate / (1 + selectedEmployee.getOverheadMultiPercent().doubleValue());
//        // Multiplied by 8, assuming a day of work is 8 hours
//        double dailyRateBeforeOverhead = hourlyRateBeforeOverhead * 8;
//        return Double.valueOf(String.format("%.2f", dailyRateBeforeOverhead));
        double annualSalary = selectedEmployee.getAnnualSalary().doubleValue();
        double overheadMultiplier = selectedEmployee.getOverheadMultiPercent().doubleValue();
        double fixedAnnualAmount = selectedEmployee.getAnnualAmount().doubleValue();
        double workingHours = selectedEmployee.getWorkingHours();
        double hourlyRate = ((annualSalary + fixedAnnualAmount) * (1 + overheadMultiplier)) / workingHours;
        double dailyRate = hourlyRate * 8;
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
