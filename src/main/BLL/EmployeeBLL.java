package BLL;

import BE.Employee;
import BE.Team;
import DAL.EmployeeDAO;
import Exceptions.BBExceptions;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class EmployeeBLL {
    private final EmployeeDAO employeeDAO;

    public EmployeeBLL() {
        employeeDAO = new EmployeeDAO();
    }


    public int addNewEmployee(Employee employee) throws BBExceptions{
        return employeeDAO.newEmployee(employee);
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
        double hourlyRate = rate / 8; // Assuming 8 working hours in a day, have to ask in sprint review

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(hourlyRate)).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Double calculateDailyRate(Employee selectedEmployee) {
        double dailyRate = calculateRate(selectedEmployee); // The rate calculated is already a daily rate

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(dailyRate)).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Double calculateTotalHourlyRateForCountry(String country){

        List<Employee> allEmployees = null;
        try {
            allEmployees = getAllEmployees();
        } catch (BBExceptions e) {
            throw new RuntimeException(e);
        }

        Double totalRate = 0.0;
        for(Employee employee: allEmployees){
            if(employee.getCountry().equals(country)){
                totalRate += calculateHourlyRate(employee);
            } else if (country == "All Countries"){ //if it's "All Countries" then add everyone to total
                //this might make the program slow in the future so feel free to delete it
                totalRate += calculateHourlyRate(employee);
            }
        }
        return totalRate;
    }

    public Double calculateTotalDailyRateForCountry(String country){

        List<Employee> allEmployees = null;
        try {
            allEmployees = getAllEmployees();
        } catch (BBExceptions e) {
            throw new RuntimeException(e);
        }

        Double totalRate = 0.0;
        for(Employee employee: allEmployees){
            if(employee.getCountry().equals(country)){
                totalRate += calculateDailyRate(employee);
            }else if (country == "All Countries"){
                totalRate += calculateHourlyRate(employee);
            }
        }
        return totalRate;
    }

    public double calculateMarkUp(double markupValue){
        return 1 + (markupValue / 100);
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

    public BigDecimal getUtilizationForTeam(Employee employee, Team team) throws BBExceptions {
        return employeeDAO.getUtilizationForTeam(employee, team);
    }
}
