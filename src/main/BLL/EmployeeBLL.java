package BLL;

import BE.Employee;
import BE.Team;
import DAL.EmployeeDAO;
import Exceptions.BBExceptions;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class EmployeeBLL {
    private final EmployeeDAO employeeDAO;
    private static int workingHours = 8;

    public EmployeeBLL() {
        employeeDAO = new EmployeeDAO();
    }

    public int addNewEmployee(Employee employee) throws BBExceptions{
        return employeeDAO.newEmployee(employee);
    }

    public List<Employee> getAllEmployees() throws BBExceptions {
        return employeeDAO.getAllEmployees();
    }

    public void addEmployeeToTeam(int employeeId, int teamId) throws BBExceptions {
        employeeDAO.addEmployeeToTeam(employeeId, teamId);
    }
    public void removeEmployeeFromTeam(int employeeId, int teamId) throws BBExceptions {
        employeeDAO.removeEmployeeFromTeam(employeeId, teamId);
    }
    public List<Employee> getAllEmployeesFromTeam(int TeamId) throws BBExceptions {
            return employeeDAO.getAllEmployeesFromTeam(TeamId);
    }

    public BigDecimal calculateTotalTeamUtil(int employeeId) throws BBExceptions {
        return employeeDAO.calculateTotalTeamUtilization(employeeId);
    }

    public void updateEmployee(Employee employee) throws BBExceptions{
        employeeDAO.updateEmployee(employee);
    }

    public BigDecimal getUtilizationForTeam(int employeeId, int teamId) throws BBExceptions {
        return employeeDAO.getUtilizationForTeam(employeeId, teamId);
    }

    public void updateTeamUtilForEmployee(int teamId, int employeeId, BigDecimal newUtil) throws BBExceptions {
        employeeDAO.updateTeamUtilForEmployee(teamId, employeeId, newUtil);
    }

    public void updateTeamIsOverheadForEmployee(int teamId, int employeeId, boolean isOverhead) throws BBExceptions {
        employeeDAO.updateTeamIsOverheadForEmployee(teamId, employeeId, isOverhead);
    }

    ////////////////////////////////////////////////////////
    /////////////////Calculation Logic//////////////////////
    ////////////////////////////////////////////////////////

    private double calculateRate(Employee selectedEmployee) throws BBExceptions {
        if (selectedEmployee != null) {
            double annualSalary = selectedEmployee.getAnnualSalary().doubleValue();
            double overheadMultiplier = selectedEmployee.getOverheadMultiPercent().doubleValue() / 100; // convert to decimal
            double fixedAnnualAmount = selectedEmployee.getAnnualAmount().doubleValue();
            double utilizationPercentage = selectedEmployee.getUtilization().doubleValue() / 100; // convert to decimal
            double annualEffectiveWorkingHours = selectedEmployee.getWorkingHours(); // convert to total working hours in a year
            return (((annualSalary + fixedAnnualAmount) * (1 + overheadMultiplier)) / (annualEffectiveWorkingHours * utilizationPercentage));
        } else {
            throw new BBExceptions("No employee selected");
        }
    }


    public Double calculateHourlyRate(Employee selectedEmployee) throws BBExceptions {
        double rate = calculateRate(selectedEmployee);  // The rate calculated is already in hourly rate

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(rate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing hourly rate", e);
        }
    }

    public Double calculateDailyRate(Employee selectedEmployee, int hoursPerDay) throws BBExceptions {
        if (hoursPerDay < 0 || hoursPerDay > 24) {
            throw new BBExceptions("Invalid number of hours per day. It should be between 0 and 24.");
        }

        double hourlyRate = calculateHourlyRate(selectedEmployee);
        double dailyRate = hourlyRate * hoursPerDay;

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(dailyRate)).doubleValue();
        } catch (ParseException e) {
            throw new BBExceptions("Error parsing daily rate", e);
        }
    }

    public Double calculateTotalHourlyRateForCountry(String country) throws BBExceptions{

        List<Employee> allEmployees = null;
        try {
            allEmployees = getAllEmployees();
        } catch (BBExceptions e) {
            throw new BBExceptions("Alert", e);
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

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(totalRate)).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Double calculateTotalDailyRateForCountry(String country, int hoursPerDay) throws BBExceptions{
        List<Employee> allEmployees;
        try {
            allEmployees = getAllEmployees();
        } catch (BBExceptions e) {
            throw new BBExceptions("Error getting all employees", e);
        }

        Double totalRate = 0.0;
        for(Employee employee: allEmployees){
            if(employee.getCountry().equals(country)){
                totalRate += calculateDailyRate(employee, hoursPerDay);
            }else if (country == "All Countries"){
                totalRate += calculateHourlyRate(employee);
            }
        }
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        try {
            return nf.parse(nf.format(totalRate)).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public double calculateMarkUp(double markupValue){
        return 1 + (markupValue / 100);
    }

    public double calculateGrossMargin(double grossMarginValue) {
        return 1 + (grossMarginValue / 100);
    }

    public static int setWorkingHours(int newWorkingHours) {
        return workingHours = newWorkingHours;
    }

    public static int getWorkingHours() {
        return workingHours;
    }
}
